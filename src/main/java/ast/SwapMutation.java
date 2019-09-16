package ast;

import java.util.Collections;
import java.util.Random;

/**
 * 
 * 	The order of two children of the node is switched. For example, this allows swapping the
 *	positions of two rules, or changing a − b to b − a
 *
 */
public class SwapMutation implements Mutation {

	@Override
	public boolean equals(Mutation m) {
		return m instanceof SwapMutation;
	}


	@Override
	public boolean mutate(ProgramImpl n) {
		Random rand = new Random();
		System.out.println(n.ruleNodes.size());
		if (n.ruleNodes.size() <= 0) {
			return false;
		}
		
		Collections.swap(n.ruleNodes, rand.nextInt(n.ruleNodes.size()), rand.nextInt(n.ruleNodes.size()));
		return true;
	}

	@Override
	public boolean mutate(Rule n) {
		return false;
	}

	@Override
	public boolean mutate(MemExpr n) {
		return false;

	}

	@Override
	public boolean mutate(FactorExpr n) {
		Node node = n.x;
		n.x = n.y; 
		n.y = (Expr)node;
		return true; 
	}

	@Override
	public boolean mutate(NumExpr n) {
		return false;

	}

	@Override
	public boolean mutate(BinaryCondition n) {
		Node node = n.x;
		n.x = n.y;
		n.y = (Condition) node;
		return true;
	}

	@Override
	public boolean mutate(RelationCondition n) {
		Node node = n.x;
		n.x = n.y; 
		n.y = (Expr)node;
		return true;
	}

	@Override
	public boolean mutate(SensorExpr n) {
		return false;
	}

	@Override
	public boolean mutate(ActionImpl n) {
		return false;
	}

	@Override
	public boolean mutate(TermExpr n) {
		Node node = n.x;
		n.x = n.y; 
		n.y = (Expr)node;
		return true;
	}

	@Override
	public boolean mutate(UpdateImpl n) {
		return false;
	}


	@Override
	public boolean mutate(UnaryExpr n) {
		return false;
	}

}
