package parsertests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Test;

import ast.BinaryCondition;
import ast.Condition;
import ast.FactorExpr;
import ast.NumExpr;
import ast.Program;
import ast.RelationCondition;
import ast.Rule;
import ast.TermExpr;
import ast.TermExpr.Operator;
import interpret.Interpreter;
import interpret.InterpreterImpl;
import parse.Parser;
import parse.ParserFactory;
public class InterpreterTests {
	@Test
	public void testIntepretCondition() {
		InputStream in = ParserTest.class.getResourceAsStream("testCritter");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser p = ParserFactory.getParser();
		StringBuilder sb = new StringBuilder();
        Program prog = p.parse(r);
        Interpreter i = new InterpreterImpl(null);
        assertTrue(((BinaryCondition)prog.nodeAt(2)).accept(i));
        assertFalse(((RelationCondition)prog.nodeAt(3)).accept(i)); 
        assertTrue(((TermExpr)prog.nodeAt(4)).accept(i) == 2343);
        assertTrue(((NumExpr)prog.nodeAt(5)).accept(i) == 3);
        assertTrue(((FactorExpr)prog.nodeAt(6)).accept(i) == 2340); 
        assertTrue(((TermExpr)prog.nodeAt(9)).accept(i) == 2);
        assertTrue(((FactorExpr)prog.nodeAt(11)).accept(i) == 0);
        assertTrue(((RelationCondition)prog.nodeAt(14)).accept(i)); 
        
	}

}
