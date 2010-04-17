package org.erights.e.elang.interp;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.base.Script;
import org.erights.e.elib.base.Callable;
import org.erights.e.elib.prim.E;


/**
 * An StaticMaker on this class is the function named "__loop" in the
 * universalScope (and therefore also in the safeScope). <p>
 * <p/>
 * This wrapper is used as the E language's primitive looping construct. When
 * called as a one-argument function, it just repeatedly calls its argument as
 * a zero-argument, bool-returning function until that function returns
 * 'false'.
 *
 * @author Mark S. Miller.
 */
public class Loop {

    /**
     *
     */
    static public final Loop THE_ONE = new Loop();
    static private final Object[] NO_ARGS = new Object[0];

    /**
     *
     */
    private Loop() {
    }

    /**
     * Keep calling loopBody until it returns false.
     */
    public void run(Callable loopBody) {
        Script script = loopBody.optShorten("run", 0);
        if (script == null) {
            while (E.asBoolean(loopBody.callAll("run", NO_ARGS))) {
            }
        } else {
            script = script.shorten(loopBody, "run", 0);
            while (((Boolean) script.execute(loopBody, "run", NO_ARGS)).booleanValue()) {
            }
        }
    }

    /**
     *
     */
    public String toString() {
        return "<__loop>";
    }
}
