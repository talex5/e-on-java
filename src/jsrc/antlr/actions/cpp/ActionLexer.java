// $ANTLR : "action.g" -> "ActionLexer.java"$

package antlr.actions.cpp;

import antlr.ActionTransInfo;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.CodeGenerator;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;
import antlr.NoViableAltForCharException;
import antlr.RecognitionException;
import antlr.RuleBlock;
import antlr.Token;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.Tool;
import antlr.collections.impl.BitSet;
import antlr.collections.impl.Vector;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;

/**
 * Perform the following translations:
 * <p/>
 * AST related translations
 * <p/>
 * ##				-> currentRule_AST #(x,y,z)		-> codeGenerator.getASTCreateString(vector-of(x,y,z))
 * #[x]			-> codeGenerator.getASTCreateString(x) #x				->
 * codeGenerator.mapTreeId(x)
 * <p/>
 * Inside context of #(...), you can ref (x,y,z), [x], and x as shortcuts.
 * <p/>
 * Text related translations
 * <p/>
 * $append(x)	  -> text.append(x) $setText(x)	  -> text.setLength(_begin);
 * text.append(x) $getText		  -> new String(text.getBuffer(),_begin,text.length()-_begin)
 * $setToken(x)  -> _token = x $setType(x)	  -> _ttype = x $FOLLOW(r)    ->
 * FOLLOW set name for rule r (optional arg) $FIRST(r)     -> FIRST set name
 * for rule r (optional arg)
 */
public class ActionLexer extends antlr.CharScanner
  implements ActionLexerTokenTypes {

    protected RuleBlock currentRule;
    protected CodeGenerator generator;
    protected int lineOffset = 0;
    private Tool antlrTool;        // The ANTLR tool
    ActionTransInfo transInfo;

    public ActionLexer(String s,
                       RuleBlock currentRule,
                       CodeGenerator generator,
                       ActionTransInfo transInfo) {
        this(new StringReader(s));
        this.currentRule = currentRule;
        this.generator = generator;
        this.transInfo = transInfo;
    }

    public void setLineOffset(int lineOffset) {
        setLine(lineOffset);
    }

    public void setTool(Tool tool) {
        antlrTool = tool;
    }

    public void reportError(RecognitionException e) {
        antlrTool.error("Syntax error in action: " + e,
                        getFilename(),
                        getLine(),
                        getColumn());
    }

    public void reportError(String s) {
        antlrTool.error(s, getFilename(), getLine(), getColumn());
    }

    public void reportWarning(String s) {
        if (getFilename() == null) {
            antlrTool.warning(s);
        } else {
            antlrTool.warning(s, getFilename(), getLine(), getColumn());
        }
    }

    public ActionLexer(InputStream in) {
        this(new ByteBuffer(in));
    }

    public ActionLexer(Reader in) {
        this(new CharBuffer(in));
    }

    public ActionLexer(InputBuffer ib) {
        this(new LexerSharedInputState(ib));
    }

    public ActionLexer(LexerSharedInputState state) {
        super(state);
        caseSensitiveLiterals = true;
        setCaseSensitive(true);
        literals = new Hashtable();
    }

    public Token nextToken() throws TokenStreamException {
        Token theRetToken = null;
        tryAgain:
        for (; ;) {
            Token _token = null;
            int _ttype = Token.INVALID_TYPE;
            resetText();
            try {   // for char stream error handling
                try {   // for lexical error handling
                    if ((('\u0003' <= LA(1) && '\u00ff' >= LA(1)))) {
                        mACTION(true);
                        theRetToken = _returnToken;
                    } else {
                        if (EOF_CHAR == LA(1)) {
                            uponEOF();
                            _returnToken = makeToken(Token.EOF_TYPE);
                        } else {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }
                    }

                    if (_returnToken == null) {
                        continue tryAgain; // found SKIP token
                    }
                    _ttype = _returnToken.getType();
                    _returnToken.setType(_ttype);
                    return _returnToken;
                } catch (RecognitionException e) {
                    throw new TokenStreamRecognitionException(e);
                }
            } catch (CharStreamException cse) {
                if (cse instanceof CharStreamIOException) {
                    throw new TokenStreamIOException(((CharStreamIOException)cse).io);
                } else {
                    throw new TokenStreamException(cse.getMessage());
                }
            }
        }
    }

    public final void mACTION(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = ACTION;
        int _saveIndex;

        {
            int _cnt502 = 0;
            _loop502:
            do {
                switch (LA(1)) {
                case'#': {
                    mAST_ITEM(false);
                    break;
                }
                case'$': {
                    mTEXT_ITEM(false);
                    break;
                }
                default:
                    if ((_tokenSet_0.member(LA(1)))) {
                        mSTUFF(false);
                    } else {
                        if (1 <= _cnt502) {
                            break _loop502;
                        } else {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }
                    }
                }
                _cnt502++;
            } while (true);
        }
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    /**
     * stuff in between #(...) and #id items Allow the escaping of the # for C
     * preprocessor stuff.
     */
    protected final void mSTUFF(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = STUFF;
        int _saveIndex;

        switch (LA(1)) {
        case'"': {
            mSTRING(false);
            break;
        }
        case'\'': {
            mCHAR(false);
            break;
        }
        case'\n': {
            match('\n');
            newline();
            break;
        }
        default:
            if (('/' == LA(1)) && ('*' == LA(2) || '/' == LA(2))) {
                mCOMMENT(false);
            } else if (('\r' == LA(1)) && ('\n' == LA(2)) && (true)) {
                match("\r\n");
                newline();
            } else if (('\\' == LA(1)) && ('#' == LA(2)) && (true)) {
                match('\\');
                match('#');
                text.setLength(_begin);
                text.append("#");
            } else if (('/' == LA(1)) && (_tokenSet_1.member(LA(2)))) {
                match('/');
                {
                    match(_tokenSet_1);
                }
            } else if (('\r' == LA(1)) && (true) && (true)) {
                match('\r');
                newline();
            } else if ((_tokenSet_2.member(LA(1))) && (true) && (true)) {
                {
                    match(_tokenSet_2);
                }
            } else {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }
        }
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mAST_ITEM(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = AST_ITEM;
        int _saveIndex;
        Token t = null;
        Token id = null;
        Token ctor = null;

        if (('#' == LA(1)) && ('(' == LA(2))) {
            _saveIndex = text.length();
            match('#');
            text.setLength(_saveIndex);
            mTREE(true);
            t = _returnToken;
        } else if (('#' == LA(1)) && (_tokenSet_3.member(LA(2)))) {
            _saveIndex = text.length();
            match('#');
            text.setLength(_saveIndex);
            {
                switch (LA(1)) {
                case'\t':
                case'\n':
                case'\r':
                case' ': {
                    mWS(false);
                    break;
                }
                case':':
                case'A':
                case'B':
                case'C':
                case'D':
                case'E':
                case'F':
                case'G':
                case'H':
                case'I':
                case'J':
                case'K':
                case'L':
                case'M':
                case'N':
                case'O':
                case'P':
                case'Q':
                case'R':
                case'S':
                case'T':
                case'U':
                case'V':
                case'W':
                case'X':
                case'Y':
                case'Z':
                case'_':
                case'a':
                case'b':
                case'c':
                case'd':
                case'e':
                case'f':
                case'g':
                case'h':
                case'i':
                case'j':
                case'k':
                case'l':
                case'm':
                case'n':
                case'o':
                case'p':
                case'q':
                case'r':
                case's':
                case't':
                case'u':
                case'v':
                case'w':
                case'x':
                case'y':
                case'z': {
                    break;
                }
                default: {
                    throw new NoViableAltForCharException((char)LA(1),
                                                          getFilename(),
                                                          getLine(),
                                                          getColumn());
                }
                }
            }
            mID(true);
            id = _returnToken;

            String idt = id.getText();
            String mapped = generator.mapTreeId(id.getText(), transInfo);

            // verify that it's not a preprocessor macro...
            if (mapped != null && !idt.equals(mapped)) {
                text.setLength(_begin);
                text.append(mapped);
            } else {
                if ("if".equals(idt) || "define".equals(idt) ||
                  "ifdef".equals(idt) || "ifndef".equals(idt) ||
                  "else".equals(idt) || "elif".equals(idt) ||
                  "endif".equals(idt) || "warning".equals(idt) ||
                  "error".equals(idt) || "ident".equals(idt) ||
                  "pragma".equals(idt) || "include".equals(idt)) {
                    text.setLength(_begin);
                    text.append("#" + idt);
                }
            }

            {
                if ((_tokenSet_4.member(LA(1))) && (true) && (true)) {
                    mWS(false);
                } else {
                }

            }
            {
                if (('=' == LA(1)) && (true) && (true)) {
                    mVAR_ASSIGN(false);
                } else {
                }

            }
        } else if (('#' == LA(1)) && ('[' == LA(2))) {
            _saveIndex = text.length();
            match('#');
            text.setLength(_saveIndex);
            mAST_CONSTRUCTOR(true);
            ctor = _returnToken;
        } else if (('#' == LA(1)) && ('#' == LA(2))) {
            match("##");

            if (currentRule != null) {
                String r = currentRule.getRuleName() + "_AST";
                text.setLength(_begin);
                text.append(r);

                if (transInfo != null) {
                    transInfo.refRuleRoot = r;        // we ref root of tree
                }
            } else {
                reportWarning("\"##\" not valid in this context");
                text.setLength(_begin);
                text.append("##");
            }

            {
                if ((_tokenSet_4.member(LA(1))) && (true) && (true)) {
                    mWS(false);
                } else {
                }

            }
            {
                if (('=' == LA(1)) && (true) && (true)) {
                    mVAR_ASSIGN(false);
                } else {
                }

            }
        } else {
            throw new NoViableAltForCharException((char)LA(1),
                                                  getFilename(),
                                                  getLine(),
                                                  getColumn());
        }

        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mTEXT_ITEM(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = TEXT_ITEM;
        int _saveIndex;
        Token a1 = null;
        Token a2 = null;
        Token a3 = null;
        Token a4 = null;
        Token a5 = null;
        Token a6 = null;

        if (('$' == LA(1)) && ('F' == LA(2)) && ('O' == LA(3))) {
            match("$FOLLOW");
            {
                if ((_tokenSet_5.member(LA(1))) &&
                  (_tokenSet_6.member(LA(2))) &&
                  (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
                    {
                        switch (LA(1)) {
                        case'\t':
                        case'\n':
                        case'\r':
                        case' ': {
                            mWS(false);
                            break;
                        }
                        case'(': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }
                        }
                    }
                    match('(');
                    mTEXT_ARG(true);
                    a5 = _returnToken;
                    match(')');
                } else {
                }

            }

            String rule = currentRule.getRuleName();
            if (a5 != null) {
                rule = a5.getText();
            }
            String setName = generator.getFOLLOWBitSet(rule, 1);
            // System.out.println("FOLLOW("+rule+")="+setName);
            if (setName == null) {
                reportError("$FOLLOW(" + rule + ")" +
                  ": unknown rule or bad lookahead computation");
            } else {
                text.setLength(_begin);
                text.append(setName);
            }

        } else if (('$' == LA(1)) && ('F' == LA(2)) && ('I' == LA(3))) {
            match("$FIRST");
            {
                if ((_tokenSet_5.member(LA(1))) &&
                  (_tokenSet_6.member(LA(2))) &&
                  (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
                    {
                        switch (LA(1)) {
                        case'\t':
                        case'\n':
                        case'\r':
                        case' ': {
                            mWS(false);
                            break;
                        }
                        case'(': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }
                        }
                    }
                    match('(');
                    mTEXT_ARG(true);
                    a6 = _returnToken;
                    match(')');
                } else {
                }

            }

            String rule = currentRule.getRuleName();
            if (a6 != null) {
                rule = a6.getText();
            }
            String setName = generator.getFIRSTBitSet(rule, 1);
            // System.out.println("FIRST("+rule+")="+setName);
            if (setName == null) {
                reportError("$FIRST(" + rule + ")" +
                  ": unknown rule or bad lookahead computation");
            } else {
                text.setLength(_begin);
                text.append(setName);
            }

        } else if (('$' == LA(1)) && ('a' == LA(2))) {
            match("$append");
            {
                switch (LA(1)) {
                case'\t':
                case'\n':
                case'\r':
                case' ': {
                    mWS(false);
                    break;
                }
                case'(': {
                    break;
                }
                default: {
                    throw new NoViableAltForCharException((char)LA(1),
                                                          getFilename(),
                                                          getLine(),
                                                          getColumn());
                }
                }
            }
            match('(');
            mTEXT_ARG(true);
            a1 = _returnToken;
            match(')');

            String t = "text += " + a1.getText();
            text.setLength(_begin);
            text.append(t);

        } else if (('$' == LA(1)) && ('s' == LA(2))) {
            match("$set");
            {
                if (('T' == LA(1)) && ('e' == LA(2))) {
                    match("Text");
                    {
                        switch (LA(1)) {
                        case'\t':
                        case'\n':
                        case'\r':
                        case' ': {
                            mWS(false);
                            break;
                        }
                        case'(': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }
                        }
                    }
                    match('(');
                    mTEXT_ARG(true);
                    a2 = _returnToken;
                    match(')');

                    String t;
                    t =
                      "{ text.erase(_begin); text += " + a2.getText() + "; }";
                    text.setLength(_begin);
                    text.append(t);

                } else if (('T' == LA(1)) && ('o' == LA(2))) {
                    match("Token");
                    {
                        switch (LA(1)) {
                        case'\t':
                        case'\n':
                        case'\r':
                        case' ': {
                            mWS(false);
                            break;
                        }
                        case'(': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }
                        }
                    }
                    match('(');
                    mTEXT_ARG(true);
                    a3 = _returnToken;
                    match(')');

                    String t = "_token = " + a3.getText();
                    text.setLength(_begin);
                    text.append(t);

                } else if (('T' == LA(1)) && ('y' == LA(2))) {
                    match("Type");
                    {
                        switch (LA(1)) {
                        case'\t':
                        case'\n':
                        case'\r':
                        case' ': {
                            mWS(false);
                            break;
                        }
                        case'(': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }
                        }
                    }
                    match('(');
                    mTEXT_ARG(true);
                    a4 = _returnToken;
                    match(')');

                    String t = "_ttype = " + a4.getText();
                    text.setLength(_begin);
                    text.append(t);

                } else {
                    throw new NoViableAltForCharException((char)LA(1),
                                                          getFilename(),
                                                          getLine(),
                                                          getColumn());
                }

            }
        } else if (('$' == LA(1)) && ('g' == LA(2))) {
            match("$getText");

            text.setLength(_begin);
            text.append("text.substr(_begin,text.length()-_begin)");

        } else {
            throw new NoViableAltForCharException((char)LA(1),
                                                  getFilename(),
                                                  getLine(),
                                                  getColumn());
        }

        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mCOMMENT(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = COMMENT;
        int _saveIndex;

        if (('/' == LA(1)) && ('/' == LA(2))) {
            mSL_COMMENT(false);
        } else if (('/' == LA(1)) && ('*' == LA(2))) {
            mML_COMMENT(false);
        } else {
            throw new NoViableAltForCharException((char)LA(1),
                                                  getFilename(),
                                                  getLine(),
                                                  getColumn());
        }

        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mSTRING(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = STRING;
        int _saveIndex;

        match('"');
        {
            _loop599:
            do {
                if (('\\' == LA(1))) {
                    mESC(false);
                } else if ((_tokenSet_7.member(LA(1)))) {
                    matchNot('"');
                } else {
                    break _loop599;
                }

            } while (true);
        }
        match('"');
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mCHAR(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = CHAR;
        int _saveIndex;

        match('\'');
        {
            if (('\\' == LA(1))) {
                mESC(false);
            } else if ((_tokenSet_8.member(LA(1)))) {
                matchNot('\'');
            } else {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }

        }
        match('\'');
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mTREE(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = TREE;
        int _saveIndex;
        Token t = null;
        Token t2 = null;

        StringBuffer buf = new StringBuffer();
        int n = 0;
        Vector terms = new Vector(10);


        _saveIndex = text.length();
        match('(');
        text.setLength(_saveIndex);
        {
            switch (LA(1)) {
            case'\t':
            case'\n':
            case'\r':
            case' ': {
                _saveIndex = text.length();
                mWS(false);
                text.setLength(_saveIndex);
                break;
            }
            case'"':
            case'#':
            case'(':
            case':':
            case'A':
            case'B':
            case'C':
            case'D':
            case'E':
            case'F':
            case'G':
            case'H':
            case'I':
            case'J':
            case'K':
            case'L':
            case'M':
            case'N':
            case'O':
            case'P':
            case'Q':
            case'R':
            case'S':
            case'T':
            case'U':
            case'V':
            case'W':
            case'X':
            case'Y':
            case'Z':
            case'[':
            case'_':
            case'a':
            case'b':
            case'c':
            case'd':
            case'e':
            case'f':
            case'g':
            case'h':
            case'i':
            case'j':
            case'k':
            case'l':
            case'm':
            case'n':
            case'o':
            case'p':
            case'q':
            case'r':
            case's':
            case't':
            case'u':
            case'v':
            case'w':
            case'x':
            case'y':
            case'z': {
                break;
            }
            default: {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }
            }
        }
        _saveIndex = text.length();
        mTREE_ELEMENT(true);
        text.setLength(_saveIndex);
        t = _returnToken;

        terms.appendElement(generator.processStringForASTConstructor(t.getText()));

        {
            switch (LA(1)) {
            case'\t':
            case'\n':
            case'\r':
            case' ': {
                _saveIndex = text.length();
                mWS(false);
                text.setLength(_saveIndex);
                break;
            }
            case')':
            case',': {
                break;
            }
            default: {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }
            }
        }
        {
            _loop528:
            do {
                if ((',' == LA(1))) {
                    _saveIndex = text.length();
                    match(',');
                    text.setLength(_saveIndex);
                    {
                        switch (LA(1)) {
                        case'\t':
                        case'\n':
                        case'\r':
                        case' ': {
                            _saveIndex = text.length();
                            mWS(false);
                            text.setLength(_saveIndex);
                            break;
                        }
                        case'"':
                        case'#':
                        case'(':
                        case':':
                        case'A':
                        case'B':
                        case'C':
                        case'D':
                        case'E':
                        case'F':
                        case'G':
                        case'H':
                        case'I':
                        case'J':
                        case'K':
                        case'L':
                        case'M':
                        case'N':
                        case'O':
                        case'P':
                        case'Q':
                        case'R':
                        case'S':
                        case'T':
                        case'U':
                        case'V':
                        case'W':
                        case'X':
                        case'Y':
                        case'Z':
                        case'[':
                        case'_':
                        case'a':
                        case'b':
                        case'c':
                        case'd':
                        case'e':
                        case'f':
                        case'g':
                        case'h':
                        case'i':
                        case'j':
                        case'k':
                        case'l':
                        case'm':
                        case'n':
                        case'o':
                        case'p':
                        case'q':
                        case'r':
                        case's':
                        case't':
                        case'u':
                        case'v':
                        case'w':
                        case'x':
                        case'y':
                        case'z': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }
                        }
                    }
                    _saveIndex = text.length();
                    mTREE_ELEMENT(true);
                    text.setLength(_saveIndex);
                    t2 = _returnToken;

                    terms.appendElement(generator.processStringForASTConstructor(
                      t2.getText()));

                    {
                        switch (LA(1)) {
                        case'\t':
                        case'\n':
                        case'\r':
                        case' ': {
                            _saveIndex = text.length();
                            mWS(false);
                            text.setLength(_saveIndex);
                            break;
                        }
                        case')':
                        case',': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }
                        }
                    }
                } else {
                    break _loop528;
                }

            } while (true);
        }
        text.setLength(_begin);
        text.append(generator.getASTCreateString(terms));
        _saveIndex = text.length();
        match(')');
        text.setLength(_saveIndex);
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mWS(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = WS;
        int _saveIndex;

        {
            int _cnt619 = 0;
            _loop619:
            do {
                if (('\r' == LA(1)) && ('\n' == LA(2)) && (true)) {
                    match('\r');
                    match('\n');
                    newline();
                } else if ((' ' == LA(1)) && (true) && (true)) {
                    match(' ');
                } else if (('\t' == LA(1)) && (true) && (true)) {
                    match('\t');
                } else if (('\r' == LA(1)) && (true) && (true)) {
                    match('\r');
                    newline();
                } else if (('\n' == LA(1)) && (true) && (true)) {
                    match('\n');
                    newline();
                } else {
                    if (1 <= _cnt619) {
                        break _loop619;
                    } else {
                        throw new NoViableAltForCharException((char)LA(1),
                                                              getFilename(),
                                                              getLine(),
                                                              getColumn());
                    }
                }

                _cnt619++;
            } while (true);
        }
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mID(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = ID;
        int _saveIndex;

        {
            switch (LA(1)) {
            case'a':
            case'b':
            case'c':
            case'd':
            case'e':
            case'f':
            case'g':
            case'h':
            case'i':
            case'j':
            case'k':
            case'l':
            case'm':
            case'n':
            case'o':
            case'p':
            case'q':
            case'r':
            case's':
            case't':
            case'u':
            case'v':
            case'w':
            case'x':
            case'y':
            case'z': {
                matchRange('a', 'z');
                break;
            }
            case'A':
            case'B':
            case'C':
            case'D':
            case'E':
            case'F':
            case'G':
            case'H':
            case'I':
            case'J':
            case'K':
            case'L':
            case'M':
            case'N':
            case'O':
            case'P':
            case'Q':
            case'R':
            case'S':
            case'T':
            case'U':
            case'V':
            case'W':
            case'X':
            case'Y':
            case'Z': {
                matchRange('A', 'Z');
                break;
            }
            case'_': {
                match('_');
                break;
            }
            case':': {
                match("::");
                break;
            }
            default: {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }
            }
        }
        {
            _loop585:
            do {
                if ((_tokenSet_9.member(LA(1))) && (true) && (true)) {
                    {
                        switch (LA(1)) {
                        case'a':
                        case'b':
                        case'c':
                        case'd':
                        case'e':
                        case'f':
                        case'g':
                        case'h':
                        case'i':
                        case'j':
                        case'k':
                        case'l':
                        case'm':
                        case'n':
                        case'o':
                        case'p':
                        case'q':
                        case'r':
                        case's':
                        case't':
                        case'u':
                        case'v':
                        case'w':
                        case'x':
                        case'y':
                        case'z': {
                            matchRange('a', 'z');
                            break;
                        }
                        case'A':
                        case'B':
                        case'C':
                        case'D':
                        case'E':
                        case'F':
                        case'G':
                        case'H':
                        case'I':
                        case'J':
                        case'K':
                        case'L':
                        case'M':
                        case'N':
                        case'O':
                        case'P':
                        case'Q':
                        case'R':
                        case'S':
                        case'T':
                        case'U':
                        case'V':
                        case'W':
                        case'X':
                        case'Y':
                        case'Z': {
                            matchRange('A', 'Z');
                            break;
                        }
                        case'0':
                        case'1':
                        case'2':
                        case'3':
                        case'4':
                        case'5':
                        case'6':
                        case'7':
                        case'8':
                        case'9': {
                            matchRange('0', '9');
                            break;
                        }
                        case'_': {
                            match('_');
                            break;
                        }
                        case':': {
                            match("::");
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }
                        }
                    }
                } else {
                    break _loop585;
                }

            } while (true);
        }
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mVAR_ASSIGN(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = VAR_ASSIGN;
        int _saveIndex;

        match('=');

        // inform the code generator that an assignment was done to
        // AST root for the rule if invoker set refRuleRoot.
        if ('=' != LA(1) && transInfo != null &&
          transInfo.refRuleRoot != null) {
            transInfo.assignToRoot = true;
        }

        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mAST_CONSTRUCTOR(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = AST_CONSTRUCTOR;
        int _saveIndex;
        Token x = null;
        Token y = null;

        _saveIndex = text.length();
        match('[');
        text.setLength(_saveIndex);
        {
            switch (LA(1)) {
            case'\t':
            case'\n':
            case'\r':
            case' ': {
                _saveIndex = text.length();
                mWS(false);
                text.setLength(_saveIndex);
                break;
            }
            case'"':
            case'#':
            case'(':
            case'0':
            case'1':
            case'2':
            case'3':
            case'4':
            case'5':
            case'6':
            case'7':
            case'8':
            case'9':
            case':':
            case'A':
            case'B':
            case'C':
            case'D':
            case'E':
            case'F':
            case'G':
            case'H':
            case'I':
            case'J':
            case'K':
            case'L':
            case'M':
            case'N':
            case'O':
            case'P':
            case'Q':
            case'R':
            case'S':
            case'T':
            case'U':
            case'V':
            case'W':
            case'X':
            case'Y':
            case'Z':
            case'[':
            case'_':
            case'a':
            case'b':
            case'c':
            case'd':
            case'e':
            case'f':
            case'g':
            case'h':
            case'i':
            case'j':
            case'k':
            case'l':
            case'm':
            case'n':
            case'o':
            case'p':
            case'q':
            case'r':
            case's':
            case't':
            case'u':
            case'v':
            case'w':
            case'x':
            case'y':
            case'z': {
                break;
            }
            default: {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }
            }
        }
        _saveIndex = text.length();
        mAST_CTOR_ELEMENT(true);
        text.setLength(_saveIndex);
        x = _returnToken;
        {
            switch (LA(1)) {
            case'\t':
            case'\n':
            case'\r':
            case' ': {
                _saveIndex = text.length();
                mWS(false);
                text.setLength(_saveIndex);
                break;
            }
            case',':
            case']': {
                break;
            }
            default: {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }
            }
        }
        {
            switch (LA(1)) {
            case',': {
                _saveIndex = text.length();
                match(',');
                text.setLength(_saveIndex);
                {
                    switch (LA(1)) {
                    case'\t':
                    case'\n':
                    case'\r':
                    case' ': {
                        _saveIndex = text.length();
                        mWS(false);
                        text.setLength(_saveIndex);
                        break;
                    }
                    case'"':
                    case'#':
                    case'(':
                    case'0':
                    case'1':
                    case'2':
                    case'3':
                    case'4':
                    case'5':
                    case'6':
                    case'7':
                    case'8':
                    case'9':
                    case':':
                    case'A':
                    case'B':
                    case'C':
                    case'D':
                    case'E':
                    case'F':
                    case'G':
                    case'H':
                    case'I':
                    case'J':
                    case'K':
                    case'L':
                    case'M':
                    case'N':
                    case'O':
                    case'P':
                    case'Q':
                    case'R':
                    case'S':
                    case'T':
                    case'U':
                    case'V':
                    case'W':
                    case'X':
                    case'Y':
                    case'Z':
                    case'[':
                    case'_':
                    case'a':
                    case'b':
                    case'c':
                    case'd':
                    case'e':
                    case'f':
                    case'g':
                    case'h':
                    case'i':
                    case'j':
                    case'k':
                    case'l':
                    case'm':
                    case'n':
                    case'o':
                    case'p':
                    case'q':
                    case'r':
                    case's':
                    case't':
                    case'u':
                    case'v':
                    case'w':
                    case'x':
                    case'y':
                    case'z': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException((char)LA(1),
                                                              getFilename(),
                                                              getLine(),
                                                              getColumn());
                    }
                    }
                }
                _saveIndex = text.length();
                mAST_CTOR_ELEMENT(true);
                text.setLength(_saveIndex);
                y = _returnToken;
                {
                    switch (LA(1)) {
                    case'\t':
                    case'\n':
                    case'\r':
                    case' ': {
                        _saveIndex = text.length();
                        mWS(false);
                        text.setLength(_saveIndex);
                        break;
                    }
                    case']': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException((char)LA(1),
                                                              getFilename(),
                                                              getLine(),
                                                              getColumn());
                    }
                    }
                }
                break;
            }
            case']': {
                break;
            }
            default: {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }
            }
        }
        _saveIndex = text.length();
        match(']');
        text.setLength(_saveIndex);

        //			System.out.println("AST_CONSTRUCTOR: "+((x==null)?"null":x.getText())+
        //									 ", "+((y==null)?"null":y.getText()));
        String ys = generator.processStringForASTConstructor(x.getText());

        // the second does not need processing coz it's a string
        // (eg second param of astFactory.create(x,y)
        if (y != null) {
            ys += "," + y.getText();
        }

        text.setLength(_begin);
        text.append(generator.getASTCreateString(null, ys));

        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mTEXT_ARG(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = TEXT_ARG;
        int _saveIndex;

        {
            switch (LA(1)) {
            case'\t':
            case'\n':
            case'\r':
            case' ': {
                mWS(false);
                break;
            }
            case'"':
            case'$':
            case'\'':
            case'+':
            case'0':
            case'1':
            case'2':
            case'3':
            case'4':
            case'5':
            case'6':
            case'7':
            case'8':
            case'9':
            case':':
            case'A':
            case'B':
            case'C':
            case'D':
            case'E':
            case'F':
            case'G':
            case'H':
            case'I':
            case'J':
            case'K':
            case'L':
            case'M':
            case'N':
            case'O':
            case'P':
            case'Q':
            case'R':
            case'S':
            case'T':
            case'U':
            case'V':
            case'W':
            case'X':
            case'Y':
            case'Z':
            case'_':
            case'a':
            case'b':
            case'c':
            case'd':
            case'e':
            case'f':
            case'g':
            case'h':
            case'i':
            case'j':
            case'k':
            case'l':
            case'm':
            case'n':
            case'o':
            case'p':
            case'q':
            case'r':
            case's':
            case't':
            case'u':
            case'v':
            case'w':
            case'x':
            case'y':
            case'z': {
                break;
            }
            default: {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }
            }
        }
        {
            int _cnt559 = 0;
            _loop559:
            do {
                if ((_tokenSet_10.member(LA(1))) &&
                  (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) && (true)) {
                    mTEXT_ARG_ELEMENT(false);
                    {
                        if ((_tokenSet_4.member(LA(1))) &&
                          (_tokenSet_11.member(LA(2))) && (true)) {
                            mWS(false);
                        } else
                        if ((_tokenSet_11.member(LA(1))) && (true) && (true)) {
                        } else {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }

                    }
                } else {
                    if (1 <= _cnt559) {
                        break _loop559;
                    } else {
                        throw new NoViableAltForCharException((char)LA(1),
                                                              getFilename(),
                                                              getLine(),
                                                              getColumn());
                    }
                }

                _cnt559++;
            } while (true);
        }
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mTREE_ELEMENT(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = TREE_ELEMENT;
        int _saveIndex;
        Token id = null;
        boolean was_mapped;

        switch (LA(1)) {
        case'(': {
            mTREE(false);
            break;
        }
        case'[': {
            mAST_CONSTRUCTOR(false);
            break;
        }
        case':':
        case'A':
        case'B':
        case'C':
        case'D':
        case'E':
        case'F':
        case'G':
        case'H':
        case'I':
        case'J':
        case'K':
        case'L':
        case'M':
        case'N':
        case'O':
        case'P':
        case'Q':
        case'R':
        case'S':
        case'T':
        case'U':
        case'V':
        case'W':
        case'X':
        case'Y':
        case'Z':
        case'_':
        case'a':
        case'b':
        case'c':
        case'd':
        case'e':
        case'f':
        case'g':
        case'h':
        case'i':
        case'j':
        case'k':
        case'l':
        case'm':
        case'n':
        case'o':
        case'p':
        case'q':
        case'r':
        case's':
        case't':
        case'u':
        case'v':
        case'w':
        case'x':
        case'y':
        case'z': {
            mID_ELEMENT(false);
            break;
        }
        case'"': {
            mSTRING(false);
            break;
        }
        default:
            if (('#' == LA(1)) && ('(' == LA(2))) {
                _saveIndex = text.length();
                match('#');
                text.setLength(_saveIndex);
                mTREE(false);
            } else if (('#' == LA(1)) && ('[' == LA(2))) {
                _saveIndex = text.length();
                match('#');
                text.setLength(_saveIndex);
                mAST_CONSTRUCTOR(false);
            } else if (('#' == LA(1)) && (_tokenSet_12.member(LA(2)))) {
                _saveIndex = text.length();
                match('#');
                text.setLength(_saveIndex);
                was_mapped = mID_ELEMENT(true);
                id = _returnToken;
                // RK: I have a queer feeling that this maptreeid is redundant..
                if (!was_mapped) {
                    String t = generator.mapTreeId(id.getText(), null);
//				System.out.println("mapped: "+id.getText()+" -> "+t);
                    if (t != null) {
                        text.setLength(_begin);
                        text.append(t);
                    }
                }

            } else if (('#' == LA(1)) && ('#' == LA(2))) {
                match("##");

                if (currentRule != null) {
                    String t = currentRule.getRuleName() + "_AST";
                    text.setLength(_begin);
                    text.append(t);
                } else {
                    reportError("\"##\" not valid in this context");
                    text.setLength(_begin);
                    text.append("##");
                }

            } else {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }
        }
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    /**
     * An ID_ELEMENT can be a func call, array ref, simple var, or AST label
     * ref.
     */
    protected final boolean mID_ELEMENT(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        boolean mapped = false;
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = ID_ELEMENT;
        int _saveIndex;
        Token id = null;

        mID(true);
        id = _returnToken;
        {
            if ((_tokenSet_4.member(LA(1))) && (_tokenSet_13.member(LA(2))) &&
              (true)) {
                _saveIndex = text.length();
                mWS(false);
                text.setLength(_saveIndex);
            } else if ((_tokenSet_13.member(LA(1))) && (true) && (true)) {
            } else {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }

        }
        {
            switch (LA(1)) {
            case'(':
            case'<': {
                {
                    switch (LA(1)) {
                    case'<': {
                        match('<');
                        {
                            _loop542:
                            do {
                                if ((_tokenSet_14.member(LA(1)))) {
                                    matchNot('>');
                                } else {
                                    break _loop542;
                                }

                            } while (true);
                        }
                        match('>');
                        break;
                    }
                    case'(': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException((char)LA(1),
                                                              getFilename(),
                                                              getLine(),
                                                              getColumn());
                    }
                    }
                }
                match('(');
                {
                    if ((_tokenSet_4.member(LA(1))) &&
                      (_tokenSet_15.member(LA(2))) &&
                      (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
                        _saveIndex = text.length();
                        mWS(false);
                        text.setLength(_saveIndex);
                    } else if ((_tokenSet_15.member(LA(1))) &&
                      (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) && (true)) {
                    } else {
                        throw new NoViableAltForCharException((char)LA(1),
                                                              getFilename(),
                                                              getLine(),
                                                              getColumn());
                    }

                }
                {
                    switch (LA(1)) {
                    case'"':
                    case'#':
                    case'\'':
                    case'(':
                    case'0':
                    case'1':
                    case'2':
                    case'3':
                    case'4':
                    case'5':
                    case'6':
                    case'7':
                    case'8':
                    case'9':
                    case':':
                    case'A':
                    case'B':
                    case'C':
                    case'D':
                    case'E':
                    case'F':
                    case'G':
                    case'H':
                    case'I':
                    case'J':
                    case'K':
                    case'L':
                    case'M':
                    case'N':
                    case'O':
                    case'P':
                    case'Q':
                    case'R':
                    case'S':
                    case'T':
                    case'U':
                    case'V':
                    case'W':
                    case'X':
                    case'Y':
                    case'Z':
                    case'[':
                    case'_':
                    case'a':
                    case'b':
                    case'c':
                    case'd':
                    case'e':
                    case'f':
                    case'g':
                    case'h':
                    case'i':
                    case'j':
                    case'k':
                    case'l':
                    case'm':
                    case'n':
                    case'o':
                    case'p':
                    case'q':
                    case'r':
                    case's':
                    case't':
                    case'u':
                    case'v':
                    case'w':
                    case'x':
                    case'y':
                    case'z': {
                        mARG(false);
                        {
                            _loop547:
                            do {
                                if ((',' == LA(1))) {
                                    match(',');
                                    {
                                        switch (LA(1)) {
                                        case'\t':
                                        case'\n':
                                        case'\r':
                                        case' ': {
                                            _saveIndex = text.length();
                                            mWS(false);
                                            text.setLength(_saveIndex);
                                            break;
                                        }
                                        case'"':
                                        case'#':
                                        case'\'':
                                        case'(':
                                        case'0':
                                        case'1':
                                        case'2':
                                        case'3':
                                        case'4':
                                        case'5':
                                        case'6':
                                        case'7':
                                        case'8':
                                        case'9':
                                        case':':
                                        case'A':
                                        case'B':
                                        case'C':
                                        case'D':
                                        case'E':
                                        case'F':
                                        case'G':
                                        case'H':
                                        case'I':
                                        case'J':
                                        case'K':
                                        case'L':
                                        case'M':
                                        case'N':
                                        case'O':
                                        case'P':
                                        case'Q':
                                        case'R':
                                        case'S':
                                        case'T':
                                        case'U':
                                        case'V':
                                        case'W':
                                        case'X':
                                        case'Y':
                                        case'Z':
                                        case'[':
                                        case'_':
                                        case'a':
                                        case'b':
                                        case'c':
                                        case'd':
                                        case'e':
                                        case'f':
                                        case'g':
                                        case'h':
                                        case'i':
                                        case'j':
                                        case'k':
                                        case'l':
                                        case'm':
                                        case'n':
                                        case'o':
                                        case'p':
                                        case'q':
                                        case'r':
                                        case's':
                                        case't':
                                        case'u':
                                        case'v':
                                        case'w':
                                        case'x':
                                        case'y':
                                        case'z': {
                                            break;
                                        }
                                        default: {
                                            throw new NoViableAltForCharException(
                                              (char)LA(1),
                                              getFilename(),
                                              getLine(),
                                              getColumn());
                                        }
                                        }
                                    }
                                    mARG(false);
                                } else {
                                    break _loop547;
                                }

                            } while (true);
                        }
                        break;
                    }
                    case'\t':
                    case'\n':
                    case'\r':
                    case' ':
                    case')': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException((char)LA(1),
                                                              getFilename(),
                                                              getLine(),
                                                              getColumn());
                    }
                    }
                }
                {
                    switch (LA(1)) {
                    case'\t':
                    case'\n':
                    case'\r':
                    case' ': {
                        _saveIndex = text.length();
                        mWS(false);
                        text.setLength(_saveIndex);
                        break;
                    }
                    case')': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException((char)LA(1),
                                                              getFilename(),
                                                              getLine(),
                                                              getColumn());
                    }
                    }
                }
                match(')');
                break;
            }
            case'[': {
                {
                    int _cnt552 = 0;
                    _loop552:
                    do {
                        if (('[' == LA(1))) {
                            match('[');
                            {
                                switch (LA(1)) {
                                case'\t':
                                case'\n':
                                case'\r':
                                case' ': {
                                    _saveIndex = text.length();
                                    mWS(false);
                                    text.setLength(_saveIndex);
                                    break;
                                }
                                case'"':
                                case'#':
                                case'\'':
                                case'(':
                                case'0':
                                case'1':
                                case'2':
                                case'3':
                                case'4':
                                case'5':
                                case'6':
                                case'7':
                                case'8':
                                case'9':
                                case':':
                                case'A':
                                case'B':
                                case'C':
                                case'D':
                                case'E':
                                case'F':
                                case'G':
                                case'H':
                                case'I':
                                case'J':
                                case'K':
                                case'L':
                                case'M':
                                case'N':
                                case'O':
                                case'P':
                                case'Q':
                                case'R':
                                case'S':
                                case'T':
                                case'U':
                                case'V':
                                case'W':
                                case'X':
                                case'Y':
                                case'Z':
                                case'[':
                                case'_':
                                case'a':
                                case'b':
                                case'c':
                                case'd':
                                case'e':
                                case'f':
                                case'g':
                                case'h':
                                case'i':
                                case'j':
                                case'k':
                                case'l':
                                case'm':
                                case'n':
                                case'o':
                                case'p':
                                case'q':
                                case'r':
                                case's':
                                case't':
                                case'u':
                                case'v':
                                case'w':
                                case'x':
                                case'y':
                                case'z': {
                                    break;
                                }
                                default: {
                                    throw new NoViableAltForCharException((char)LA(
                                      1),
                                                                          getFilename(),
                                                                          getLine(),
                                                                          getColumn());
                                }
                                }
                            }
                            mARG(false);
                            {
                                switch (LA(1)) {
                                case'\t':
                                case'\n':
                                case'\r':
                                case' ': {
                                    _saveIndex = text.length();
                                    mWS(false);
                                    text.setLength(_saveIndex);
                                    break;
                                }
                                case']': {
                                    break;
                                }
                                default: {
                                    throw new NoViableAltForCharException((char)LA(
                                      1),
                                                                          getFilename(),
                                                                          getLine(),
                                                                          getColumn());
                                }
                                }
                            }
                            match(']');
                        } else {
                            if (1 <= _cnt552) {
                                break _loop552;
                            } else {
                                throw new NoViableAltForCharException((char)LA(
                                  1), getFilename(), getLine(), getColumn());
                            }
                        }

                        _cnt552++;
                    } while (true);
                }
                break;
            }
            case'.': {
                match('.');
                mID_ELEMENT(false);
                break;
            }
            case':': {
                match("::");
                mID_ELEMENT(false);
                break;
            }
            default:
                if (('-' == LA(1)) && ('>' == LA(2)) &&
                  (_tokenSet_12.member(LA(3)))) {
                    match("->");
                    mID_ELEMENT(false);
                } else if ((_tokenSet_16.member(LA(1))) && (true) && (true)) {

                    mapped = true;
                    String t = generator.mapTreeId(id.getText(), transInfo);
                    //				System.out.println("mapped: "+id.getText()+" -> "+t);
                    if (t != null) {
                        text.setLength(_begin);
                        text.append(t);
                    }

                    {
                        if (((_tokenSet_17.member(LA(1))) &&
                          (_tokenSet_16.member(LA(2))) && (true)) && (
                          transInfo != null &&
                            transInfo.refRuleRoot != null)) {
                            {
                                switch (LA(1)) {
                                case'\t':
                                case'\n':
                                case'\r':
                                case' ': {
                                    mWS(false);
                                    break;
                                }
                                case'=': {
                                    break;
                                }
                                default: {
                                    throw new NoViableAltForCharException((char)LA(
                                      1),
                                                                          getFilename(),
                                                                          getLine(),
                                                                          getColumn());
                                }
                                }
                            }
                            mVAR_ASSIGN(false);
                        } else
                        if ((_tokenSet_18.member(LA(1))) && (true) && (true)) {
                        } else {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }

                    }
                } else {
                    throw new NoViableAltForCharException((char)LA(1),
                                                          getFilename(),
                                                          getLine(),
                                                          getColumn());
                }
            }
        }
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
        return mapped;
    }

    /**
     * The arguments of a #[...] constructor are text, token type, or a tree.
     */
    protected final void mAST_CTOR_ELEMENT(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = AST_CTOR_ELEMENT;
        int _saveIndex;

        if (('"' == LA(1)) && (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) &&
          (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
            mSTRING(false);
        } else if ((_tokenSet_19.member(LA(1))) &&
          (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) && (true)) {
            mTREE_ELEMENT(false);
        } else if ((('0' <= LA(1) && '9' >= LA(1)))) {
            mINT(false);
        } else {
            throw new NoViableAltForCharException((char)LA(1),
                                                  getFilename(),
                                                  getLine(),
                                                  getColumn());
        }

        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mINT(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = INT;
        int _saveIndex;

        {
            int _cnt610 = 0;
            _loop610:
            do {
                if ((('0' <= LA(1) && '9' >= LA(1)))) {
                    mDIGIT(false);
                } else {
                    if (1 <= _cnt610) {
                        break _loop610;
                    } else {
                        throw new NoViableAltForCharException((char)LA(1),
                                                              getFilename(),
                                                              getLine(),
                                                              getColumn());
                    }
                }

                _cnt610++;
            } while (true);
        }
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mARG(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = ARG;
        int _saveIndex;

        {
            switch (LA(1)) {
            case'\'': {
                mCHAR(false);
                break;
            }
            case'0':
            case'1':
            case'2':
            case'3':
            case'4':
            case'5':
            case'6':
            case'7':
            case'8':
            case'9': {
                mINT_OR_FLOAT(false);
                break;
            }
            default:
                if ((_tokenSet_19.member(LA(1))) &&
                  (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) &&
                  (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
                    mTREE_ELEMENT(false);
                } else if (('"' == LA(1)) &&
                  (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) &&
                  (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
                    mSTRING(false);
                } else {
                    throw new NoViableAltForCharException((char)LA(1),
                                                          getFilename(),
                                                          getLine(),
                                                          getColumn());
                }
            }
        }
        {
            _loop580:
            do {
                if ((_tokenSet_20.member(LA(1))) &&
                  (_tokenSet_21.member(LA(2))) &&
                  (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
                    {
                        switch (LA(1)) {
                        case'\t':
                        case'\n':
                        case'\r':
                        case' ': {
                            mWS(false);
                            break;
                        }
                        case'*':
                        case'+':
                        case'-':
                        case'/': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }
                        }
                    }
                    {
                        switch (LA(1)) {
                        case'+': {
                            match('+');
                            break;
                        }
                        case'-': {
                            match('-');
                            break;
                        }
                        case'*': {
                            match('*');
                            break;
                        }
                        case'/': {
                            match('/');
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }
                        }
                    }
                    {
                        switch (LA(1)) {
                        case'\t':
                        case'\n':
                        case'\r':
                        case' ': {
                            mWS(false);
                            break;
                        }
                        case'"':
                        case'#':
                        case'\'':
                        case'(':
                        case'0':
                        case'1':
                        case'2':
                        case'3':
                        case'4':
                        case'5':
                        case'6':
                        case'7':
                        case'8':
                        case'9':
                        case':':
                        case'A':
                        case'B':
                        case'C':
                        case'D':
                        case'E':
                        case'F':
                        case'G':
                        case'H':
                        case'I':
                        case'J':
                        case'K':
                        case'L':
                        case'M':
                        case'N':
                        case'O':
                        case'P':
                        case'Q':
                        case'R':
                        case'S':
                        case'T':
                        case'U':
                        case'V':
                        case'W':
                        case'X':
                        case'Y':
                        case'Z':
                        case'[':
                        case'_':
                        case'a':
                        case'b':
                        case'c':
                        case'd':
                        case'e':
                        case'f':
                        case'g':
                        case'h':
                        case'i':
                        case'j':
                        case'k':
                        case'l':
                        case'm':
                        case'n':
                        case'o':
                        case'p':
                        case'q':
                        case'r':
                        case's':
                        case't':
                        case'u':
                        case'v':
                        case'w':
                        case'x':
                        case'y':
                        case'z': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException((char)LA(1),
                                                                  getFilename(),
                                                                  getLine(),
                                                                  getColumn());
                        }
                        }
                    }
                    mARG(false);
                } else {
                    break _loop580;
                }

            } while (true);
        }
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mTEXT_ARG_ELEMENT(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = TEXT_ARG_ELEMENT;
        int _saveIndex;

        switch (LA(1)) {
        case':':
        case'A':
        case'B':
        case'C':
        case'D':
        case'E':
        case'F':
        case'G':
        case'H':
        case'I':
        case'J':
        case'K':
        case'L':
        case'M':
        case'N':
        case'O':
        case'P':
        case'Q':
        case'R':
        case'S':
        case'T':
        case'U':
        case'V':
        case'W':
        case'X':
        case'Y':
        case'Z':
        case'_':
        case'a':
        case'b':
        case'c':
        case'd':
        case'e':
        case'f':
        case'g':
        case'h':
        case'i':
        case'j':
        case'k':
        case'l':
        case'm':
        case'n':
        case'o':
        case'p':
        case'q':
        case'r':
        case's':
        case't':
        case'u':
        case'v':
        case'w':
        case'x':
        case'y':
        case'z': {
            mTEXT_ARG_ID_ELEMENT(false);
            break;
        }
        case'"': {
            mSTRING(false);
            break;
        }
        case'\'': {
            mCHAR(false);
            break;
        }
        case'0':
        case'1':
        case'2':
        case'3':
        case'4':
        case'5':
        case'6':
        case'7':
        case'8':
        case'9': {
            mINT_OR_FLOAT(false);
            break;
        }
        case'$': {
            mTEXT_ITEM(false);
            break;
        }
        case'+': {
            match('+');
            break;
        }
        default: {
            throw new NoViableAltForCharException((char)LA(1),
                                                  getFilename(),
                                                  getLine(),
                                                  getColumn());
        }
        }
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mTEXT_ARG_ID_ELEMENT(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = TEXT_ARG_ID_ELEMENT;
        int _saveIndex;
        Token id = null;

        mID(true);
        id = _returnToken;
        {
            if ((_tokenSet_4.member(LA(1))) && (_tokenSet_22.member(LA(2))) &&
              (true)) {
                _saveIndex = text.length();
                mWS(false);
                text.setLength(_saveIndex);
            } else if ((_tokenSet_22.member(LA(1))) && (true) && (true)) {
            } else {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }

        }
        {
            switch (LA(1)) {
            case'(': {
                match('(');
                {
                    if ((_tokenSet_4.member(LA(1))) &&
                      (_tokenSet_23.member(LA(2))) &&
                      (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
                        _saveIndex = text.length();
                        mWS(false);
                        text.setLength(_saveIndex);
                    } else if ((_tokenSet_23.member(LA(1))) &&
                      (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) && (true)) {
                    } else {
                        throw new NoViableAltForCharException((char)LA(1),
                                                              getFilename(),
                                                              getLine(),
                                                              getColumn());
                    }

                }
                {
                    _loop568:
                    do {
                        if ((_tokenSet_24.member(LA(1))) &&
                          (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) &&
                          (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
                            mTEXT_ARG(false);
                            {
                                _loop567:
                                do {
                                    if ((',' == LA(1))) {
                                        match(',');
                                        mTEXT_ARG(false);
                                    } else {
                                        break _loop567;
                                    }

                                } while (true);
                            }
                        } else {
                            break _loop568;
                        }

                    } while (true);
                }
                {
                    switch (LA(1)) {
                    case'\t':
                    case'\n':
                    case'\r':
                    case' ': {
                        _saveIndex = text.length();
                        mWS(false);
                        text.setLength(_saveIndex);
                        break;
                    }
                    case')': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException((char)LA(1),
                                                              getFilename(),
                                                              getLine(),
                                                              getColumn());
                    }
                    }
                }
                match(')');
                break;
            }
            case'[': {
                {
                    int _cnt573 = 0;
                    _loop573:
                    do {
                        if (('[' == LA(1))) {
                            match('[');
                            {
                                if ((_tokenSet_4.member(LA(1))) &&
                                  (_tokenSet_24.member(LA(2))) &&
                                  (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
                                    _saveIndex = text.length();
                                    mWS(false);
                                    text.setLength(_saveIndex);
                                } else if ((_tokenSet_24.member(LA(1))) &&
                                  (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) &&
                                  (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
                                } else {
                                    throw new NoViableAltForCharException((char)LA(
                                      1),
                                                                          getFilename(),
                                                                          getLine(),
                                                                          getColumn());
                                }

                            }
                            mTEXT_ARG(false);
                            {
                                switch (LA(1)) {
                                case'\t':
                                case'\n':
                                case'\r':
                                case' ': {
                                    _saveIndex = text.length();
                                    mWS(false);
                                    text.setLength(_saveIndex);
                                    break;
                                }
                                case']': {
                                    break;
                                }
                                default: {
                                    throw new NoViableAltForCharException((char)LA(
                                      1),
                                                                          getFilename(),
                                                                          getLine(),
                                                                          getColumn());
                                }
                                }
                            }
                            match(']');
                        } else {
                            if (1 <= _cnt573) {
                                break _loop573;
                            } else {
                                throw new NoViableAltForCharException((char)LA(
                                  1), getFilename(), getLine(), getColumn());
                            }
                        }

                        _cnt573++;
                    } while (true);
                }
                break;
            }
            case'.': {
                match('.');
                mTEXT_ARG_ID_ELEMENT(false);
                break;
            }
            case'-': {
                match("->");
                mTEXT_ARG_ID_ELEMENT(false);
                break;
            }
            default:
                if ((':' == LA(1)) && (':' == LA(2)) &&
                  (_tokenSet_12.member(LA(3)))) {
                    match("::");
                    mTEXT_ARG_ID_ELEMENT(false);
                } else if ((_tokenSet_11.member(LA(1))) && (true) && (true)) {
                } else {
                    throw new NoViableAltForCharException((char)LA(1),
                                                          getFilename(),
                                                          getLine(),
                                                          getColumn());
                }
            }
        }
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mINT_OR_FLOAT(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = INT_OR_FLOAT;
        int _saveIndex;

        {
            int _cnt613 = 0;
            _loop613:
            do {
                if ((('0' <= LA(1) && '9' >= LA(1))) &&
                  (_tokenSet_25.member(LA(2))) && (true)) {
                    mDIGIT(false);
                } else {
                    if (1 <= _cnt613) {
                        break _loop613;
                    } else {
                        throw new NoViableAltForCharException((char)LA(1),
                                                              getFilename(),
                                                              getLine(),
                                                              getColumn());
                    }
                }

                _cnt613++;
            } while (true);
        }
        {
            if (('L' == LA(1)) && (_tokenSet_26.member(LA(2))) && (true)) {
                match('L');
            } else
            if (('l' == LA(1)) && (_tokenSet_26.member(LA(2))) && (true)) {
                match('l');
            } else if (('.' == LA(1))) {
                match('.');
                {
                    _loop616:
                    do {
                        if ((('0' <= LA(1) && '9' >= LA(1))) &&
                          (_tokenSet_26.member(LA(2))) && (true)) {
                            mDIGIT(false);
                        } else {
                            break _loop616;
                        }

                    } while (true);
                }
            } else if ((_tokenSet_26.member(LA(1))) && (true) && (true)) {
            } else {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }

        }
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mSL_COMMENT(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = SL_COMMENT;
        int _saveIndex;

        match("//");
        {
            _loop590:
            do {
                // nongreedy exit test
                if (('\n' == LA(1) || '\r' == LA(1)) && (true) && (true)) {
                    break _loop590;
                }
                if ((('\u0003' <= LA(1) && '\u00ff' >= LA(1))) &&
                  (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) && (true)) {
                    matchNot(EOF_CHAR);
                } else {
                    break _loop590;
                }

            } while (true);
        }
        {
            if (('\r' == LA(1)) && ('\n' == LA(2)) && (true)) {
                match("\r\n");
            } else if (('\n' == LA(1))) {
                match('\n');
            } else if (('\r' == LA(1)) && (true) && (true)) {
                match('\r');
            } else {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }

        }
        newline();
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mML_COMMENT(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = ML_COMMENT;
        int _saveIndex;

        match("/*");
        {
            _loop594:
            do {
                // nongreedy exit test
                if (('*' == LA(1)) && ('/' == LA(2)) && (true)) {
                    break _loop594;
                }
                if (('\r' == LA(1)) && ('\n' == LA(2)) &&
                  (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
                    match('\r');
                    match('\n');
                    newline();
                } else if (('\r' == LA(1)) &&
                  (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) &&
                  (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
                    match('\r');
                    newline();
                } else if (('\n' == LA(1)) &&
                  (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) &&
                  (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
                    match('\n');
                    newline();
                } else if ((('\u0003' <= LA(1) && '\u00ff' >= LA(1))) &&
                  (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) &&
                  (('\u0003' <= LA(3) && '\u00ff' >= LA(3)))) {
                    matchNot(EOF_CHAR);
                } else {
                    break _loop594;
                }

            } while (true);
        }
        match("*/");
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mESC(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = ESC;
        int _saveIndex;

        match('\\');
        {
            switch (LA(1)) {
            case'n': {
                match('n');
                break;
            }
            case'r': {
                match('r');
                break;
            }
            case't': {
                match('t');
                break;
            }
            case'v': {
                match('v');
                break;
            }
            case'b': {
                match('b');
                break;
            }
            case'f': {
                match('f');
                break;
            }
            case'"': {
                match('"');
                break;
            }
            case'\'': {
                match('\'');
                break;
            }
            case'\\': {
                match('\\');
                break;
            }
            case'0':
            case'1':
            case'2':
            case'3': {
                {
                    matchRange('0', '3');
                }
                {
                    if ((('0' <= LA(1) && '9' >= LA(1))) &&
                      (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) && (true)) {
                        mDIGIT(false);
                        {
                            if ((('0' <= LA(1) && '9' >= LA(1))) &&
                              (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) &&
                              (true)) {
                                mDIGIT(false);
                            } else if (
                              (('\u0003' <= LA(1) && '\u00ff' >= LA(1))) &&
                                (true) && (true)) {
                            } else {
                                throw new NoViableAltForCharException((char)LA(
                                  1), getFilename(), getLine(), getColumn());
                            }

                        }
                    } else if ((('\u0003' <= LA(1) && '\u00ff' >= LA(1))) &&
                      (true) && (true)) {
                    } else {
                        throw new NoViableAltForCharException((char)LA(1),
                                                              getFilename(),
                                                              getLine(),
                                                              getColumn());
                    }

                }
                break;
            }
            case'4':
            case'5':
            case'6':
            case'7': {
                {
                    matchRange('4', '7');
                }
                {
                    if ((('0' <= LA(1) && '9' >= LA(1))) &&
                      (('\u0003' <= LA(2) && '\u00ff' >= LA(2))) && (true)) {
                        mDIGIT(false);
                    } else if ((('\u0003' <= LA(1) && '\u00ff' >= LA(1))) &&
                      (true) && (true)) {
                    } else {
                        throw new NoViableAltForCharException((char)LA(1),
                                                              getFilename(),
                                                              getLine(),
                                                              getColumn());
                    }

                }
                break;
            }
            default: {
                throw new NoViableAltForCharException((char)LA(1),
                                                      getFilename(),
                                                      getLine(),
                                                      getColumn());
            }
            }
        }
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    protected final void mDIGIT(boolean _createToken)
      throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = DIGIT;
        int _saveIndex;

        matchRange('0', '9');
        if (_createToken && _token == null && Token.SKIP != _ttype) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(),
                                      _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }


    static private long[] mk_tokenSet_0() {
        long[] data = new long[8];
        data[0] = -103079215112L;
        for (int i = 1; 3 >= i; i++) {
            data[i] = -1L;
        }
        return data;
    }

    static public final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());

    static private long[] mk_tokenSet_1() {
        long[] data = new long[8];
        data[0] = -145135534866440L;
        for (int i = 1; 3 >= i; i++) {
            data[i] = -1L;
        }
        return data;
    }

    static public final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());

    static private long[] mk_tokenSet_2() {
        long[] data = new long[8];
        data[0] = -141407503262728L;
        for (int i = 1; 3 >= i; i++) {
            data[i] = -1L;
        }
        return data;
    }

    static public final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());

    static private long[] mk_tokenSet_3() {
        long[] data = {288230380446688768L, 576460745995190270L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());

    static private long[] mk_tokenSet_4() {
        long[] data = {4294977024L, 0L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());

    static private long[] mk_tokenSet_5() {
        long[] data = {1103806604800L, 0L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());

    static private long[] mk_tokenSet_6() {
        long[] data = {576189812881499648L, 576460745995190270L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());

    static private long[] mk_tokenSet_7() {
        long[] data = new long[8];
        data[0] = -17179869192L;
        data[1] = -268435457L;
        for (int i = 2; 3 >= i; i++) {
            data[i] = -1L;
        }
        return data;
    }

    static public final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());

    static private long[] mk_tokenSet_8() {
        long[] data = new long[8];
        data[0] = -549755813896L;
        data[1] = -268435457L;
        for (int i = 2; 3 >= i; i++) {
            data[i] = -1L;
        }
        return data;
    }

    static public final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());

    static private long[] mk_tokenSet_9() {
        long[] data = {576179277326712832L, 576460745995190270L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());

    static private long[] mk_tokenSet_10() {
        long[] data = {576188709074894848L, 576460745995190270L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());

    static private long[] mk_tokenSet_11() {
        long[] data = {576208504579171840L, 576460746532061182L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());

    static private long[] mk_tokenSet_12() {
        long[] data = {288230376151711744L, 576460745995190270L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());

    static private long[] mk_tokenSet_13() {
        long[] data = {3747275269732312576L, 671088640L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());

    static private long[] mk_tokenSet_14() {
        long[] data = new long[8];
        data[0] = -4611686018427387912L;
        for (int i = 1; 3 >= i; i++) {
            data[i] = -1L;
        }
        return data;
    }

    static public final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());

    static private long[] mk_tokenSet_15() {
        long[] data = {576183181451994624L, 576460746129407998L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());

    static private long[] mk_tokenSet_16() {
        long[] data = {2306051920717948416L, 536870912L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());

    static private long[] mk_tokenSet_17() {
        long[] data = {2305843013508670976L, 0L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());

    static private long[] mk_tokenSet_18() {
        long[] data = {208911504254464L, 536870912L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());

    static private long[] mk_tokenSet_19() {
        long[] data = {288231527202947072L, 576460746129407998L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());

    static private long[] mk_tokenSet_20() {
        long[] data = {189120294954496L, 0L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());

    static private long[] mk_tokenSet_21() {
        long[] data = {576370098428716544L, 576460746129407998L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());

    static private long[] mk_tokenSet_22() {
        long[] data = {576315157207066112L, 576460746666278910L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());

    static private long[] mk_tokenSet_23() {
        long[] data = {576190912393127424L, 576460745995190270L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());

    static private long[] mk_tokenSet_24() {
        long[] data = {576188713369871872L, 576460745995190270L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());

    static private long[] mk_tokenSet_25() {
        long[] data = {576459193230304768L, 576460746532061182L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());

    static private long[] mk_tokenSet_26() {
        long[] data = {576388824486127104L, 576460746532061182L, 0L, 0L, 0L};
        return data;
    }

    static public final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());

}
