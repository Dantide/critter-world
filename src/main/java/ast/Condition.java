package ast;


import interpret.Interpreter;

/**
 * An interface representing a Boolean condition in a critter program.
 *
 */
public interface Condition extends Node {
	Condition clone();
	boolean accept(Interpreter i); 
}
