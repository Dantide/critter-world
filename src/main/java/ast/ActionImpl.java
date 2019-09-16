package ast;

import interpret.Interpreter;

/**
 * 
 * Representation of a critter action on the AST
 *
 */
public class ActionImpl extends AbstractNode implements Action{
	/**
	 * The type of action
	 */
	protected Actions action; 
	/**
	 * Expression needed for serve[] and tag[]
	 */
	protected Expr e;
	/**
	 * Constructor.
	 * @param action - the type of action
	 */
	public ActionImpl(Actions action) {
		this.action = action; 
		e = null;
	}
	
	/**
	 * Constructor
	 * Primarily to be used with TAG and SERVE actions that require an expression
	 * @param action - the type of action
	 * @param e - expression holding a value for the action
	 */
	public ActionImpl(Actions action, Expr e) {
		this.action = action;
		this.e = e; 
		this.e.setParent(this);
	}

	
	@Override
	public Actions getActionType() {
		return action;
	}
	
	@Override
	public Expr getExpr() {
		return e; 
	}
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if (sb == null) {
			throw new IllegalArgumentException();
		}
		sb.append(action.toString().toLowerCase());
		if (action == Actions.TAG || action == Actions.SERVE) {
			sb.append('[');
			e.prettyPrint(sb);
			sb.append(']');
		}
		return sb;
	}
	
	@Override
	public int size() {
		if (e == null)
			return SIZE;
		return e.size() + SIZE;
	}
	
	@Override
	public Node nodeAt(int index) {
		Node n = super.nodeAt(index);
		if (n == null && e != null) {
			n = e.nodeAt(index - 1); 
		}
		return n; 
	}
	@Override
	public boolean accept(Mutation m) {
		return m.mutate(this);
	}
	

	@Override
	public ActionImpl clone() {
		if (action == Actions.TAG || action == Actions.SERVE)
			return new ActionImpl(action, e.clone());
		return new ActionImpl(action);
	}
	
	/**
	 * 
	 * Enumeration of the different types of actions of a critter
	 *
	 */
	public enum Actions{
		WAIT, FORWARD, BACKWARD, LEFT, RIGHT, EAT, ATTACK, BUD, MATE, GROW, TAG, SERVE; 
	}

	@Override
	public void accept(Interpreter i) {
		i.eval(this);
	}
}
