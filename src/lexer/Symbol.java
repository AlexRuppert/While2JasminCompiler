package lexer;

import lexer.LexerGenerator.Token;

public class Symbol{


	private final Token token;
	private final String attribute;

	public Symbol(Token t, String a){ 
		token = t;
		attribute = a;   
	}

	public Token getToken(){
		return token;
	}

	public String getAttribute() {
		return attribute;
	}

	public String toString()  { 
		return "(" + token.toString() + ", " + attribute.toString() + ")"; 
	}

}
