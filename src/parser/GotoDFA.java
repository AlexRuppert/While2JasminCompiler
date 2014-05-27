package parser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import parser.Grammar.NonTerminal;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class GotoDFA {
	
	Grammar grammar;
	public Set<LR0Set> states = new HashSet<LR0Set>();
	LR0Set initialState;
	Table<LR0Set, Alphabet, LR0Set> transitions = HashBasedTable.create();
	
	public GotoDFA(Grammar grammar) {
		this.grammar = grammar;
		generateLR0StateSpace();
	}
	
	public void addState(LR0Set state){
		assert(!states.contains(state));
		states.add(state);
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
	
	/**
	 * This method computes all LR0 sets and transitions between them.
	 */
	public void generateLR0StateSpace(){
		assert(grammar != null);
		
		List<Rule> startRules = grammar.getRules(NonTerminal.start);
		
		if (startRules == null || startRules.size() == 0) {
			return;
		}
		
		// we assume that there's just one start rule
		assert(startRules.size() == 1);
		
		Rule startRule = startRules.get(0);
		LR0Set startSet = freshItems(startRule.getLhs());
		
		initialState = epsilonClosure(startSet);
		
		states.add(initialState);
		
		// TODO check if this is correct
		boolean changed;
		LR0Set newToSet;
		LR0Item newItem;
		do {
			changed = false;
			newToSet = null;
			newItem = null;
					
			for (LR0Set state: states) {
				for (LR0Item item : state) {
					// first rule
					if (item.canShift()) {
						Alphabet symbol = item.getShiftableSymbolName();
						newItem = item.getShiftedItem();
						
						newToSet = getSuccessor(state, symbol);
						if (newToSet == null) {
							String name = state.getName() + symbol; // TODO check name (insert space?)
							
							newToSet = new LR0Set();
							newToSet.setName(name);
							transitions.put(state, symbol, newToSet);
							changed = true;
						}
						if (!newToSet.contains(newItem)) {
							changed = true;
						}
					}
					if (changed) {
						break;
					}
					// second rule
					// TODO fix, doesn't terminate
					if (item.canShift()) {
						NonTerminal symbol = item.getEpsilonStep();
						if (symbol != null) {
							List<Rule> rules = grammar.getRules(symbol);
							if (rules != null) {
								for (Rule rule: rules) {
									newItem = LR0Item.freshItem(rule);
									if (!state.contains(newItem)) {
										System.out.println("adding item " + newItem + " to " + state); // TODO remove
										newToSet = state;
										changed = true;
										break;
									}
								}
							}
						}
					}
					if (changed) {
						break;
					}
				}
				if (changed) {
					break;
				}
			}
			if (changed) {
				if (newToSet != null) {
					if (newItem != null) {
						newToSet.add(newItem);
					}
					states.add(newToSet);
				}
			}
		} while (changed); 
	}
	
	private LR0Set epsilonClosure(LR0Set set){
		LR0Set ret = new LR0Set();
		ret.setName("epsilon");

		for (LR0Item item : set) {
			ret.add(item);
		}
		
		LR0Item newItem;
		do {
			newItem = null;
			
			for (LR0Item item : ret) {
				NonTerminal nonTerminal = item.getEpsilonStep();
				if (nonTerminal != null) {
					List<Rule> rules = grammar.getRules(nonTerminal);
					if (rules != null) {
						for (Rule rule: rules) {
							newItem = LR0Item.freshItem(rule);
							if (!ret.contains(newItem)) {
								System.out.println("adding item " + newItem); // TODO remove
								break;
							} else {
								newItem = null;
							}
						}
						if (newItem != null) {
							break;
						}
					}
				}
			}
			
			// add outside of loop to avoid ConcurrentModificationException
			if (newItem != null) {
				ret.add(newItem);
			}			
		} while (newItem != null);
		
		return ret;
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
