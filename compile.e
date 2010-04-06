def OuterNounExpr := <type:org.erights.e.elang.evm.OuterNounExpr>
def LiteralExpr := <type:org.erights.e.elang.evm.LiteralExpr>
def CallExpr := <type:org.erights.e.elang.evm.CallExpr>
def SeqExpr := <type:org.erights.e.elang.evm.SeqExpr>

def <asm> := <unsafe:org.objectweb.asm.*>
def Opcodes := <asm:makeOpcodes>
def <op> {
	to get(code) {
		return E.call(Opcodes, `get$code`, [])
	}
}

def compile(transformed, scopeLayout, nLocals) {
	def cw := <asm:makeClassWriter>(0)
	cw.visit(<op:V1_1>, <op:ACC_PUBLIC>, "Test", null, "java/lang/Object", null)

	#cw.visitField(<op:ACC_PRIVATE>, "scope", "Lorg/erights/e/elang/scope/Scope;", null, null)
	cw.visitField(<op:ACC_PRIVATE>,
			"context", "Lorg/erights/e/elang/scope/EvalContext;", null, null)

	# Constructor
        def cons := cw.visitMethod(<op:ACC_PUBLIC>, "<init>",
                "(Lorg/erights/e/elang/scope/Scope;)V",
                null,
                null)
	# pushes the 'this' variable
        cons.visitVarInsn(<op:ALOAD>, 0);
	# invokes the super class constructor
        cons.visitMethodInsn(<op:INVOKESPECIAL>, "java/lang/Object", "<init>", "()V");

        cons.visitVarInsn(<op:ALOAD>, 0);
        cons.visitVarInsn(<op:ALOAD>, 1);
	cons.visitLdcInsn(0)
	cons.visitMethodInsn(<op:INVOKEVIRTUAL>, "org/erights/e/elang/scope/Scope", "newContext",
			"(I)Lorg/erights/e/elang/scope/EvalContext;")
	cons.visitFieldInsn(<op:PUTFIELD>, "Test", "context", "Lorg/erights/e/elang/scope/EvalContext;")

        cons.visitInsn(<op:RETURN>);
        cons.visitMaxs(3, 2);
        cons.visitEnd();

	def mw := cw.visitMethod(<op:ACC_PUBLIC>,
				"run", "()V",
				null, null)

	def eval(item) {
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
			match x :LiteralExpr {
				mw.visitLdcInsn(x.value())
				return 1
			}
			match x :SeqExpr {
				var maxStack := 0
				def last := x.subs().size() - 1
				for i => expr in x.subs() {
					def exprStack := eval(expr)
					if (exprStack > maxStack) {
						maxStack := exprStack
					}
					if (i != last) {
						mw.visitInsn(<op:POP>)
					}
				}
				return maxStack
			}
			match call :CallExpr {
				var maxStack := 0
				def recipient := call.recipient()
				maxStack := eval(recipient) + 2
				mw.visitLdcInsn(call.verb())

				mw.visitIntInsn(<op:BIPUSH>, call.args().size())
				mw.visitTypeInsn(<op:ANEWARRAY>, "java/lang/Object")
				# stack: recipient, verb, array
				for i => arg in call.args() {
					mw.visitInsn(<op:DUP>)
					mw.visitLdcInsn(i);
					# stack: recipient, verb, array, array, i
					def stackForArg := eval(arg) + 5
					mw.visitInsn(<op:AASTORE>)
					if (stackForArg > maxStack) {
						maxStack := stackForArg
					}
				}
				mw.visitMethodInsn(<op:INVOKESTATIC>, "org/erights/e/elib/prim/E", "callAll",
					"(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;")
				return maxStack
			}
			match _ {
				mw.visitLdcInsn(`$item`)
				println(`(string)`)
				return 1
			}
		}
	}

	def stackSize := eval(transformed)

	mw.visitMaxs(stackSize, nLocals + 1)

	mw.visitInsn(<op:RETURN>)
	mw.visitEnd()

	def code := cw.toByteArray()
	def os := <file:Test.class>.setBytes(code)
	return code
}

def [source] := interp.getArgs()
def eCode := <elang:syntax.makeEParser>(<file>[source].getTwine())
def compiled := eCode.compile(safeScope, compile)
println(`c: $compiled`)
compiled()
