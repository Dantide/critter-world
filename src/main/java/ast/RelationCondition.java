package ast;

import interpret.Interpreter;

/**
 * 
 *  A representation of a relation between two expressions and a relation operator
 *  
 *
 */
public class RelationCondition extends AbstractNode implements Condition{
	/**
	 * Right side expression being compared to {@code y}
	 */
	protected Expr x;
	/**
	 * Operator comparing {@code x} and {@code y}
	 */
	protected Operator rel;
	/**
	 * Left side expression being compared to {@code x}
	 */
	protected Expr y; 
	
	/**
	 * Constructor. 
	 * 
	 * Create an AST representation of x rel y
	 * 
	 * @param x Expression being compared to {@code y}
	 * @param rel Relation Operator
	 * @param y Expression being compared to {@code x}
	 */
	public RelationCondition(Expr x, Operator rel, Expr y) {
		this.x = x;
		this.y = y;
		this.rel = rel; 
		this.x.setParent(this);
		this.y.setParent(this);
	}
	/**
	 * 
	 * @return - the operator for this relation condition
	 */
	public Operator getOperator() {
		return rel; 
	}
	/**
	 * 
	 * @return one of the expressions being evaluated by this relation condition
	 */
	public Expr getExprOne(){
		return x;
	}
	/**
	 * 
	 * @return one of the expressions being evaluated by this relatin condition
	 */
	public Expr getExprTwo() {
		return y; 
	}
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if (sb == null) {
			throw new IllegalArgumentException();
		}
		x.prettyPrint(sb);
		switch(rel) {
		case LT:
			sb.append(" < ");
			break;
		case LE:
			sb.append(" <= ");
			break;
		case EQ:
			sb.append(" = ");
			break;
		case GE:
			sb.append(" >= ");
			break;
		case GT:
			sb.append(" > ");
			break;
		case NE:
			sb.append(" != ");
			break;
		}
		y.prettyPrint(sb);
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
	public RelationCondition clone() {
		return new RelationCondition(x.clone(), rel, y.clone());
	}
	
	/**
	 * 
	 * Enumeration of all possible relation operators
	 *
	 */
	public enum Operator {
		LT , LE, EQ, GE, GT, NE;
	}

	@Override
	public boolean accept(Interpreter i) {
		
		return i.eval(this);
	}
}
