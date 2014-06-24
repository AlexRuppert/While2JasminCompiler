package parser;

import java.util.List;

import parser.Grammar.NonTerminal;

public class Rule {
	
	private final NonTerminal lhs;
	private final Alphabet [] rhs;
	
	public NonTerminal getLhs() {
		return lhs;
	}

	public Alphabet[] getRhs() {
		return rhs;
	}

	public Rule(NonTerminal lhs, Alphabet...  rhs){
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	public Rule(NonTerminal lhs, List<Alphabet> rhs){
		this.lhs = lhs;
		this.rhs = new Alphabet[rhs.size()];
		for(int i = 0; i < rhs.size(); i++){
			this.rhs[i] = rhs.get(i);
		}
	}
}
