package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.jGuru.com
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: //depot/code/org.antlr/release/antlr-2.7.5/antlr/NoViableAltException.java#1 $
 */

import antlr.collections.AST;

public class NoViableAltException extends RecognitionException {

    static private final long serialVersionUID = 8980547557178225301L;

    public Token token;
    public AST node;        // handles parsing and treeparsing

    public NoViableAltException(AST t) {
        super("NoViableAlt", "<AST>", t.getLine(), t.getColumn());
        node = t;
    }

    public NoViableAltException(Token t, String fileName_) {
        super("NoViableAlt", fileName_, t.getLine(), t.getColumn());
        token = t;
    }

    /**
     * Returns a clean error message (no line number/column information)
     */
    public String getMessage() {
        if (token != null) {
            return "unexpected token: " + token.toString();
        }

        // must a tree parser error if token==null
        if (node == TreeParser.ASTNULL) {
            return "unexpected end of subtree";
        }
        return "unexpected AST node: " + node.toString();
    }
}
