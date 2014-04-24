package lexer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.TreeBasedTable;
import lexer.LexerGenerator.Token;

import com.google.common.collect.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class AbstractDFA {

    public static final int FINAL_STATE= Integer.MAX_VALUE-1; //set some reasonable values as constants
    public static final int SINK_STATE= Integer.MAX_VALUE;
    public static final int INITIAL_STATE=0;
    public static final char ELSE=0;//should not be a printable thus never occur in a string
    protected final int initialState = INITIAL_STATE;
	protected int finalState=FINAL_STATE;
	protected int sinkState=SINK_STATE;
	protected Token token; // Token that is recognised by this automaton

	// set of states in encoded in transitions, i.e. number of rows
	protected Table<Integer, Character, Integer> transitions=HashBasedTable.create(); //init here HashBased seems nice for fast look-ups


	// remember the autmaton's current state
	protected int currentState = initialState;

	/**
	 * Reset the automaton to the initial state.
	 */
	public void reset(){
		currentState = initialState;
	}

    /**
     * adds a new transition
     * @param from
     * @param reading
     * @param to
     */
    protected void addTransition(Integer from,Character reading,Integer to)
    {
        transitions.put(from,reading,to);
    }
	/**
	 * This is useful for resuming scanning at a certain position
	 * @param state ID of state that the automaton should start in
	 */
	public void resetToState(int state){
		assert(0 <= state && state <= sinkState);
		currentState = state;
	}

	/**
	 * Performs one step of the DFA for a given letter. If there is a transition
	 * for the given letter, then the automaton proceeds to the successor state.
	 * Otherwise it checks for the ELSE character, which should be not printable/occur in strings. If no transition found it
     * goes to the sink state. By construction it will stay in the
	 * sink for every input letter.
	 * @param letter
	 */
	public void doStep(char letter){
		Integer nextState = transitions.get(currentState, letter);
		if(null == nextState)
        {
            nextState = transitions.get(currentState, ELSE);//is there a transition for ELSE?

            if(null==nextState)
			    currentState = sinkState;
            else
                currentState = nextState;
        }
		else
        {
			currentState = nextState;
        }
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
        inputWord=prepareString(inputWord);
		this.reset();
		char[] inputCharWord = inputWord.toCharArray();
		for(char letter:inputCharWord){
			doStep(letter);
		}
		return isAccepting();
	}

    /**
     * formats string, i.e. converts line breaks to \n
     * @param inputWord
     * @return
     */
    protected String prepareString(String inputWord)
    {
        inputWord=inputWord.replaceAll("\r\n","\n");
        inputWord=inputWord.replaceAll("\r","\n");
        return inputWord;
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
