package ast;

import interpret.Interpreter;

/**
 * A critter program expression that has an integer value.
 */
public interface Expr extends Node {
	Expr clone();
	int accept(Interpreter i);
}
