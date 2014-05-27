import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lexer.LexerException;
import lexer.LexerGenerator;
import lexer.LexerGenerator.Token;
import lexer.Symbol;
import parser.*;

public class Main {

	private static String inputProgram; // Contents of the input file
	
	public static void main(String[] args) {
		// If args is not a path to a text file, show help.
		// Otherwise open the file
//		if(args.length != 1){
//			showHelp();
//			System.exit(0);
//		}
//		else{
//			try {
//				inputProgram = file2String(args[0]);
//			} catch (IOException e) {
//				e.printStackTrace();
//				showHelp();
//				System.exit(0);
//			}
//		}
		
		// Lexical Analysis
//		List<Symbol> symbols = null;
//		try {
//			symbols = LexerGenerator.analyse(inputProgram);	
//			System.out.println("Symbol stream: "+symbols);
//		} catch (LexerException e) {
//			System.out.println(e.getMessage());
//			System.out.println(e.getAnalysisBeforeFailure());
//		}
				
		// Syntactical Analysis
		Grammar grammar = WhileGrammar.getInstance();
//		System.out.println(grammar);
		GotoDFA gt = new GotoDFA(grammar);
		gt.printLR0Sets();
		System.out.println("There are "+gt.states.size()+" LR(0) sets.");
		System.out.println(gt.countConflicts()+" conflicts were detected");
//		System.out.println(parser.getGotoDFA());
						
		// Semantical Analysis
				
		// Byte Code Generation
	}
	
	public static void showHelp(){
		System.out.println("Usage: java Main PATH_TO_SOURCE_FILE");
	}
	
	public static String file2String(String filename) throws IOException {
		// Versuche Datei zu öffnen - Löst FileNotFoundException aus,
		// wenn Datei nicht existiert, ein Verzeichnis ist oder nicht gelesen
		// werden kann
		FileReader in = new FileReader(filename);
		// Dateiinhalt in String lesen
		String str = file2String(in);
		// Stream schließen und String zurückliefern
		in.close();
		return str;
	}
	
	public static String file2String(FileReader in) throws IOException {
		StringBuilder str = new StringBuilder();
		int countBytes = 0;
		char[] bytesRead = new char[512];
		while( (countBytes = in.read(bytesRead)) > 0)
			str.append(bytesRead, 0, countBytes);
		return str.toString();
	}

}
