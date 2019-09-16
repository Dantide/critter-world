package interpret;

import java.util.Random;

import ast.Action;
import ast.BinaryCondition;
import ast.NumExpr;
import ast.Program;
import ast.RelationCondition;
import ast.Rule;
import ast.SensorExpr;
import ast.TermExpr;
import ast.UnaryExpr;
import ast.Update;
import ast.FactorExpr;
import ast.MemExpr;
import ast.MemExpr.MemSugar;
import console.Critter;

public class InterpreterImpl implements Interpreter{
	private Critter critter;
	
	public InterpreterImpl(Critter critter) {
		this.critter = critter;
	}
	
	@Override
	public boolean interpretProgram(Program p) {
		Rule r = null;
		for (int i = 0; i < p.numberOfRules(); i++) {
			r = p.get(i);
			if (r.getCondition().accept(this)) {
				if (r.numberOfUpdates() > 0) {
					for (int x = 0; x < r.numberOfUpdates(); x++) {
						r.getUpdate(x).accept(this);
					}
				}
				if (r.actionExists()) {
					r.getAction().accept(this);
					critter.setLastRule(r);
					return true; 
				}
				
			}
		}
		return false;
	}
	@Override
	public boolean eval(RelationCondition c) {
		switch(c.getOperator()) {
		case EQ:
			return c.getExprOne().accept(this) == c.getExprTwo().accept(this); 
		case NE:
			return c.getExprOne().accept(this) != c.getExprTwo().accept(this);
		case LT:
			return c.getExprOne().accept(this) < c.getExprTwo().accept(this);
		case GT:
			return c.getExprOne().accept(this) > c.getExprTwo().accept(this);
		case GE:
			return c.getExprOne().accept(this) >= c.getExprTwo().accept(this);
		case LE:
			return c.getExprOne().accept(this) <= c.getExprTwo().accept(this); 
		default:
			throw new IllegalArgumentException();
		}
	}
	@Override
	public boolean eval(BinaryCondition c) {
		switch(c.getOperator() ) {
		case AND:
			return c.getConditionOne().accept(this) && c.getConditionTwo().accept(this);
		case OR:
			return c.getConditionOne().accept(this) || c.getConditionTwo().accept(this);
		default:
			throw new IllegalArgumentException(); 
		}
		
	}
	@Override
	public int eval(NumExpr n) {
		return n.getValue(); 
	}
	@Override
	public int eval(TermExpr t) {
		switch(t.getOperator()) {
		case PLUS:
			return t.getExprOne().accept(this) + t.getExprTwo().accept(this); 
		case MINUS:
			return t.getExprOne().accept(this) - t.getExprTwo().accept(this); 
		default:
			throw new IllegalArgumentException(); 
		}
	}
	@Override
	public int eval(FactorExpr t) {
		int y = 0;
		switch(t.getOperator()) {
		case MOD:
			y = t.getExprTwo().accept(this);
			if (y == 0) {
				return 0;
			}
			return t.getExprOne().accept(this) % y;
		case MUL:
			return t.getExprOne().accept(this) * t.getExprTwo().accept(this);
		case DIV:
			y = t.getExprTwo().accept(this);
			if (y == 0) {
				return 0;
			}
			return t.getExprOne().accept(this) / y; 
		default:
			throw new IllegalArgumentException(); 
		}
	}
	@Override
	public int eval(SensorExpr t) {
		switch(t.getSensorType()) {
		case SMELL:
			return critter.smell();
		case RANDOM:
			Random rand = new Random();
			int upperBound = t.getSensorExpr().accept(this); 
			if (upperBound < 2) {
				return 0; 
			}
			return rand.nextInt(upperBound); 
		case AHEAD:
			return critter.ahead(t.getSensorExpr().accept(this)); 
		case NEARBY:
			return critter.nearby(t.getSensorExpr().accept(this)); 
		default:
			throw new IllegalArgumentException(); 
		}
	}
	@Override
	public int eval(MemExpr t) {
		if (t.getMemSugar() != null) {
			switch(t.getMemSugar()) {
			case TAG:
				return critter.getMemoryValue(MemSugar.TAG.getValue());
			case SIZE:
				return critter.getMemoryValue(MemSugar.SIZE.getValue());
			case POSTURE:
				return critter.getMemoryValue(MemSugar.POSTURE.getValue());
			case PASS:
				return critter.getMemoryValue(MemSugar.PASS.getValue());
			case OFFENSE:
				return critter.getMemoryValue(MemSugar.OFFENSE.getValue());
			case MEMSIZE:
				return critter.getMemoryValue(MemSugar.MEMSIZE.getValue());
			case ENERGY:
				return critter.getMemoryValue(MemSugar.ENERGY.getValue());
			case DEFENSE:
				return critter.getMemoryValue(MemSugar.DEFENSE.getValue());
			default:
				throw new IllegalArgumentException(); 
			}
		}
		else {
			return critter.getMemoryValue(t.getExpr().accept(this));
		}

	}


	@Override
	public void eval(Action a) {
		int value = 0;
		switch(a.getActionType()) {
		case WAIT:
			critter.critterWait();;
			break;
		case TAG:
			value = a.getExpr().accept(this);
			critter.tag(value);
			break;
		case SERVE:
			value = a.getExpr().accept(this); 
			critter.serve(value);
			break;
		case RIGHT:
			critter.right();
			break;
		case MATE:
			critter.mate();
			break;
		case LEFT:
			critter.left();
			break;
		case GROW:
			critter.grow();
			break; 
		case FORWARD:
			critter.forward();
			break;
		case EAT:
			critter.eat();
			break;
		case BUD:
			critter.bud();
			break;
		case BACKWARD: 
			critter.backwards();
			break; 
		case ATTACK: 
			critter.attack();
			break;
		default:
			throw new IllegalArgumentException(); 
		}
	}

	@Override
	public void eval(Update u) {
		critter.setMemoryValue(updateHelper((MemExpr) u.getExprOne()), u.getExprTwo().accept(this));
	}

	/**
	 * Gets the actual memory location within the critter memory
	 * @param t the mem expression representng the location within the critter memory
	 * @return critter memory location
	 */
	private int updateHelper(MemExpr t) {
		if (t.getMemSugar() != null) {
			switch(t.getMemSugar()) {
			case TAG:
				return MemSugar.TAG.getValue();
			case SIZE:
				return MemSugar.SIZE.getValue();
			case POSTURE:
				return MemSugar.POSTURE.getValue();
			case PASS:
				return MemSugar.PASS.getValue();
			case OFFENSE:
				return MemSugar.OFFENSE.getValue();
			case MEMSIZE:
				return MemSugar.MEMSIZE.getValue();
			case ENERGY:
				return MemSugar.ENERGY.getValue();
			case DEFENSE:
				return MemSugar.DEFENSE.getValue();
			default:
				throw new IllegalArgumentException();
			}
		}
		else {
			return t.getExpr().accept(this);
		}

	}
	@Override
	public int eval(UnaryExpr t) {
		return -(t.getExpr().accept(this));
	}


}
