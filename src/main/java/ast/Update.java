package ast;

import interpret.Interpreter;

/**
 * 
 * Interface representing an update in a critter program
 *
 */
public interface Update extends Node{
	Update clone();

	void accept(Interpreter i);
	/**
	 * 
	 * @return the left expression specifying the critter memory area to update
	 */
	Expr getExprOne();
	/**
	 * 
	 * @return the right expression specifying what {@code x} should be updated to
	 */
	Expr getExprTwo();
}
