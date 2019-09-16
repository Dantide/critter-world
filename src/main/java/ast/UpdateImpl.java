package ast;

import interpret.Interpreter;

/**
 * 
 *  Representation of a critter update in the AST
 *  mem[expr] := expr 
 *
 */
public class UpdateImpl extends AbstractNode implements Update{
	
	/**
	 * Expression representing value to be updated
	 */
	protected Expr x;
	/**
	 * Expression that represents the value the new value {@code x} is being set to =
	 */
	protected Expr y;
	/**
	 * Constructor.
	 * 
	 * Creates a new representation of a critter update in the AST
	 * 
	 * @param x The critter characteristic to be updated
	 * @param y The new expression the critter characteristic ({@code x}) will be updated to
	 */
	
	public UpdateImpl(Expr x, Expr y) {
		this.x = x;
		this.y = y; 
		this.x.setParent(this);
		this.y.setParent(this);
	}

	
	@Override
	public Expr getExprOne() {
		return x;
	}
	
	
	@Override
	public Expr getExprTwo() {
		return y; 
	}
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if (sb == null) {
			throw new IllegalArgumentException();
		}
		x.prettyPrint(sb);
		sb.append(" := ");
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
			System.out.println(index);
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
	public UpdateImpl clone() {
		return new UpdateImpl(x.clone(), y.clone());
	}
	
	@Override
	public boolean accept(Mutation m) {
		return m.mutate(this);
	}
	@Override
	public void accept(Interpreter i) {
		i.eval(this);
	}
	
}
