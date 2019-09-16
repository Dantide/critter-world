package ast;

import interpret.Interpreter;

/**
 * 
 * Expression that represents a unary operation on an expression
 * Ex. -(2 + 3)
 *
 */
public class UnaryExpr extends AbstractNode implements Expr{

	/**
	 * Expression represented by this unary expression
	 */
	protected Expr x;
	
	/**
	 * Initializer.
	 * @param x - expression held within this unary expression
	 */
	public UnaryExpr(Expr x) {
		this.x = x;
		this.x.setParent(this);
	}
	
	@Override
	public int size() {
		
		return x.size() + SIZE;
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append("-");
		sb.append("(");
		x.prettyPrint(sb);	
		sb.append(")");
		return sb;
	}

	@Override
	public boolean accept(Mutation m) {
		return m.mutate(this);
	}

	@Override
	public Expr clone() {
		return new UnaryExpr(x.clone());
	}

	@Override
	public int accept(Interpreter i) {
		return i.eval(this);
	}
	@Override
	public Node nodeAt(int index) {
		Node n = super.nodeAt(index); 
		if (n == null && x != null) {
			n = x.nodeAt(index - 1);
		}
		return n;
	}
	/**
	 * 
	 * @return the expression representing this unary operation
	 */
	public Expr getExpr() {
		return x;
	}

}
