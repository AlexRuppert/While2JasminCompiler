package lexer;

public class LexerGenerator {
	// the automata's alphabet is every character that Java can represent
	// however the relevant letters are the following
	final static protected char [] alpha = {//individual letters
		};
	final static protected char [] numbers = {//individual digits
		};
	final static protected char [] underScoreNumerical = {//underscore and digits
		};
	final static protected char [] special = {//special characters like parenthesis, line breaks, ...
		};

	public static enum Token{
		WHILE, COMMENT,
		// and so on
		}

}
