package parsertests;
import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.junit.Test;
import ast.DuplicateMutation;
import ast.InsertMutation;
import ast.Program;
import ast.RemoveMutation;
import ast.ReplaceMutation;
import ast.SwapMutation;
import ast.TransformMutation;
import parse.*;

public class MutationTests {

	//@Test
	public void testRemoveMutation() {
		InputStream in = ParserTest.class.getResourceAsStream("testCritter");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser p = ParserFactory.getParser();
		StringBuilder sb = new StringBuilder();
        Program prog = p.parse(r);
        System.out.println(prog.nodeAt(3));
        System.out.println(prog.mutate(3, new RemoveMutation()));

        
	}
	
	//@Test 
	public void testSwapMutation() {
		InputStream in = ParserTest.class.getResourceAsStream("testCritter");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser p = ParserFactory.getParser();
		StringBuilder sb = new StringBuilder();
        Program prog = p.parse(r);

        for (int i = 0;i < prog.size(); i++) {
        		prog.mutate(i, new SwapMutation()); 
        }
        System.out.println(prog);

	}
	
	//@Test
	public void testTransformMutation() {
		InputStream in = ParserTest.class.getResourceAsStream("testCritter");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser p = ParserFactory.getParser();
		StringBuilder sb = new StringBuilder();
        Program prog = p.parse(r);
        for (int i = 0; i< prog.size(); i++) {
        		prog = prog.mutate(i, new TransformMutation()); 
        }
        System.out.println(prog);
	}
	
	//@Test 
	public void testReplaceMutation() {
		InputStream in = ParserTest.class.getResourceAsStream("testCritter");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser p = ParserFactory.getParser();
		StringBuilder sb = new StringBuilder();
        Program prog = p.parse(r);
        System.out.println(prog.nodeAt(3));
        	prog = prog.mutate(3, new ReplaceMutation());
        System.out.println(prog.nodeAt(3));

	}
       
	//@Test
	public void testDuplicateMutation() {
		InputStream in = ParserTest.class.getResourceAsStream("testCritter");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser p = ParserFactory.getParser();
        Program prog = p.parse(r);
        
        prog = prog.mutate(0, new DuplicateMutation());
        System.out.println(prog.prettyPrint(new StringBuilder()));
        
        int i = prog.get(0).size()+1 + prog.get(1).size();
        prog = prog.mutate(i, new DuplicateMutation());
        System.out.println(prog.prettyPrint(new StringBuilder()));

	}
	
	//@Test
	public void testInsertMutation() {
		InputStream in = ParserTest.class.getResourceAsStream("testCritter");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser p = ParserFactory.getParser();
        Program prog = p.parse(r);
        
        System.out.println('\n');
        assertNull(prog.mutate(0, new InsertMutation()));
        System.out.println(prog.nodeAt(1));
        System.out.println(prog.nodeAt(6));
        prog = prog.mutate(6, new InsertMutation());
        System.out.println(prog.nodeAt(1));
	}
	
	@Test
	public void mutation() {
		InputStream in = ParserTest.class.getResourceAsStream("unmutated_critter.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser p = ParserFactory.getParser();
        Program prog = p.parse(r);
        Program prev = null;
		for (int i = 0; i < 1000; i++) {
			prev = prog;
			prog = prog.mutate();
			System.out.println(prog);
			assertTrue(prog.toString() != prev.toString());
		}
		
	}
}
