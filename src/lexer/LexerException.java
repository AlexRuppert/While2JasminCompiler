package lexer;

import java.util.List;

import lexer.LexerGenerator.Token;

public class LexerException extends Exception {
	
	private List<Symbol> analysisBeforeFailure = null;

	public LexerException() {
	}

	public LexerException( String s ) {
		super( s );
	}
	
	public LexerException(String s, List<Symbol> analysis){
		this(s);
		this.analysisBeforeFailure = analysis;
	}
	
	public List<Symbol> getAnalysisBeforeFailure(){
		return analysisBeforeFailure;
	}
}
