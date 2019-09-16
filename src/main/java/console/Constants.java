package console;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Scanner;	

public class Constants {
	private Scanner scan;
	
	/*
	 * Table that holds all the values of the constants with their name as the key
	 */
	private static Hashtable<String, Integer> table;
	/*
	 * Controls how quickly increased offensive or defensive ability affects damage
	 */
	private static double DAMAGE_INC;
	
	
	/*
	 * Here in case we need to switch from static to singleton pattern
	 * 
	private static Constants instance;
	
	public static Constants getInstance() {
		if (instance == null)
			instance = new Constants(Constants.class.getResourceAsStream("constants.txt"));
		return instance;
	}*/
	
	static {
		new Constants(Constants.class.getResourceAsStream("constants.txt"));
	}
	
	private Constants(InputStream in) {
		scan = new Scanner(in);
		table = new Hashtable<String, Integer>();
		
		while (scan.hasNext()) {
			String s = scan.next();
			if (s.equals("DAMAGE_INC"))
				DAMAGE_INC = scan.nextDouble();
			else {
				int i = scan.nextInt();
				table.put(s, i);
			}
			scan.nextLine();
		}
	}
	
	/**
	 * The multiplier for all damage done by attacking
	 */
	public static int BASE_DAMAGE() {
		return table.get("BASE_DAMAGE");
	}
	
	/**
	 * Controls how quickly increased offensive or defensive ability affects damage
	 */
	public static double DAMAGE_INC() {
		return DAMAGE_INC;
	}
	
	/**
	 * How much energy a critter can have per point of size
	 */
	public static int ENERGY_PER_SIZE() {
		return table.get("ENERGY_PER_SIZE");
	}
	
	/**
	 * How much food is created per point of size when a critter dies
	 */
	public static int FOOD_PER_SIZE() {
		return table.get("FOOD_PER_SIZE");
	}
	
	/**
	 * Maximum distance at which food can be sensed
	 */
	public static int MAX_SMELL_DISTANCE() {
		return table.get("MAX_SMELL_DISTANCE");
	}
	
	/**
	 * The value reported when a rock is sensed
	 */
	public static int ROCK_VALUE() {
		return table.get("ROCK_VALUE");
	}
	
	/**
	 * Default number of columns in the world map
	 */
	public static int COLUMNS() {
		return table.get("COLUMNS");
	}
	
	/**
	 * Default number of rows in the world map
	 */
	public static int ROWS() {
		return table.get("ROWS");
	}
	
	/**
	 * The maximum number of rules that can be run per critter turn
	 */
	public static int MAX_RULES_PER_TURN() {
		return table.get("MAX_RULES_PER_TURN");
	}
	
	/**
	 * Energy gained from sun by doing nothing
	 */
	public static int SOLAR_FLUX() {
		return table.get("SOLAR_FLUX");
	}
	
	/**
	 * Energy cost of moving (per unit size)
	 */
	public static int MOVE_COST() {
		return table.get("MOVE_COST");
	}
	
	/**
	 * Energy cost of attacking (per unit size)
	 */
	public static int ATTACK_COST() {
		return table.get("ATTACK_COST");
	}
	
	/**
	 * Energy cost of growing (per size and complexity)
	 */
	public static int GROW_COST() {
		return table.get("GROW_COST");
	}
	
	/**
	 * Energy cost of budding (per unit complexity)
	 */
	public static int BUD_COST() {
		return table.get("BUD_COST");
	}
	
	/**
	 * Energy cost of successful mating (per unit complexity)
	 */
	public static int MATE_COST() {
		return table.get("MATE_COST");
	}
	
	/**
	 * Complexity cost of having a rule
	 */
	public static int RULE_COST() {
		return table.get("RULE_COST");
	}
	
	/**
	 * Complexity cost of having an ability point
	 */
	public static int ABILITY_COST() {
		return table.get("ABILITY_COST");
	}
	
	/**
	 * Energy of a newly birthed critter
	 */
	public static int INITIAL_ENERGY() {
		return table.get("INITIAL_ENERGY");
	}
	
	/**
	 * Minimum number of memory entries in a critter
	 */
	public static int MIN_MEMORY() {
		return table.get("MIN_MEMORY");
	}
}
