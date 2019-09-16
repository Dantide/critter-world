package console;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import console.DummyClassLibrary.*;
import ast.Program;
import exceptions.SyntaxError;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import parse.Parser;
import parse.ParserFactory;

/**
 * The class that holds the hex tiles and references to the critters in the world
 */
public class World {
	/**
	 * Scanner for world.txt
	 */
	private Scanner scan;
    /*
     * HashTable of all the Hex's on the grid
     */
    private Hashtable<String, Hex> grid;
    /**
     * List of critters in the world
     */
    private ArrayList<Critter> critterList;
    /**
     * List of dead critters in the world
     */
    private ArrayList<DeadCritter> deadCritterList;
    /**
     * List of hex tiles in the world with food on them
     */
    private ArrayList<Hex> foodList;
    /**
     * List of hex tiles in the world with food on them
     */
    private ArrayList<Hex> rockList;
    /**
     * The number of steps that have been taken in the world since creation
     */
    private int numSteps;
    /**
     * The current version of the world. Increments when critters or entities added,
     * or when the world takes a step.
     */
    private int versionNumber;
 	/**
     * Number of rows
     */
    private int rows;
    /**
     * Number of columns
     */
    private int columns;
    /**
     * Number of hex tiles in one column
     */
    private int height;
    /**
     * Name of the world
     */
    private String name;
    /**
     * Path of the folder that world.txt is currently in
     */
    private String path;
    /**
     * 
     */
    private Timeline timer;
    /**
     * 
     */
    private static int crittersPlaced;
    
    /**
     * Constructor.
     * Used to create the client-side world
     */
    public World(int numSteps, int versionNumber, int row, int col, 
    		String name) {
    		grid = new Hashtable<String, Hex>();
    		critterList = new ArrayList<Critter>(); 
    		foodList = new ArrayList<Hex>();
    		rockList = new ArrayList<Hex>();
    		path = null;
    		this.rows = row;
    		this.columns = col;
    		height = rows - ((columns + 1) / 2); 
    		this.name = name;
    		this.numSteps = numSteps;
    		this.versionNumber = versionNumber;
    		createGrid();
    		
    }
    /**
     * Default constructor with rows and columns as defaults given by the Constants class
     */
    public World() {
    	this.scan = null;
    	grid = new Hashtable<String, Hex>();
    	critterList = new ArrayList<Critter>();
    	deadCritterList = new ArrayList<DeadCritter>();
    	rockList = new ArrayList<Hex>();
    	foodList = new ArrayList<Hex>();
    	numSteps = 0;
    	versionNumber = 0;
    	rows = Constants.ROWS();
    	columns = Constants.COLUMNS();
    	height = rows - ((columns + 1) / 2);
    	name = "Default World";
    	path = null;
    	createGrid();
    }

    /**
     * Constructor when given the PATH of a world initialization file.
     */
    public World(String filePath) {
    	//Remove quotes or dangling slashes from filename so that it can be read by FileInputStream
    	if (filePath.startsWith("\"") && filePath.endsWith("\""))
    		filePath = filePath.substring(1, filePath.length()-1);
    	if (filePath.endsWith("\\") || filePath.endsWith("/"))
    		filePath = filePath.substring(0, filePath.length()-1);
    	
    	//Search for the world file in the system directory
    	InputStream in;
    	try {
			in = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
    		System.out.println("Cannot find world file specified.");
    		return;
		}
    	
    	//Prepare the path string that will be used when looking for the critter files
    	int i = (filePath.lastIndexOf('\\') > filePath.lastIndexOf('/')) ? 
    			filePath.lastIndexOf('\\') : filePath.lastIndexOf('/');
    	
    	//Initialize the World and its variables
    	path = filePath.substring(0, i+1);
    	this.scan = new Scanner(in);
    	grid = new Hashtable<String, Hex>();
    	deadCritterList = new ArrayList<DeadCritter>();
    	critterList = new ArrayList<Critter>();
    	rockList = new ArrayList<Hex>();
    	foodList = new ArrayList<Hex>();
    	numSteps = 0;
    	versionNumber = 0;
    	initialize(scan);
    }
    
    public World(Description description) {
    	//Initialize the World and its variables
    	path = null;
    	String des = description.description();
		if (des.startsWith("[") && des.endsWith("]")) {
			des = des.substring(1, des.length()-1);
		}
    	this.scan = new Scanner(des);
    	grid = new Hashtable<String, Hex>();
    	deadCritterList = new ArrayList<DeadCritter>();
    	critterList = new ArrayList<Critter>();
    	rockList = new ArrayList<Hex>();
    	foodList = new ArrayList<Hex>();
    	numSteps = 0;
    	versionNumber = 0;
    	initialize(scan);
    }
    
    /**
     * Initialize the world from a world.txt file passed into a scanner
     */
    private void initialize(Scanner scan) {
    	if (scan == null || scan.hasNext() == false) {
    		System.out.println("This world program is either empty or does not exist.");
    		return;
    	}
    	
        createWorld(scan);
        
        while (scan.hasNext()) {
        	String s = next();
        	switch(s) {
        	case "rock":
        		addRock();
        		break;
        	case "food":
        		addFood();
        		break;
        	case "critter":
        		if (path != null) {
        			System.out.println(path);
        			addCritter();
        		}
        		else
        			scan.nextLine();
        		break;
        	}
        }
    }
    
    /**
     * Helper method that reads the name and size of the world and
     * creates a grid based off that size
     */
    private void createWorld(Scanner scan) {
    	//Read in name
        String s = next();
        if (!s.equals("name")) {
        	System.out.println("Invalid syntax at name in the World file. "
        			+ "Name will be set to default value of 'New World'.");
        	name = "New World";
        	scan.nextLine();
        }
        else
        	name = scan.nextLine().trim();
        
        //Read in size
        s = next();
        if (!s.equals("size")) {
        	System.out.printf("Invalid Syntax at size in the World file. Size will be set to "
        			+ "default values Columns: %d, Rows %d.", Constants.ROWS(), Constants.COLUMNS());
        	columns = Constants.COLUMNS();
        	rows = Constants.ROWS();
        	scan.nextLine();
        }
        else {
	        columns = scan.nextInt();
	        rows = scan.nextInt();
	        height = rows - ((columns + 1) / 2);
	        if (rows < (columns+1)/2) {
	        	System.out.println("Not enough rows for the amount of columns. Cannot inizialize world.");
	        	rows = -1;
	        	columns = -1;
	        	return;
	        }
        }
        createGrid();
    }
    
    /**
     * Create the grid with the hex tile objects placed in the hash table
     */
    private void createGrid() {
    	for (int i = 0; i < columns; i++) {
    		int strtrow = (i + 1) / 2; 
    		for (int j = strtrow; j < (height + strtrow); j++) {
    			String key = getKey(i, j);
    			grid.put(key, new Hex(i, j, this));
    		}
    	}
    }
    
    /*
     * Look past any comments that might be in the world file
     */
    private String next() {
    	String s = "";
    	if (scan.hasNext())
    		s = scan.next();
    	else
    		return null;
    	while (s.equals("//")) {
    		scan.nextLine();
    		if (scan.hasNextLine())
    			s = scan.next();
    		else
    			return null;
    	}
    	return s;
    }
    
    /**
     * Place a rock at {@code col} and {@code row}
     */
    private void addRock() {
    	int col = scan.nextInt();
    	int row = scan.nextInt();
    	String key = getKey(col, row);
    	Hex h = grid.get(key);
    	rockList.add(h);
    	h.setRock();
    	h.update();
    }
    
    /**
     * Place {@code amt} of food at {@code col} and {@code row}
     */
    private void addFood() {
    	int col = scan.nextInt();
    	int row = scan.nextInt();
    	int amt = scan.nextInt();
    	String key = getKey(col, row);
    	Hex h = grid.get(key);
    	foodList.add(h);
    	h.setFood(amt);
    	h.update();
    }
    
    /**
     * Place a critter at {@code col} and {@code row} looking in {@code dir}. Helper method for initialize()
     */
    private void addCritter() {
    	//Check to see if the critter file exists in the same folder as the world file
       	String filename = scan.next();
    	Scanner critScan;
		try {
			critScan = new Scanner(new FileInputStream(path + filename));
		} catch (FileNotFoundException e) {
			System.out.println("Could not find critter file specifed in the world file. "
					+ "The critter must be in the same folder as the world file.");
			return;
		}
    	
    	//Scan the world file for the coordinates to place the critter at
    	int col = scan.nextInt();
    	int row = scan.nextInt();
    	int dir = scan.nextInt();
    	if (getHex(col, row) == null || !getHex(col, row).isEmpty()) {
    		System.out.println("An invalid column or row was passed into critter placement. "
    				+ "The critter will now be randomly placed.");
    		do {
	    		col = new Random().nextInt(columns);
	    		int strtrow = (col + 1) / 2;
	        	row = new Random().nextInt(height) + strtrow;
    		} while (!getHex(col, row).isEmpty());
    	}
    	if (dir < 0 || dir >= 6) {
    		dir = ((dir % 6) + 6) % 6;
    	}
    	
    	critterHelper(critScan, row, col, dir);
	}
    
    
    /**
     * Helper method, reads the memory values from a critter file
     */
    private int[] readCritMem(Scanner critScan) {
    	
    	//Set memsize
    	int memsize;
    	if (!critScan.next().equals("memsize:") || !critScan.hasNextInt()) {
        	System.out.println("Invalid syntax at memsize in the critter file. "
        			+ "Memsize set to default value of " + Constants.MIN_MEMORY() + ".");
        	memsize = Constants.MIN_MEMORY();
        	critScan.nextLine();
        }
    	else {
        	memsize = critScan.nextInt();
	    	if (memsize < 8) {
	    		System.out.println("Critter memsize is too small and has been set to the "
	    				+ "default value of " + Constants.MIN_MEMORY() + ".");
	    		memsize = Constants.MIN_MEMORY();
	    	}
    	}
    	int[] mem = new int[memsize];
    	mem[0] = memsize;
    	
    	//Set Defense
    	if (!critScan.next().equals("defense:") || !critScan.hasNextInt()) {
    		System.out.println("Invalid syntax at defense in the critter file. "
        			+ "Defense set to default value of 1.");
    		mem[1] = 1;
    		critScan.nextLine();
        }
    	else {
    		mem[1] = critScan.nextInt();
    		if (mem[1] < 1) {
    			System.out.println("Invalid value at defense in the critter file. "
            			+ "Defense set to default value of 1.");
        		mem[1] = 1;
    		}
    	}
    	
    	//Set Offense
    	if (!critScan.next().equals("offense:") || !critScan.hasNextInt()) {
    		System.out.println("Invalid syntax at offense in the critter file. "
        			+ "Offense set to default value of 1.");
    		mem[2] = 1;
    		critScan.nextLine();
        }
    	else {
    		mem[2] = critScan.nextInt();
    		if (mem[2] < 1) {
    			System.out.println("Invalid value at offense in the critter file. "
            			+ "Offense set to default value of 1.");
        		mem[2] = 1;
    		}
    	}
    	
    	//Set Size
    	if (!critScan.next().equals("size:") || !critScan.hasNextInt()) {
    		System.out.println("Invalid syntax at size in the critter file. "
        			+ "Size set to default value of 1.");
    		mem[3] = 1;
    		critScan.nextLine();
        }
    	else {
    		mem[3] = critScan.nextInt();
    		if (mem[3] < 1) {
    			System.out.println("Invalid vlue at size in the critter file. "
            			+ "Size set to default value of 1.");
        		mem[3] = 1;
    		}
    	}
    	
    	//Set Energy
    	if (!critScan.next().equals("energy:") || !critScan.hasNextInt()) {
    		System.out.println("Invalid syntax at energy in the critter file. "
        			+ "Energy set to energy value of 1.");
    		mem[4] = 1;
    		critScan.nextLine();
        }
    	else {
    		mem[4] = critScan.nextInt();
    		if (mem[4] < 1) {
    			System.out.println("Invalid vlue at energy in the critter file. "
            			+ "Energy set to default value of 1.");
        		mem[4] = 1;
    		}
    	}
    	
    	//Set Posture
    	if (!critScan.next().equals("posture:") || !critScan.hasNextInt()) {
    		System.out.println("Invalid syntax at posture in the critter file. "
        			+ "Posture set to default value of 0.");
    		mem[7] = 0;
    		critScan.nextLine();
        }
    	else {
    		mem[7] = critScan.nextInt();
    		if (mem[7] < 0 || mem[7] > 99) {
    			System.out.println("Invalid value at posture in the critter file. "
            			+ "Posture set to default value of 0.");
        		mem[7] = 0;
    		}
    	}
    	
    	return mem;
    }
    
    /**
     * Loads {@code n} number of critters from {@code filename} critter file. They will be placed 
     * randomly at valid Hex's on the grid.
     * 
     * @param path - path of the critterfile to be read
     * @param n - number of critters to be placed
     * @throws SyntaxError if the critter does not have a memsize >= 8
     */
    public void loadCritters(String path, int n) {
		ArrayList<Hex> copy = new ArrayList<Hex>();
    	for (int i = 0; i < columns; i++) {
    		int strtrow = (i + 1) /2;
    		for (int j = strtrow; j < (height + strtrow); j++) {
    			Hex h = getHex(i, j);
    			if (h.isEmpty())
    				copy.add(h);
    		}
    	}
    	
    	
    	for (int i = 0; i < n; i++) {
    		if (copy.isEmpty()) {
    			System.out.println("There is no remaining space on the board to place any more critters.");
    			return;
    		}
    		
    		//Find an empty Hex tile
    		int hex = new Random().nextInt(copy.size());
    		int dir = new Random().nextInt(6);
    		Hex h = copy.get(hex);
    		copy.remove(hex);
    		
    		//Scan the critter program
    		Scanner critScan;
			try {
				critScan = new Scanner(new FileInputStream(path));
			} catch (FileNotFoundException e) {
				System.out.println("Cannot find critter file specified.");
	    		return;
			}
			
			critterHelper(critScan, h.getRow(), h.getColumn(), dir);
    	}
    }
    
    /**
     * Add {@code num} number of critters to the world, all with the information stored in
     * the AddCritter dummy class. They are randomly placed on the world.
     * @param add
     * @param num
     */
    public Critter[] loadCritters(AddCritter add, int num) {
    	Critter[] critList = new Critter[num];
    	ArrayList<Hex> copy = new ArrayList<Hex>();
    	for (int col = 0; col < columns; col++) {
    		int strtrow = (col + 1) /2;
    		for (int row = strtrow; row < (height + strtrow); row++) {
    			Hex h = getHex(col, row);
    			if (h.isEmpty())
    				copy.add(h);
    		}
    	}
    	
    	for (int i = 0; i < num; i++) {
    		if (copy.isEmpty()) {
	    		return critList;
	    	}
	    	
			//Find an empty Hex tile
			int hex = new Random().nextInt(copy.size());
			int dir = new Random().nextInt(6);
			Hex h = copy.get(hex);
			copy.remove(hex);
			
			//Scan the critter file
			String program = add.program();
			if (program.startsWith("[") && program.endsWith("]"))
				program = program.substring(1, program.length() - 1);
			Scanner critScan = new Scanner(program);
			
			critList[i] = critterHelper(critScan, h.getRow(), h.getColumn(), dir, add.species_id(), add.mem());
			if (critList[i] != null) {
				critList[i].setId(crittersPlaced);
				incCrittersPlaced();
			}
    	}
    	return critList;
    }
    
    /**
     * Place a critter on hex tile and (col, row) used when the user 
     * wants to place critters in exact grid locations
     * @param path - path of critter file to be read
     * @param col - column of hex tile to place critter on
     * @param row - row of hex tile to place critter on
     */
    public void placeCritter(String path, int col, int row) {
    	//Scan the critter program
		Scanner critScan;
		try {
			critScan = new Scanner(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find critter file specified.");
    		return;
		}
		int dir = new Random().nextInt(6);
		
		critterHelper(critScan, row, col, dir);
    }
    
    /**
     * Place a critter with the information stored in AddCritter at the position p
     * @param add - AddCritter dummy object to hold the critter information
     * @param p - Position dummy object with the column and row information
     */
    public Critter placeCritter(AddCritter add, Position p) {
    	String program = add.program();
    	if (program.startsWith("[") && program.endsWith("]"))
    		program = program.substring(1, program.length()-1);
    	Scanner critScan = new Scanner(program);
    	int dir = new Random().nextInt(6);
    	
    	Hex hex = getHex(p.col(), p.row());
    	if (hex == null || !hex.isEmpty()) {
    		critScan.close();
    		return null;
    	}
    	
    	return critterHelper(critScan, p.row(), p.col(), dir, add.species_id(), add.mem());
	}
    
    
    /**
     * A helper method for the place and add critter methods
     */
    private Critter critterHelper(Scanner critScan, int row, int col, int dir) {
    	//Parse critter program
    	String name;
    	if (!critScan.next().equals("species:")) {
        	System.out.println("Invalid syntax at species in the critter file. "
        			+ "Species has been set to default value of 'critter'.");
        	name = "critter";
        }
    	else
    		name = critScan.nextLine().trim();
    	int[] mem = readCritMem(critScan);
    	
    	return critterHelper(critScan, row, col, dir, name, mem);
    }
    /**
     * A helper method for the place and add critter methods
     */
    private Critter critterHelper(Scanner critScan, int row, int col, int dir, String name, int[] mem) {
    	//Parse critter program
    	Parser p = ParserFactory.getParser();
    	StringBuilder sb = new StringBuilder();
    	while (critScan.hasNextLine()) {
    		sb.append(critScan.nextLine() + "\n");
    	}
    	BufferedReader reader = new BufferedReader(new StringReader(sb.toString()));
    	Program prgm = p.parse(reader);
    	if (prgm == null) 
    		return null;
    	//Critter creation and hex tile reference setting
    	Critter critter = new Critter(prgm, mem, name, dir, col, row, this, null);
    	critterList.add(critter);
    	Hex h = getHex(col, row);
    	h.setCritter(critter);
    	h.update();
    	critScan.close();
    	System.out.printf("A critter was placed at (%d, %d).%n", col, row);
    	return critter;
    }
    
    
    /**
     * Advances the world by {@code n} time steps.
     *
     * @param n - number of time steps to advance the world by
     */
    public void advanceTime(int n) {
    	for (int i = 0; i < n; i++) {
	    	numSteps += 1;
	    	versionNumber += 1;
	    	for (int j = 0; j < critterList.size(); j++) {
	    		Critter critter = critterList.get(j);
	    		if (critter != null) {
	    			critter.nextTurn();
	    			getHex(critter.getCol(), critter.getRow()).update();
	    		}
	    	}
	    	System.out.println("The world has entered version " + versionNumber);
	    	for (int j = 0; j < critterList.size(); j++) {
	    		if (critterList.get(j) == null) {
	    			critterList.remove(j);
	    			j--;
	    		}
	    	}
	    	for (int j = 0; j < foodList.size(); j++) {
	    		if (!foodList.get(j).hasFood()) {
	    			foodList.remove(j);
	    			j--;
	    		}
	    	}
    	}
    }
    /**
     * Run the simulation at {@code simSpeed} times per second
     */
    public void run(float simSpeed) {
    	if (simSpeed > 0) {
	    	simSpeed = 1000 / simSpeed;
			if (timer == null) {
				timer = new Timeline(new KeyFrame(Duration.millis(simSpeed), 
					ae -> advanceTime(1)));
			}
			else {
				timer.stop();
				timer = new Timeline(new KeyFrame(Duration.millis(simSpeed), 
						ae -> advanceTime(1)));
			}
			timer.setCycleCount(Animation.INDEFINITE);
			timer.play();
    	}
    	else { //The simulation speed is 0
    		if (timer != null)
    			timer.stop();
    	}
    }
    /**
     * Set up the changes between worlds to create a diff based on which hex's are changing.
     * Continues deadCritterList to the next world.
     */
    public void setWorldChanges(World oldWorld) {
    	for (int i = 0; i < columns && i < oldWorld.getColumns(); i++) {
    		int strtrow = (i+1) / 2;
    		for (int j = strtrow; j < strtrow + height && j < strtrow + oldWorld.getHeight(); j++) {
    			Hex oldHex = oldWorld.getHex(i, j);
    			Hex newHex = this.getHex(i, j);
    			if (newHex.hasRock() && !oldHex.hasRock()) {
    				newHex.update();
    			}
    			else if (newHex.hasFood() && !(oldHex.getFood() == newHex.getFood())) {
    				newHex.update();
    			}
    			else if (!oldHex.isEmpty()) {
    				newHex.update();
    			}
    		}
    	}
    	
    	this.deadCritterList = oldWorld.getDeadCritters();
    }
    /**
     * Prints the map in ASCII to console. Prints a (-) for an empty space,
     * a (#) for a rock, an integer (d) for a critter facing direction d, or an (F) for food.
     */
    public void printMap() {
    	StringBuilder sb = new StringBuilder();
    	
    	//Spaces for good formatting. Amount of spaces may change, but
    	//spaces.length() should always be an odd number, otherwise the offset will be slightly off.
    	String spaces = "     "; 
    	String offset = spaces.substring(0, (spaces.length()+1)/2);
    	
    	for (int i = height - 1; i >= 0; i--) {
    		StringBuilder top = new StringBuilder(offset);
    		StringBuilder bottom = new StringBuilder("");
    		
    		for (int j = 0; j < columns; j++) {
        		int strtrow = (j + 1) / 2;
        		Hex h = getHex(j, i+strtrow);
        		
    			if (j % 2 == 0) {
    				//bottom
    				if (h.hasRock())
    					bottom.append('#');
    				else if (h.hasFood())
    					bottom.append('F');
    				else if (h.hasCritter())
    					bottom.append(h.getCritter().getDirection());
    				else
    					bottom.append('-');
    				bottom.append(spaces);
    			}
    			else {
    				//top
    				if (h.hasRock())
    					top.append('#');
    				else if (h.hasFood())
    					top.append('F');
    				else if (h.hasCritter())
    					top.append(h.getCritter().getDirection());
    				else
    					top.append('-');
    				top.append(spaces);
    			}
    		}
    		sb.append(top.toString() + '\n' + bottom.toString() + '\n');
    	}
    	
    	System.out.println("\nWorld: \n" + sb.toString() + '\n');
    }
    
    /**
     * Returns the ArrayList of Critters in the world
     * @return list of critters
     */
    public ArrayList<Critter> getCritters() {
		return critterList; 
    }
    /**
     * Returns the ArrayList of Critters that died in the world
     */
    public ArrayList<DeadCritter> getDeadCritters() {
    	return deadCritterList;
    }
    /**
     * Return the ArrayList of tiles with rocks in the world
     * @return list of hex tiles with rocks
     */
    public ArrayList<Hex> getRocks() {
    	return rockList;
    }
    /**
     * Return the ArrayList of tiles with food in the world
     * @return list of hex tiles with food
     */
    public ArrayList<Hex> getFood() {
    	return foodList;
    }
    /**
     * Returns the hex at {@code col} and {@code row}, null if the coordinates are out-of-bounds.
     * @return the hex at col and row
     */
    public Hex getHex(int col, int row) {
		return grid.get(getKey(col, row));
    }
    /**
     * Returns the name of the world as specified in world.txt,
     * otherwise the name is 'Default World'
     */
    public String getName() {
		return name;
    }
    /**
     * Returns the number of rows in the hex grid
     */
    public int getRows() {
		return rows;
    }
    /**
     * Returns the number of columns in the hex grid
     */
    public int getColumns() {
		return columns;
    }
    /**
     * Returns the number of hex tiles that go in each column
     */
    public int getHeight() {
    	return height;
    }
    /**
     * Returns the amount of Critters placed over all versions of the world.
     * @return The amount of critters placed since the server was started.
     */
    public int getCrittersPlaced() {
    	return crittersPlaced;
    }
    /**
     * Increments the counter of critters placed by one.
     */
    public void incCrittersPlaced() {
    	crittersPlaced += 1;
    }
    /**
     * Returns the number of steps this world has taken since is was initialized
     */
    public int getNumsteps() {
    	return numSteps;
    }
    /**
     * 
     */
    public float getRate() {
    	if (timer == null || timer.getStatus() == Animation.Status.STOPPED)
    		return 0;
    	return (float) (1 / timer.getKeyFrames().get(0).getTime().toSeconds());
    }
    /**
     * 
     * @return The key required to get the hex tile at (col, row) from the hashtable
     */
    private String getKey(int col, int row) {
		return "(" + col + ", " + row + ")";
    }
    /**
     * Increments the world's version number by one.
     */
    public void incVersion() {
    	versionNumber += 1;
    }
    /**
     * 
     */
    public void setVersion(int version) {
    	versionNumber = version;
    }
    /**
     * Returns the current version number of the world.
     */
    public int getVersion() {
    	return versionNumber;
    }
    /**
     * Adds a critter to the critter list 
     */
    public void addCritter(Critter critter) {
		critterList.add(critter); 
    }
    /**
     * Changes all distances back to infinite
     */
    public void resetTiles() {
    		Set<String> keys = grid.keySet();
    		for (String key: keys) {
    			grid.get(key).setDistance(Integer.MAX_VALUE);
    			grid.get(key).setDirection(0);
    			grid.get(key).setPrev(null);
    		}
    }
    

/**
 * Client Side method to place rocks in the client side world 
 * @param row - row to place rock
 * @param col - col to place rock
 */
    public void placeRock(int row, int col) {
    		String key = getKey(col, row);
    		Hex hex = grid.get(key);
    		if (hex != null) {
    			if (hex.hexContains() < -1) {
    				hex.setFood(0);
    				foodList.remove(hex);
    				hex.setRock();
    				rockList.add(hex);
    			}
    			else if (hex.hexContains() > 0) {
    				critterList.remove(hex.getCritter());
    				hex.removeCritter();
    				hex.setRock();
    				rockList.add(hex);
    			}
    			else if (hex.hexContains() == 0){
    				hex.setRock();
    				rockList.add(hex);
    			}
    		}
    		
    		
    }
    /**
     * Client side method used to place critters in the client side world model
     * @param row critter's row location
     * @param col critter's col location
     * @param critter to add
     */
    public void placeCritter(int row, int col, Critter critter) {
    		String key = getKey(col, row);
    		Hex hex = grid.get(key);
    		if (hex != null) {
    			if (hex.hasCritter()) {
    				critterList.remove(hex.getCritter());
    				hex.removeCritter();
    			}
    			else if (hex.hasFood()) {
    				foodList.remove(hex);
    				hex.setFood(0);
    			}
    			else if (hex.hasRock()) {
    				rockList.remove(hex);
    				hex.removeRock();
    			}
    			hex.setCritter(critter);
    			critterList.add(critter);
    		}
    }
    /**
     * Client side method used to manipulate food on hexes
     * @param row critter's row location
     * @param col critter's col location
     * @param amount of food to add
     */
    public void placeFood(int row, int col, int amount) {
    		String key = getKey(col, row);
    		Hex hex = grid.get(key);
    		if (hex != null) {
    			if (hex.hasCritter()) {
    				critterList.remove(hex.getCritter());
    				hex.removeCritter();
    				hex.setFood(amount);
    				foodList.add(hex);
    			}
    			else if (hex.hasFood()) {
    				hex.setFood(amount);
    			}
    			else if (hex.hasRock()) {
    				hex.removeRock();
    				foodList.add(hex);
    				hex.setFood(amount);
    			}
    			else {
    				foodList.add(hex);
    				hex.setFood(amount);
    			}
    		}
    }
    /**
     * 
     * @param row of hex to make empty
     * @param col of hex to make empty
     */
    public void placeEmpty(int row, int col) {
    		String key = getKey(col, row);
    		Hex hex = grid.get(key);
    		if (hex != null) {
    			if  (hex.hasCritter()) {
    				critterList.remove(hex.getCritter());
    				hex.removeCritter();
    			}
    			else if (hex.hasFood()) {
    				hex.setFood(0);
    				foodList.remove(hex);
    			}
    			else if (hex.hasRock()) {
    				hex.removeRock();
    				rockList.remove(hex);
    			}
    		}
    }
    /**
     * 
     * @param timeStep the time step to set {@code} this to
     */
    public void setTimeStep(int timeStep) {
    		numSteps = timeStep; 
    }
}
