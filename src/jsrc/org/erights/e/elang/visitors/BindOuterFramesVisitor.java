package org.erights.e.elang.visitors;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elang.evm.GuardedPattern;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.OuterNounExpr;
import org.erights.e.elang.scope.Scope;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.AtomicExpr;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.DefineExpr;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.LiteralExpr;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elang.evm.FinalPattern;

/**
 * @author E. Dean Tribble
 */
class BindOuterFramesVisitor extends BindFramesVisitor {

    /**
     *
     */
    BindOuterFramesVisitor(Scope scope) {
        super(scope, new int[1], null);
    }

    /**
     *
     */
    KernelECopyVisitor nest(GuardedPattern oName) {
        ScopeLayout nested = myScope.getScopeLayout().nest(oName.getOptName());
        return new BindNestedFramesVisitor(myScope.update(nested),
                                           0,
                                           myMaxLocalsCell,
                                           null,
                                           this);
    }

    /**
     *
     */
    KernelECopyVisitor nest() {
        return new BindNestedFramesVisitor(myScope.nest(),
                                           0,
                                           myMaxLocalsCell,
                                           null,
                                           this);
    }

    /**
     *
     */
    NounExpr newFinal(SourceSpan optSpan, String varName) {
        return newVar(optSpan, varName);
    }

    /**
     *
     */
    NounExpr newVar(SourceSpan optSpan, String varName) {
        int outerCount = myScope.getScopeLayout().getOuterCount();
        T.require(0 <= outerCount, "internal: scope confusion: ", varName);
        return new OuterNounExpr(optSpan,
                                 varName,
                                 outerCount,
                                 getOptScopeLayout());
    }

    public Object visitDefineExpr(ENode optOriginal,
                                  Pattern patt,
                                  EExpr optEjectorExpr,
                                  EExpr rValue) {
        DefineExpr xDefine = (DefineExpr) super.visitDefineExpr(optOriginal, patt, optEjectorExpr, rValue);

        Pattern xPattern = xDefine.getPattern();
        if (xPattern instanceof FinalPattern) {
            FinalPattern finalPattern = (FinalPattern) xPattern;
            if (finalPattern.getOptGuardExpr() == null) {
                NounExpr xNoun = finalPattern.getNoun();
                EExpr xrValue = xDefine.getRValue();
                if (xNoun instanceof OuterNounExpr && xrValue instanceof LiteralExpr) {
                    Object value = ((LiteralExpr) xrValue).getValue();
                    //System.out.println("Constant: " + xNoun + " = " + value);
                    xNoun.initFinal(myScope.newContext(0), value);
                    return xrValue;
                }
            }
        }

        return xDefine;
    }
}
