package application;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Test extends Application {

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();
        int numRows = 5;
        int numCols = 5;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Label label = new Label("Cell " + i + "-" + j);
                label.setStyle("-fx-background-color: white; -fx-border-color: black;");
                label.setPrefSize(100, 100);

                // Set up drag gesture
                label.setOnDragDetected(event -> {
                    Dragboard dragboard = label.startDragAndDrop(TransferMode.COPY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(label.getText());
                    dragboard.setContent(content);
                });

                // Set up drag over
                label.setOnDragOver(event -> {
                    if (event.getGestureSource() != label && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.COPY);
                    }
                    System.out.println(event.getTarget().getClass());
                    event.consume();
                });

                // Set up drag dropped
                label.setOnDragDropped(event -> {
                    Dragboard dragboard = event.getDragboard();
                    if (dragboard.hasString()) {
                        String draggedText = dragboard.getString();
                        System.out.println("Dropped on cell: " + label.getText() + ", Dragged data: " + draggedText);
                        // Handle the dragged data here
                    }
                    event.setDropCompleted(true);
                    event.consume();
                });

                GridPane.setConstraints(label, j, i);
                grid.getChildren().add(label);
            }
        }

        StackPane root = new StackPane(grid);
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setTitle("Drag and Drop Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
