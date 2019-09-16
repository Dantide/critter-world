package ast;

import interpret.Interpreter;

/*
 * A representation of a binary Boolean condition: 'and' or 'or'
 *
 */

public class BinaryCondition extends AbstractNode implements Condition {
	/*
	 * One condition being compared with the Operator op
	 */
	protected Condition x;

	/*
	 * Operator being used to compare Condition l and Condition r
	 */
	protected Operator op;

	/*
	 * Another condition being compared with the operator op
	 */
	protected Condition y; 

   /**
    * Create an AST representation of l op r.
    * 
    * @param l - Condition being compared to {@code r}
    * @param op - Operator comparing {@code l} and {@code r}
    * @param r - Condition being compared to {@code l}
    */
   public BinaryCondition(Condition l, Operator op, Condition r) {
	   this.x = l;
	   this.y = r;
	   this.op = op; 
	   this.x.setParent(this);
	   this.y.setParent(this);
   }
   
   /**
    * 
    * @return left condition being compared by {@code op} to {@code y}
    */
   public Condition getConditionOne() {
	   return x; 
   }
   /**
    * @return right condition being compared by {@code op} to {@code x}
    */
   public Condition getConditionTwo() {
	   return y;
   }
   /**
    * 
    * @return the operator represented by this binarycondition node
    */
   public Operator getOperator() {
	   return op;
   }
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if (sb == null) {
			throw new IllegalArgumentException();
		}
		if (x.getClass() == BinaryCondition.class)
			sb.append("{");
		x.prettyPrint(sb);
		if (x.getClass() == BinaryCondition.class)
			sb.append("}");
		
		sb.append(" " + op.toString().toLowerCase() + " ");
		
		if (y.getClass() == BinaryCondition.class)
			sb.append("{");
		y.prettyPrint(sb);
		if (y.getClass() == BinaryCondition.class)
			sb.append("}");
		
		return sb;
	}

	
	@Override
	public int size() {
		return x.size() + y.size() + SIZE;
	}

	@Override
	public Node nodeAt(int index) {
		Node n = super.nodeAt(index);
		int size = 0;
		if (n == null) {
			size = x.size();
			
			if (size >= index) {
				n = x.nodeAt(index - 1);
			}
			else {
				index -= size;
			}
		}
		if (n == null) {
			n = y.nodeAt(index - 1); 
		}
		return n; 
	}
	
	@Override
	public BinaryCondition clone() {
		return new BinaryCondition(x.clone(), op, y.clone());
	}
	
	/**
	 * An enumeration of all possible binary condition operators.
	 */
	public enum Operator {
		OR, AND;
	}


	@Override
	public boolean accept(Mutation m) {
		return m.mutate(this);
	}

	@Override
	public boolean accept(Interpreter i) {
		return i.eval(this);
	}
}
