package lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import lexer.LexerGenerator.Token;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class BacktrackingDFA {
	
	private List<AbstractDFA> automata;
	private Table<int[], Character, int[]> transitions;
	private Map<String, Token> recognisedToken;
    private Map<Token,String> tokenMapping;
	private final int [] initialState = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //34 * '0';
	private int [] backtrackState = new int[initialState.length];
	private int [] currentState = new int[initialState.length];

    public void generateAttributesFromTokens()
    {
        tokenMapping= new HashMap<Token, String>();
       /* tokenMapping.put(Token.WHILE,"while");
        tokenMapping.put(Token.WRITE,"write");
        tokenMapping.put(Token.READ,"read");
        tokenMapping.put(Token.INT,"int");
        tokenMapping.put(Token.IF,"if");
        tokenMapping.put(Token.ELSE,"else");
        tokenMapping.put(Token.TRUE,"true");
        tokenMapping.put(Token.FALSE,"false");
        tokenMapping.put(Token.LPAR,"(");
        tokenMapping.put(Token.RPAR,")");
        tokenMapping.put(Token.LBRACE,"{");
        tokenMapping.put(Token.RBRACE,"}");
        tokenMapping.put(Token.PLUS,"+");
        tokenMapping.put(Token.MINUS,"-");
        tokenMapping.put(Token.TIMES,"*");
        tokenMapping.put(Token.DIV,"/");
        tokenMapping.put(Token.LEQ,"<=");
        tokenMapping.put(Token.LT,"<");
        tokenMapping.put(Token.GEQ,">=");
        tokenMapping.put(Token.GT,">");
        tokenMapping.put(Token.EQ,"==");
        tokenMapping.put(Token.ASSIGN,"=");
        tokenMapping.put(Token.NEQ,"!=");
        tokenMapping.put(Token.AND,"&&");
        tokenMapping.put(Token.OR,"||");
        tokenMapping.put(Token.NOT,"!");
        tokenMapping.put(Token.SEMICOLON,";");*/
        tokenMapping.put(Token.EOF,"$");
        tokenMapping.put(Token.BLANK," ");


    }
	/*
	 * This method creates an array of DFAs, one for every token (and symbol).
	 * Those automata will run in parallel and are controlled by the
	 * doStep()
	 * isProductive()
	 * resetToState()
	 * methods.
	 */
	public void generateDFAforTokens(){
		automata = new ArrayList<AbstractDFA>(initialState.length);
		// generate all automata
		automata.add(new DFA("while", Token.WHILE));
		automata.add(new DFA("write", Token.WRITE));
		automata.add(new DFA("read", Token.READ));
		automata.add(new DFA("int", Token.INT));
		automata.add(new DFA("if", Token.IF));
		automata.add(new DFA("else", Token.ELSE));
		automata.add(new DFA("true", Token.TRUE));
		automata.add(new DFA("false", Token.FALSE));
		automata.add(new DFA("(", Token.LPAR));
		automata.add(new DFA(")", Token.RPAR));
		automata.add(new DFA("{", Token.LBRACE));
		automata.add(new DFA("}", Token.RBRACE));
		automata.add(new DFA("+", Token.PLUS));
		automata.add(new DFA("-", Token.MINUS));
		automata.add(new DFA("*", Token.TIMES));
		automata.add(new DFA("/", Token.DIV));
		automata.add(new DFA("<=", Token.LEQ));
		automata.add(new DFA("<", Token.LT));
		automata.add(new DFA(">=", Token.GEQ));
		automata.add(new DFA(">", Token.GT));
		automata.add(new DFA("==", Token.EQ));
		automata.add(new DFA("=", Token.ASSIGN));
		automata.add(new DFA("!=", Token.NEQ));
		automata.add(new DFA("&&", Token.AND));
		automata.add(new DFA("||", Token.OR));
		automata.add(new DFA("!", Token.NOT));
		automata.add(new DFA(";", Token.SEMICOLON));
		automata.add(new DFA("$", Token.EOF));
		automata.add(new IDDFA());
		automata.add(new NumberDFA());
		automata.add(new CommentDFA());
		automata.add(new StringDFA());
		automata.add(new DFA(" ", Token.BLANK));
		automata.add(new DFA("\t", Token.BLANK));
		automata.add(new DFA("\r", Token.BLANK));
		automata.add(new DFA("\n", Token.BLANK));
		
		assert(automata.size() == initialState.length);
	}
	
	//All possible state-combinations for all automata?
	private void generateTransitions(){
		transitions = HashBasedTable.create();
		char [] relevantAlphabet =
				new char[LexerGenerator.alpha.length+LexerGenerator.underScoreNumerical.length+LexerGenerator.special.length];
		System.arraycopy(LexerGenerator.alpha, 0, relevantAlphabet, 0, LexerGenerator.alpha.length);
		System.arraycopy(LexerGenerator.underScoreNumerical, 0, relevantAlphabet, LexerGenerator.alpha.length, LexerGenerator.underScoreNumerical.length);
		System.arraycopy(LexerGenerator.special, 0, relevantAlphabet, LexerGenerator.alpha.length+LexerGenerator.underScoreNumerical.length, LexerGenerator.special.length);
		
		Queue<int[]> statesToExpand = new LinkedList<int[]>();
		
		Set<String> visitedStates = new HashSet<String>();
		
		int [] state = new int[initialState.length];
		System.arraycopy(initialState, 0, state, 0, initialState.length);
		statesToExpand.add(state);
		visitedStates.add(hashState(state));
		
		while(!statesToExpand.isEmpty()){
			state = statesToExpand.remove();
			for(char letter:relevantAlphabet){
				
				int [] tempState = new int[initialState.length];
				for(int i = 0; i < automata.size(); i++){
					AbstractDFA automaton = automata.get(i);
					automaton.resetToState(state[i]);
					automaton.doStep(letter);
					tempState[i] = automaton.getCurrentState();
				}
				if(!visitedStates.contains(hashState(tempState))){
					statesToExpand.add(tempState);
					visitedStates.add(hashState(state));
				}
				transitions.put(state, letter, tempState);
			}
		}
		
	}
	
	private String hashState(int [] state){
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < state.length; i++){
			builder.append(state[i]).append(",");//just appending the numbers = bad
		}
		return builder.toString();
	}


	private void mapFinalStatesToTokens(){
		recognisedToken = new HashMap<String, Token>();
		for(int [] state : transitions.rowKeySet()){
//			assert(state.length == initialState.length);
			for(int i=0; i < state.length; i++){
				automata.get(i).resetToState(state[i]);
				if(automata.get(i).isAccepting()){
					recognisedToken.put(hashState(state), automata.get(i).getToken());
					break; // proceed to next state in transitions
				}
				automata.get(i).reset();
			}
		}
	}
	
	public BacktrackingDFA(){
        generateAttributesFromTokens();
		generateDFAforTokens();
		generateTransitions();
		mapFinalStatesToTokens();
	}
	
	public Token doStep(char letter){
		for(int i = 0; i < automata.size(); i++){
			automata.get(i).doStep(letter);
			currentState[i] = automata.get(i).getCurrentState();
		}
		return recognisedToken.get(hashState(currentState));
	}
	
	/**
	 * Given a string of lexemes, chop them up to the corresponding symbols, i.e. a list of token, attribute pairs.
	 * Note that since all keywords and symbols are represented by their own token, the attribute only really matters for 
	 * identifiers and numbers.
	 * @param word
	 * @return
	 * @throws LexerException
	 */
	public List<Symbol> run(String word) throws LexerException{
		List<Symbol> result = new ArrayList<Symbol>();

        String currentAttribute="";
		Token current=null;
        Token previous=current;
        int lastCorrectIndex=0;

        for(int i = 0; i < word.length(); i++){
            char letter=word.charAt(i);

            if(current==null)// normal mode
            {
                current=doStep(letter);
                currentAttribute=""+letter;
                if(!isProductive())
                {
                    throw new LexerException("not productive state",result);
                }
            }
            else //backtrack mode
            {

                previous=current;
                current=doStep(letter);
                if(!isProductive())
                {
                    if(tokenMapping.get(previous)!=null)
                        result.add(new Symbol(previous,tokenMapping.get(previous)));
                    else
                    {
                        result.add(new Symbol(previous, currentAttribute));
                    }
                    current=null;
                    currentAttribute="";
                    resetToState(initialState);
                    i=lastCorrectIndex;
                    System.out.println(result);
                }
                else if(current!=null)//final
                {
                    currentAttribute+=letter;
                    lastCorrectIndex=i;
                }
            }

          /*  if(i==word.length()-1&&current!=null)
            {
                if(tokenMapping.get(current)!=null)
                    result.add(new Symbol(current,tokenMapping.get(current)));
                else
                {
                    result.add(new Symbol(current, currentAttribute));
                }
                current=null;
                currentAttribute="";
                resetToState(initialState);
                i=lastCorrectIndex;
            }*/
        }

        if(current!=null)
        {
            if(tokenMapping.get(current)!=null)
                result.add(new Symbol(current,tokenMapping.get(current)));
            else
            {
                result.add(new Symbol(current, currentAttribute));
            }

        }
        else
        {
            throw new LexerException("no final state",result);
        }

		
		return result;
	}
	
	/**
	 * @return true iff the currentState is not the sinkState of every component
	 */
	private boolean isProductive(){
		for(AbstractDFA automaton:automata){
			if(automaton.isProductive())
				return true;
		}
		return false;
	}
	
	public void resetToState(int[] state){
		for(int i = 0; i < automata.size(); i++){
			currentState[i] = state[i];
			automata.get(i).resetToState(currentState[i]);
		}
	}
	
}
