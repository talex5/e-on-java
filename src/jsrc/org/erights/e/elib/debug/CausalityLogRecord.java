package org.erights.e.elib.debug;

import org.erights.e.elib.vat.StackContext;
import org.erights.e.elib.vat.SendingContext;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.debug.EStackItem;
import org.erights.e.elib.tables.ConstList;

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

    public CausalityLogRecord(SendingContext context, String messageID) {
        super(Level.WARNING, "CausalityLogRecord");
        mySendingContext = context;
        numberWithinTurn = nextNumberWithinTurn;
        myMessageID = messageID;
        synchronized (this) {
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
            // also, we need to reverse the order of the frames
            for (int i = nFrames - 1; i >= 0; i--) {
                EStackItem rawFrame = (EStackItem) rawFrames.get(i);
                String name = rawFrame.toString();
                SourceSpan sourceSpan = rawFrame.getOptSpan();
                String source;
                int line;
                if (sourceSpan == null) {
                    continue;
                }

                source = sourceSpan.getUrl();
                line = sourceSpan.getStartLine();

                if (!"".equals(calls)) {
                    calls += ", ";
                }
                calls += "{\"name\": " + StringHelper.quote(name) + ", \"source\": " + StringHelper.quote(source) + ", \"span\": [[" + line + "]]}";
            }
        } else {
            // Java stack
            StackTraceElement[] jFrames = stack.getOptJStack().getStackTrace();

            for (int i = 4; i < jFrames.length; i++) {
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
        return calls;
    }
    
    public String asJSON() {
        String loopName = mySendingContext.getVatID();
        if (loopName == null) {
            loopName = "unknown vat";
        }
        long turnNumber = mySendingContext.getSendingTicket();

        return "{\n" +
            "\"class\" : [" + StringHelper.quote(getEventClass()) + ", \"org.ref_send.log.Event\" ],\n" +
            "\"anchor\" : {\n" +
            "  \"number\" : " + numberWithinTurn + ",\n" +
            "  \"turn\" : {\n" +
            "    \"loop\" : " + StringHelper.quote(loopName) + ",\n" +
            "    \"number\" : " + turnNumber + ",\n" +
            "  }\n" +
            "},\n" +
            "\"message\" : " + StringHelper.quote(myMessageID) + ",\n" +
            "\"trace\" : {\"calls\" : [" + getStackTrace() + "] }\n" +
          "}";
    }
    
    abstract public String getEventClass();
}
