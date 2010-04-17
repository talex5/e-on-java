package org.erights.e.elang.visitors;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.GuardedPattern;
import org.erights.e.elang.evm.LocalFinalNounExpr;
import org.erights.e.elang.evm.FrameFinalNounExpr;
import org.erights.e.elang.evm.LocalSlotNounExpr;
import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.ObjectExpr;
import org.erights.e.elang.evm.LiteralExpr;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elang.evm.FinalPattern;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.DefineExpr;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elang.scope.Scope;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;
import java.util.Map;
import java.util.HashMap;

/**
 * @author E. Dean Tribble
 */
class BindNestedFramesVisitor extends BindFramesVisitor {
    private final Map myKnownLocals = new HashMap();  // var name => FinalSlot()
    private final BindFramesVisitor myParent;

    private int myNextLocal;

    /**
     *
     */
    BindNestedFramesVisitor(Scope bindings,
                            int localN,
                            int[] localsCell,
                            ObjectExpr optSource,
                            BindFramesVisitor parent) {
        super(bindings, localsCell, optSource);
        myNextLocal = localN;
        myOptSource = optSource;
        myParent = parent;
    }

    /**
     *
     */
    KernelECopyVisitor nest(GuardedPattern oName) {
        ScopeLayout nested = myScope.getScopeLayout().nest(oName.getOptName());
        return new BindNestedFramesVisitor(myScope.update(nested),
                                           myNextLocal,
                                           myMaxLocalsCell,
                                           myOptSource,
                                           this);
    }

    /**
     *
     */
    KernelECopyVisitor nest() {
        return new BindNestedFramesVisitor(myScope.nest(),
                                           myNextLocal,
                                           myMaxLocalsCell,
                                           myOptSource,
                                           this);
    }

    /**
     *
     */
    NounExpr newFinal(SourceSpan optSpan, String varName) {
        //System.out.println("undefine " + varName);
        myKnownLocals.put(varName, null);

        return new LocalFinalNounExpr(optSpan,
                                      varName,
                                      nextLocal(),
                                      getOptScopeLayout());
    }

    /**
     *
     */
    NounExpr newVar(SourceSpan optSpan, String varName) {
        //System.out.println("undefine " + varName);
        myKnownLocals.put(varName, null);

        return new LocalSlotNounExpr(optSpan,
                                     varName,
                                     nextLocal(),
                                     getOptScopeLayout());
    }

    /**
     *
     */
    private int nextLocal() {
        int index = myNextLocal;
        myNextLocal++;
        myMaxLocalsCell[0] = StrictMath.max(myNextLocal, myMaxLocalsCell[0]);
        return index;
    }

    protected Slot getCompileSlot(String varName) {
        if (myKnownLocals.containsKey(varName)) {
            return (Slot) myKnownLocals.get(varName);
        }
        return myParent.getCompileSlot(varName);
    }

    public Object visitDefineExpr(ENode optOriginal,
                                  Pattern patt,
                                  EExpr optEjectorExpr,
                                  EExpr rValue) {
        Object xDefine = super.visitDefineExpr(optOriginal, patt, optEjectorExpr, rValue);

        if (!(xDefine instanceof DefineExpr)) {
            return xDefine;
        }

        DefineExpr xDefineExpr = (DefineExpr) xDefine;
        Pattern xPattern = xDefineExpr.getPattern();

        if (xPattern instanceof FinalPattern) {
            FinalPattern finalPattern = (FinalPattern) xPattern;
            if (finalPattern.getOptGuardExpr() == null) {
                NounExpr xNoun = finalPattern.getNoun();
                EExpr xrValue = xDefineExpr.getRValue();
                if (xrValue instanceof LiteralExpr) {
                    // Value is known at compile time. Remember it.
                    Object value = ((LiteralExpr) xrValue).getValue();
                    myKnownLocals.put(xNoun.getName(), new FinalSlot(value));
                    System.out.println("Local constant: " + xNoun + " = " + value);
                }
            }
        }

        return xDefine;
    }

    public Object visitNounExpr(ENode optOriginal, String varName) {
        Object noun = super.visitNounExpr(optOriginal, varName);
        if (inSlotExpr)
            return noun;

        if (noun instanceof LocalFinalNounExpr ||
            noun instanceof FrameFinalNounExpr) {
            Slot known = getCompileSlot(((NounExpr) noun).getName());
            if (known != null) {
                System.out.println("LocalFinalNounExpr expr: " + noun + " -> " + known.get());
                return new LiteralExpr((NounExpr) noun, known.get());
            } else {
                //System.out.println("Can't expand " + noun);
            }
        }
        return noun;
    }

}
