package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.jGuru.com
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: //depot/code/org.antlr/release/antlr-2.7.5/antlr/DefaultFileLineFormatter.java#1 $
 */

public class DefaultFileLineFormatter extends FileLineFormatter {

    public String getFormatString(String fileName, int line, int column) {
        StringBuffer buf = new StringBuffer();

        buf.append("(");
        if (fileName != null) {
            buf.append(fileName);
            buf.append(":");
        }

        if (-1 != line) {
            if (fileName == null) {
                buf.append("line ");
            }
            buf.append(line);
        }
        buf.append(")");

        if (-1 != column) {
            buf.append("@" + column);
        }
        buf.append(":");

        buf.append(" ");

        return buf.toString();
    }
}
