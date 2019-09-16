package ast;

/**
 * An abstraction of a critter program.
 */
public interface Program extends Node {
   /**
    * Mutates this program with a single mutation
    * 
    * @return The root of the mutated AST
    */
   Program mutate();

   /**
    * Mutates {@code nodeAt(index)} (and not its children) with mutation
    * {@code m}.
    * 
    * @param index
    *           The index of the node to mutate
    * @param m
    *           The mutation to perform on the node
    * @return The mutated program, or {@code null} if {@code m} represents an
    *         invalid mutation for the node at {@code index}
    * @throws IndexOutOfBoundsException
    *            if {@code index} is not valid
    */
   Program mutate(int index, Mutation m);
   
   /**
    * Creates a hard copy of this node and all its children
    * @return the copy of this node and its children
    */
   Program clone();
   
   /**
    * 
    * @param index - location of the rule in the Critter program list
    * @return a rule within this Critter program
    */
   Rule get(int index);
   
   /**
    * 
    * @return the number of rules hled by this critter program
    */
   int numberOfRules();
   /**
    * Add a new rule node to the program
    * @param ruleNode rule to be added to the program
    */
   void add(Rule ruleNode);

   /**
    * Get the index of the rule
    * @param rule The rule to get the index of
    * @return The index of the rule in the rule list
    */
   int getIndexOf(Rule rule);
   
   
}
