package javafinalproject;

public class main {
	import javafx.application.Application;
	import javafx.scene.Scene;
	import javafx.scene.control.Button;
	import javafx.scene.layout.VBox;
	import javafx.stage.Stage;

	public class Main extends Application {

	    @Override
	    public void start(Stage primaryStage) {
	        primaryStage.setTitle("JavaFX Application");

	        // Create UI elements (like a button)
	        Button btn = new Button("Click Me");
	        
	        // Put elements into a layout pane
	        VBox root = new VBox(10);
	        root.getChildren().add(btn);

	        // Set up the scene and stage (window)
	        Scene scene = new Scene(root, 400, 300);
	        primaryStage.setScene(scene);
	        primaryStage.show();
	    }

	    public static void main(String[] args) {
	        // Launches the JavaFX runtime environment
	        launch(args);
	    }

		private static void launch(String[] args) {
			// TODO Auto-generated method stub
			
		}
	}
