package lexer;

import lexer.LexerGenerator.Token;

//import com.google.common.collect.HashBasedTable;

public class CommentDFA extends AbstractDFA{
	
	/**
	 * Construct a new DFA that recognises comments within source code
	 * There are two kinds of comments:
	 * A single line comment: starts with // and ends with a newline
	 * And a multiline comment that starts with /* and ends with * / 
	 * (without the space)
	 */
	public CommentDFA(){
		this.token = Token.COMMENT;

        addTransition(INITIAL_STATE,'/',INITIAL_STATE+1);//must start with /
            addTransition(INITIAL_STATE+1,'/',INITIAL_STATE+2);//either * or /
                addTransition(INITIAL_STATE+2,ELSE,INITIAL_STATE+2);//something other than \n, irrelevant
                addTransition(INITIAL_STATE+2,'\n',FINAL_STATE);


            addTransition(INITIAL_STATE+1,'*',INITIAL_STATE+3);
                addTransition(INITIAL_STATE+3,'*',INITIAL_STATE+4);//if another * occurs
                addTransition(INITIAL_STATE+3,ELSE,INITIAL_STATE+3);// ignore everything else
                addTransition(INITIAL_STATE+4,'/',FINAL_STATE); //after another * comes/
                addTransition(INITIAL_STATE+4,ELSE,INITIAL_STATE+3);//if after the other * comes no /, loop back
                addTransition(INITIAL_STATE+4,'*',INITIAL_STATE+4);//if additional *, stay in state

	}
	
	/**
	 * Performs one step of the DFA for a given letter. This method works
	 * differently than in the superclass AbstractDFA
	 * @param letter
	 */
    /* no it doesn't..

    public void doStep(char letter){
		//TODO
	}

	@Override
	public boolean isAccepting() {
		//TODO
		return false;
	}*/
}
