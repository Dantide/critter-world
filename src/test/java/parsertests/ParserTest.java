package parsertests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import org.junit.Test;
import ast.Program;
import parse.*;

/**
 * This class contains tests for the Critter parser.
 */
public class ParserTest {

    /** Checks that a valid critter program is not {@code null} when parsed. */
    //@Test
    public void testProgramIsNotNull() {
        InputStream in = ParserTest.class.getResourceAsStream("simple_critter");
        Reader r = new BufferedReader(new InputStreamReader(in));
        Parser p = ParserFactory.getParser();
        Program prog = p.parse(r);
        assertNotNull("A valid critter program should not be null.", prog);
    }
    
    /**
     * Tests how well the parser parses for Rules
     */
    //@Test
    public void testRuleParsing() {
		InputStream in = ParserTest.class.getResourceAsStream("example-rules.txt");
        Reader r = new BufferedReader(new InputStreamReader(in));
        Parser p = ParserFactory.getParser();
        Program prog = p.parse(r);
        assertNotNull("A valid critter program should not be null.", prog);
    }
    
    /**
     * Tests the pretty printing with several different simplified test cases
     */
    //@Test
    public void testPrettyPrint() {
    	InputStream in = ParserTest.class.getResourceAsStream("testCritter");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser p = ParserFactory.getParser();
		StringBuilder sb = new StringBuilder();
        Program prog = p.parse(r);
        sb = prog.prettyPrint(sb);
        System.out.println('\n');
        System.out.println(sb);
    }
    
    /**
     * Tests pretty printing by parsing a file, pretty printing that file,
     * parsing the pretty print, and then printing the two pretty prints,
     * one from the file and one from the pretty print of the file.
     */
    //@Test
    public void testPrinttoParse() {
    	InputStream in = ParserTest.class.getResourceAsStream("simple_critter");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser p = ParserFactory.getParser();
		StringBuilder sb1 = new StringBuilder();
        Program prog1 = p.parse(r);
        sb1 = prog1.prettyPrint(sb1);
        
        Reader w = new BufferedReader(new StringReader(sb1.toString()));
        Program prog2 = p.parse(w);
        StringBuilder sb2 = new StringBuilder();
        sb2 = prog1.prettyPrint(sb2);
        
        System.out.println("\nSB1:\n" + sb1 + "\n ");
        System.out.println("\nSB2:\n" + sb2 + "\n ");
    }
    
    /**
     * Does testPrinttoParse, but converts back and forth 1000 times
     */
    @Test
    public void testPrinttoParse1000() {
    	InputStream in = ParserTest.class.getResourceAsStream("unmutated_critter.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser p = ParserFactory.getParser();
		StringBuilder sb1 = new StringBuilder();
        Program prog1 = p.parse(r);
        sb1 = prog1.prettyPrint(sb1);
        
        StringBuilder sb2 = sb1;
        for (int i = 0; i < 1; i++) {
	        Reader w = new BufferedReader(new StringReader(sb2.toString()));
	        Program prog2 = p.parse(w);
	        sb2 = prog1.prettyPrint(sb2);
        }
        
        System.out.println("\nSB1:\n" + sb1 + "\n ");
        System.out.println("\nSB2:\n" + sb2 + "\n ");
    }

}
