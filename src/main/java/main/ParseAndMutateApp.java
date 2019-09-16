package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import ast.Program;
import parse.Parser;
import parse.ParserFactory;


public class ParseAndMutateApp {

	public static void main(String[] args) {
		int n = 0;
		String file;
		
		try {
			if (args.length == 1) {
				file = args[0];
			} else if (args.length == 3 && args[0].equals("--mutate")) {
				
				n = parsePositive(args[1]);
				file = args[2];
			} else {
				throw new IllegalArgumentException();
			}
			File fileName = new File(file);
			InputStream in = new FileInputStream(fileName); 
	        Reader r = new BufferedReader(new InputStreamReader(in));
			Parser p = ParserFactory.getParser();
			Program program = p.parse(r);
			System.out.println(program);
			if (n > 0) {
				for (int i = 0; i < n; i++) {
					program = program.mutate();
					System.out.println(program);
				}
			}
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Usage:\n" + "  <input_file>\n" + " --mutate <n> <input_file");
		} catch (FileNotFoundException e) {
			System.out.println("You did not enter a valid filepath. Please try again.");
		}
	}

	/**
     * Parses {@code str} to an integer.
     * 
     * @param str
     *            the string to parse
     * @return the integer represented by {@code str}
     * @throws NumberFormatException
     *             if {@code str} does not contain a parsable integer
     * @throws IllegalArgumentException
     *             if {@code str} represents a negative integer
     */
	public static int parsePositive(String str) {
		int n = Integer.parseInt(str);
		if (n < 0) { throw new IllegalArgumentException(); }
		else { return n; }
	}
}
