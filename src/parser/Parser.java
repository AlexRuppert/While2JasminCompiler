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

	/**
	 * generates the LR(0) sets for a given grammar
	 * 
	 * @param grammar
	 */
	public Parser(Grammar grammar) {
		this.gt = new GotoDFA(grammar);
	}

	/**
	 * siehe Algorithmus 8.2.35, Seite 262, "Theoretische Informatik" (Asteroth,
	 * Baier)
	 * 
	 * @param lexOutput
	 * @return
	 * @throws ParserException
	 */
	public List<Rule> parse(List<Symbol> lexOutput) throws ParserException {
		List<Rule> analysis = new LinkedList<Rule>();

		Iterator<Symbol> it = lexOutput.iterator();
		Pair<Alphabet, LR0Set> initialKellerInput = new Pair<Alphabet, LR0Set>(
				null, gt.initialState);
		Stack<Pair<Alphabet, LR0Set>> keller = new Stack<Pair<Alphabet, LR0Set>>();

		keller.push(initialKellerInput);

		while (!keller.isEmpty()) {
			Pair<Alphabet, LR0Set> kellerSpitze = keller.peek();
			if (kellerSpitze.getSecond().containsFinalItem()) {
				// we do not actually have to empty the stack now
				if (!it.hasNext()) {
					// input completely read
					return analysis;
				} else {
					// Parser finished but there is more input
					throw new ParserException(
							"Parser finished but there is more unprocessed input!",
							analysis);
				}
			} else {
				if (kellerSpitze.getSecond().containsCompleteItem()) {
					// Item of the form [A -> alpha *]
					LR0Item completeItem = kellerSpitze.getSecond()
							.getCompleteItem();
					// remove |alpha| elements from the stack
					for (int i = 0; i < completeItem.rhs.length; i++) {
						keller.pop();
					}
					Pair<Alphabet, LR0Set> prevKellerSpitze;
					// I' := Top(Keller)
					prevKellerSpitze = keller.peek();
					// J := delta(I', A)
					LR0Set succState = gt.getSuccessor(
							prevKellerSpitze.getSecond(), completeItem.lhs);
					if (null == succState) {
						throw new ParserException("Tried reducing with rule "
								+ completeItem
								+ " but could not find a successor delta("
								+ prevKellerSpitze.getSecond() + ", "
								+ completeItem.lhs + ")", analysis);
					}
					Pair<Alphabet, LR0Set> succKellerSpitze = new Pair<Alphabet, LR0Set>(
							completeItem.lhs, succState);
					keller.push(succKellerSpitze);
					// prepend the applied rule (ensures correct order)
					analysis.add(0, completeItem);
				} else {
					if (!it.hasNext()) {
						// nothing more to read, nothing to reduce and no final
						// item
						throw new ParserException(
								"Only shift operation possible but the input terminated",
								analysis);
					} else {
						Token symbol = it.next().getToken();
						LR0Set succState = gt.getSuccessor(
								kellerSpitze.getSecond(), symbol);
						if (null == succState) {
							throw new ParserException(
									"Tried shifting "
											+ symbol
											+ " onto the stack but could not find a successor delta("
											+ kellerSpitze.getSecond() + ", "
											+ symbol + ")", analysis);
						} else {
							Pair<Alphabet, LR0Set> succKellerSpitze = new Pair<Alphabet, LR0Set>(
									symbol, succState);
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

		// TODO

		Iterator<Symbol> it = lexOutput.iterator();
		Iterator<Symbol> it2 = lexOutput.iterator();
		Pair<Alphabet, LR0Set> initialKellerInput = new Pair<Alphabet, LR0Set>(
				null, gt.initialState);
		Stack<Pair<Alphabet, LR0Set>> keller = new Stack<Pair<Alphabet, LR0Set>>();

		keller.push(initialKellerInput);

		Alphabet lookahead = it2.next().getToken();

		while (!keller.isEmpty()) {
			Pair<Alphabet, LR0Set> kellerSpitze = keller.peek();
			if (kellerSpitze.getSecond().containsFinalItem()) {
				// we do not actually have to empty the stack now
				if (!it.hasNext()) {
					// input completely read
					return analysis;
				} else {
					// Parser finished but there is more input
					throw new ParserException(
							"Parser finished but there is more unprocessed input!",
							analysis);
				}
			} else {
				if (kellerSpitze.getSecond().containsCompleteItem()
						&& gt.grammar.follow.get(
								kellerSpitze.getSecond().getCompleteItem()
										.getLhs()).contains(lookahead)) {
					// Item of the form [A -> alpha *]
					LR0Item completeItem = kellerSpitze.getSecond()
							.getCompleteItem();

					// remove |alpha| elements from the stack
					for (int i = 0; i < completeItem.rhs.length; i++) {
						keller.pop();
					}
					Pair<Alphabet, LR0Set> prevKellerSpitze;
					// I' := Top(Keller)
					prevKellerSpitze = keller.peek();
					// J := delta(I', A)
					LR0Set succState = gt.getSuccessor(
							prevKellerSpitze.getSecond(), completeItem.lhs);
					if (null == succState) {
						throw new ParserException("Tried reducing with rule "
								+ completeItem
								+ " but could not find a successor delta("
								+ prevKellerSpitze.getSecond() + ", "
								+ completeItem.lhs + ")", analysis);
					}
					Pair<Alphabet, LR0Set> succKellerSpitze = new Pair<Alphabet, LR0Set>(
							completeItem.lhs, succState);
					keller.push(succKellerSpitze);
					// prepend the applied rule (ensures correct order)
					analysis.add(0, completeItem);
				} else {
					if (!it.hasNext()) {
						// nothing more to read, nothing to reduce and no final
						// item
						throw new ParserException(
								"Only shift operation possible but the input terminated",
								analysis);
					} else {
						Token symbol = it.next().getToken();
						if (it2.hasNext()) {
							lookahead = it2.next().getToken();
						} else {
							lookahead = null;
						}
						LR0Set succState = gt.getSuccessor(
								kellerSpitze.getSecond(), symbol);
						if (null == succState) {
							throw new ParserException(
									"Tried shifting "
											+ symbol
											+ " onto the stack but could not find a successor delta("
											+ kellerSpitze.getSecond() + ", "
											+ symbol + ")", analysis);
						} else {
							Pair<Alphabet, LR0Set> succKellerSpitze = new Pair<Alphabet, LR0Set>(
									symbol, succState);
							keller.push(succKellerSpitze);
						}
					}
				}
			}
		}

		return analysis;
	}

	public GotoDFA getGotoDFA() {
		return gt;
	}
}
