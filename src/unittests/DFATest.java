package unittests;
import lexer.AbstractDFA;
import lexer.CommentDFA;
import lexer.DFA;
import lexer.LexerGenerator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexander
 */
public class DFATest
{
    @Test
    public void test1()
    {
        AbstractDFA whileDFA = new DFA("while", LexerGenerator.Token.WHILE);
        AbstractDFA commentDFA = new CommentDFA();
        String input="while";
        assertEquals(true, whileDFA.run(input));
        assertEquals(false, commentDFA.run(input));
    }
    @Test
    public void test2()
    {
        AbstractDFA whileDFA = new DFA("while", LexerGenerator.Token.WHILE);
        AbstractDFA commentDFA = new CommentDFA();
        String input="While";
        assertEquals(false, whileDFA.run(input));
        assertEquals(false, commentDFA.run(input));
    }


    @Test
    public void test3()
    {
        AbstractDFA whileDFA = new DFA("while", LexerGenerator.Token.WHILE);
        AbstractDFA commentDFA = new CommentDFA();
        String input="/**while*/";
        assertEquals(false, whileDFA.run(input));
        assertEquals(true, commentDFA.run(input));
    }

    @Test
    public void test4()
    {
        AbstractDFA whileDFA = new DFA("while", LexerGenerator.Token.WHILE);
        AbstractDFA commentDFA = new CommentDFA();
        String input="/* */*/";
        assertEquals(false, whileDFA.run(input));
        assertEquals(false, commentDFA.run(input));
    }

    @Test
    public void test5()
    {
        AbstractDFA whileDFA = new DFA("while", LexerGenerator.Token.WHILE);
        AbstractDFA commentDFA = new CommentDFA();
        String input="//foo\n";
        assertEquals(false, whileDFA.run(input));
        assertEquals(true, commentDFA.run(input));
    }

}
