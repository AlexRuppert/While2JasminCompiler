package lexer;

import lexer.LexerGenerator.Token;

import com.google.common.collect.HashBasedTable;

public class CommentDFA extends AbstractDFA{
	
	private final int slashRead = 1;
	private final int singleLine = 2;
	private final int multiLine = 3;
	private final int maybeEndMultiLine = 4;
	private final int macFinalState = 6;
	
	/**
	 * Construct a new DFA that recognises comments within source code
	 * There are two kinds of comments:
	 * A single line comment: starts with // and ends with a newline
	 * And a multiline comment that starts with /* and ends with * / 
	 * (without the space)
	 */
	public CommentDFA(){
		token = Token.COMMENT;
		// state 0 is the initial state
		
		finalState = 5;
		sinkState = 7;
		
		transitions = HashBasedTable.create(7,2);
		transitions.put(initialState, '/', slashRead);
		transitions.put(slashRead, '/', singleLine);
		transitions.put(slashRead, '*', multiLine);
		transitions.put(singleLine, '\n', finalState);
		transitions.put(singleLine, '\r', macFinalState);
		transitions.put(macFinalState, '\n', finalState);
		transitions.put(multiLine, '*', maybeEndMultiLine);
		transitions.put(maybeEndMultiLine, '*', maybeEndMultiLine);
		transitions.put(maybeEndMultiLine, '/', finalState);
	}
	
	/**
	 * Performs one step of the DFA for a given letter. This method works
	 * differently than in the superclass AbstractDFA
	 * @param letter
	 */
	public void doStep(char letter){
		Integer nextState = transitions.get(currentState, letter);
		if(null == nextState){
			if(currentState == singleLine){} //stay there
			else if(currentState == multiLine){} //stay there
			else if(currentState == maybeEndMultiLine){
				currentState = multiLine;
			}
			else{	
				currentState = sinkState;
			}
		}
		else
			currentState = nextState;
	}

	@Override
	public boolean isAccepting() {
		return (currentState == finalState || currentState == macFinalState);
	}
}
