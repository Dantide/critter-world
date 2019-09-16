package ast;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import console.Critter; 
/**
 * A data structure representing a critter program.
 *
 */
public class ProgramImpl extends AbstractNode implements Program {
	
	/**
	 * List of Rule nodes connected to the Program
	 */
	protected List<Rule> ruleNodes; 
	/*
	 * Constructor. 
	 * Instantiates the List of Rule nodes
	 * Sets the size of the AST under this node to 0
	 * 
	 */
	public ProgramImpl() {
		ruleNodes = new ArrayList<Rule>();
		parent = null; 
	}
	
	/**
	 * Add a child Rule node to this Program
	 */
	@Override
	public void add(Rule ruleNode) {
		ruleNode.setParent(this);
		ruleNodes.add(ruleNode);
	}
	
	/**
	 * Get the index of the rule within the list of rules
	 */
	public int getIndexOf(Rule rule) {
		for (int i = 0; i < ruleNodes.size(); i++) {
			Rule r = ruleNodes.get(i);
			if (rule == r) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Return a pointer to the node at index
	 */
	public Rule get(int index) {
		if (index < 0 || index >= ruleNodes.size())
			return null;
		return ruleNodes.get(index);
	}
	/**
	 * Return number of rules in this program
	 * 
	 */
	public int numberOfRules() {
		return ruleNodes.size();
	}
	/**
	 * Delete a child Rule node from this Program
	 */
	public void delete(Rule ruleNode) {
		ruleNodes.remove(ruleNode); 
	}
	
	/**
	 * The number of rules held in this program
	 */
	public int numRules() {
	
		return ruleNodes.size();
	}
	
	@Override
	public Program mutate() {
		Program p = this.clone();
		Random rand = new Random(); 
		boolean done = false;
		int i = rand.nextInt(p.size());
		
		while (!done) {
			int x = rand.nextInt(6);
			Node n = p.nodeAt(i); 
			Mutation m = null;
			switch(x) {
			case 0:
				m = MutationFactory.getDuplicate();
				break;
			case 1:
				m = MutationFactory.getInsert();
				break;
			case 2:
				m = MutationFactory.getRemove();
				break;
			case 3:
				m = MutationFactory.getReplace();
				break;
			case 4:
				m = MutationFactory.getSwap();
				break;
			case 5:
				m = MutationFactory.getTransform(); 
				break; 
			}
			
			if (n.accept(m)) {
				done = true;
			}
			else if (p.numberOfRules() == 0) {
				done = true; 
			}
		}
		return p;
	}

	@Override
	public Program mutate(int index, Mutation m) {
		
		Program clone = this.clone();
		Node n = null;
		try {
			n = clone.nodeAt(index);
			
		} catch(IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
		}
		
		if (n.accept(m)) {
			return clone; 
		}
		return null;
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if (sb == null) {
			throw new IllegalArgumentException();
		}
		for (Rule x : ruleNodes) {
			sb.append(x.prettyPrint(new StringBuilder()));
			sb.append('\n');
			sb.append('\n');
		}
		return sb;
	}
	
	@Override
	public int size() {
		int s = 0;
		for (Rule x : ruleNodes) {
			s += x.size();
		}
		return s + SIZE;
	}

	@Override
	public Node nodeAt(int index) {
		
		Node n = super.nodeAt(index); 
		int i = 0;
		int size = 0;
		
		Node node = null;
		
		while(n == null) {
			
			node = ruleNodes.get(i);
			size = node.size();
			if (size >= index) {
				n = node.nodeAt(index - 1);
			}
			else {
				index -= size; 
			}
			
			i++;
		}
		return n;
	}
	@Override
	public boolean accept(Mutation m) {
		return m.mutate(this);
	}
	
	@Override
	public Program clone() {
		ProgramImpl prog = new ProgramImpl();
		for (Rule r : ruleNodes)
			prog.add(r.clone());
		return prog;
	}
	
	@Override
	public void setParent(Node p) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Node getParent() {
		throw new UnsupportedOperationException();
	}
}
