package ast;

import java.util.Random;

public class DuplicateMutation implements Mutation{

	@Override
	public boolean equals(Mutation m) {
		return m instanceof DuplicateMutation;
		
	}

	@Override
	public boolean mutate(ProgramImpl n) {
		if (n.numRules() == 0)
			return false;
		Random r = new Random();
		r.nextInt(n.numRules());
		Rule rule = n.get(r.nextInt(n.numRules()));
		n.add(rule.clone());
		return true;
	}

	@Override
	public boolean mutate(Rule n) {
		if (n.numberOfUpdates() == 0)
			return false;
		Random r = new Random();
		r.nextInt(n.numberOfUpdates());
		Update up = n.getUpdate(r.nextInt(n.numberOfUpdates()));
		n.addUpdate(up.clone());
		return true;
	}

	@Override
	public boolean mutate(MemExpr n) {
		return false;
	}

	@Override
	public boolean mutate(FactorExpr n) {
		return false;
	}

	@Override
	public boolean mutate(NumExpr n) {
		return false;
	}

	@Override
	public boolean mutate(BinaryCondition n) {
		return false;
	}

	@Override
	public boolean mutate(RelationCondition n) {
		return false;
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
		return false;
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
