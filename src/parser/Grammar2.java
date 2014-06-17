package parser;

import static lexer.LexerGenerator.Token.*;
import static parser.Grammar.NonTerminal.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*	start	-> S
 *  S	-> AA
 *  A	->	+A | -
 */

public class Grammar2 extends Grammar {
private static final Grammar INSTANCE = new Grammar2();
	
	public static Grammar getInstance() {
		return INSTANCE;
	}

	private Grammar2 () {
		rules = new HashMap<Grammar.NonTerminal, List<List<Alphabet>>>();
		List<List<Alphabet>> alternatives = new ArrayList<List<Alphabet>>();
		List<Alphabet> rhs = new ArrayList<Alphabet>();
		
		rhs.add(S);
		alternatives.add(rhs);
		rules.put(start, alternatives);
		rhs = new ArrayList<Alphabet>();
		alternatives = new ArrayList<List<Alphabet>>();
		
		rhs.add(A);rhs.add(A); 
		alternatives.add(rhs);
		rules.put(S, alternatives);
		rhs = new ArrayList<Alphabet>();
		alternatives = new ArrayList<List<Alphabet>>();
		
		rhs.add(PLUS);rhs.add(A); 
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(MINUS);
		alternatives.add(rhs);
		rules.put(A,alternatives);
		alternatives = new ArrayList<List<Alphabet>>();
		
		computeFirst();
		computeFollow();
	}

}

/*
 * 1.	Start	->	* S
 * 			S	->	* A A
 * 			A	->	* a A
 * 			A	->  * b
 * 
 * 2.	Start	->  S *
 * 
 * 3.	S		->	A * A
 * 		A		->	* a A
 * 		A		-> * b
 * 
 * 4.	A		-> a * A
 * 		A		-> * a A
 * 		A		-> * b
 * 
 * 5.	A		-> b *
 * 
 * 6.	S		-> A A *
 * 
 * 7.	A		->	a * A
 * 		A		->	* a A
 * 		A		->	* b
 */

