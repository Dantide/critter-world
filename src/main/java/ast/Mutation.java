package ast;

/**
 * A mutation to the AST
 */
public interface Mutation {
	/**
	 * Compares the type of this mutation to {@code m}
	 * 
	 * @param m
	 *           The mutation to compare with
	 * @return Whether this mutation is the same type as {@code m}
	 */
	boolean equals(Mutation m);

	boolean mutate(ProgramImpl n);

	boolean mutate(Rule n);

	boolean mutate(MemExpr n);

	boolean mutate(FactorExpr n);

	boolean mutate(NumExpr n);

	boolean mutate(BinaryCondition n);

	boolean mutate(RelationCondition n);

	boolean mutate(SensorExpr n);

	boolean mutate(ActionImpl n);

	boolean mutate(TermExpr n);
	
	boolean mutate(UpdateImpl n);
	
	boolean mutate(UnaryExpr n);
}
