def ObjectExpr := <type:org.erights.e.elang.evm.ObjectExpr>
def FinalPattern := <type:org.erights.e.elang.evm.FinalPattern>
def VarPattern := <type:org.erights.e.elang.evm.VarPattern>
def OuterNounExpr := <type:org.erights.e.elang.evm.OuterNounExpr>
def LocalFinalNounExpr := <type:org.erights.e.elang.evm.LocalFinalNounExpr>
def LiteralExpr := <type:org.erights.e.elang.evm.LiteralExpr>
def CallExpr := <type:org.erights.e.elang.evm.CallExpr>
def SeqExpr := <type:org.erights.e.elang.evm.SeqExpr>
def AssignExpr := <type:org.erights.e.elang.evm.AssignExpr>
def DefineExpr := <type:org.erights.e.elang.evm.DefineExpr>
def IfExpr := <type:org.erights.e.elang.evm.IfExpr>

def <asm> := <unsafe:org.objectweb.asm.*>
def makeLabel := <asm:makeLabel>
def Opcodes := <asm:makeOpcodes>
def <op> {
	to get(code) {
		return E.call(Opcodes, `get$code`, [])
	}
}

def max(a, b) {
	if (a > b) {
		return a
	} else {
		return b
	}
}

var nInner := 0
def innerClasses := [].diverge()

def queueCompileInnerClass(objExpr) {
	def name := `e/generated/inner${nInner}`
	nInner += 1
	innerClasses.push([name, objExpr])
	return name
}

def eval

def evalGuard(mw, guard, valueMaker) {
	if (guard == null) {
		return valueMaker()
	}

	#E.call(foo, "coerce", specimen, optEjector);
	def guardStack := eval(mw, guard)
	mw.visitLdcInsn("coerce");
	def maxStack := 2 + valueMaker() + 1
	mw.visitInsn(<op:ACONST_NULL>)	# XXX
	mw.visitMethodInsn(<op:INVOKESTATIC>, "org/erights/e/elib/prim/E", "call",
		"(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")
	#mw.visitMethodInsn(<op:INVOKEVIRTUAL>, "org/erights/e/elib/slot/Guard", "coerce",
	#"(Ljava/lang/Object;Lorg/erights;Lorg/erights/e/elib/util/OneArgFunc;)Ljava/lang/Object;")
	return maxStack
}

# => slot, value
def evalMakeSlot(mw, pattern, valueMaker) {
	def guard := pattern.getOptGuardExpr()

	def valueStack := evalGuard(mw, guard, valueMaker)

	# Package top-most value in a slot
	def slotType := switch (pattern) {
		match finalPattern :FinalPattern {
			"org/erights/e/elib/slot/FinalSlot"
		}
		match varPattern :VarPattern {
			if (guard == null) {
				"org/erights/e/elib/slot/SimpleSlot"
			} else {
				"org/erights/e/elib/slot/SettableSlot"
			}
		}
	}

	mw.visitTypeInsn(<op:NEW>, slotType);
	# stack: value, slot
	mw.visitInsn(<op:SWAP>)
	mw.visitInsn(<op:DUP2>)
	# stack: slot, value, slot, value

	mw.visitMethodInsn(<op:INVOKESPECIAL>, slotType, "<init>",
		"(Ljava/lang/Object;)V");

	# stack: slot, value
	return max(valueStack, 4)
}

def eLocalToJaveLocal(i) {
	return i + 2
}

def evalEInt(mw, value :int) {
	# XXX: values longer than Long?
	mw.visitLdcInsn(value :<type:long>)
	mw.visitMethodInsn(<op:INVOKESTATIC>, "java/math/BigInteger", "valueOf",
				"(J)Ljava/math/BigInteger;")
	return 1
}

def evalIf(mw, ifExpr) {
	def testStack := eval(mw, ifExpr.getTest())
	#E.asBoolean(value)

	mw.visitMethodInsn(<op:INVOKESTATIC>, "org/erights/e/elib/prim/E", "asBoolean",
		"(Ljava/lang/Object;)Z")

	def elseBlock := makeLabel()
	def done := makeLabel()

	mw.visitJumpInsn(<op:IFEQ>, elseBlock)

	def thenStack := eval(mw, ifExpr.getThen())
	mw.visitJumpInsn(<op:GOTO>, done)

	mw.visitLabel(elseBlock)
	def elseStack := eval(mw, ifExpr.getElse())

	mw.visitLabel(done)

	return max(max(testStack, thenStack), elseStack)
}

def evalDefInteral(mw, pattern, valueMaker) {
	def guard := pattern.getOptGuardExpr()
	def noun := pattern.getNoun()
	traceln(`def $noun (${noun.__getAllegedType()})`)
	switch (noun) {
		match lNoun :LocalFinalNounExpr {
			pattern :FinalPattern
			def i := lNoun.getIndex()
			def valueStack := evalGuard(mw, guard, valueMaker)
			mw.visitInsn(<op:DUP>)
			mw.visitIntInsn(<op:ASTORE>, eLocalToJaveLocal(i))
			return valueStack
		}
		match oNoun :OuterNounExpr {
			def i := oNoun.getIndex()
			# push(this.context)
			mw.visitVarInsn(<op:ALOAD>, 0);
			mw.visitFieldInsn(<op:GETFIELD>, "Test", "context", "Lorg/erights/e/elang/scope/EvalContext;")
			# push(new Slot(value))
			def stackSlot := evalMakeSlot(mw, pattern, valueMaker)
			# stack: context, slot, value
			mw.visitInsn(<op:DUP_X2>)
			mw.visitInsn(<op:POP>)
			# stack: value, context, slot
			# push(i)
			mw.visitLdcInsn(i)
			mw.visitInsn(<op:SWAP>)
			# stack: value, context, i, slot
			# context.initOuter(newSlow)
			mw.visitMethodInsn(<op:INVOKEVIRTUAL>, "org/erights/e/elang/scope/EvalContext", "initOuter",
					"(ILorg/erights/e/elib/slot/Slot;)V")
			# stack: value
			return 70
		}
	}
}

def evalDef(mw, pattern, value) {
	return evalDefInteral(mw, pattern, fn { eval(mw, value) })
}

def evalObjectDef(mw, objExpr) {
	return evalDefInteral(mw, objExpr.getOName(), fn {
		def newObjClass := queueCompileInnerClass(objExpr)

		def oName := objExpr.getOName()

		mw.visitTypeInsn(<op:NEW>, newObjClass)
		mw.visitInsn(<op:DUP>)
		mw.visitVarInsn(<op:ALOAD>, 0);
		mw.visitFieldInsn(<op:GETFIELD>, "Test", "context", "Lorg/erights/e/elang/scope/EvalContext;")
		mw.visitMethodInsn(<op:INVOKESPECIAL>, newObjClass, "<init>", "(Lorg/erights/e/elang/scope/EvalContext;)V");

		2
	})
}

def evalAssign(mw, noun, value) {
	switch (noun) {
		match lNoun :LocalFinalNounExpr {
			def i := lNoun.getIndex()
			#def valueStack := evalGuard(mw, guard, value)
			def valueStack := eval(mw, value)
			mw.visitInsn(<op:DUP>)
			mw.visitIntInsn(<op:ASTORE>, eLocalToJaveLocal(i))
			return valueStack
		}
		match oNoun :OuterNounExpr {
			def i := oNoun.getIndex()
			# push(this.context)
			mw.visitVarInsn(<op:ALOAD>, 0);
			mw.visitFieldInsn(<op:GETFIELD>, "Test", "context", "Lorg/erights/e/elang/scope/EvalContext;")
			# context.outer(i)
			mw.visitLdcInsn(i)
			# stack: context, i
			mw.visitMethodInsn(<op:INVOKEVIRTUAL>, "org/erights/e/elang/scope/EvalContext", "outer",
					"(I)Lorg/erights/e/elib/slot/Slot;")
			def valueStack := eval(mw, value)
			# stack: slot, value
			mw.visitInsn(<op:DUP_X1>)
			# stack: value, slot, value
			mw.visitMethodInsn(<op:INVOKEINTERFACE>, "org/erights/e/elib/slot/Slot", "put",
					"(Ljava/lang/Object;)V")

			return max(valueStack + 1, 3)
		}
	}
}

bind eval(mw, item) {
	println(`eval: $item : ${item.__getAllegedType()}`)
	switch (item) {
		match x :OuterNounExpr {
			def i := x.getIndex()
			mw.visitVarInsn(<op:ALOAD>, 0);
			mw.visitFieldInsn(<op:GETFIELD>, "Test", "context", "Lorg/erights/e/elang/scope/EvalContext;")
			mw.visitLdcInsn(i)
			mw.visitMethodInsn(<op:INVOKEVIRTUAL>, "org/erights/e/elang/scope/EvalContext", "outer",
					"(I)Lorg/erights/e/elib/slot/Slot;")
			mw.visitMethodInsn(<op:INVOKEINTERFACE>, "org/erights/e/elib/slot/Slot", "get",
					"()Ljava/lang/Object;")
			println(`(outer)`)
			return 2
		}
		match x :LocalFinalNounExpr {
			def i := x.getIndex()
			mw.visitIntInsn(<op:ALOAD>, eLocalToJaveLocal(i))
			return 1
		}
		match x :LiteralExpr {
			switch (x.value()) {
				match i :int {
					return evalEInt(mw, i)
				}
				match s :String {
					mw.visitLdcInsn(s)
					return 1
				}
			}
		}
		match x :ObjectExpr {
			return evalObjectDef(mw, x)
		}
		match x :IfExpr {
			return evalIf(mw, x)
		}
		match x :AssignExpr {
			return evalAssign(mw, x.getNoun(), x.getRValue())
		}
		match x :SeqExpr {
			var maxStack := 0
			def last := x.subs().size() - 1
			for i => expr in x.subs() {
				def exprStack := eval(mw, expr)
				if (exprStack > maxStack) {
					maxStack := exprStack
				}
				if (i != last) {
					mw.visitInsn(<op:POP>)
				}
			}
			return maxStack
		}
		match d : DefineExpr {
			require(d.getOptEjectorExpr() == null)
			def pattern := d.getPattern()
			def value := d.getRValue()
			traceln(`def $pattern (${pattern.__getAllegedType()})`)

			return evalDef(mw, pattern, value)
		}
		match call :CallExpr {
			var maxStack := 0
			def recipient := call.recipient()
			maxStack := eval(mw, recipient) + 2
			mw.visitLdcInsn(call.verb())

			mw.visitIntInsn(<op:BIPUSH>, call.args().size())
			mw.visitTypeInsn(<op:ANEWARRAY>, "java/lang/Object")
			# stack: recipient, verb, array
			for i => arg in call.args() {
				mw.visitInsn(<op:DUP>)
				mw.visitLdcInsn(i);
				# stack: recipient, verb, array, array, i
				def stackForArg := eval(mw, arg) + 5
				mw.visitInsn(<op:AASTORE>)
				if (stackForArg > maxStack) {
					maxStack := stackForArg
				}
			}
			mw.visitMethodInsn(<op:INVOKESTATIC>, "org/erights/e/elib/prim/E", "callAll",
				"(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;")
			return maxStack
		}
	}
}


def compileOne(className, transformed, scopeLayout, nLocals) {
	def cw := <asm:makeClassWriter>(0)
	cw.visit(<op:V1_1>, <op:ACC_PUBLIC>, className, null, "java/lang/Object", null)

	cw.visitField(<op:ACC_PRIVATE>, "context", "Lorg/erights/e/elang/scope/EvalContext;", null, null)

	# Constructor
        def cons := cw.visitMethod(<op:ACC_PUBLIC>, "<init>",
                "(Lorg/erights/e/elang/scope/EvalContext;)V",
                null,
                null)
	# pushes the 'this' variable
        cons.visitVarInsn(<op:ALOAD>, 0);
	# invokes the super class constructor
        cons.visitMethodInsn(<op:INVOKESPECIAL>, "java/lang/Object", "<init>", "()V");

        cons.visitVarInsn(<op:ALOAD>, 0);
        cons.visitVarInsn(<op:ALOAD>, 1);
	cons.visitFieldInsn(<op:PUTFIELD>, className, "context", "Lorg/erights/e/elang/scope/EvalContext;")

        cons.visitInsn(<op:RETURN>);
        cons.visitMaxs(3, 2);
        cons.visitEnd();

	def mw := cw.visitMethod(<op:ACC_PUBLIC>,
				"run", "()Ljava/lang/Object;",
				null, null)

	def stackSize := eval(mw, transformed)

	println(`$className : ss = $stackSize`)

	mw.visitMaxs(stackSize + 1, nLocals + 2)

	mw.visitInsn(<op:ARETURN>)
	mw.visitEnd()

	def code := cw.toByteArray()
	def os := <file>[`$className.class`].setBytes(code)
	return code
}

def compile(transformed, scopeLayout, nLocals) {
	def root := compileOne("Test", transformed, scopeLayout, nLocals)
	while (innerClasses.size() > 0) {
		def [name, objExpr] := innerClasses.pop()
		def script := objExpr.getScript()
		compileOne(name, e`1`, script.getScopeLayout(), 0)
	}
	return root
}

def [source] := interp.getArgs()
def eCode := <elang:syntax.makeEParser>(<file>[source].getTwine())
def compiled := eCode.compile(safeScope, compile)
println(`c: $compiled`)
compiled()
