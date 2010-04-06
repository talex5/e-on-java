all: Test.class

%.class: %.emaker compile.e
	rune-dev -cpa asm-3.2/lib/asm-3.2.jar compile.e $<
	#./jad/jad Test.class
	#cat Test.jad
	#rune -cpa . run.e

.PHONY: jad

jad:
	rm -f Test.jad
	./jad/jad Test.class
	cat Test.jad
