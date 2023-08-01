package application;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Test extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Right-click me!");

        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem1 = new MenuItem("Option 1");
        MenuItem menuItem2 = new MenuItem("Option 2");
        MenuItem menuItem3 = new MenuItem("Option 3");

        // Add event handlers for menu items if needed
        menuItem1.setOnAction(e -> System.out.println("Option 1 selected"));
        menuItem2.setOnAction(e -> System.out.println("Option 2 selected"));
        menuItem3.setOnAction(e -> System.out.println("Option 3 selected"));

        contextMenu.getItems().addAll(menuItem1, menuItem2, menuItem3);

        label.setOnContextMenuRequested(event -> {
            contextMenu.show(label, event.getScreenX(), event.getScreenY());
            event.consume();
        });

        StackPane root = new StackPane();
        root.getChildren().add(label);

        Scene scene = new Scene(root, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Right-Click Menu Example");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}