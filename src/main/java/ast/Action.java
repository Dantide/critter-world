package ast;

import ast.ActionImpl.Actions;
import interpret.Interpreter;

/**
 * Interface representing an Action in a Critter program
 */

public interface Action extends Node{
	Action clone();

	/**
	 * 
	 * @return the expression represented in this Action node
	 */
	Expr getExpr();

	/**
	 * 
	 * @return action represented by this Action
	 */
	Actions getActionType();
	
	void accept(Interpreter i);
}
