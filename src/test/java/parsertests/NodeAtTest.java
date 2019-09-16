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

public class NodeAtTest {
	@Test
    public void testNodeAt() {
    		InputStream in = ParserTest.class.getResourceAsStream("size_test.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser p = ParserFactory.getParser();
		StringBuilder sb1 = new StringBuilder();
        Program prog1 = p.parse(r);
        int total = 0;
        for (int i = 0; i < prog1.size(); i++) {
        		System.out.println(prog1.nodeAt(i));
        		total += 1;
        }
        System.out.println(total);
    }
}
