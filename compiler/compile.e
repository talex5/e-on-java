def EScript := <type:org.erights.e.elang.evm.EScript>

def <asm> := <unsafe:org.objectweb.asm.*>
def makeLabel := <asm:makeLabel>
def Opcodes := <asm:makeOpcodes>
def <op> {
	to get(code) {
		return E.call(Opcodes, `get$code`, [])
	}
}
def makeEMethodWriter := <this:writer>(<asm>, println)

var nInner := 0
def innerClasses := [].diverge()

def queueCompileInnerClass(objExpr) {
	def name := `e/generated/inner${nInner}`
	nInner += 1
	innerClasses.push([name, objExpr])
	return name
}

def scriptMaker := <unsafe:org.erights.e.elib.prim.makeScriptMaker>.getTHE_ONE()

def makeMethodCompiler := <this:eval>(<asm>, queueCompileInnerClass, scriptMaker, println)

def compileOne(className, transformed, scopeLayout, nLocals, knownOuters) {
	def cw := <asm:makeClassWriter>(0)
	cw.visit(<op:V1_1>, <op:ACC_PUBLIC> | <op:ACC_FINAL>, className, null, "java/lang/Object", null)

	cw.visitField(<op:ACC_PRIVATE>, "outers", "[Lorg/erights/e/elib/slot/Slot;", null, null)
	cw.visitField(<op:ACC_PRIVATE>, "fields", "[Ljava/lang/Object;", null, null)

	# Constructor
        def cons := cw.visitMethod(<op:ACC_PUBLIC>, "<init>",
                "([Lorg/erights/e/elib/slot/Slot;[Ljava/lang/Object;)V",
                null,
                null)
	# pushes the 'this' variable
        cons.visitVarInsn(<op:ALOAD>, 0);
	# invokes the super class constructor
        cons.visitMethodInsn(<op:INVOKESPECIAL>, "java/lang/Object", "<init>", "()V");

        cons.visitVarInsn(<op:ALOAD>, 0);
        cons.visitVarInsn(<op:ALOAD>, 1);
	cons.visitFieldInsn(<op:PUTFIELD>, className, "outers", "[Lorg/erights/e/elib/slot/Slot;")

        cons.visitVarInsn(<op:ALOAD>, 0);
        cons.visitVarInsn(<op:ALOAD>, 2);
	cons.visitFieldInsn(<op:PUTFIELD>, className, "fields", "[Ljava/lang/Object;")

        cons.visitInsn(<op:RETURN>);
        cons.visitMaxs(3, 3);
        cons.visitEnd();

	if (transformed =~ script :EScript) {
		def scriptMethods := script.getOptMethods()
		for m in scriptMethods {
			def name := m.getVerb()
			def args := "Ljava/lang/Object;" * m.getPatterns().size()
			def emw := makeEMethodWriter(cw, className, <op:ACC_PUBLIC>, name, `($args)Ljava/lang/Object;`, m.getLocalCount())

			def compiler := makeMethodCompiler(emw, className, knownOuters)
			compiler.run(m)

			emw.aReturn()
			emw.endMethod()
		}
	} else {
		def emw := makeEMethodWriter(cw, className, <op:ACC_PUBLIC>, "run", "()Ljava/lang/Object;", nLocals)

		def compiler := makeMethodCompiler(emw, className, knownOuters)
		compiler.run(transformed)

		emw.aReturn()
		emw.endMethod()
	}

	def code := cw.toByteArray()
	def os := <file>[`$className.class`].setBytes(code)
	return code
}

def compile(transformed, scopeLayout, nLocals, knownOuters) {
	def root := compileOne("Test", transformed, scopeLayout, nLocals, knownOuters)
	while (innerClasses.size() > 0) {
		def [name, objExpr] := innerClasses.pop()
		def script := objExpr.getScript()
		compileOne(name, script, script.getScopeLayout(), 0, knownOuters)
	}
	return root
}

def [source] := interp.getArgs()
def eCode := <elang:syntax.makeEParser>(<file>[source].getTwine())
def compiled := eCode.compile(safeScope, compile)()
println(`c: $compiled`)
println(`compiled: ${compiled()}`)
def t1 := timer.now()
compiled()
def t2 := timer.now()

def interpreted := <this:Test>
println(`interpreted: ${interpreted()}`)
def t3 := timer.now()
interpreted()
def t4 := timer.now()
println(`compiled ran in ${t2-t1} ms`)
println(`interpreted ran in ${t4-t3} ms`)
