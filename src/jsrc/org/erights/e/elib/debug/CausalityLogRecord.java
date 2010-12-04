package org.erights.e.elib.debug;

import org.erights.e.elib.vat.StackContext;
import org.erights.e.elib.vat.SendingContext;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.debug.EStackItem;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.oldeio.TextWriter;
import java.io.StringWriter;

/*
Copyright University of Southampton IT Innovation Centre, 2010,
under the terms of the MIT X license, available from
http://www.opensource.org/licenses/mit-license.html
*/

import java.util.logging.LogRecord;
import java.util.logging.Level;

public abstract class CausalityLogRecord extends LogRecord {
    private static long nextNumberWithinTurn = 0;

    private SendingContext mySendingContext;
    private long numberWithinTurn;
    private String myMessageID;
    private String myConditionID;

    public CausalityLogRecord(SendingContext context, String messageID) {
        this(context, messageID, null);
    }

    public CausalityLogRecord(SendingContext context, String messageID, String conditionID) {
        super(Level.WARNING, "CausalityLogRecord");
        mySendingContext = context;
        myMessageID = messageID;
        myConditionID = conditionID;
        synchronized (this) {
            numberWithinTurn = nextNumberWithinTurn;
            nextNumberWithinTurn += 1;
        }
    }

    protected String getStackTrace() {
        String calls = "";

        StackContext stack = mySendingContext.getStackContext();

        ConstList rawFrames = stack.getOptEStack();

        if (rawFrames != null && rawFrames.size() != 0) {
            // E stack
            int nFrames = rawFrames.size();

            // 1st frame is Ref.send, so skip that
            int firstFrame = (nFrames > 1) ? nFrames - 2 : nFrames - 1;
            // also, we need to reverse the order of the frames
            for (int i = firstFrame; i >= 0; i--) {
                EStackItem rawFrame = (EStackItem) rawFrames.get(i);
                String name;

                try {
                    StringWriter sw = new StringWriter();
                    TextWriter tw = new TextWriter(sw);
                    rawFrame.traceOn(tw);
                    name = sw.getBuffer().toString();
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }

                SourceSpan sourceSpan = rawFrame.getOptSpan();
                String source;
                int line;
                if (sourceSpan == null) {
                    source = "-";
                    line = 0;
                } else {
                    source = sourceSpan.getUrl();
                    line = sourceSpan.getStartLine();
                }

                if (!"".equals(calls)) {
                    calls += ", ";
                }
                calls += "{\"name\": " + StringHelper.quote(name) + ", \"source\": " + StringHelper.quote(source) + ", \"span\": [[" + line + "]]}";
            }
        } else {
            // Java stack
            StackTraceElement[] jFrames = stack.getOptJStack().getStackTrace();

            for (int i = 3; i < jFrames.length; i++) {
                StackTraceElement rawFrame = jFrames[i];
                String name = rawFrame.getMethodName();
                String source = rawFrame.getClassName().replaceAll("\\.", "/") + ".java";
                int line = rawFrame.getLineNumber();
                if (!"".equals(calls)) {
                    calls += ", ";
                }
                calls += "{\"name\": " + StringHelper.quote(name) + ", \"source\": " + StringHelper.quote(source) + ", \"span\": [[" + line + "]]}";
            }
        }
        if (calls.equals("")) {
            throw new RuntimeException("No stack: " + mySendingContext);
        }
        return calls;
    }
    
    public String asJSON() {
        String loopName = mySendingContext.getVatID();
        if (loopName == null) {
            loopName = "unknown vat";
        }
        long turnNumber = mySendingContext.getSendingTicket();
        String text = getText();

        String classesStr = "";
        String[] classes = getEventClass();
        for (int i = 0; i < classes.length; i++) {
            classesStr += StringHelper.quote(classes[i]) + ", ";
        }

        return "{\n" +
            "\"class\" : [" + classesStr + "\"org.ref_send.log.Event\"],\n" +
            "\"anchor\" : {\n" +
            "  \"number\" : " + numberWithinTurn + ",\n" +
            "  \"turn\" : {\n" +
            "    \"loop\" : " + StringHelper.quote(loopName) + ",\n" +
            "    \"number\" : " + turnNumber + ",\n" +
            "  }\n" +
            "},\n" +
            // (the order is important!)
            ((myConditionID != null) ? ("\"condition\" : " + StringHelper.quote(myConditionID) + ",\n") : "") +
            ((myMessageID != null) ? ("\"message\" : " + StringHelper.quote(myMessageID) + ",\n") : "") +
            ((text != null) ? ("\"text\" : " + StringHelper.quote(text) + ",\n") : "") +
            "\"trace\" : {\"calls\" : [" + getStackTrace() + "] }\n" +
          "}";
    }
    
    abstract public String[] getEventClass();

    /** A label for the event. */
    abstract protected String getText();
}
