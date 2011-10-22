// Fixed by EYaccFixer to meet jvm size limits
//### This file created by BYACC 1.8(/Java extension  0.92)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//### Please send bug reports to rjamison@lincom-asg.com
//### static char yysccsid[] = "@(#)yaccpar       1.8 (Berkeley) 01/20/90";



//#line 30 "e.y"
package org.erights.e.elang.syntax;

import org.erights.e.develop.exception.EBacktraceException;
import org.erights.e.develop.exception.PrintStreamWriter;
import org.erights.e.develop.assertion.T;
import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.IdentityCacheTable;
import org.erights.e.elib.tables.Memoizer;
import org.erights.e.elib.tables.Twine;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroSchema;
import org.quasiliteral.astro.BaseSchema;
import org.quasiliteral.syntax.SyntaxException;
import org.quasiliteral.syntax.LexerFace;
import org.quasiliteral.text.EYaccFixer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
//#line 40 "EParser.java"




//#####################################################################
// class: EParser
// does : encapsulates yacc() parser functionality in a Java
//        class for quick code development
//#####################################################################
public class EParser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.err.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[],stateptr;             //state stack
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
void state_push(int state)
{
  if (stateptr>=YYSTACKSIZE)         //overflowed?
    return;
  statestk[++stateptr]=state;
}
int state_pop()
{
  if (stateptr<0)                    //underflowed?
    return -1;
  return statestk[stateptr--];
}
void state_drop(int cnt)
{
int ptr;
  ptr=stateptr-cnt;
  if (ptr<0)
    return;
  stateptr = ptr;
}
int state_peek(int relative)
{
int ptr;
  ptr=stateptr-relative;
  if (ptr<0)
    return -1;
  return statestk[ptr];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
boolean init_stacks()
{
  statestk = new int[YYSTACKSIZE];
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.err.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.err.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.err.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:Object
String   yytext;//user variable to return contextual strings
Object yyval; //used to return semantic vals from action routines
Object yylval;//the 'lval' (result) I got from yylex()
Object valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new Object[YYSTACKSIZE];
  yyval=new Object();
  yylval=new Object();
  valptr=-1;
}
void val_push(Object val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
Object val_pop()
{
  if (valptr<0)
    return null;
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
Object val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return null;
  return valstk[ptr];
}
//#### end semantic value section ####
public final static short EOL=257;
public final static short EOTLU=258;
public final static short LiteralInteger=259;
public final static short LiteralFloat64=260;
public final static short LiteralChar=261;
public final static short LiteralString=262;
public final static short LiteralTwine=263;
public final static short ID=264;
public final static short VerbAssign=265;
public final static short QuasiOpen=266;
public final static short QuasiClose=267;
public final static short DollarIdent=268;
public final static short AtIdent=269;
public final static short DollarOpen=270;
public final static short AtOpen=271;
public final static short URI=272;
public final static short DocComment=273;
public final static short AS=274;
public final static short BIND=275;
public final static short BREAK=276;
public final static short CATCH=277;
public final static short CONTINUE=278;
public final static short DEF=279;
public final static short ELSE=280;
public final static short ESCAPE=281;
public final static short EXIT=282;
public final static short EXTENDS=283;
public final static short FINALLY=284;
public final static short FN=285;
public final static short FOR=286;
public final static short GUARDS=287;
public final static short IF=288;
public final static short IMPLEMENTS=289;
public final static short IN=290;
public final static short INTERFACE=291;
public final static short MATCH=292;
public final static short META=293;
public final static short METHOD=294;
public final static short PRAGMA=295;
public final static short RETURN=296;
public final static short SWITCH=297;
public final static short TO=298;
public final static short TRY=299;
public final static short VAR=300;
public final static short VIA=301;
public final static short WHEN=302;
public final static short WHILE=303;
public final static short _=304;
public final static short ACCUM=305;
public final static short INTO=306;
public final static short MODULE=307;
public final static short ON=308;
public final static short SELECT=309;
public final static short THROWS=310;
public final static short THUNK=311;
public final static short ABSTRACT=312;
public final static short AN=313;
public final static short ASSERT=314;
public final static short ATTRIBUTE=315;
public final static short BE=316;
public final static short BEGIN=317;
public final static short BEHALF=318;
public final static short BELIEF=319;
public final static short BELIEVE=320;
public final static short BELIEVES=321;
public final static short CASE=322;
public final static short CLASS=323;
public final static short CONST=324;
public final static short CONSTRUCTOR=325;
public final static short DATATYPE=326;
public final static short DECLARE=327;
public final static short DEFAULT=328;
public final static short DEFINE=329;
public final static short DEFMACRO=330;
public final static short DELEGATE=331;
public final static short DELICATE=332;
public final static short DEPRECATED=333;
public final static short DISPATCH=334;
public final static short DO=335;
public final static short ENCAPSULATE=336;
public final static short ENCAPSULATED=337;
public final static short ENCAPSULATES=338;
public final static short END=339;
public final static short ENSURE=340;
public final static short ENUM=341;
public final static short EVENTUAL=342;
public final static short EVENTUALLY=343;
public final static short EXPORT=344;
public final static short FACET=345;
public final static short FORALL=346;
public final static short FUN=347;
public final static short FUNCTION=348;
public final static short GIVEN=349;
public final static short HIDDEN=350;
public final static short HIDES=351;
public final static short INLINE=352;
public final static short KNOW=353;
public final static short KNOWS=354;
public final static short LAMBDA=355;
public final static short LET=356;
public final static short METHODS=357;
public final static short NAMESPACE=358;
public final static short NATIVE=359;
public final static short OBEYS=360;
public final static short OCTET=361;
public final static short ONEWAY=362;
public final static short OPERATOR=363;
public final static short PACKAGE=364;
public final static short PRIVATE=365;
public final static short PROTECTED=366;
public final static short PUBLIC=367;
public final static short RAISES=368;
public final static short RELIANCE=369;
public final static short RELIANT=370;
public final static short RELIES=371;
public final static short RELY=372;
public final static short REVEAL=373;
public final static short SAKE=374;
public final static short SIGNED=375;
public final static short STATIC=376;
public final static short STRUCT=377;
public final static short SUCHTHAT=378;
public final static short SUPPORTS=379;
public final static short SUSPECT=380;
public final static short SUSPECTS=381;
public final static short SYNCHRONIZED=382;
public final static short THIS=383;
public final static short TRANSIENT=384;
public final static short TRUNCATABLE=385;
public final static short TYPEDEF=386;
public final static short UNSIGNED=387;
public final static short UNUM=388;
public final static short USES=389;
public final static short USING=390;
public final static short UTF8=391;
public final static short UTF16=392;
public final static short VIRTUAL=393;
public final static short VOLATILE=394;
public final static short WSTRING=395;
public final static short OpLAnd=396;
public final static short OpLOr=397;
public final static short OpSame=398;
public final static short OpNSame=399;
public final static short OpButNot=400;
public final static short OpLeq=401;
public final static short OpABA=402;
public final static short OpGeq=403;
public final static short OpThru=404;
public final static short OpTill=405;
public final static short OpAsl=406;
public final static short OpAsr=407;
public final static short OpFlrDiv=408;
public final static short OpMod=409;
public final static short OpPow=410;
public final static short OpAss=411;
public final static short OpAssAdd=412;
public final static short OpAssAnd=413;
public final static short OpAssAprxDiv=414;
public final static short OpAssFlrDiv=415;
public final static short OpAssAsl=416;
public final static short OpAssAsr=417;
public final static short OpAssRemdr=418;
public final static short OpAssMod=419;
public final static short OpAssMul=420;
public final static short OpAssOr=421;
public final static short OpAssPow=422;
public final static short OpAssSub=423;
public final static short OpAssXor=424;
public final static short Send=425;
public final static short OpWhen=426;
public final static short MapsTo=427;
public final static short MatchBind=428;
public final static short MisMatch=429;
public final static short OpScope=430;
public final static short AssignExpr=431;
public final static short CallExpr=432;
public final static short DefineExpr=433;
public final static short EscapeExpr=434;
public final static short HideExpr=435;
public final static short IfExpr=436;
public final static short LiteralExpr=437;
public final static short NounExpr=438;
public final static short ObjectExpr=439;
public final static short QuasiLiteralExpr=440;
public final static short QuasiPatternExpr=441;
public final static short MetaStateExpr=442;
public final static short MetaContextExpr=443;
public final static short SeqExpr=444;
public final static short SlotExpr=445;
public final static short MetaExpr=446;
public final static short CatchExpr=447;
public final static short FinallyExpr=448;
public final static short FinalPattern=449;
public final static short SlotPattern=450;
public final static short ListPattern=451;
public final static short IgnorePattern=452;
public final static short QuasiLiteralPatt=453;
public final static short QuasiPatternPatt=454;
public final static short EScript=455;
public final static short EMethod=456;
public final static short EMatcher=457;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    0,    0,    5,    5,    5,    2,    6,    6,
    7,    7,    7,    9,    9,    9,    9,    9,    9,    9,
   10,   10,   10,   10,   10,   10,   10,   10,   10,   15,
   15,   15,   12,   12,   20,   20,   21,   21,   21,   21,
   21,   21,   21,   21,   21,   23,   23,   24,   24,   22,
   22,   22,   22,   22,   22,   22,   25,   25,   25,   27,
   27,   27,   28,   28,   28,   29,   29,   29,   29,   29,
   29,   30,   30,   31,   31,   31,   31,   31,   31,   31,
   18,   18,   18,   18,   18,   18,   18,   18,   18,   18,
   33,   33,   33,   33,   33,   33,   33,   33,   33,   33,
   33,   33,   33,   38,   38,   38,   38,   45,   45,   45,
   45,   32,   32,   32,   32,   32,   32,   32,   32,   32,
   32,   32,   32,   32,   32,   32,   32,   32,   32,   32,
   62,   62,   63,   63,   63,   63,   65,   65,   46,   46,
   46,   74,   74,   74,   75,   75,   49,   61,   61,   61,
   55,   55,   59,   59,   52,   47,   47,   48,   48,   78,
   78,   79,   79,   40,   37,   37,   19,   19,   50,   50,
   50,   50,   81,   81,   53,   53,   53,   82,   82,   82,
   82,   82,   76,   76,   51,   51,    4,    4,    4,    4,
   83,   83,   83,   83,   83,   83,   83,   83,   83,   83,
   83,   83,   83,   88,   86,   86,   91,   91,   92,   92,
   93,   93,   85,   85,   85,   84,   84,   84,   84,   84,
   16,   16,   17,   17,   94,   94,   94,   66,   66,   66,
   66,   66,   43,   43,   95,   90,   90,   96,   96,   89,
   97,   97,   98,   98,   98,   98,   98,   98,   99,   99,
  100,  100,   64,   64,   64,   70,   68,  101,  101,  102,
  102,  105,  105,   73,  106,  106,  103,  103,  107,  107,
  104,    3,    3,    3,    3,  108,  108,  109,  109,   72,
   72,   72,  111,   26,   26,   26,   26,   26,   87,   87,
   87,   87,   87,  110,  110,  110,  110,  112,  112,   60,
   60,  113,  113,  113,  114,  114,  114,  114,  115,  115,
    1,    1,    8,    8,  117,   80,   39,   39,   34,   34,
   35,   35,   13,   13,   13,   77,   77,   14,   14,   14,
   14,   14,   14,   14,   14,   14,   14,   14,   14,   14,
  119,  119,  119,  119,  119,  119,  119,  119,  119,  119,
  119,  119,  119,   44,   44,   41,  120,  120,  120,  121,
  121,  121,   56,   71,   71,  123,  123,  124,  122,  122,
   57,   57,  116,   54,   54,  125,   58,   58,   11,   67,
   67,   69,   69,  127,  127,  128,  128,  129,  129,  126,
  126,  132,  132,  132,  130,  130,  133,  133,  134,  134,
  134,  131,  131,   36,   36,   42,   42,   42,   42,   42,
   42,   42,   42,   42,   42,   42,   42,  118,  118,  118,
  118,  118,  118,  118,  118,  118,  118,  118,  118,  118,
  118,  118,  118,  118,  118,  118,  118,  118,  118,  118,
  118,  118,  118,  118,  118,  118,  118,  118,  118,  118,
  118,  118,  118,  118,  118,  118,  118,  118,  118,  118,
  118,  118,  118,  118,  118,  118,  118,  118,  118,  118,
  118,  118,  118,  118,  118,  118,  118,  118,  118,  118,
  118,  118,  118,  118,  118,  118,  118,  118,  118,  118,
  118,  118,  118,  118,  118,  118,  118,  118,  118,  118,
  118,
};
final static short yylen[] = {                            2,
    1,    1,    5,    3,    1,    1,    1,    3,    1,    3,
    1,    2,    3,    1,    1,    3,    2,    2,    1,    2,
    1,    2,    3,    3,    3,    4,    3,    3,    6,    1,
    2,    5,    1,    3,    1,    3,    1,    3,    3,    1,
    1,    3,    3,    3,    3,    3,    3,    3,    3,    1,
    3,    3,    3,    3,    3,    3,    1,    3,    3,    1,
    3,    3,    1,    3,    3,    1,    3,    3,    3,    3,
    3,    1,    3,    1,    2,    2,    2,    2,    2,    2,
    1,    3,    3,    3,    4,    4,    3,    4,    5,    5,
    1,    4,    1,    4,    4,    4,    3,    2,    4,    4,
    4,    3,    1,    7,    4,    4,    6,    1,    1,    1,
    1,    1,    1,    1,    2,    1,    3,    7,    3,    9,
    1,    4,    4,    3,    4,    1,    2,    1,    3,    1,
    1,    2,    2,    8,    2,    3,    2,    3,    1,    1,
    1,    3,    2,    2,    3,    2,    3,    3,    5,    5,
    1,    3,    6,    5,    1,    0,    1,    1,    2,    2,
    3,    1,    3,    3,    1,    1,    1,    2,    1,    1,
    3,    2,    1,    3,    1,    3,    2,    3,    3,    4,
    4,    4,    1,    2,    1,    3,    1,    3,    3,    4,
    1,    1,    2,    2,    2,    2,    5,    7,    5,    1,
    3,    3,    5,    3,    1,    2,    2,    3,    1,    1,
    1,    5,    1,    3,    2,    3,    1,    1,    1,    1,
    4,    2,    4,    2,    4,    2,    2,    1,    1,    2,
    2,    1,    1,    3,    1,    1,    3,    1,    3,    3,
    1,    3,    3,    5,    5,    2,    4,    4,    1,    1,
    0,    1,    2,    2,    2,    2,    2,    0,    2,    0,
    1,    2,    3,    2,    0,    2,    0,    1,    2,    3,
    1,    1,    1,    2,    2,    4,    4,    4,    5,    4,
    6,    6,    3,    1,    1,    1,    4,    3,    1,    1,
    1,    1,    1,    2,    0,    4,    2,    1,    3,    6,
    6,    6,    3,    1,    2,    0,    4,    2,    3,    1,
    0,    1,    1,    2,    0,    1,    1,    1,    1,    1,
    1,    1,    1,    2,    2,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    3,    3,    3,    5,    3,    3,    7,
    8,    5,    4,    5,    1,    1,    3,    1,    1,    3,
    1,    1,    2,    0,    1,    3,    0,    2,    3,    0,
    2,    1,    2,    1,    3,    1,    2,    7,    6,    1,
    2,    4,    6,    6,    2,    3,    1,    3,    2,    2,
    2,    0,    2,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,
};
final static short yydefred[] = {                         0,
  313,    0,    0,    0,    2,    0,  326,    0,    0,    0,
    0,  404,  405,    0,    0,    0,  458,  418,  419,  420,
  421,  422,  423,  424,  425,  426,  427,  428,  429,  430,
  431,  432,  433,  434,  435,  436,  437,  438,  439,  440,
  441,  442,  443,  444,  445,  446,  447,  448,  449,  450,
  451,  452,  453,  454,  455,  456,  457,  459,  460,  461,
  462,  463,  464,  465,  466,  467,  468,  469,  470,  471,
  472,  473,  474,  475,  476,  477,  478,  479,  480,  481,
  482,  483,  484,  485,  486,  487,  488,  489,  490,  491,
  492,  493,  494,  495,  496,  497,  498,  499,  500,  501,
    0,    0,    0,  290,  291,  292,    0,    0,  289,  293,
    0,    0,    0,    0,    0,  139,  218,  219,    0,    0,
    0,  140,  141,    0,    0,  191,  192,    0,    0,  220,
  327,  108,  109,  110,  272,  273,  114,    0,    0,    5,
    6,    0,    0,    0,    0,    0,    0,    7,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   11,   14,   19,    0,    0,    0,    0,    0,   35,
    0,    0,    0,    0,    0,    0,    0,   66,    0,    0,
    0,    0,    0,  103,  121,  112,  113,    0,  116,  126,
  128,  130,  131,    0,  314,    0,    0,    0,  323,    0,
    0,    0,    0,  227,    0,    0,    0,    0,    0,  194,
  195,  325,  324,    0,  285,    0,  284,  286,    0,  236,
    0,    0,  143,  144,  146,    4,    0,    0,    0,  205,
  193,    0,    0,  196,    0,   20,  132,    0,    0,    0,
    0,    0,    0,    0,    0,  253,    0,    0,    0,  233,
    0,    0,  183,    0,    0,    0,    0,    0,  229,  228,
    0,    0,  315,    0,    0,    0,  127,    0,    0,    0,
    0,  135,    0,    0,    0,   18,    0,   80,    0,    0,
    0,    0,    0,    0,    0,  169,    0,  175,    0,    0,
  274,  275,    0,   17,    8,    0,    0,    0,    0,    0,
  328,  329,  330,  331,  332,  333,  334,  335,  336,  337,
  338,  339,  340,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   98,  415,  406,  407,
  408,  409,  413,  410,  411,  414,  412,  417,    0,  416,
    0,  158,  115,    0,    0,    0,    0,    0,  133,    0,
    0,    0,  142,  145,    0,    0,    0,    0,    0,  189,
    0,  254,    0,  255,    0,    0,    0,    0,  238,  250,
  249,    0,    0,  241,    0,    0,  204,    0,    0,  162,
  211,    0,    0,  209,  207,  210,    0,  206,  188,  202,
    0,    0,    0,    0,    0,    0,    0,  136,  184,    0,
    0,    0,    0,  230,  231,    0,    0,    0,    0,    0,
  379,    0,    0,  124,    0,  372,  371,    0,    0,    0,
    0,    0,    0,    0,    0,  356,    0,  129,  147,    0,
    0,    0,  117,    0,  119,    0,  354,  355,   16,    0,
   13,    0,   30,   25,    0,   23,   24,   27,   28,    0,
    0,   83,    0,   97,    0,    0,    0,    0,   84,  321,
    0,   82,    0,  173,  166,    0,  165,    0,   36,   38,
   39,   43,   44,   45,   42,   46,   48,   47,   49,    0,
   52,   53,   54,   51,   55,    0,    0,    0,    0,    0,
    0,   69,   71,   67,   68,   70,   73,    0,    0,    0,
    0,  102,  317,   87,    0,    0,    0,    0,    0,  160,
    0,  159,  271,  259,    0,    0,    0,    0,    0,  137,
  365,    0,    0,    0,  256,    0,    0,    0,    0,    0,
    0,  288,    0,    0,    0,  237,    0,  240,    0,    0,
  190,    0,    0,  208,    0,   26,    0,  122,  375,  234,
  186,  155,    0,  152,    0,  381,    0,    0,    0,    0,
    0,    0,    0,    0,  391,  315,    0,  125,  373,    0,
    0,  123,    0,    0,    0,    3,    0,    0,    0,  179,
    0,    0,    0,    0,  176,    0,   31,    0,    0,    0,
   88,   96,   86,   85,   94,   95,    0,  105,    0,  101,
  164,   99,  100,    0,  106,  161,    0,    0,    0,    0,
  315,  266,  138,    0,  264,    0,  197,    0,  199,  287,
    0,    0,  239,  242,    0,  203,  163,    0,    0,    0,
    0,  150,  149,    0,    0,    0,    0,  395,    0,  397,
    0,    0,  262,    0,  257,    0,  369,    0,  378,    0,
  154,    0,    0,  358,  359,  182,  181,  180,    0,    0,
    0,    0,   90,   89,  174,    0,    0,    0,    0,    0,
  280,    0,  283,  366,    0,  269,    0,    0,  248,  247,
    0,    0,    0,   29,  376,  153,    0,    0,  400,    0,
  399,    0,  396,  392,    0,    0,  263,  363,    0,    0,
    0,    0,    0,    0,  178,    0,    0,  107,    0,    0,
    0,    0,    0,  252,    0,    0,    0,    0,  270,  198,
  245,  244,  212,    0,    0,  398,    0,    0,    0,  382,
    0,  384,    0,  370,    0,  301,    0,  300,  304,  357,
    0,    0,  118,    0,   32,  104,  281,    0,    0,  282,
    0,    0,  367,  364,  393,  394,  134,    0,  383,    0,
  387,    0,    0,    0,    0,    0,    0,  345,  346,  344,
  348,  351,  353,  342,  350,  341,  352,  349,  343,  347,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  385,  309,    0,    0,    0,    0,  303,    0,
    0,  362,  120,    0,    0,  277,  276,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  360,  278,    0,  389,    0,  302,  361,  279,  388,
};
final static short yydgoto[] = {                          3,
  258,  504,  168,  262,  169,  170,  171,    6,  172,  173,
  174,  175,  116,  324,  484,  117,  118,  178,  505,  179,
  180,  181,  182,  183,  184,  750,  185,  186,  187,  188,
  189,  190,  191,  492,  499,  192,  506,  193,  828,  367,
  194,  379,  259,  195,  196,  197,  198,  383,  199,  294,
  263,  593,  295,  588,  266,  454,  455,  608,  200,  277,
  201,  202,  203,  204,  389,  256,  450,  602,  768,  390,
  560,  391,  563,  122,  123,  264,  124,  384,  424,  260,
  508,  298,  125,  126,  127,  241,  128,  129,  231,  232,
  242,  425,  426,  130,  436,  412,  413,  414,  415,  769,
  392,  603,  655,  554,  604,  564,  656,  756,  829,  711,
  739,  751,  778,  807,  779,  456,  457,  131,  822,  466,
  694,  688,  715,  758,  589,  451,  771,  772,  773,  599,
  729,  452,  679,  680,
};
final static short yysindex[] = {                      -209,
    0,15268,    0,10210,    0, -195,    0, -161, -124,13523,
16302,    0,    0,16302,  101,   99,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
16302,13242,13242,    0,    0,    0,17729,16302,    0,    0,
16169,  -49,   -5,    3,  -49,    0,    0,    0,  101,  187,
  361,    0,    0,    0,  150,    0,    0,13242,  344,    0,
    0,    0,    0,    0,    0,    0,    0,  477,13523,    0,
    0,14253,15268,  -49,14763,  101,15902,    0,  101,  267,
16302,14899,  101,12740,  380,  101,  267,12740,  -49,11367,
12740,13242,12740,12740,12740,  -49,  -49,  415,11731,  -49,
  342,    0,    0,    0, 1315,   -2,   32,  -25,   84,    0,
  172,  487,  416,   31,  321,  382,   33,    0,  119,  263,
  519,  -32,17461,    0,    0,    0,    0,  434,    0,    0,
    0,    0,    0,   25,    0,  440,  443,  504,    0,  518,
  516,15268,16169,    0,  698,13523,15902,15902,16302,    0,
    0,    0,    0,  520,    0,  -71,    0,    0,12878,    0,
  506,  526,    0,    0,    0,    0,  178,16169,  722,    0,
    0,  481,13242,    0,15404,    0,    0,  504,13523,16302,
   99,  415, -112,    0,  187,    0,  267,15268,  267,    0,
15268,  207,    0,  349,  213,  267,13523,16302,    0,    0,
  161,  528,    0,  516,  -49,  366,    0,  267,  -23,  456,
  528,    0,  -36,11224,  621,    0,  -36,    0,  -36,  -36,
  -36,10353,   70,  578,   59,    0,  630,    0,10717,  554,
    0,    0,   -6,    0,    0,10860,11224,11874,12238,11367,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,11367,11367,11367,14034,16791,16169,17595,
  -49,12238,12238,12238,12238,15268,15268,12238,12238,12238,
12238,12238,12238,12238,12238,12238,12238,12238,12238,12238,
12238,12238,12238,12238,12238,12238,12238,12238,12238,  -49,
  -49,17061,17595,  -49,17595,  -49,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  -16,    0,
  192,    0,    0,  494,12238,17595,  -49,17595,    0,  -46,
  414,  414,    0,    0,16169,  -49,17595,  -49,16169,    0,
  -71,    0,  409,    0,16169,17595,  -49, 8099,    0,    0,
    0,   34,   50,    0,  265,  586,    0,15268,  -71,    0,
    0,  -49,  -49,    0,    0,    0,  722,    0,    0,    0,
  504,  516,12740,11367,  439,  -49,  679,    0,    0,15268,
12238,15268,  451,    0,    0,15268,17595,  -49,17595,  455,
    0,  267,  -49,    0,  -79,    0,    0,11224,    0,  695,
12238,  439,15268,  101,  101,    0,  -49,    0,    0,16035,
15268,  -49,    0,  -49,    0,  -49,    0,    0,    0,  342,
    0,   10,    0,    0,   84,    0,    0,    0,    0,16926,
  415,    0,  710,    0,    0,17595,17595,  415,    0,    0,
  -71,    0,  710,    0,    0,  670,    0,  723,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  -36,
    0,    0,    0,    0,    0,  372,  372,  382,  382,   33,
   33,    0,    0,    0,    0,    0,    0,  267,  728,  415,
  710,    0,    0,    0,  730,  710,  682,  -49,  267,    0,
  192,    0,    0,    0,  740,  751,  756,15268,  -49,    0,
    0,12238,  267,  509,    0,  -71,  766,  774,  741,  -71,
  -71,    0,  748,  110,15268,    0,  -28,    0,15268,15404,
    0,  691,15268,    0,    6,    0,15268,    0,    0,    0,
    0,    0,  267,    0,   54,    0,  803,16656,  792,  804,
12238,  729,  509,  807,    0,    0,  267,    0,    0,  -49,
  267,    0,  564,  736,  736,    0,13523,16302,16302,    0,
  570,  575,10353,  438,    0,    0,    0,  445,17595,17595,
    0,    0,    0,    0,    0,    0,  -49,    0,17461,    0,
    0,    0,    0,  822,    0,    0,  -49,  -40,  -49,  267,
    0,    0,    0,12238,    0,  825,    0,  -49,    0,    0,
12238,12238,    0,    0,  117,    0,    0,  -49,11367,  267,
  439,    0,    0,  -49,  813,16169,  813,    0,   56,    0,
  813,  -49,    0,  -49,    0,12238,    0,   14,    0,  448,
    0,12238,  -49,    0,    0,    0,    0,    0,12238,15268,
  -49,  -49,    0,    0,    0,  -49,  267,  834,16169,16169,
    0,  837,    0,    0,  607,    0,12238,  841,    0,    0,
12238,12238,  761,    0,    0,    0,  852,16169,    0,  -71,
    0,16656,    0,    0,  853,  607,    0,    0,  -49,15768,
  736,  405,  805,  611,    0,  861,  267,    0,  -40,  -71,
  862,  -52,  -40,    0,  211,  -49,  613,  782,    0,    0,
    0,    0,    0,  813,  -71,    0,  813,  784,  614,    0,
  -49,    0,  267,    0,    0,    0,    5,    0,    0,    0,
  793,  -49,    0,12238,    0,    0,    0,16169,16169,    0,
17195,17195,    0,    0,    0,    0,    0,17329,    0, -172,
    0,  439,  627,16169,  -49,16169,  267,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
17595,11367,  795,  828,  -71,  862,  -49,  883,  267,  267,
  -49,  886,    0,    0,  862,15268,  888,  -50,    0,  710,
  -49,    0,    0,  890,  -49,    0,    0,  891,  -49,  -15,
16169,  -49,  808,  -40,  894,  813,  895,  267,  862,  818,
    0,    0,  -40,    0,  813,    0,    0,    0,    0,
};
final static short yyrindex[] = {                      1583,
    0,  524,    0,   15,    0, 1183,    0,    0,    0,    0,
    0,    0,    0,    0,    0, 1347,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  524,  524,    0,    0,    0,    0,    0,    0,    0,
    0,12376,    0,    0,  938,    0,    0,    0,    0, 4420,
    0,    0,    0, 1742, 4688,    0,    0,  524, 6801,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  524,  524,13374,  524,    0,    0,    0,    0,    0,
    0,  524,    0,  524,    0,    0,    0,  524, 9846,  524,
  524,  524,  524,  524,  524, 8271, 9160, 2445,  125,  604,
   40,    0,    0,    0,  551,    0,    0, 4637, 3592,    0,
 2276, 2715, 3154,  941, 6371, 5904, 5589,    0, 5415, 3497,
 4463,    0, 3761,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  323,    0,    0,    0, 6862,    0,    0,
 7033,  524,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0, 7076,    0, 7247,    0,    0,  228,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  524,    0,  524,    0,    0, 1103,    0,    0,
  876, 1975,    0,  826,  997,    0,    0,   51,    0,    0,
  524,  656,    0,    0,  827,    0,    0,    0,    0,    0,
  129,    0,    0, 1343, 9321,    0,    0,    0,    0,    0,
    0,    0, 4726,  524,    0,    0, 4899,    0, 5152, 5325,
 5361,  228,   71,    0,    0,    0,    0,    0,  524,    0,
    0,    0, 9846,    0,    0,   79,  903,  524,  524,  524,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  524,  524,  524,    0,    0,    0,    0,
 9685,  524,  524,  524,  524,  524,  524,  524,  524,  524,
  524,  524,  524,  524,  524,  524,  524,  524,  524,  524,
  524,  524,  524,  524,  524,  524,  524,  524,  524,13374,
 9321,    0,    0, 9321,    0, 9685,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,13374,    0,
    0,    0,    0,    0,  524,    0,13743,    0,    0,    0,
  -73,  -67,    0,    0,    0,13743,    0,13884,    0,    0,
 7287,    0,  129,    0,    0,    0, 9685,    0,    0,    0,
    0,  463,  860,    0,    0, 7344,    0,  524, 7461,    0,
    0, 9846,14394,    0,    0,    0,    0,    0,    0,    0,
 1009, 1022,  524,  524, 2620,  394,  171,    0,    0,  524,
  524,  524, 2884,    0,    0,  524,    0,16524,    0,  -34,
    0,  904,   66,    0, 3059,    0,    0,  155,  495,    0,
  524, 2620,  524,    0,    0,    0, 9846,    0,    0,    0,
  524, 9846,    0, 7907,    0, 8635,    0,    0,    0,  532,
    0, 9846,    0,    0, 6026,    0,    0,    0,    0,    0,
 3936,    0,    0,    0, 4025,    0,    0, 3322,    0,    0,
 6765,    0,    0,    0,    0,    0,    0,  474,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0, 4637,
    0,    0,    0,    0,    0, 6545, 6581, 6296, 6335, 5807,
 5847,    0,    0,    0,    0,    0,    0,    0,    0,  915,
    0,    0,    0,    0,    0,    0,    0, 9321,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  524,  367,    0,
    0,  524,    0,  -14,    0, 7515,    0,    0,    0, 7562,
 7737,    0,    0,    2,  524,    0,    0,    0,  524,  524,
    0,    0,  524,    0,    0,    0,  524,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  919,    0,    0,
  524,    0,  833,   77,    0,    0,    0,    0,    0,  538,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   82,  524,    0,    0,  577,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0, 8796,    0, 4200,    0,
    0,    0,    0,    0,    0,    0,13743,  245,13743,    0,
    0,    0,    0,  524,    0,   58,    0,13743,    0,    0,
  524,  524,    0,    0,  286,    0,    0,  840,  524,    0,
 2620,    0,    0,16524,   80,    0,   80,    0,  927,    0,
   17,16524,    0,  -56,    0,  524,    0,    0,    0,    0,
    0,  524,  429,    0,    0,    0,    0,    0,  524,  524,
 9846, 9846,    0,    0,    0,13374,    0,    0,    0,    0,
    0,    0,    0,    0,   -8,    0,  524,    0,    0,    0,
  524,  524,    0,    0,    0,    0,    0,    0,    0,   87,
    0,    0,    0,    0,    0,  -21,    0,    0,   66,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  245,   38,
  346,  348,  245,    0,    0,  367,  847,    0,    0,    0,
    0,    0,    0,   17,   86,    0,   17,    0,    0,    0,
  840,    0,  -42,    0, 2006,    0,  851,    0,    0,    0,
    0,  840,    0,  524,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   12,
    0,    0, 2181,    0,13743,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  524,    0,    0,   62,  379,13743,    0,    0,    0,
16524,    0,    0,    0,  857,   67,    0,  858,    0,    0,
  840,    0,    0,    0,13743,    0,    0,    0,16524,  851,
    0,  840,    0,  871,    0,   65,    0,    0,  880,    0,
    0,    0,  871,    0,   65,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    1,   36,  639,  512,    0,    0,  669, -168,  677, -132,
  838,    0,  -10,    0,    0,  503,  589, 2035,  303,  697,
 -323, 1653,    0,    0,  399,  -24,  436,  453,  472,  465,
  648,  388,    0,  678, -310,  218, -259,    0, -298, -320,
    0,  371, -349, -123, -213,   11,  437,    0,  182,  738,
 -147, -407,    0, -418,  -81,  733,  236,  212,    0,  278,
  424,    0,   21,    0,    0, -144,    0,    0,    0,    0,
    0,    0,  629,    0,    0,    0,  136,    0, -324,  -93,
  122,  548, -239,  618,    0,    0,    0,    0,    0,  225,
    0,  605,    0,    0,    0,  780,    0,  462,    0,  325,
    0,    0,  441,   13,    0,    0,    0,    0,  249, -297,
  652, -243,    0,  196, -747,  273, -113, -189,    0,  308,
 -416,  336,    0,    0, -429,    0,    0,  252,    0, -286,
 -151,    0,    0,  324,
};
final static int YYTABLESIZE=18124;

//These two tables are not statically initialized, but rather
//initialized on first use, so that a failure to initialize
//them can successfully report the problem.
static private short[] yytable = null;
static private short[] yycheck = null;
/** Ensures that yytable and yycheck are initialized. */
static private void initTables() {
    if (null != yycheck) {
        return;
    }
    try {
        String rName = "org/erights/e/elang/syntax/ParserTables.data";
        InputStream inp = 
          EParser.class.getClassLoader().getResourceAsStream(rName);
        if (null == inp) {
            T.fail(rName + " not found");
        }
        ObjectInput obInp = new ObjectInputStream(inp);
        yytable = (short[])obInp.readObject();
        yycheck = (short[])obInp.readObject();
        long hash = EYaccFixer.checkhash(yytable, yycheck);
        if (hash != 9063901177920640481L) {
            T.fail(rName + " bad checkhash: " +
                                       hash);
        }
    } catch (Exception ex) {
        throw new EBacktraceException(ex,
            "# initing parser");
    }
}

final static short YYFINAL=3;
final static short YYMAXTOKEN=457;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'!'",null,null,"'$'","'%'","'&'",null,"'('","')'","'*'","'+'",
"','","'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,"':'",
"';'","'<'",null,"'>'","'?'","'@'",null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'['",null,"']'","'^'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,"'{'","'|'","'}'","'~'",null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,"EOL","EOTLU","LiteralInteger",
"LiteralFloat64","LiteralChar","LiteralString","LiteralTwine","ID","VerbAssign",
"QuasiOpen","QuasiClose","DollarIdent","AtIdent","DollarOpen","AtOpen","URI",
"DocComment","AS","BIND","BREAK","CATCH","CONTINUE","DEF","ELSE","ESCAPE",
"EXIT","EXTENDS","FINALLY","FN","FOR","GUARDS","IF","IMPLEMENTS","IN",
"INTERFACE","MATCH","META","METHOD","PRAGMA","RETURN","SWITCH","TO","TRY","VAR",
"VIA","WHEN","WHILE","_","ACCUM","INTO","MODULE","ON","SELECT","THROWS","THUNK",
"ABSTRACT","AN","ASSERT","ATTRIBUTE","BE","BEGIN","BEHALF","BELIEF","BELIEVE",
"BELIEVES","CASE","CLASS","CONST","CONSTRUCTOR","DATATYPE","DECLARE","DEFAULT",
"DEFINE","DEFMACRO","DELEGATE","DELICATE","DEPRECATED","DISPATCH","DO",
"ENCAPSULATE","ENCAPSULATED","ENCAPSULATES","END","ENSURE","ENUM","EVENTUAL",
"EVENTUALLY","EXPORT","FACET","FORALL","FUN","FUNCTION","GIVEN","HIDDEN",
"HIDES","INLINE","KNOW","KNOWS","LAMBDA","LET","METHODS","NAMESPACE","NATIVE",
"OBEYS","OCTET","ONEWAY","OPERATOR","PACKAGE","PRIVATE","PROTECTED","PUBLIC",
"RAISES","RELIANCE","RELIANT","RELIES","RELY","REVEAL","SAKE","SIGNED","STATIC",
"STRUCT","SUCHTHAT","SUPPORTS","SUSPECT","SUSPECTS","SYNCHRONIZED","THIS",
"TRANSIENT","TRUNCATABLE","TYPEDEF","UNSIGNED","UNUM","USES","USING","UTF8",
"UTF16","VIRTUAL","VOLATILE","WSTRING","OpLAnd","OpLOr","OpSame","OpNSame",
"OpButNot","OpLeq","OpABA","OpGeq","OpThru","OpTill","OpAsl","OpAsr","OpFlrDiv",
"OpMod","OpPow","OpAss","OpAssAdd","OpAssAnd","OpAssAprxDiv","OpAssFlrDiv",
"OpAssAsl","OpAssAsr","OpAssRemdr","OpAssMod","OpAssMul","OpAssOr","OpAssPow",
"OpAssSub","OpAssXor","Send","OpWhen","MapsTo","MatchBind","MisMatch","OpScope",
"AssignExpr","CallExpr","DefineExpr","EscapeExpr","HideExpr","IfExpr",
"LiteralExpr","NounExpr","ObjectExpr","QuasiLiteralExpr","QuasiPatternExpr",
"MetaStateExpr","MetaContextExpr","SeqExpr","SlotExpr","MetaExpr","CatchExpr",
"FinallyExpr","FinalPattern","SlotPattern","ListPattern","IgnorePattern",
"QuasiLiteralPatt","QuasiPatternPatt","EScript","EMethod","EMatcher",
};
final static String yyrule[] = {
"$accept : start",
"start : br",
"start : eExpr",
"start : br MODULE litString EOL eExpr",
"start : MatchBind pattern br",
"ejector : BREAK",
"ejector : CONTINUE",
"ejector : RETURN",
"eExpr : br seqs br",
"seqs : seq",
"seqs : seqs EOLs seq",
"seq : chunk",
"seq : seq ';'",
"seq : seq ';' chunk",
"chunk : assign",
"chunk : ejector",
"chunk : ejector '(' ')'",
"chunk : ejector assign",
"chunk : '^' assign",
"chunk : oFuncType",
"chunk : DocComment oFuncType",
"assign : cond",
"assign : DEF noun",
"assign : cond OpAss assign",
"assign : cond assignop assign",
"assign : cond VerbAssign nAssign",
"assign : DEF pattern OpAss assign",
"assign : bindNamer OpAss assign",
"assign : varNamer OpAss assign",
"assign : DEF pattern EXIT postfix OpAss assign",
"nAssign : assign",
"nAssign : '(' ')'",
"nAssign : '(' eExpr ',' args ')'",
"cond : condAnd",
"cond : cond OpLOr condAnd",
"condAnd : comp",
"condAnd : condAnd OpLAnd comp",
"comp : order",
"comp : order OpSame order",
"comp : order OpNSame order",
"comp : conjunction",
"comp : disjunction",
"comp : order '^' order",
"comp : order OpButNot order",
"comp : order MatchBind pattern",
"comp : order MisMatch pattern",
"conjunction : order '&' order",
"conjunction : conjunction '&' order",
"disjunction : order '|' order",
"disjunction : disjunction '|' order",
"order : interval",
"order : interval '<' interval",
"order : interval OpLeq interval",
"order : interval OpABA interval",
"order : interval OpGeq interval",
"order : interval '>' interval",
"order : postfix ':' guard",
"interval : shift",
"interval : shift OpThru shift",
"interval : shift OpTill shift",
"shift : add",
"shift : shift OpAsl add",
"shift : shift OpAsr add",
"add : mult",
"add : add '+' mult",
"add : add '-' mult",
"mult : pow",
"mult : mult '*' pow",
"mult : mult '/' pow",
"mult : mult OpFlrDiv pow",
"mult : mult '%' pow",
"mult : mult OpMod pow",
"pow : prefix",
"pow : prefix OpPow prefix",
"prefix : postfix",
"prefix : '!' postfix",
"prefix : '~' postfix",
"prefix : '&' postfix",
"prefix : OpLAnd postfix",
"prefix : '*' postfix",
"prefix : '-' prim",
"postfix : call",
"postfix : postfix '.' vcurry",
"postfix : postfix Send vcurry",
"postfix : postfix OpScope prop",
"postfix : postfix OpScope '&' prop",
"postfix : postfix OpScope OpLAnd prop",
"postfix : metaoid OpScope prop",
"postfix : postfix Send OpScope prop",
"postfix : postfix Send OpScope '&' prop",
"postfix : postfix Send OpScope OpLAnd prop",
"call : prim",
"call : call '(' argList ')'",
"call : control",
"call : postfix '.' verb parenArgs",
"call : postfix '[' argList ']'",
"call : postfix Send verb parenArgs",
"call : postfix Send parenArgs",
"call : metaoid parenArgs",
"call : metaoid '.' verb parenArgs",
"call : metaoid '[' argList ']'",
"call : metaoid Send verb parenArgs",
"call : metaoid Send parenArgs",
"call : accumExpr",
"control : call '(' argList ')' sepWord paramList body",
"control : prim FN paramList body",
"control : control sepWord paramList body",
"control : control sepWord '(' argList ')' body",
"literal : LiteralInteger",
"literal : LiteralFloat64",
"literal : LiteralChar",
"literal : litString",
"prim : literal",
"prim : nounExpr",
"prim : URI",
"prim : quasiParser quasiExpr",
"prim : parenExpr",
"prim : '[' exprList ']'",
"prim : '[' eExpr FOR iterPattern IN iterable ']'",
"prim : '[' maps ']'",
"prim : '[' eExpr MapsTo eExpr FOR iterPattern IN iterable ']'",
"prim : body",
"prim : ESCAPE pattern body optHandler",
"prim : WHILE test body optHandler",
"prim : SWITCH parenExpr caseList",
"prim : TRY body catchList finallyClause",
"prim : forExpr",
"prim : WHEN whenRest",
"prim : ifExpr",
"prim : SELECT parenExpr caseList",
"prim : docodef",
"docodef : postdocodef",
"docodef : DocComment postdocodef",
"postdocodef : defName script",
"postdocodef : INTERFACE oName guards iDeclTail '{' br mTypeList '}'",
"postdocodef : THUNK body",
"postdocodef : FN paramList body",
"script : oDeclTail vTable",
"script : funcHead auditors body",
"nounExpr : noun",
"nounExpr : dollarHole",
"nounExpr : atHole",
"dollarHole : DollarOpen LiteralInteger '}'",
"dollarHole : '$' LiteralInteger",
"dollarHole : '$' '$'",
"atHole : AtOpen LiteralInteger '}'",
"atHole : '@' LiteralInteger",
"parenExpr : '(' eExpr ')'",
"ifExpr : IF test body",
"ifExpr : IF test body ELSE ifExpr",
"ifExpr : IF test body ELSE body",
"test : parenExpr",
"test : parenExpr MatchBind pattern",
"forExpr : FOR matchIterPattern IN iterable body optHandler",
"forExpr : WHEN iterPattern IN iterable body",
"iterable : comp",
"quasiParser :",
"quasiParser : ident",
"quasiExpr : QuasiClose",
"quasiExpr : innerExprs QuasiClose",
"innerExprs : QuasiOpen innerExpr",
"innerExprs : innerExprs QuasiOpen innerExpr",
"innerExpr : DollarIdent",
"innerExpr : DollarOpen eExpr '}'",
"parenArgs : '(' argList ')'",
"argList : emptyBr",
"argList : args",
"args : exprs",
"args : exprs ','",
"exprList : emptyBr",
"exprList : eExpr",
"exprList : exprs ',' eExpr",
"exprList : exprs ','",
"exprs : eExpr",
"exprs : exprs ',' eExpr",
"maps : map",
"maps : maps ',' map",
"maps : maps ','",
"map : eExpr MapsTo eExpr",
"map : br MapsTo nounExpr",
"map : br MapsTo '&' nounExpr",
"map : br MapsTo OpLAnd nounExpr",
"map : br MapsTo DEF noun",
"matchIterPattern : iterPattern",
"matchIterPattern : MATCH iterPattern",
"iterPattern : pattern",
"iterPattern : pattern MapsTo pattern",
"pattern : subPattern",
"pattern : subPattern '?' prim",
"pattern : VIA parenExpr pattern",
"pattern : metaoid parenExpr MapsTo pattern",
"subPattern : namer",
"subPattern : ignorer",
"subPattern : quasiParser quasiPattern",
"subPattern : OpSame prim",
"subPattern : OpNSame prim",
"subPattern : compOp prim",
"subPattern : IN nounExpr '(' paramList ')'",
"subPattern : IN nounExpr '.' verb '(' paramList ')'",
"subPattern : IN nounExpr '[' paramList ']'",
"subPattern : listPattern",
"subPattern : '[' mapPatternList ']'",
"subPattern : listPattern '+' subPattern",
"subPattern : '[' mapPatternList ']' '|' subPattern",
"listPattern : '[' patternList ']'",
"quasiPattern : QuasiClose",
"quasiPattern : innerThings QuasiClose",
"innerThings : QuasiOpen innerThing",
"innerThings : innerThings QuasiOpen innerThing",
"innerThing : innerExpr",
"innerThing : innerPattern",
"innerPattern : AtIdent",
"innerPattern : AtOpen br pattern br '}'",
"ignorer : _",
"ignorer : _ ':' guard",
"ignorer : ':' guard",
"namer : nounExpr ':' guard",
"namer : nounExpr",
"namer : bindNamer",
"namer : varNamer",
"namer : slotNamer",
"bindNamer : BIND noun ':' guard",
"bindNamer : BIND noun",
"varNamer : VAR nounExpr ':' guard",
"varNamer : VAR nounExpr",
"slotNamer : '&' nounExpr ':' guard",
"slotNamer : '&' nounExpr",
"slotNamer : OpLAnd nounExpr",
"oName : nounExpr",
"oName : _",
"oName : BIND noun",
"oName : VAR nounExpr",
"oName : litString",
"paramList : emptyBr",
"paramList : br params br",
"params : patterns",
"patternList : emptyBr",
"patternList : br patterns br",
"patterns : pattern",
"patterns : patterns ',' pattern",
"mapPatternList : br mapPatterns br",
"mapPatterns : mapPattern",
"mapPatterns : mapPatterns ',' mapPattern",
"mapPattern : key MapsTo pattern",
"mapPattern : key MapsTo pattern OpAss order",
"mapPattern : key MapsTo pattern DEFAULT order",
"mapPattern : MapsTo namer",
"mapPattern : MapsTo namer OpAss order",
"mapPattern : MapsTo namer DEFAULT order",
"key : parenExpr",
"key : literal",
"doco :",
"doco : DocComment",
"defName : DEF oName",
"defName : BIND noun",
"defName : VAR nounExpr",
"oDeclTail : extends auditors",
"iDeclTail : iExtends impls",
"extends :",
"extends : EXTENDS base",
"iExtends :",
"iExtends : iExtendsList",
"iExtendsList : EXTENDS base",
"iExtendsList : iExtendsList ',' base",
"auditors : optAs impls",
"optAs :",
"optAs : AS base",
"impls :",
"impls : implsList",
"implsList : IMPLEMENTS base",
"implsList : implsList ',' base",
"base : order",
"litString : LiteralString",
"litString : LiteralTwine",
"litString : litString LiteralString",
"litString : litString LiteralTwine",
"method : doco TO methHead body",
"method : doco METHOD methHead body",
"methHead : '(' paramList ')' resultGuard",
"methHead : verb '(' paramList ')' resultGuard",
"funcHead : '(' paramList ')' resultGuard",
"funcHead : TO verb '(' paramList ')' resultGuard",
"funcHead : '.' verb '(' paramList ')' resultGuard",
"matcher : MATCH pattern body",
"guard : nounExpr",
"guard : URI",
"guard : parenExpr",
"guard : guard '[' argList ']'",
"guard : guard OpScope prop",
"compOp : '<'",
"compOp : OpLeq",
"compOp : OpABA",
"compOp : OpGeq",
"compOp : '>'",
"resultGuard : ':' guard",
"resultGuard :",
"resultGuard : ':' guard THROWS throws",
"resultGuard : THROWS throws",
"throws : guard",
"throws : throws ',' guard",
"whenRest : '(' exprList ')' br OpWhen whenTail",
"whenRest : '(' exprList ')' br OpWhen whenRest",
"whenTail : oName '(' patternList ')' whenGuard whenBody",
"whenTail : oName whenGuard whenBody",
"whenTail : whenBody",
"whenGuard : ':' guard",
"whenGuard :",
"whenGuard : ':' guard THROWS throws",
"whenGuard : THROWS throws",
"whenBody : body catches finallyClause",
"whenBody : body",
"br :",
"br : EOLs",
"EOLs : EOL",
"EOLs : EOLs EOL",
"emptyList :",
"emptyBr : br",
"verb : ident",
"verb : litString",
"vcurry : ident",
"vcurry : litString",
"prop : ident",
"prop : litString",
"noun : ident",
"noun : OpScope ident",
"noun : OpScope LiteralString",
"ident : ID",
"ident : reserved",
"assignop : OpAssAdd",
"assignop : OpAssAnd",
"assignop : OpAssAprxDiv",
"assignop : OpAssFlrDiv",
"assignop : OpAssAsl",
"assignop : OpAssAsr",
"assignop : OpAssRemdr",
"assignop : OpAssMod",
"assignop : OpAssMul",
"assignop : OpAssOr",
"assignop : OpAssPow",
"assignop : OpAssSub",
"assignop : OpAssXor",
"assignableop : '+'",
"assignableop : '&'",
"assignableop : '/'",
"assignableop : OpFlrDiv",
"assignableop : OpAsl",
"assignableop : OpAsr",
"assignableop : '%'",
"assignableop : OpMod",
"assignableop : '*'",
"assignableop : '|'",
"assignableop : OpPow",
"assignableop : '-'",
"assignableop : '^'",
"body : '{' br '}'",
"body : '{' eExpr '}'",
"accumExpr : ACCUM postfix accumulator",
"accumulator : FOR iterPattern IN iterable accumBody",
"accumulator : IF test accumBody",
"accumulator : WHILE test accumBody",
"accumBody : '{' br _ assignableop assign br '}'",
"accumBody : '{' br _ '.' verb parenArgs br '}'",
"accumBody : '{' br accumulator br '}'",
"caseList : '{' br matchList '}'",
"vTable : '{' br methodList vMatchList '}'",
"vTable : matcher",
"methodList : emptyList",
"methodList : methodList method br",
"vMatchList : matchList",
"matchList : emptyList",
"matchList : matchList matcher br",
"catchList : emptyList",
"catchList : catches",
"catches : catchList catcher",
"optHandler :",
"optHandler : catcher",
"catcher : CATCH pattern body",
"finallyClause :",
"finallyClause : FINALLY body",
"oFuncType : INTERFACE oName funcType",
"guards :",
"guards : GUARDS pattern",
"mTypeList : emptyList",
"mTypeList : mTypes br",
"mTypes : mType",
"mTypes : mTypes EOLs mType",
"mType : mTypeHead",
"mType : mTypeHead body",
"mTypeHead : doco TO verb '(' pTypeList ')' optType",
"mTypeHead : doco TO '(' pTypeList ')' optType",
"funcType : funcTypeHead",
"funcType : funcTypeHead body",
"funcTypeHead : '(' pTypeList ')' optType",
"funcTypeHead : TO verb '(' pTypeList ')' optType",
"funcTypeHead : '.' verb '(' pTypeList ')' optType",
"pTypeList : br emptyList",
"pTypeList : br pTypes br",
"pTypes : pType",
"pTypes : pTypes ',' pType",
"pType : noun optType",
"pType : _ optType",
"pType : ':' guard",
"optType :",
"optType : ':' guard",
"metaoid : META",
"metaoid : PRAGMA",
"sepWord : CATCH",
"sepWord : ELSE",
"sepWord : ESCAPE",
"sepWord : FINALLY",
"sepWord : GUARDS",
"sepWord : IN",
"sepWord : THUNK",
"sepWord : FN",
"sepWord : TRY",
"sepWord : ID",
"sepWord : reserved",
"sepWord : OpWhen",
"reserved : ABSTRACT",
"reserved : AN",
"reserved : ASSERT",
"reserved : ATTRIBUTE",
"reserved : BE",
"reserved : BEGIN",
"reserved : BEHALF",
"reserved : BELIEF",
"reserved : BELIEVE",
"reserved : BELIEVES",
"reserved : CASE",
"reserved : CLASS",
"reserved : CONST",
"reserved : CONSTRUCTOR",
"reserved : DATATYPE",
"reserved : DECLARE",
"reserved : DEFAULT",
"reserved : DEFINE",
"reserved : DEFMACRO",
"reserved : DELICATE",
"reserved : DEPRECATED",
"reserved : DISPATCH",
"reserved : DO",
"reserved : ENCAPSULATE",
"reserved : ENCAPSULATED",
"reserved : ENCAPSULATES",
"reserved : END",
"reserved : ENSURE",
"reserved : ENUM",
"reserved : EVENTUAL",
"reserved : EVENTUALLY",
"reserved : EXPORT",
"reserved : FACET",
"reserved : FORALL",
"reserved : FUN",
"reserved : FUNCTION",
"reserved : GIVEN",
"reserved : HIDDEN",
"reserved : HIDES",
"reserved : INLINE",
"reserved : INTO",
"reserved : KNOW",
"reserved : KNOWS",
"reserved : LAMBDA",
"reserved : LET",
"reserved : METHODS",
"reserved : NAMESPACE",
"reserved : NATIVE",
"reserved : OBEYS",
"reserved : OCTET",
"reserved : ONEWAY",
"reserved : OPERATOR",
"reserved : PACKAGE",
"reserved : PRIVATE",
"reserved : PROTECTED",
"reserved : PUBLIC",
"reserved : RAISES",
"reserved : RELIANCE",
"reserved : RELIANT",
"reserved : RELIES",
"reserved : RELY",
"reserved : REVEAL",
"reserved : SAKE",
"reserved : SIGNED",
"reserved : STATIC",
"reserved : STRUCT",
"reserved : SUCHTHAT",
"reserved : SUPPORTS",
"reserved : SUSPECT",
"reserved : SUSPECTS",
"reserved : SYNCHRONIZED",
"reserved : THIS",
"reserved : TRANSIENT",
"reserved : TRUNCATABLE",
"reserved : TYPEDEF",
"reserved : UNSIGNED",
"reserved : UNUM",
"reserved : USES",
"reserved : USING",
"reserved : UTF8",
"reserved : UTF16",
"reserved : VIRTUAL",
"reserved : VOLATILE",
"reserved : WSTRING",
};

//#line 1586 "e.y"


/**
 *
 */
static public final StaticMaker EParserMaker =
    StaticMaker.make(EParser.class);

/**
 * caches previous simple parses (as is used for quasi-parsing)
 */
static private final IdentityCacheTable OurCache =
  new IdentityCacheTable(ENode.class, 100);

static private final class ParseFunc implements OneArgFunc, DeepPassByCopy {

    static private final long serialVersionUID = 8761482410783169702L;

    ParseFunc() {}

    public Object run(Object arg) {
        return EParser.run((Twine)arg, false);
    }

    /**
     * XXX We only say we implement this, but don't really, since we really
     * only need to be DeepFrozen; but currently the only way to declare
     * ourselves to be DeepFrozen is to claim to be DeepPassByCopy.
     */
    public Object[] getSpreadUncall() {
        T.fail("XXX not yet implemented");
        return new Object[0]; //make compiler happy
    }
}

static private final Memoizer OurMemoizer = new Memoizer(new ParseFunc(), 100);

/**
 *
 */
static private final ConstMap DefaultProps =
  ConstMap.fromProperties(System.getProperties());



/** contains all the tokens after yylval */
private final LexerFace myLexer;

/**
 * Do we escape after parsing only one expression, or do we parse the
 * entire input?
 */
private final boolean myOnlyOneExprFlag;

/**
 * Where the result is stored by the top-level productions
 */
private ENode myOptResult;

/** receives parsing events */
private final EBuilder b;


/**
 *
 */
static public EParser make(LexerFace lexer, TextWriter warns) {
    return make(null, lexer, warns, false, false);
}

/**
 *
 */
static public EParser make(ConstMap props, LexerFace lexer, TextWriter warns) {
    return make(props, lexer, warns, false, false);
}

/**
 *
 */
static public EParser make(ConstMap optProps,
                           LexerFace lexer,
                           TextWriter warns,
                           boolean debugFlag,
                           boolean onlyOneExprFlag) {
    if (null == optProps) {
        optProps = DefaultProps;
    }
    return new EParser(new ENodeBuilder(optProps, lexer, warns),
                       lexer,
                       debugFlag,
                       onlyOneExprFlag);
}

/**
 *
 */
public EParser(ENodeBuilder builder,
               LexerFace lexer,
               boolean debugFlag,
               boolean onlyOneExprFlag) {
    b = builder;
    initTables();
    myLexer = lexer;
    yydebug = debugFlag;
    myOnlyOneExprFlag = onlyOneExprFlag;
    myOptResult = null;
}

/**
 * For use as from E as a quasi-literal parser.
 * <p>
 * Transparently caches the result
 *
 * @param sourceCode The source code itself, not the location of
 * the source code
 */
static public ENode valueMaker(Twine sourceCode) {
    ENode result = (ENode)OurCache.fetch(sourceCode, ValueThunk.NULL_THUNK);
    if (null == result) {
        result = run(sourceCode, true);
        OurCache.put(sourceCode, result);
    }
    return result;
}

/**
 * For use from E as a quasi-pattern parser.
 * <p>
 * Just delegates to valueMaker/1.
 *
 * @param sourceCode The source code itself, not the location of
 * the source code
 */
static public ENode matchMaker(Twine sourceCode) {
    return valueMaker(sourceCode);
}


/**
 * For simple string -> expression parsing, especially for use from E
 *
 * @param sourceCode The source code itself, not the location of
 * the source code
 */
static public ENode run(Twine sourceCode) {
    return (ENode)OurMemoizer.run(sourceCode);
}

/**
 *
 */
static public ENode run(Twine sourceCode, boolean quasiFlag) {
    TextWriter warns = new TextWriter(PrintStreamWriter.stderr());
    return run(sourceCode, quasiFlag, null, warns);
}

/**
 * No longer does the caching.
 */
static public ENode run(Twine sourceCode,
                        boolean quasiFlag,
                        ConstMap optProps,
                        TextWriter warns) {
    try {
        if (null == optProps) {
            optProps = DefaultProps;
        }
        LexerFace lexer = ELexer.make(sourceCode,
                                      quasiFlag,
                                      ConstMap.testProp(optProps,
                                                        "e.enable.notabs"));
        EParser parser = EParser.make(optProps,
                                      lexer,
                                      warns,
                                      false,
                                      false);
        return parser.parse();

    } catch (IOException iox) {
        throw new EBacktraceException(iox, "# parsing a string?!");
    }
}

/**
 *
 */
public ENode optParse() {
    if (yyparse() != 0) {
        yyerror("couldn't parse expression");
    }
    return myOptResult;
}

/**
 * If the input is empty, returns the null expression e`null`, rather
 * than null.
 */
public ENode parse() {
    ENode result = optParse();
    if (result == null) {
        return b.getNULL();
    } else {
        return result;
    }
}

/**
 * Converts EOTLUs to either EOLs or EOFTOKs according to myOnlyOneExprFlag
 * <p>
 * Note that yacc uses tag-codes, while Antlr uses type-codes.
 */
private short yylex() {
    if (myLexer.isEndOfFile()) {
        yylval = null;
        return LexerFace.EOFTOK;
    }
    Astro token = null;
    try {
        token = myLexer.nextToken();
    } catch (IOException ex) {
        yyerror("io: " + ex);
    }
    yylval = token;
    short code = token.getOptTagCode();
    if (EOTLU == code) {
        if (myOnlyOneExprFlag) {
            return LexerFace.EOFTOK;
        } else {
            return EOL;
        }
    } else {
        return code;
    }
}

/**
 *
 */
private void yyerror(String s) throws SyntaxException {
    if ("syntax error".equals(s)) {
        if (null == yylval) {
            myLexer.needMore("Unexpected EOF");
            return; //give the compiler better info
        }
        short tagCode = ((Astro)yylval).getOptTagCode();
        if (LexerFace.EOFTOK == tagCode) {
            myLexer.needMore("Unexpected EOF");
            return; //give the compiler better info
        }
    }
    myLexer.syntaxError(s);
}

/**
 *
 */
public void setSource(Twine newSource) {
    myLexer.setSource(newSource);
}

/**
 *
 */
public boolean isEndOfFile() {
    return myLexer.isEndOfFile();
}

/**
 *
 */
static NounExpr noun(Object name) {
    return BaseENodeBuilder.noun(name);
}

/**
 *
 */
static private boolean isTokenKind(Object tok, int[] members) {
    if (! (tok instanceof Astro)) {
        return false;
    }
    short tagCode = ((Astro)tok).getOptTagCode();
    for (int i = 0; i < members.length; i++) {
        if (tagCode == members[i]) {
            return true;
        }
    }
    return false;
}

static private final int[] LiteralTypes = {
    LiteralInteger,
    LiteralFloat64,
    LiteralChar,
    LiteralString,
    LiteralTwine
};

/**
 *
 */
static boolean isLiteralToken(Object tok) {
    return isTokenKind(tok, LiteralTypes);
}

static private final int[] QuasiTypes = {
    QuasiOpen,
    QuasiClose
};

/**
 *
 */
static boolean isQuasiPart(Object tok) {
    return isTokenKind(tok, QuasiTypes);
}



/*********************************/


/**
 *
 */
static private final String[] TheTokens = new String[yyname.length];

/**
 * For all the names below, if the name == name.toLowerCase(), then
 * the name must be a keyword. Else it must not be a keyword. The
 * names themselves must be legal Term tags.
 */
static {
    System.arraycopy(yyname, 0, TheTokens, 0, yyname.length);

    TheTokens[LexerFace.EOFTOK] = "EOFTOK";
    /* The magical end-of-line token, not considered whitespace */
    TheTokens[EOL]              = "EOL";
    TheTokens[EOTLU]            = "EOTLU";

    TheTokens[LiteralInteger]   = ".int.";
    TheTokens[LiteralFloat64]   = ".float64.";
    TheTokens[LiteralChar]      = ".char.";
    TheTokens[LiteralString]    = ".String.";
    TheTokens[LiteralTwine]     = ".Twine.";

    TheTokens[ID]               = "ID";
    TheTokens[VerbAssign]       = "VerbAssign";
    TheTokens[QuasiOpen]        = "QuasiOpen";
    TheTokens[QuasiClose]       = "QuasiClose";
    TheTokens[DollarIdent]      = "DollarIdent";
    TheTokens[AtIdent]          = "AtIdent";
    TheTokens[DollarOpen]       = "DollarOpen";
    TheTokens[AtOpen]           = "AtOpen";
    TheTokens[URI]              = "URI";
    TheTokens[DocComment]       = "DocComment";

    /* Keywords */
    TheTokens[AS]               = "as";
    TheTokens[BIND]             = "bind";
    TheTokens[BREAK]            = "break";
    TheTokens[CATCH]            = "catch";
    TheTokens[CONTINUE]         = "continue";
    TheTokens[DEF]              = "def";
    TheTokens[ELSE]             = "else";
    TheTokens[ESCAPE]           = "escape";
    TheTokens[EXIT]             = "exit";
    TheTokens[EXTENDS]          = "extends";
    TheTokens[FINALLY]          = "finally";
    TheTokens[FN]               = "fn";
    TheTokens[FOR]              = "for";
    TheTokens[GUARDS]           = "guards";
    TheTokens[IF]               = "if";
    TheTokens[IMPLEMENTS]       = "implements";
    TheTokens[IN]               = "in";
    TheTokens[INTERFACE]        = "interface";
    TheTokens[MATCH]            = "match";
    TheTokens[META]             = "meta";
    TheTokens[METHOD]           = "method";
    TheTokens[PRAGMA]           = "pragma";
    TheTokens[RETURN]           = "return";
    TheTokens[SWITCH]           = "switch";
    TheTokens[TO]               = "to";
    TheTokens[TRY]              = "try";
    TheTokens[VAR]              = "var";
    TheTokens[VIA]              = "via";
    TheTokens[WHEN]             = "when";
    TheTokens[WHILE]            = "while";
    TheTokens[_]                = "_";

    /* pseudo-reserved keywords */
    TheTokens[ACCUM]            = "accum";
    TheTokens[MODULE]           = "module";
    TheTokens[ON]               = "on";
    TheTokens[SELECT]           = "select";
    TheTokens[THROWS]           = "throws";
    TheTokens[THUNK]            = "thunk";

    /* reserved keywords */
    TheTokens[ABSTRACT]         = "abstract";
    TheTokens[AN]               = "an";
    TheTokens[ASSERT]           = "assert";
    TheTokens[ATTRIBUTE]        = "attribute";
    TheTokens[BE]               = "be";
    TheTokens[BEGIN]            = "begin";
    TheTokens[BEHALF]           = "behalf";
    TheTokens[BELIEF]           = "belief";
    TheTokens[BELIEVE]          = "believe";
    TheTokens[BELIEVES]         = "believes";
    TheTokens[CASE]             = "case";
    TheTokens[CLASS]            = "class";
    TheTokens[CONST]            = "const";
    TheTokens[CONSTRUCTOR]      = "constructor";
    TheTokens[DATATYPE]         = "datatype";
    TheTokens[DECLARE]          = "declare";
    TheTokens[DEFAULT]          = "default";
    TheTokens[DEFINE]           = "define";
    TheTokens[DEFMACRO]         = "defmacro";
    TheTokens[DELEGATE]         = "delegate";
    TheTokens[DELICATE]         = "delicate";
    TheTokens[DEPRECATED]       = "deprecated";
    TheTokens[DISPATCH]         = "dispatch";
    TheTokens[DO]               = "do";
    TheTokens[ENCAPSULATE]      = "encapsulate";
    TheTokens[ENCAPSULATED]     = "encapsulated";
    TheTokens[ENCAPSULATES]     = "encapsulates";
    TheTokens[END]              = "end";
    TheTokens[ENSURE]           = "ensure";
    TheTokens[ENUM]             = "enum";
    TheTokens[EVENTUAL]         = "eventual";
    TheTokens[EVENTUALLY]       = "eventually";
    TheTokens[EXPORT]           = "export";
    TheTokens[FACET]            = "facet";
    TheTokens[FORALL]           = "forall";
    TheTokens[FUN]              = "fun";
    TheTokens[FUNCTION]         = "function";
    TheTokens[GIVEN]            = "given";
    TheTokens[HIDDEN]           = "hidden";
    TheTokens[HIDES]            = "hides";
    TheTokens[INLINE]           = "inline";
    TheTokens[INTO]             = "into";
    TheTokens[KNOW]             = "know";
    TheTokens[KNOWS]            = "knows";
    TheTokens[LAMBDA]           = "lambda";
    TheTokens[LET]              = "let";
    TheTokens[METHODS]          = "methods";
    TheTokens[NAMESPACE]        = "namespace";
    TheTokens[NATIVE]           = "native";
    TheTokens[OBEYS]            = "obeys";
    TheTokens[OCTET]            = "octet";
    TheTokens[ONEWAY]           = "oneway";
    TheTokens[OPERATOR]         = "operator";
    TheTokens[PACKAGE]          = "package";
    TheTokens[PRIVATE]          = "private";
    TheTokens[PROTECTED]        = "protected";
    TheTokens[PUBLIC]           = "public";
    TheTokens[RAISES]           = "raises";
    TheTokens[RELIANCE]         = "reliance";
    TheTokens[RELIANT]          = "reliant";
    TheTokens[RELIES]           = "relies";
    TheTokens[RELY]             = "rely";
    TheTokens[REVEAL]           = "reveal";
    TheTokens[SAKE]             = "sake";
    TheTokens[SIGNED]           = "signed";
    TheTokens[STATIC]           = "static";
    TheTokens[STRUCT]           = "struct";
    TheTokens[SUCHTHAT]         = "suchthat";
    TheTokens[SUPPORTS]         = "supports";
    TheTokens[SUSPECT]          = "suspect";
    TheTokens[SUSPECTS]         = "suspects";
    TheTokens[SYNCHRONIZED]     = "synchronized";
    TheTokens[THIS]             = "this";
    TheTokens[TRANSIENT]        = "transient";
    TheTokens[TRUNCATABLE]      = "truncatable";
    TheTokens[TYPEDEF]          = "typedef";
    TheTokens[UNSIGNED]         = "unsigned";
    TheTokens[UNUM]             = "unum";
    TheTokens[USES]             = "uses";
    TheTokens[USING]            = "using";
    TheTokens[UTF8]             = "utf8";
    TheTokens[UTF16]            = "utf16";
    TheTokens[VIRTUAL]          = "virtual";
    TheTokens[VOLATILE]         = "volatile";
    TheTokens[WSTRING]          = "wstring";

    /* Single-Character Tokens */
    TheTokens[';']              = "SemiColon";
    TheTokens['&']              = "Ampersand";
    TheTokens['|']              = "VerticalBar";
    TheTokens['^']              = "Caret";
    TheTokens['+']              = "Plus";
    TheTokens['-']              = "Minus";
    TheTokens['*']              = "Star";
    TheTokens['/']              = "Slash";
    TheTokens['%']              = "Percent";
    TheTokens['!']              = "Bang";
    TheTokens['~']              = "Tilde";
    TheTokens['$']              = "Dollar";
    TheTokens['@']              = "At";
    TheTokens[',']              = "Comma";
    TheTokens['?']              = "Question";
    TheTokens[':']              = "Colon";
    TheTokens['.']              = "Dot";

    TheTokens['(']              = "OpenParen";
    TheTokens[')']              = "CloseParen";
    TheTokens['[']              = "OpenBracket";
    TheTokens[']']              = "CloseBracket";
    TheTokens['{']              = "OpenBrace";
    TheTokens['}']              = "CloseBrace";
    TheTokens['<']              = "OpenAngle";
    TheTokens['>']              = "CloseAngle";


    /* Multi-Character Operators */
    TheTokens[OpLAnd]           = "OpLAnd";
    TheTokens[OpLOr]            = "OpLOr";
    TheTokens[OpSame]           = "OpSame";
    TheTokens[OpNSame]          = "OpNSame";
    TheTokens[OpButNot]         = "OpButNot";
    TheTokens[OpLeq]            = "OpLeq";
    TheTokens[OpABA]            = "OpABA";
    TheTokens[OpGeq]            = "OpGeq";
    TheTokens[OpThru]           = "OpThru";
    TheTokens[OpTill]           = "OpTill";
    TheTokens[OpAsl]            = "OpAsl";
    TheTokens[OpAsr]            = "OpAsr";
    TheTokens[OpFlrDiv]         = "OpFlrDiv";
    TheTokens[OpMod]            = "OpMod";
    TheTokens[OpPow]            = "OpPow";

    TheTokens[OpAss]            = "OpAss";
    TheTokens[OpAssAdd]         = "OpAssAdd";
    TheTokens[OpAssAnd]         = "OpAssAnd";
    TheTokens[OpAssAprxDiv]     = "OpAssAprxDiv";
    TheTokens[OpAssFlrDiv]      = "OpAssFlrDiv";
    TheTokens[OpAssAsl]         = "OpAssAsl";
    TheTokens[OpAssAsr]         = "OpAssAsr";
    TheTokens[OpAssRemdr]       = "OpAssRemdr";
    TheTokens[OpAssMod]         = "OpAssMod";
    TheTokens[OpAssMul]         = "OpAssMul";
    TheTokens[OpAssOr]          = "OpAssOr";
    TheTokens[OpAssPow]         = "OpAssPow";
    TheTokens[OpAssSub]         = "OpAssSub";
    TheTokens[OpAssXor]         = "OpAssXor";

    /* Other funky tokens */
    TheTokens[Send]             = "Send";
    TheTokens[OpWhen]           = "OpWhen";
    TheTokens[MapsTo]           = "MapsTo";
    TheTokens[MatchBind]        = "MatchBind";
    TheTokens[MisMatch]         = "MisMatch";
    TheTokens[OpScope]          = "OpScope";

    /* Non-token Kernel-E Term-tree tag names (ie, functor names) */

    TheTokens[AssignExpr]       = "AssignExpr";
    TheTokens[CallExpr]         = "CallExpr";
    TheTokens[DefineExpr]       = "DefineExpr";
    TheTokens[EscapeExpr]       = "EscapeExpr";
    TheTokens[HideExpr]         = "HideExpr";
    TheTokens[IfExpr]           = "IfExpr";
    TheTokens[LiteralExpr]      = "LiteralExpr";
    TheTokens[NounExpr]         = "NounExpr";
    TheTokens[ObjectExpr]       = "ObjectExpr";
    TheTokens[QuasiLiteralExpr] = "QuasiLiteralExpr";
    TheTokens[QuasiPatternExpr] = "QuasiPatternExpr";
    TheTokens[MetaStateExpr]    = "MetaStateExpr";
    TheTokens[MetaContextExpr]  = "MetaContextExpr";
    TheTokens[SeqExpr]          = "SeqExpr";
    TheTokens[SlotExpr]         = "SlotExpr";
    TheTokens[MetaExpr]         = "MetaExpr";
    TheTokens[CatchExpr]        = "CatchExpr";
    TheTokens[FinallyExpr]      = "FinallyExpr";

    TheTokens[FinalPattern]     = "FinalPattern";
    TheTokens[SlotPattern]      = "SlotPattern";
    TheTokens[ListPattern]      = "ListPattern";
    TheTokens[IgnorePattern]    = "IgnorePattern";
    TheTokens[QuasiLiteralPatt] = "QuasiLiteralPatt";
    TheTokens[QuasiPatternPatt] = "QuasiPatternPatt";

    TheTokens[EScript]          = "EScript";
    TheTokens[EMethod]          = "EMethod";
    TheTokens[EMatcher]         = "EMatcher";
}

/**
 *
 */
static public final AstroSchema DEFAULT_SCHEMA =
  new BaseSchema("E-Language", ConstList.fromArray(TheTokens));

/**
 *
 */
static private final int DEFAULT_CONTINUE_INDENT = 2;

/**
 * These are the tokens that may appear at the end of a line, in which
 * case the next line is a (to be indented) continuation of the
 * expression.
 */
static private final int[][] TheContinuerOps = {
    { '!',          DEFAULT_CONTINUE_INDENT },
    { '%',          DEFAULT_CONTINUE_INDENT },
    { '&',          DEFAULT_CONTINUE_INDENT },
    { '*',          DEFAULT_CONTINUE_INDENT },
    { '+',          DEFAULT_CONTINUE_INDENT },
    { '-',          DEFAULT_CONTINUE_INDENT },
    { '/',          DEFAULT_CONTINUE_INDENT },
    { ':',          DEFAULT_CONTINUE_INDENT },
    { '<',          DEFAULT_CONTINUE_INDENT },
    { '>',          DEFAULT_CONTINUE_INDENT },
    { '?',          DEFAULT_CONTINUE_INDENT },
    { '^',          DEFAULT_CONTINUE_INDENT },
    { '|',          DEFAULT_CONTINUE_INDENT },
    { '~',          DEFAULT_CONTINUE_INDENT },
    { '.',          DEFAULT_CONTINUE_INDENT },

    { VerbAssign,   DEFAULT_CONTINUE_INDENT },    // ID"="

    { EXTENDS,      DEFAULT_CONTINUE_INDENT },
    { IMPLEMENTS,   DEFAULT_CONTINUE_INDENT },
    { IN,           DEFAULT_CONTINUE_INDENT },
    { EXTENDS,      DEFAULT_CONTINUE_INDENT },

    { OpABA,        DEFAULT_CONTINUE_INDENT },    // <=>
    { OpAsl,        DEFAULT_CONTINUE_INDENT },    // <<
    { OpAsr,        DEFAULT_CONTINUE_INDENT },    // >>
    { OpAss,        DEFAULT_CONTINUE_INDENT },    // :=
    { OpAssAdd,     DEFAULT_CONTINUE_INDENT },    // +=
    { OpAssAnd,     DEFAULT_CONTINUE_INDENT },    // &=
    { OpAssAprxDiv, DEFAULT_CONTINUE_INDENT },    // /=
    { OpAssAsl,     DEFAULT_CONTINUE_INDENT },    // <<=
    { OpAssAsr,     DEFAULT_CONTINUE_INDENT },    // >>=
    { OpAssFlrDiv,  DEFAULT_CONTINUE_INDENT },    // //=
    { OpAssMod,     DEFAULT_CONTINUE_INDENT },    // %%=
    { OpAssMul,     DEFAULT_CONTINUE_INDENT },    // *=
    { OpAssOr,      DEFAULT_CONTINUE_INDENT },    // |=
    { OpAssPow,     DEFAULT_CONTINUE_INDENT },    // **=
    { OpAssRemdr,   DEFAULT_CONTINUE_INDENT },    // %=
    { OpAssSub,     DEFAULT_CONTINUE_INDENT },    // -=
    { OpAssXor,     DEFAULT_CONTINUE_INDENT },    // ^=
    { OpButNot,     DEFAULT_CONTINUE_INDENT },    // &!
    { OpFlrDiv,     DEFAULT_CONTINUE_INDENT },    // //
    { OpGeq,        DEFAULT_CONTINUE_INDENT },    // >=
    { OpLAnd,       DEFAULT_CONTINUE_INDENT },    // &&
    { OpLOr,        DEFAULT_CONTINUE_INDENT },    // ||
    { OpLeq,        DEFAULT_CONTINUE_INDENT },    // <=
    { OpMod,        DEFAULT_CONTINUE_INDENT },    // %%
    { OpNSame,      DEFAULT_CONTINUE_INDENT },    // !=
    { OpPow,        DEFAULT_CONTINUE_INDENT },    // **
    { OpSame,       DEFAULT_CONTINUE_INDENT },    // ==
    { OpThru,       DEFAULT_CONTINUE_INDENT },    // ..
    { OpTill,       DEFAULT_CONTINUE_INDENT },    // ..!

    { Send,         DEFAULT_CONTINUE_INDENT },    // <-
    { OpWhen,       DEFAULT_CONTINUE_INDENT },    // ->
    { MapsTo,       DEFAULT_CONTINUE_INDENT },    // =>
    { MatchBind,    DEFAULT_CONTINUE_INDENT },    // =~
    { MisMatch,     DEFAULT_CONTINUE_INDENT },    // !~
    { OpScope,      DEFAULT_CONTINUE_INDENT },    // ::

    { ',',          0 },
    { DocComment,   0 }                           // /**..*/
};

/**
 * TheContinuers[tagCode] says whether this is a continuation
 * operator, and if so, how much to indent by.
 * <p>
 * If this isn't a continuation operator, then -1.
 */
static private final int[] TheContinuers = new int[yyname.length];

static {
    for (int i = 0, len = TheContinuers.length; i < len; i++) {
        TheContinuers[i] = -1;
    }
    for (int i = 0; i < TheContinuerOps.length; i++) {
        TheContinuers[TheContinuerOps[i][0]] = TheContinuerOps[i][1];
    }
}

/**
 * If this token appears at the end of a line, does that make the next
 * line a (to be indented) continuation line?
 * <p>
 * -1 if not. The number of spaces to indent if so.
 */
static public int continueCount(int tagCode) {
    return TheContinuers[tagCode];
}

/**
 * Used to mark places where we should be providing a poser (an object
 * from which source position info can be derived).
 */
static private final Object NO_POSER = BaseEBuilder.NO_POSER;
//#line 5613 "EParser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}



//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse() 
{
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  char:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]+"");
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 207 "e.y"
{ myOptResult = null; }
break;
case 2:
//#line 208 "e.y"
{ myOptResult = b.forValue(val_peek(0), null); }
break;
case 3:
//#line 211 "e.y"
{ b.reserved(val_peek(3),"module"); }
break;
case 4:
//#line 214 "e.y"
{ myOptResult = (Pattern)val_peek(1); }
break;
case 5:
//#line 221 "e.y"
{ yyval = b.get__BREAK(); }
break;
case 6:
//#line 222 "e.y"
{ yyval = b.get__CONTINUE(); }
break;
case 7:
//#line 223 "e.y"
{ yyval = b.get__RETURN(); }
break;
case 8:
//#line 236 "e.y"
{ yyval = val_peek(1); }
break;
case 10:
//#line 246 "e.y"
{ yyval = b.sequence(val_peek(2), val_peek(0)); }
break;
case 13:
//#line 255 "e.y"
{ yyval = b.sequence(val_peek(2), val_peek(0)); }
break;
case 15:
//#line 273 "e.y"
{ yyval = b.ejector(val_peek(0)); }
break;
case 16:
//#line 274 "e.y"
{ yyval = b.ejector(val_peek(2)); }
break;
case 17:
//#line 275 "e.y"
{ yyval = b.ejector(val_peek(1), val_peek(0)); }
break;
case 18:
//#line 276 "e.y"
{ b.pocket(val_peek(1),"smalltalk-return");
                                          yyval = b.ejector(b.get__RETURN(),val_peek(0));}
break;
case 19:
//#line 278 "e.y"
{ yyval = b.doco("",val_peek(0)); }
break;
case 20:
//#line 279 "e.y"
{ yyval = b.doco(val_peek(1),val_peek(0)); }
break;
case 22:
//#line 292 "e.y"
{ yyval = b.forward(val_peek(0)); }
break;
case 23:
//#line 293 "e.y"
{ yyval = b.assign(val_peek(2),     val_peek(0)); }
break;
case 24:
//#line 294 "e.y"
{ yyval = b.update(val_peek(2), val_peek(1), b.list(val_peek(0))); }
break;
case 25:
//#line 295 "e.y"
{ yyval = b.update(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 26:
//#line 297 "e.y"
{ yyval = b.define(val_peek(2), val_peek(0)); }
break;
case 27:
//#line 298 "e.y"
{ yyval = b.define(val_peek(2), val_peek(0)); }
break;
case 28:
//#line 299 "e.y"
{ yyval = b.define(val_peek(2), val_peek(0)); }
break;
case 29:
//#line 302 "e.y"
{ b.pocket(val_peek(3),"trinary-define");
                                          yyval = b.define(val_peek(4),val_peek(2),val_peek(0)); }
break;
case 30:
//#line 310 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 31:
//#line 311 "e.y"
{ yyval = b.list(); }
break;
case 32:
//#line 312 "e.y"
{ yyval = b.append(b.list(val_peek(3)),val_peek(1)); }
break;
case 34:
//#line 321 "e.y"
{ yyval = b.condOr(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 36:
//#line 330 "e.y"
{ yyval = b.condAnd(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 38:
//#line 342 "e.y"
{ yyval = b.same(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 39:
//#line 343 "e.y"
{ yyval = b.not(val_peek(1), b.same(val_peek(2), val_peek(1), val_peek(0))); }
break;
case 42:
//#line 346 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"xor", val_peek(0)); }
break;
case 43:
//#line 347 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"butNot", val_peek(0)); }
break;
case 44:
//#line 349 "e.y"
{ yyval = b.matchBind(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 45:
//#line 350 "e.y"
{ yyval = b.not(val_peek(1),
                                                     b.matchBind(val_peek(2),val_peek(1),val_peek(0))); }
break;
case 46:
//#line 355 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"and", val_peek(0)); }
break;
case 47:
//#line 356 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"and", val_peek(0)); }
break;
case 48:
//#line 359 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"or", val_peek(0)); }
break;
case 49:
//#line 360 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"or", val_peek(0)); }
break;
case 51:
//#line 372 "e.y"
{ yyval = b.lessThan(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 52:
//#line 373 "e.y"
{ yyval = b.leq(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 53:
//#line 374 "e.y"
{ yyval = b.asBigAs(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 54:
//#line 375 "e.y"
{ yyval = b.geq(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 55:
//#line 376 "e.y"
{ yyval = b.greaterThan(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 56:
//#line 380 "e.y"
{ b.pocket(val_peek(1),"cast");
                                          yyval = b.cast(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 58:
//#line 389 "e.y"
{ yyval = b.thru(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 59:
//#line 390 "e.y"
{ yyval = b.till(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 61:
//#line 399 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"shiftLeft", val_peek(0)); }
break;
case 62:
//#line 400 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"shiftRight",val_peek(0)); }
break;
case 64:
//#line 409 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"add", val_peek(0)); }
break;
case 65:
//#line 410 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"subtract",val_peek(0)); }
break;
case 67:
//#line 421 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"multiply", val_peek(0)); }
break;
case 68:
//#line 422 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"approxDivide", val_peek(0)); }
break;
case 69:
//#line 423 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"floorDivide", val_peek(0)); }
break;
case 70:
//#line 424 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"remainder", val_peek(0)); }
break;
case 71:
//#line 425 "e.y"
{ yyval = b.mod(val_peek(2), val_peek(1), b.list(val_peek(0))); }
break;
case 73:
//#line 434 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"pow", val_peek(0)); }
break;
case 75:
//#line 452 "e.y"
{ yyval = b.call(val_peek(0), val_peek(1),"not", b.list()); }
break;
case 76:
//#line 453 "e.y"
{ yyval = b.call(val_peek(0), val_peek(1),"complement", b.list());}
break;
case 77:
//#line 454 "e.y"
{ yyval = b.slotExpr(val_peek(1), val_peek(0)); }
break;
case 78:
//#line 455 "e.y"
{ yyval = b.bindingExpr(val_peek(1), val_peek(0)); }
break;
case 79:
//#line 456 "e.y"
{ b.pocket(val_peek(1),"unary-star");
                                  yyval = b.call(val_peek(0), val_peek(1),"get", b.list()); }
break;
case 80:
//#line 458 "e.y"
{ yyval = b.call(val_peek(0), val_peek(1),"negate", b.list()); }
break;
case 82:
//#line 467 "e.y"
{ yyval = b.callFacet(val_peek(2), val_peek(0)); }
break;
case 83:
//#line 468 "e.y"
{ yyval = b.sendFacet(val_peek(2), val_peek(0)); }
break;
case 84:
//#line 470 "e.y"
{ yyval = b.propValue(val_peek(2), val_peek(0)); }
break;
case 85:
//#line 471 "e.y"
{ yyval = b.propSlot(val_peek(3), val_peek(0)); }
break;
case 86:
//#line 472 "e.y"
{ b.reserved(val_peek(1),"::&"); }
break;
case 87:
//#line 473 "e.y"
{ yyval = b.doMetaProp(val_peek(2), val_peek(0)); }
break;
case 88:
//#line 475 "e.y"
{ yyval = b.sendPropValue(val_peek(3), val_peek(0)); }
break;
case 89:
//#line 476 "e.y"
{ yyval = b.sendPropSlot(val_peek(4), val_peek(0)); }
break;
case 90:
//#line 477 "e.y"
{ b.reserved(val_peek(1),"<-::&"); }
break;
case 92:
//#line 489 "e.y"
{ yyval = b.call(val_peek(3), val_peek(2),"run", val_peek(1)); }
break;
case 93:
//#line 490 "e.y"
{ b.pocket(NO_POSER,"lambda-args");
                                          yyval = b.call(val_peek(0),
                                                      "run__control",
                                                      b.list()); }
break;
case 94:
//#line 495 "e.y"
{ yyval = b.call(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 95:
//#line 496 "e.y"
{ yyval = b.call(val_peek(3), val_peek(2),"get", val_peek(1)); }
break;
case 96:
//#line 497 "e.y"
{ yyval = b.send(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 97:
//#line 498 "e.y"
{ yyval = b.send(val_peek(2), val_peek(1),"run", val_peek(0)); }
break;
case 98:
//#line 500 "e.y"
{ yyval = b.doMeta(val_peek(1), val_peek(1),"run", val_peek(0)); }
break;
case 99:
//#line 501 "e.y"
{ yyval = b.doMeta(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 100:
//#line 502 "e.y"
{ yyval = b.doMeta(val_peek(3), val_peek(2),"get", val_peek(1)); }
break;
case 101:
//#line 503 "e.y"
{ yyval = b.doMetaSend(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 102:
//#line 504 "e.y"
{ yyval = b.doMetaSend(val_peek(2), val_peek(1),"run", val_peek(0));}
break;
case 104:
//#line 511 "e.y"
{ yyval = b.control(val_peek(6), val_peek(2), val_peek(4), val_peek(1), val_peek(0)); }
break;
case 105:
//#line 512 "e.y"
{ yyval = b.control(val_peek(3), val_peek(2), val_peek(1), val_peek(0)); }
break;
case 106:
//#line 513 "e.y"
{ yyval = b.control(val_peek(3), val_peek(2), val_peek(1), val_peek(0)); }
break;
case 107:
//#line 515 "e.y"
{ yyval = b.control(val_peek(5), val_peek(4), val_peek(2),
                                                         b.list(), val_peek(0)); }
break;
case 108:
//#line 523 "e.y"
{ yyval = b.literal(val_peek(0)); }
break;
case 109:
//#line 524 "e.y"
{ yyval = b.literal(val_peek(0)); }
break;
case 110:
//#line 525 "e.y"
{ yyval = b.literal(val_peek(0)); }
break;
case 111:
//#line 526 "e.y"
{ yyval = b.literal(val_peek(0)); }
break;
case 114:
//#line 535 "e.y"
{ yyval = b.uriExpr(val_peek(0)); }
break;
case 115:
//#line 536 "e.y"
{ yyval = b.quasiExpr(val_peek(1),val_peek(0)); }
break;
case 117:
//#line 539 "e.y"
{ yyval = b.tuple(val_peek(1)); }
break;
case 118:
//#line 540 "e.y"
{ yyval = b.listComprehension(val_peek(6),val_peek(5),val_peek(3),val_peek(1)); }
break;
case 119:
//#line 541 "e.y"
{ yyval = b.map(val_peek(1)); }
break;
case 120:
//#line 542 "e.y"
{ yyval = b.mapComprehension(val_peek(8),val_peek(7),val_peek(5),val_peek(3),val_peek(1)); }
break;
case 121:
//#line 544 "e.y"
{ yyval = b.hide(val_peek(0)); }
break;
case 122:
//#line 545 "e.y"
{ yyval = b.escape(val_peek(2),val_peek(1),val_peek(0)); }
break;
case 123:
//#line 546 "e.y"
{ yyval = b.whilex(val_peek(2),val_peek(1),val_peek(0)); }
break;
case 124:
//#line 547 "e.y"
{ yyval = b.switchx(val_peek(1),val_peek(0)); }
break;
case 125:
//#line 548 "e.y"
{ yyval = b.tryx(val_peek(2),val_peek(1),val_peek(0)); }
break;
case 127:
//#line 551 "e.y"
{ yyval = val_peek(0); }
break;
case 129:
//#line 554 "e.y"
{ b.reserved(val_peek(2),"select"); }
break;
case 131:
//#line 562 "e.y"
{ yyval = b.doco("",val_peek(0)); }
break;
case 132:
//#line 563 "e.y"
{ yyval = b.doco(val_peek(1),val_peek(0)); }
break;
case 133:
//#line 573 "e.y"
{ yyval = ((ConstMap)val_peek(0)).with(
					         "oName", val_peek(1), true); }
break;
case 134:
//#line 576 "e.y"
{ yyval = b.oType("",val_peek(6),b.list(),
                                                       val_peek(5),val_peek(4),val_peek(1)); }
break;
case 135:
//#line 578 "e.y"
{ b.pocket(NO_POSER,"thunk");
                                          /* doesn't bind __return */
                                          yyval = b.fnDecl(val_peek(1), b.list(), val_peek(0)); }
break;
case 136:
//#line 582 "e.y"
{ b.pocket(NO_POSER,"anon-lambda");
                                          /* doesn't bind __return */
                                          yyval = b.fnDecl(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 137:
//#line 591 "e.y"
{ yyval = ((ConstMap)val_peek(1)).with(
						 "script", val_peek(0), true); }
break;
case 138:
//#line 593 "e.y"
{ /* binds __return */
                                          yyval = ((ConstMap)val_peek(1)).
                                                 or(b.methDecl(val_peek(2), val_peek(0), true), 
                                                    true); }
break;
case 139:
//#line 604 "e.y"
{ yyval = noun(val_peek(0)); }
break;
case 140:
//#line 605 "e.y"
{ yyval = b.quasiLiteralExpr(val_peek(0)); }
break;
case 141:
//#line 606 "e.y"
{ yyval = b.quasiPatternExpr(val_peek(0)); }
break;
case 142:
//#line 610 "e.y"
{ yyval = val_peek(1); }
break;
case 143:
//#line 611 "e.y"
{ yyval = val_peek(0); }
break;
case 144:
//#line 612 "e.y"
{ yyval = null; }
break;
case 145:
//#line 616 "e.y"
{ yyval = val_peek(1); }
break;
case 146:
//#line 617 "e.y"
{ yyval = val_peek(0); }
break;
case 147:
//#line 624 "e.y"
{ yyval = val_peek(1); }
break;
case 148:
//#line 632 "e.y"
{ yyval = b.ifx(val_peek(1), val_peek(0)); }
break;
case 149:
//#line 633 "e.y"
{ yyval = b.ifx(val_peek(3), val_peek(2), val_peek(0)); }
break;
case 150:
//#line 634 "e.y"
{ yyval = b.ifx(val_peek(3), val_peek(2), val_peek(0)); }
break;
case 152:
//#line 639 "e.y"
{ b.reserved(val_peek(1),"if-match"); }
break;
case 153:
//#line 647 "e.y"
{ yyval = b.forx(val_peek(4),val_peek(2),val_peek(1),val_peek(0)); }
break;
case 154:
//#line 648 "e.y"
{ b.reserved(val_peek(2),"when-in"); }
break;
case 156:
//#line 660 "e.y"
{ yyval = noun("simple__quasiParser"); }
break;
case 157:
//#line 661 "e.y"
{ yyval = noun(b.mangle(val_peek(0),
                                                     "__quasiParser")); }
break;
case 158:
//#line 666 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 159:
//#line 667 "e.y"
{ yyval = b.with(val_peek(1), val_peek(0)); }
break;
case 160:
//#line 671 "e.y"
{ yyval = b.list(val_peek(1), val_peek(0)); }
break;
case 161:
//#line 672 "e.y"
{ yyval = b.with(b.with(val_peek(2), val_peek(1)), val_peek(0)); }
break;
case 162:
//#line 676 "e.y"
{ yyval = b.dollarNoun(val_peek(0)); }
break;
case 163:
//#line 677 "e.y"
{ yyval = val_peek(1); }
break;
case 164:
//#line 688 "e.y"
{ yyval = val_peek(1); }
break;
case 170:
//#line 714 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 171:
//#line 715 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 173:
//#line 719 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 174:
//#line 720 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 175:
//#line 725 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 176:
//#line 726 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 178:
//#line 730 "e.y"
{ yyval = b.assoc(val_peek(2), val_peek(0)); }
break;
case 179:
//#line 731 "e.y"
{ b.pocket(val_peek(1),"exporter");
                                          yyval = b.exporter(val_peek(0)); }
break;
case 180:
//#line 733 "e.y"
{ b.pocket(val_peek(2),"exporter");
                                          yyval = b.exporter(b.slotExpr(val_peek(1),val_peek(0))); }
break;
case 181:
//#line 735 "e.y"
{ b.pocket(val_peek(2),"exporter");
                                          yyval=b.exporter(b.bindingExpr(val_peek(1),val_peek(0)));}
break;
case 182:
//#line 737 "e.y"
{ b.pocket(val_peek(2),"exporter");
                                          b.reserved(val_peek(1),"Forward exporter"); }
break;
case 183:
//#line 752 "e.y"
{ yyval = val_peek(0); }
break;
case 184:
//#line 753 "e.y"
{ b.pocket(val_peek(1),"for-must-match");
 					  yyval = ((Assoc) val_peek(0)).withMatch(); }
break;
case 185:
//#line 758 "e.y"
{ yyval = b.assoc(b.ignore(), val_peek(0)); }
break;
case 186:
//#line 759 "e.y"
{ yyval = b.assoc(val_peek(2), val_peek(0)); }
break;
case 188:
//#line 765 "e.y"
{ yyval = b.suchThat(val_peek(2), val_peek(0)); }
break;
case 189:
//#line 766 "e.y"
{ yyval = b.via(val_peek(1),val_peek(0)); }
break;
case 190:
//#line 768 "e.y"
{ b.reserved(val_peek(1),"meta pattern"); }
break;
case 193:
//#line 774 "e.y"
{ yyval = b.quasiPattern(val_peek(1), val_peek(0)); }
break;
case 194:
//#line 775 "e.y"
{ yyval = b.patternEquals(val_peek(0)); }
break;
case 195:
//#line 776 "e.y"
{ b.reserved(val_peek(1),"not-same pattern"); }
break;
case 196:
//#line 777 "e.y"
{ b.reserved(val_peek(1),
                                                     "comparison pattern"); }
break;
case 197:
//#line 780 "e.y"
{ b.pocket(val_peek(4),"call-pattern");
                                                 yyval = b.callPattern(val_peek(3), val_peek(2),"run", val_peek(1));}
break;
case 198:
//#line 782 "e.y"
{ b.pocket(val_peek(6),"call-pattern");
                                                 yyval = b.callPattern(val_peek(5), val_peek(3), val_peek(1)); }
break;
case 199:
//#line 784 "e.y"
{ b.pocket(val_peek(4),"call-pattern");
                                                 yyval = b.callPattern(val_peek(3), val_peek(2),"get", val_peek(1));}
break;
case 200:
//#line 787 "e.y"
{ yyval = b.listPattern(val_peek(0)); }
break;
case 201:
//#line 788 "e.y"
{ yyval = b.mapPattern(val_peek(1),null); }
break;
case 202:
//#line 789 "e.y"
{ yyval = b.cdrPattern(val_peek(2), val_peek(0)); }
break;
case 203:
//#line 790 "e.y"
{ yyval = b.mapPattern(val_peek(3), val_peek(0)); }
break;
case 204:
//#line 794 "e.y"
{ yyval = val_peek(1); }
break;
case 205:
//#line 798 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 206:
//#line 799 "e.y"
{ yyval = b.with(val_peek(1), val_peek(0)); }
break;
case 207:
//#line 803 "e.y"
{ yyval = b.list(val_peek(1), val_peek(0)); }
break;
case 208:
//#line 804 "e.y"
{ yyval = b.with(b.with(val_peek(2), val_peek(1)),
                                                              val_peek(0)); }
break;
case 211:
//#line 814 "e.y"
{ yyval = b.atNoun(val_peek(0)); }
break;
case 212:
//#line 815 "e.y"
{ yyval = val_peek(2); }
break;
case 213:
//#line 828 "e.y"
{ yyval = b.ignore(); }
break;
case 214:
//#line 829 "e.y"
{ yyval = b.ignore(val_peek(0)); }
break;
case 215:
//#line 830 "e.y"
{ yyval = b.ignore(val_peek(0));}
break;
case 216:
//#line 834 "e.y"
{ yyval = b.finalPattern(val_peek(2),val_peek(0));}
break;
case 217:
//#line 835 "e.y"
{ b.antiPocket(val_peek(0),
                                                       "explicit-final-guard");
                                          yyval = b.finalPattern(val_peek(0)); }
break;
case 221:
//#line 844 "e.y"
{ yyval = b.bindDefiner(val_peek(2),val_peek(0)); }
break;
case 222:
//#line 845 "e.y"
{ b.antiPocket(val_peek(1),
                                                       "explicit-final-guard");
                                          yyval = b.bindDefiner(val_peek(0)); }
break;
case 223:
//#line 851 "e.y"
{ yyval = b.varPattern(val_peek(2),val_peek(0)); }
break;
case 224:
//#line 852 "e.y"
{ b.antiPocket(val_peek(1),
                                                       "explicit-var-guard");
                                          yyval = b.varPattern(val_peek(0)); }
break;
case 225:
//#line 858 "e.y"
{ yyval = b.slotPattern(val_peek(2),val_peek(0)); }
break;
case 226:
//#line 859 "e.y"
{ b.antiPocket(val_peek(1),
                                                       "explicit-slot-guard");
                                          yyval = b.slotPattern(val_peek(0)); }
break;
case 227:
//#line 862 "e.y"
{ yyval = b.bindingPattern(val_peek(0)); }
break;
case 228:
//#line 871 "e.y"
{ yyval = b.finalOName(val_peek(0)); }
break;
case 229:
//#line 872 "e.y"
{ yyval = b.ignoreOName(); }
break;
case 230:
//#line 873 "e.y"
{ yyval = b.bindOName(val_peek(0)); }
break;
case 231:
//#line 874 "e.y"
{ yyval = b.varOName(val_peek(0)); }
break;
case 232:
//#line 875 "e.y"
{ b.reserved(val_peek(0),
                                "literal qualified name no longer accepted"); }
break;
case 234:
//#line 888 "e.y"
{ yyval = val_peek(1); }
break;
case 237:
//#line 914 "e.y"
{ yyval = val_peek(1); }
break;
case 238:
//#line 918 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 239:
//#line 919 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 240:
//#line 923 "e.y"
{ yyval = val_peek(1); }
break;
case 241:
//#line 927 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 242:
//#line 928 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 243:
//#line 932 "e.y"
{ yyval = b.assoc(val_peek(2), val_peek(0)); }
break;
case 244:
//#line 933 "e.y"
{ b.pocket(val_peek(1),"pattern-default");
                                          yyval = b.assoc(val_peek(4), b.assoc(val_peek(0),val_peek(2))); }
break;
case 245:
//#line 935 "e.y"
{ b.pocket(val_peek(1),"pattern-default");
                                     b.reserved(val_peek(1),"default in map pattern"); }
break;
case 246:
//#line 937 "e.y"
{ b.pocket(val_peek(1),"importer");
                                          yyval = b.importer(val_peek(0)); }
break;
case 247:
//#line 939 "e.y"
{ b.pocket(val_peek(1),"pattern-default");
                                          b.pocket(val_peek(3),"importer");
                                          yyval = b.importer(b.assoc(val_peek(0),val_peek(2))); }
break;
case 248:
//#line 942 "e.y"
{ b.pocket(val_peek(1),"pattern-default");
                                          b.pocket(val_peek(3),"importer");
                                     b.reserved(val_peek(0),"default in map pattern"); }
break;
case 251:
//#line 963 "e.y"
{ yyval = ""; }
break;
case 253:
//#line 971 "e.y"
{ yyval = val_peek(0); }
break;
case 254:
//#line 972 "e.y"
{ yyval = b.bindOName(val_peek(0)); }
break;
case 255:
//#line 973 "e.y"
{ yyval = b.varOName(val_peek(0)); }
break;
case 256:
//#line 980 "e.y"
{ yyval = ((ConstMap)val_peek(0)).
                                                 with("extends", val_peek(1), true); }
break;
case 257:
//#line 988 "e.y"
{yyval=ConstMap.fromPairs(new Object[][]{
                                              { "supers", b.optExprs(val_peek(1)) },
                                              { "impls", b.optExprs(val_peek(0)) }}); }
break;
case 258:
//#line 999 "e.y"
{ yyval = null; }
break;
case 259:
//#line 1000 "e.y"
{ yyval = val_peek(0); }
break;
case 260:
//#line 1007 "e.y"
{ yyval = b.list(); }
break;
case 262:
//#line 1011 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 263:
//#line 1012 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 264:
//#line 1019 "e.y"
{yyval=ConstMap.fromPairs(new Object[][]{
                                              { "as", val_peek(1) },
                                              { "impls", b.optExprs(val_peek(0)) }}); }
break;
case 265:
//#line 1024 "e.y"
{ yyval = null; }
break;
case 266:
//#line 1025 "e.y"
{ yyval = val_peek(0); }
break;
case 267:
//#line 1028 "e.y"
{ yyval = b.list(); }
break;
case 269:
//#line 1032 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 270:
//#line 1033 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 274:
//#line 1047 "e.y"
{ b.reserved(val_peek(0),"literal concat"); }
break;
case 275:
//#line 1048 "e.y"
{ b.reserved(val_peek(0),"literal concat"); }
break;
case 276:
//#line 1057 "e.y"
{ /* binds __return */
                                               yyval = b.to(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 277:
//#line 1059 "e.y"
{ /* doesn't bind __return */
                                               yyval = b.method(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 278:
//#line 1088 "e.y"
{ yyval = b.methHead(val_peek(3),"run",val_peek(2),val_peek(0)); }
break;
case 279:
//#line 1089 "e.y"
{ yyval = b.methHead(val_peek(4),      val_peek(2),val_peek(0)); }
break;
case 280:
//#line 1098 "e.y"
{ yyval = b.methHead(val_peek(3),"run", val_peek(2), val_peek(0));}
break;
case 281:
//#line 1100 "e.y"
{ b.pocket(val_peek(5),"one-method-object");
                                          yyval = b.methHead(val_peek(4), val_peek(2), val_peek(0)); }
break;
case 282:
//#line 1103 "e.y"
{ b.pocket(val_peek(5),"one-method-object");
                                          yyval = b.methHead(val_peek(4), val_peek(2), val_peek(0)); }
break;
case 283:
//#line 1114 "e.y"
{ yyval = b.matcher(val_peek(1), val_peek(0)); }
break;
case 285:
//#line 1122 "e.y"
{ yyval = b.uriExpr(val_peek(0)); }
break;
case 287:
//#line 1124 "e.y"
{ yyval = b.call(val_peek(3), val_peek(2),"get", val_peek(1)); }
break;
case 288:
//#line 1125 "e.y"
{ yyval = b.propValue(val_peek(2), val_peek(0)); }
break;
case 294:
//#line 1137 "e.y"
{ yyval = val_peek(0); }
break;
case 295:
//#line 1138 "e.y"
{ yyval = b.defaultOptResultGuard(yylval); }
break;
case 296:
//#line 1139 "e.y"
{ b.reserved(val_peek(1),"throws"); }
break;
case 297:
//#line 1140 "e.y"
{ b.reserved(val_peek(1),"throws"); }
break;
case 298:
//#line 1143 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 299:
//#line 1144 "e.y"
{ yyval = b.with(val_peek(2),val_peek(0)); }
break;
case 300:
//#line 1152 "e.y"
{ yyval = b.when(val_peek(4), val_peek(1), val_peek(0)); }
break;
case 301:
//#line 1153 "e.y"
{ b.pocket(val_peek(1),"when-sequence");
                                              yyval = b.whenSeq(val_peek(4), val_peek(1), val_peek(0)); }
break;
case 302:
//#line 1169 "e.y"
{ /* binds __return */
                                  b.pocket(val_peek(5),"hard-when");
                                  yyval = ConstMap.fromPairs(new Object[][]{
                                         { "oName", val_peek(5) },
                                         { "whenParams", val_peek(3) },
				         { "whenGuard", b.forValue(val_peek(1), null) },
                                         { "bindReturn", Boolean.TRUE }
				       }).or((ConstMap)val_peek(0), true); }
break;
case 303:
//#line 1178 "e.y"
{ /* XXX should this bind __return ?? */
                                  /* Currently, it does. */
                                  b.pocket(val_peek(2),"easy-when");
                                  b.pocket(val_peek(2),"hard-when");
                                  yyval = ConstMap.fromPairs(new Object[][]{
                                         { "oName", val_peek(2) },
				         { "whenGuard", b.forValue(val_peek(1), null) },
                                         { "bindReturn", Boolean.TRUE }
				       }).or((ConstMap)val_peek(0), true); }
break;
case 304:
//#line 1188 "e.y"
{ /* Doesn't bind __return */
                                  b.pocket(val_peek(0),"easy-when");
                                  yyval = val_peek(0); }
break;
case 305:
//#line 1194 "e.y"
{ b.pocket(val_peek(1),"hard-when");
                                          yyval = val_peek(0); }
break;
case 306:
//#line 1196 "e.y"
{ yyval = b.defaultOptWhenGuard(yylval); }
break;
case 307:
//#line 1197 "e.y"
{ b.reserved(val_peek(1),"throws"); }
break;
case 308:
//#line 1198 "e.y"
{ b.reserved(val_peek(1),"throws"); }
break;
case 309:
//#line 1212 "e.y"
{ yyval = ConstMap.fromPairs(new Object[][]{
	                                    { "whenBody", val_peek(2) },
		                            { "whenCatches", val_peek(1) },
		                            { "whenFinally", val_peek(0) }}); }
break;
case 310:
//#line 1216 "e.y"
{ b.pocket(val_peek(0),"easy-when");
                                     yyval = ConstMap.fromPairs(new Object[][]{
	                                    { "whenBody", val_peek(0) }}); }
break;
case 315:
//#line 1238 "e.y"
{ yyval = b.list(); }
break;
case 316:
//#line 1242 "e.y"
{ yyval = b.list(); }
break;
case 318:
//#line 1251 "e.y"
{ b.pocket(val_peek(0),"verb-string");
                                          yyval = val_peek(0); }
break;
case 319:
//#line 1259 "e.y"
{ b.pocket(val_peek(0),"verb-curry");
                                          yyval = val_peek(0); }
break;
case 320:
//#line 1261 "e.y"
{ b.pocket(val_peek(0),"verb-curry");
                                          b.pocket(val_peek(0),"verb-string");
                                          yyval = val_peek(0); }
break;
case 321:
//#line 1274 "e.y"
{ b.pocket(val_peek(0),"dot-props");
                                          yyval = val_peek(0); }
break;
case 322:
//#line 1276 "e.y"
{ b.pocket(val_peek(0),"dot-props");
                                          yyval = val_peek(0); }
break;
case 323:
//#line 1284 "e.y"
{ yyval = b.varName(val_peek(0)); }
break;
case 324:
//#line 1285 "e.y"
{ b.pocket(val_peek(1),"noun-string");
                                          yyval = b.varName(val_peek(0)); }
break;
case 325:
//#line 1287 "e.y"
{ b.pocket(val_peek(1),"noun-string");
                                          yyval = b.varName(val_peek(0)); }
break;
case 327:
//#line 1296 "e.y"
{ b.reserved(val_peek(0),"keyword \"" +
                                     ((Astro)val_peek(0)).getTag().getTagName() +
                                     "\""); }
break;
case 328:
//#line 1312 "e.y"
{ yyval = b.ident(val_peek(0), "add"); }
break;
case 329:
//#line 1313 "e.y"
{ yyval = b.ident(val_peek(0), "and"); }
break;
case 330:
//#line 1314 "e.y"
{ yyval = b.ident(val_peek(0), "approxDivide"); }
break;
case 331:
//#line 1315 "e.y"
{ yyval = b.ident(val_peek(0), "floorDivide"); }
break;
case 332:
//#line 1316 "e.y"
{ yyval = b.ident(val_peek(0), "shiftLeft"); }
break;
case 333:
//#line 1317 "e.y"
{ yyval = b.ident(val_peek(0), "shiftRight"); }
break;
case 334:
//#line 1318 "e.y"
{ yyval = b.ident(val_peek(0), "remainder"); }
break;
case 335:
//#line 1319 "e.y"
{ yyval = b.ident(val_peek(0), "mod"); }
break;
case 336:
//#line 1320 "e.y"
{ yyval = b.ident(val_peek(0), "multiply"); }
break;
case 337:
//#line 1321 "e.y"
{ yyval = b.ident(val_peek(0), "or"); }
break;
case 338:
//#line 1322 "e.y"
{ yyval = b.ident(val_peek(0), "pow"); }
break;
case 339:
//#line 1323 "e.y"
{ yyval = b.ident(val_peek(0), "subtract"); }
break;
case 340:
//#line 1324 "e.y"
{ yyval = b.ident(val_peek(0), "xor"); }
break;
case 341:
//#line 1335 "e.y"
{ yyval = b.ident(val_peek(0), "add"); }
break;
case 342:
//#line 1336 "e.y"
{ yyval = b.ident(val_peek(0), "and"); }
break;
case 343:
//#line 1337 "e.y"
{ yyval = b.ident(val_peek(0), "approxDivide"); }
break;
case 344:
//#line 1338 "e.y"
{ yyval = b.ident(val_peek(0), "floorDivide"); }
break;
case 345:
//#line 1339 "e.y"
{ yyval = b.ident(val_peek(0), "shiftLeft"); }
break;
case 346:
//#line 1340 "e.y"
{ yyval = b.ident(val_peek(0), "shiftRight"); }
break;
case 347:
//#line 1341 "e.y"
{ yyval = b.ident(val_peek(0), "remainder"); }
break;
case 348:
//#line 1342 "e.y"
{ yyval = b.ident(val_peek(0), "mod"); }
break;
case 349:
//#line 1343 "e.y"
{ yyval = b.ident(val_peek(0), "multiply"); }
break;
case 350:
//#line 1344 "e.y"
{ yyval = b.ident(val_peek(0), "or"); }
break;
case 351:
//#line 1345 "e.y"
{ yyval = b.ident(val_peek(0), "pow"); }
break;
case 352:
//#line 1346 "e.y"
{ yyval = b.ident(val_peek(0), "subtract"); }
break;
case 353:
//#line 1347 "e.y"
{ yyval = b.ident(val_peek(0), "xor"); }
break;
case 354:
//#line 1356 "e.y"
{ yyval = b.getNULL(); }
break;
case 355:
//#line 1357 "e.y"
{ yyval = val_peek(1); }
break;
case 356:
//#line 1365 "e.y"
{ b.pocket(val_peek(2),"accumulator");
                                                  yyval = b.accumulate(val_peek(1),val_peek(0)); }
break;
case 357:
//#line 1370 "e.y"
{ yyval = b.accumFor(val_peek(3),val_peek(1),val_peek(0)); }
break;
case 358:
//#line 1371 "e.y"
{ yyval = b.accumIf(val_peek(1),val_peek(0)); }
break;
case 359:
//#line 1372 "e.y"
{ yyval = b.accumWhile(val_peek(1),val_peek(0)); }
break;
case 360:
//#line 1376 "e.y"
{ yyval = b.accumBody(val_peek(3),
                                                                 b.list(val_peek(2))); }
break;
case 361:
//#line 1378 "e.y"
{ yyval = b.accumBody(val_peek(3),val_peek(2)); }
break;
case 362:
//#line 1379 "e.y"
{ yyval = val_peek(2); }
break;
case 363:
//#line 1384 "e.y"
{ yyval = val_peek(1); }
break;
case 364:
//#line 1388 "e.y"
{ yyval = b.vTable(val_peek(2), val_peek(1)); }
break;
case 365:
//#line 1389 "e.y"
{ b.pocket(NO_POSER,
                                                           "plumbing");
                                                  yyval = b.vTable(null,
                                                                b.list(val_peek(0))); }
break;
case 367:
//#line 1404 "e.y"
{ yyval = b.with(val_peek(2), val_peek(1)); }
break;
case 370:
//#line 1414 "e.y"
{ yyval = b.with(val_peek(2), val_peek(1)); }
break;
case 373:
//#line 1431 "e.y"
{ yyval = b.with(val_peek(1), val_peek(0)); }
break;
case 374:
//#line 1435 "e.y"
{ yyval = null; }
break;
case 375:
//#line 1436 "e.y"
{ b.pocket(NO_POSER,
                                                           "escape-handler");
                                                  yyval = val_peek(0); }
break;
case 376:
//#line 1442 "e.y"
{ yyval = b.matcher(val_peek(1), val_peek(0)); }
break;
case 377:
//#line 1449 "e.y"
{ yyval = null; }
break;
case 378:
//#line 1450 "e.y"
{ yyval = val_peek(0); }
break;
case 379:
//#line 1460 "e.y"
{ yyval = b.oType("", val_peek(1), b.list(),
                                                       b.list(val_peek(0))); }
break;
case 380:
//#line 1469 "e.y"
{ yyval = null; }
break;
case 381:
//#line 1470 "e.y"
{ yyval = val_peek(0); }
break;
case 384:
//#line 1479 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 385:
//#line 1480 "e.y"
{ yyval = b.with(val_peek(2),val_peek(0)); }
break;
case 387:
//#line 1488 "e.y"
{ b.reserved(NO_POSER,"causality"); }
break;
case 388:
//#line 1493 "e.y"
{ yyval = b.mType(val_peek(6), val_peek(4), val_peek(2), val_peek(0)); }
break;
case 389:
//#line 1495 "e.y"
{ yyval = b.mType(val_peek(5), "run", val_peek(2), val_peek(0));}
break;
case 391:
//#line 1503 "e.y"
{ b.reserved(NO_POSER,"causality"); }
break;
case 392:
//#line 1507 "e.y"
{ yyval = b.mType("", "run", val_peek(2), val_peek(0)); }
break;
case 393:
//#line 1508 "e.y"
{ b.pocket(val_peek(5),"one-method-object");
                                            yyval = b.mType("", val_peek(4),    val_peek(2), val_peek(0)); }
break;
case 394:
//#line 1510 "e.y"
{b.pocket(val_peek(5),"one-method-object");
                                            yyval = b.mType("", val_peek(4),    val_peek(2), val_peek(0)); }
break;
case 395:
//#line 1515 "e.y"
{ yyval = val_peek(0); }
break;
case 396:
//#line 1516 "e.y"
{ yyval = val_peek(1); }
break;
case 397:
//#line 1521 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 398:
//#line 1522 "e.y"
{ yyval = b.with(val_peek(2),val_peek(0)); }
break;
case 399:
//#line 1529 "e.y"
{ yyval = b.pType(val_peek(1),val_peek(0)); }
break;
case 400:
//#line 1530 "e.y"
{ yyval = b.pType(null,val_peek(0)); }
break;
case 401:
//#line 1531 "e.y"
{ yyval = b.pType(null,val_peek(0)); }
break;
case 402:
//#line 1539 "e.y"
{ yyval = null; }
break;
case 403:
//#line 1540 "e.y"
{ yyval = val_peek(0); }
break;
case 417:
//#line 1556 "e.y"
{ yyval = "->"; }
break;
//#line 7167 "EParser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



}
//################### END OF CLASS yaccpar ######################
