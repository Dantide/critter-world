package ast;

/**
 * 
 * Abstract class containing useful code used across all nodes in the AST
 *
 */
public abstract class AbstractNode implements Node {
	/**
	 * This is a pointer to the parent Node
	 */
	protected Node parent;
	/**
	 * The size of the number of nodes below this node
	 */
	protected final int SIZE = 1;
	
	
	@Override
	public void setParent(Node p) {
		parent = p;
	}
	
	@Override
	public Node getParent() {
		return parent;
	}
	
	@Override
	public Node nodeAt(int index) {

		if (index >= size() || index < 0) {
			throw new IndexOutOfBoundsException("The index you selected was larger than the number of nodes in the program.");
		}
		
		if (index == 0) {
			return this; 
		}
		return null;
	}
	
	@Override
	public String toString() {
		return prettyPrint(new StringBuilder()).toString(); 
	}
	
	@Override
	public abstract Node clone();
}
