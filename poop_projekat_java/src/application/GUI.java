package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class GUI {
	public static GridPane populateGrid(Table table) {
        GridPane grid = new GridPane();

        // Create column constraints and set them to grow always
        for (int j = 0; j <= table.getNumOfColumns(); j++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(columnConstraints);
        }

        // Create row constraints and set them to grow always
        for (int i = 0; i <= table.getNumOfRows(); i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(rowConstraints);
        }

        for (int i = 0; i <= table.getNumOfRows(); i++) {
            for (int j = 0; j <= table.getNumOfColumns(); j++) {
                String val = "";
                Label label = new Label();
                if (i == 0 && j == 0) {

                } else if (i == 0) {
                    val = String.format("%c", 'A' + j - 1);
                    label.setAlignment(Pos.CENTER);
                    label.setOnMouseClicked(e->{
                    	System.out.println(label.getText());
                    });
                } else if (j == 0) {
                    val = String.format("%d", i);
                } else {
                    val = table.getData().get(i - 1).get(j - 1).getValue();
                }

                label.setText(val);
                label.setPadding(new Insets(5));
                label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                GridPane.setConstraints(label, j, i);

                grid.getChildren().add(label);
                grid.setGridLinesVisible(true);
            }
        }
        return grid;
    }


}
