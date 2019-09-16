package ast;

import interpret.Interpreter;
import parse.TokenType;

/**
 * 
 *  Representation of a critter memory expression mem[]
 *
 */
public class MemExpr extends AbstractNode implements Expr{
	/**
	 * Expression held within the brackets of the Mem[] to designate the Critter 
	 * characteristic 
	 */
	protected Expr x;
	
	/**
	 * If the critter program was written with syntactic sugar for a Memory reference,
	 * the type of it will be stored here
	 */
	protected MemSugar type;
	
	/**
	 * Constructor. 
	 * Creates a new memory expression representing the characteristic of a critter
	 * @param x - Expression representing value used in Critter memory
	 */
	public MemExpr(Expr x) {
		type = null; 
		this.x = x;
		this.x.setParent(this);
	}
	
	/**
	 * Constructor.
	 * Create a new memory expression representing the characterisitic of a critter
	 * Used in the case the memory expression is represented with syntactic sugar
	 * @param type - Type of syntactic sugar used as a substitute for critter memory
	 */
	public MemExpr(MemSugar type) {
		x = null; 
		this.type = type;
	}
	
	/**
	 * 
	 * @return the syntactic sugar for the memory expression
	 */
	public MemSugar getMemSugar() {
		return type;
	}
	
	/**
	 * 
	 * @return the expression representing this memory expression
	 */
	public Expr getExpr() {
		return x;
	}
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if (sb == null) {
			throw new IllegalArgumentException();
		}
		if (type == null) {
			sb.append("mem[");
			sb = x.prettyPrint(sb);
			sb.append("]");
		} else {
			sb.append(type);
		}
		return sb;
	}
	
	@Override
	public int size() {
		if (x == null)
			return SIZE;
		return x.size() + SIZE;
	}
	@Override
	public Node nodeAt(int index) {
		Node n = super.nodeAt(index); 
		if (n == null && x != null) {
			n = x.nodeAt(index - 1);
		}
		return n;
	}
	@Override
	public boolean accept(Mutation m) {
		return m.mutate(this);
	}
	@Override
	public MemExpr clone() {
		if (type == null)
			return new MemExpr(x.clone());
		return new MemExpr(type);
	}
	/**
	 * Enumeration of different types of syntactic sugar for memory expressions
	 */
	public enum MemSugar {
		MEMSIZE(0), DEFENSE(1), OFFENSE(2), SIZE(3), ENERGY(4), PASS(5), TAG(6), POSTURE(7);
		
		private int value; 
		
		private MemSugar(int value) {
			this.value = value; 
		}
		/**
		 * 
		 * @return the critter memory location associated with a certain syntactic sugar
		 */
		public int getValue() {
			return value;
		}
	}
	@Override
	public int accept(Interpreter i) {
		return i.eval(this);
	}
}
