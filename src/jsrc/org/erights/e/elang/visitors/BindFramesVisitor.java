package org.erights.e.elang.visitors;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.AuditorExprs;
import org.erights.e.elang.evm.FastCallExpr;
import org.erights.e.elang.evm.CallExpr;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.EMatcher;
import org.erights.e.elang.evm.EMethod;
import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.EScript;
import org.erights.e.elang.evm.FinalPattern;
import org.erights.e.elang.evm.GuardedPattern;
import org.erights.e.elang.evm.MetaContextExpr;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.NounPattern;
import org.erights.e.elang.evm.ObjectExpr;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elang.evm.SeqExpr;
import org.erights.e.elang.evm.LiteralExpr;
import org.erights.e.elang.evm.StaticScope;
import org.erights.e.elang.scope.Scope;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.scope.StaticContext;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elang.evm.OuterNounExpr;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elib.prim.ScriptMaker;
import org.erights.e.elib.prim.JavaMemberNode;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.base.Script;
import java.lang.reflect.Member;
import org.erights.e.elang.interp.TypeLoader;
import org.erights.e.meta.java.lang.InterfaceGuardSugar;

/**
 * @author E. Dean Tribble
 */
public abstract class BindFramesVisitor extends BaseBindVisitor {

    final int[] myMaxLocalsCell;

    /**
     * A verified and bound Kernel-E tree.
     */
    ObjectExpr myOptSource;

    /**
     *
     */
    static public BindFramesVisitor make(Scope scope) {
        return new BindOuterFramesVisitor(scope);
    }

    /**
     *
     */
    protected BindFramesVisitor(Scope bindings,
                                int[] localsCell,
                                ObjectExpr optSource) {
        super(bindings);
        myMaxLocalsCell = localsCell;
        myOptSource = optSource;
    }

    /**
     *
     */
    private BindNestedFramesVisitor nestLocals() {
        // NOTE the int cell is initialized by Java to 0. This is a cell to
        // accumulate the max locals for any part of the nested method or
        // matcher.
        return new BindNestedFramesVisitor(myScope.nest(),
                                           0,
                                           new int[1],
                                           myOptSource);
    }

    /**
     *
     */
    private BindNestedFramesVisitor nestObject(ConstMap newSynEnv) {
        ScopeLayout inner =
          ScopeLayout.make(-1, newSynEnv, myScope.getFQNPrefix()).nest();
        return new BindNestedFramesVisitor(myScope.update(inner),
                                           0,
                                           myMaxLocalsCell,
                                           myOptSource);
    }

    /**
     *
     */
    public int maxLocals() {
        return myMaxLocalsCell[0];
    }

    /**
     *
     */
    private NounPattern asField(NounExpr noun,
                                NounPattern namer,
                                FlexList fields) {
        if (noun.isOuter()) {
            return namer;
        }
        NounExpr newNoun = noun.asFieldAt(fields.size());
        fields.push(noun);
        return namer.withNounExpr(newNoun);
    }

    /**************************** EExprs **************************/

    /**
     *
     */
    public Object visitObjectExpr(ENode optOriginal,
                                  String docComment,
                                  GuardedPattern oName,
                                  AuditorExprs auditors,
                                  EScript eScript) {
        GuardedPattern guarded = (GuardedPattern)xformPattern(oName);
        BindFramesVisitor t = (BindNestedFramesVisitor)nest(guarded);
        t.myOptSource = (ObjectExpr)optOriginal;
        AuditorExprs xauds = (AuditorExprs)t.run(auditors);

        StaticScope ss = eScript.staticScope();
        String[] used = (String[])ss.namesUsed().getKeys(String.class);
        FlexMap newSynEnv =
          FlexMap.fromTypes(String.class, NounPattern.class, used.length);
        FlexList fields = FlexList.fromType(NounExpr.class, used.length);
        for (int i = 0, max = used.length; i < max; i++) {
            String varName = used[i];
            NounPattern namer = t.myScope.getScopeLayout().getPattern(varName);
            NounExpr noun = namer.getNoun();
            namer = t.asField(noun, namer, fields);
            newSynEnv.put(varName, namer);
        }
        BindFramesVisitor nested = t.nestObject(newSynEnv.snapshot());
        NounExpr[] fieldNouns = (NounExpr[])fields.getArray(NounExpr.class);
        ObjectExpr result = new ObjectExpr(getOptSpan(optOriginal),
                                           docComment,
                                           guarded,
                                           xauds,
                                           nested.xformEScript(eScript),
                                           fieldNouns,
                                           (ObjectExpr)optOriginal,
                                           getOptScopeLayout());
        return result;
    }

    /**
     *
     */
    public Object visitMetaContextExpr(ENode optOriginal) {
        ScopeLayout layout = optOriginal.getScopeLayout();
        StaticContext context = new StaticContext(layout.getFQNPrefix(),
                                                  layout.getSynEnv(),
                                                  myOptSource);
        return new MetaContextExpr(getOptSpan(optOriginal),
                                   context,
                                   getOptScopeLayout());
    }

    /**
     * Eliminate the HideExpr, while preserving its scope boundary.
     */
    public Object visitHideExpr(ENode optOriginal, EExpr body) {
        return body.welcome(nest());
    }

    /**************************** Other **************************/

    /**
     *
     */
    public Object visitEMethod(ENode optOriginal,
                               String docComment,
                               String verb,
                               Pattern[] patterns,
                               EExpr optResultGuard,
                               EExpr body) {
        // NOTE the access to maxLocals is after the visitor is used on
        // the other children.
        BindFramesVisitor t = nestLocals();
        return new EMethod(getOptSpan(optOriginal),
                           docComment,
                           verb,
                           t.xformPatterns(patterns),
                           t.xformEExpr(optResultGuard),
                           t.xformEExpr(body),
                           t.maxLocals(),
                           getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitEMatcher(ENode optOriginal,
                                Pattern pattern,
                                EExpr body) {
        BindFramesVisitor t = nestLocals();
        return new EMatcher(getOptSpan(optOriginal),
                            t.xformPattern(pattern),
                            t.xformEExpr(body),
                            t.maxLocals(),
                            getOptScopeLayout());
    }

    /************************** Optimizations *********************/

    /**
     *
     */
    private EExpr killSillyReturn(FinalPattern pat, EExpr body) {
        // XXX Bug: Do this by name matching for now rather than using the
        // results of scope analysis. Will break if the ejector is shadowed
        // as of the end of body.
        String ejName = pat.getNoun().getName();

        if (body instanceof CallExpr) {
            CallExpr lastCall = (CallExpr)body;
            if ("run".equals(lastCall.getVerb()) &&
              lastCall.getRecipient() instanceof NounExpr &&
              ((NounExpr)lastCall.getRecipient()).getName().equals(ejName)) {

                int arity = lastCall.getArgs().length;
                if (0 == arity) {
                    // XXX is this right?
                    NounExpr optNULL = myScope.getScopeLayout().getOptNoun("null");
                    if (null != optNULL) {
                        // If null isn't in scope, we can't do this
                        // transformation. XXX This seems wrong.
                        return optNULL.withScopeLayout(getOptScopeLayout());
                    }
                } else if (1 == arity) {
                    return lastCall.getArgs()[0];
                }
            }
        } else if (body instanceof SeqExpr) {
            EExpr[] subs = ((SeqExpr)body).getSubs();
            int last = subs.length - 1;
            // Check for { ...; return ...; null }
            if (1 <= last && subs[last - 1] instanceof CallExpr) {
                CallExpr possibleEjectorCall = (CallExpr) subs[last - 1];
                EExpr simplified = killSillyReturn(pat, possibleEjectorCall);
                if (simplified != possibleEjectorCall) {
                    // If we simplified it, then it was an ejector call
                    EExpr[] newSubs = new EExpr[subs.length - 1];
                    System.arraycopy(subs, 0, newSubs, 0, last - 1);
                    newSubs[last - 1] = simplified;
                    // We call the constructor directly since all the parts are
                    // still to be visited.
                    return new SeqExpr(getOptSpan(body), newSubs, null);
                }
            }
            // Check for less common { ...; return ... }
            if (0 <= last) {
                EExpr[] newSubs = new EExpr[subs.length];
                System.arraycopy(subs, 0, newSubs, 0, last);
                newSubs[last] = killSillyReturn(pat, subs[last]);
                // We call the constructor directly since all the parts are
                // still to be visited.
                return new SeqExpr(getOptSpan(body), newSubs, null);
            }
        }
        return body;
    }

    /**
     * If the escape is a def and is never used, eliminate the escape.
     * <p/>
     * Under these conditions, the argPattern/catcher are irrelevant as well.
     */
    public Object visitEscapeExpr(ENode optOriginal,
                                  Pattern hatch,
                                  EExpr body,
                                  Pattern optArgPattern,
                                  EExpr optCatcher) {
        if (hatch instanceof FinalPattern) {
            // TODO this should just match against a quasipattern
            FinalPattern pat = (FinalPattern)hatch;
            if (null == pat.getOptGuardExpr()) {
                if (null == optArgPattern) {
                    body = killSillyReturn(pat, body);
                }
                StaticScope scope = body.staticScope();
                if (!scope.namesUsed().maps(pat.getNoun().getName())) {
                    if (!scope.hasMetaStateExpr()) {
                        // XXX kludge: This fixes just one special case. The
                        // right answer is to expand out MetaStateExpr in
                        // the VerifyVisitor.
                        return body.welcome(nest());
                    }
                }
            }
        }
        return super.visitEscapeExpr(optOriginal,
                                     hatch,
                                     body,
                                     optArgPattern,
                                     optCatcher);
    }

    /**
     *
     */
    public Object visitIfExpr(ENode optOriginal,
                              EExpr test,
                              EExpr then,
                              EExpr els) {
        if (test instanceof NounExpr) {
            // TODO this should just match against a quasipattern
            String name = ((NounExpr)test).getName();
            if ("true".equals(name)) {
                return then.welcome(nest());
            } else if ("false".equals(name)) {
                return els.welcome(nest());
            } else {
                // fall through to super
            }
        }
        return super.visitIfExpr(optOriginal, test, then, els);
    }

    public Object visitNounExpr(ENode optOriginal, String varName) {
        Object noun = super.visitNounExpr(optOriginal, varName);
        if (noun instanceof OuterNounExpr) {
            EvalContext evalContext = myScope.newContext(0);
            Slot slot = ((OuterNounExpr) noun).getSlot(evalContext);
            if (slot != null && slot instanceof FinalSlot) {
                //System.out.println("FinalSlot expr: " + noun + " -> " + slot.get());
                return new LiteralExpr((OuterNounExpr) noun, slot.get());
            } else {
                //System.out.println("Can't expand " + noun);
            }
        }
        return noun;
    }

    private Object[] literalArgs(EExpr[] args) {
        Object[] argValues = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof LiteralExpr) {
                argValues[i] = ((LiteralExpr) args[i]).getValue();
            } else {
                return null;
            }
        }
        return argValues;
    }

    /** If we know the type of the recipient at compile-time, look up the method now.
     */
    public Object visitCallExpr(ENode optOriginal,
                                EExpr recip,
                                String verb,
                                EExpr[] args) {
        EExpr xRecip = xformEExpr(recip);
        EExpr[] xArgs = xformEExprs(args);

        CallExpr newCall = new CallExpr(getOptSpan(optOriginal),
                            xRecip,
                            verb,
                            xArgs,
                            getOptScopeLayout());

        if (xRecip instanceof LiteralExpr) {
            Object value = ((LiteralExpr) xRecip).getValue();
            // XXX: what should we do if we can't find the method?
            // That means this call will always fail at runtime. But this code may be unreachable. Should
            // we throw an error or carry on?
            if (value == null) {
                throw new RuntimeException("Calling " + verb + " on null: " + getOptSpan(optOriginal));
            } else {
                // Certain safe objects can be invoked at compile time...
                if (value == TypeLoader.THE_ONE && verb.equals("get")) {
                    Object[] argValues = literalArgs(xArgs);
                    if (argValues != null) {
                        return new LiteralExpr(newCall,
                                       E.callAll(value, verb, argValues));
                    }
                }

                if (value instanceof InterfaceGuardSugar && verb.equals("coerce")) {
                    Object[] argValues = literalArgs(xArgs);
                    if (argValues != null) {
                        return new LiteralExpr(newCall,
                                       E.callAll(value, verb, argValues));
                    }
                }

                // Otherwise, at least shorten the script...
                Script script = ScriptMaker.THE_ONE.instanceScript(value.getClass());
                script = script.shorten(value, verb, args.length);
                return new FastCallExpr(getOptSpan(optOriginal),
                                    xRecip,
                                    verb,
                                    xArgs,
                                    value,
                                    script,
                                    getOptScopeLayout());
                /*
                if (script instanceof JavaMemberNode) {
                    //Member member = (Member) ((JavaMemberNode) script).member();
                    //System.out.println("Expand call " + value + "." + verb + " to " + member);

                }
                */
            }
        }
        
        return newCall;
    }

    public Object visitSeqExpr(ENode optOriginal, EExpr[] subs) {
        EExpr[] xSubs = xformEExprs(subs);
        int i = 0;
        int j = 0;
        for (; i < xSubs.length - 1; i++) {
            if (xSubs[i] instanceof LiteralExpr) {
                if (((LiteralExpr) xSubs[i]).getValue() != null) {
                    System.out.println("Warning: constant expression with no effect: " + subs[i] + "\n" + getOptSpan(subs[i]));
                }
            } else {
                xSubs[j] = xSubs[i];
                j += 1;
            }
        }
        if (j == 0) {
            return xSubs[i];
        }
        if (i == j) {
            return new SeqExpr(getOptSpan(optOriginal),
                               xSubs,
                               getOptScopeLayout());
        } else {
            EExpr[] newSubs = new EExpr[j + 1];
            System.arraycopy(xSubs, 0, newSubs, 0, j);
            newSubs[j] = xSubs[i];
            return new SeqExpr(getOptSpan(optOriginal),
                               newSubs,
                               getOptScopeLayout());
        }
    }
}
