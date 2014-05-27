package parser;

import java.util.Arrays;

import lexer.LexerGenerator.Token;

import parser.Grammar.NonTerminal;

public class LR0Item extends Rule{

	// represents an LR(0) item as 
	// lhs -> first MARKER last
	
	final int marker; // marker points to index i which is the first
	                  // symbol after the marker
	                  // ie marker = 0 => everything comes after the dot
	                  // marker = rhs.length => everything comes before the dot
	                  // marker = i, 0,..,i-1 before the dot and i,..,rhs.length-1 after the dot
	
	
	public LR0Item(NonTerminal lhs, Alphabet [] rhs, int marker){
		super(lhs,rhs);
		this.marker = marker;
	}
	
	/**
	 * @return true iff the dot is followed by a terminal symbol 
	 */
	public boolean canShiftOverTerminal(){
		return marker < rhs.length && rhs[marker] instanceof Token;
	}
	
	/**
	 * @return true iff there comes nothing after the dot
	 */
	public boolean canReduce(){
		return marker == rhs.length;
	}
	
	/**
	 * @return the non-terminal after the dot if any otherwise null
	 */
	public NonTerminal getEpsilonStep(){
		if(marker < rhs.length && rhs[marker] instanceof NonTerminal)
			return (NonTerminal)rhs[marker];
		else
			return null;
	}
	
	/**
	 * Check if the marker can be shifted further.
	 * Equivalently, if it cannot this item is complete and marker = rhs.length.
	 * @return
	 */
	public boolean canShift(){
		return marker < rhs.length;
	}
	
	public boolean isFinal(){
		if(!canShift() && NonTerminal.start == lhs){
			return true;
		}
		return false;
	}
	
	public int hashCode(){
		//FIXME: genius! (but works of course)
		return 0;
	}
	
	public boolean equals(Object a){
		if(a instanceof LR0Item){
			LR0Item other = (LR0Item)a;
			return other.lhs.equals(lhs) && other.marker == marker && Arrays.equals(other.rhs, rhs);
		}
		return false;
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder();
		result.append("[ ");
		result.append(lhs);
		result.append(" -> ");
		for(int i = 0; i < marker; i++){
			result.append(rhs[i]);
			result.append(" ");
		}
		result.append("* ");
		for(int i = marker; i < rhs.length; i++){
			result.append(rhs[i]);
			result.append(" ");
		}
		result.append("]");
		return result.toString();
	}
	
	public LR0Item getShiftedItem(){
		assert(canShift());
		return new LR0Item(lhs, rhs, marker+1);
	}
	
	/**
	 * Given a grammar production rule like S -> alpha
	 * return an item [S -> . alpha]
	 * @param rule
	 * @return
	 */
	public static LR0Item freshItem(Rule rule){
		return new LR0Item(rule.getLhs(), rule.getRhs(), 0);
	}

	/**
	 * @return the symbol after the dot if there is one else null
	 */
	public Alphabet getShiftableSymbolName() {
		if(0 <= marker && marker < rhs.length)
			return rhs[marker];
		else //error
			return null;
	}
}
