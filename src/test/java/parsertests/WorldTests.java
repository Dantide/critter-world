package parsertests;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Test;

import console.Constants;
import console.World;
import exceptions.SyntaxError;

public class WorldTests {
	
	/*
	 * Tests the constants class
	 */
	@Test
	public void testConstants() throws FileNotFoundException {
		//Constants c = new Constants(WorldTests.class.getResourceAsStream("constants.txt"));
		//Constants Constants = Constants.getInstance();
		//assertNotNull(Constants);
		assertTrue(Constants.BASE_DAMAGE() == 100);
		assertTrue(Constants.DAMAGE_INC() == 0.2);
		assertTrue(Constants.ENERGY_PER_SIZE() == 500);
		assertTrue(Constants.FOOD_PER_SIZE() == 200);
		assertTrue(Constants.MAX_SMELL_DISTANCE() == 10);
		assertTrue(Constants.ROCK_VALUE() == -1);
		assertTrue(Constants.COLUMNS() == 50);
		assertTrue(Constants.ROWS() == 68);
		assertTrue(Constants.MAX_RULES_PER_TURN() == 999);
		assertTrue(Constants.SOLAR_FLUX() == 1);
		assertTrue(Constants.MOVE_COST() == 3);
		assertTrue(Constants.ATTACK_COST() == 5);
		assertTrue(Constants.GROW_COST() == 1);
		assertTrue(Constants.BUD_COST() == 9);
		assertTrue(Constants.MATE_COST() == 5);
		assertTrue(Constants.RULE_COST() == 2);
		assertTrue(Constants.ABILITY_COST() == 25);
		assertTrue(Constants.INITIAL_ENERGY() == 250);
		assertTrue(Constants.MIN_MEMORY() == 8);
	}                
	
	/*
	 * Tests the creation of a World based of a world.txt
	 */
	//@Test
	public void testCreation() throws SyntaxError {
		World w = new World("C:\\Users\\ikmuj\\Desktop\\a4\\team-mf647-ssc248-cw\\examples\\world.txt");
		assertNotNull(w);
		assertTrue(w.getRows() == 15);
		assertTrue(w.getColumns() == 10);
		assertTrue(w.getName().equals("Small world"));
		assertTrue(w.getCritters().size() == 3);
		
		assertTrue(w.getHex(2, 2).hasRock());
		assertTrue(w.getHex(3, 6).hasRock());
		assertTrue(w.getHex(9, 10).hasRock());
		
		assertTrue(w.getHex(4, 4).getFood() == 500);
		assertTrue(w.getHex(1, 3).getFood() == 1000);
		
		assertNotNull(w.getHex(2, 5).getCritter());
		assertNotNull(w.getHex(4, 3).getCritter());
		assertNotNull(w.getHex(4, 4).getCritter());
		
		assertTrue(w.getHex(2, 5).getCritter().getDirection() == 3);
		assertTrue(w.getHex(4, 3).getCritter().getDirection() == 1);
		assertTrue(w.getHex(4, 4).getCritter().getDirection() == 2);
		
		w.printMap();
	}
	
	/*
	 * Test the default constructor
	 */
	//@Test
	public void testDefault() {
		World w = new World();
		assertNotNull(w);
		assertTrue(w.getRows() == Constants.ROWS());
		assertTrue(w.getColumns() == Constants.COLUMNS());
		assertTrue(w.getName().equals("Default World"));
		w.printMap();
	}
}
