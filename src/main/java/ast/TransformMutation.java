package ast;

import java.util.Random;

import ast.TermExpr.Operator;

/**
 * 
 * The node is replaced with a randomly chosen node of the same kind (for example, replacing
 * attack with eat, or + with *), but its children remain the same.
 * The node cannot be replaced if it has no children
 */
public class TransformMutation implements Mutation {

	
	@Override
	public boolean equals(Mutation m) {
		return m instanceof TransformMutation;
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
		if (n.type == null) {
			return false;
		}
		Random rand = new Random(); 
		int i = rand.nextInt(8);
		switch(i) {
		case 0:
			n.type = MemExpr.MemSugar.DEFENSE;
			break;
		case 1:
			n.type = MemExpr.MemSugar.ENERGY;
			break;
		case 2:
			n.type = MemExpr.MemSugar.MEMSIZE;
			break;
		case 3:
			n.type = MemExpr.MemSugar.OFFENSE;
			break;
		case 4:
			n.type = MemExpr.MemSugar.PASS;
			break;
		case 5:
			n.type = MemExpr.MemSugar.POSTURE;
			break;
		case 6:
			n.type = MemExpr.MemSugar.SIZE;
			break;
		case 7:
			n.type = MemExpr.MemSugar.TAG;
			break;
		
		}
		return true;
		
	}

	@Override
	public boolean mutate(FactorExpr n) {
		Random rand = new Random();
		int i = rand.nextInt(5);
		Expr node = null;
		switch (i){
		case 0:
			node = new TermExpr(n.x, Operator.PLUS, n.y);
			
			break;
		case 1:
			node = new TermExpr(n.x, Operator.MINUS, n.y);
			
			break;
		case 2:
			node = new FactorExpr(n.x, FactorExpr.Operator.DIV, n.y); 
			
			break;
		case 3:
			node = new FactorExpr(n.x, FactorExpr.Operator.MOD, n.y);
			
			break;
		case 4:
			node = new FactorExpr(n.x, FactorExpr.Operator.MUL, n.y);
			break;

		}
		node.setParent(n.parent);
		Node x = n.parent;
		if (x instanceof RelationCondition) {
			if (n.equals(((RelationCondition) x).x)) {
				((RelationCondition) x).x = node; 
			}
			else {
				((RelationCondition) x).y = node; 
			}
		}
		else if (x instanceof TermExpr ) {
			if (n.equals(((TermExpr) x).x)){
				((TermExpr) x).x = node;
				
			}
			else {
				((TermExpr) x).y = node;
			}
			
		}
		else if (x instanceof FactorExpr) {
			
			if (n.equals(((FactorExpr) x).x)){
				((FactorExpr) x).x = node;
			}
			else {
				((FactorExpr) x).y = node;
			}
		}
		else if (x instanceof MemExpr) {
			((MemExpr) x).x = node;
		}
		else if (x instanceof SensorExpr) {
			((SensorExpr) x).x = node;
		}
		else if (x instanceof UnaryExpr) {
			((UnaryExpr) x).x = node; 
		}
		return true;
		
		
	}

	@Override
	public boolean mutate(NumExpr n) {
		Random rand = new Random();
		int value = Integer.MAX_VALUE/rand.nextInt();
		int choice = rand.nextInt(2); 
		switch(choice) {
		case 0:
			n.value += value;
			break;
		case 1:
			n.value -= value; 
			break; 
		}
		return true;
		
	}

	@Override
	public boolean mutate(BinaryCondition n) {
		Random rand = new Random();
		
		int choice = rand.nextInt(2);
		switch(choice) {
		case 0:
			n.op = BinaryCondition.Operator.AND;
			break;
		case 1:
			n.op = BinaryCondition.Operator.OR; 
			break;
		}
		return true;
	}

	@Override
	public boolean mutate(RelationCondition n) {
		Random rand = new Random();
		int choice = rand.nextInt(6);
		switch(choice) {
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
			break;
		}
		return true;
		
		
	}

	@Override
	public boolean mutate(SensorExpr n) {
		if (n.x == null) {
			return false;
		}
		Random rand = new Random();
		SensorExpr replace = null;
		int choice = 0;
		choice = rand.nextInt(3);
		switch(choice) {
		case 0:
			replace = new SensorExpr(n.x, SensorExpr.SensorType.AHEAD);
			break;
		case 1:
			replace = new SensorExpr(n.x, SensorExpr.SensorType.RANDOM);
			break;
		case 2:
			replace = new SensorExpr(n.x, SensorExpr.SensorType.NEARBY);
			break;
		}
		replace.setParent(n.parent);
		Node x = n.parent;
		if (x instanceof RelationCondition) {
			if (n.equals(((RelationCondition) x).x)) {
				((RelationCondition) x).x = replace; 
			}
			else {
				((RelationCondition) x).y = replace; 
			}
		}
		else if (x instanceof TermExpr ) {
			if (n.equals(((TermExpr) x).x)){
				((TermExpr) x).x = replace;
				
			}
			else {
				((TermExpr) x).y = replace;
			}
			
		}
		else if (x instanceof FactorExpr) {
			
			if (n.equals(((FactorExpr) x).x)){
				((FactorExpr) x).x = replace;
			}
			else {
				((FactorExpr) x).y = replace;
			}
		}
		else if (x instanceof MemExpr) {
			((MemExpr) x).x = replace;
		}
		else if (x instanceof SensorExpr) {
			((SensorExpr) x).x = replace;
		}
		else if (x instanceof UnaryExpr) {
			((UnaryExpr) x).x = replace; 
		}
		return true;

	}

	@Override
	public boolean mutate(ActionImpl n) {
		Random rand = new Random(); 
		ActionImpl replace = null;
		int choice = 0;
		
		if (n.e == null) {
			choice = rand.nextInt(8);
			switch(choice) {
			case 0:
				replace = new ActionImpl(ActionImpl.Actions.ATTACK);
				break;
			case 1:
				replace = new ActionImpl(ActionImpl.Actions.BACKWARD);
				break;
			case 2:
				replace = new ActionImpl(ActionImpl.Actions.EAT);
				break;
			case 3:
				replace = new ActionImpl(ActionImpl.Actions.GROW);
				break;
			case 4:
				replace = new ActionImpl(ActionImpl.Actions.FORWARD);
				break;
			case 5:
				replace = new ActionImpl(ActionImpl.Actions.WAIT);
				break;
			case 6:
				replace = new ActionImpl(ActionImpl.Actions.LEFT); 
				break;
			case 7:
				replace = new ActionImpl(ActionImpl.Actions.RIGHT);
				break; 
			}
		}
		else {
			choice = rand.nextInt(2);
			switch(choice) {
			case 0:
				replace = new ActionImpl(ActionImpl.Actions.SERVE, n.e);
				break;
			case 1:
				replace = new ActionImpl(ActionImpl.Actions.TAG, n.e); 
				break; 
			}
		}
		replace.setParent(n.parent);
		((Rule)n.parent).setAction(replace);
		return true;
		
		
	}

	@Override
	public boolean mutate(TermExpr n) {	
		Random rand = new Random();
		int i = rand.nextInt(5);
		Expr node = null;
		switch (i){
		case 0:
			node = new TermExpr(n.x, Operator.PLUS, n.y);
			
			break;
		case 1:
			node = new TermExpr(n.x, Operator.MINUS, n.y);
			
			break;
		case 2:
			node = new FactorExpr(n.x, FactorExpr.Operator.DIV, n.y); 
			
			break;
		case 3:
			node = new FactorExpr(n.x, FactorExpr.Operator.MOD, n.y);
			
			break;
		case 4:
			node = new FactorExpr(n.x, FactorExpr.Operator.MUL, n.y);
			break;

		}
		node.setParent(n.parent);
		Node x = n.parent;
		
		if (x instanceof RelationCondition) {
			if (n.equals(((RelationCondition) x).x)) {
				((RelationCondition) x).x = node; 
			}
			else {
				((RelationCondition) x).y = node; 
			}
		}
		else if (x instanceof TermExpr ) {
			if (n.equals(((TermExpr) x).x)){
				((TermExpr) x).x = node;
				
			}
			else {
				((TermExpr) x).y = node;
			}
			
		}
		else if (x instanceof FactorExpr) {
			
			if (n.equals(((FactorExpr) x).x)){
				((FactorExpr) x).x = node;
			}
			else {
				((FactorExpr) x).y = node;
			}
		}
		else if (x instanceof MemExpr) {
			((MemExpr) x).x = node;
		}
		else if (x instanceof SensorExpr) {
			((SensorExpr) x).x = node;
		}
		else if (x instanceof UnaryExpr) {
			((UnaryExpr) x).x = node; 
		}
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
