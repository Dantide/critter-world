package ast;

import interpret.Interpreter;

/**
 * 
 * Representation of an expression with +/- 
 *
 */
public class TermExpr extends AbstractNode implements Expr {
	/**
	 * Right side expression being operated with {@code y}
	 */
	protected Expr x;
	/**
	 * Operator on {@code x} and {@code y}
	 */
	protected Operator rel;
	/**
	 * Left side expression being operated with {@code x}
	 */
	protected Expr y;
	
	/**
	 * Constructor. 
	 * Takes two expressions as parameters 
	 * @param x - expression to be operated on
	 * @param op - add/subtract operator to be used on the {@code x} and {@code y}
	 * @param y - expression to be operated on
	 */
	public TermExpr(Expr x, Operator op, Expr y) {
		this.x = x;
		this.rel = op;
		this.y = y;
		this.x.setParent(this);
		this.y.setParent(this);
	}

	/**
	 * 
	 * @return the operator of this term expression
	 */
	public Operator getOperator() {
		return rel; 
	}
	/**
	 * 
	 * @return the left side of the Term Expression 
	 */
	public Expr getExprOne() {
		return x;
	}
	/**
	 * 
	 * @return the right side of the Term Expression
	 */
	public Expr getExprTwo() {
		return y;
	}
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if (sb == null) {
			throw new IllegalArgumentException();
		}
		if (x.getClass() == TermExpr.class || x.getClass() == FactorExpr.class)
			sb.append('(');
		x.prettyPrint(sb);
		if (x.getClass() == TermExpr.class || x.getClass() == FactorExpr.class)
			sb.append(')');
		
		switch(rel) {
		case PLUS:
			sb.append(" + ");
			break;
		case MINUS:
			sb.append(" - ");
			break;
		}
		
		if (y.getClass() == TermExpr.class || y.getClass() == FactorExpr.class)
			sb.append('(');
		y.prettyPrint(sb);
		if (y.getClass() == TermExpr.class || y.getClass() == FactorExpr.class)
			sb.append(')');
		
		return sb;
	}
	
	@Override
	public int size() {
		return x.size() + y.size() + SIZE;
	}
	@Override
	public Node nodeAt(int index) {
		Node n = super.nodeAt(index);
		int size = 0;
		if (n == null) {
			size = x.size();
			
			if (size >= index) {
				n = x.nodeAt(index - 1);
			}
			else {
				index -= size;
			}
		}
		if (n == null) {
			n = y.nodeAt(index - 1); 
		}
		return n; 
	}
	@Override
	public boolean accept(Mutation m) {
		return m.mutate(this);
	}
	@Override
	public TermExpr clone() {
		return new TermExpr(x.clone(), rel, y.clone());
	}
	/**
	 * 
	 * Enumeration of possible operations in a Term Expression
	 *
	 */
	public enum Operator{
		PLUS, MINUS;
	}
	@Override
	public int accept(Interpreter i) {
		return i.eval(this);
	}
}
