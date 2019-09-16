package ast;

import interpret.Interpreter;

/**
 * 
 *  Representation of a number expression 
 *
 */
public class NumExpr extends AbstractNode implements Expr{
	protected int value; 
	/**
	 * Constructor. 
	 * @param value - value of the number
	 */
	public NumExpr(int value) {
		this.value = value; 
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if (sb == null) {
			throw new IllegalArgumentException();
		}
		return sb.append(value);
	}
	
	@Override
	public int size() {
		return SIZE;
	}
	@Override
	public Node nodeAt(int index) {
		Node n = super.nodeAt(index);
		return n;
	}
	@Override
	public boolean accept(Mutation m) {
		
		return m.mutate(this);
	}
	@Override
	public NumExpr clone() {
		return new NumExpr(value);
	}

	@Override
	public int accept(Interpreter i) {
		return i.eval(this);
	}
	/**
	 * 
	 * @return value held in this node 
	 */
	public int getValue() {
		return value; 
	}
}
