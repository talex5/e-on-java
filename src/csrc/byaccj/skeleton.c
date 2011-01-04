#include <stdio.h>
#include <string.h>
#include "defs.h"

/*  The banner used here should be replaced with an #ident directive    */
/*  if the target C compiler supports #ident directives.                */
/*                                                                      */
/*  If the skeleton is changed, the banner should be changed so that    */
/*  the altered version can easily be distinguished from the original.  */

unsigned char *banner[] =
{
    "#ifndef lint",
    "static char yysccsid[] = \"@(#)yaccpar     1.8 (Berkeley) 01/20/90\";",
    "#endif",
    "#define YYBYACC 1",
    0
};

unsigned char *jbanner[] =
{
    "//### This file created by BYACC 1.8(/Java extension  0.92)",
    "//### Java capabilities added 7 Jan 97, Bob Jamison",
    "//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten",
    "//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor",
    "//###           01 Jun 99  -- Bob Jamison -- added Runnable support",
    "//### Please send bug reports to rjamison@lincom-asg.com",
    "//### static char yysccsid[] = \"@(#)yaccpar       1.8 (Berkeley) 01/20/90\";",
    "\n\n",
    0
};


unsigned char *tables[] =
{
    "extern short yylhs[];",
    "extern short yylen[];",
    "extern short yydefred[];",
    "extern short yydgoto[];",
    "extern short yysindex[];",
    "extern short yyrindex[];",
    "extern short yygindex[];",
    "extern short yytable[];",
    "extern short yycheck[];",
    "#if YYDEBUG",
    "extern char *yyname[];",
    "extern char *yyrule[];",
    "#endif",
    0
};

unsigned char *jtables[] =
{
    "extern short yylhs[];",
    0
};


unsigned char *header[] =
{
    "#define yyclearin (yychar=(-1))",
    "#define yyerrok (yyerrflag=0)",
    "#ifdef YYSTACKSIZE",
    "#ifndef YYMAXDEPTH",
    "#define YYMAXDEPTH YYSTACKSIZE",
    "#endif",
    "#else",
    "#ifdef YYMAXDEPTH",
    "#define YYSTACKSIZE YYMAXDEPTH",
    "#else",
    "#define YYSTACKSIZE 500",
    "#define YYMAXDEPTH 500",
    "#endif",
    "#endif",
    "int yydebug;",
    "int yynerrs;",
    "int yyerrflag;",
    "int yychar;",
    "short *yyssp;",
    "YYSTYPE *yyvsp;",
    "YYSTYPE yyval;",
    "YYSTYPE yylval;",
    "short yyss[YYSTACKSIZE];",
    "YYSTYPE yyvs[YYSTACKSIZE];",
    "#define yystacksize YYSTACKSIZE",
    0
};

unsigned char *jheader[] =
{
  "\n\n\n",
  "//#####################################################################",
  "@JAVA@// class: %s\n",
  "// does : encapsulates yacc() parser functionality in a Java",
  "//        class for quick code development",
  "//#####################################################################",
  "@JAVAX@public class %s",
  "{\n",
  "boolean yydebug;        //do I want debug output?",
  "int yynerrs;            //number of errors so far",
  "int yyerrflag;          //was there an error?",
  "int yychar;             //the current working character",
  "\n//########## MESSAGES ##########",
  "//###############################################################",
  "// method: debug",
  "//###############################################################",
  "void debug(String msg)",
  "{",
  "  if (yydebug)",
  "    System.err.println(msg);",
  "}",
  "\n//########## STATE STACK ##########",
  "final static int YYSTACKSIZE = 500;  //maximum stack size",
  "int statestk[],stateptr;             //state stack",
  "//###############################################################",
  "// methods: state stack push,pop,drop,peek",
  "//###############################################################",
  "void state_push(int state)",
  "{",
  "  if (stateptr>=YYSTACKSIZE)         //overflowed?",
  "    return;",
  "  statestk[++stateptr]=state;",
  "}",
  "int state_pop()",
  "{",
  "  if (stateptr<0)                    //underflowed?",
  "    return -1;",
  "  return statestk[stateptr--];",
  "}",
  "void state_drop(int cnt)",
  "{",
  "int ptr;",
  "  ptr=stateptr-cnt;",
  "  if (ptr<0)",
  "    return;",
  "  stateptr = ptr;",
  "}",
  "int state_peek(int relative)",
  "{",
  "int ptr;",
  "  ptr=stateptr-relative;",
  "  if (ptr<0)",
  "    return -1;",
  "  return statestk[ptr];",
  "}",
  "//###############################################################",
  "// method: init_stacks : allocate and prepare stacks",
  "//###############################################################",
  "boolean init_stacks()",
  "{",
  "  statestk = new int[YYSTACKSIZE];",
  "  stateptr = -1;",
  "  val_init();",
  "  return true;",
  "}",
  "//###############################################################",
  "// method: dump_stacks : show n levels of the stacks",
  "//###############################################################",
  "void dump_stacks(int count)",
  "{",
  "int i;",
  "  System.err.println(\"=index==state====value=     s:\"+stateptr+\"  v:\"+valptr);",
  "  for (i=0;i<count;i++)",
  "    System.err.println(\" \"+i+\"    \"+statestk[i]+\"      \"+valstk[i]);",
  "  System.err.println(\"======================\");",
  "}",
  0
};


unsigned char *body[] =
{
    "#define YYABORT goto yyabort",
    "#define YYACCEPT goto yyaccept",
    "#define YYERROR goto yyerrlab",
    "int",
    "yyparse()",
    "{",
    "    register int yym, yyn, yystate;",
    "#if YYDEBUG",
    "    register char *yys;",
    "    extern char *getenv();",
    "",
    "    if (yys = getenv(\"YYDEBUG\"))",
    "    {",
    "        yyn = *yys;",
    "        if (yyn >= '0' && yyn <= '9')",
    "            yydebug = yyn - '0';",
    "    }",
    "#endif",
    "",
    "    yynerrs = 0;",
    "    yyerrflag = 0;",
    "    yychar = (-1);",
    "",
    "    yyssp = yyss;",
    "    yyvsp = yyvs;",
    "    *yyssp = yystate = 0;",
    "",
    "yyloop:",
    "    if (yyn = yydefred[yystate]) goto yyreduce;",
    "    if (yychar < 0)",
    "    {",
    "        if ((yychar = yylex()) < 0) yychar = 0;",
    "#if YYDEBUG",
    "        if (yydebug)",
    "        {",
    "            yys = 0;",
    "            if (yychar <= YYMAXTOKEN) yys = yyname[yychar];",
    "            if (!yys) yys = \"illegal-symbol\";",
    "            printf(\"yydebug: state %d, reading %d (%s)\\n\", yystate,",
    "                    yychar, yys);",
    "        }",
    "#endif",
    "    }",
    "    if ((yyn = yysindex[yystate]) && (yyn += yychar) >= 0 &&",
    "            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)",
    "    {",
    "#if YYDEBUG",
    "        if (yydebug)",
    "            printf(\"yydebug: state %d, shifting to state %d\\n\",",
    "                    yystate, yytable[yyn]);",
    "#endif",
    "        if (yyssp >= yyss + yystacksize - 1)",
    "        {",
    "            goto yyoverflow;",
    "        }",
    "        *++yyssp = yystate = yytable[yyn];",
    "        *++yyvsp = yylval;",
    "        yychar = (-1);",
    "        if (yyerrflag > 0)  --yyerrflag;",
    "        goto yyloop;",
    "    }",
    "    if ((yyn = yyrindex[yystate]) && (yyn += yychar) >= 0 &&",
    "            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)",
    "    {",
    "        yyn = yytable[yyn];",
    "        goto yyreduce;",
    "    }",
    "    if (yyerrflag) goto yyinrecovery;",
    "#ifdef lint",
    "    goto yynewerror;",
    "#endif",
    "yynewerror:",
    "    yyerror(\"syntax error\");",
    "#ifdef lint",
    "    goto yyerrlab;",
    "#endif",
    "yyerrlab:",
    "    ++yynerrs;",
    "yyinrecovery:",
    "    if (yyerrflag < 3)",
    "    {",
    "        yyerrflag = 3;",
    "        for (;;)",
    "        {",
    "            if ((yyn = yysindex[*yyssp]) && (yyn += YYERRCODE) >= 0 &&",
    "                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)",
    "            {",
    "#if YYDEBUG",
    "                if (yydebug)",
    "                    printf(\"yydebug: state %d, error recovery shifting\\",
    " to state %d\\n\", *yyssp, yytable[yyn]);",
    "#endif",
    "                if (yyssp >= yyss + yystacksize - 1)",
    "                {",
    "                    goto yyoverflow;",
    "                }",
    "                *++yyssp = yystate = yytable[yyn];",
    "                *++yyvsp = yylval;",
    "                goto yyloop;",
    "            }",
    "            else",
    "            {",
    "#if YYDEBUG",
    "                if (yydebug)",
    "                    printf(\"yydebug: error recovery discarding state %d\
\\n\",",
    "                            *yyssp);",
    "#endif",
    "                if (yyssp <= yyss) goto yyabort;",
    "                --yyssp;",
    "                --yyvsp;",
    "            }",
    "        }",
    "    }",
    "    else",
    "    {",
    "        if (yychar == 0) goto yyabort;",
    "#if YYDEBUG",
    "        if (yydebug)",
    "        {",
    "            yys = 0;",
    "            if (yychar <= YYMAXTOKEN) yys = yyname[yychar];",
    "            if (!yys) yys = \"illegal-symbol\";",
    "            printf(\"yydebug: state %d, error recovery discards token %d\
 (%s)\\n\",",
    "                    yystate, yychar, yys);",
    "        }",
    "#endif",
    "        yychar = (-1);",
    "        goto yyloop;",
    "    }",
    "yyreduce:",
    "#if YYDEBUG",
    "    if (yydebug)",
    "        printf(\"yydebug: state %d, reducing by rule %d (%s)\\n\",",
    "                yystate, yyn, yyrule[yyn]);",
    "#endif",
    "    yym = yylen[yyn];",
    "    yyval = yyvsp[1-yym];",
    "    switch (yyn)",
    "    {",
    0
};

unsigned char *jbody[] =
{
    "//###############################################################",
    "// method: yylexdebug : check lexer state",
    "//###############################################################",
    "void yylexdebug(int state,int ch)",
    "{",
    "String s=null;",
    "  if (ch < 0) ch=0;",
    "  if (ch <= YYMAXTOKEN) //check index bounds",
    "     s = yyname[ch];    //now get it",
    "  if (s==null)",
    "    s = \"illegal-symbol\";",
    "  debug(\"state \"+state+\", reading \"+ch+\" (\"+s+\")\");",
    "}\n\n\n",
    "//###############################################################",
    "// method: yyparse : parse input and execute indicated items",
    "//###############################################################",
    "int yyparse()",
    "{",
    "int yyn;       //next next thing to do",
    "int yym;       //",
    "int yystate;   //current parsing state from state table",
    "String yys;    //current token string",
    "boolean doaction;",
    "  init_stacks();",
    "  yynerrs = 0;",
    "  yyerrflag = 0;",
    "  yychar = -1;          //impossible char forces a read",
    "  yystate=0;            //initial state",
    "  state_push(yystate);  //save it",
    "  while (true) //until parsing is done, either correctly, or w/error",
    "    {",
    "    doaction=true;",
    "    if (yydebug) debug(\"loop\"); ",
    "    //#### NEXT ACTION (from reduction table)",
    "    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])",
    "      {",
    "      if (yydebug) debug(\"yyn:\"+yyn+\"  state:\"+yystate+\"  char:\"+yychar);",
    "      if (yychar < 0)      //we want a char?",
    "        {",
    "        yychar = yylex();  //get next token",
    "        //#### ERROR CHECK ####",
    "        if (yychar < 0)    //it it didn't work/error",
    "          {",
    "          yychar = 0;      //change it to default string (no -1!)",
    "          if (yydebug)",
    "            yylexdebug(yystate,yychar);",
    "          }",
    "        }//yychar<0",
    "      yyn = yysindex[yystate];  //get amount to shift by (shift index)",
    "      if ((yyn != 0) && (yyn += yychar) >= 0 &&",
    "          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)",
    "        {",
    "        if (yydebug)",
    "          debug(\"state \"+yystate+\", shifting to state \"+yytable[yyn]+\"\");",
    "        //#### NEXT STATE ####",
    "        yystate = yytable[yyn];//we are in a new state",
    "        state_push(yystate);   //save it",
    "        val_push(yylval);      //push our lval as the input for next rule",
    "        yychar = -1;           //since we have 'eaten' a token, say we need another",
    "        if (yyerrflag > 0)     //have we recovered an error?",
    "           --yyerrflag;        //give ourselves credit",
    "        doaction=false;        //but don't process yet",
    "        break;   //quit the yyn=0 loop",
    "        }",
    "",
    "    yyn = yyrindex[yystate];  //reduce",
    "    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&",
    "            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)",
    "      {   //we reduced!",
    "      if (yydebug) debug(\"reduce\");",
    "      yyn = yytable[yyn];",
    "      doaction=true; //get ready to execute",
    "      break;         //drop down to actions",
    "      }",
    "    else //ERROR RECOVERY",
    "      {",
    "      if (yyerrflag==0)",
    "        {",
    "        yyerror(\"syntax error\");",
    "        yynerrs++;",
    "        }",
    "      if (yyerrflag < 3) //low error count?",
    "        {",
    "        yyerrflag = 3;",
    "        while (true)   //do until break",
    "          {",
    "          if (stateptr<0)   //check for under & overflow here",
    "            {",
    "            yyerror(\"stack underflow. aborting...\");  //note lower case 's'",
    "            return 1;",
    "            }",
    "          yyn = yysindex[state_peek(0)];",
    "          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&",
    "                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)",
    "            {",
    "            if (yydebug)",
    "              debug(\"state \"+state_peek(0)+\", error recovery shifting to state \"+yytable[yyn]+\" \");",
    "            yystate = yytable[yyn];",
    "            state_push(yystate);",
    "            val_push(yylval);",
    "            doaction=false;",
    "            break;",
    "            }",
    "          else",
    "            {",
    "            if (yydebug)",
    "              debug(\"error recovery discarding state \"+state_peek(0)+\" \");",
    "            if (stateptr<0)   //check for under & overflow here",
    "              {",
    "              yyerror(\"Stack underflow. aborting...\");  //capital 'S'",
    "              return 1;",
    "              }",
    "            state_pop();",
    "            val_pop();",
    "            }",
    "          }",
    "        }",
    "      else            //discard this token",
    "        {",
    "        if (yychar == 0)",
    "          return 1; //yyabort",
    "        if (yydebug)",
    "          {",
    "          yys = null;",
    "          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];",
    "          if (yys == null) yys = \"illegal-symbol\";",
    "          debug(\"state \"+yystate+\", error recovery discards token \"+yychar+\" (\"+yys+\")\");",
    "          }",
    "        yychar = -1;  //read another",
    "        }",
    "      }//end error recovery",
    "    }//yyn=0 loop",
    "    if (!doaction)   //any reason not to proceed?",
    "      continue;      //skip action",
    "    yym = yylen[yyn];          //get count of terminals on rhs",
    "    if (yydebug)",
    "      debug(\"state \"+yystate+\", reducing \"+yym+\" by rule \"+yyn+\" (\"+yyrule[yyn]+\")\");",
    "    if (yym>0)                 //if count of rhs not 'nil'",
    "      yyval = val_peek(yym-1); //get current semantic value",
    "    switch(yyn)",
    "      {",
    "//########## USER-SUPPLIED ACTIONS ##########",
    0
};


unsigned char *trailer[] =
{
    "    }",
    "    yyssp -= yym;",
    "    yystate = *yyssp;",
    "    yyvsp -= yym;",
    "    yym = yylhs[yyn];",
    "    if (yystate == 0 && yym == 0)",
    "    {",
    "#if YYDEBUG",
    "        if (yydebug)",
    "            printf(\"yydebug: after reduction, shifting from state 0 to\\",
    " state %d\\n\", YYFINAL);",
    "#endif",
    "        yystate = YYFINAL;",
    "        *++yyssp = YYFINAL;",
    "        *++yyvsp = yyval;",
    "        if (yychar < 0)",
    "        {",
    "            if ((yychar = yylex()) < 0) yychar = 0;",
    "#if YYDEBUG",
    "            if (yydebug)",
    "            {",
    "                yys = 0;",
    "                if (yychar <= YYMAXTOKEN) yys = yyname[yychar];",
    "                if (!yys) yys = \"illegal-symbol\";",
    "                printf(\"yydebug: state %d, reading %d (%s)\\n\",",
    "                        YYFINAL, yychar, yys);",
    "            }",
    "#endif",
    "        }",
    "        if (yychar == 0) goto yyaccept;",
    "        goto yyloop;",
    "    }",
    "    if ((yyn = yygindex[yym]) && (yyn += yystate) >= 0 &&",
    "            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)",
    "        yystate = yytable[yyn];",
    "    else",
    "        yystate = yydgoto[yym];",
    "#if YYDEBUG",
    "    if (yydebug)",
    "        printf(\"yydebug: after reduction, shifting from state %d \\",
    "to state %d\\n\", *yyssp, yystate);",
    "#endif",
    "    if (yyssp >= yyss + yystacksize - 1)",
    "    {",
    "        goto yyoverflow;",
    "    }",
    "    *++yyssp = yystate;",
    "    *++yyvsp = yyval;",
    "    goto yyloop;",
    "yyoverflow:",
    "    yyerror(\"yacc stack overflow\");",
    "yyabort:",
    "    return (1);",
    "yyaccept:",
    "    return (0);",
    "}",
    0
};

unsigned char *jtrailer[] =
{
    "//########## END OF USER-SUPPLIED ACTIONS ##########",
    "    }//switch",
    "    //#### Now let's reduce... ####",
    "    if (yydebug) debug(\"reduce\");",
    "    state_drop(yym);             //we just reduced yylen states",
    "    yystate = state_peek(0);     //get new state",
    "    val_drop(yym);               //corresponding value drop",
    "    yym = yylhs[yyn];            //select next TERMINAL(on lhs)",
    "    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL",
    "      {",
    "      debug(\"After reduction, shifting from state 0 to state \"+YYFINAL+\"\");",
    "      yystate = YYFINAL;         //explicitly say we're done",
    "      state_push(YYFINAL);       //and save it",
    "      val_push(yyval);           //also save the semantic value of parsing",
    "      if (yychar < 0)            //we want another character?",
    "        {",
    "        yychar = yylex();        //get next character",
    "        if (yychar<0) yychar=0;  //clean, if necessary",
    "        if (yydebug)",
    "          yylexdebug(yystate,yychar);",
    "        }",
    "      if (yychar == 0)          //Good exit (if lex returns 0 ;-)",
    "         break;                 //quit the loop--all DONE",
    "      }//if yystate",
    "    else                        //else not done yet",
    "      {                         //get next state and push, for next yydefred[]",
    "      yyn = yygindex[yym];      //find out where to go",
    "      if ((yyn != 0) && (yyn += yystate) >= 0 &&",
    "            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)",
    "        yystate = yytable[yyn]; //get new state",
    "      else",
    "        yystate = yydgoto[yym]; //else go to new defred",
    "      debug(\"after reduction, shifting from state \"+state_peek(0)+\" to state \"+yystate+\"\");",
    "      state_push(yystate);     //going again, so push state & val...",
    "      val_push(yyval);         //for next action",
    "      }",
    "    }//main loop",
    "  return 0;//yyaccept!!",
    "}",
    "//## end of method parse() ######################################",
    "\n\n",
    "//## run() --- for Thread #######################################",
    "public void run()",
    "{",
    "   yyparse();",
    "}",
    "//## end of method run() ########################################",
    "\n\n",
    "//## Constructor ################################################",
    "@JAVA@public %s()\n",
    "{",
    "}\n",
    "@JAVA@public %s(boolean debug_me)\n",
    "{",
    "  yydebug=debug_me;",
    "}",
    "//###############################################################",
    "\n\n",
    "}",
    "//################### END OF CLASS yaccpar ######################",
    0
};

unsigned char *jtrailer_nothread[] =
{
    "//########## END OF USER-SUPPLIED ACTIONS ##########",
    "    }//switch",
    "    //#### Now let's reduce... ####",
    "    if (yydebug) debug(\"reduce\");",
    "    state_drop(yym);             //we just reduced yylen states",
    "    yystate = state_peek(0);     //get new state",
    "    val_drop(yym);               //corresponding value drop",
    "    yym = yylhs[yyn];            //select next TERMINAL(on lhs)",
    "    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL",
    "      {",
    "      debug(\"After reduction, shifting from state 0 to state \"+YYFINAL+\"\");",
    "      yystate = YYFINAL;         //explicitly say we're done",
    "      state_push(YYFINAL);       //and save it",
    "      val_push(yyval);           //also save the semantic value of parsing",
    "      if (yychar < 0)            //we want another character?",
    "        {",
    "        yychar = yylex();        //get next character",
    "        if (yychar<0) yychar=0;  //clean, if necessary",
    "        if (yydebug)",
    "          yylexdebug(yystate,yychar);",
    "        }",
    "      if (yychar == 0)          //Good exit (if lex returns 0 ;-)",
    "         break;                 //quit the loop--all DONE",
    "      }//if yystate",
    "    else                        //else not done yet",
    "      {                         //get next state and push, for next yydefred[]",
    "      yyn = yygindex[yym];      //find out where to go",
    "      if ((yyn != 0) && (yyn += yystate) >= 0 &&",
    "            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)",
    "        yystate = yytable[yyn]; //get new state",
    "      else",
    "        yystate = yydgoto[yym]; //else go to new defred",
    "      debug(\"after reduction, shifting from state \"+state_peek(0)+\" to state \"+yystate+\"\");",
    "      state_push(yystate);     //going again, so push state & val...",
    "      val_push(yyval);         //for next action",
    "      }",
    "    }//main loop",
    "  return 0;//yyaccept!!",
    "}",
    "//## end of method parse() ######################################",
    "\n\n",
    "}",
    "//################### END OF CLASS yaccpar ######################",
    0
};


void write_section(unsigned char **section)
{
int i;
FILE *fp;
  fp = code_file;
  if (section==jtrailer)
    {
    if (strcmp(java_extend_name,"Thread")!=0)
      section=jtrailer_nothread;
    }
  for (i = 0; section[i]; ++i)
    {
         ++outline;
    if (strncmp(section[i],"@JAVA@",6)==0)
           fprintf(fp,&(section[i][6]),java_class_name);
    else if (strncmp(section[i],"@JAVAX@",7)==0)
           {
      fprintf(fp,&(section[i][7]),java_class_name);
      if (java_extend_name[0]!='\0')
        fprintf(fp," extends %s",java_extend_name);
      if (java_implement_name[0]!='\0')
        fprintf(fp," implements %s",java_implement_name);
      fprintf(fp,"\n");
      }
    else
           fprintf(fp, "%s\n", section[i]);
    }
}



