%%

%{
  private Parser yyparser;

  public Yylex(java.io.Reader r, Parser yyparser) {
    this(r);
    this.yyparser = yyparser;
  }


%} 

%integer
%line
%char

DIGIT = [0-9]
NUMBER = [0-9]+(\.[0-9]{1,3})?
WHITE_SPACE_CHAR=[\n\r\ \t\b\012]

%%

"$TRACE_ON"   { yyparser.setDebug(true); }
"$TRACE_OFF"  { yyparser.setDebug(false); }

"while"	 	{ return Parser.WHILE; }
"if"		{ return Parser.IF; }
"else"		{ return Parser.ELSE; }
"fi"		{ return Parser.FI; }
"for"       { return Parser.FOR; }
"print"     { return Parser.PRINT; }
"return"    { return Parser.RETURN; }
"define"    { return Parser.DEFINE; }
"++"    { return Parser.PLUS; }
"--"    { return Parser.MINUS; }

[:jletter:][:jletterdigit:]* { return Parser.IDENT; }  

[0-9]+ 	{ return Parser.NUM; }
"," |
"{" |
"}" |
";" |
"(" |
")" |
"="    	{ return yytext().charAt(0); } 

"=="|
"+" |
"-" |
"*" |
"/"     { return Parser.OP; } 

"<=" |
">=" |
"=="       { return Parser.LE; }

\"([^\"\n\r\\]|\\.)*\" { return Parser.STRING; }


/* Números (double) */
{NUMBER}    { return Parser.NUM; }


{WHITE_SPACE_CHAR}+ { }

. { System.out.println("Erro lexico: caracter invalido: <" + yytext() + ">"); }
