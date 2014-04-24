package lexer;

import lexer.LexerGenerator.Token;

//import com.google.common.collect.HashBasedTable;

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

        int states=INITIAL_STATE;
        word=prepareString(word);
        char[] inputCharWord = word.toCharArray();
        for(int i =0; i<inputCharWord.length-1;i++)
        {
            addTransition(states,inputCharWord[i],++states);
        }
        addTransition(states,inputCharWord[inputCharWord.length-1],FINAL_STATE);


	}
}
