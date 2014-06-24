package checker;

import java.util.ArrayList;
import java.util.List;

import lexer.LexerGenerator.Token;
import parser.Alphabet;

public class ASTNode {
	
	/**
	 *  A list of the node's children
	 */
	//It is essential that the children are stored in an ordered list and not e.g. a set. Reading the leaves from left to right then gives us the same sequence as given by the lexer.
	private List<ASTNode> children = new ArrayList<ASTNode>();
	public List<ASTNode> getChildren() {
		return children;
	}

	/**
	 * This node's type indicates which non-terminal (e.g. expr) or token (e.g. ID) this node represents.
	 * Set by the constructor. 
	 */
	private final Alphabet type;
	public Alphabet getType() {
		return type;
	}
	
	/**
	 * In case this node is a leaf and represents a terminal (token), store the token's attribute here.
	 */
	private String attribute;
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute){
		this.attribute = attribute;
	}

	/**
	 * Constructor, requires the Alphabet element that this node represent, i.e. an instance of NonTerminal or Token.
	 * @param type
	 */
	public ASTNode(Alphabet type){
		this.type = type;
	}
	
	/**
	 * Constructor, requires the Alphabet element that this node represent, i.e. an instance of NonTerminal or Token.
	 * Additionally, assuming this node represents a Token (e.g. ID), the attribute (e.g. "x") is set here.
	 * @param type
	 * @param attribute
	 */
	public ASTNode(Alphabet type, String attribute){
		assert(type instanceof Token);
		this.type = type;
		this.attribute = attribute;
	}
	
	/**
	 * Append a list of child nodes to this node
	 * @param children
	 */
	public void addChildren(List<ASTNode> children) {
		this.children.addAll(children);
	}
	
	/**
	 * Append a single child to this node
	 * @param child
	 */
	public void addChild(ASTNode child) {
		this.children.add(child);
	}

}
