package console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ast.Program;
import ast.Rule;

/**
 * 
 * Set of classes used to store critter world information to prepare
 * to be serialized with JSON and sent to the server
 *
 */
public class DummyClassLibrary {
	public static class SetMessage {
		private boolean status;
		
		public SetMessage(boolean status) {
			this.status = status;
		}
	}
	/**
	 * The login information processed for a login attempt.
	 * Request in post("/login").
	 */
	public static class Login {
		private Level level;
		private String password;
		public Login(String level, String password) {
			this.level = Level.getLevel(level);
			this.password = password; 
		}
		public Level level() {return level;}
		public String password() {return password;}
	}
	/**
	 * The session id created after a successful login attempt.
	 * Response in post("/login").
	 */
	public static class Session {
		private int session_id;
		private String author_message = "You have sucessfully logged in.";
		
		public Session(int id) {
			this.session_id = id;
		}
		/**
		 * 
		 * @return the session ID
		 */
		public int getSessionID() {return session_id;}
	}
	/**
	 * The information to what critters should be added to the world.
	 * Request in post("/critters").
	 */
	public static class AddCritter {
		private String species_id;
		private String program;
		private int[] mem;
		private Position[] positions;
		private Integer num;
		
		public AddCritter(String species_id, String program, int[] mem, int num) {
			this.species_id = species_id;
			this.program = program;
			this.mem = mem;
			this.positions = null;
			this.num = num;
		}
		public AddCritter(String species_id, String program, int[] mem, Position[] positions) {
			this.species_id = species_id;
			this.program = program;
			this.mem = mem;
			this.positions = positions;
			this.num = null;
		}
		public String species_id() {return species_id;}
		public String program() {return program;}
		public int[] mem() {return mem;}
		public Position[] positions() {return positions;}
		public Integer num() {return num;}
	}
	
	/**
	 * Used to hold information about a critter that has died. Never explicitly
	 * used in a request or response, but is used in the deadCritterList object
	 * in World.
	 */
	public static class DeadCritter {
		private int timeOfDeath;
		private int id;
		
		public DeadCritter(int timeOfDeath, int id) {
			this.timeOfDeath = timeOfDeath;
			this.id = id;
		}
		
		public int timeOfDeath() {return timeOfDeath;}
		public int id() {return id;}
	}
	/**
	 * The info for the critters added to the world after a post("/critters") request.
	 * Response in a post("/critters") request.
	 */
	public static class CrittersAdded {
		private String species_id;
		private int[] ids;
		
		public CrittersAdded(String species_id, int[] ids) {
			this.species_id = species_id;
			this.ids = ids;
		}
		
		public int[] ids() {return ids;}
	}
	
	/**
	 * The position info for a critter to be placed.
	 * Request in post("/critter").
	 */
	public static class Position {
		private int row;
		private int col;
		
		public Position(int row, int col) {
			this.row = row;
			this.col = col;
		}
		public int row() {return row;}
		public int col() {return col;}
	}
	/**
	 * Class for getting general information about a hex or entity.
	 * Request in post("/create_entity").
	 */
	public static class GenInfo {
		private int row;
		private int col;
		private Type type;
		private int value;
		
		public int row() {return row;}
		public int col() {return col;}
		public Type type() {return type;}
		public int value() {return value;}
	}
	/**
	 * Interface for all Info class that send information in get("/world").
	 * Response in get("/world").
	 */
	public static interface StateInfo {
		public int row();
		public int col();
		public Type type();
	}
	/**
	 * Class for holding information about a rock hex.
	 * Response in get("/world").
	 */
	public static class RockInfo implements StateInfo {
		private int row;
		private int col;
		private Type type;
		
		public RockInfo(int row, int col) {
			this.row = row;
			this.col = col;
			this.type = Type.rock;
		}
		
		public int row() {return row;}
		public int col() {return col;}
		public Type type() {return type;}
	}
	/**
	 * Class for holding information about a hex with food on it.
	 * Response in get("/world").
	 */
	public static class FoodInfo implements StateInfo {
		private int row;
		private int col;
		private Type type;
		private int value;
		
		public FoodInfo(int row, int col, int value) {
			this.row = row;
			this.col = col;
			this.value = value;
			this.type = Type.food;
		}
		
		public int row() {return row;}
		public int col() {return col;}
		public Type type() {return type;}
		public int value() {return value;}
	}
	/**
	 * Class for holding information about a hex that has been updated to have nothing.
	 * Response in get("/world").
	 */
	public static class NothingInfo implements StateInfo {
		private Type type;
		private int row;
		private int col;
		
		public NothingInfo(int row, int col) {
			this.row = row;
			this.col = col;
			this.type = Type.nothing;
		}
		
		public int row() {return row;}
		public int col() {return col;}
		public Type type() {return type;}
	}
	/**
	 * A collection of CritterInfo.
	 * Response in a get("/critters") or get("/critter") request.
	 */
	public static class CritterInfo implements StateInfo{
		private Type type;
		private int id;
		private String program;
		private String species_id;
		private int row, col, direction;
		private int[] mem;
		private int recently_executed_rule;
		
		public CritterInfo(int id, String species_id, int row, int col, 
				int direction, int[] mem) {
			this.id = id;
			this.species_id = species_id;
			this.row = row;
			this.col = col;
			this.direction = direction;
			this.mem = mem;
		}
		
		public CritterInfo(int id, String species_id, int row, int col, 
				int direction, int[] mem, Type type) {
			this(id, species_id, row, col, direction, mem);
			this.type = type;
		}
		
		public CritterInfo(int id, String species_id, int row, int col, 
				int direction, int[] mem, Program program, Rule rule) {
			this(id, species_id, row, col, direction, mem);
			this.program = program.toString();
			this.recently_executed_rule = rule == null ? -1 : program.getIndexOf(rule);
		}
		
		public CritterInfo(int id, String species_id, int row, int col, 
				int direction, int[] mem, Program program, Rule rule, Type type) {
			this(id, species_id, row, col, direction, mem, program, rule);
			this.type = type;
		}
		
		public int row() {return row;}
		public int col() {return col;}
		public Type type() {return type;}
	}
	
	/**
	 * A collection of CritterInfo.
	 * Response in a get("/critters") or get("/critter") request.
	 */
	public static class CritterInfoForGet {
		private Type type;
		private int id;
		private String program;
		private String species_id;
		private int row, col, direction;
		private int[] mem;
		private int recently_executed_rule;
		
	}
	/**
	 * 
	 * Used for get("/world")
	 *
	 */
	public static class WorldInfoForGet {
		private int current_timestep;
		private int current_version_number;
		private int update_since;
		private double rate;
		private String name;
		private int population;
		private int rows;
		private int cols;
		private ArrayList<Integer> dead_critters;
		private ArrayList<GeneralInfo> state;
		
		/**
		 * @return the name of the world
		 */
		public String getName() {
			return name;
		}
		/**
		 * 
		 * @return the last update
		 */
		public int getUpdate_since() {
			return update_since;
		}
		/**
		 * 
		 * @return the current time-step of the world
		 */
		public int getCurrent_timestep() {
			return this.current_timestep;
		}
		/**
		 * 
		 * @return the current version of the world
		 */
		public int getCurrent_version_number() {
			return this.current_version_number;
		}
		public double getRate() {
			return rate;
		}
		public int getPopulation() {
			return this.population;
		}
		public int getRows() {
			return this.rows;
		}
		public int getCols() {
			return this.cols;
		}
		public ArrayList<Integer> getDeadCritters(){
			return this.dead_critters;
		}
		public ArrayList<GeneralInfo> getStates(){
			return state;
		}
	}
	
	/**
	 * 
	 * Used to convert stateinfo sent by the server to something that is able to be processed by GSON
	 *
	 */
	public static class GeneralInfo {
		private Type type;
		private int id;
		private String program;
		private String species_id;
		private int row, col, direction;
		private int[] mem;
		private int recently_executed_rule;
		private int value;
		
		public Type getType() {
			return type;
		}
		public int getID() {
			return id;
		}
		public String getProgram() {
			return program;
		}
		public String getSpecies_id() {
			return species_id;
		}
		public int getRow() {
			return row;
		}
		public int getCol() {
			return col;
		}
		public int getDirection() {
			return direction;
		}
		public int[] getMemory() {
			return mem;
		}
		public int getRecently_executed_rule() {
			return recently_executed_rule;
		}
		public int getValue() {
			return value; 
		}
	}
	/**
	 * A collection of world info.
	 * Response in get("/world").
	 */
	public static class WorldInfo{
		private int current_timestep;
		private int current_version_number;
		private int update_since;
		private double rate;
		private String name;
		private int population;
		private int rows;
		private int cols;
		private ArrayList<Integer> dead_critters;
		private ArrayList<StateInfo> state;
		
		public WorldInfo(int time, int version, int update, double rate,
				String name, int population, int rows, int columns, ArrayList<Integer> ids,
				ArrayList<StateInfo> state) {
			this.current_timestep = time;
			this.current_version_number = version;
			this.update_since = update;
			this.rate = rate;
			this.name = name;
			this.population = population;
			this.rows = rows;
			this.cols = columns;
			this.dead_critters = ids;
			this.state = state;
		}
		
		public int getCurrentTimeStep() { return current_timestep; }
		public int getUpdateSince() { return update_since; }
		public int getVersionNumber() {return current_version_number;}
		public double getRate() {return rate;}
		public String getName() {return name;}
		public int getPopulation() {return population;}
		public int getRows() {return rows;}
		public int getColumns() {return cols;}
		public ArrayList<Integer> getDeadCritters() {return dead_critters;}
		public ArrayList<StateInfo> getStateInfo() {return state;}
	}

	/**
	 * A world description.
	 * Request in post("/world").
	 */
	public static class Description {
		private String description;
		
		public Description(String description) {
			this.description = description;
		}
		
		public String description() {return description;}
	}
	/**
	 * A class to hold information about the rate at which the world is running.
	 * Response and request in post("/run").
	 */
	public static class Rate {
		private float rate;
		
		public Rate(int rate) {
			this.rate = rate;
		}
		public float rate() {return rate;}
	}
	/**
	 * A class to hold the amount of times that the world should step.
	 * Request in post("/step").
	 */
	public static class Step {
		private int count;
		
		public Step() {
			count = 1;
		}
		public Step(int count) {
			this.count = count;
		}
		
		public int count() {return count;}
	}
	/**
	 * 
	 */
	public static class Error {
		//TODO use this class for error messages
		private int error_code;
		private String message;
	}
	/**
	 * Level of access for a session id.
	 */
	public static enum Level {
		read("read"), write("write"), admin("admin");
		private static final Map<String, Level> levelMap = new HashMap<String, Level>(values().length, 1);
		static {
			for (Level l: values()) {
				levelMap.put(l.levelString, l);
			}
		}
		private final String levelString;
		private Level(String level) {
			this.levelString = level; 
		}
		
		public static Level getLevel(String levelString) {
			Level level = levelMap.get(levelString);
			return level; 
		}
	}
	/**
	 * The types of objects that can be returned in state in a get("/world") request.
	 */
	public static enum Type {
		critter, rock, food, nothing
	}
}
