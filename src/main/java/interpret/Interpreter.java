package interpret;

import ast.Action;
import ast.BinaryCondition;
import ast.Condition;
import ast.Expr;
import ast.FactorExpr;
import ast.MemExpr;
import ast.NumExpr;
import ast.Program;
import ast.RelationCondition;
import ast.Rule;
import ast.SensorExpr;
import ast.TermExpr;
import ast.UnaryExpr;
import ast.Update;

/**
 * An example interface for interpreting a critter program. This is just a starting
 * point and may be changed as much as you like.
 */
public interface Interpreter {

    /**
     * Execute program {@code p} until either the maximum number of rules per
     * turn is reached or some rule whose command contains an action is
     * executed.
     * @param p critter program to be interpreted
     * @return true if the interpreter found a valid action to be carried out
     */
    boolean interpretProgram(Program p);
    
   /**
    * Evaluate the given critter action
    * @param a - critter action to be evaluated
    */
    void eval(Action a);
    
    /**
     * Evaluate the given critter state update
     * @param u -= critter state update
     */
    void eval(Update u);

    /**
     * Evaluate the binary condition
     * @param c - Binary condition to be evaluated 
     * @return result of the binary condition
     */
	boolean eval(BinaryCondition c);

	/**
	 * Evaluate the relation condition
	 * @param c - Relation condition to be evaluated
	 * @return result of the relation condition 
	 */
	boolean eval(RelationCondition c);

	/**
	 * 
	 * @param n - Number expression to be evaluated 
	 * @return - value stored in the number expression
	 */
	int eval(NumExpr n);

	/**
	 * 
	 * @param t - Term Expression (+/-) to be evaluated 
	 * @return - value resulting from a PLUS or MINUS operation
	 */
	int eval(TermExpr t);

	/**
	 * 
	 * @param t - Factor Expression (*, /, mod) to be evaluated
	 * @return - value resulting from a MOD, MUL, or DIV operation
	 */
	int eval(FactorExpr t);

	/**
	 * 
	 * @param t - Sensor Expression to be evaluated
	 * @return - value resulting from the critter's senses
	 */
	int eval(SensorExpr t);

	/**
	 * 
	 * @param t - Memory Expression to be evaluated
	 * @return - the value resulting from the critter's memory space
	 */
	int eval(MemExpr t);
	/**
	 * 
	 * @param unaryExpr - Memory Expression to be evaluated
	 * @return - the value resulting from the critter's memory space
	 */
	int eval(UnaryExpr t);
}
