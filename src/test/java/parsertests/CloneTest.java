package parsertests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Test;

import ast.NumExpr;
import ast.Program;
import ast.TermExpr;
import ast.TermExpr.Operator;
import parse.Parser;
import parse.ParserFactory;

public class CloneTest {
	
	/**
	 * Tests if Operators can be used with ==
	 */
	//@Test
    public void testOperatorEquals() {
		Operator op = Operator.PLUS;
		Operator plus = Operator.PLUS;
		Operator minus = Operator.MINUS;
		assertTrue(op == Operator.PLUS);
		assertTrue(op == plus);
		assertFalse(op == minus);
		System.out.println(op);
		
		op = Operator.MINUS;
		assertTrue(op == Operator.MINUS);
		assertFalse(op == plus);
		assertTrue(op == minus);
    }
	
	/**
	 * Tests if Operators pass by reference or value, and how
	 */
	//@Test
	public void testOperatorReference() {
		Operator op = Operator.PLUS;
		Operator other = op;
		op = Operator.MINUS;
		assertTrue(other == Operator.PLUS);
	}
	
	/**
	 * Test the clone method. clone should not have a pointer to prog,
	 * but they should prettyPrint the same ast
	 */
	@Test
	public void testClone() {
		InputStream in = ParserTest.class.getResourceAsStream("simple_critter");
        Reader r = new BufferedReader(new InputStreamReader(in));
        Parser p = ParserFactory.getParser();
        Program prog = p.parse(r);
        
        Program clone = prog.clone();
        System.out.println(clone.prettyPrint(new StringBuilder()));
        
        assertTrue(clone.prettyPrint(new StringBuilder()).toString().equals(
        		prog.prettyPrint(new StringBuilder()).toString()));
        assertFalse(clone == prog);
	}
}
