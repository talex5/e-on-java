package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.jGuru.com
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: //depot/code/org.antlr/release/antlr-2.7.5/antlr/RecognitionException.java#1 $
 */

public class RecognitionException extends ANTLRException {

    static private final long serialVersionUID = 5226339087475726306L;

    public String fileName;                // not used by treeparsers
    public int line;
    public int column;

    public RecognitionException() {
        super("parsing error");
        fileName = null;
        line = -1;
        column = -1;
    }

    /**
     * RecognitionException constructor comment.
     *
     * @param s java.lang.String
     */
    public RecognitionException(String s) {
        super(s);
        fileName = null;
        line = -1;
        column = -1;
    }

    /**
     * @deprecated As of ANTLR 2.7.2 use 
     *             {@link #RecognitionException(String,String,int,int) }
     */
    public RecognitionException(String s, String fileName_, int line_) {
        this(s, fileName_, line_, -1);
    }

    /**
     * RecognitionException constructor comment.
     *
     * @param s java.lang.String
     */
    public RecognitionException(String s,
                                String fileName_,
                                int line_,
                                int column_) {
        super(s);
        fileName = fileName_;
        line = line_;
        column = column_;
    }

    public String getFilename() {
        return fileName;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    /**
     * @deprecated As of ANTLR 2.7.0
     */
    public String getErrorMessage() {
        return getMessage();
    }

    public String toString() {
        return FileLineFormatter.getFormatter()
          .getFormatString(fileName, line, column) + getMessage();
    }
}
