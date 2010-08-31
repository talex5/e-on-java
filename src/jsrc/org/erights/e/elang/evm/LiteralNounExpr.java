package org.erights.e.elang.evm;

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
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.util.AlreadyDefinedException;

/**
 * A noun-expr for compile-time literals, particularly universal constants.
 * <p/>
 * These only occur in transformed E.
 *
 * @author E. Dean Tribble
 */
public class LiteralNounExpr extends TopNounExpr {

    static private final long serialVersionUID = -3710049002517188374L;

    private final Object myValue;

    /**
     * Constructor.
     * Value must be DeepPassByCopy, but we don't check this.
     *
     * @param optSpan
     * @param name
     * @param value
     * @param optScopeLayout
     */
    public LiteralNounExpr(SourceSpan optSpan,
                           String name,
                           Object value,
                           ScopeLayout optScopeLayout) {
        super(optSpan, name, optScopeLayout);
        myValue = value;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(LiteralNounExpr.class),
          "run",
          getOptSpan(),
          getName(),
          myValue,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Slot getSlot(EvalContext ctx) {
        return new FinalSlot(myValue);
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        return myValue;
    }

    /**
     *
     */
    public NounExpr asFieldAt(int index) {
        // XXX is this right?
//        return new FrameFinalNounExpr(getOptSpan(),
//                                      getOptScopeLayout(),
//                                      name(),
//                                      index);
        return this;
    }

    /**
     *
     */
    public NounExpr withScopeLayout(ScopeLayout optScopeLayout) {
        return new LiteralNounExpr(getOptSpan(),
                                   getName(),
                                   myValue,
                                   optScopeLayout);
    }

    /**
     *
     */
    public void initFinal(EvalContext ctx, Object value) {
        throw new AlreadyDefinedException(
          "Cannot redefine a universal constant" + getName());
    }

    /**
     * Shouldn't happen
     */
    public void initSlot(EvalContext ctx, Slot slot) {
        T.fail("Internal: Not a slot variable: " + this);
    }
}
