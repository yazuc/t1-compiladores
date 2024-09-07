%%

%{
  private AsdrSample yyparser;

  public Yylex(java.io.Reader r, AsdrSample yyparser) {
    this(r);
    this.yyparser = yyparser;
  }


%} 

%integer
%line
%char

DIGIT = [0-9]
NUMBER = {DIGIT}+("."{DIGIT}+)?
WHITE_SPACE_CHAR=[\n\r\ \t\b\012]

%%

"$TRACE_ON"   { yyparser.setDebug(true); }
"$TRACE_OFF"  { yyparser.setDebug(false); }

"while"	 	{ return AsdrSample.WHILE; }
"if"		{ return AsdrSample.IF; }
"else"		{ return AsdrSample.ELSE; }
"fi"		{ return AsdrSample.FI; }
"for"       { return AsdrSample.FOR; }
"print"     { return AsdrSample.PRINT; }
"return"    { return AsdrSample.RETURN; }
"define"    { return AsdrSample.DEFINE; }

[:jletter:][:jletterdigit:]* { return AsdrSample.IDENT; }  

[0-9]+ 	{ return AsdrSample.NUM; }

"{" |
"}" |
";" |
"(" |
")" |
"+" |
"="    	{ return yytext().charAt(0); } 

"+" |
"-" |
"*" |
"/"     { return AsdrSample.OP; } 

"<="        { return AsdrSample.LE; }
"="         { return AsdrSample.ASSIGN; }

/* Números (double) */
{NUMBER}    { return AsdrSample.NUM; }


{WHITE_SPACE_CHAR}+ { }

. { System.out.println("Erro lexico: caracter invalido: <" + yytext() + ">"); }
