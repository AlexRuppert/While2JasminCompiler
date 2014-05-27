package lexer;

import java.util.ArrayList;
import java.util.Arrays;
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
	private final int [] initialState = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //34 * '0';
	private int [] currentState = new int[initialState.length];
		
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
			builder.append(state[i]);
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
		generateDFAforTokens();
		generateTransitions();
		mapFinalStatesToTokens();
//		System.out.println("Number of transitions of Backtracking DFA: "+transitions.size());
	}
	
	public Token doStep(char letter){
		for(int i = 0; i < automata.size(); i++){
			automata.get(i).doStep(letter);
			currentState[i] = automata.get(i).getCurrentState();
		}
		return recognisedToken.get(hashState(currentState));
	}
	
	public List<Symbol> run(String word) throws LexerException{
		List<Symbol> result = new ArrayList<Symbol>();
		char [] wordAsChar = word.toCharArray();
		Token backtrackToken = null;
		Token currentToken = null;
		int backtrackPointer = 0;
		int currentPointer = 0;
		System.arraycopy(initialState, 0, currentState, 0, initialState.length);
		
		while(backtrackPointer < wordAsChar.length){
			String value = Character.toString(wordAsChar[currentPointer]);
			while(currentPointer < wordAsChar.length && isProductive()){
				currentToken = doStep(wordAsChar[currentPointer]);
				if(null != currentToken){
					value += new String(Arrays.copyOfRange(wordAsChar, backtrackPointer+1, currentPointer+1));
					backtrackToken = currentToken;
					backtrackPointer = currentPointer;
				}
				currentPointer++;
			}
			if(null != backtrackToken){
				result.add(new Symbol(backtrackToken, value));
				currentPointer = backtrackPointer+1;
				resetToState(initialState);
				backtrackToken = null;
				backtrackPointer++;
			}
			else{
				throw new LexerException("Last backtrack position is: "+
				        backtrackPointer+"\nScanned before failure: "+
						word.substring(0, backtrackPointer+1), result);
			}
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
