package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.jGuru.com
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: //depot/code/org.antlr/release/antlr-2.7.5/antlr/CharStreamIOException.java#1 $
 */

import java.io.IOException;

/**
 * Wrap an IOException in a CharStreamException
 */
public class CharStreamIOException extends CharStreamException {

    static private final long serialVersionUID = -5700611266924430798L;

    public IOException io;

    public CharStreamIOException(IOException io) {
        super(io.getMessage());
        this.io = io;
    }
}
