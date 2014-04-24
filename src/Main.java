import java.io.FileReader;
import java.io.IOException;

import lexer.*;
import lexer.LexerGenerator.Token;

public class Main {

	private static String inputProgram; // Contents of the input file
	
	public static void main(String[] args) {
		// If args is not a path to a text file, show help.
		// Otherwise open the file
		if(args.length != 1){
			showHelp();
			System.exit(0);
		}
		else{
			try {
				inputProgram = file2String(args[0]);
			} catch (IOException e) {
				e.printStackTrace();
				showHelp();
				System.exit(0);
			}
		}
		
		// Lexical Analysis
		System.out.println("input: "+inputProgram);
		// here comes the call and result of the automaton that recognises "while"
		AbstractDFA whileDFA = new DFA("while",Token.WHILE);
		System.out.println("WHILE: "+whileDFA.run(inputProgram));
		// here comes the call and result of the automaton that recognises comments
		AbstractDFA commentDFA = new CommentDFA();
		System.out.println("COMMENT: "+commentDFA.run(inputProgram));
				
		// Syntactical Analysis
						
		// Semantical Analysis
		
		// Byte Code Generation
		
	}
	
	public static void showHelp(){
		System.out.println("Usage: java Main PATH_TO_SOURCE_FILE");
	}
	
	public static String file2String(String filename) throws IOException {
		// Versuche Datei zu Öffnen - Löst FileNotFoundException aus,
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
