package console;


import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MenuScreen {
	
	@FXML
	private Label titleLabel;
	@FXML
	private TextField addressTextField;
	@FXML
	private TextField passwordTextField;
	@FXML 
	private TextField usernameTextField;
	@FXML
	private Label addressLabel;
	@FXML
	private Label passwordLabel;
	@FXML 
	private Label usernameLabel; 
	@FXML
	private Button startButton;
	/**
	 * String array that stores the http URL, username, and password entered by 
	 * the user
	 */
	private static String[] information;
	private static Stage mainStage;
	@FXML
	void initialize(){
		information = new String[3];
		addressTextField.appendText("http://hexworld.herokuapp.com/hexworld");
		usernameTextField.appendText("admin");
		passwordTextField.appendText("gandalf");
	}
	
	/**
	 * Launches the menu screen for the critter program
	 * @return an array of strings containing the http url, username, and password
	 * to access the server
	 */
	public static String[] openMenuScreen() {
		mainStage = new Stage(); 
	
		final URL r = MenuScreen.class.getResource("critterMenu.fxml");
		try {
			final Parent node = FXMLLoader.load(r);
			final Scene scene = new Scene(node); 
			mainStage.setTitle("Menu");
			mainStage.setResizable(true);
			mainStage.initModality(Modality.APPLICATION_MODAL);
			mainStage.setScene(scene);
			mainStage.setOnCloseRequest(e -> System.exit(0));
			mainStage.showAndWait();
		} catch (IOException e) {
			System.out.println("Can't load the FXML file");
			e.printStackTrace();
		}
		return information; 
	}
	
	/**
	 * Stores the text in {@code addressTextField}, {@code usernameTextField}, {@code passwordTextField}
	 * to the string array {@code information}
	 */
	public void startButtonPressed() {
		information[0] = addressTextField.getText();
		information[1] = usernameTextField.getText();
		information[2] = passwordTextField.getText();
		mainStage.close();
	}
}
