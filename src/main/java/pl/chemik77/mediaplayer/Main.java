package pl.chemik77.mediaplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	private static Stage primaryStage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		setPrimaryStage(primaryStage);
		Parent root = null;
		root = FXMLLoader.load(this.getClass().getResource("/fxml/MainView.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(this.getClass().getResource("/css/Application.css").toExternalForm());

		primaryStage.setTitle("Video Player");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static Stage getPrimaryStage() {
		return Main.primaryStage;
	}

	public static void setPrimaryStage(Stage primaryStage) {
		Main.primaryStage = primaryStage;
	}

}
