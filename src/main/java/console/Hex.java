package console;

/**
 * A class representing a single hex tile in the world. The coordinates are read
 * as (col, row), and the methods/constructors of the class reflect this.
 */
public class Hex implements Comparable<Hex>{
	/*
	 * The row of the Hex tile
	 */
	private int row;
	/**
	 * The column of the Hex tile
	 */
	private int column;
	/**
	 * The number of food units on a tile
	 */
	private int food;
	/**
	 * True or false depending on if that tile has a rock on it or not
	 */
	private boolean rock;
	/**
	 * The critter that is on this hex tile, null if there is none
	 */
	private Critter critter;
	/**
	 * The last version number that this hex was updated at
	 */
	private int lastUpdated;
	/**
	 * Variable used for search when the critter smells
	 */
	private int distance;
	/**
	 * Variable used to keep track of direction of critter smell
	 */
	private int direction;
	/**
	 * The hex of shortest path 
	 */
	private Hex prevHex;
	/**
	 * Reference to the world the hex is in
	 */
	private World w;
	
	/**
	 * Constructor for Hex tile at {@code col} and {@code row}
	 * @param row
	 * @param column
	 */
	public Hex(int column, int row, World w) {
		this.w = w;
		this.row = row;
		this.column = column;
		lastUpdated = 0;
		food = 0;
		rock = false;
		critter = null;
		distance = Integer.MAX_VALUE;
		direction = 0; 
		prevHex = null;
	}
	
	/**
	 * Set the amount of food on the tile equal to {@code f} and update the hex
	 * @param f - amt food to be set on tile
	 */
	public void setFood(int f) {
		food = f;
		update();
	}
	
	/**
	 * Get the amount of food left on the hex. Returns 0 if there is no food.
	 * @return - amt of food currently on the tile
	 */
	public int getFood() {
		return food;
	}
	
	/**
	 * Add {@code f} amount of food to tile and update the hex
	 * @param f - amt of food to be added to tile
	 */
	public void addFood(int f) {
		if (!isEmpty())
			return;
		food += f;
		update();
	}
	
	/**
	 * Set this tile to hold a rock and update the hex.
	 */
	public void setRock() {
		if (!isEmpty())
			return;
		rock = true;
		update();
	}
	
	/**
	 * Remove the rock from this tile and update the hex.
	 * Works even if there is no rock on tile.
	 */
	public void removeRock() {
		rock = false;
		update();
	}
	
	/**
	 * Checks if {@code this} hex tile is empty
	 * @return true or false, depending on whether or not this hex tile is empty
	 */
	public boolean isEmpty() {
		return !hasRock() && !hasFood() && !hasCritter();
	}
	
	/**
	 * Checks if this hex tile holds a rock on it
	 * @return true or false, depending on whether or not this hex tile holds a rock
	 */
	public boolean hasRock() {
		return rock;
	}
	
	/**
	 * Checks if {@code this} has food on it
	 * @return true if it does, false if it does not
	 */
	public boolean hasFood() {
		return food > 0; 
	}
	
	/**
	 * Checks if {@code this} has a critter on it
	 * @return true if it does, false if it does not
	 */
	public boolean hasCritter() {
		return critter != null; 
	}
	
	/**
	 * Set the critter associated with this tile to {@code c} and update the tile
	 * @param c - the critter that is now on top of this tile
	 */
	public void setCritter(Critter c) {
		if (!isEmpty())
			return;
		critter = c;
		update();
	}
	
	/**
	 * Get the critter that is currently on the hex tile.
	 * @return - the critter on this tile right now
	 */
	public Critter getCritter() {
		return critter;
	}
	/**
	 * Get the last time this hex was updated
	 * @return The last version number this hex was updated.
	 */
	public int getLastUpdated() {
		return lastUpdated;
	}
	
	/**
	 * 
	 * @return the row of this tile in the world
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * 
	 * @return the column of this tile in the world
	 */
	public int getColumn() {
		return column;
	}
	/**
	 * Sets {@code critter} to null to indicate that there is no critter stored on this hex.
	 * Updates the hex.
	 */
	public void removeCritter() {
		critter = null;
		update();
	}
	/**
	 * 
	 */
	public void update() {
		lastUpdated = w.getVersion() + 1;
	}
	/**
	 * Returns what {@code this} hex contains
	 * @return -1 for rock, less than -1 for food, 0 for nothing, and the critter's appearance if there is a critter
	 */
	public int hexContains() {
		if (hasCritter()) {
			return critter.getAppearance();
		}
		else if (hasRock()) {
			return -1;
		}
		else if (hasFood()) {
			return -1 * getFood() - 1;
		}
		else {
			return 0;
		}
	}
	/**
	 * Sets the distance of this hex
	 * @param dist distance to set this hex to relative to origin hex
	 */
	public void setDistance(int dist) {
		this.distance = dist;
	}
	/**
	 * 
	 * @return the distance of this hex relative to an origin hex
	 */
	public int getDistance() {
		return distance;
	}
	/**
	 * 
	 * @return the pseudo direction for smell
	 */
	public int getDirection() {
		return direction;
	}
	/**
	 * Sets the direction of this hex
	 * @param direction that the critter would be facing traveling on this tile
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * Sets the hex before it parameter
	 * @param hex the shortest path hex before this one
	 */
	public void setPrev(Hex hex) {
		this.prevHex = hex;
	}
	/**
	 * 
	 * @return the shortest path hex before this one
	 */
	public Hex getPrev() {
		return prevHex;
	}
	@Override
	public int compareTo(Hex hex) {
		if (this.distance > hex.distance) {
			return 1;
		}
		else if(this.distance == hex.distance) {
			return 0;
		}
		else {
			return -1;
		}
	}
	
	public Hex getHexInDirection(int distance, int direction) {
		if (direction >= 6) {
			direction -= 6;
		}
		Hex hex = null;
		switch(direction) {
		case 0:
			hex = w.getHex(column, row + distance); 
			break; 
		case 1: 
			hex = w.getHex(column + distance, row + distance); 
			break;
		case 2:
			hex = w.getHex(column + distance, row); 
			break;
		case 3:
			hex = w.getHex(column, row - distance);
			break;
		case 4:
			hex = w.getHex(column - distance, row - distance);
			break;
		case 5:
			hex = w.getHex(column - distance, row); 
			break;
		}
		return hex;
	}
}
