package lexer;

import lexer.LexerGenerator.Token;

import com.google.common.collect.HashBasedTable;

public class IDDFA extends AbstractDFA{
	
	/**
	 * Construct a new DFA that recognises every alphanumerical identifier that
	 * starts with a letter and continues with letter, numbers or underscores. 
	 */
	public IDDFA(){
		token = Token.ID;
		// there are only 3 states, state 0 is the initial state
		finalState = 1;
		sinkState = 2;
		
		transitions = HashBasedTable.create(3,63);
		for(int i = 0; i < LexerGenerator.alpha.length; i++){
			transitions.put(initialState, LexerGenerator.alpha[i], finalState);
			transitions.put(finalState, LexerGenerator.alpha[i], finalState);
		}
		for(int i = 0; i < LexerGenerator.underScoreNumerical.length; i++){
			transitions.put(finalState, LexerGenerator.underScoreNumerical[i], finalState);
		}
	}	
}
