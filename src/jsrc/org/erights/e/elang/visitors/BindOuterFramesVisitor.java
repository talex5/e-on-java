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
                                           null);
    }

    /**
     *
     */
    KernelECopyVisitor nest() {
        return new BindNestedFramesVisitor(myScope.nest(),
                                           0,
                                           myMaxLocalsCell,
                                           null);
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
}
