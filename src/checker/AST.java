package checker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Stack;

import lexer.LexerGenerator.Token;
import lexer.Symbol;
import parser.Alphabet;
import parser.Grammar.NonTerminal;
import parser.Rule;

/**
 * Abstract syntax tree
 * Contains the root node of the AST. The tree is built of ASTNode objects.
 * Each ASTNode maintains a list of its direct successors (children nodes).
 */
public class AST {

	private ASTNode root;
	public ASTNode getRoot() {
		return root;
	}

	/**
	 * Given a list of symbols from the lexer and the sequence of (right-most) derivations from the parser,
	 * constructs the corresponding abstract syntax tree.
	 * @param symbols
	 * @param rightMostDerivation
	 */
	public AST(List<Symbol> symbols, List<Rule> rightMostDerivation){
		Stack<ASTNode> innerNodes = new Stack<ASTNode>();
		Iterator<Rule> derivationIterator = rightMostDerivation.iterator();
		ListIterator<Symbol> symbolsIterator = symbols.listIterator(symbols.size());
		
		assert(!rightMostDerivation.isEmpty());
		Rule rootRule = derivationIterator.next();
		root = new ASTNode(rootRule.getLhs());
		for (Alphabet rhs : rootRule.getRhs()){
			ASTNode child = new ASTNode(rhs);
			root.addChild(child);
			innerNodes.add(child);
		}
		
		while(innerNodes.size() > 0){
			ASTNode stackTop = innerNodes.pop();
			if(stackTop.getType() instanceof NonTerminal){
				Rule rule = derivationIterator.next();
				assert(rule.getLhs().equals(stackTop.getType()));
				for (Alphabet rhs : rule.getRhs()){
					ASTNode child = new ASTNode(rhs);
					stackTop.addChild(child);
					innerNodes.add(child);
				}
			}
			if(stackTop.getType() instanceof Token){
				assert(symbolsIterator.hasPrevious());
				stackTop.setAttribute(symbolsIterator.previous().getAttribute());
			}
		}
		assert(!derivationIterator.hasNext());
		assert(!symbolsIterator.hasPrevious());
	}
	
	/**
	 * Creates dot input representing the abstract syntax tree.
	 */
	public String ast2dot(){
		Queue<ASTNode> queue = new LinkedList<ASTNode>();
		queue.add(root);
		StringBuilder nodeBuilder = new StringBuilder();
		StringBuilder transitionBuilder = new StringBuilder();
		while(!queue.isEmpty()){
			ASTNode node = queue.poll();
			nodeBuilder.append(node.hashCode());
			nodeBuilder.append(" [label=\"");
			nodeBuilder.append(node.getType());
			if(node.getType() instanceof Token){
				nodeBuilder.append("\n");
				String attribute = node.getAttribute().replace("\"","\\\"");
				nodeBuilder.append(attribute);
				nodeBuilder.append("\n");
			}
			nodeBuilder.append("\"];\n");
			
			for(ASTNode child : node.getChildren()){
				transitionBuilder.append(node.hashCode());
				transitionBuilder.append(" -> ");
				transitionBuilder.append(child.hashCode());
				transitionBuilder.append(" [label=\"\"];\n");
			}
			queue.addAll(node.getChildren());
		}
		return "digraph{\n"+nodeBuilder.toString()+transitionBuilder.toString()+"}";
	}
	
}
