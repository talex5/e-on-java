package org.erights.e.elang.scope;

import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.AssocFunc;
// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.FinalPattern;
import org.erights.e.elang.evm.LiteralSlotNounExpr;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.NounPattern;
import org.erights.e.elang.evm.ParseNode;
import org.erights.e.elang.evm.SlotPattern;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.tables.FlexSet;
import org.erights.e.meta.java.math.EInt;

/**
 * The end of the ScopeLayout chain, backed by an EEnv.
 */
public class ScopeLayoutEnv extends ScopeLayout {
    private final EEnv myEnv;

    static public final StaticMaker ScopeLayoutEnvMaker =
      StaticMaker.make(ScopeLayoutEnv.class);

    public ScopeLayoutEnv(EEnv env) {
        super(0);               // No outers; we use SlotPatterns instead
        myEnv = env;
        ensureValidFQNPrefix(env.getFQNPrefix());
    }

    public Object[] getSpreadUncall() {
        // why do we need this?
        Object[] result = {ScopeLayoutMaker,
          "run",
          myEnv};
        return result;
    }

    public String getFQNPrefix() {
        return myEnv.getFQNPrefix();
    }

    public ScopeLayout withPrefix(String fqnPrefix) {
        return new ScopeLayoutEnv(myEnv.withPrefix(fqnPrefix));
    }

    public NounPattern getOptPattern(String varName) {
        Slot slot = (Slot) myEnv.getSlots().fetch(varName, ValueThunk.NULL_THUNK);
        if (slot != null) {
            NounExpr nounExpr = new LiteralSlotNounExpr(null, varName, slot, null);
            if (slot instanceof FinalSlot) {
                return new FinalPattern(null, nounExpr, null, true, null);
            } else {
                return new SlotPattern(null, nounExpr, null, true, null);
            }
        }
        return null;
    }

    boolean contains(String varName) {
        return myEnv.maps(varName);
    }

    void addNamesTo(FlexSet names) {
        names.addAll(myEnv.getSlots().domain());
    }

    /**
     * Everything is shadowable as far as we're concerned.
     */
    public void requireShadowable(String varName, ParseNode optPoser) {
        return;
    }
}
