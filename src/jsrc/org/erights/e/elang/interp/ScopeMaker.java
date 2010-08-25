package org.erights.e.elang.interp;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.FinalPattern;
import org.erights.e.elang.evm.LiteralNounExpr;
import org.erights.e.elang.evm.LiteralSlotNounExpr;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.NounPattern;
import org.erights.e.elang.evm.OuterNounExpr;
import org.erights.e.elang.scope.EEnv;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elib.slot.RuinedSlot;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.tables.ConstSet;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.tables.Twine;

/**
 * Used to make {@link EEnv EEnv}s.
 * <p/>
 * Note that you can use a ScopeMaker to make scopes containing unshadowable
 * names. This is how the safeScope itself is built.
 *
 * @author E. Dean Tribble
 */
class ScopeMaker {

    static private final int DEFAULT_SIZE = 50;

    private final FlexMap myOuters;

    ScopeMaker() {
        this(FlexMap.fromTypes(String.class, Slot.class, DEFAULT_SIZE));
    }

    /**
     * @param synEnv Must be a map in which each association is
     *               <pre>    varName =&gt; {@link Slot}</pre>
     */
    private ScopeMaker(FlexMap outers) {
        myOuters = outers;
    }

    /**
     *
     */
    public ScopeMaker copy() {
        return new ScopeMaker(myOuters.diverge(String.class, Slot.class));
    }

    /**
     *
     */
    public ConstSet getVarNames() {
        return (ConstSet) myOuters.snapshot().domain();
    }

    /**
     *
     */
    public EEnv make(String fqnPrefix) {
        return new EEnv(myOuters.snapshot(), fqnPrefix);
    }

    /**
     * Generate a bindings for a noun that will be compiled into transformed
     * code.
     */
    public void comp(String name, Object value) {
        myOuters.put(name, new FinalSlot(value));
//        init(name, value);
    }

    /**
     * Generate a lazy bindings for a noun that will be compiled into
     * transformed code.
     */
    public void comp(String name, Object scope, String srcstr) {
        Slot slot = new LazyEvalSlot(scope, Twine.fromString(srcstr));
        myOuters.put(name, slot);
//        init(name, scope, srcstr);
    }

    /**
     *
     */
    public void init(String name, Object value) {
        Slot slot = new FinalSlot(value);
        initSlot(name, slot);
    }

    /**
     *
     */
    public void init(String name, Object scope, String srcstr) {
        Slot slot = new LazyEvalSlot(scope, Twine.fromString(srcstr));
        initSlot(name, slot);
    }

    /**
     *
     */
    public void ruin(String name, String complaint) {
        initSlot(name, new RuinedSlot(new RuntimeException(complaint)));
    }

    /**
     *
     */
    public void initSlot(String name, Slot slot) {
        myOuters.put(name, slot);
    }
}
