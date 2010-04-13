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

import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.slot.Slot;
import org.erights.e.meta.java.math.EInt;

import java.io.IOException;

/**
 * @author E. Dean Tribble
 */
public class LocalSlotNounExpr extends NestNounExpr {

    static private final long serialVersionUID = 7992767560378360660L;

    private final int myIndex;

    public LocalSlotNounExpr(SourceSpan optSpan,
                             String name,
                             int index,
                             ScopeLayout optScopeLayout) {
        super(optSpan, name, optScopeLayout);
        myIndex = index;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(LocalSlotNounExpr.class),
          "run",
          getOptSpan(),
          getName(),
          EInt.valueOf(myIndex),
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Slot getSlot(EvalContext ctx) {
        return (Slot)ctx.local(myIndex);
    }

    /**
     *
     */
    public NounExpr asFieldAt(int index) {
        return new FrameSlotNounExpr(getOptSpan(),
                                     getName(),
                                     index,
                                     getOptScopeLayout());
    }

    /**
     *
     */
    public NounExpr withScopeLayout(ScopeLayout optScopeLayout) {
        return new LocalSlotNounExpr(getOptSpan(),
                                     getName(),
                                     myIndex,
                                     optScopeLayout);
    }

    /**
     *
     */
    public void initSlot(EvalContext ctx, Slot slot) {
//System.err.println("Initing: " + name() + " at" + myIndex);
        ctx.initLocal(myIndex, slot);
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        super.subPrintOn(out, priority);
//        out.print("$lv", new Integer(myIndex));
    }

    public int getIndex() {
        return myIndex;
    }
}
