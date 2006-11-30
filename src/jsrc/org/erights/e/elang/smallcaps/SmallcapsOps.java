package org.erights.e.elang.smallcaps;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Each instruction of the Smallcaps instruction set begins with a byte holding
 * one of the these opcodes.
 * <p/>
 * See {@link SmallcapsEncoderVisitor} for an explanation of how these are used
 * to encode the various Transformed-E expressions and patterns.
 *
 * @author Mark S. Miller
 * @author Darius Bacon
 */
public interface SmallcapsOps {

    /////////////////////// Opcodes ////////////////////////

    /**
     * [x],[] =&gt; OP_DUP =&gt; [x, x],[]
     */
    final int OP_DUP = 1;
    /**
     * [x],[] =&gt; OP_POP =&gt; [],[]
     */
    final int OP_POP = 2;
    /**
     * [x, y],[] =&gt; OP_SWAP =&gt; [y, x],[]
     */
    final int OP_SWAP = 3;
    /**
     * [x, y, z],[] =&gt; OP_ROT =&gt; [y, z, x],[]
     */
    final int OP_ROT = 4;

    /**
     * [x],[] =&gt; OP_RETURN
     */
    final int OP_RETURN = 5;

    /**
     * [],[] =&gt; OP_JUMP(label) =&gt; [],[]
     * <p/>
     * Stacks at addr must be the same as here.
     * <p/>
     * A label is represented by an integer offset from the position after this
     * instruction to the position of the target instruction.
     */
    final int OP_JUMP = 8;

    /**
     * [optEjector, flag],[] =&gt; OP_BRANCH =&gt; [],[]
     * <p/>
     * Coerces flag to a boolean. If it's <ul> <li>true, then we fall through
     * -- continue with the next instruction, pushing nothing. <li>false, then
     * exit abruptly according to optEjector. <li>otherwise, the top handler on
     * the stack is invoked to deal with the coercion failure. </ul>
     */
    final int OP_BRANCH = 9;

    /**
     * [recip, args...],[] =&gt; OP_CALL_ONLY(verb, arity) =&gt; [],[]
     */
    final int OP_CALL_ONLY = 10;
    /**
     * [recip, args...],[] =&gt; OP_CALL(verb, arity) =&gt; [result],[]
     */
    final int OP_CALL = 11;

    /**
     * [],[] =&gt; OP_EJECTOR_ONLY(label) =&gt; [ejector],[handler]<br>
     * [...],[...] =&gt; ejector(_) =&gt; [],[]
     * <p/>
     * Generated at least by the if and escape expressions.
     * <p/>
     * Creates/pushes an ejector on the operand stack, and creates/pushes its
     * corresponding handler onto the handler stack. The ejector, when called,
     * if it's still enabled, will ignore its argument, pop both stacks to the
     * height when the ejector was created/pushed, disable itself, and branch
     * to the label.
     * <p/>
     * This is a <i>handler-introducing op</i>, so it must be balanced by an
     * {@link #OP_END_HANDLER} which occurs at the same operand stack height,
     * and with the created/pushed handler at the top of the handler stack. The
     * label must be to code outside the handled region.
     */
    final int OP_EJECTOR_ONLY = 14;

    /**
     * [],[] =&gt; OP_EJECTOR(label) =&gt; [ejector],[handler]<br> [...],[...]
     * =&gt; ejector(result) =&gt; [result],[]
     * <p/>
     * Generated by the escape expression.
     * <p/>
     * Creates/pushes an ejector on the operand stack, and creates/pushes its
     * corresponding handler onto the handler stack. The ejector, when called,
     * if it's still enabled, will pop both stacks to the height when the
     * ejector was created/pushed, disable itself, push its argument, and
     * branch to the label.
     * <p/>
     * As with {@link #OP_EJECTOR_ONLY}, this is a handler-introducing op.
     */
    final int OP_EJECTOR = 15;

    /**
     * [],[] =&gt; OP_TRY(label) =&gt; [],[handler]<br> [...],[...] =&gt;
     * handler(arg) =&gt; [arg],[]<br> [...],[...] =&gt; handler.drop(_) =&gt;
     * [...],[...]
     * <p/>
     * Generated by the try/catch expression.
     * <p/>
     * Creates and pushes onto the handler stack a problem handler. This
     * handler, when dropped, does nothing. This handler, when invoked,
     * truncates the stacks to their height when the handler was
     * created/pushed, pushes its argument onto the operand stack, and jumps to
     * label.
     * <p/>
     * As with {@link #OP_EJECTOR_ONLY}, this is a handler-introducing op.
     */
    final int OP_TRY = 16;

    /**
     * [],[] =&gt; OP_UNWIND(label) =&gt; [],[handler]<br> [...],[...] =&gt;
     * handler(arg) =&gt; [rethrower(arg)],[]<br> [...],[...] =&gt;
     * handler.drop(pc) =&gt; [returner(pc)],[]
     * <p/>
     * Generated by the try/finally expression.
     * <p/>
     * Creates and pushes onto the handler stack a problem handler. This
     * handler, when invoked, truncates the stacks to their height when the
     * handler was created/pushed, pushes a <i>rethrower</i> of the arg on to
     * the operand stack, and jumps to label. A rethrower is an ejector which
     * will pop and rethrow its arg by invoking the top handler on the handler
     * stack.
     * <p/>
     * This handler, when dropped, pushes a <i>returner</i> for the current pc.
     * A returner is an ejector that jumps to its pc.
     * <p/>
     * E code can never get its hands on a rethrower or a returner. Unlike some
     * other ejectors, these are not reified in the language.
     * <p/>
     * As with {@link #OP_EJECTOR_ONLY}, this is a handler-introducing op.
     */
    final int OP_UNWIND = 17;

    /**
     * [],[handler] =&gt; OP_END_HANDLER =&gt; [],[]
     * <p/>
     * Drops the top handler on the handler stack. This is the closing bracket
     * that balances an openning {@link #OP_EJECTOR_ONLY handler-introducing
     * op}.
     */
    final int OP_END_HANDLER = 18;

    /**
     * [],[] =&gt; OP_WHOLE_NUM(wholeNum) =&gt; [wholeNum],[]
     */
    final int OP_WHOLE_NUM = 22;
    /**
     * [],[] =&gt; OP_NEG_INT(wholeNum) =&gt; [-wholeNum],[]
     */
    final int OP_NEG_INT = 23;
    /**
     * [],[] =&gt; OP_FLOAT64(float64) =&gt; [float64],[]
     */
    final int OP_FLOAT64 = 24;
    /**
     * [],[] =&gt; OP_CHAR(chr) =&gt; [chr],[]
     */
    final int OP_CHAR = 25;
    /**
     * [],[] =&gt; OP_STRING(str) =&gt; [str],[]
     */
    final int OP_STRING = 26;
    /**
     * [],[] =&gt; OP_TWINE(twine) =&gt; [twine],[]
     */
    final int OP_TWINE = 27; //XXX reserved

    /**
     * [],[] =&gt; OP_TRUE =&gt; [true],[]
     */
    final int OP_TRUE = 28;
    /**
     * [],[] =&gt; OP_FALSE =&gt; [false],[]
     */
    final int OP_FALSE = 29;
    /**
     * [],[] =&gt; OP_NULL =&gt; [null],[]
     */
    final int OP_NULL = 30;

    /**
     * [],[] =&gt; OP_SCOPE =&gt; [scope],[]
     */
    final int OP_SCOPE = 31;

    /**
     * [ivars..., auditors...],[] =&gt; OP_OBJECT(<i>seeBelow</i>) =&gt;
     * [object],[]
     * <p/>
     * XXX to be written
     */
    final int OP_OBJECT = 32;

    /**
     * <pre>
     * [optEjector, specimen],[] =&gt; OP_LIST(n)
     * =&gt; [optEjector, specimen[n-1], ... optEjector, specimen[0]],[]</pre>
     */
    final int OP_LIST_PATT = 33;

    /**
     * <pre>
     * [optEjector, specimen],[] =&gt; OP_CDR_PATT(n)
     * =&gt; [optEjector, specimen(n,specimen.size()),
     *     optEjector, specimen[n-1], ... optEjector, specimen[0]],[]</pre>
     */
    final int OP_CDR_PATT = 34;

    ////////////////////// Opcodes with addressing modes /////////////////


    /**
     * [],[] =&gt; (OP_NOUN+addrMode)(index) =&gt; [value],[]
     * <p/>
     * Pushes the value of the variable. The value of the variable is what
     * would be obtained by calling get() on the variable's slot.
     */
    final int OP_NOUN = 40;

    /**
     * [],[] =&gt; (OP_SLOT+addrMode)(index) =&gt; [slot],[]
     * <p/>
     * Pushes the slot holding the value of the variable.
     */
    final int OP_SLOT = 48;

    /**
     * [rValue],[] =&gt; (OP_ASSIGN+addrMode)(index) =&gt; [],[]
     * <p/>
     * Sets the value of the variable. To set the variable is to cause those
     * effects that would be caused by calling put(rValue) on the
     * variable's slot.
     */
    final int OP_ASSIGN = 56;

    /**
     * [rValue],[] =&gt; (OP_BIND+addrMode)(index) =&gt; [],[]
     * <p/>
     * Define the variable as a final variable whose permanent value is rValue.
     * Equivalently, bind the variable to a FinalSlot holding rValue.
     */
    final int OP_BIND = 64;

    /**
     * [rValue],[] =&gt; (OP_BIND_SLOT+addrMode)(index) =&gt; [],[]
     * <p/>
     * Define the variable as a non-final variable (a "<tt>var</tt>" variable)
     * whose slot is rValue.
     */
    final int OP_BIND_SLOT = 72;

    ///////////////////// Addressing modes ////////////////////////////

    /**
     * Accesses the index'th instance variable.
     */
    final int ADDR_FRAME = 0;
    /**
     * Accesses the value of the slot at the index'th instance variable.
     */
    final int ADDR_FRAME_SLOT = 1;
    /**
     * Accesses the index'th local variable.
     */
    final int ADDR_LOCAL = 2;
    /**
     * Accesses the value of the slot at the index'th local variable.
     */
    final int ADDR_LOCAL_SLOT = 3;
    /**
     * Accesses the index'th literal.
     */
    final int ADDR_LITERAL = 4;
    /**
     * Accesses the value of the slot at the index'th outer variable.
     */
    final int ADDR_OUTER_SLOT = 5;
}
