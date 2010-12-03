package org.erights.e.elib.vat;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.debug.CausalityLogRecord;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.prim.E;

import java.io.IOException;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents an event to happen within a Runner's thread.
 * <p/>
 * Like a Runnable, but does bookkeeping useful for debugging.
 *
 * @author Mark S. Miller
 */
public abstract class PendingEvent implements Runnable, EPrintable {

    /**
     * The Vat onto which this PendingEvent was queued.
     */
    private final Vat myVat;

    /**
     * The takeTicket taken from myVat by queueing this PendingEvent.
     */
    private final long myTicket;

    /**
     *
     */
    private final SendingContext mySendingContext;

    /**
     * @param vat The Vat onto which this PendingEvent will be queued. Note
     *            that this may be different than {@link Vat#getCurrentVat()}.
     */
    PendingEvent(String tag, Vat vat) {
        myVat = vat;
        myTicket = vat.takeTicket();
        mySendingContext = new SendingContext(tag);
    }

    /**
     * @param context The context to be recorded as the one causing this event
     *                to be queued.
     * @param vat     The Vat onto which this PendingEvent will be queued. Note
     *                that this may be different than {@link Vat#getCurrentVat()}.
     */
    PendingEvent(String tag, Vat vat, SendingContext context) {
        myVat = vat;
        myTicket = vat.takeTicket();
        mySendingContext = new SendingContext(tag, context);
    }

    /**
     * Do now the event that this PendingEvent represents.
     * <p/>
     * Does bookkeeping of debugging info around call to {@link #innerRun()},
     * including catching <i>all</i> thrown Exceptions that escape from
     * innerRun() {@link #report reporting} them rather than propogating them.
     */
    public final void run() {
        Runner serv = myVat.getRunner();
        serv.requireCurrent();
        Vat optOldVat = serv.myOptServingVat;
        long oldTicket = serv.myServingTicket;
        serv.myOptServingVat = myVat;
        serv.myServingTicket = myTicket;

        if (Trace.causality.debug && Trace.ON) {
            Trace.causalityLogger.log(new RunEvent(new SendingContext("RunEvent")));
        }

        try {
            innerRun();
        } catch (Throwable problem) {
            report("Problem in turn", problem);
        } finally {
            serv.myOptServingVat = optOldVat;
            serv.myServingTicket = oldTicket;
        }
    }

    /**
     * This should always be called once construction is complete.
     */
    protected void trace() {
        if (Trace.causality.debug && Trace.ON) {
            //Trace.causality.debugm("", this);
            if (mySendingContext.getSendingTicket() != -1) {
                Trace.causalityLogger.log(new NewPendingEvent());
            }
        }
    }

    /**
     *
     */
    public void report(String msg) {
        if (Trace.causality.warning && Trace.ON) {
            Trace.causality.warningm(msg);
        }
    }

    /**
     * Trace at warning level that this event caused this problem.
     */
    public void report(String msg, Throwable problem) {
        if (Trace.causality.warning && Trace.ON) {
            msg += " <" + myVat + "," + myTicket + ">: ";
            Trace.causality.warningm(msg, problem);
        }
    }

    /**
     * Override this to do the real work of {@link #run()}.
     */
    protected abstract void innerRun();

    /**
     *
     */
    public void printContextOn(TextWriter out) throws IOException {
        out.println();
        out.print("event: ", myVat, ":" + myTicket);
        mySendingContext.printContextOn(out);
    }

    /**
     * @param out
     * @throws IOException
     */
    public void __printOn(TextWriter out) throws IOException {
        out.println();
        printContextOn(out.indent("--- "));
        out.println();
    }

    /**
     * @return
     */
    public String toString() {
        return E.toString(this);
    }

    abstract public String getAbbrevCall();

    private class NewPendingEvent extends CausalityLogRecord {
        NewPendingEvent() {
            // There's always a receiving vat, so use that for the message ID
            super(mySendingContext, myVat.getOptName() + "_" + myTicket);
        }

        public String getEventClass() {
            return "org.ref_send.log.Sent";
        }
    }

    private class RunEvent extends CausalityLogRecord {
        RunEvent(SendingContext recvContext) {
            // Message ID must be same as for NewPendingEvent
            super(recvContext, recvContext.getVatID() + "_" + myTicket);
        }

        public String getEventClass() {
            return "org.ref_send.log.Got";
        }

        protected String getStackTrace() {
            return "{\"name\": " + StringHelper.quote(getAbbrevCall()) + ", \"source\": \"-\"}\n";
        }
    }
}
