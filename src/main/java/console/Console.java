package console;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * The console user interface for Assignment 5.
 */
public class Console {
    private Scanner scan;
    public boolean done;
    public PrintStream out;

    /* =========================== */
    /* DO NOT EDIT ABOVE THIS LINE */
    /* (except imports...)         */
    /* =========================== */
    
    
    /**
     * The world file that holds all the critters and hex tiles.
     */
    private World world;
    
    /**
     * Starts new random world simulation.
     */
    public void newWorld() {
        world = new World();
    }

    /**
     * Starts new simulation with world specified in PATH.
     *
     * @param filename
     */
    public void loadWorld(String filename) {
		world = new World(filename);
    }

    /**
     * Loads critter definition from filename and randomly places
     * n critters with that definition into the world.
     *
     * @param filename
     * @param n
     */
    public void loadCritters(String filename, int n) {
    	if (world == null) {
    		System.out.println("The world has not been initialized yet.");
    		return;
    	}
		world.loadCritters(filename, n);
    }

    /**
     * Advances the world by n time steps.
     *
     * @param n
     */
    public void advanceTime(int n) {
        
    	if (world == null) {
    		System.out.println("The world has not been initialized yet.");
    		return;
    	}
    	world.advanceTime(n);
    }

    /**
     * Prints current time step, number of critters, and world
     * map of the simulation.
     */
    public void worldInfo() {
    	if (world == null) {
    		System.out.println("The world has not been initialized yet.");
    		return;
    	}
        worldInfo(world.getNumsteps(), world.getCritters().size());
        world.printMap();
    }

    /**
     * Prints description of the contents of hex (c,r).
     *
     * @param c column of hex
     * @param r row of hex
     */
    public void hexInfo(int c, int r) {
    	if (world == null) {
    		System.out.println("The world has not been initialized yet.");
    		return;
    	}
    	else if (c >= world.getColumns() || c < 0) {
    		System.out.println("You did not pass a valid column number. Please try again.");
    		return;
    	}
    	else if (r >= world.getColumns() || r < 0) {
    		System.out.println("You did not pass a valid row number. Please try again.");
    		return;
    	}
    	
    	
    	Hex h = world.getHex(c, r);
    	if (h != null) { 
    		Critter crit = h.getCritter();
    		if (crit != null) {
    			if (crit.getLastRule() != null)
    				critterInfo(crit.getSpecies(), crit.getMem(), crit.getAst(), crit.getLastRule().toString());
    			else
    				critterInfo(crit.getSpecies(), crit.getMem(), crit.getAst(), null);
    		}
    		else if (h.hasRock())
    			terrainInfo(Constants.ROCK_VALUE());
    		else if (h.getFood() > 0)
    			terrainInfo(-h.getFood() - 1);
    		else
    			terrainInfo(0);
    		}
    		else {
    			System.out.println("No hex was found with that index");
        }
    }
   
    /* =========================== */
    /* DO NOT EDIT BELOW THIS LINE */
    /* =========================== */

    /**
     * Be sure to call this function, we will override it to grade.
     *
     * @param numSteps      The number of steps that have passed in the world.
     * @param crittersAlive The number of critters currently alive.
     */
    protected void worldInfo(int numSteps, int crittersAlive) {
        out.println("steps: " + numSteps);
        out.println("critters: " + crittersAlive);
    }

    /**
     * 
     *
     * @param species  The species of the critter.
     * @param mem      The memory of the critter.
     * @param program  The program of the critter pretty printed as a String. This should be able to be parsed back to
     *                 the same AST.
     * @param lastrule The last rule executed by the critter pretty printed as a String. This should be able to be
     *                 parsed back to the same AST. If no rule has been executed, this parameter should be null.
     */
    protected void critterInfo(String species, int[] mem, String program, String lastrule) {
        out.println("Species: " + species);
        StringBuilder sbmem = new StringBuilder();
        for (int i : mem) {
            sbmem.append(" ").append(i);
        }
        out.println("Memory:" + sbmem.toString());
        out.println("Program: " + program);
        out.println("Last rule: " + lastrule);
    }

    /**
     * Be sure to call this function, we will override it to grade.
     *
     * @param terrain 0 is empty, -1 is rock, -X is (X-1) food
     */
    protected void terrainInfo(int terrain) {
        if (terrain == 0) {
            out.println("Empty");
        } else if (terrain == -1) {
            out.println("Rock");
        } else {
            out.println("Food: " + (-terrain - 1));
        }
    }

    /**
     * Prints a list of possible commands to the standard output.
     */
    public void printHelp() {
        out.println("new: start a new simulation with a random world");
        out.println("load <world_file>: start a new simulation with "
                + "the world loaded from world_file");
        out.println("critters <critter_file> <n>: add n critters "
                + "defined by critter_file randomly into the world");
        out.println("step <n>: advance the world by n timesteps");
        out.println("info: print current timestep, number of critters "
                + "living, and map of world");
        out.println("hex <c> <r>: print contents of hex "
                + "at column c, row r");
        out.println("exit: exit the program");
    }

    /**
     * Constructs a new Console capable of reading a given input.
     */
    public Console(InputStream in, PrintStream out) {
        this.out = out;
        scan = new Scanner(in);
        done = false;
    }

    /**
     * Constructs a new Console capable of reading the standard input.
     */
    public Console() {
        this(System.in, System.out);
    }

    /**
     * Processes a single console command provided by the user.
     */
    public void handleCommand() {
        out.print("Enter a command or \"help\" for a list of commands.\n> ");
        String command = scan.next();
        switch (command) {
            case "new": {
                newWorld();
                break;
            }
            case "load": {
                String filename = scan.next();
                loadWorld(filename);
                break;
            }
            case "critters": {
                String filename = scan.next();
                int n = scan.nextInt();
                loadCritters(filename, n);
                break;
            }
            case "step": {
                int n = scan.nextInt();
                advanceTime(n);
                break;
            }
            case "info": {
                worldInfo();
                break;
            }
            case "hex": {
                int c = scan.nextInt();
                int r = scan.nextInt();
                hexInfo(c, r);
                break;
            }
            case "help": {
                printHelp();
                break;
            }
            case "exit": {
                done = true;
                break;
            }
            default:
                out.println(command + " is not a valid command.");
        }
    }

    public static void main(String[] args) {
        Console console = new Console();
        while (!console.done) {
            console.handleCommand();
        }
    }

}
