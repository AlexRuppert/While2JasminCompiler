package unittests;

import lexer.LexerException;
import lexer.LexerGenerator;
import lexer.Symbol;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Alexander
 */
public class BacktrackingDFATest
{
    public static String file2String(String filename) throws IOException
    {
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
    @Test
    public void test1() throws IOException
    {
        String testFile="./test2/backtracking2.txt";
        String comparisonFile="./test2/symbols1.txt";

        String inputProgram = file2String(testFile);
        String testProgram = file2String(comparisonFile);
        List<Symbol> symbols=null;

        try {
            symbols = LexerGenerator.analyse(inputProgram);
        } catch (LexerException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getAnalysisBeforeFailure());
        }
        assertNotNull(symbols);

        StringBuilder sb= new StringBuilder();
        sb.append("[");
        for(int i = 0; i < symbols.size(); i++)
        {
            sb.append(symbols.get(i).toString());
            if(i<symbols.size()-1)
                sb.append(", ");

        }
        sb.append("]");

        assertEquals(testProgram,sb.toString());
    }
}
