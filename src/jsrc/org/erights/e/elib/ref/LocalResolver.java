package org.erights.e.elib.ref;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.vat.SendingContext;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.debug.CausalityLogRecord;

/**
 * The arrowhead facet of a local promise for resolving the outcome of the
 * promise.
 *
 * @author Mark S. Miller
 */
class LocalResolver implements Resolver {
    // causality tracing
    private static long nextConditionID = 0;
    private long myConditionID = -1;

    /**
     * Once it's done, it stops pointing at the Ref.
     */
    private Ref myOptRef;

    /**
     * Until the promise is done, this holds the buffer of all messages sent to
     * far.
     */
    private FlexList myOptBuf;

    /**
     *
     */
    private SendingContext myOptSendingContext;

    /**
     * Makes (what should be) the one resolver for resolving sRef. sRef should
     * start out switchable, and forwarding to a BufferingRef, for which 'buf'
     * is the buffer.
     */
    LocalResolver(Ref sRef, FlexList buf) {
        myOptRef = sRef;
        myOptBuf = buf;
        myOptSendingContext = null;
    }

    public synchronized void traceWhen() {
        if (Trace.causality.debug && Trace.ON) {
            synchronized (LocalResolver.class) {
                myConditionID = nextConditionID;
                nextConditionID += 1;
            }

            Trace.causalityLogger.log(new NewResolver(new SendingContext("LocalResolver")));
        }
    }

    /**
     * @param target
     * @param strict
     * @return
     */
    public boolean resolve(Object target, boolean strict) {
        if (null == myOptRef) {
            T.require(!strict, "Already resolved");
            return false;
        } else {
            if (myConditionID != -1) {
                SendingContext context = new SendingContext("LocalResolver/resolve");
                Trace.causalityLogger.log(new Fulfilled(context));
                Trace.causalityLogger.log(new Got(context));
            }

            myOptRef.setTarget(Ref.toRef(target));
            myOptRef.commit();
            if (null != myOptBuf) {
                BufferingRef.deliverAll(myOptBuf, target, myOptSendingContext);
            }
            myOptRef = null;
            myOptBuf = null;
            return true;
        }
    }

    /**
     * @param target
     */
    public void resolve(Object target) {
        resolve(target, true);
    }

    public boolean resolveRace(Object target) {
        return resolve(target, false);
    }

    /**
     * @return
     */
    public boolean smash(Throwable problem) {
        return resolve(new UnconnectedRef(problem), false);
    }

    /**
     *
     */
    public boolean isDone() {
        return null == myOptRef;
    }

    /**
     *
     */
    public void gettingCloser() {
        myOptSendingContext =
          new SendingContext("SCcloser", myOptSendingContext);

        if (myConditionID == -1) {
            traceWhen();
        } else {
            SendingContext context = new SendingContext("LocalResolver/gettingCloser");
            Trace.causalityLogger.log(new Progressed(context));
        }
    }

    /**
     *
     */
    public String toString() {
        if (isDone()) {
            return "<Closed Resolver>";
        } else {
            return "<Resolver>";
        }
    }

    private class Fulfilled extends CausalityLogRecord {
        Fulfilled(SendingContext context) {
            super(context, null, "resolver#" + myConditionID);
        }

        public String[] getEventClass() {
            return new String[] {"org.ref_send.log.Fulfilled", "org.ref_send.log.Resolved"};
        }

        protected String getText() {
            return null;
        }
    }

    private class Got extends CausalityLogRecord {
        Got(SendingContext context) {
            super(context, "m#" + myConditionID);
        }

        public String[] getEventClass() {
            return new String[] {"org.ref_send.log.Got"};
        }

        protected String getText() {
            return null;
        }
    }

    private class Progressed extends CausalityLogRecord {
        Progressed(SendingContext context) {
            super(context, null, "resolver#" + myConditionID);
        }

        public String[] getEventClass() {
            return new String[] {"org.ref_send.log.Progressed", "org.ref_send.log.Resolved"};
        }

        protected String getText() {
            return null;
        }
    }

    private class NewResolver extends CausalityLogRecord {
        NewResolver(SendingContext context) {
            super(context, "m#" + myConditionID, "resolver#" + myConditionID);
        }

        public String[] getEventClass() {
            return new String[] {"org.ref_send.log.SentIf", "org.ref_send.log.Sent"};
        }

        protected String getText() {
            return null;
        }
    }
}
