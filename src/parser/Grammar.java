package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lexer.LexerGenerator.Token;

public abstract class Grammar {

	public static enum NonTerminal implements Alphabet{
		start,
		program,
		statement,
		declaration,
		assignment,
		out,
		branch,
		loop,
		expr,
		subexpr,
		guard,
		subguard,
		relation,
		S1,
		S,
		A,
		B,
		E,
		T,
		F
	}


	/**
	 * A grammar is modelled as a mapping. For each non-terminal (left-hand side of the CFG)
	 * there is a list of alternatives for the right-hand-side, each again being a list of Alphabet elements
	 * 
	 * For example:
	 * 	S -> a S a | a
	 * is represented by
	 * 	{S, [[a, S, a], [a]]} 
	 */
	protected Map<NonTerminal, List<List<Alphabet>>> rules;
	
	public List<Rule> getRules(NonTerminal name){
		List<Rule> result = new ArrayList<Rule>();
		if(rules.containsKey(name)){
			for(List<Alphabet> rhs : rules.get(name)){
				result.add(new Rule(name, rhs));
			}
		}
		else{
			System.out.println("No rules for "+name);
		}
		return result;
	}
	
	public List<Rule> getRules(){
		List<Rule> result = new ArrayList<Rule>();
		for(NonTerminal nonTerminal : rules.keySet()){
			for(List<Alphabet> rhs : rules.get(nonTerminal)){
				result.add(new Rule(nonTerminal, rhs));
			}
		}
		return result;
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder();
		Iterator<NonTerminal> lhs = rules.keySet().iterator();
		while(lhs.hasNext()){
			NonTerminal rule = lhs.next();
			result.append(rule);
			result.append(" -> ");
			
			Iterator<List<Alphabet>> alternatives = rules.get(rule).iterator();
			while(alternatives.hasNext()){
				List<Alphabet> alternative = alternatives.next();

				Iterator<Alphabet> character = alternative.iterator();
				while(character.hasNext()){
					result.append(character.next());
					if(character.hasNext())
						result.append(" ");
				}
				if(alternatives.hasNext())
					result.append(" | ");
			}
			result.append("\n");
		}
		
		return result.toString();
	}
}
