package org.erights.e.elang.visitors;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.AuditorExprs;
import org.erights.e.elang.evm.FastCallExpr;
import org.erights.e.elang.evm.CallExpr;
import org.erights.e.elang.evm.CompilerFlags;
import org.erights.e.elang.evm.DefineExpr;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.EMatcher;
import org.erights.e.elang.evm.EMethod;
import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.EScript;
import org.erights.e.elang.evm.FinalPattern;
import org.erights.e.elang.evm.GuardedPattern;
import org.erights.e.elang.evm.LiteralExpr;
import org.erights.e.elang.evm.LiteralNounExpr;
import org.erights.e.elang.evm.MetaContextExpr;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.NounPattern;
import org.erights.e.elang.evm.ObjectExpr;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elang.evm.SeqExpr;
import org.erights.e.elang.evm.StaticScope;
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
import org.erights.e.develop.exception.EBacktraceException;

/**
 * @author E. Dean Tribble
 */
public abstract class BindFramesVisitor extends BaseBindVisitor {

    final int[] myMaxLocalsCell;

    protected final CompilerFlags myCompilerFlags;

    /**
     * A verified and bound Kernel-E tree.
     */
    ObjectExpr myOptSource;

    /**
     *
     */
    static public BindFramesVisitor make(ScopeLayout scopeLayout, CompilerFlags compilerFlags) {
        return new BindOuterFramesVisitor(scopeLayout, compilerFlags);
    }

    /**
     *
     */
    protected BindFramesVisitor(ScopeLayout bindings,
                                int[] localsCell,
                                ObjectExpr optSource,
                                CompilerFlags compilerFlags) {
        super(bindings);
        myMaxLocalsCell = localsCell;
        myOptSource = optSource;
        myCompilerFlags = compilerFlags;
    }

    /**
     *
     */
    private BindNestedFramesVisitor nestLocals() {
        // NOTE the int cell is initialized by Java to 0. This is a cell to
        // accumulate the max locals for any part of the nested method or
        // matcher.
        return new BindNestedFramesVisitor(myLayout.nest(),
                                           0,
                                           new int[1],
                                           myOptSource,
                                           myCompilerFlags);
    }

    /**
     *
     */
    private BindNestedFramesVisitor nestObject(ConstMap newSynEnv) {
        ScopeLayout inner =
          ScopeLayout.make(-1, newSynEnv, myLayout.getFQNPrefix());
        return new BindNestedFramesVisitor(inner.nest(),
                                           0,
                                           myMaxLocalsCell,
                                           myOptSource,
                                           myCompilerFlags);
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
            NounPattern namer = t.myLayout.getPattern(varName);
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
                    NounExpr optNULL = myLayout.getOptNoun("null");
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

    protected void warn(ENode item, String message) {
        if (myCompilerFlags.warnings) {
            System.err.println("WARNING: " + message + "\n" +
                               "(at " + getOptSpan(item) + ")");
        }
    }

    private Object[] literalArgs(EExpr[] args) {
        Object[] argValues = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Slot optKnownArgSlot = args[i].getOptKnownSlot();
            if (optKnownArgSlot != null) {
                argValues[i] = optKnownArgSlot.get();
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

        Slot optKnownSlot = myCompilerFlags.basicOptimisations ? xRecip.getOptKnownSlot() : null;
        if (optKnownSlot != null && optKnownSlot instanceof FinalSlot) {
            Object value = optKnownSlot.get();
            // XXX: what should we do if we can't find the method?
            // That means this call will always fail at runtime. But this code may be unreachable. Should
            // we throw an error or carry on?
            if (value == null) {
                warn(optOriginal, "calling " + verb + " on null");
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
                Script script;
                try {
                    script = ScriptMaker.THE_ONE.instanceScript(value.getClass());
                    script = script.shorten(value, verb, args.length);
                } catch (Exception ex) {
                    throw new EBacktraceException(ex, "Compile error while looking up method in:\n  " + optOriginal + "\n@ " + getOptSpan(optOriginal));
                }
                return new FastCallExpr(getOptSpan(optOriginal),
                                    xRecip,
                                    verb,
                                    xArgs,
                                    value,
                                    script,
                                    getOptScopeLayout());
            }
        }

        return newCall;
    }

    public Object visitSeqExpr(ENode optOriginal, EExpr[] subs) {
        EExpr[] xSubs = xformEExprs(subs);
        int i = 0;
        int j = 0;
        for (; i < xSubs.length - 1; i++) {
            Slot optKnownSlot = myCompilerFlags.basicOptimisations ? xSubs[i].getOptKnownSlot() : null;
            if (optKnownSlot != null && optKnownSlot instanceof FinalSlot) {
                // OK
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

    public Object visitDefineExpr(ENode optOriginal,
                                  Pattern patt,
                                  EExpr optEjectorExpr,
                                  EExpr rValue) {
        ScopeLayout oldLayout = myLayout;

        Pattern xPatt = xformPattern(patt);
        EExpr xEjector = xformEExpr(optEjectorExpr);
        EExpr xValue = xformEExpr(rValue);

        if (xPatt instanceof FinalPattern && optEjectorExpr == null && myCompilerFlags.basicOptimisations) {
            FinalPattern finalPattern = (FinalPattern) xPatt;
            if (finalPattern.getOptGuardExpr() == null) {
                Slot optKnownSlot = xValue.getOptKnownSlot();
                if (optKnownSlot != null && optKnownSlot instanceof FinalSlot) {
                    /* This is a simple define to a value known at compile time.
                     * Instead of creating a new local variable, just add the
                     * value to the ScopeLayout.
                     */
                    NounExpr nounExpr = finalPattern.getNoun();
                    String varName = nounExpr.asNoun().getName();
                    NounExpr newNounExpr = new LiteralNounExpr(getOptSpan(optOriginal),
                                                               varName,
                                                               optKnownSlot.get(),
                                                               getOptScopeLayout());
                    NounPattern result = new FinalPattern(getOptSpan(optOriginal),
                                                          newNounExpr,
                                                          null,         // guard
                                                          getOptScopeLayout());
                    myLayout = oldLayout.with(varName, result);
                    return xValue;
                }
            }
        }

        return new DefineExpr(getOptSpan(optOriginal),
                              xPatt,
                              xEjector,
                              xValue,
                              getOptScopeLayout());
    }
}
