package lexer;

import lexer.LexerGenerator.Token;

import com.google.common.collect.HashBasedTable;

public class NumberDFA extends AbstractDFA{
	
	/**
	 * Construct a new DFA that recognises integers 
	 */
	public NumberDFA(){
		token = Token.NUMBER;
		// there are only 3 states, state 0 is the initial state
		finalState = 1;
		sinkState = 2;
		
		transitions = HashBasedTable.create(3,10);
		for(int i = 0; i < LexerGenerator.numbers.length; i++){
			transitions.put(initialState, LexerGenerator.numbers[i], finalState);
			transitions.put(finalState, LexerGenerator.numbers[i], finalState);
		}
	}	
}
