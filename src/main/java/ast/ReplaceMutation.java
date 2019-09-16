package ast;
import java.util.Random;

import ast.TermExpr.Operator;

/**
 * 
 * The node and its children are replaced with randomly selected subtree of the right kind. Randomly
 * selected subtrees are either chosen from somewhere in the current rule set or else randomly
 * generated. 
 *
 */
public class ReplaceMutation implements Mutation {

	@Override
	public boolean equals(Mutation m) {
		return m instanceof ReplaceMutation;
	}

	@Override
	public boolean mutate(ProgramImpl n) {
		return false;
	}

	@Override
	public boolean mutate(Rule n) {
		Random rand = new Random(); 
		ProgramImpl p = (ProgramImpl) n.parent; 
		int i = rand.nextInt(p.ruleNodes.size());
		Rule copy = p.ruleNodes.get(i).clone();
		int index = p.ruleNodes.indexOf(n);
		p.ruleNodes.set(index, copy);
		return true;	
	}

	@Override
	public boolean mutate(MemExpr n) {

		Random rand = new Random(); 
		int i = rand.nextInt(9);
		switch(i) {
		case 0:
			n.type = MemExpr.MemSugar.DEFENSE;
			n.x = null;
			break;
		case 1:
			n.type = MemExpr.MemSugar.ENERGY;
			n.x = null;
			break;
		case 2:
			n.type = MemExpr.MemSugar.MEMSIZE;
			n.x = null;
			break;
		case 3:
			n.type = MemExpr.MemSugar.OFFENSE;
			n.x = null;
			break;
		case 4:
			n.type = MemExpr.MemSugar.PASS;
			n.x = null;
			break;
		case 5:
			n.type = MemExpr.MemSugar.POSTURE;
			n.x = null;
			break;
		case 6:
			n.type = MemExpr.MemSugar.SIZE;
			n.x = null;
			break;
		case 7:
			n.type = MemExpr.MemSugar.TAG;
			n.x = null;
			break;
		case 8:
			n.type = null;
			if (n.x == null) {
				n.x = new NumExpr(Integer.MAX_VALUE/rand.nextInt());
				n.x.setParent(n);
			}
			else {
				n.x.accept(this);
			}
		}
		return true;
	}

	@Override
	public boolean mutate(FactorExpr n) {
		Random rand = new Random();
		int i = rand.nextInt(3);
		switch(i) {
		case 0:
			n.op = FactorExpr.Operator.DIV;
			break;
		case 1:
			n.op = FactorExpr.Operator.MUL;
			break;
		case 2:
			n.op = FactorExpr.Operator.MOD;
			break;
		}
		n.x.accept(this);
		n.y.accept(this);
		return true;
		
		
	}

	@Override
	public boolean mutate(NumExpr n) {
		Random rand = new Random(); 
		int value = Integer.MAX_VALUE/rand.nextInt(); 
		n.value = value; 
		return true;		
	}

	@Override
	public boolean mutate(BinaryCondition n) {
		Random rand = new Random();
		int i = rand.nextInt(2);
		switch(i) {
		case 0:
			n.op = BinaryCondition.Operator.AND;
			break;
		case 1:
			n.op = BinaryCondition.Operator.OR;
		}
		n.x.accept(this);
		n.y.accept(this);
		
		return true;
	}

	@Override
	public boolean mutate(RelationCondition n) {
		Random rand = new Random();
		int i = rand.nextInt(6);
		switch(i) {
		case 0:
			n.rel = RelationCondition.Operator.EQ;
			break;
		case 1:
			n.rel = RelationCondition.Operator.GE;
			break;
		case 2:
			n.rel = RelationCondition.Operator.GT;
			break;
		case 3:
			n.rel = RelationCondition.Operator.LE;
			break;
		case 4:
			n.rel = RelationCondition.Operator.LT;
			break; 
		case 5:
			n.rel = RelationCondition.Operator.NE;
		}
		n.x.accept(this);
		n.y.accept(this); 
		return true;
		
	}

	@Override
	public boolean mutate(SensorExpr n) {
		Random rand = new Random();
		int i = rand.nextInt(4);
		switch (i) {
		case 0:
			n.type = SensorExpr.SensorType.SMELL;
			n.x = null;
			break;
		case 1:
			n.type = SensorExpr.SensorType.NEARBY;
			if (n.x == null) {
				n.x = new NumExpr(Integer.MAX_VALUE/rand.nextInt());
				n.x.setParent(n);
			}
			else {
				n.x.accept(this);
			}
			break;
		case 2:
			n.type = SensorExpr.SensorType.RANDOM;
			if (n.x == null) {
				n.x = new NumExpr(Integer.MAX_VALUE/rand.nextInt());
				n.x.setParent(n);
			}
			else {
				n.x.accept(this);
			}
			break;
		case 3:
			n.type = SensorExpr.SensorType.AHEAD;
			if (n.x == null) {
				n.x = new NumExpr(Integer.MAX_VALUE/rand.nextInt());
				n.x.setParent(n);
			}
			else {
				n.x.accept(this);
			}
			break;
		}
		return true;
		
		
	}

	@Override
	public boolean mutate(ActionImpl n) {
		Random rand = new Random();
		int i = rand.nextInt(12);
		int value = 0; 
		switch (i) {
		case 0:
			n.action = ActionImpl.Actions.ATTACK;
			n.e = null;
			break;
		case 1:
			n.action = ActionImpl.Actions.BACKWARD;
			n.e = null;
			break;
		case 2:
			n.action = ActionImpl.Actions.BUD;
			n.e = null;
			break;
		case 3:
			n.action = ActionImpl.Actions.EAT;
			n.e = null;
			break;
		case 4:
			n.action = ActionImpl.Actions.FORWARD;
			n.e = null;
			break;
		case 5:
			n.action = ActionImpl.Actions.GROW;
			n.e = null;
			break;
		case 6:
			n.action = ActionImpl.Actions.LEFT;
			n.e = null;
			break;
		case 7: 
			n.action = ActionImpl.Actions.MATE;
			n.e = null;
			break; 
		case 8:
			n.action = ActionImpl.Actions.RIGHT;
			n.e = null;
			break;
		case 9:
			n.action = ActionImpl.Actions.TAG;
			if (n.e == null) {
				n.e = new NumExpr(Integer.MAX_VALUE/rand.nextInt());
				n.e.setParent(n);
			}
			else {
				n.e.accept(this);
			}
			break;
		case 10:
			n.action = ActionImpl.Actions.SERVE;
			if (n.e == null) {
				n.e = new NumExpr(Integer.MAX_VALUE/rand.nextInt());
				n.e.setParent(n);
			}
			n.e.accept(this);
			break;
		case 11:
			n.action = ActionImpl.Actions.WAIT;
			n.e = null;
		}
		return true;
	}

	@Override
	public boolean mutate(TermExpr n) {
		Random rand = new Random();
		int i = rand.nextInt(2);
		switch(i) {
		case 0:
			n.rel = TermExpr.Operator.MINUS; 
			break;
		case 1:
			n.rel = TermExpr.Operator.PLUS;
		}
		n.x.accept(this);
		n.y.accept(this);
		return true;
		
	}

	@Override
	public boolean mutate(UpdateImpl n) {
		n.x.accept(this);
		n.y.accept(this);
		return true;
	}

	@Override
	public boolean mutate(UnaryExpr n) {
		n.x.accept(this);
		return true;
	}
}
