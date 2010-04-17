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
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.meta.java.math.EInt;
import org.erights.e.elib.base.Script;
import org.erights.e.elib.prim.ScriptMaker;

import java.io.IOException;


/**
 * BNF: "match" pattern "{" expr "}"
 * <p/>
 * Used as a clause in a switch (until transformed away), and used as the
 * otherwise case within a dispatch. In this latter role, it's pattern is
 * matched against a pair of the message verb and array of arguments, and then
 * the body is evaluated in a child of that scope.
 *
 * @author Mark S. Miller
 */
public class EMatcher extends ENode implements Script {
    private static class SuperScript implements Script {
        private Object myTarget;
        private Script myScript;

        private SuperScript(Object target, Script script) {
            myTarget = target;
            myScript = script;
        }

        public boolean canHandleR(Object optShortSelf) {
            return false;
        }

        public boolean respondsTo(Object optSelf, String verb, int arity) {
            throw new RuntimeException("Shouldn't be called");
        }

        public void protocol(Object optSelf, FlexList mTypes) {
            throw new RuntimeException("Shouldn't be called");
        }

        public Object execute(Object optSelf, String verb, Object[] args) {
            return myScript.execute(myTarget, verb, args);
        }

        public Script shorten(Object optSelf, String aVerb, int arity) {
            return this;
        }

        public String toString() {
            return myScript.toString();
        }
    }

    static private final long serialVersionUID = -5850612086101187616L;

    private final Pattern myPattern;

    private final EExpr myBody;

    private final int myLocalCount;

    /**
     *
     */
    public EMatcher(SourceSpan optSpan,
                    Pattern pattern,
                    EExpr body,
                    ScopeLayout optScopeLayout) {
        this(optSpan, pattern, body, -1, optScopeLayout);
    }

    /**
     *
     */
    public EMatcher(SourceSpan optSpan,
                    Pattern pattern,
                    EExpr body,
                    int localCount,
                    ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myPattern = pattern;
        myBody = body;
        myLocalCount = localCount;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(EMatcher.class),
          "run",
          getOptSpan(),
          myPattern,
          myBody,
          EInt.valueOf(myLocalCount),
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitEMatcher(this, myPattern, myBody);
    }

    /**
     *
     */
    public Pattern getPattern() {
        return myPattern;
    }

    /**
     *
     */
    public EExpr getBody() {
        return myBody;
    }

    /**
     * When staticScope() is first requested on a given node, it calls
     * computeStaticScope() to do the actual computation, which is then
     * remembered.
     */
    protected StaticScope computeStaticScope() {
        StaticScope result = myPattern.staticScope();
        result = result.add(myBody.staticScope()).hide();
        return result;
    }

    /**
     *
     */
    Object execute(Object optSelf,
                   String verb,
                   Object[] args,
                   OneArgFunc optEjector) {
        Object[] message = {verb, ConstList.fromArray(args)};
        EImpl self = (EImpl)Ref.resolution(optSelf);
        EvalContext ctx = self.newContext(myLocalCount);
        myPattern.testMatch(ctx, ConstList.fromArray(message), optEjector);
        return myBody.subEval(ctx, true);
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        EMatcher other;
        try {
            other = (EMatcher)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        myPattern.subMatchBind(args, other.myPattern, optEjector, bindings);
        myBody.subMatchBind(args, other.myBody, optEjector, bindings);
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("match ");
        myPattern.subPrintOn(out, PR_PATTERN);
        out.print(" ");
        myBody.printAsBlockOn(out);
    }

    /** See if we just forward messages to some object. If so, return it. */
    private Object optShortenSuper(NounExpr noun) {
        // Optimise the special case (the expansion of super):
        //   match noun { E.callWithPair(super, noun) }
        if (!(myBody instanceof FastCallExpr)) {
            return null;
        }

        FastCallExpr call = (FastCallExpr) myBody;
        Object rcvr = call.getReceiver();
        if (!(rcvr instanceof StaticMaker)) {
            return null;
        }
        
        String type = ((StaticMaker) rcvr).asType().getFQName();
        if (!type.equals("org.erights.e.elang.interp.E4E")) {
            return null;
        }

        if (!call.getVerb().equals("callWithPair")) {
            return null;
        }

        EExpr[] args = call.getArgs();
        if (args.length != 2) {
            return null;
        }

        if (args[0] instanceof LiteralExpr && args[1] instanceof LocalFinalNounExpr) {
            // super known at compile-time
            Object supr = ((LiteralExpr) args[0]).getValue();
            LocalFinalNounExpr theNoun = (LocalFinalNounExpr) args[1];
            if (noun.getName().equals(theNoun.getName())) {
                // OK, everything matches! Any call to "this" simply forwards to "supr".
                //System.out.println("Forward to " + supr);

                return supr;
            }
        }

        return null;
    }

    /** Would this matcher match a call to aVerb/arity?
      * If yes, return a script for that (possibly ==ifUnsure).
      * If not , return null.
      * If unsure (depends on the arguments), return ifUnsure.
      */
    public Script shorten(String aVerb, int arity, Script ifUnsure) {
        if (myPattern instanceof GuardedPattern) {
            if (((GuardedPattern) myPattern).getOptGuardExpr() != null) {
                return ifUnsure;
            }
            Class patternClass = myPattern.getClass();
            if (patternClass == FinalPattern.class) {
                NounExpr noun = ((FinalPattern) myPattern).getNoun();

                // We certainly match. But maybe we can shorten further by looking in the body...
                Object forward = optShortenSuper(noun);
                if (forward != null) {
                    Script suprScript = ScriptMaker.THE_ONE.instanceScript(forward.getClass());
                    return new SuperScript(forward, suprScript.shorten(forward, aVerb, arity));
                } else {
                    return this;
                }
            }
        } else if (myPattern instanceof ListPattern) {
            Pattern[] subs = ((ListPattern) myPattern).getSubPatterns();
            if (subs.length != 2) {
                return null;
            }
            //System.out.println("p: " + subs[0] + ", " + subs[0].getClass());
        }
        return ifUnsure;
    }

    /* Script methods */

    public boolean canHandleR(Object optShortSelf) {
        return true;    // we never looked at self in the first place, so result is still valid
    }

    public boolean respondsTo(Object optSelf, String verb, int arity) {
        throw new RuntimeException("Shouldn't be called");
    }

    public void protocol(Object optSelf, FlexList mTypes) {
        throw new RuntimeException("Shouldn't be called");
    }

    public Object execute(Object optSelf, String verb, Object[] args) {
        return execute(optSelf, verb, args, null);
    }

    public Script shorten(Object optSelf, String aVerb, int arity) {
        return this;
    }
}
