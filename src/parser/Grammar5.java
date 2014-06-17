package parser;

import static lexer.LexerGenerator.Token.*;
import static parser.Grammar.NonTerminal.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*	start	-> S
 *  S	-> A | B
 *  A	-> + A - | + - 
 *  B	-> + + B $ | + + $
 */

public class Grammar5 extends Grammar {
private static final Grammar INSTANCE = new Grammar5();
	
	public static Grammar getInstance() {
		return INSTANCE;
	}

	private Grammar5 () {
		rules = new HashMap<Grammar.NonTerminal, List<List<Alphabet>>>();
		List<List<Alphabet>> alternatives = new ArrayList<List<Alphabet>>();
		List<Alphabet> rhs = new ArrayList<Alphabet>();
		
		rhs.add(S);
		alternatives.add(rhs);
		rules.put(start, alternatives);
		rhs = new ArrayList<Alphabet>();
		alternatives = new ArrayList<List<Alphabet>>();
		
		rhs.add(A); 
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(B);
		alternatives.add(rhs);
		rules.put(S,alternatives);
		rhs = new ArrayList<Alphabet>();
		alternatives = new ArrayList<List<Alphabet>>();		

		rhs.add(PLUS);rhs.add(A); rhs.add(MINUS) ;
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(PLUS); rhs.add(MINUS) ;
		alternatives.add(rhs);
		rules.put(A, alternatives);
		rhs = new ArrayList<Alphabet>();
		alternatives = new ArrayList<List<Alphabet>>();
		
		rhs.add(PLUS);rhs.add(PLUS);rhs.add(B); rhs.add(EOF) ;
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(PLUS);rhs.add(PLUS); rhs.add(EOF) ;
		alternatives.add(rhs);
		rules.put(B, alternatives);
		rhs = new ArrayList<Alphabet>();
		alternatives = new ArrayList<List<Alphabet>>();

		computeFirst();
		computeFollow();
	}

}

