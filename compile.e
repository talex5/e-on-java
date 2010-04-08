def EScript := <type:org.erights.e.elang.evm.EScript>

def <asm> := <unsafe:org.objectweb.asm.*>
def makeLabel := <asm:makeLabel>
def Opcodes := <asm:makeOpcodes>
def <op> {
	to get(code) {
		return E.call(Opcodes, `get$code`, [])
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

def makeMethodCompiler := <this:eval>(<asm>, queueCompileInnerClass)

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

	if (transformed =~ script :EScript) {
		def scriptMethods := script.getOptMethods()
		for m in scriptMethods {
			def name := m.getVerb()
			def mw := cw.visitMethod(<op:ACC_PUBLIC>,
						name, "()Ljava/lang/Object;",
						null, null)

			def stackSize := makeMethodCompiler(mw, className).run(m.getBody())

			println(`$className.$name : ss = $stackSize`)

			mw.visitMaxs(stackSize + 1, nLocals + 2)

			mw.visitInsn(<op:ARETURN>)
			mw.visitEnd()
		}
	} else {
		def mw := cw.visitMethod(<op:ACC_PUBLIC>,
					"run", "()Ljava/lang/Object;",
					null, null)

		def stackSize := makeMethodCompiler(mw, className).run(transformed)

		println(`$className : ss = $stackSize`)

		mw.visitMaxs(stackSize + 1, nLocals + 2)

		mw.visitInsn(<op:ARETURN>)
		mw.visitEnd()
	}

	def code := cw.toByteArray()
	def os := <file>[`$className.class`].setBytes(code)
	return code
}

def compile(transformed, scopeLayout, nLocals) {
	def root := compileOne("Test", transformed, scopeLayout, nLocals)
	while (innerClasses.size() > 0) {
		def [name, objExpr] := innerClasses.pop()
		def script := objExpr.getScript()
		compileOne(name, script, script.getScopeLayout(), 0)
	}
	return root
}

def [source] := interp.getArgs()
def eCode := <elang:syntax.makeEParser>(<file>[source].getTwine())
def compiled := eCode.compile(safeScope, compile)
println(`c: $compiled`)
compiled()
