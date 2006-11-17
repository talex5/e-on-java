package org.erights.e.elang.smallcaps;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elang.evm.AtomicExpr;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.EMatcher;
import org.erights.e.elang.evm.EMethod;
import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.EScript;
import org.erights.e.elang.evm.GuardedPattern;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.tables.Twine;

import java.math.BigInteger;

/**
 * Defines and outputs a simple byte code encoding of a Transformed-E AST.
 * <p/>
 * This instruction set must be straightforward to interpret with a simple
 * bytecode interpreter, where that interpreter can serve as an executable
 * specification for more ambitious compilation and interpretation schemes.
 * <p/>
 * Unlike a previous failed attempt at such an instruction set design, this
 * instruction set does not represent all the information in a Bound-E AST, and
 * so a Bound-E AST is not recoverable from it. Since auditors must be given a
 * Bound-E AST, this tree must be separately provided as a literal for these
 * instructions to refer to. </ul> This bytecode instruction set is to be
 * understood in terms of a state machine for an activation frame as
 * implemented by {@link SmallcapsActivation}. The state of a state machine
 * consists of<ul> <li>A reference to the code generated by this visitor,
 * shared with all other activations executing in the same code. <li>A program
 * counter, as an index into this code. <li>An {@link EvalContext}. <li>An
 * operand stack, holding arbitrary objects. <li><p>A handler stack, holding
 * problem handlers. An ambitious execution engine for this instruction set
 * should do what Java and C++ do -- use the side table trick (probably
 * invented by Barbara Liskov for CLU): Have a separate static table on the
 * side, indexed by PC, that's only consulted when handling a problem. This has
 * the advantage that it has zero overhead when a problem doesn't occur. It has
 * the disadvantage that it's a bit harder to explain, generate, and interpret.
 * For the purposes of {@link SmallcapsActivation}, these latter considerations
 * dominate, so we generate instructions designed to a model of a runtime
 * handler stack, and {@link SmallcapsActivation} implements according to this
 * model. Those wishing to implement to using the side-table trick should
 * probably start by translating these instructions into a format with an
 * explicit side table.
 * <p/>
 * Each handler is created and pushed in one operation. The handler remembers
 * the operand stack height at the time it was pushed. When a handler is
 * invoked or popped, it first truncates the operand stack back to that value.
 * This truncated operand stack must be as it was at the time the handler was
 * created-pushed. Each handler create/push intruction must have a balancing
 * pop instruction for exiting the handled region <i>normally</i> (defined
 * below).
 * <p/>
 * Since there are a fixed number of types of handlers, a C or C++ based
 * implementation can avoid dynamic allocation of handlers by allocating the
 * handler stack as a stack of a tagged union of these handler types. Handlers
 * are not reified to the E programmer, so the implementor has much freedom in
 * choosing actual representations. </ul>
 * <p/>
 * The encoding consists primarily of the encoding of expressions and patterns,
 * where these are generated forValue, forControl, or for neither value nor
 * control. In all cases, these are generated for <i>effects</i>, so we refer
 * to the last as forFxOnly. We define <i>effects</i> to be both side effects
 * and <i>abrupt exits</i> (defined below). A side-effect free expression
 * generated for effects only must still execute if its execution could require
 * an abrupt exit.
 * <pre>
 * [],[] =&gt; forValue(expr) =&gt; [result],[]
 * [],[] =&gt; forFxOnly(expr) =&gt; [],[]
 * [optEjector],[] =&gt; forControl(expr) =&gt; [],[]
 * <p/>
 * [specimen],[] =&gt; forFxOnly(pattern) =&gt; [],[]
 * [optEjector, specimen],[] =&gt; forControl(pattern) =&gt; [],[]
 * [specimen],[] =&gt; forValue(pattern) =&gt; [flag],[]</pre>
 * Each expression generated <b>forValue</b>, when executed, should it exit
 * normally, pops [] (nothing) and pushes [result] on the operand stack as its
 * result. By "pops nothing", we mean pops nothing that was on the stack prior
 * to the beginning of the expression. It will of course often use the stack to
 * push and pop the value of subexpressions and subpatterns. An expression
 * should leave the handler stack as it was, but will again use push and pop
 * values on the handler stack in handling subexpressions and subpatterns.
 * <p/>
 * Besides exiting normally, an expression may exit abrubtly -- with a thrown
 * exception, a thrown error, or an ejection. All are forms of non-local exit.
 * In this case, the top handler on the handler stack is invoked. What happens
 * then is up to it.
 * <p/>
 * Each expression generated <b>forFxOnly</b> executes the same as one
 * generated forValue, except [] (nothing) is pushed on the operand stack.
 * <p/>
 * Each expression generated <b>forControl</b>, when executed, pops
 * [optEjector] from the operand stack, and either invokes it or not depending
 * on the value of the expression. If the expression generated forValue would
 * have exited normally, and its value would have coerced to a boolean, then if
 * this boolean would have been <tt>true</tt>, then the expression generated
 * forControl just exits normally pushing [] (nothing) -- it <i>falls
 * through</i>. If the boolean would have been <tt>false</tt>, then the
 * expression exits abruptly according to optEjector -- it <i>branches</i>.
 * Finally, if the expression generated forValue would have exited abruply,
 * then the expression generated forControl exits abruptly in the same manner
 * -- by invoking the top handler on the handler stack. As with other normal
 * exits, when falling through, the operand stack must be in the correct state
 * (in this case, as it was after [optEjector] was popped). As with other
 * abrupt exits, the operand stack can have extra stuff on it when branching,
 * as the handlers will truncate the operand stack back to the correct height.
 * <p/>
 * Each pattern generated <b>forFxOnly</b>, when executed, pops [specimen]. If
 * the specimen matches the pattern, then the pattern exits normally with []
 * (nothing) pushed on the operand stack. If the specimen doesn't match, an
 * abrupt exit is performed by invoking the top handler on the handler stack.
 * Any other abrupt exit is also handled by invoking the top handler on the
 * handler stack.
 * <p/>
 * Each pattern generated <b>forControl</b>, when executed, pops [optEjector,
 * specimen] and pushes [] (nothing). If the specimen matches the pattern, then
 * the pattern exits normally with [] (nothing) pushed on the operand stack. If
 * the specimen doesn't match, a non-local exit is performed according to
 * optEjector. Any other abrupt exit is handled by invoking the top handler on
 * the handler stack.
 * <p/>
 * Each pattern generated <b>forValue</b>, when executed, pops [specimen]. If
 * the specimen matches the pattern, the pattern pushes [true] and exits
 * normally. If the specimen doesn't match, the pattern pushes [false] and
 * exits normally. Any abrupt exit is handled by invoking the top handler on
 * the handler stack.
 * <p/>
 * XXX This should be broken up into three subclasses for the three kinds,
 * rather than having each method test myKind.
 *
 * @author Mark S. Miller
 * @author Darius Bacon
 */
class SmallcapsEncoderVisitor implements ETreeVisitor, SmallcapsOps {

    // Implementation note:
    // We emit code from back to front instead of the more
    // conventional front to back, because the SmallcapsEmitter
    // interface requires it. (Why?  Because it's simpler to encode
    // the output that way, and not any harder on this side. It just
    // looks weird.)

    /**
     *
     */
    private final SmallcapsEmitter myEmitter;

    /**
     * One of "FOR_VALUE", "FOR_CONTROL", or "FOR_FX_ONLY", as an interned
     * string (so we can test using Java's "==").
     */
    private final String myKind;

    /**
     * Corresponding visitors of all different kinds.
     */
    private final SmallcapsVisitorTable myVisitors;

    /**
     *
     */
    SmallcapsEncoderVisitor(SmallcapsEmitter emitter,
                            String kind,
                            SmallcapsVisitorTable visitors) {
        myEmitter = emitter;
        myKind = kind;
        myVisitors = visitors;
    }

    /**
     *
     */
    void run(ENode eNode) {
        int bound = myEmitter.getLabel();
        eNode.welcome(this);
        int start = myEmitter.getLabel();
        SourceSpan optSpan = eNode.getOptSpan();
        if (null != optSpan) {
            // XXX associate optSpan with start..!bound
        }
    }

    /**
     *
     */
    void run(ENode[] eNodes) {
        for (int i = eNodes.length - 1; 0 <= i; i--) {
            run(eNodes[i]);
        }
    }

    /**************************** EExprs **************************/

    /**
     * Post-transformation, hide has no runtime effect beyond the evaluation of
     * its body, so just generate the body.
     * <pre>
     * all: this(body)</pre>
     */
    public Object visitHideExpr(ENode optOriginal, EExpr body) {
        run(body);
        return null;
    }

    /**
     * All subexpressions but the last are generated forFxOnly.
     * <pre>
     * all: forFxOnly(subs[0]) ... forFxOnly(subs[n-2]) this(subs[n-1])</pre>
     */
    public Object visitSeqExpr(ENode optOriginal, EExpr[] subs) {
        int last = subs.length - 1;
        subs[last].welcome(this);
        for (int i = last - 1; 0 <= i; i--) {
            subs[i].welcome(myVisitors.forFxOnly);
        }
        return null;
    }

    private void branchify() {
        if ("FOR_CONTROL" == myKind) {
            myEmitter.emitBranch();
        }
    }

    /**
     * A call expression evaluates the recipient and arguments left to right
     * forValue, then pops all these to perform an immediate call.
     * <pre>
     * forFxOnly: forValue(recip) forValue(args[0]) ... forValue(args[n-1])
     *            OP_CALL_ONLY(verb :UTF8, arity :WholeNum)</pre>
     * If {@link #OP_CALL_ONLY} exits successfully, then the value returned by
     * the call is ignored and [] (nothing) is pushed.
     * <pre>
     * forValue: forValue(recip) forValue(args[0]) ... forValue(args[n-1])
     *            OP_CALL(verb :UTF8, arity :WholeNum)</pre>
     * If {@link #OP_CALL} exits successfully, then [result] (the value
     * returned by the call) is pushed.
     * <pre>
     * forValue: forValue(recip) forValue(args[0]) ... forValue(args[n-1])
     *           OP_CALL(verb :UTF8, arity :WholeNum)
     *           OP_BRANCH</pre>
     * {@link #OP_BRANCH} converts a boolean result into a conditional branch.
     */
    public Object visitCallExpr(ENode optOriginal,
                                EExpr recip,
                                String verb,
                                EExpr[] args) {
        branchify();
        myEmitter.emitCall(verb, args.length, "FOR_FX_ONLY" == myKind);
        myVisitors.forValue.run(args);
        myVisitors.forValue.run(recip);
        return null;
    }

    /**
     * Evaluates rValue, matches it against patt, and evaluates to the value of
     * rValue.
     * <pre>
     * forFxOnly: forValue(rValue) forFxOnly(patt)</pre>
     * The value pushed by rValue is popped by patt, which either continues
     * executing or reports a problem.
     * <pre>
     * forValue: forValue(rValue) OP_DUP forFxOnly(patt)</pre>
     * {@link #OP_DUP} duplicates the top of stack, so that rValue's value is
     * left pushed if patt exits normally.
     * <pre>
     * forControl: forValue(rValue) OP_DUP forFxOnly(patt)
     *             OP_BRANCH</pre>
     * {@link #OP_BRANCH} converts a boolean result into a conditional branch.
     */
    public Object visitDefineExpr(ENode optOriginal,
                                  Pattern patt,
                                  EExpr optEjectorExpr,
                                  EExpr rValue) {
        if (null != optEjectorExpr) {
            T.fail("XXX Not yet implemented");
        }
        branchify();
        myVisitors.forFxOnly.run(patt);
        if ("FOR_FX_ONLY" != myKind) {
            myEmitter.emitDup();
        }
        myVisitors.forValue.run(rValue);
        return null;
    }

    /**
     * Evaluates <tt>test</tt> to a boolean, and then evaluates to the outcome
     * of either <tt>then</tt> or <tt>els</tt>, depending.
     * <pre>
     * all: OP_EJECTOR_ONLY(elsLabel)
     *          forControl(test)
     *      OP_END_HANDLER this(then) OP_JUMP(doneLabel)
     *      elsLabel: this(els)
     *      doneLabel:</pre>
     * {@link #OP_EJECTOR_ONLY} pushes [ejector] onto the operand stack and
     * pushes its handler onto the handler stack. This ejector, if invoked
     * before it's disabled, will truncate the stacks to their height at the
     * time the OP_EJECTOR_ONLY was executed (thereby popping itself and its
     * handler), disables itself, and transfer control to the elsLabel. Note
     * that the popping of the handler stack may run <tt>finally</tt>-clauses,
     * which may themselves exit abruptly, in which case control may never
     * reach elsLabel.
     * <p/>
     * <tt>test</tt> is evaluated forControl, so it pops the [ejector] pushed
     * by OP_EJECTOR_ONLY. If the test is true, then it falls through to the
     * OP_END_HANDLER. If the test is false, then it invokes the optEjector.
     * Otherwise, it reports a problem to the top handler on the handler
     * stack.
     * <p/>
     * {@link #OP_END_HANDLER} drops the top handler on the handler stack,
     * which must be the handler pushed by OP_EJECTOR_ONLY. This handler, when
     * dropped, disables the ejector pushed by OP_EJECTOR_ONLY.
     * <p/>
     * The then-clause is then evaluated so that its outcome is the outcome of
     * the <tt>if</tt> expression as a whole.
     * <p/>
     * {@link #OP_JUMP} just jumps to doneLabel, in which case the
     * if-expression exits normally.
     * <p/>
     * If the test was false, then the ejector was invoked, transfering control
     * to the els-clause. The else-clause is then evaluated so that its outcome
     * is the outcome of the <tt>if</tt> expression as a whole.
     */
    public Object visitIfExpr(ENode optOriginal,
                              EExpr test,
                              EExpr then,
                              EExpr els) {
        int doneLabel = myEmitter.getLabel();
        run(els);
        int elsLabel = myEmitter.getLabel();
        myEmitter.emitJump(doneLabel);
        run(then);
        myEmitter.emitEndHandler();
        myVisitors.forControl.run(test);
        myEmitter.emitEjectorOnly(elsLabel);
        return null;
    }

    /**
     * Reify a dynamic extent continuation, sort-of.
     * <pre>
     * forFxOnly: OP_EJECTOR_ONLY(doneLabel)
     *                forFxOnly(hatchPatt) forFxOnly(body)
     *            OP_END_HANDLER
     *            doneLabel:</pre>
     * The {@link #OP_EJECTOR_ONLY} pushes an ejector which is popped (and
     * typically bound by) hatchPatt. If this ejector is invoked, stacks are
     * truncated back to what they were as when the OP_EJECTOR_ONLY was
     * executed, the ejector is disabled, and control is transfered to
     * doneLabel:. In the forFxOnly case, the ejector is useful for early exit
     * but nothing more.
     * <pre>
     * forValue: OP_EJECTOR(doneLabel)
     *               forFxOnly(hatchPatt) forValue(body)
     *           OP_END_HANDLER
     *           doneLabel:</pre>
     * {@link #OP_EJECTOR} is like OP_EJECTOR_ONLY, but the ejector it creates,
     * when its run/0 or run/1 methods are called, also pushes its argument.
     * (If run/0 is used, a null is pushed.)
     * <pre>
     * forControl: OP_EJECTOR(elsLabel)
     *                 forFxOnly(hatchPatt) forControl(body)
     *             OP_END_HANDLER OP_JUMP(doneLabel)
     *             elsLabel: OP_BRANCH
     *             doneLabel:</pre>
     * Since this case is evaluated forControl, we enter with an optEjector
     * already on the stack -- to be invoked to indicate that the expression as
     * a whole evaluated to false. Let's call this <i>optEjector1</i>. The
     * OP_EJECTOR instruction pushes another one, to be used by the escape
     * expression to exit early with some value. Let's call this
     * <i>ejector2</i>. hatchPatt pops (and typically binds) ejector2. If body
     * evaluates to true, then, since it's evaluated for control, pops
     * optEjector1, falls through to OP_END_HANDLER and then jumps to
     * doneLabel. If body evaluates to false, then it exits according to
     * optEjector1.
     * <p/>
     * If ejector is invoked before it's disabled, it truncates the stacks back
     * to [optEjector1],[], pushes [argument] leaving [optEjector,argument],[],
     * disables itself, and jumps to elsLabel.
     * <p/>
     * {@link #OP_BRANCH} pops [optEjector,argument] and converts the truth
     * value of argument into control flow.
     */
    public Object visitEscapeExpr(ENode optOriginal,
                                  Pattern hatch,
                                  EExpr body,
                                  Pattern optArgPattern,
                                  EExpr optCatcher) {
        if (null != optArgPattern) {
            T.fail("XXX Doesn't yet deal with the argPattern/catcher clause");
        }
        if ("FOR_FX_ONLY" == myKind || "FOR_VALUE" == myKind) {
            int doneLabel = myEmitter.getLabel();
            myEmitter.emitEndHandler();
            run(body);
            myVisitors.forFxOnly.run(hatch);
            myEmitter.emitEjectorOnly(doneLabel);
        } else if ("FOR_CONTROL" == myKind) {
            int doneLabel = myEmitter.getLabel();
            myEmitter.emitBranch();
            int elsLabel = myEmitter.getLabel();
            myEmitter.emitJump(doneLabel);
            myEmitter.emitEndHandler();
            run(body);
            myVisitors.forFxOnly.run(hatch);
            myEmitter.emitEjector(elsLabel);
        }
        return null;
    }

    /**
     * A try/catch expression.
     * <pre>
     * all: OP_TRY(catchLabel)
     *          this(attempt)
     *      OP_END_HANDLER OP_JUMP(doneLabel)
     *      catchLabel: forFxOnly(patt) this(catcher)
     *      doneLabel: </pre>
     */
    public Object visitCatchExpr(ENode optOriginal,
                                 EExpr attempt,
                                 Pattern patt,
                                 EExpr catcher) {
        T.fail("XXX not yet implemented");
        return null;
    }

    /**
     * A try/finally expression
     * <pre>
     * all: OP_UNWIND(finallyLabel)
     *          this(attempt)
     *      OP_END_HANDLER XXX punt</pre>
     */
    public Object visitFinallyExpr(ENode optOriginal,
                                   EExpr attempt,
                                   EExpr unwinder) {
        T.fail("XXX not yet implemented");
        return null;
    }

    /**
     * A literal expression.
     * <pre>
     * forFxOnly: # nothing</pre>
     * <pre>
     * forValue: One of<ul>
     * <li>OP_WHOLE_NUM(num :WholeNum)
     * <li>OP_NEG_INT(num :WholeNum) # encodes -num
     * <li>OP_FLOAT64(float64 :Float64)
     * <li>OP_CHAR(highbyte :Byte, lowbyte :Byte)
     * <li>OP_STRING(str :UTF8)</ul></pre>
     * <pre>
     * forControl: forValue OP_BRANCH</pre>
     * <p/>
     * XXX Treats Twine like String for now.
     */
    public Object visitLiteralExpr(ENode optOriginal, Object value) {
        if ("FOR_FX_ONLY" == myKind) {
            return null;
        }
        branchify();
        if (value instanceof Number) {
            if (value instanceof BigInteger) {
                myEmitter.emitInteger((BigInteger)value);
            } else {
                myEmitter.emitFloat64(((Double)value).doubleValue());
            }
        } else if (value instanceof Character) {
            myEmitter.emitChar(((Character)value).charValue());
        } else if (value instanceof String) {
            myEmitter.emitString((String)value);
        } else {
            T.require(value instanceof Twine, "Twine value expected: ", value);
            myEmitter.emitString(value.toString()); // XXX emitTwine(value)
        }
        return null;
    }

    /**
     * Reifies the current environment.
     * <pre>
     * forFxOnly: # nothing</pre>
     * <pre>
     * forValue: OP_SCOPE</pre>
     * <pre>
     * forControl: OP_SCOPE OP_BRANCH</pre>
     * Since a scope doesn't coerce to a boolean, this will always fail, but we
     * generate it this way so the complaint might be more informative.
     */
    public Object visitMetaStateExpr(ENode optOriginal) {
        if ("FOR_FX_ONLY" == myKind) {
            return null;
        }
        branchify();
        myEmitter.emitScope();
        return null;
    }

    /**
     * Reifies the current syntactic environment.
     */
    public Object visitMetaContextExpr(ENode optOriginal) {
        T.fail("XXX Not yet implemented");
        return null; // make compiler happy
    }

    /**
     * Accesses the value of a variable.
     * <pre>
     * (OP_NOUN+addrMode)(index)</pre>
     */
    public Object visitNounExpr(ENode optOriginal, String varName) {
        //Must change visitor interface for this, or something
        T.fail("XXX not yet implemented");
        return null;
    }

    /**
     * Obtain the slot holding the value of a variable.
     * <pre>
     * (OP_SLOT+addrMode)(index)</pre>
     */
    public Object visitSlotExpr(ENode optOriginal, AtomicExpr noun) {
        T.fail("XXX not yet implemented");
        return null;
    }

    /**
     * Sets a variable's value to the value of an rValue expression.
     * <pre>
     * forFxOnly: forValue(rValue)
     *            (OP_ASSIGN+addrMode)(index)</pre>
     * <pre>
     * forValue: forValue(rValue) OP_DUP
     *           (OP_ASSIGN+addrMode)(index)</pre>
     * <pre>
     * forControl: forValue(rValue) OP_DUP
     *             (OP_ASSIGN+addrMode)(index)
     *             OP_BRANCH</pre>
     */
    public Object visitAssignExpr(ENode optOriginal,
                                  AtomicExpr noun,
                                  EExpr rValue) {
        T.fail("XXX not yet implemented");
//        rValue.welcome(this);
//        myEnc.writeByte(OP_ASSIGN);
//        lValue.welcome(this);
        return null;
    }

    /**
     * OP_OBJECT(fqName :UTF8, numAuditors :WholeNum, auditors, eScript)
     */
    public Object visitObjectExpr(ENode optOriginal,
                                  String docComment,
                                  GuardedPattern oName,
                                  EExpr[] auditors,
                                  EScript eScript) {
        T.fail("XXX not yet implemented");
//        myEnc.writeByte(OP_OBJECT);
//        myEnc.writeUTF(qualifiedName);
//        myEnc.writeWholeNum(auditors.length);
//        writeNodes(auditors);
//        eScript.welcome(this);
        return null;
    }

    /**
     * Not Applicable
     */
    public Object visitQuasiLiteralExpr(ENode optOriginal, int index) {
        T.fail("May not evaluate non-literal tree");
        return null;
    }

    /**
     * Not Applicable
     */
    public Object visitQuasiPatternExpr(ENode optOriginal, int index) {
        T.fail("May not evaluate non-literal tree");
        return null;
    }

    ////////////////// patterns ///////////////////

    /**
     * Define final variable whose value is the coercion of the specimen.
     * <pre>
     * forFxOnly: forValue(guardExpr) OP_SWAP OP_NULL
     *            OP_CALL("coerce", 2) (OP_BIND+addrMode)(index)</pre>
     * <pre>
     * forControl: forValue(guardExpr) OP_SWAP OP_ROT
     *             OP_CALL("coerce", 2) (OP_BIND+addrMode)(index)</pre>
     * <pre>
     * forValue: forValue(guardExpr) OP_SWAP OP_EJECTOR_ONLY(elsLabel)
     *               OP_CALL("coerce", 2) (OP_BIND+addrMode)(index)
     *           OP_END_HANDLER OP_TRUE OP_JUMP(doneLabel)
     *           elsLabel: OP_FALSE
     *           doneLabel:</pre>
     * When guardExpr is the default (":any"), then this simplifies to
     * <pre>
     * forFxOnly: (OP_BIND+addrMode)(index)</pre>
     * <pre>
     * forControl: (OP_BIND+addrMode)(index) OP_POP</pre>
     * <pre>
     * forValue: (OP_BIND+addrMode)(index) OP_TRUE</pre>
     * Since the <tt>any</tt> guard can't fail to match, the pattern cannot
     * generate a <tt>false</tt>.
     */
    public Object visitFinalPattern(ENode optOriginal,
                                    AtomicExpr nounExpr,
                                    EExpr optGuardExpr) {
        T.fail("XXX not yet implemented");
        return null;
    }

    /**
     *
     */
    public Object visitVarPattern(ENode optOriginal,
                                  AtomicExpr nounExpr,
                                  EExpr optGuardExpr) {
        T.fail("XXX not yet implemented");
        return null;
    }

    /**
     *
     */
    public Object visitSlotPattern(ENode optOriginal,
                                   AtomicExpr nounExpr,
                                   EExpr optGuardExpr) {
        T.fail("XXX not yet implemented");
        return null;
    }

    /**
     * Always succeeds, matches specimen, binds nothing.
     * <pre>
     * forFxOnly: OP_POP</pre>
     * <pre>
     * forValue: OP_POP OP_TRUE</pre>
     * <pre>
     * forControl: OP_POP OP_POP</pre>
     */
    public Object visitIgnorePattern(ENode optOriginal, EExpr optGuardExpr) {
        // XXX emit guard
        if ("FOR_VALUE" == myKind) {
            myEmitter.emitTrue();
        } else if ("FOR_CONTROL" == myKind) {
            myEmitter.emitPop();
        }
        myEmitter.emitPop();
        return null;
    }

    public Object visitViaPattern(ENode optOriginal,
                                  EExpr viaExpr,
                                  Pattern subPattern) {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    /**
     * PATT_LIST(numSubs :WholeNum) subs...
     * <p/>
     * PATT_LIST(n) pops [optEjector, specimen], coerces the specimen to an
     * EList, and checks that the size is n. If it doesn't coerce of if the
     * size doesn't match, then it escapes according to optEjector. If all this
     * succeeds, then it pushes
     * <pre>[optEjector, specimen[0], ..., optEjector, specimen[n-1]]</pre>
     * onto the stack for the subs to consume.
     */
    public Object visitListPattern(ENode optOriginal, Pattern[] subs) {
        T.fail("XXX not yet implemented");
        return null;
    }

    /**
     * Not Applicable
     */
    public Object visitQuasiLiteralPatt(ENode optOriginal, int index) {
        T.fail("May not evaluate non-literal tree");
        return null;
    }

    /**
     * Not Applicable
     */
    public Object visitQuasiPatternPatt(ENode optOriginal, int index) {
        T.fail("May not evaluate non-literal tree");
        return null;
    }

    ////////////////////// other ///////////////////

    /**
     *
     */
    public Object visitEScript(ENode optOriginal,
                               EMethod[] optMethods,
                               EMatcher[] matchers) {
        T.fail("XXX not yet implemented");
        return null;
    }

    /**
     *
     */
    public Object visitEMethod(ENode optOriginal,
                               String docComment,
                               String verb,
                               Pattern[] patterns,
                               EExpr optResultGuard,
                               EExpr body) {
        T.fail("XXX not yet implemented");
        return null;
    }

    /**
     *
     */
    public Object visitEMatcher(ENode optOriginal,
                                Pattern pattern,
                                EExpr body) {
        T.fail("XXX not yet implemented");
        return null;
    }
}
