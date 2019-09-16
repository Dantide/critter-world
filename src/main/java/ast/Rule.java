package ast;

import java.util.ArrayList;
import java.util.List;

import interpret.Interpreter; 
/**
 * A representation of a critter rule.
 */
public class Rule extends AbstractNode implements Node {
	/**
	 * Condition connected to this Rule 
	 */
	private Condition c; 
	/**
	 * Action connected to this Rule 
	 */
	private Action a; 
	/**
	 * List of Updates connected to this Rule
	 */
	private List<Update> updates; 
	
	/**
	 * Constructor.
	 * Creates a new rule with the given condition {@code c}
	 * @param c - Condition associated with {@code this}
	 */
	public Rule(Condition c) {
		this.c = c;
		c.setParent(this);
		updates = new ArrayList<Update>();
		a = null;
	}
	
	/**
	 * Adds an update to this Rule node in the AST
	 * @param u - Update to be added to {@code this}
	 */
	public void addUpdate(Update u) {
		u.setParent(this);
		updates.add(u);
	}
	
	/**
	 * Removes an update node child from this rule node
	 * @param u - update node to be removed 
	 */
	public void removeUpdate(Update u) {
		updates.remove(u);
	}
	
	/**
	 * 
	 * @return the number of updates in the rule
	 */
	public int numberOfUpdates() {
		return updates.size();
	}
	
	/**
	 * Removes the action node from this Rule
	 */
	public void removeAction() {
		a = null;
	}
	/**
	 * Returns the condition held within this rule node 
	 * @return - condition 
	 */
	public Condition getCondition() {
		return c; 
	}
	/**
	 * 
	 * @return true if there is an action represented in this node, false otherwise
	 */
	public boolean actionExists() {
		return a != null;
	}
	
	/**
	 * Sets the action result of this Rule node
	 * @param a - Action to be associated with {@code this}
	 */
	public void setAction(Action a) {
		this.a = a;
		a.setParent(this);
	}
	/**
	 * 
	 * @return the action stored in this Rule node
	 */
	public Action  getAction() {
		return a;
	}
	/**
	 * Set condition of this Rule node
	 * @param c - Condition to be associated with {@code this}
	 */
	public void setCondition(Condition c) {
		this.c = c;
	}
	
	/**
	 * Get the update at the index in rule
	 * @return - reference to the update or null if index too large or small
	 */
	public Update getUpdate(int index) {
		if (index < 0 || index >= updates.size())
			return null;
		return updates.get(index);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		c.prettyPrint(sb);
		sb.append(" --> ");
		int indent = sb.length();
		for (Update up : updates) {
			up.prettyPrint(sb);
			sb.append('\n');
			
			// Add whitespace for new updates
			for (int i = 0; i < indent; i++) {
				sb.append(" ");
			}
		}
		if (a != null) {
			a.prettyPrint(sb);
		} else {
			// If there is no action that needs to be printed, remove whitespace and newline
			sb = sb.delete(sb.length()-indent-1, sb.length());
		}
		
		return sb.append(";");
	}
	
	@Override
	public int size() {
		int s = 0;
		s += c.size();
		for (Update up : updates) {
			s += up.size();
		}
		if (a != null)
			s += a.size();
		return s + SIZE;
	}

	@Override
	public Node nodeAt(int index) {
		
		Node n = super.nodeAt(index); 
		int size = 0;
		if (n == null) {
			size = c.size();
			
			if (size >= index) {
				
				n = c.nodeAt(index - 1);
			}
			else {
				index -= size; 
			}
		}
		if (n == null) {
			if (updates != null) {
				int i = 0;
				Node node = null;
				while (n == null && i < updates.size()) {
					
					node = updates.get(i);
					size = node.size();
					if (size >= index) {
						n = node.nodeAt(index - 1);
					}
					index -= size; 
					i++;
				}
			}
		}
		if (n == null) {
			
			n = a.nodeAt(index - 1); 
		}
		

		return n;
	}
	@Override
	public boolean accept(Mutation m) {
		return m.mutate(this);
	}
	@Override
	public Rule clone() {
		Rule r = new Rule(c.clone());
		for (Update up : updates)
			r.addUpdate(up.clone());
		if (actionExists())
			r.setAction(a.clone());
		return r;
	}
}
