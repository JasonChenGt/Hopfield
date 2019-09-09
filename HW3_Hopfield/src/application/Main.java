package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class Main extends Application {
	Stage stage ;
	Menu menu;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			stage = primaryStage ;
			menu = new Menu(this , FXMLLoader.load(getClass().getResource("Menu.fxml")));
			primaryStage.setTitle("HW3 HopField");
			primaryStage.setScene(menu);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
