package org.erights.e.elib.ref;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

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


/**
 * A OldRemotePromise is a EProxy intended to represent a promise that will be
 * resolved remotely (in another vat).
 * <p/>
 * A OldRemotePromise is born handled (delegating to its EProxyHandler). While it
 * is handled, it is EVENTUAL and has no identity. It is therefore also
 * unsettled.
 *
 * @author Mark S. Miller
 */
class OldRemotePromise extends EProxy {
    /** People to notify when we're committed. */
    private FlexList myOptCommitReactors;

    /**
     *
     */
    OldRemotePromise(EProxyHandler handler) {
        super(handler);
    }

    /**
     *
     */
    public boolean isResolved() {
        if (null == myOptTarget) {
            return false;
        } else {
            return myOptTarget.isResolved();
        }
    }

    /**
     * To set me is to commit me
     */
    void setTarget(Ref newTarget) {
        if (null != myOptTarget) {
            T.fail("No longer handled");
        }
        EProxyHandler handler = myOptHandler;
        myOptHandler = null;

        myOptTarget = TheViciousRef;
        TheViciousRef.optProblem().fillInStackTrace();
        newTarget = newTarget.resolutionRef();
        if (newTarget == TheViciousRef) {
            myOptTarget =
              new UnconnectedRef(new ViciousCycleException("Ref loop"));
        } else {
            myOptTarget = newTarget;
        }
        //XXX should we allow throws to propogate?
        handler.handleResolution(myOptTarget);

        if (myOptCommitReactors != null) {
            final Ref finalTarget = newTarget;
            myOptCommitReactors.iterate(new AssocFunc() {
                public void run(Object key, Object reactor) {
                    Throwable optProblem = finalTarget.whenResolved(reactor);
                    if (optProblem != null) {
                        Trace.causality.warningm("whenResolved failed", optProblem);
                    }
                }
            });
        }
    }

    /**
     * @param out
     * @throws java.io.IOException
     */
    public void __printOn(TextWriter out) throws IOException {
        if (null == myOptTarget) {
            out.print("<Promise>");
        } else {
            myOptTarget.__printOn(out);
        }
    }

    public Throwable whenResolved(Object reactor) {
        if (myOptTarget == null) {
            if (myOptCommitReactors == null) {
                myOptCommitReactors = ConstList.EmptyList.diverge();
            }
            myOptCommitReactors.push(reactor);
            return null;
        } else {
            return myOptTarget.whenResolved(reactor);
        }
    }
}
