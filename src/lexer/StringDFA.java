package lexer;

import lexer.LexerGenerator.Token;
import com.google.common.collect.HashBasedTable;


public class StringDFA extends AbstractDFA{
	
	private final int readLetters = 1;

	/**
	 * Construct a new DFA that recognises string constants
	 */
	public StringDFA(){
		token = Token.STRING;
		// there are only 4 states, state 0 is the initial state
		finalState = 2;
		sinkState = 3;

		transitions = HashBasedTable.create();
		transitions.put(initialState, '"', readLetters);
		transitions.put(readLetters, '"', finalState);
	}	
	
	/**
	 * Performs one step of the DFA for a given letter. This method works
	 * differently than in the superclass AbstractDFA
	 * @param letter
	 */
	public void doStep(char letter){
		Integer nextState = transitions.get(currentState, letter);
		if(null == nextState){
			if(currentState == readLetters && letter != '"'){} //stay there
			else currentState = sinkState;
		}
		else
			currentState = nextState;
	}

}
