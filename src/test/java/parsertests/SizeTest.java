package parsertests;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import ast.Node;
import ast.Program;
import parse.*;
public class SizeTest {
	/**
     * Tests the size using a text file where we know the size of the tree already
     */
    //@Test
    public void testSize() {
    	InputStream in = ParserTest.class.getResourceAsStream("size_test.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser p = ParserFactory.getParser();
        Program prog1 = p.parse(r);
        System.out.println(prog1.size());
        assertTrue(prog1.size() == 36);
    }
}
