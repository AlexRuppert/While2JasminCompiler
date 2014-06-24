package checker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import lexer.LexerGenerator.Token;
import lexer.Symbol;
import parser.Alphabet;
import parser.Grammar.NonTerminal;
import parser.Rule;

public class Checker {

	private AST ast;

	public AST getAst() {
		return ast;
	}

	/**
	 * Requires a right most analysis from the parser to initiate a new abstract
	 * syntax tree. This tree is then used for subsequent semantic checks.
	 * 
	 * @param analysis
	 */
	public Checker(List<Symbol> symbols, List<Rule> analysis) {
		ast = new AST(symbols, analysis);
	}

	/**
	 * Check if every identifier which is used has been declared before.
	 * 
	 * @return
	 */
	public boolean checkDeclaredBeforeUsed() {
		Stack<List<String>> attributeStack = new Stack<List<String>>();
		List<String> stackElement;

		ASTNode root = ast.getRoot();

		Queue<ASTNode> queue = new LinkedList<ASTNode>();
		queue.add(root);
		while (!queue.isEmpty()) {
			ASTNode node = queue.poll();

			Alphabet type = node.getType();
			if (type == NonTerminal.start || type == NonTerminal.branch
					|| type == NonTerminal.loop) {
				// new block entered
				stackElement = new ArrayList<String>();
				attributeStack.push(stackElement);
			}
			stackElement = attributeStack.peek();
			if (type == NonTerminal.declaration) {
				// track declarations within current block
				stackElement.add(findDeclaredID(node));
			} else if (type == NonTerminal.expr
					&& node.getChildren().get(0).getType() == Token.ID) {
				// check if used identifier is declared
				String name = node.getChildren().get(0).getAttribute();
				if (!isInStack(attributeStack, name)) {
					return false;
				}
			} else if (type == Token.RBRACE) {
				// RBRACEs always indicate the end of a block
				stackElement = attributeStack.pop();
			}

			queue.addAll(node.getChildren());
		}

		return true;
	}

	private boolean isInStack(Stack<List<String>> stack, String s) {
		for (List<String> list : stack) {
			if (list.contains(s)) {
				return true;
			}
		}

		return false;
	}

	private String findDeclaredID(ASTNode node) {
		assert (node.getType().equals(NonTerminal.declaration));
		assert (node.getChildren().size() == 2);
		return node.getChildren().get(1).getAttribute();
	}

}
