package console;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import console.DummyClassLibrary.*;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import parse.Parser;
import parse.ParserFactory;

import com.google.gson.Gson;

import ast.Program;
import ast.Rule;

public class CritterWorldController{

	/**
	 * Pixel size of the side of a hexaaon
	 */
	private int hexSize = 50; 
	/**
	 * The side length of the hexagon
	 */
	private int sideLength;
	/**
	 * The height of the hexagon in terms of top to bottom
	 */
	private int height;
	/**
	 * The radius of the circle transcribed within the hexagon
	 */
	private int radiusOfCircle;
	/**
	 * The height of the 30, 60, 90 triangle created by the edges of the hexagon
	 */
	private int heightOfSideTriangle;
	/**
	 * Canvas on which to draw the world tiles
	 */
	@FXML
	private Canvas hexCanvas; 
	/**
	 * Button to prompt user to enter a file
	 */
	@FXML
	private Button loadWorldButton;
	/**
	 * Button that creates a new World for user
	 */
	@FXML
	private Button newWorldButton;
	/**
	 * The critterWorld created
	 */
	private World world;
	
	/**
	 * Button that reads a critter file and adds a critter to the world
	 */
	@FXML
	private Button loadCritterButton; 
	/**
	 * Label that provides some information about the world
	 */
	@FXML
	private Label worldInfo;
	/**
	 * Button to zoom in
	 */
	@FXML
	private Button zoomIn;
	/**
	 * Button to zoom out
	 */
	@FXML
	private Button zoomOut;
	/**
	 * The scrollpane representing the critter world
	 */
	@FXML
	private ScrollPane worldPane; 
	
	@FXML
	private Group zoomgroup;
	/**
	 * Button allowing user to run the simulation one step at a time
	 */
	@FXML
	private Button stepButton;
	/**
	 * Button allowing user to stop the simulation
	 */
	@FXML
	private Button pauseButton;
	/**
	 * Button allowing user to run the simulation continuously
	 */
	@FXML
	private Button simulateButton;
	
	/**
	 * CheckBox that determines how the user wants to add critters to the World
	 */
	@FXML
	private CheckBox placeCritterToggle;
	/**
	 * Pressing the button will reset the current loaded world, essentially reloads the current file
	 */
	@FXML
	private Button refreshWorld;
	/**
	 * Designates the way the user would like to place the critters. If this is true, the critters will be
	 * randomly placed. If false, the critter locations will be chosen by the user.
	 */
	private boolean critterPlacement;
	/**
	 * True or false depending on whether or not the user is currently placing a critter on the world.
	 */
	private boolean placingCritter;
	/**
	 * 
	 */
	private int crittersToBePlaced;
	/**
	 * 
	 */
	private File critterFile;
	/**
	 * Image of the rock
	 */
	private Image rockImg;
	/**
	 * Image of food
	 */
	private Image foodImg;
	/**
	 * Holds the image of critters
	 */
	private Image critImg;
	/**
	 * Used to draw objects in the {@code hexCanvas}
	 */
	GraphicsContext gc;
	/**
	 * Label for critter number
	 */
	@FXML
	private Label critterNumLabel;
	/**
	 * Label for steps
	 */
	@FXML
	private Label stepLabel;
	/**
	 * Displays number of critters in the world
	 */
	@FXML
	private Label numberOfCritters;
	/**
	 * Displays number of steps the world has gone through
	 */
	@FXML
	private Label numberOfSteps; 
	
	private URL url;

	
	/**
	 * Timer used to track the simulation
	 */
	private Timeline timer; 
	/**
	 * File that holds the last loaded world file
	 */
	private File worldFile;
	/**
	 * Holds the http, username, and password used to access the server
	 */
	private String[] information;
	/**
	 * The sessionID given by the server to a client
	 */
	private int sessionID;
	/**
	 * Gson object serialize objects for communication with the server
	 */
	private Gson gson;
	/**
	 * Used to connect with the server
	 */
	private HttpURLConnection connection;
	/**
	 * Holds the address for connection to the server
	 */
	private URL serverAddress;
	/**
	 * Used to write JSON strings to the server
	 */
	private PrintWriter writer;
	/**
	 * Helps process JSON strings from the server
	 */
	private StringBuilder builder;
	/**
	 * Helps read output from the server
	 */
	private BufferedReader reader;
	
	@FXML 
	void initialize() {
		gson = new Gson();
		boolean invalidHTTP = true;
		builder = new StringBuilder();
		/**
		 * Connect to a server
		 * If the server is invalid, the user is forced to retry
		 */
		while (invalidHTTP) {
			information = MenuScreen.openMenuScreen();
			/**
			 * Send a login request to the server based on information given through the menu screen
			 */
			try {
				serverAddress = new URL(information[0] + "/login");
				connection = (HttpURLConnection) serverAddress.openConnection();
				Login userLogin = new Login(information[1], information[2]);
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				writer = new PrintWriter(connection.getOutputStream());
				String login = gson.toJson(userLogin);
				writer.println(login);
				writer.flush();
				reader = new BufferedReader
						(new InputStreamReader(connection.getInputStream()));
				
				while(reader.ready()) {
					builder.append(reader.readLine());
				}
				Session loginSession = gson.fromJson(builder.toString(), Session.class);
				builder.setLength(0);
				sessionID = loginSession.getSessionID();
				invalidHTTP = false;
				
			} 	catch(MalformedURLException m) {
				Alert.display("Malformed URL", "You provided an invalid URL.");
				m.printStackTrace();
			}	catch(IOException e) {
				Alert.display("Invalid Connection", "Could not connect to the provided server address.");
				e.printStackTrace();
			} 
		}
		
		
		worldInfo.setText("Load a world file to begin.");
		worldPane.setPannable(true);
		worldPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		worldPane.setFitToWidth(true);
		worldPane.setFitToHeight(true);
		world = null; 
		gc = hexCanvas.getGraphicsContext2D();
		rockImg = new Image(Main.class.getResourceAsStream("rock.png"));
		foodImg = new Image(Main.class.getResourceAsStream("food.png"));
		critImg = new Image(Main.class.getResourceAsStream("critter.png"));
		
		Stage fileDialogue = new Stage(); 
		FileChooser fileChooser = new FileChooser();
		 
		
		intializeHex();
		
		
		
		/**
		 * Set the labels for number of critters and number of steps
		 */
		critterNumLabel.setText("Critters: ");
		stepLabel.setText("Steps: ");
		
		/**
		 * Starts getting information about the server side world 
		 * in order to update the World 
		 */
		timer = new Timeline(new KeyFrame(Duration.millis(33.3), 
				ae -> this.critterWorldUpdate()));
		timer.setCycleCount(Animation.INDEFINITE);
		timer.play();
		/**
		 * If the critter toggle is checked, then the user is placing their critters
		 * Else the critters are placed randomly
		 */
		placeCritterToggle.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				if (critterPlacement) {
					critterPlacement = false;
				}
				else {
					critterPlacement = true;
				}
			}
		});
		/**
		 * If the loadWorldButton is pressed, a file chooser pops up and the user can 
		 * select a world file to be used for the Critter Simulation
		 */
		loadWorldButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				worldFile = fileChooser.showOpenDialog(fileDialogue);
				if (worldFile != null) {
					try {
						serverAddress = new URL(information[0] + "/world?session_id=" + sessionID);
						connection = (HttpURLConnection) serverAddress.openConnection();
						connection.setDoOutput(true);
						connection.setRequestProperty("Content-Type", "application/json");
						connection.setRequestMethod("POST");
						
						writer = new PrintWriter(connection.getOutputStream());
						String serverWorld = new String(Files.readAllBytes(Paths.get(worldFile.getAbsolutePath())));
						Description sendWorld = new Description(serverWorld);
						String gsonWorld = gson.toJson(sendWorld);
						writer.println(gsonWorld);
						writer.flush();
						worldInfo.setText("World successfully loaded.");
						reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					} catch(NullPointerException n) {
						Alert.display("File Error", "The world file you provided had invalid syntax. \n"
								+ "The world file was not loaded.");
					} catch (FileNotFoundException n1) {
						Alert.display("File Error", "The world file you provided could not be found \n"
								+ "The world file was not loaded.");
						n1.printStackTrace();
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						Alert.display("Invalid Connection", "Could not connect to the provided server address.");
						e1.printStackTrace();
					}
				}
			}
		});
		
		/**
		 * If the newWorldButton is pressed, a new default world is generated
		 */
		newWorldButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				try {
					
				Description	sendWorld = new Description("name default\nsize " + Constants.COLUMNS() + " " + Constants.ROWS());
				serverAddress = new URL(information[0] + "/world?session_id=" + sessionID);
				connection = (HttpURLConnection) serverAddress.openConnection();
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestMethod("POST");
				
				writer = new PrintWriter(connection.getOutputStream());
				String gsonWorld = gson.toJson(sendWorld);
				writer.println(gsonWorld);
				writer.flush();
				worldInfo.setText("World successfully loaded.");
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				} catch(IOException e1) {
					Alert.display("Error", "Problem loading the world, check connection to server.");
					e1.getStackTrace();
				}
			}
		});
		
		/**
		 * loadCritterButton is initialized to prompt user for a critter file to be 
		 * loaded into the world 
		 */
		loadCritterButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				if (world == null) {
					Alert.display("Error", "You have not yet initialized a critter world!!!");
				}
				else {
					Integer n = Alert.prompt("Enter number of critters", "Get File");
					if (n <= 0) {
						Alert.display("Error", "You did not input a valid number.");
						return;
					}
					File file = fileChooser.showOpenDialog(fileDialogue);
					if (file != null) {
						try {
							if(critterPlacement) {
								serverAddress = new URL(information[0] + "/critters?session_id=" + sessionID);
								connection = (HttpURLConnection) serverAddress.openConnection();
								connection.setDoOutput(true);
								connection.setRequestProperty("Content-Type", "application/json");
								connection.setRequestMethod("POST");
							
								writer = new PrintWriter(connection.getOutputStream());
								AddCritter critterToAdd = processCritterFile(file.getAbsolutePath(), -1, -1, true, n);
								writer.println(gson.toJson(critterToAdd));
								writer.flush();
								reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
								worldInfo.setText("Critter successfully loaded.");
							} else {
								critterFile = file;
								placingCritter = true;
								crittersToBePlaced = n;
								worldInfo.setText("Click " + n + " tiles to place the critter(s). To stop, press escape.");
							}
						}
						catch(AssertionError a) {
							Alert.display("Error", "The critter file you provided contained invalid syntax. \n"
									+ "No critter was loaded. ");
						} catch (IOException e1) {
							Alert.display("Invalid Connection", "Could not connect to the provided server address.");
							e1.printStackTrace();
						}
					}
					
				}
			}
		});
		/**
		 * Runs the simulation for 1 step
		 */
		stepButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(final ActionEvent e) {
				if (world == null) {
					Alert.display("Error", "You have not yet initialized a critter world!!!");
					return;
				}
				stepServer(1);
			}
		});
		
		/**
		 * Zooms in on the world
		 */
		zoomIn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				if (world == null) {
					Alert.display("Error", "You have not yet initialized a critter world!!!");
					return;
				}
				hexSize += 3;
				intializeHex();
				updateWorld();
				
				if (hexSize > 80) {
					worldInfo.setText("You are at the maximum zoom.");
					zoomIn.setDisable(true);
				}
				if (zoomOut.isDisable())
					zoomOut.setDisable(false);
			}
		});
		
		/**
		 * Zooms out on the world
		 */
		zoomOut.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public synchronized void handle(final ActionEvent e) {
				if (world == null) {
					Alert.display("Error", "You have not yet initialized a critter world!!!");
					return;
				}
				hexSize -= 3;
				intializeHex();
				updateWorld();
				
				if (hexSize < 20) {
					worldInfo.setText("You are at the maximum zoom.");
					zoomOut.setDisable(true);
				}
				if (zoomIn.isDisable())
					zoomIn.setDisable(false);
			
			}
		});
		/**
		 * Runs the simulation at a user specified times per second
		 */
		simulateButton.setOnAction(new EventHandler<ActionEvent>(){
			
			@Override
			public void handle(final ActionEvent e) {
				
				if (world == null) {
					Alert.display("Error", "You have not yet initialized a critter world!!!");
					return;
				}
				int simSpeed = Alert.prompt("Enter the Simulation Speed", "Close");
				if (simSpeed < 0) {
					Alert.display("Error", "Criter world cannot update negative times per second");
				} else {
					setRate(simSpeed);
					worldInfo.setText("Server Running at " + simSpeed + " per second.");
				}
			}
		});

		/**
		 * If a simulation is running, it is paused 
		 */
		pauseButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				setRate(0);
				worldInfo.setText("Server Paused");
			}
		});
		/**
		 * Refreshes the world based on the last world loaded
		 */
		refreshWorld.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				Description sendWorld = null;
				if (world == null) {
					Alert.display("Error", "You have not yet initialized a critter world!!!");
					return;
				}
				if (worldFile == null) {
					worldInfo.setText("No world loaded prior, created default world.");
					sendWorld = new Description("name default\nsize " + Constants.COLUMNS() + " " + Constants.ROWS());
					
				}
				else {
					String serverWorld;
					try {
						serverWorld = new String(Files.readAllBytes(Paths.get(worldFile.getAbsolutePath())));
						sendWorld = new Description(serverWorld);
					} catch (IOException e1) {
						Alert.display("Error", "Could not get the world file requested.");
						e1.printStackTrace();
					}
				}
				try {
					serverAddress = new URL(information[0] + "/world?session_id=" + sessionID);
					connection = (HttpURLConnection) serverAddress.openConnection();
					connection.setDoOutput(true);
					connection.setRequestProperty("Content-Type", "application/json");
					connection.setRequestMethod("POST");
					writer = new PrintWriter(connection.getOutputStream());
					String gsonWorld = gson.toJson(sendWorld);
					writer.println(gsonWorld);
					writer.flush();
					worldInfo.setText("Prior world successfully loaded.");
					reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				} catch(IOException e1) {
					Alert.display("Invalid Connection", "Could not connect to the provided server address.");
					e1.getStackTrace();
				}

			}
		});
		/**
		 * Returns the information in the hex that is clicked by the user
		 */
		hexCanvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (world == null) {
					Alert.display("Error", "You have not yet initialized a critter world!!!");
					return;
				}
				if (placingCritter) {
					int x = (int) event.getX();
					int y = (int) event.getY();
					int mX = (int) (x - radiusOfCircle / 3.5) / (heightOfSideTriangle + sideLength);
					int mY = (int) (((hexCanvas.getHeight() - ((mX) % 2) * (height / 2) - y) / height)
							+ ((mX + 1) / 2));
					if (world.getHex(mX, mY) == null) {
						Alert.display("Out of Bounds Placement", "You clicked out of the world! Place the critter in the world.");
						return;
					}
					else if (!world.getHex(mX, mY).isEmpty()) {
						
						Alert.display("Occupied Hex", "This hex already has a something on it.");
						return;
					}
					if (crittersToBePlaced > 0) {
						try {
							serverAddress = new URL(information[0] + "/critters?session_id=" + sessionID);
							connection = (HttpURLConnection) serverAddress.openConnection();
							connection.setDoOutput(true);
							connection.setRequestProperty("Content-Type", "application/json");
							connection.setRequestMethod("POST");
						
							writer = new PrintWriter(connection.getOutputStream());
							AddCritter critterToAdd = processCritterFile(critterFile.getAbsolutePath(), mX, mY, false, -1);
							String critterToAddJson = gson.toJson(critterToAdd).replaceAll(",\"num\":0", "");
							writer.println(critterToAddJson);
							writer.flush();
							reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
							CrittersAdded c = gson.fromJson(reader, CrittersAdded.class);
							if (c.ids()[0] == -1) {
								Alert.display("Error", "Could not place a Critter at that hex.");
								return;
							}
							crittersToBePlaced--;
							worldInfo.setText("Click " + crittersToBePlaced + " tiles to place the critter(s). To stop, press escape.");
						} catch(IOException e) {
							Alert.display("Invalid Connection", "Could not connect to the provided server address.");
							e.printStackTrace();
						}
					}
					if (crittersToBePlaced <= 0) {
						placingCritter = false;
						worldInfo.setText("You have placed all the critters.");
					}
				}
				else {
					int x = (int)event.getX();
					int y = (int)event.getY();
					int mX = (int) (x - radiusOfCircle / 3.5 ) / (heightOfSideTriangle + sideLength); 
					int mY = (int) (((hexCanvas.getHeight() - ((mX) % 2) * (height/ 2) - y) / height) + ((mX+1)/2));
					Hex hex = world.getHex(mX, mY);
					String textToDisplay = "";
					
					
					if (hex != null) {
						if (hex.hasCritter()) {
							Critter c = world.getHex(mX, mY).getCritter();
							if (c.getLastRule() != null) {
								textToDisplay = critterInfo(c.getSpecies(), c.getMem(), c.getAst(), c.getLastRule().toString());
							}
							else {
								textToDisplay = critterInfo(c.getSpecies(), c.getMem(), c.getAst(), "Critter did not execute anything.");
							}
						}
						else if (hex.hasRock()) {
							textToDisplay = terrainInfo(Constants.ROCK_VALUE());
						}
						else if (hex.hasFood()) {
							textToDisplay = terrainInfo(-hex.getFood() - 1);
						}
						else {
							textToDisplay = terrainInfo(0);
						}
						Alert.display("Hex Info", textToDisplay);
					}
				}
			}
		});
		
		
	}
	/**
	 * If the user presses escape while placing critters, 
	 * they will no longer have to continue placing critters
	 * while interacting with tiles
	 * @param event user action
	 */
	@FXML
    void keyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ESCAPE && placingCritter) {
			worldInfo.setText("You are no longer placing critters.");
			placingCritter = false;
		}
    }
	
	/**
	 * Helper method that initializes  the dimensions of a hexagon to be drawn
	 */
	private void intializeHex() {
		sideLength = hexSize;
		radiusOfCircle = (int)(hexSize * 0.8660254037844); 
		height = radiusOfCircle * 2; 
		heightOfSideTriangle = (int)(hexSize / 2);
	}

	/**
	 * Draw the critter world based on the given size
	 * @param x the width of the critter world
	 * @param y the height of the critter world
	 */
	private void drawWorld(int x, int y) {
		hexCanvas.setWidth(x * (sideLength + heightOfSideTriangle) + heightOfSideTriangle); 
		hexCanvas.setHeight(y * height + radiusOfCircle);
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				drawHex(i, j); 
			}
		}

	}
	/**
	 * Takes the coordinates of the hexes in the model representation and converts it to 
	 * coordinates of the GUI pane
	 * Draws a hexagon at that location 
	 * @param i The x coordinate of the hexagon in the grid
	 * @param j The y coordinate of the hexagon in the grid
	 */
	private void drawHex(int i, int j) {
		int x = i * (sideLength + heightOfSideTriangle);
		int y = j * (height) + ((i + 1) % 2) * (height/ 2);
		
		double[] xC, yC;
		xC = new double[] { x + heightOfSideTriangle,
				x + sideLength + heightOfSideTriangle, 
				x + sideLength + heightOfSideTriangle + heightOfSideTriangle,
				x + sideLength + heightOfSideTriangle,
				x + heightOfSideTriangle,
				x };

		yC = new double[] { y,
				y,
				y + radiusOfCircle,
				y + radiusOfCircle + radiusOfCircle,
				y + radiusOfCircle + radiusOfCircle,
				y + radiusOfCircle }; 
		gc.setStroke(Color.BLACK);
		gc.strokePolygon(xC, yC, 6);
		gc.setFill(Color.ANTIQUEWHITE);
		gc.fillPolygon(xC, yC, 6);		
		
	}
	
	/**
	 * Draws the critters that are loaded in through world and place them on their corresponding 
	 * hex tile
	 */
	private void drawHexInfo() {
		
		for (Critter c : world.getCritters()) {
			int dir = c.getDirection();
			int col = c.getCol();
			int h = (c.getRow() - ((col + 1) / 2));
			double x = col * (sideLength + heightOfSideTriangle) + ((sideLength + heightOfSideTriangle)/2);
			double y = hexCanvas.getHeight() - radiusOfCircle - ((col) % 2) * (height/ 2) - (h * height);
			gc.strokeText("" + dir, x, y);
			gc.drawImage(critImg, x - (sideLength)/4, y - radiusOfCircle/2, 
					sideLength, sideLength);
		}
		for (Hex h : world.getRocks()) {
			int col = h.getColumn();
			int high = (h.getRow() - ((col + 1) / 2));
			double x = col * (sideLength + heightOfSideTriangle) + ((sideLength + heightOfSideTriangle)/2);
			double y = hexCanvas.getHeight() - radiusOfCircle - ((col) % 2) * (height/ 2) - (high * height);
			gc.drawImage(rockImg, x - (sideLength + heightOfSideTriangle)/2, y - radiusOfCircle, 
					(heightOfSideTriangle*2)+sideLength, radiusOfCircle+radiusOfCircle);
		}
		for (Hex h: world.getFood()) {
			int col = h.getColumn();
			int high = (h.getRow() - ((col + 1) / 2));
			double x = col * (sideLength + heightOfSideTriangle) + ((sideLength + heightOfSideTriangle)/2);
			double y = hexCanvas.getHeight() - radiusOfCircle - ((col) % 2) * (height/ 2) - (high * height);
			gc.drawImage(foodImg, x - (sideLength)/4, y - radiusOfCircle/2, 
					sideLength, sideLength);
		}
	}
	
	/**
	 * Updates the world by redrawing hexes based on {@code world}
	 */
	private void updateWorld() {
		
		gc.clearRect(0, 0, hexCanvas.getWidth(), hexCanvas.getHeight());
		drawWorld(world.getColumns(), world.getHeight());
		drawHexInfo();
		numberOfCritters.setText("" + world.getCritters().size());
		
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
    protected String critterInfo(String species, int[] mem, String program, String lastrule) {
    		StringBuilder info = new StringBuilder();
        info.append("Species: " + species + "\n");
        StringBuilder sbmem = new StringBuilder();
        for (int i : mem) {
            sbmem.append(" ").append(i);
        }
        info.append("Memory:" + sbmem.toString() + "\n");
        info.append("Program: " + program);
        info.append("Last rule: " + lastrule);
        return info.toString();
    }
    /**
     *
     * @param terrain 0 is empty, -1 is rock, -X is (X-1) food
     */
    protected String terrainInfo(int terrain) {
    		if (terrain == -1) {
            return "Rock";
        } else if (terrain < -1){
            return("Food: " + (-terrain - 1));
        }
        else {
            return "Empty";
        } 
        
    }
    
    /**
     * Initiates a http get world info request to the stored server address
     * @return a WorldInfo object containing all information from the get world request
     */
    private WorldInfoForGet getWorldInfo() {
    		WorldInfoForGet info = null;
    			try {
    				if (world == null) {
    					serverAddress = new URL(this.information[0] + "/world?session_id=" + sessionID);
    				}
    				else {
    					serverAddress = new URL(this.information[0] + "/world?session_id=" + sessionID + "&update_since=" + world.getVersion());
    				}
				connection = (HttpURLConnection) serverAddress.openConnection();
				connection.connect();
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				info = gson.fromJson(reader, WorldInfoForGet.class);
				//System.out.println(gson.toJson(info));
			} catch (MalformedURLException e) {
				System.out.println("Invalid URL to connect to the server");
				e.printStackTrace();
			} catch (IOException e) {
				Alert.display("Invalid Connection", "Could not connect to the provided server address.");
				e.printStackTrace();
			} 
    		return info;
    }
    /**
     * Advances the world by {@code stepToTake}
     * @param stepToTake amount of steps to advance the server world by
     */
    private void stepServer(int stepToTake) {
    		try {
    			serverAddress = new URL(information[0] + "/step?session_id=" + sessionID);
    			connection = (HttpURLConnection) serverAddress.openConnection();
    			connection.setRequestProperty("Content-Type", "application/json");
    			connection.setDoOutput(true);
    			connection.setRequestMethod("POST");
    			writer = new PrintWriter(connection.getOutputStream());
    			Step step = new Step(stepToTake);
    			writer.println(gson.toJson(step));
    			writer.flush();
    			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    		} 
    		catch(IOException e){
    			Alert.display("Invalid Connection", "Could not connect to the provided server address.");
    			e.printStackTrace();
    		}
    }
    /**
     * Advances the world continuously by {@code rate}
     * @param rate speed to step the world continuously at
     */
    private void setRate(int rateAmount) {
    		try {
    			serverAddress = new URL(information[0] + "/run?session_id=" + sessionID);
    			connection = (HttpURLConnection) serverAddress.openConnection();
    			connection.setDoOutput(true);
    			connection.setRequestProperty("Content-Type", "application/json");
    			connection.setRequestMethod("POST");
    			writer = new PrintWriter(connection.getOutputStream());
    			Rate rate = new Rate(rateAmount);
    			writer.println(gson.toJson(rate));
    			writer.flush();
    			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    		}
    		catch (IOException e) {
    			Alert.display("Invalid Connection", "Could not connect to the provided server address.");
    			e.printStackTrace();
    		}
    }
    
    
     
	 /**
	  * Helps process the Critter File and converts it into a serializable 
	  * AddCritter object
	  * @param path file path of the critter file 
	  * @param col of the critter
	  * @param row of the critter
	  * @param random if the critter is being placed randomly
	  * @param num the number of critters placed if being placed randomly
	  * @return dummy object that is easily serializable with json to send to the server
	  */
    private AddCritter processCritterFile(String path, int col, int row, boolean random, int num) {
    		Scanner critScan = new Scanner(path); 
    		
    		String name = "";
    		
    		try {
			critScan = new Scanner(new FileInputStream(path));
			if (!critScan.next().equals("species:")) {
	        	name = "critter";
	        }
	    	else {
	    		name = critScan.nextLine().trim();
	    		}
		} catch (FileNotFoundException e) {
			Alert.display("Error", "Cannot find critter file specified.");
			critScan.close();
			return null;
		}
    		int[] critMem = readCritMem(critScan);
    		Parser p = ParserFactory.getParser();
        	while (critScan.hasNextLine()) {
        		builder.append(critScan.nextLine() + "\n");
        	}
        	reader = new BufferedReader(new StringReader(builder.toString()));
        	String program = builder.toString();
        	builder.setLength(0);
        	Position[] position = {new Position(row, col)};
        	
        	if (random == true) {
        		return new AddCritter(name, program, critMem, num);
        	}
        	else {
        		return new AddCritter(name, program, critMem, position);
        	}
    }
    
    /**
     * Helper method to process critter memory from file
     * @param critScan used to read from critter file
     * @return an array representing the critter memory
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
     * Constantly pulls from the server and takes the information from the server to be processed
     * to update the client-side world
     */
    private void critterWorldUpdate() {
    		WorldInfoForGet info = getWorldInfo();
    		ArrayList<GeneralInfo> states = info.getStates();
    		
    	
    		if ( world == null || (info.getCols() != world.getColumns() || info.getRows() != world.getRows())) {
    			world = new World(info.getCurrent_timestep(), info.getCurrent_version_number(), 
    					info.getRows(), info.getCols(), info.getName());
    		}
    		else {
    			world.setTimeStep(info.getCurrent_timestep());
    			world.setVersion(info.getCurrent_version_number());
    			numberOfSteps.setText("" + world.getNumsteps());
    			
    		}
    		for (int i = 0; i < states.size(); i++) {
    			if(states.get(i).getType() == Type.critter) {
    				StringReader programReader = new StringReader(states.get(i).getProgram());
    				Rule rule = null;
    				Program program = ParserFactory.getParser().parse(new BufferedReader(programReader));
    				if (states.get(i).getRecently_executed_rule() > -1) {
    					rule = program.get(states.get(i).getRecently_executed_rule());
    				}
    				Critter newCrit = new Critter(program, states.get(i).getMemory(), states.get(i).getSpecies_id(),
    						states.get(i).getDirection(), states.get(i).getCol(), states.get(i).getRow(), world, rule);
    				world.placeCritter(states.get(i).getRow(), states.get(i).getCol(),newCrit);
    			}
    			else if(states.get(i).getType() == Type.food){
    				world.placeFood(states.get(i).getRow(), states.get(i).getCol(), states.get(i).getValue());
    			}
    			else if(states.get(i).getType() == Type.nothing) {
    				world.placeEmpty(states.get(i).getRow(), states.get(i).getCol());
    			}
    			else {
    				world.placeRock(states.get(i).getRow(), states.get(i).getCol());
    			}
    		}
    		if (states.size() > 0 || info.getUpdate_since() == 0) {
    			this.updateWorld();
    		}
    }
   
}
	

