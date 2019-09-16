package console;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.delete;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.gson.Gson;

import console.DummyClassLibrary.*;
import spark.Request;

/**
 * A server that hosts a critter world on it
 */
public class Server {
	/**
	 * The world that is running on the server
	 */
	private World world;
	/**
	 * Hash table of session numbers and their corresponding Levels of access
	 */
	private Hashtable<Integer, Level> sessions;
	/**
	 * The port the server must be connected through to
	 */
	private int port;
	/**
	 * The password required for a session to have read privileges
	 */
	private String readPassword;
	/**
	 * The password required for a session to have write privileges
	 */
	private String writePassword;
	/**
	 * The password required for a session to have admin privileges
	 */
	private String adminPassword;
	/**
	 * Constructor for a server object with the specified passwords.
	 */
	public Server(int port, String readPassword, String writePassword, String adminPassword) {
		this.port = port;
		this.readPassword = readPassword;
		this.writePassword = writePassword;
		this.adminPassword = adminPassword;
		
		sessions = new Hashtable<Integer, Level>();
		sessions.put(1, Level.admin);
	}
	/**
	 * Starts running a server
	 */
	public void run() {
		//Run the server on a port
		port(port);
		
		//Create a new ReadWrite lock
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		
		//Create a new GSON converter
		Gson gson = new Gson();
		
		//Create an empty world of default size
		world = new World();
		
		
		/**
		 * Login to the server by sending a login post request. The body required to be sent
		 * is a raw Json file in the form of
		 * {
		 * 		"level": "level", //where level can be read, write, or admin
		 * 		"password": "password" //where password corresponds to the level.
		 * }
		 * 
		 * If successful, this sends back the sessionId created with the privileges specified by the level.
		 * If unsuccessful, this sends back a status code 401 
		 */
		post("/login",
			(request, response)
				-> {
					response.header("Content-Type", "application/json");
					//Get data from the post body
					String json = request.body();
					Login login = gson.fromJson(json, Login.class);
					if (login == null || login.level() == null || login.password() == null) {
						return error400();
					}
					
					boolean correct = false;
					
					switch(login.level()) {
					case read:
						if (login.password().equals(readPassword))
							correct = true;
						break;
					case write:
						if (login.password().equals(writePassword))
							correct = true;
						break;
					case admin:
						if (login.password().equals(adminPassword))
							correct = true;
						break;
					}
					
					if (correct) {
						int id;
						do {id = new Random().nextInt(Integer.MAX_VALUE);}
						while (sessions.containsKey(id));
						
						sessions.put(id, login.level());
						return new Session(id);
					} else {
						return error401();
					}
				},
			gson::toJson);
		
		/**
		 * Read locked action.
		 * 
		 * Responds with the json list of all the critters in the world. The critter info shown
		 * Is the id #, species_id name, position, direction, and memory values. If the critter
		 * was created by the same {@code session_id} that is currently doing the call, then the info will
		 * also show the {@code program} and the most {@code recently_executed_rule}.
		 */
		get("/critters",
			(request, response)
				-> {
					if(checkSessionId(request, Level.read)) {
						lock.readLock().lock(); //Read lock
						
						response.header("Content-Type", "application/json");
						int sessionId = Integer.parseInt(request.queryParams("session_id"));
						CritterInfo[] critList = new CritterInfo[world.getCritters().size()];
						for (int i = 0; i < world.getCritters().size(); i++) {
							Critter critter = world.getCritters().get(i);
							if (sessionId == critter.getSessionId() || sessions.get(sessionId) == Level.admin) {
								critList[i] = new CritterInfo(critter.getId(),
										critter.getSpecies(), critter.getRow(), critter.getCol(),
										critter.getDirection(), critter.getMem(), critter.getProgram(), 
										critter.getLastRule());
							}
							else {
								critList[i] = new CritterInfo(critter.getId(),
										critter.getSpecies(), critter.getRow(), critter.getCol(),
										critter.getDirection(), critter.getMem());
							}
						}
						
						lock.readLock().unlock(); //Read unlock
						return critList;
					} else {
						return error401();
					}
				},
			gson::toJson);
		
		/**
		 * Write locked action.
		 * 
		 * Create a critter on the world file. Request body should be a json that holds information about
		 * the {@code species_id}, {@code program}, {@code mem}, and either the {@code positions} of the 
		 * critters to be placed, or the {@code num} of critters to be placed. The positions must be held in
		 * a list of objects that have the parameters {@code row} and {@code column}.
		 * 
		 * The response should be the species id and the id's of the critters that were just added.
		 * {
		 * 		"species_id": "",
		 * 		"ids": []
		 * }
		 */
		post("/critters",
			(request, response)
				-> {
					if(checkSessionId(request, Level.write)) {
						response.header("Content-Type", "application/json");
						
						String json = request.body();
						AddCritter add = gson.fromJson(json, AddCritter.class);
						
						if (add == null || add.species_id() == null ||
								add.program() == null || add.mem() == null) {
							return error400();
						}
						else if ( !(add.positions() == null ^ add.num() == null) ) {
							//Check if either both arguments are there, or neither arguments
							//True if there is either a value num > 0 or a position in positions
							//But not both at a time, and not neither.
							return error400();
						}


						
						world.incVersion();
						boolean placed = false;
						boolean usePositions = add.positions() != null;
						int[] ids = usePositions ? new int[add.positions().length] : new int[add.num()];
						
						lock.writeLock().lock(); //Write lock
						
						if (usePositions) {
							for (int i = 0; i < add.positions().length; i++) {
								Position p = add.positions()[i];
								Critter critter = world.placeCritter(add, p);
								if (critter != null) {
									critter.setId(world.getCrittersPlaced());
									ids[i] = world.getCrittersPlaced();
									placed = true;
									world.incCrittersPlaced();
								}
								else {
									ids[i] = -1;
								}
							}
						}
						else {
							Critter[] critList = world.loadCritters(add, add.num());
							for (int i = 0; i < add.num(); i++) {
								Critter critter = critList[i];
								if (critter != null) {
									ids[i] = world.getCrittersPlaced();
									placed = true;
								}
								else {
									ids[i] = -1;
								}
							}
						}
						
						if (!placed)
							world.setVersion(world.getVersion() - 1);
						
						lock.writeLock().unlock(); //Write unlock
						return new CrittersAdded(add.species_id(), ids);
					} else {
						return error401();
					}
				},
			gson::toJson);
		
		/**
		 * Read locked action.
		 * 
		 * Get the critter information of a specific critter given an id number.
		 */
		get("/critter/:id",
			(request, response)
				-> {
					if (checkSessionId(request, Level.read)) {
						response.header("Content-Type", "application/json");
						int sessionId = Integer.parseInt(request.queryParams("session_id"));
						int num;
						try {
							num = Integer.parseInt(request.params(":id"));
						} catch (NumberFormatException e) {
							e.printStackTrace();
							return error406();
						}
						
						lock.readLock().lock(); //Read lock
						if (num <= 0 || num >= world.getCrittersPlaced()) {
							lock.readLock().unlock(); //Read unlock
							return error406();
						}
						
						for (Critter critter : world.getCritters()) {
							if (critter.getId() == num) {
								if (sessionId == critter.getSessionId() || sessions.get(sessionId) == Level.admin) {
									lock.readLock().unlock(); //Read unlock
									lock.writeLock().unlock();
									return new CritterInfo(critter.getId(),	critter.getSpecies(),
											critter.getRow(), critter.getCol(), critter.getDirection(),
											critter.getMem(), critter.getProgram(), critter.getLastRule());
								}
								else {
									lock.readLock().unlock(); //Read unlock
									return new CritterInfo(critter.getId(), critter.getSpecies(), critter.getRow(),
											critter.getCol(), critter.getDirection(), critter.getMem());
								}
							}
						}
						
						lock.readLock().unlock(); //Read unlock
						return "The critter id specified belongs to a critter that has died.";
					} else {
						return error401();
					}
				},
			gson::toJson);
		
		/**
		 * Write locked action.
		 * 
		 * Delete the critter with the specified id from the world. The critter can only be
		 * removed by the user who placed the critter, or an admin.
		 */
		delete("/critter/:id",
			(request, response)
				-> {
					if (checkSessionId(request, Level.write)) {
						response.header("Content-Type", "application/json");
						int sessionId = Integer.parseInt(request.queryParams("session_id"));
						int num;
						try {
							num = Integer.parseInt(request.params(":id"));
						} catch (NumberFormatException e) {
							e.printStackTrace();
							return error406();
						}
						
						lock.readLock().lock(); //Read lock
						if (num <= 0 || num >= world.getCrittersPlaced()) {
							lock.readLock().unlock(); //Read unlock
							return error406();
						}
						
						for (int i = 0; i < world.getCritters().size(); i++) {
							Critter critter = world.getCritters().get(i);
							if (critter.getId() == num) {
								if (sessionId == critter.getSessionId() || sessions.get(sessionId) == Level.admin) {
									lock.readLock().unlock(); //Read unlock
									lock.writeLock().lock(); //Write lock
									
									int row = critter.getRow();
									int col = critter.getCol();
									
									world.getCritters().remove(i);
									world.getHex(col, row).removeCritter();
									world.getDeadCritters().add(new DeadCritter(world.getVersion(), num));
									world.incVersion();
									
									lock.writeLock().unlock(); //Write unlock
									return new SetMessage(true);
								}
								else {
									lock.readLock().unlock(); //Read unlock
									return error401();
								}
							}
						}
						
						return "The critter id specified belongs to a critter that has died.";
					} else {
						return error401();
					}
				},
			gson::toJson);
		
		/**
		 * Admin locked action.
		 * 
		 * Creates the world following the specifications sent in the body of the request under 
		 * the parameter "description". The critters specified in the world file will not be added
		 * to the world. They must be added individually later.  
		 */
		post("/world",
			(request, response)
				-> {
					if (checkSessionId(request, Level.admin)) {
						
						
						response.header("Content-Type", "application/json");
						//Get data from the post body
						String json = request.body();
						Description description = gson.fromJson(json, Description.class);
						
						if (description == null) {
							lock.writeLock().unlock();
							return error400();
						}
						
						World w;
						try {
							w = new World(description);
						} catch (Exception e) {
							e.printStackTrace();
							//TODO Change message
							return "There was a problem loading your world file.";
						}
						
						if (w.getRows() == -1 && w.getColumns() == -1) {
							return error406();
						}
						
						lock.writeLock().lock(); //Write lock
						w.setVersion(world.getVersion() + 1);
						w.setWorldChanges(world);
						world = w;
						lock.writeLock().unlock(); //Write unlock
						return "Ok";
					} else {
						return error401();
					}
				},
			gson::toJson);
		
		/**
		 * Read locked action.
		 * 
		 * Return the status of the world at its current timestep and version number.
		 * Takes in arguments {@code update_since}, for specific updates on the world, as well as
		 * from and to col/row arguments for updating subsections. For subsections, the code passed through is
		 * inclusive for the from col/row, but exclusive of the to col/row.
		 * 
		 * Returns a JSON containing information about: the {@code current_timestep} of the world,
		 * the {@code current_version_number} of the world, {@code update_since} as was passed into the
		 * parameters (default set to zero), the {@code rate} that the world is changing, the {@code name}
		 * of the world, the {@code population}, size in {@code rows} and {@code cols}, a list of the ids of 
		 * the {@code dead_critters}, and a list of the the changes to the hex's in the world, held in {@code state}.
		 */
		get("/world",
			(request, response)
				-> {
					if (checkSessionId(request, Level.read)) {
						response.header("Content-Type", "application/json");
						int update_since, from_row, to_row, from_col, to_col;
						try {
							update_since = request.queryParams().contains("update_since") ?
									Integer.parseInt(request.queryParams("update_since")) : 0;
							from_row = request.queryParams().contains("from_row") ?
									Integer.parseInt(request.queryParams("from_row")) : 0;
							to_row = request.queryParams().contains("to_row") ?
									Integer.parseInt(request.queryParams("to_row")) : world.getRows();
							from_col = request.queryParams().contains("from_col") ?
									Integer.parseInt(request.queryParams("from_col")) : 0;
							to_col = request.queryParams().contains("to_col") ?
									Integer.parseInt(request.queryParams("to_col")) : world.getColumns();
						} catch (NumberFormatException e) {
							return error400();
						}
						
						//Check to_col and from_col to make sure they are valid inputs
						if (update_since > world.getVersion()+1 || world.getHex(from_col, from_row) == null
								|| world.getHex(to_col-1, to_row-1) == null || from_col > to_col || from_row > to_row) {
							/*System.out.println();
							System.out.println(update_since > world.getVersion()+1);
							System.out.println(world.getHex(from_col, from_row) == null);
							System.out.println("from_col: " + from_col + " from_row: " + from_row);
							System.out.println("to_col: " + to_col + " to_row: " + to_row);
							System.out.println(world.getHex(to_col-1, to_row-1) == null);
							System.out.println(world.getHex(to_col-2, to_row-2) == null);
							System.out.println(from_col > to_col);
							System.out.println(from_row > to_row);*/
							return error406();
						}
						
						lock.readLock().lock(); //Read lock
						
						ArrayList<Integer> ids = new ArrayList<Integer>();
						for (DeadCritter d : world.getDeadCritters()) {
							if (d.timeOfDeath() > update_since)
								ids.add(d.id());
						}
						
						//Increase efficiency?
						ArrayList<StateInfo> state = new ArrayList<StateInfo>();
						for (int i = from_col; i < to_col; i++) {
							int strtrow = (i + 1) / 2;
							for (int j = strtrow; j < strtrow + world.getHeight() && j < to_row; j++) {
								Hex h = world.getHex(i, j);
								if (h.getLastUpdated() > update_since) {
									if (h.hasRock()) {
										state.add(new RockInfo(j, i));
									} else if (h.hasFood()) {
										state.add(new FoodInfo(j, i, h.getFood()));
									} else if (h.hasCritter()) {
										Critter critter = h.getCritter();
										int sessionId = Integer.parseInt(request.queryParams("session_id"));
										
										if (sessionId == critter.getSessionId() || sessions.get(sessionId) == Level.admin) {
											state.add(new CritterInfo(critter.getId(),
													critter.getSpecies(), critter.getRow(), critter.getCol(),
													critter.getDirection(), critter.getMem(), critter.getProgram(), 
													critter.getLastRule(), Type.critter));
										}
										else {
											state.add(new CritterInfo(critter.getId(),
													critter.getSpecies(), critter.getRow(), critter.getCol(),
													critter.getDirection(), critter.getMem(), Type.critter));
										}
									} else {
										if (update_since > 0)
											state.add(new NothingInfo(j, i));
									}
								}
							}
						}
						
						lock.readLock().unlock(); //Read unlock
						WorldInfo w = new WorldInfo(world.getNumsteps(), world.getVersion() + 1, update_since, world.getRate(),
									world.getName(), world.getCritters().size(), world.getRows(), world.getColumns(),
									ids, state);
						return w;
					} else {
						return error401();
					}
				},
			gson::toJson);
		
		/**
		 * Write locked action. (Still Implementing)
		 * 
		 * Creates either a rock or a critter entity at {@code col} and {@code row}.
		 * Cannot take both a 
		 */
		post("/create_entity",
			(request, response)
				-> {
					if (checkSessionId(request, Level.write)) {
						response.header("Content-Type", "application/json");
						String json = request.body();
						GenInfo gen = gson.fromJson(json, GenInfo.class);
						
						if (gen == null || (gen.type() == Type.rock && gen.value() != 0)) {
							return error400();
						}
						else if (!world.getHex(gen.col(), gen.row()).isEmpty()) {
							return error406();
						}
						
						lock.writeLock().lock(); //Write lock
						
						if (gen.type() == Type.food) {
							FoodInfo food = new FoodInfo(gen.row(), gen.col(), gen.value());
							Hex h = world.getHex(food.col(), food.row());
							h.addFood(food.value());
							world.getFood().add(h);
							world.incVersion();
							lock.writeLock().unlock(); //Write unlock
							return "Ok";
						}
						else if (gen.type() == Type.rock) {
							RockInfo rock = new RockInfo(gen.row(), gen.col());
							Hex h = world.getHex(rock.col(), rock.row());
							h.setRock();
							world.getRocks().add(h);
							world.incVersion();
							lock.writeLock().unlock(); //Write unlock
							return "Ok";
						}
						else {
							lock.writeLock().unlock(); //Write unlock
							return error400();
						}
					} else {
						return error401();
					}
				},
			gson::toJson);
		
		/**
		 * Write locked action.
		 * 
		 * Step the world {@code count} times. If the count is not specified in the 
		 * body of the request, it will step once. The world cannot step if it is currently running.
		 */
		post("/step",
			(request, response)
				-> {
					if (checkSessionId(request, Level.write)) {
						lock.readLock().lock(); //Read lock
						
						response.header("Content-Type", "application/json");
						String json = request.body();
						Step step = gson.fromJson(json, Step.class);
						if (step == null) {
							step = new Step();
						}
						else if (step.count() < 0 || world.getRate() > 0) {
							lock.readLock().unlock(); //Read unlock
							return error406();
						}
						
						lock.readLock().unlock(); //Read unlock
						lock.writeLock().lock(); //Write lock
						world.advanceTime(step.count());
						lock.writeLock().unlock(); //Write unlock
						return "Ok";
					} else {
						return error401();
					}
				},
			gson::toJson);
		
		/**
		 * Write locked action.
		 * 
		 * Run the world continuously at a rate of x times per second. Takes in only the argument
		 * the argument rate, which is a float.
		 * 
		 * Returns the same rate that was sent in the request.
		 */
		post("/run",
			(request, response)
				-> {
					if (checkSessionId(request, Level.write)) {
						
						response.header("Content-Type", "application/json");
						String json = request.body();
						Rate rate = gson.fromJson(json, Rate.class);
						if (rate == null || rate.rate() < 0) {
							lock.writeLock().unlock(); //Write unlock
							return error406();
						}

						lock.writeLock().lock(); //Write lock
						world.run(rate.rate());
						lock.writeLock().unlock(); //Write unlock
						return rate;
					} else {
						return error401();
					}
				},
			gson::toJson);
	}
	
	/**
	 * Method to check the session id passed through the url by the client to check for privileges.
	 * @param request The request passed into the server through the client.
	 * @param level The level of access required for the user to process this action.
	 * @return Whether or not the user has the authorization for this request.
	 */
	private boolean checkSessionId(Request request, Level level) {
		try {
			int sessionId = Integer.parseInt(request.queryParams("session_id"));
			Level sessionLevel = sessions.get(sessionId);
			switch (level) {
			case read:
				if (sessionLevel != null ) { return true; }
			case write:
				if (sessionLevel == Level.write || sessionLevel == Level.admin) {return true;}
			case admin:
				if (sessionLevel == Level.admin) {return true;}
			}
			return false;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Method handler for Error 406 Not Acceptable problems.
	 * 
	 * The request contains unacceptable values for the specific request.
	 * 
	 * @return String message for Error 406 problems.
	 */
	private String error406() {
		return "Error code 406. This is not an acceptable value to pass in this request.";
	}
	
	/**
	 * Method handler for Error 401 Authentication problems.
	 * 
	 * The request requires user authentication.
	 * 
	 * @return String message for Error 401 problems.
	 */
	private String error401() {
		return "Error code 401. You do not have the valid permissions to do this request";
	}
	
	/**
	 * Method handler for Error 400 Bad Request problems.
	 * 
	 * The request could not be understood by the server due to malformed syntax.
	 * The client SHOULD NOT repeat the request without modifications.
	 * 
	 * @return String message for Error 400 problems.
	 */
	private String error400() {
		return "Error code 400. The request sent by the client was syntactically incorrect.";
	}
	
	
}