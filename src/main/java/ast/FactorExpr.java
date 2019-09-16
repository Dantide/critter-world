package ast;

import interpret.Interpreter;

/**
 * 
 * Representation of a factor expression with *  /  mod operations
 *
 */
public class FactorExpr extends AbstractNode implements Expr{
	/**
	 * The right side of the factor expression
	 */
	protected Expr x;
	
	/**
	 * The left side of the factor expression 
	 */
	protected Expr y;
	
	/**
	 * The operation represented by this node in the AST
	 */
	protected Operator op; 
	
	/**
	 * Constructor.
	 * @param x - Right side of the operation
	 * @param op - Operator doing something to {@code x} and {@code y}
	 * @param y - Left side of the operation 
	 */
	public FactorExpr(Expr x, Operator op, Expr y) {
		this.x = x;
		this.op = op;
		this.y = y;
		this.x.setParent(this);
		this.y.setParent(this);
	}

	/**
	 * 
	 * @return the operation represented through this node 
	 */
	public Operator getOperator() {
		return op;
	}
	
	/**
	 * 
	 * @return the left expression being operated on by {@code op}
	 */
	public Expr getExprOne() {
		return x;
	}
	/**
	 * 
	 * @return the right expression being operated on by {@code op}
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
		
		switch(op) {
		case MUL:
			sb.append(" * ");
			break;
		case DIV:
			sb.append(" / ");
			break;
		case MOD:
			sb.append(" mod ");
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
		// TODO Auto-generated method stub
		return m.mutate(this);
	}
	@Override
	public Expr clone() {
		return new FactorExpr(x.clone(), op, y.clone());
	}
	/**
	 * 
	 * Enumeration of all possible operations in a factor expression 
	 *
	 */
	public enum Operator{
		MUL, DIV, MOD; 
	}
	
	@Override
	public int accept(Interpreter i) {
		return i.eval(this);
	}
}
