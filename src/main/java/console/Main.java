package console;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Main extends Application {
	public static void main(String[] args) {
		if (args.length == 0) {
			Application.launch(args);
		} else if (args.length == 4) {
			try {
				Server server = new Server(Integer.parseInt(args[0]), args[1], args[2], args[3]);
				server.run();
			} catch (NumberFormatException e) {
				System.out.println("There was a problem with the port.");
			}
		} else {
            System.out.println("Usage: java -jar hexworld.jar [port] [read password] "
            		+ "[write password] [admin password]");
        }
	}
	
	@Override
	public void start(Stage mainStage) throws Exception {
		try {
			
			final URL r = Main.class.getResource("simwindow.fxml");
			if (r == null) {
				System.out.println("No FXML resource found.");
				try {
					stop();
				} catch (final Exception e) {}
				return;
			}
			final Parent node = FXMLLoader.load(r);
			
			final Scene scene = new Scene(node);
			
			mainStage.setTitle("Critterworld");
			mainStage.setResizable(true);
			mainStage.setScene(scene);
			mainStage.sizeToScene();
			mainStage.setOnCloseRequest(e -> System.exit(0));
			mainStage.show();
			
		} catch (final IOException ioe) {
			System.out.println("Can't load FXML file.");
			ioe.printStackTrace();
			try {
				stop();
			} catch (final Exception e) {}
		}
	}

	
}
