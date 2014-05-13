package lexer;

import java.util.Iterator;
import java.util.List;

public class LexerGenerator {
	// the automata's alphabet is every character that Java can represent
	// however the relevant letters are the following
	final static protected char [] alpha = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	final static protected char [] numbers = {'0','1','2','3','4','5','6','7','8','9'};
	final static protected char [] underScoreNumerical = {'_','0','1','2','3','4','5','6','7','8','9'};
	final static protected char [] special = {':','=','(',')','{','}','+','-','*','/','<','>','!','$','&','|',';','"',' ','\t','\r','\n'};

	public static enum Token{
		WHILE,
		WRITE,
		READ,
		INT,
		IF,
		ELSE,
		TRUE,
		FALSE,
		ASSIGN,
		LPAR,
		RPAR,
		LBRACE,
		RBRACE,
		PLUS,
		MINUS,
		TIMES,
		DIV,
		LEQ,
		LT,
		GEQ,
		GT,
		EQ,
		NEQ,
		AND,
		OR,
		NOT,
		SEMICOLON,
		ID,
		STRING,
		NUMBER,
		COMMENT,
		BLANK,
		EOF}
	
	public static List<Symbol> analyse(String input) throws LexerException{
		return analyse(input, true);
	}
	
	public static List<Symbol> analyse(String input, 
			boolean suppressBlankAndComments) throws LexerException{
		BacktrackingDFA bdfa = new BacktrackingDFA();
		List<Symbol> analysis = null;
		analysis = bdfa.run(input);
		
		if(suppressBlankAndComments){
			Iterator<Symbol> it = analysis.iterator();
			while(it.hasNext()){
				Token t = it.next().getToken();
				if(Token.BLANK == t || Token.COMMENT == t){
					it.remove();
				}
			}
		}

		return analysis;
	}

}
