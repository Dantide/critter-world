package ast;

/**
 * 
 * 	The node, along with its descendants, is removed. If the parent of the node being removed
 *	needs a replacement child, one of the node’s children of the correct kind is randomly selected.
 *
 */
public class RemoveMutation implements Mutation {

	@Override
	public boolean equals(Mutation m) {
		return m instanceof RemoveMutation;
	}

	@Override
	public boolean mutate(ProgramImpl n) {
		return false;
	}

	@Override
	public boolean mutate(Rule n) {
		ProgramImpl p = (ProgramImpl) n.parent; 
		p.delete(n);
		return true;
	}

	@Override
	public boolean mutate(MemExpr n) {
		Node x = n.parent;
		boolean mutateSuccess = false;
		if (n.x != null) {
			if (x instanceof RelationCondition) {

				
				if (n.equals(((RelationCondition) x).x)) {
					((RelationCondition) x).x = n.x; 
				}
				else {
					((RelationCondition) x).y = n.x; 
				}
				mutateSuccess = true;
			}
			else if (x instanceof TermExpr ) {
				
				if (n.equals(((TermExpr) x).x)){
					((TermExpr) x).x = n.x;

				}
				else {
					((TermExpr) x).y = n.x;
				}
				mutateSuccess = true;
			}
			else if (x instanceof FactorExpr) {
				
				if (n.equals(((FactorExpr) x).x)){
					((FactorExpr) x).x = n.x;
				}
				else {
					((FactorExpr) x).y = n.x;
				}
				mutateSuccess = true;
			}
			else if (x instanceof MemExpr) {
				
				((MemExpr) x).x = n.x;
				mutateSuccess = true; 
			}
			else if (x instanceof SensorExpr) {
				((SensorExpr)x).x = n.x; 
				mutateSuccess = true; 
			}
			else if (x instanceof UnaryExpr) {
				((UnaryExpr)x).x = n.x;
				mutateSuccess = true; 
			}
		}
		return mutateSuccess;
		
		
	}

	@Override
	public boolean mutate(FactorExpr n) {
		Node x = n.parent;
		boolean mutateSuccess = false;
		if (x instanceof RelationCondition) {
			
			x = (RelationCondition)n.parent;
			if (n.equals(((RelationCondition) x).x)) {
				((RelationCondition) x).x = n.x; 
			}
			else {
				((RelationCondition) x).y = n.x; 
			}
			mutateSuccess = true;
		}
		else if (x instanceof TermExpr ) {
			x = (TermExpr)n.parent;
			if (n.equals(((TermExpr) x).x)){
				((TermExpr) x).x = n.x;
				
			}
			else {
				((TermExpr) x).y = n.x;
			}
			mutateSuccess = true;
		}
		else if (x instanceof FactorExpr) {
			
			if (n.equals(((FactorExpr) x).x)){
				((FactorExpr) x).x = n.x;
			}
			else {
				((FactorExpr) x).y = n.x;
			}
			mutateSuccess = true;
		}
		else if (x instanceof MemExpr) {
			
			((MemExpr) x).x = n.x;
			mutateSuccess = true; 
		}
		else if (x instanceof SensorExpr) {
			((SensorExpr)x).x = n.x; 
			mutateSuccess = true; 
		}
		else if (x instanceof UnaryExpr) {
			((UnaryExpr)x).x = n.x;
			mutateSuccess = true; 
		}
			
		return mutateSuccess;
		
		
	}

	@Override
	public boolean mutate(NumExpr n) {
		return false;
		
	}

	@Override
	public boolean mutate(BinaryCondition n) {
		Node x = n.parent;
		boolean mutateSuccess = false;
		if (x instanceof BinaryCondition) {
			if (n.equals(((BinaryCondition) x).x)){
				((BinaryCondition) x).x = n.x;
			}
			else {
				((BinaryCondition) x).y = n.x;
			}
			mutateSuccess = true; 
		}
		return mutateSuccess;
		
		
	}

	@Override
	public boolean mutate(RelationCondition n) {
		return false;
	}

	@Override
	public boolean mutate(SensorExpr n) {
		Node x = n.parent;
		boolean mutateSuccess = false;
		if (n.x != null) {
			if (x instanceof RelationCondition) {

				
				if (n.equals(((RelationCondition) x).x)) {
					((RelationCondition) x).x = n.x; 
				}
				else {
					((RelationCondition) x).y = n.x; 
				}
				mutateSuccess = true;
			}
			else if (x instanceof TermExpr ) {
				
				if (n.equals(((TermExpr) x).x)){
					((TermExpr) x).x = n.x;

				}
				else {
					((TermExpr) x).y = n.x;
				}
				mutateSuccess = true;
			}
			else if (x instanceof FactorExpr) {
				
				if (n.equals(((FactorExpr) x).x)){
					((FactorExpr) x).x = n.x;
				}
				else {
					((FactorExpr) x).y = n.x;
				}
				mutateSuccess = true;
			}
			else if (x instanceof MemExpr) {
				
				((MemExpr) x).x = n.x;
				mutateSuccess = true; 
			}
			else if (x instanceof SensorExpr) {
				((SensorExpr)x).x = n.x; 
				mutateSuccess = true; 
			}
			else if (x instanceof UnaryExpr) {
				((UnaryExpr)x).x = n.x;
				mutateSuccess = true; 
			}
		}
			
		return mutateSuccess;
		
		
	}

	@Override
	public boolean mutate(ActionImpl n) {
		Rule r = (Rule) n.parent;
		if (r.numberOfUpdates() > 0) {
			r.removeAction();
			return true; 
		}
		return false;
	}

	@Override
	public boolean mutate(TermExpr n) {
		Node x = n.parent;
		boolean mutateSuccess = false;
		if (x instanceof RelationCondition) {
			
			
			if (n.equals(((RelationCondition) x).x)) {
				((RelationCondition) x).x = n.x; 
			}
			else {
				((RelationCondition) x).y = n.x; 
			}
			mutateSuccess = true;
		}
		else if (x instanceof TermExpr ) {
		
			if (n.equals(((TermExpr) x).x)){
				((TermExpr) x).x = n.x;
				
			}
			else {
				((TermExpr) x).y = n.x;
			}
			mutateSuccess = true;
		}
		else if (x instanceof FactorExpr) {
			
			if (n.equals(((FactorExpr) x).x)){
				((FactorExpr) x).x = n.x;
			}
			else {
				((FactorExpr) x).y = n.x;
			}
			mutateSuccess = true;
		}
		else if (x instanceof MemExpr) {
			((MemExpr) x).x = n.x;
			mutateSuccess = true; 
		}
		else if (x instanceof SensorExpr) {
			((SensorExpr)x).x = n.x; 
			mutateSuccess = true; 
		}
		else if (x instanceof UnaryExpr) {
			((UnaryExpr)x).x = n.x;
			mutateSuccess = true; 
		}
		return mutateSuccess;
		
		
	}

	@Override
	public boolean mutate(UpdateImpl n) {
		Rule r = (Rule) n.parent;
		if (r.numberOfUpdates() > 1 || r.actionExists()) {
			r.removeUpdate(n);
		}
		else {
			return false;
		}
		return true;
		
	}

	@Override
	public boolean mutate(UnaryExpr n) {
		Node x = n.parent;
		boolean mutateSuccess = false; 
		if (x instanceof RelationCondition) {
			
			x = (RelationCondition)n.parent;
			if (n.equals(((RelationCondition) x).x)) {
				((RelationCondition) x).x = n.x; 
			}
			else {
				((RelationCondition) x).y = n.x; 
			}
			mutateSuccess = true;
		}
		else if (x instanceof TermExpr ) {
			x = (TermExpr)n.parent;
			if (n.equals(((TermExpr) x).x)){
				((TermExpr) x).x = n.x;
				
			}
			else {
				((TermExpr) x).y = n.x;
			}
			mutateSuccess = true;
		}
		else if (x instanceof FactorExpr) {
			
			if (n.equals(((FactorExpr) x).x)){
				((FactorExpr) x).x = n.x;
			}
			else {
				((FactorExpr) x).y = n.x;
			}
			mutateSuccess = true;
		}
		else if (x instanceof MemExpr) {
			
			((MemExpr) x).x = n.x;
			mutateSuccess = true; 
		}
		else if (x instanceof SensorExpr) {
			((SensorExpr)x).x = n.x; 
			mutateSuccess = true; 
		}
		else if (x instanceof UnaryExpr) {
			((UnaryExpr)x).x = n.x;
			mutateSuccess = true; 
		}
		return mutateSuccess;
	}

}
