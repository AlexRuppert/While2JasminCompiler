package lexer;

import lexer.LexerGenerator.Token;

import com.google.common.collect.HashBasedTable;

public class DFA extends AbstractDFA{
	
	/**
	 * Construct a new DFA that recognises exactly the given word.
	 * Given a word "foo" the constructed automaton looks like:
	 * -> () -f-> () -o-> () -o-> []
	 * from every state (including the final one) every other input letter leads
	 * to a distinguished sink state in which the automaton then remains
	 * 
	 * @param word A String that the automaton should recognise
	 */
	public DFA(String word, Token token){
		assert(word.length() > 0);
		
		this.token = token;
		
		finalState = word.length();
		sinkState = word.length() + 1;
		
		transitions = HashBasedTable.create(word.length() + 2,1);
		for(int i = 0; i < word.length(); i++){
			transitions.put(i, word.charAt(i), i+1);
		}
	}
}
