package parser;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LR0Set extends HashSet<LR0Item>{
	
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString () {
		StringBuilder result = new StringBuilder();
		Iterator<LR0Item> it = this.iterator();
		while(it.hasNext()){
			result.append(it.next());
			result.append("\\n");
		}
		return result.toString();
	}

	public boolean hasConflicts() {
		boolean ret = false;
		
		// shift/reduce conflicts
		boolean shiftRecuceConflictCond1 = false;
		boolean shiftRecuceConflictCond2 = false;
		for (LR0Item item: this) {
			if (item.canShiftOverTerminal()) {
				shiftRecuceConflictCond1 = true;
			}
			if (item.canReduce()) {
				shiftRecuceConflictCond2 = true;
			}
			if (shiftRecuceConflictCond1 && shiftRecuceConflictCond2) {
				System.out.println("shift/reduce conflict in " + this);
				ret = true;
				break;
			}
		}
		
		// reduce/reduce conflict
		boolean canReduce = false;
		// note: there exist no equal items -> sufficient to find two which can be reduced
		for (LR0Item item: this) {
			if (item.canReduce()) {
				if (canReduce) {
					System.out.println("reduce/reduce conflict in " + this);
					ret = true;		
					break;
				}
				canReduce = true;
			}
		}
		
		return ret;
	}

	public Set<Alphabet> getShiftableSymbols() {
		Set<Alphabet> symbols = new HashSet<Alphabet>();
		Iterator<LR0Item> it = iterator();
		while(it.hasNext()){
			LR0Item curItem = it.next();
			if(curItem.canShift()){
				symbols.add(curItem.getShiftableSymbolName());
			}
		}
		return symbols;
	}

	/**
	 * 
	 * @param symbol
	 * @return A subset (of the current LR0Set) which contains those items that allow for a shift with the given symbol
	 */
	public LR0Set getShiftedItemsFor(Alphabet symbol) {
		LR0Set result = new LR0Set();
		Iterator<LR0Item> it = iterator();
		while(it.hasNext()){
			LR0Item curItem = it.next();
			if(curItem.canShift() && curItem.getShiftableSymbolName().equals(symbol)){
				result.add(curItem.getShiftedItem());
			}
		}
		return result;
	}

	/**
	 * @return true iff this set contains a complete item of the form [A -> alpha *]
	 */
	public boolean containsCompleteItem() {
		Iterator<LR0Item> it = iterator();
		while(it.hasNext()){
			LR0Item curItem = it.next();
			if(!curItem.canShift()){
				return true;
			}
		}
		return false;
	}
	
	public boolean containsFinalItem() {
		LR0Item item = getCompleteItem();
		if(null != item){
			if(item.isFinal()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Assuming there is one complete item in this set, return it.
	 * Returns null if there is no complete item.
	 * @return
	 */
	public LR0Item getCompleteItem() {
		Iterator<LR0Item> it = iterator();
		while(it.hasNext()){
			LR0Item curItem = it.next();
			if(!curItem.canShift()){
				return curItem;
			}
		}
		return null;
	}
}
