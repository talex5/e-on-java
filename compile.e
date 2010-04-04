def OuterNounExpr := <type:org.erights.e.elang.evm.OuterNounExpr>
def LiteralExpr := <type:org.erights.e.elang.evm.LiteralExpr>

def <asm> := <unsafe:org.objectweb.asm.*>
def Opcodes := <asm:makeOpcodes>
def <op> {
	to get(code) {
		return E.call(Opcodes, `get$code`, [])
	}
}

def cw := <asm:makeClassWriter>(0)
cw.visit(<op:V1_1>, <op:ACC_PUBLIC>, "Test", null, "java/lang/Object", null)

cw.visitField(<op:ACC_PUBLIC> | <op:ACC_STATIC>,
		"scope", "Lorg/erights/e/elang/scope/Scope;", null, null)
cw.visitField(<op:ACC_PRIVATE> | <op:ACC_STATIC>,
		"context", "Lorg/erights/e/elang/scope/EvalContext;", null, null)

def mw := cw.visitMethod(<op:ACC_PUBLIC> | <op:ACC_STATIC>,
			"main", "()V",
			null, null)

def [source] := interp.getArgs()
def eCode := <elang:syntax.makeEParser>(<file>[source].getTwine())
def [transformed, scopeLayout, nLocals] := eCode.getTransformed(safeScope)

mw.visitMaxs(5, nLocals + 1)

mw.visitFieldInsn(<op:GETSTATIC>, "Test", "scope", "Lorg/erights/e/elang/scope/Scope;")
mw.visitLdcInsn(0)
mw.visitMethodInsn(<op:INVOKEVIRTUAL>, "org/erights/e/elang/scope/Scope", "newContext",
		"(I)Lorg/erights/e/elang/scope/EvalContext;")
mw.visitFieldInsn(<op:PUTSTATIC>, "Test", "context", "Lorg/erights/e/elang/scope/EvalContext;")

def eval(item) {
	println(`eval: $item : ${item.__getAllegedType()}`)
	switch (item) {
		match x :OuterNounExpr {
			def i := x.getIndex()
			mw.visitFieldInsn(<op:GETSTATIC>, "Test", "context", "Lorg/erights/e/elang/scope/EvalContext;")
			mw.visitLdcInsn(i)
			mw.visitMethodInsn(<op:INVOKEVIRTUAL>, "org/erights/e/elang/scope/EvalContext", "outer",
					"(I)Lorg/erights/e/elib/slot/Slot;")
			mw.visitMethodInsn(<op:INVOKEINTERFACE>, "org/erights/e/elib/slot/Slot", "get",
					"()Ljava/lang/Object;")
			println(`(outer)`)
		}
		match x :LiteralExpr {
			mw.visitLdcInsn(x.value())
		}
		match _ {
			mw.visitLdcInsn(`$item`)
			println(`(string)`)
		}
	}
}

for expr in [transformed] {
	def recipient := expr.recipient()
	eval(recipient)
	mw.visitLdcInsn(expr.verb())
	mw.visitIntInsn(<op:BIPUSH>, expr.args().size())
	mw.visitTypeInsn(<op:ANEWARRAY>, "java/lang/Object")
	mw.visitIntInsn(<op:ASTORE>, 0)
	for i => arg in expr.args() {
		mw.visitIntInsn(<op:ALOAD>, 0)
		mw.visitLdcInsn(i);
		eval(arg)
		mw.visitInsn(<op:AASTORE>)
	}
	mw.visitIntInsn(<op:ALOAD>, 0)
	mw.visitMethodInsn(<op:INVOKESTATIC>, "org/erights/e/elib/prim/E", "callAll",
		"(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;")
}

mw.visitInsn(<op:RETURN>)
mw.visitEnd()

def code := cw.toByteArray()
def os := <file:Test.class>.setBytes(code)
