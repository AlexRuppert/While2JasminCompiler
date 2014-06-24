package parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import parser.Grammar.NonTerminal;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class GotoDFA {
	
	Grammar grammar;
	public Set<LR0Set> states = new HashSet<LR0Set>();
	private LR0Set initialState;
	public LR0Set getInitialState() {
		return initialState;
	}

	Table<LR0Set, Alphabet, LR0Set> transitions = HashBasedTable.create();
	
	public GotoDFA(Grammar grammar) {
		this.grammar = grammar;
		generateLR0StateSpace();
	}
	
	public void addState(LR0Set state){
		assert(!states.contains(state));
		states.add(state);
	}
	
	public void addTransition(LR0Set source, Alphabet letter, LR0Set target){
		assert(states.contains(source));
		assert(states.contains(target));
		transitions.put(source, letter, target);
	}
	
	/**
	 * 
	 * @param source
	 * @param letter
	 * @return Returns the letter-successor of source, or null if no such mapping exists.
	 */
	public LR0Set getSuccessor(LR0Set source, Alphabet letter){
		return transitions.get(source, letter);
	}
	
	public void generateLR0StateSpace(){
		// start with the empty word
		LR0Set epsilon = new LR0Set();
		assert(grammar.getRules(NonTerminal.start).size() == 1);
		Rule startRule = grammar.getRules(NonTerminal.start).get(0);
		epsilon.add(LR0Item.freshItem(startRule));
		epsilon.addAll(epsilonClosure(epsilon));
		epsilon.setName("epsilon"); //epsilon.setName("");
		states.add(epsilon);
		initialState = epsilon;

		// while new sets were added continue building
		Queue<LR0Set> queue = new LinkedList<LR0Set>();
		queue.add(epsilon);
		while(!queue.isEmpty()){
			LR0Set set = queue.poll();
			Set<Alphabet> symbols = set.getShiftableSymbols();
			for(Alphabet symbol : symbols){
				LR0Set newSet = new LR0Set();
				newSet.setName(set.getName() + symbol);//newSet.setName(set.getName() + " " + symbol);
				newSet.addAll(set.getShiftedItemsFor(symbol));
				newSet.addAll(epsilonClosure(newSet));
				if(!states.contains(newSet)){
					queue.add(newSet);
					states.add(newSet);
				}
				else {
					for(LR0Set state : states){
						if (state.equals(newSet)){
							newSet = state;
							break;
						}
					}
				}
				if(!transitions.contains(set, symbol)) {
					assert(!transitions.contains(set, symbol));
					transitions.put(set, symbol, newSet);
				}
			}
		}
	}
	
	private LR0Set epsilonClosure(LR0Set set){
		LR0Set result = new LR0Set();
		List<NonTerminal> nonTerminals = new ArrayList<NonTerminal>();
		
		// for every item of the form A -> alpha * B gamma
		// collect the nonterminal to the right of *
		for(LR0Item item : set){
			NonTerminal n = item.getEpsilonStep();
			if(null != n)
				nonTerminals.add(n);
		}
		
		for(NonTerminal nonTerminal : nonTerminals){
			for(LR0Item item : freshItems(nonTerminal)){
				// here we prevent the addition of items that were there already
				// without this we might run into infinite recursion!
				if(!set.contains(item))
					result.add(item);
			}
		}
		
		// recursively continue the closure since the above procedure might have
		// introduced an item like [S->.Aa] etc...
		// the recursion terminates when no *new* items of the above form can be
		// found
		if(result.size() > 0){
			result.addAll(set);
			result.addAll(epsilonClosure(result));
			return result;
		}
		else {
			return set;
		}
	}
	
	/**
	 * From a set of rules of the form
	 * A -> ab | aC
	 * create the "fresh" items
	 * A -> * ab, A -> * aC
	 * 
	 * @param lhs
	 * @return A set of items with nothing left of the dot
	 */
	private LR0Set freshItems(NonTerminal lhs){
		LR0Set result = new LR0Set();
		List<Rule> rules = grammar.getRules(lhs);
		for(Rule rule : rules){
			result.add(LR0Item.freshItem(rule));
		}
		return result;
	}
	
	public int countConflicts(){
		int counter = 0;
		for(LR0Set lr0set : states){
			if(lr0set.hasConflicts())
				counter++;
		}
		return counter;
	}

	public void printLR0Sets(){
		for(LR0Set set : states){
			System.out.println(set.getName() + " " + set);
		}
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("digraph{");
		for(LR0Set state : states){
			builder.append(state.getName());
			builder.append(" [label=\"");
			builder.append(state.toString());
			builder.append("\"];\n");
		}
		for(Table.Cell<LR0Set, Alphabet, LR0Set> transition: transitions.cellSet()){
			builder.append(transition.getRowKey().getName());
			builder.append(" -> ");
			builder.append(transition.getValue().getName());
			builder.append(" [label=\"");
			builder.append(transition.getColumnKey());
			builder.append("\"];\n");
		}
		builder.append("}");
		return builder.toString();
	}
}
