package parser;

import java.util.List;

public class ParserException extends Exception {
	
	private List<Rule> analysisBeforeFailure = null;

	public ParserException() {
	}

	public ParserException( String s ) {
		super( s );
	}
	
	public ParserException(String s, List<Rule> analysis){
		this(s);
		this.analysisBeforeFailure = analysis;
	}
	
	public List<Rule> getAnalysisBeforeFailure(){
		return analysisBeforeFailure;
	}

}
