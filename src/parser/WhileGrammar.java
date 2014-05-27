package parser;

import static lexer.LexerGenerator.Token.*; // static => use Tokens without
                                            // prefixing them with NonTerminal.
import static parser.Grammar.NonTerminal.*; // static => use NonTerminals without
                                            // prefixing them with NonTerminal.

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WhileGrammar extends Grammar {
	
	/**
	 * Single instance created upon class loading.
	 */
	private static final Grammar INSTANCE = new WhileGrammar();
	
	public static Grammar getInstance() {
		return INSTANCE;
	}

	/**
	 * This class repre
	 */
	private WhileGrammar () {
		rules = new HashMap<Grammar.NonTerminal, List<List<Alphabet>>>();
		List<List<Alphabet>> alternatives = new ArrayList<List<Alphabet>>();
		List<Alphabet> rhs = new ArrayList<Alphabet>();
		
		// start -> program $ 
		rhs.add(program); rhs.add(EOF);
		alternatives.add(rhs);
		rules.put(start, alternatives);
		rhs = new ArrayList<Alphabet>();
		alternatives = new ArrayList<List<Alphabet>>();
		
		// program -> statement program | statement
		rhs.add(statement); rhs.add(program);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(statement);
		alternatives.add(rhs);
		rules.put(program, alternatives);
		rhs = new ArrayList<Alphabet>();
		alternatives = new ArrayList<List<Alphabet>>();
		
		// statement -> declaration SEM | assignment SEM | branch | loop | out SEM
		rhs.add(declaration); rhs.add(SEMICOLON);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(assignment); rhs.add(SEMICOLON);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(branch);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(loop);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(out); rhs.add(SEMICOLON);
		alternatives.add(rhs);
		rules.put(statement, alternatives);
		rhs = new ArrayList<Alphabet>();
		alternatives = new ArrayList<List<Alphabet>>();
		
		// declaration -> INT ID
		rhs.add(INT); rhs.add(ID);
		alternatives.add(rhs);
		rules.put(declaration, alternatives);
		alternatives = new ArrayList<List<Alphabet>>();
		rhs = new ArrayList<Alphabet>();
		
		// assignment -> ID ASSIGN expr | ID ASSIGN READ LBRAC RBRAC
		rhs.add(ID); rhs.add(ASSIGN); rhs.add(expr);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(ID); rhs.add(ASSIGN); rhs.add(READ); rhs.add(LPAR); rhs.add(RPAR);
		alternatives.add(rhs);
		rules.put(assignment, alternatives);
		alternatives = new ArrayList<List<Alphabet>>();
		rhs = new ArrayList<Alphabet>();
		
		// out -> WRITE LBRAC expr RBRAC | WRITE LBRAC STRING RBRAC
		rhs.add(WRITE); rhs.add(LPAR); rhs.add(expr); rhs.add(RPAR);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(WRITE); rhs.add(LPAR); rhs.add(STRING); rhs.add(RPAR);
		alternatives.add(rhs);
		rules.put(out, alternatives);
		alternatives = new ArrayList<List<Alphabet>>();
		rhs = new ArrayList<Alphabet>();
		
		// branch -> IF LBRAC guard RBRAC LCBRAC prog RCBRAC |
		//           IF LBRAC guard RBRAC LCBRAC prog RCBRAC ELSE LCBRAC prog RCBRAC
		rhs.add(IF); rhs.add(LPAR); rhs.add(guard); rhs.add(RPAR); rhs.add(LBRACE); rhs.add(program); rhs.add(RBRACE);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(IF); rhs.add(LPAR); rhs.add(guard); rhs.add(RPAR); rhs.add(LBRACE); rhs.add(program); rhs.add(RBRACE);
		rhs.add(ELSE); rhs.add(LBRACE); rhs.add(program); rhs.add(RBRACE);
		alternatives.add(rhs);
		rules.put(branch, alternatives);
		alternatives = new ArrayList<List<Alphabet>>();
		rhs = new ArrayList<Alphabet>();
		
		// loop -> WHILE LBRAC guard RBRAC LCBRAC prog RCBRAC
		rhs.add(WHILE); rhs.add(LPAR); rhs.add(guard); rhs.add(RPAR); rhs.add(LBRACE); rhs.add(program); rhs.add(RBRACE);
		alternatives.add(rhs);
		rules.put(loop, alternatives);
		alternatives = new ArrayList<List<Alphabet>>();
		rhs = new ArrayList<Alphabet>();
		
		// expr -> NUM | ID | subexpr | LBRAC subexpr RBRAC
		rhs.add(NUMBER);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(ID);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(subexpr);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(LPAR); rhs.add(subexpr); rhs.add(RPAR);
		alternatives.add(rhs);
		rules.put(expr, alternatives);
		alternatives = new ArrayList<List<Alphabet>>();
		rhs = new ArrayList<Alphabet>();
		
		// subexpr -> expr PLUS expr | expr MINUS expr | expr TIMES expr | expr DIV expr
		rhs.add(expr); rhs.add(PLUS); rhs.add(expr);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(expr); rhs.add(MINUS); rhs.add(expr);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(expr); rhs.add(TIMES); rhs.add(expr);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(expr); rhs.add(DIV); rhs.add(expr);
		alternatives.add(rhs);
		rules.put(subexpr, alternatives);
		alternatives = new ArrayList<List<Alphabet>>();
		rhs = new ArrayList<Alphabet>();
		
		// guard -> relation | subguard | LBRAC subguard RBRAC | NOT LBRAC guard RBRAC
		rhs.add(relation);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(subguard);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(LPAR); rhs.add(subguard); rhs.add(RPAR);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(NOT); rhs.add(LPAR); rhs.add(guard); rhs.add(RPAR);
		alternatives.add(rhs);
		rules.put(guard, alternatives);
		alternatives = new ArrayList<List<Alphabet>>();
		rhs = new ArrayList<Alphabet>();
		
		// subguard -> guard AND guard | guard OR guard
		rhs.add(guard); rhs.add(AND); rhs.add(guard);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(guard); rhs.add(OR); rhs.add(guard);
		alternatives.add(rhs);
		rules.put(subguard, alternatives);
		alternatives = new ArrayList<List<Alphabet>>();
		rhs = new ArrayList<Alphabet>();
		
		// relation -> expr LT expr | expr LEQ expr | expr EQ expr | expr NEQ expr | expr GEQ expr | expr GT expr
		rhs.add(expr); rhs.add(LT); rhs.add(expr);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(expr); rhs.add(LEQ); rhs.add(expr);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(expr); rhs.add(EQ); rhs.add(expr);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(expr); rhs.add(NEQ); rhs.add(expr);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(expr); rhs.add(GEQ); rhs.add(expr);
		alternatives.add(rhs);
		rhs = new ArrayList<Alphabet>();
		rhs.add(expr); rhs.add(GT); rhs.add(expr);
		alternatives.add(rhs);
		rules.put(relation, alternatives);
		alternatives = new ArrayList<List<Alphabet>>();
		rhs = new ArrayList<Alphabet>();
		
	}

}
