package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.jGuru.com
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: //depot/code/org.antlr/release/antlr-2.7.5/antlr/TreeWalkerGrammar.java#1 $
 */

import java.io.IOException;


/**
 * Parser-specific grammar subclass
 */
class TreeWalkerGrammar extends Grammar {

    // true for transform mode
    protected boolean transform = false;


    TreeWalkerGrammar(String className_, Tool tool_, String superClass) {
        super(className_, tool_, superClass);
    }

    /**
     * Top-level call to generate the code for this grammar
     */
    public void generate() throws IOException {
        generator.gen(this);
    }

    // Get name of class from which generated parser/lexer inherits
    protected String getSuperClass() {
        return "TreeParser";
    }

    /**
     * Process command line arguments. -trace			have all rules call
     * traceIn/traceOut -traceParser		have parser rules call traceIn/traceOut
     * -debug			generate debugging output for parser debugger
     */
    public void processArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("-trace".equals(args[i])) {
                traceRules = true;
                antlrTool.setArgOK(i);
            } else if ("-traceTreeParser".equals(args[i])) {
                traceRules = true;
                antlrTool.setArgOK(i);
            }
//			else if ( args[i].equals("-debug") ) {
//				debuggingOutput = true;
//				superClass = "parseview.DebuggingTreeWalker";
//				Tool.setArgOK(i);
//			}
        }
    }

    /**
     * Set tree parser options
     */
    public boolean setOption(String key, Token value) {
        if ("buildAST".equals(key)) {
            if ("true".equals(value.getText())) {
                buildAST = true;
            } else if ("false".equals(value.getText())) {
                buildAST = false;
            } else {
                antlrTool.error("buildAST option must be true or false",
                                getFilename(),
                                value.getLine(),
                                value.getColumn());
            }
            return true;
        }
        if ("ASTLabelType".equals(key)) {
            super.setOption(key, value);
            return true;
        }
        if ("className".equals(key)) {
            super.setOption(key, value);
            return true;
        }
        if (super.setOption(key, value)) {
            return true;
        }
        antlrTool.error("Invalid option: " + key,
                        getFilename(),
                        value.getLine(),
                        value.getColumn());
        return false;
    }
}
