package ast;

import java.util.Random;

import ast.SensorExpr.SensorType;

/**
 * 
 * A newly created node is inserted as the parent of the mutated node. The old parent of the
 * mutated node becomes the parent of the inserted node, and the mutated node becomes a child
 * of the inserted node. 
 *
 */
public class InsertMutation implements Mutation{
	
	/**
	 * Goes backwards in the Ast to the ProgramImpl node and chooses a random expression
	 * from the whole rule tree (that is not the original node).
	 * 
	 * @param n - the original node
	 * @return - a copy of the random expression node chosen
	 */
	private Expr copyRandExpr(Node n) {
		
		Node parent = n;
		
		do{
			System.out.println("Node: " + parent.getParent());
			parent = parent.getParent();
		}	while (!(parent instanceof ProgramImpl));
		
		int i = parent.size();
		Random r = new Random();
		Node node;
		
		do {
			node = parent.nodeAt(r.nextInt(i));
		}
		while (!(node instanceof Expr) && (node != n));
		return (Expr) node.clone();
	}
	
	/**
	 * Goes backwards in the Ast to the ProgramImpl node and chooses a random condition
	 * from the whole rule tree (that is not the original node).
	 * 
	 * @param n - the original node
	 * @return - a copy of the random expression node chosen
	 */
	private Condition copyRandCondition(Condition n) {
		Node parent = n;
		do {
			parent = parent.getParent();
		}
		while (!(parent instanceof ProgramImpl));
		
		int i = parent.size();
		Random r = new Random();
		Node node;
		
		do {
			node = parent.nodeAt(r.nextInt(i));
		}
		while (!(node instanceof Condition) || (node == n));
		return (Condition) node.clone();
	}
	
	/**
	 * Create a random expression node with a random operator if nessesary
	 * If the node requires two expressions, a random expression will be picked from the rule tree.
	 * 
	 * @param n - the expression that will be added as a child of the new node
	 * @return - the new node
	 */
	private Expr chooseRandExpr(Expr n) {
		Random r = new Random();
		switch(r.nextInt(4)) {
		case 0:
			switch(r.nextInt(2)) {
			case 0:
				return new TermExpr(n, TermExpr.Operator.MINUS, copyRandExpr(n));
			case 1:
				return new TermExpr(n, TermExpr.Operator.PLUS, copyRandExpr(n));
			}
		case 1:
			switch(r.nextInt(3)) {
			case 0:
				return new FactorExpr(n, FactorExpr.Operator.MUL, copyRandExpr(n));
			case 1:
				return new FactorExpr(n, FactorExpr.Operator.DIV, copyRandExpr(n));
			case 2:
				return new FactorExpr(n, FactorExpr.Operator.MOD, copyRandExpr(n));
			}
		case 2:
			switch(r.nextInt(3)) {
			case 0:
				return new SensorExpr(n, SensorType.AHEAD);
			case 1:
				return new SensorExpr(n, SensorType.NEARBY);
			case 2:
				return new SensorExpr(n, SensorType.RANDOM);
			}
		case 3:
			return new MemExpr(n);
		}
		return null;
	}
	
	/**
	 * Create a random binary condition node with a random operator.
	 * If the node requires two condition, a random expression will be picked from the rule tree.
	 * 
	 * @param n - the expression that will be added as a child of the new node
	 * @return - the new node
	 */
	private Condition chooseRandCondition(Condition n) {
		Random r = new Random();
		switch(r.nextInt(2)) {
		case 0:
			return new BinaryCondition(n, BinaryCondition.Operator.AND, copyRandCondition(n));
		case 1:
			return new BinaryCondition(n, BinaryCondition.Operator.AND, copyRandCondition(n));
		}
		return null;
	}
	
	/**
	 * Mutates an expression by inserting a random expression between
	 * the expression and its parent
	 * 
	 * @param n - the expression node to e mutated
	 * @return - true or false if the mutation succeeds
	 */
	private boolean exprMut(Expr n) {
		Node x = n.getParent();
		Expr expr = chooseRandExpr(n);
		expr.setParent(x);
		
		if (x instanceof TermExpr) {
			if ( ((TermExpr) x).x == n) {
				((TermExpr) x).x = expr;
			}
			else {
				((TermExpr) x).y = expr;
			}
			return true;
		}
		else if (x instanceof FactorExpr) {
			if ( ((FactorExpr) x).x == n) {
				((FactorExpr) x).x = expr;
			}
			else {
				((FactorExpr) x).y = expr;
			}
			return true;
		}
		else if (x instanceof RelationCondition) {
			if ( ((RelationCondition) x).x == n) {
				((RelationCondition) x).x = expr;
			}
			else {
				((RelationCondition) x).y = expr;
			}
			return true;
		}
		else if (x instanceof SensorExpr) {
			((SensorExpr) x).x = expr;
			return true;
		}
		else if (x instanceof ActionImpl) {
			((ActionImpl) x).e = expr;
			return true;
		}
		return false;
	}
	/**
	 * Helper Method to mutate a condition
	 * @param n - Condition to be processed 
	 * @return - Whether or not the mutation was successful
	 */
	private boolean conditionMut(Condition n) {
		Node x = n.getParent();
		Condition c = chooseRandCondition(n);
		
		c.setParent(x);
		
		if (x instanceof Rule) {
			((Rule) x).setCondition(c);
		}
		else if (x instanceof BinaryCondition) {
			if ( ((BinaryCondition) x).x == n) {
				((BinaryCondition) x).x = c;
				return true;
			}
			else {
				((BinaryCondition) x).y = c;
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean equals(Mutation m) {
		return m instanceof InsertMutation;
	}

	@Override
	public boolean mutate(ProgramImpl n) {
		return false;
	}

	@Override
	public boolean mutate(Rule n) {
		return false;
	}

	@Override
	public boolean mutate(MemExpr n) {
		return exprMut(n);
	}

	@Override
	public boolean mutate(FactorExpr n) {
		return exprMut(n);
	}

	@Override
	public boolean mutate(NumExpr n) {
		return exprMut(n);
	}

	@Override
	public boolean mutate(BinaryCondition n) {
		return conditionMut(n);
	}

	@Override
	public boolean mutate(RelationCondition n) {
		return conditionMut(n);
	}

	@Override
	public boolean mutate(SensorExpr n) {
		return exprMut(n);
	}

	@Override
	public boolean mutate(ActionImpl n) {
		return false;
	}

	@Override
	public boolean mutate(TermExpr n) {
		return exprMut(n);
	}

	@Override
	public boolean mutate(UpdateImpl n) {
		return false;
	}

	@Override
	public boolean mutate(UnaryExpr n) {
		return exprMut(n);
	}

}
