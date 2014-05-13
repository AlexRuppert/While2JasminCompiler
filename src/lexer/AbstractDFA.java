package lexer;

import lexer.LexerGenerator.Token;

import com.google.common.collect.Table;

public abstract class AbstractDFA {
	
	protected final int initialState = 0;
	protected int finalState;
	protected int sinkState;
	protected Token token; // Token that is recognised by this automaton

	// set of states in encoded in transitions, i.e. number of rows
	protected Table<Integer, Character, Integer> transitions;

	// remember the autmaton's current state
	protected int currentState = 0;

	/**
	 * Reset the automaton to the initial state.
	 */
	public void reset(){
		currentState = initialState;
	}
	
	/**
	 * This is useful for resuming parsing at a certain position
	 * @param state ID of state that the automaton should start in
	 */
	public void resetToState(int state){
		assert(0 <= state && state <= sinkState);
		currentState = state;
	}

	/**
	 * Performs one step of the DFA for a given letter. If there is a transition
	 * for the given letter, then the automaton proceeds to the successor state.
	 * Otherwise it goes to the sink state. By construction it will stay in the
	 * sink for every input letter.
	 * @param letter
	 */
	public void doStep(char letter){
		Integer nextState = transitions.get(currentState, letter);
		if(null == nextState)
			currentState = sinkState;
		else
			currentState = nextState;
	}

	/**
	 * @return a boolean that indicates if the automaton is currently in its 
	 *         accepting state or not
	 */
	public boolean isAccepting(){
		return currentState == finalState;
	}

	/**
	 * 
	 * @param inputWord String that contains the input word
	 * @return a boolean that indicates if the word is accepted by this 
	 *         automaton or not
	 */
	public boolean run(String inputWord){
		this.reset();
		char[] inputCharWord = inputWord.toCharArray();
		for(char letter:inputCharWord){
			doStep(letter);
		}
		return isAccepting();
	}

	/**
	 * @return The ID of the state that the automaton is currently in
	 */
	public int getCurrentState(){
		return currentState;
	}
	
	public boolean isProductive(){
		return currentState != sinkState;
	}
	
	/**
	 * @return The Token that this automaton recognises
	 */
	public Token getToken(){
		return token;
	}
}
