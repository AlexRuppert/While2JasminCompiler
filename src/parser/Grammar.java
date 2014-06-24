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


	protected Map<NonTerminal, List<List<Alphabet>>> rules;
	public Map<NonTerminal, Set<Alphabet>> first;
	public Map<NonTerminal, Set<Alphabet>> follow;
	
	/**
	 * Given (implicitly as the class' attribute) the rules of the grammar,
	 * compute the first set for each non-terminal. 
	 */
	void computeFirst(){
		/*
		 * 1. If X is terminal, then FIRST(X) is {X}.
           2. If X -> e is a production, then add e to FIRST(X). (e stands for epsilon here)
           3. If X is nonterminal and X -> Y1 Y2 ... Yk. is a production, then place a in FIRST(X) if for some i, a is in
              FIRST(Yi), and e is in all of FIRST(Y1), ... , FIRST(Yi-1); that is, Y1, ... ,Yi-1 => e. If e is in FIRST(Yj) for
              all j = 1, 2, ... , k, then add e to FIRST(X). For example, everything in FIRST(Y1) is surely in
              FIRST(X). If Y1 does not derive e, then we add nothing more to FIRST(X), but if Y1 => e, then we add
              FIRST(Y2) and so on.
		 */
		first = new HashMap<NonTerminal, Set<Alphabet>>();
		for(NonTerminal nonTerminal : NonTerminal.values()){
			if(rules.containsKey(nonTerminal)){
				Set<Alphabet> s = computeFirst(getRules(nonTerminal), new HashSet<NonTerminal>());
				first.put(nonTerminal, s);
			}
		}
	}
	
	private Set<Alphabet> computeFirst(List<Rule> list, Set<NonTerminal> visited){
		Set<Alphabet> result = new HashSet<Alphabet>();
		if(!visited.contains(list.get(0).getLhs())){
			for(Rule rule : list){
				//The case that the right hand side of a rule is epsilon is ignored as we use epsilon free grammars by assumption
				if(rule.getRhs()[0] instanceof Token){
					//if first symbol to the right is a terminal, take that
					result.add(rule.getRhs()[0]);
				}
				else{
					//if first symbol to the right is a non-terminal take the first set of that
					//ignore the rest as we assume there are no epsilon productions
					assert(rule.getRhs()[0] instanceof NonTerminal);
					NonTerminal nonTerminal = (NonTerminal)rule.getRhs()[0];
					//the visited set avoids the following infinite looping for A->B...|...; B->A...|...
					visited.add(rule.getLhs());
					result.addAll(computeFirst(getRules(nonTerminal), visited));
				}
			}
		}
		return result;
	}
	
	/**
	 * Given (implicitly as the class' attribute) the rules of the grammar,
	 * compute the follow set for each non-terminal. 
	 */
	void computeFollow(){
		/*
		 * 1. Place $ in FOLLOW(S), where S is the start symbol and $ is the input right endmarker.
		   2. If there is a production A -> aBb, then everything in FIRST(b), except for e, is placed in FOLLOW(B).
		   3. If there is a production A -> aB, or a production A -> aBb where FIRST(b) contains e (i.e., b => e),
		      then everything in FOLLOW(A) is in FOLLOW(B).
		 */
		follow = new HashMap<NonTerminal, Set<Alphabet>>();
		for(NonTerminal nonTerminal : NonTerminal.values()){
			if(rules.containsKey(nonTerminal)){
				Set<Alphabet> s = computeFollow(nonTerminal, new HashSet<NonTerminal>());
				follow.put(nonTerminal, s);
			}
		}
	}
	
	private Set<Alphabet> computeFollow(NonTerminal nonTerminal, Set<NonTerminal> visited){
		Set<Alphabet> result = new HashSet<Alphabet>();
		if(!visited.contains(nonTerminal)){
			for(Rule rule : getRules()){
				int index = Arrays.asList(rule.getRhs()).indexOf(nonTerminal);
				if(index > -1){
					//either there is something to the right of the nonTerminal and we take the first-set of that
					if(index < rule.getRhs().length-1){
						index++;//consider the symbol to the right 
						if(rule.getRhs()[index] instanceof Token){
							Token token = (Token)rule.getRhs()[index];
							result.add(token);
						}
						else{
							assert(rule.getRhs()[index] instanceof NonTerminal);
							NonTerminal nt = (NonTerminal)rule.getRhs()[index];
							result.addAll(first.get(nt));
						}
					}
					//... or the nonTerminal is the rightmost symbol of the production in which case we take the 
					// follow-set of the left hand side
					else{
						assert(index == rule.getRhs().length-1);
						visited.add(nonTerminal);
						result.addAll(computeFollow(rule.getLhs(), visited));
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Given a non-terminal X return all productions (Rule objects) with X on the left hand side.
	 * @param NonTerminal object
	 * @return List of Rules for given non-terminal
	 */
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
	
	/**
	 * @return a list of all productions of this grammar
	 */
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
