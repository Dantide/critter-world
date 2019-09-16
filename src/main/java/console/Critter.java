package console;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import ast.Program;
import ast.ProgramImpl;
import ast.Rule;
import console.DummyClassLibrary.DeadCritter;
import interpret.Interpreter;
import interpret.InterpreterImpl;

/**
 * The class representing a critter in the simulation.
 */
public class Critter {
	/**
	 * Critter identification 
	 */
	private String speciesName;
	/**
	 * The memory of the critter, representing several different attributes of the critter
	 */
	private int mem[];
	/**
	 * The way the critter is facing in the world
	 */
	private int direction;
	/**
	 * The last rule that was executed by the critter
	 */
	private Rule lastrule;
	/**
	 * The critter program of the critter
	 */
	private Program critterProgram; 
	/**
	 * Value which represents how complex a critter is based on the equation 
	 */
	private int complexity;
	/**
	 * The col coordinate of {@code this}
	 */
	private int col;
	/**
	 * The row coordinate of {@code this}
	 */
	private int row;
	/**
	 * The world {@code this} inhabits
	 */
	private World w;
	/**
	 * Indicates if {@code this} critter is ready to mate
	 */
	private boolean readyToMate;
	/**
	 * The Id number of the critter
	 */
	private int numId;
	/**
	 * The sessionId that created this Critter
	 */
	private int sessionId;
	/**
	 * Constructor for a critter object.
	 * 
	 * @param critterProgram the AST of the critter program
	 * @param mem an array representing the memory of a critter
	 * @param speciesName the reference name of the critter
	 * @param direction the direction the new critter is facing 
	 * @param xCoordinate x coordinate of the new critter
	 * @param yCoordinate y coordinate of the new critter
	 */
	public Critter(Program critterProgram, int mem[], String speciesName, 
			int direction, int col, int row, World w, Rule rule) {
		this.lastrule = rule; 
		this.w = w; 
		this.col = col; 
		this.row = row; 
		this.critterProgram = critterProgram;
		this.mem = mem; 
		this.direction = direction;
		this.speciesName = speciesName;
		this.sessionId = -1;
		complexity = critterProgram.numberOfRules() * Constants.RULE_COST() + 
				(mem[1] + mem[2]) * Constants.ABILITY_COST();
		readyToMate = false; 
	}
	/**
	 * Returns information about the closest food to the critter based on a ten tile radius around it
	 * @return value based on the distance and direction of the closest food to the critter
	 */
	public int smell() {
		/**
		 * Use djikstra's algorithm to find shortest path to all valid hexes 
		 * in a 10 hex radius
		 * Set starting hex distance to 0
		 * Add starting hex to the undiscovered hexes
		 * 
		 */
		Set<Hex> blackHexes = new HashSet<Hex>();
		PriorityQueue<Hex> whiteHexes = new PriorityQueue<Hex>();
		boolean found = false;
		Hex root = w.getHex(col, row);
		root.setDistance(0);
		root.setDirection(direction);
		whiteHexes.add(root);
		while(whiteHexes.size() != 0) {
			Hex currentHex = whiteHexes.remove();
			if (currentHex.hasFood()) {
				int relativeDirection = getRelativeDistance(currentHex, root);
				w.resetTiles();
				return (currentHex.getDistance() - 1) * 1000 + relativeDirection;
			}
			else if ((currentHex.getDistance() - 1) > Constants.MAX_SMELL_DISTANCE()) {
				w.resetTiles();
				return 1000000;
			}
			Hex edgeHex = null;
			int turnDistance = 0;
			int newDistance = 0;
			for (int i = 0; i < 6; i++) {
				edgeHex = currentHex.getHexInDirection(1, i);
				turnDistance = Math.abs((i - currentHex.getDirection()) % 6);
				if (turnDistance > 6 / 2) {
					turnDistance = 6 - turnDistance; 
				}
				newDistance = currentHex.getDistance() + 1 + turnDistance; 
				if (edgeHex != null && !edgeHex.hasRock() && !edgeHex.hasCritter() && !blackHexes.contains(edgeHex)) {
					if (edgeHex.getDistance() == Integer.MAX_VALUE) {
						edgeHex.setPrev(currentHex);
						edgeHex.setDirection(i);
						edgeHex.setDistance(newDistance);
						whiteHexes.add(edgeHex);
					}
					else {
						if (edgeHex.getDistance() > newDistance) {
							edgeHex.setPrev(currentHex);
							edgeHex.setDirection(i);
							edgeHex.setDistance(newDistance);
							whiteHexes.remove(edgeHex);
							whiteHexes.add(edgeHex); 
						}
					}
				}
			}
			blackHexes.add(currentHex);
		}
		return 1000000; 
	}
	
	private int getRelativeDistance(Hex startingPoint, Hex goal) {
		boolean foundRelativeDistance = false;
		int relativeDirection = 0;
		while(!foundRelativeDistance) {
			if (startingPoint.getPrev().equals(goal)) {
				relativeDirection = startingPoint.getDirection();
				foundRelativeDistance = true;
			}
			else {
				startingPoint = startingPoint.getPrev();
			}
		}
		return relativeDirection; 
	}
	/**
	 * Generate a random number with a given upper bound
	 * @param upperBound the max number 
	 * @return a random number up to the {@code upperBound}
	 */
	private int getRandomNumber(int upperBound) {
		Random random = new Random();
		return random.nextInt(upperBound);
	}
	/**
	 * 
	 * @param distance the amount to look ahead by
	 * @return the hex in the direction the critter is directly facing 
	 */
	private Hex getHexInDirection(int distance, int direction) {
		if (direction >= 6) {
			direction -= 6;
		}
		Hex hex = null;
		switch(direction) {
		case 0:
			hex = w.getHex(col, row + distance); 
			break; 
		case 1: 
			hex = w.getHex(col + distance, row + distance); 
			break;
		case 2:
			hex = w.getHex(col + distance, row); 
			break;
		case 3:
			hex = w.getHex(col, row - distance);
			break;
		case 4:
			hex = w.getHex(col - distance, row - distance);
			break;
		case 5:
			hex = w.getHex(col - distance, row); 
			break;
		}
		return hex;
	}
	/**
	 * 
	 * @param tiles number of tiles ahead to sense
	 * @return a value representing what is on the tile a number {@code tiles} ahead
	 */
	public int ahead(int tiles) {
		Hex hex = getHexInDirection(tiles, this.direction);
		if (hex != null) {
			return hex.hexContains();
		}
		else {
			return -1; 
		}
	}
	/**
	 * 
	 * @param direction value that represents which direction the critter should check
	 * @return value representing what is on a tile next to the critter
	 */
	public int nearby(int direction) {
		if (direction > 5 || direction < 0 ) {
			direction = direction % 6; 
		}
		Hex hex = getHexInDirection(1, direction);
		if (hex != null) {
			return getHexInDirection(1, direction).hexContains(); 
		}
		else {
			return -1; 
		}
	}
	/**
	 * Executes the wait action for the critter
	 */
	public void critterWait() {
		if ((mem[4] + mem[3] * Constants.SOLAR_FLUX()) > (Constants.ENERGY_PER_SIZE() * mem[3])) {
			mem[4] = Constants.ENERGY_PER_SIZE() * mem[3]; 
		}
		else {
			mem[4] += mem[3] * Constants.SOLAR_FLUX(); 
		}
	}
	
	/**
	 * Critter attacks the opposing critter if there is one
	 * The amount of damage is calculated by:
	 * round(BASE DAMAGE · S 1 · P(DAMAGE INC · (S 1 · O1 − S 2 · D2)))
	 */
	public void attack() {
		mem[4] -= mem[3] * Constants.ATTACK_COST(); 
		if(!checkForDeath()) {
			Hex hex = getHexInDirection(1, direction);
			if (hex != null) {
				if (hex.hasCritter()) {
					Critter enemy = hex.getCritter(); 
					double P = 1 / (1 + Math.exp(-(Constants.DAMAGE_INC() * (mem[3] * mem[2] - enemy.mem[3] * enemy.mem[1])))); 
					enemy.mem[4] -= Constants.BASE_DAMAGE() * mem[3] * P;
					enemy.checkForDeath();
				}
			}
		}
	}
	/**
	 * Move in direction opposite the direction critter is facing
	 */
	public void backwards() {
		mem[4] -= mem[3] * Constants.MOVE_COST();
		if(!checkForDeath()) {
			Hex hex = this.getHexInDirection(1, direction + 3); 
			if (hex != null) {
				if (hex.isEmpty()) {
					hex.setCritter(this);
					w.getHex(col, row).removeCritter();
					col = hex.getColumn();
					row = hex.getRow();
				}
			}
		}
	}
	/**
	 * The critter turns right
	 */
	public void right() {
		mem[4] -= mem[3]; 
		if(!checkForDeath()) {
			direction = (direction + 1) % 6;
			w.getHex(col, row).update();
		}
	}
	/**
	 * The critter turns left
	 */
	public void left() {
		mem[4] -= mem[3]; 
		if(!checkForDeath()) {
			direction = (direction - 1) % 6;
			w.getHex(col, row).update();
			while (direction < 0)
				direction += 6;
		}
	}
	/**
	 * The critter eats some food from the hex in front of it
	 */
	public void eat() {
		mem[4] -= mem[3]; 
		if(!checkForDeath()) {
			Hex hex = getHexInDirection(1, direction);
			if (hex != null) {
				if (hex.hasFood()) {
					int maxEnergy = Constants.ENERGY_PER_SIZE() * mem[3];
					int totalEnergy = mem[4] + hex.getFood();
					if(totalEnergy > maxEnergy) {
						hex.setFood(totalEnergy - maxEnergy);
						mem[4] = maxEnergy; 
					}
					else {
						mem[4] += hex.getFood();
						hex.setFood(0);
					}
				}
			}
		}
	}
	/**
	 * The critter undergoes binary fission and poops out a new critter
	 */
	public void bud() {
		if (mem[4] - Constants.BUD_COST() > 0) {
			mem[4] -= Constants.BUD_COST() * complexity; 
			if (!checkForDeath()) {
				Program p = critterProgram.clone();
				Hex hex = getHexInDirection(1, direction + 3);
				if (hex != null && hex.isEmpty()) {
					Critter child = new Critter(p, prepareChild(), speciesName, direction, hex.getColumn(), hex.getRow(), w, null);
					child.mutate();
					hex.setCritter(child);
					w.addCritter(child);
				}
			}
		}
	}
	/**
	 * Helper method to prepare the child of a mate or bud 
	 * @return a memory of a child critter
	 */
	private int[] prepareChild() {
		int[] childMem = mem.clone(); 
		childMem[4] = 250;
		childMem[3] = 1;
		childMem[7] = 0;
		childMem[6] = 0;
		for (int i = 7; i < childMem.length - 1; i++) {
			childMem[i] = 0; 
		}
		return childMem; 
	}
	/**
	 * Mates with an adjacent critter if possible
	 */
	public void mate() {
		int mateCost = Constants.MATE_COST() * complexity; 
		readyToMate = true; 
		Hex hex = getHexInDirection(1, direction);
		if (hex != null) {
			if (hex.hasCritter()) {
				Critter parent = hex.getCritter();
				if (parent.readyToMate && parent.direction == (this.direction + 3) % 6) {
					parent.mem[4] -= mateCost;
					this.mem[4] -= mateCost; 
					if (parent.checkForDeath()) {
						this.mem[4] += mateCost;
						this.mem[4] -= this.mem[3];
					}
					else if (this.checkForDeath()){
						parent.mem[4] += mateCost;
						parent.mem[4] -= parent.mem[3]; 
					}
					else if (!parent.checkForDeath() && !this.checkForDeath()) {
						int choice = getRandomNumber(2);
						int[] newMem = null;
						int newSize = 0; 
						String newSpeciesName = "";
						int newDirection = 0; 
						Hex childLocation = null;
						Program newProgram = new ProgramImpl(); 
						switch (choice) {
						case 0:
							newSpeciesName = parent.speciesName;
							newMem = parent.prepareChild();
							newSize = parent.critterProgram.numberOfRules();
							newDirection = parent.direction;
							childLocation = parent.getHexInDirection(1, parent.direction + 3);
							break;
						case 1:
							newSpeciesName = speciesName;
							newMem = prepareChild();
							newSize = critterProgram.numberOfRules();
							newDirection = direction;
							childLocation = getHexInDirection(1, parent.direction + 3);
							break;
						}
						if (childLocation.isEmpty()) {
							for (int i = 0; i < newSize; i++) {
								if (i > critterProgram.size()) {
									newProgram.add(parent.critterProgram.get(i));
								}
								else if(i > parent.critterProgram.size()){
									newProgram.add(critterProgram.get(i));
								}
								else {
									choice = getRandomNumber(2);
									switch(choice) {
									case 0:
										newProgram.add(parent.critterProgram.get(i));
										break;
									case 1:
										newProgram.add(critterProgram.get(i));
									}
								}
							}
								Critter child = new Critter(newProgram, newMem, newSpeciesName, newDirection, 
										childLocation.getColumn(), childLocation.getRow(), w, null);
								child.mutate();
								childLocation.setCritter(child);
								w.addCritter(child);
								readyToMate = false;
								parent.readyToMate = false;
							}
						}
						else {
							this.mem[4] += mateCost;
							this.mem[4] -= this.mem[3];
							parent.mem[4] += mateCost;
							parent.mem[4] -= parent.mem[3]; 
						}
					}
				}
			}
		}
	public void tag(int tagValue) {
		mem[4] -= mem[3]; 
		if (!checkForDeath()) {
			Hex hex = this.getHexInDirection(1, direction); 
			if (hex != null) {
				if (hex.hasCritter()) {
					hex.getCritter().setTag(tagValue);
				}
			}
		}
	}
	
	public void serve(int servingSize) {
		mem[4] -= mem[3] + servingSize; 
		if (!checkForDeath()) {
			Hex hex = this.getHexInDirection(1, direction);
			if (hex != null) {
				if (hex.hasFood() || hex.isEmpty()) {
					hex.addFood(servingSize);
				}
			}
		}
	}
	public void forward() {
		mem[4] -= mem[3] * Constants.MOVE_COST();
		if (!checkForDeath()) {
			Hex hex = getHexInDirection(1, direction); 
			if (hex != null) {
				if (hex.isEmpty()) {
					hex.setCritter(this);
					w.getHex(col, row).removeCritter();; 
					col = hex.getColumn();
					row = hex.getRow(); 
				}
			}
		}
	}
	/**
	 * Critter increases its size by one 
	 */
	public void grow() {
		mem[4] -= mem[3] * complexity * Constants.GROW_COST();
		if(!checkForDeath()) {
			mem[3] += 1;
		}
	}
	/**
	 * Critter will run its program and execute an action
	 */
	public void nextTurn() {
		Interpreter interp = new InterpreterImpl(this);
		while (turnNotDone() && !interp.interpretProgram(critterProgram)) {
			
			if(mem[5] == 999){
				critterWait(); 
			}
			pass(); 
		}
		mem[5] = 0;
	}
	/**
	 * 
	 * @param memLocation The memory location to retrieve data from
	 * @return The data stored at the memory location {@code memLocation}
	 */
	public int getMemoryValue(int memLocation) {
		if (memLocation > mem.length || memLocation < 0) {
			return 0;
		}
		return mem[memLocation]; 
	}
	/**
	 * 
	 * @param memLocation the location in memory to be updated 
	 * @param value the value to update the {@code memLocation} by
	 */
	public void setMemoryValue(int memLocation, int value) {
		System.out.println("MemLocation: " + memLocation);
		System.out.println("Value: " + value);
		if (memLocation > 6 && memLocation < mem.length) {
			
			mem[memLocation] = value;
		}

	}
	/**
	 * 
	 * @return true if the critter has passed through its memory less than 1000 times, false otherwise
	 */
	public boolean turnNotDone() {
		return mem[5] < 1000;
	}
	/**
	 * add one to the PASS attribute in the critter's memory
	 */
	public void pass() {
		mem[5] += 1;
	}
	/**
	 * Return the name of the critter species
	 */
	public String getSpecies() {
		return speciesName;
	}
	/**
	 * Return the pretty print of the critter program
	 */
	public String getAst() {
		return critterProgram.prettyPrint(new StringBuilder()).toString();
	}
	/**
	 * Returns a hard copy of the memory values held in the critter
	 */
	public int[] getMem() {
		return mem.clone();
	}
	/**
	 * Sets the last rule run by this critter
	 */
	public void setLastRule(Rule r) {
		this.lastrule = r; 
	}
	/**
	 * Returns the last rule executed by the critter
	 */
	public Rule getLastRule() {
		return lastrule;
	}
	/**
	 * 
	 * @return the appearance of the Critter
	 */
	public int getAppearance() {
		return mem[3] * 100000 + mem[6] * 1000 + mem[7] * 10 + direction;
	}
	/**
	 * Return the direction the critter is looking in
	 */
	public int getDirection() {
		return direction;
	}
	/**
	 * Sets the tag characteristic to a given value
	 * @param value amount to set this critter's tag
	 */
	public void setTag(int value) {
		mem[6] = value; 
		System.out.println(mem[6]);
	}
	/**
	 * Checks if the critter's energy has fallen beneath 0, if it has, the critter dies
	 */
	public boolean checkForDeath() {
		if (mem[4] <= 0) {
			w.getHex(col, row).removeCritter();
			w.getHex(col, row).addFood(mem[3] * Constants.FOOD_PER_SIZE());
			w.getFood().add(w.getHex(col, row));
			w.getCritters().set(w.getCritters().indexOf(this), null);
			w.getDeadCritters().add(new DeadCritter(w.getVersion(), this.numId));
			return true; 
		}
		return false; 
	}
	/**
	 * Mutates {@code this} critter
	 * Uses random numbers to select which type of mutation to use
	 * The two possibilities for mutations to a critter are alterations to its memory or alterations to its program
	 */
	public void mutate() {
		Random rand = new Random(); 
		while(rand.nextInt(4) == 3) {
		
			int mutationType = this.getRandomNumber(2);
			switch(mutationType) {
			case 0:
				critterProgram = critterProgram.mutate(); 
				System.out.println("Critter mutation occurred");
				break;
			case 1:
				int action = this.getRandomNumber(2); 
				int property = this.getRandomNumber(3);
				if (action == 0) {
					if (property == 0) {
						if (mem[0] > 8) {
							mem[0]--;
						}
					}
					else if (property == 1) {
						if (mem[1] > 1) {
							mem[1]--;
						}
					}
					else {
						if (mem[2] > 1) {
							mem[2]--;
						}
					}
				}
				else {
					if (property == 0) {
						if(mem[0] > 8) {
							mem[0]++;
						}
					}
					else if(property == 1) {
						if (mem[1] > 1) {
							mem[1]++;
						}
						
					}
					else {
						if (mem[2] > 1) {
							mem[2]++;
						}
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @return The row the critter is standing on
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * 
	 * @return The row the critter is standing on
	 */
	public int getCol() {
		return col;
	}
	
	/**
	 * Set the unique id of this critter
	 * @param id The id number to be assigned to this critter
	 */
	public void setId(int id) {
		this.numId = id;
	}
	
	/**
	 * Get the unique id of this critter
	 * @return The id number of this critter
	 */
	public int getId() {
		return numId;
	}
	
	/**
	 * Set the session id of the user who created this critter
	 * @param sessionId The session id of the user
	 */
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	
	/**
	 * Get the session id of the user who created this critter
	 * @return The session id of the user associated with this critter
	 */
	public int getSessionId() {
		return sessionId;
	}
	
	/**
	 * 
	 * @return The critter program of this critter
	 */
	public Program getProgram() {
		return critterProgram;
	}
}
