package console;

import java.io.File;
import java.util.function.UnaryOperator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Used to display an error message to the user
 * @author mikefang
 *
 */
public class Alert {
	
	public static void display(String title, String message) {
		/**
		 * Initialize the label node that displays the error message
		 */
		Label label = new Label();
		label.setText(message);
		
		/**
		 * Initialize a window that is modal to be used for the error message
		 */
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(300);
		
		/**
		 * Button that closes the alert
		 */
		Button closeButton = new Button("Close");
		closeButton.setOnAction(
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent e) {
						window.close();
					}
				});
		
		/**
		 * Inserts nodes to be displayed vertically in a VBox
		 */
		VBox layout = new VBox(10);
		layout.getChildren().addAll(label, closeButton); 
		layout.setAlignment(Pos.CENTER);
		
		/**
		 * Initialize scene
		 */
		Scene entryScene = new Scene(layout); 
		window.setScene(entryScene);
		window.showAndWait();
	}
	
	public static int prompt(String title, String message) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(400);
		
		/**
		 * Creates a textfield that will only accept numeric values
		 */
		UnaryOperator<Change> filter = change -> {
			String text = change.getText();
			if (text.matches("[0-9]*")) {
				return change;
			}
			return null; 
		};
		TextFormatter<String> textFormatter = new TextFormatter<>(filter);
		TextField critterNumberEntry = new TextField(); 
		critterNumberEntry.setTextFormatter(textFormatter);
		
		
		
		Button closeButton = new Button(); 
		closeButton.setText(message);
		closeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
					window.close();
				}
		});
		
		VBox layout = new VBox(10);
		layout.getChildren().addAll(critterNumberEntry, closeButton);
		layout.setAlignment(Pos.CENTER);
		
		Scene newScene = new Scene(layout);
		window.setScene(newScene);
		window.showAndWait();
		
		if (critterNumberEntry.getText().equals("")) {
			return 0; 
		}
		return Integer.parseInt(critterNumberEntry.getText());
	}

}
