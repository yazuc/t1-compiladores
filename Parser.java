import java.io.*;

public class Parser {

  private static final int BASE_TOKEN_NUM = 301;
  
  public static final int IDENT  = 301;
  public static final int NUM 	 = 302;
  public static final int WHILE  = 303;
  public static final int IF	 = 304;
  public static final int FI	 = 305;
  public static final int ELSE = 306;
  public static final int OP = 307;
  public static final int LE = 308;
  public static final int FOR = 309;
  public static final int PRINT = 310;
  public static final int RETURN = 311;
  public static final int DEFINE = 312;
  public static final int STRING = 313;
  public static final int PLUS = 314;
  public static final int MINUS = 315;

    public static final String tokenList[] = 
      {"IDENT",
		 "NUM", 
		 "WHILE", 
		 "IF", 
		 "FI",
		 "ELSE",
       "OP",
       "LE",
       "FOR",
       "PRINT",
       "RETURN",
       "DEFINE",
       "STRING",
       "FOR",
       "++",
       "--" };

                                      
  /* referencia ao objeto Scanner gerado pelo JFLEX */
  private Yylex lexer;

  public ParserVal yylval;

  private static int laToken;
  private boolean debug;

  public Parser (Reader r) {
      lexer = new Yylex (r, this);
  }

  private void Prog() {
   
      if (laToken == '{') {
         if (debug) System.out.println("Prog --> Bloco");
         Bloco();
      }
      else 
        yyerror("esperado '{'");
   }

   private void Bloco() {
      verifica('{');
      while (laToken != '}') {
          Cmd();  
      }
      verifica('}');
  }

  private void Cmd() {
      if (laToken == '{') {
         if (debug) System.out.println("Cmd --> Bloco");
         Bloco();
	   }    
      else if (laToken == WHILE) {
         if (debug) System.out.println("Cmd --> WHILE ( E ) Cmd");
         verifica(WHILE);    // laToken = this.yylex(); 
  		   verifica('(');
  		   E();
         verifica(')');
         Cmd();
	   }
      else if (laToken == IDENT ) {
         if (debug) System.out.println("Cmd --> IDENT = E ;");
            verifica(IDENT);  
            verifica('='); 
            E();
            while(laToken == OP){
               verifica(OP);
               E();
            }
            if(laToken == ';')
		         verifica(';');
	   }
      else if (laToken == IF) {
            if (debug) System.out.println("Cmd --> if (E) Cmd RestoIF");
            verifica(IF);
            verifica('(');
            IfExpression();
            verifica(')');
            Cmd();
            RestoIF();
         }
      else if (laToken == DEFINE) {
         if (debug) System.out.println("Cmd --> define IDENT ( Params ) { Bloco }");
         verifica(DEFINE);
         verifica(IDENT); // Nome da função
         verifica('(');         
         Params();        // Lista de parâmetros
         verifica(')');
         Bloco();         // Corpo da função
      } else if (laToken == RETURN) {
         if (debug) System.out.println("Cmd --> RETURN E ;");
         verifica(RETURN);
         processarExpressao();

         if(laToken == ';')
            verifica(';');

      } else if (laToken == PRINT) {
            if (debug) System.out.println("Cmd --> PRINT E ;");
            verifica(PRINT);
            if(laToken == STRING || laToken == IDENT){
               verifica(laToken);
               while(laToken == ','){
                  verifica(',');
                  if(laToken == IDENT){
                     verifica(IDENT);
                  }
                  if(laToken == STRING){
                     verifica(STRING);               
                  }
               }               
            }            
            if(laToken == ';')
               verifica(';');
      } 
      else if (laToken == FOR) {
         if (debug) System.out.println("Cmd --> FOR (init; cond; incr ) Cmd");
         verifica(FOR);    
         verifica('(');           
         init();           
         verifica(';');         
         cond();           
         verifica(';');          
         incr();           
         verifica(')');             
         Cmd();            
	   }
      else yyerror("Esperado {, if, while ou identificador");
   }

   void init() {
      if (laToken == IDENT) {  
          verifica(IDENT);  
          if (laToken == '=') {  
              verifica('=');  
              E();  
          } 
      } 
   }

   void incr() {
      if (laToken == IDENT) {  // Se for uma variável
          verifica(IDENT);  // Verifica o nome da variável
  
          // Verifica operadores de incremento/decremento
          if (laToken == PLUS) {  
              verifica(PLUS);  // Verifica '++' ou '--'     
          } 
          if(laToken == MINUS){
            verifica(MINUS);  // Verifica '++' ou '--'     
          }
      } 
   }
    
   void cond() {
      E();   
      if (laToken == OP || laToken == LE ) {
          verifica(laToken);            
          E();  
      } 
   }
  

   private void Params() {
      if (laToken == IDENT) {
          if (debug) System.out.println("Params --> IDENT");
          verifica(IDENT);  // Reconhece o primeiro parâmetro
          while (laToken == ',') {
              verifica(',');  // Reconhece a vírgula separadora
              verifica(IDENT);  // Reconhece o próximo parâmetro
          }
      } 
  }
   private void IfExpression() {
      if (laToken == IDENT) {  
         if (debug) System.out.println("Params --> IDENT");
         verifica(IDENT);  
         while (laToken == OP) { 
            verifica(OP); 
            
            if (laToken == NUM) {
                  verifica(NUM);  
                  if (debug) System.out.println("Param --> NUM");
            } else if (laToken == IDENT) {
                  verifica(IDENT);  
                  if (debug) System.out.println("Param --> IDENT");
            } 
         }         
      } 
   }

   private void processarExpressao() {
      E();
      while (laToken == OP) { 
         verifica(OP); 
         E(); 
      }
   }

   private void RestoIF() {
       if (laToken == ELSE) {
         if (debug) System.out.println("RestoIF --> else Cmd FI ");
         verifica(ELSE);
         Cmd();
         verifica(FI);
    
	   } else if (laToken == FI){
         if (debug) System.out.println("RestoIF -->  FI  ");
         verifica(FI); 
      }
      else if (ifSingular()){
         if (debug) System.out.println("IF --> EXP  ");
         Cmd();
      }
      else yyerror("Esperado else ou fi");
   }     

   private boolean ifSingular() {
      return laToken == RETURN || laToken == IDENT || laToken == NUM;  
  }

  private void E() {
      if (laToken == IDENT) {
         if (debug) System.out.println("E --> IDENT");
         verifica(IDENT);

         if (laToken == '(') {  
            if (debug) System.out.println("E --> FUNCTION CALL(ident)");
            verifica('(');  
            processarExpressao();  
            verifica(')');  
        }
	   }
      else if (laToken == NUM) {
         if (debug) System.out.println("E --> NUM");
         verifica(NUM);
	   }
      else if (laToken == OP) {
         if (debug) System.out.println("E --> OP");
         verifica(OP);
	   }
      else if (laToken == '(') {
         if (debug) System.out.println("E --> ( E )");
         verifica('(');
         E();        
		 verifica(')');
	   }
 	else yyerror("Esperado operando (, identificador ou numero");
   }


  private void verifica(int expected) {
      if (laToken == expected)
         laToken = this.yylex();
      else {
         String expStr, laStr;       

		expStr = ((expected < BASE_TOKEN_NUM )
                ? ""+(char)expected
			     : tokenList[expected-BASE_TOKEN_NUM]);
         
		laStr = ((laToken < BASE_TOKEN_NUM )
                ? Character.toString((char) laToken)
                : tokenList[laToken-BASE_TOKEN_NUM]);

          yyerror( "esperado token: " + expStr +
                   " na entrada: " + laStr);
     }
   }

   /* metodo de acesso ao Scanner gerado pelo JFLEX */
   private int yylex() {
       int retVal = -1;
       try {
           yylval = new ParserVal(0); //zera o valor do token
           retVal = lexer.yylex(); //le a entrada do arquivo e retorna um token
       } catch (IOException e) {
           System.err.println("IO Error:" + e);
          }
       return retVal; //retorna o token para o Parser 
   }

  /* metodo de manipulacao de erros de sintaxe */
  public void yyerror (String error) {
     System.err.println("Erro: " + error);
     System.err.println("Entrada rejeitada");
     System.out.println("\n\nFalhou!!!");
     System.exit(1);
     
  }

  public void setDebug(boolean trace) {
      debug = true;
  }


  /**
   * Runs the scanner on input files.
   *
   * This main method is the debugging routine for the scanner.
   * It prints debugging information about each returned token to
   * System.out until the end of file is reached, or an error occured.
   *
   * @param args   the command line, contains the filenames to run
   *               the scanner on.
   */
  public static void main(String[] args) {
     Parser parser = null;
     try {
         //linha debug
         //args = new String[] {"Exemplos/exemplo4.txt"};         
         if (args.length == 0)
            parser = new Parser(new InputStreamReader(System.in));
         else 
            parser = new  Parser( new java.io.FileReader(args[0]));

          parser.setDebug(false);
          laToken = parser.yylex();          

          parser.Prog();
     
          if (laToken== Yylex.YYEOF)
             System.out.println("\n\nSucesso!");
          else     
             System.out.println("\n\nFalhou - esperado EOF.");               

        }
        catch (java.io.FileNotFoundException e) {
          System.out.println("File not found : \""+args[0]+"\"");
        }
  }
  
}

