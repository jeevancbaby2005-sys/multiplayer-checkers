import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.shape.Circle;

public class GuiServer extends Application {

	HashMap<String, Scene> sceneMap;
	Server serverConnection;
	ListView<String> listItems;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		listItems = new ListView<String>();

		serverConnection = new Server(data -> {
			Platform.runLater(() -> {
				listItems.getItems().add(data.toString());
			});
		});

		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("server", createServerGui());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

		primaryStage.setScene(sceneMap.get("server"));
		primaryStage.setTitle("Checkers Server");
		primaryStage.show();
	}

	public StackPane piece(int radius, String color1, String color2) {
		Circle circle = new Circle(radius);

		circle.setStyle(String.format(
				"-fx-fill: linear-gradient(to bottom, %s, %s);",
				color1, color2
		));
		Circle inner=new Circle(radius/1.5);
		inner.setStyle(String.format(
				"-fx-fill: linear-gradient(to top, %s, %s);",
				color1, color2
		));

		return new StackPane(circle,inner);
	}

	public Scene createServerGui() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(20));
		pane.setStyle("-fx-background-color: white; -fx-font-family: 'serif';");

		StackPane icon = piece(30, "black", "darkgrey");
		Label log=new Label("Activity Log");
		log.setStyle("-fx-background-color: cyan;");

		VBox centerBox = new VBox(10);
		centerBox.getChildren().addAll(icon,log, listItems);
		centerBox.setAlignment(Pos.TOP_CENTER);
		pane.setCenter(centerBox);
		return new Scene(pane, 300, 300);
	}

}