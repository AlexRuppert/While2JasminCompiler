package parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import util.Pair;
import lexer.LexerGenerator.Token;
import lexer.Symbol;

public class Parser {
	
	private GotoDFA gt;
	public GotoDFA getGotoDFA() {
		return gt;
	}
	
	/**
	 * generates the LR(0) sets for a given grammar
	 * @param grammar
	 */
	public Parser(Grammar grammar){
		this.gt = new GotoDFA(grammar);
	}
		
	/**
	 * siehe Algorithmus 8.2.35, Seite 262, "Theoretische Informatik" (Asteroth, Baier)
	 * @param lexOutput
	 * @return
	 * @throws ParserException
	 */
	public List<Rule> parse(List<Symbol> lexOutput) throws ParserException {
		List<Rule> analysis = new LinkedList<Rule>();
		
		Iterator<Symbol> it = lexOutput.iterator();
		Pair<Alphabet, LR0Set> initialKellerInput = new Pair<Alphabet, LR0Set>(null, gt.getInitialState());
		Stack<Pair<Alphabet, LR0Set>> keller = new Stack<Pair<Alphabet, LR0Set>>();
		
		keller.push(initialKellerInput);
		
		while(!keller.isEmpty()){
			Pair<Alphabet, LR0Set> kellerSpitze = keller.peek();
			if(kellerSpitze.getSecond().containsFinalItem()){
				// we do not actually have to empty the stack now
				if(!it.hasNext()){
					//input completely read
					return analysis;
				}
				else {
					//Parser finished but there is more input
					throw new ParserException("Parser finished but there is more unprocessed input!",analysis);
				}
			}
			else{
				if(kellerSpitze.getSecond().containsCompleteItem()){
					// Item of the form [A -> alpha *]
					LR0Item completeItem = kellerSpitze.getSecond().getCompleteItem();
					// remove |alpha| elements from the stack
					for(int i = 0; i < completeItem.getRhs().length; i++) {
						keller.pop();
					}
					Pair<Alphabet, LR0Set> prevKellerSpitze;
					// I' := Top(Keller)
					prevKellerSpitze = keller.peek();
					// J := delta(I', A)
					LR0Set succState = gt.getSuccessor(prevKellerSpitze.getSecond(), completeItem.getLhs());
					if(null == succState){
						throw new ParserException("Tried reducing with rule "+completeItem+" but could not find a successor delta("+prevKellerSpitze.getSecond()+", "+completeItem.getLhs()+")",analysis);
					}
					Pair<Alphabet, LR0Set> succKellerSpitze = new Pair<Alphabet, LR0Set>(completeItem.getLhs(), succState);
					keller.push(succKellerSpitze);
					//prepend the applied rule (ensures correct order)
					analysis.add(0,completeItem);
				}
				else{
					if(!it.hasNext()){
						//nothing more to read, nothing to reduce and no final item
						throw new ParserException("Only shift operation possible but the input terminated",analysis);
					}
					else{
						Token symbol = it.next().getToken();
						LR0Set succState = gt.getSuccessor(kellerSpitze.getSecond(), symbol);
						if(null == succState){
							throw new ParserException("Tried shifting "+symbol+" onto the stack but could not find a successor delta("+kellerSpitze.getSecond()+", "+symbol+")",analysis);
						}
						else {
							Pair<Alphabet, LR0Set> succKellerSpitze = new Pair<Alphabet, LR0Set>(symbol, succState);
							keller.push(succKellerSpitze);
						}
					}
				}
			}
		}
		
		return analysis;
	}
	
	public List<Rule> parseSLR1(List<Symbol> lexOutput) throws ParserException {
		List<Rule> analysis = new LinkedList<Rule>();

		Iterator<Symbol> it = lexOutput.iterator();
		Pair<Alphabet, LR0Set> initialKellerInput = new Pair<Alphabet, LR0Set>(null, gt.getInitialState());
		Stack<Pair<Alphabet, LR0Set>> keller = new Stack<Pair<Alphabet, LR0Set>>();

		keller.push(initialKellerInput);
		Token lookahead = null;
		
		while(!keller.isEmpty()){
			Pair<Alphabet, LR0Set> kellerSpitze = keller.peek();
			//check if lookahead has been consumed by a previous iteration and if so read one more token
			if(null == lookahead){
				//read one more symbol if there are any
				lookahead = it.hasNext()?it.next().getToken():null;
			}
			
			// Finish parsing if the stack top contains [start -> alpha *]
			if(kellerSpitze.getSecond().containsFinalItem()){
				if(null == lookahead){ // ensure that the input completely read
					analysis.add(0,kellerSpitze.getSecond().getCompleteItem()); //there can be only one complete item in the set and we have already checked it is the final item
					return analysis; // we do not actually have to empty the stack now
				}
				else { // parser finished but there is more input
					throw new ParserException("Parser finished but there is more unprocessed input!",analysis);
				}
			}
			// otherwise continue parsing:
			else{
				// if stack top has a compete item and it matches the lookahead, reduce
				if(kellerSpitze.getSecond().containsCompleteItem() && gt.grammar.follow.get(kellerSpitze.getSecond().getCompleteItem().getLhs()).contains(lookahead)){ //god bless lazy evaluation
					// Item of the form [A -> alpha *]
					LR0Item completeItem = kellerSpitze.getSecond().getCompleteItem();
					// remove |alpha| elements from the stack
					for(int i = 0; i < completeItem.getRhs().length; i++) {
						keller.pop();
					}
					Pair<Alphabet, LR0Set> prevKellerSpitze;
					// I' := Top(Keller)
					prevKellerSpitze = keller.peek();
					// J := delta(I', A)
					LR0Set succState = gt.getSuccessor(prevKellerSpitze.getSecond(), completeItem.getLhs());
					if(null == succState){
						throw new ParserException("Tried reducing with rule "+completeItem+" but could not find a successor delta("+prevKellerSpitze.getSecond()+", "+completeItem.getLhs()+")",analysis);
					}
					Pair<Alphabet, LR0Set> succKellerSpitze = new Pair<Alphabet, LR0Set>(completeItem.getLhs(), succState);
					keller.push(succKellerSpitze);
					//prepend the applied rule (ensures correct order)
					analysis.add(0,completeItem);
				}
				// otherwise, shift
				else{
					if(null == lookahead){
						//nothing more to read, nothing to reduce and no final item
						throw new ParserException("Only shift operation possible but the input terminated",analysis);
					}
					else{
						LR0Set succState = gt.getSuccessor(kellerSpitze.getSecond(), lookahead);
						if(null == succState){
							throw new ParserException("Tried shifting "+lookahead+" onto the stack but could not find a successor delta("+kellerSpitze.getSecond()+", "+lookahead+")",analysis);
						}
						else {
							Pair<Alphabet, LR0Set> succKellerSpitze = new Pair<Alphabet, LR0Set>(lookahead, succState);
							keller.push(succKellerSpitze);
							//mark lookahead as consumed
							lookahead = null;
						}
					}
				}
			}
		}
		
		return analysis;
	}
}
