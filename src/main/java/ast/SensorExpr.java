package ast;

import interpret.Interpreter;

public class SensorExpr extends AbstractNode implements Expr  {
	/**
	 * Expression holding any value held within the brackets of sense[]
	 */
	protected Expr x;
	/**
	 * The type of sensor this expression represents 
	 */
	protected SensorType type; 
	/**
	 * Number of children below this parent expression node
	 */
	protected int size;
	
	/**
	 * Constructor.
	 * Takes the sensor type and the expression holding a value for that sensor
	 * @param e - expression holding a value for the sensor
	 * @param type - the type of sensor 
	 */
	public SensorExpr(Expr e, SensorType type) {
		this.x = e;
		this.type = type;
		this.x.setParent(this);
		size = 1;

	}
	
	/**
	 * Constructor. 
	 * Designed to handle smell sensor
	 * @param type - type of sensor 
	 */
	public SensorExpr(SensorType type) {
		assert (type == SensorType.SMELL);
		x = null; 
		this.type = type;
		size = 0;

	}
	
	/**
	 * 
	 * @return the type of the sensor represented by this sensor expression 
	 */
	public SensorType getSensorType() {
		return type; 
	}
	/**
	 * 
	 * @return the expression held by the sensor expression signifying 
	 * how the critter should sense its environment
	 */
	public Expr getSensorExpr() {
		return x; 
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
	public StringBuilder prettyPrint(StringBuilder sb) {
		if (sb == null) {
			throw new IllegalArgumentException();
		}
		sb.append(type.toString().toLowerCase());
		if (x != null) {
			sb.append('[');
			x.prettyPrint(sb);
			sb.append(']');
		}
		return sb;
	}
	
	@Override
	public int size() {
		if (x == null)
			return size;
		return x.size() + size;
	}
	
	@Override
	public boolean accept(Mutation m) {
		return m.mutate(this);
	}
	
	@Override
	public SensorExpr clone() {
		if (type == SensorType.SMELL)
			return new SensorExpr(SensorType.SMELL);
		return new SensorExpr(x.clone(), type);
	}
	/**
	 * 
	 * Enumeration of all possible types of critter senses held in this node
	 *
	 */
	public enum SensorType{
		NEARBY, AHEAD, RANDOM, SMELL; 
	}
	
	@Override
	public int accept(Interpreter i) {
		return i.eval(this);
	}
	
}
