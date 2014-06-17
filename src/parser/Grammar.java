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

	public static enum NonTerminal implements Alphabet {
		// TODO: lower case letter are discouraged for enum types
		// to meet the convention one would implement a corresponding toString
		// method!
		start, program, statement, declaration, assignment, out, branch, loop, expr, subexpr, guard, subguard, relation, S1, S, A, B, E, T, F
	}

	protected Map<NonTerminal, List<List<Alphabet>>> rules;
	public Map<NonTerminal, Set<Alphabet>> first;
	public Map<NonTerminal, Set<Alphabet>> follow;

	/**
	 * Given (implicitly as the class' attribute) the rules of the grammar,
	 * compute the first set for each non-terminal.
	 */
	void computeFirst() {
		for (NonTerminal nonTerminal : NonTerminal.values()) {
			first.put(nonTerminal, new HashSet<Alphabet>());
		}

		boolean changed;
		do {
			changed = false;
			for (NonTerminal nonTerminal : NonTerminal.values()) {
				List<Rule> rules = getRules(nonTerminal);
				for (Rule rule : rules) {
					if (rule.getRhs() != null && rule.getRhs().length > 0) {
						Alphabet a = rule.getRhs()[0];
						if (first.get(nonTerminal).addAll(getFirst(a))) {
							changed = true;
						}
					}
				}
			}
		} while (changed);
	}

	/**
	 * Given (implicitly as the class' attribute) the rules of the grammar,
	 * compute the follow set for each non-terminal.
	 */
	void computeFollow() {
		for (NonTerminal nonTerminal : NonTerminal.values()) {
			follow.put(nonTerminal, new HashSet<Alphabet>());
		}

		List<Rule> rules = getRules();
		for (Rule rule : rules) {
			Alphabet[] rhs = rule.getRhs();
			if (rhs != null) {
				for (int i = 0; i < rhs.length - 1; i++) {
					Alphabet a = rhs[i];
					if (a instanceof NonTerminal) {
						Alphabet b = rhs[i + 1];
						follow.get(a).addAll(getFirst(b));
					}
				}
			}
		}
	}

	Set<Alphabet> getFirst(Alphabet symbol) {
		if (symbol instanceof Token) {
			HashSet<Alphabet> result = new HashSet<Alphabet>();
			result.add(symbol);
			return result;
		} else if (symbol instanceof NonTerminal) {
			return first.get(symbol);
		} else {
			return null;
		}
	}

	public List<Rule> getRules(NonTerminal name) {
		List<Rule> result = new ArrayList<Rule>();
		if (rules.containsKey(name)) {
			for (List<Alphabet> rhs : rules.get(name)) {
				result.add(new Rule(name, rhs));
			}
		} else {
			System.out.println("No rules for " + name);
		}
		return result;
	}

	public List<Rule> getRules() {
		List<Rule> result = new ArrayList<Rule>();
		for (NonTerminal nonTerminal : rules.keySet()) {
			for (List<Alphabet> rhs : rules.get(nonTerminal)) {
				result.add(new Rule(nonTerminal, rhs));
			}
		}
		return result;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		Iterator<NonTerminal> lhs = rules.keySet().iterator();
		while (lhs.hasNext()) {
			NonTerminal rule = lhs.next();
			result.append(rule);
			result.append(" -> ");

			Iterator<List<Alphabet>> alternatives = rules.get(rule).iterator();
			while (alternatives.hasNext()) {
				List<Alphabet> alternative = alternatives.next();

				Iterator<Alphabet> character = alternative.iterator();
				while (character.hasNext()) {
					result.append(character.next());
					if (character.hasNext())
						result.append(" ");
				}
				if (alternatives.hasNext())
					result.append(" | ");
			}
			result.append("\n");
		}

		return result.toString();
	}
}
