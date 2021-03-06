package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.jGuru.com
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: //depot/code/org.antlr/release/antlr-2.7.5/antlr/TokenStreamRecognitionException.java#1 $
 */

/**
 * Wraps a RecognitionException in a TokenStreamException so you can pass it
 * along.
 */
public class TokenStreamRecognitionException extends TokenStreamException {

    static private final long serialVersionUID = 168240458934974837L;

    public RecognitionException recog;

    public TokenStreamRecognitionException(RecognitionException re) {
        super(re.getMessage());
        recog = re;
    }

    public String toString() {
        return recog.toString();
    }
}
