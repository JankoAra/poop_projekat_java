package poop_projekat_java;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Table extends TableView<ObservableList<String>> {
	
	private static final int COLUMN_COUNT = 26;
    private static final int INITIAL_ROWS = 100;
	public Table(BorderPane layout) {
		// Create TableView with columns
        this.setEditable(true);

        // Create TableColumn instances
        for (int i = 0; i < COLUMN_COUNT; i++) {
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(Character.toString((char) ('A' + i)));

            final int columnIndex = i;
            column.setCellValueFactory(param -> {
                int ci = param.getTableView().getColumns().indexOf(param.getTableColumn());
                return new SimpleStringProperty(param.getValue().get(ci));
            });
            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setOnEditCommit(event -> {
                int rowIndex = event.getTablePosition().getRow();
                ObservableList<String> rowData = event.getTableView().getItems().get(rowIndex);
                rowData.set(columnIndex, event.getNewValue());
            });

            this.getColumns().add(column);
        }

        // Create initial rows of data
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (int i = 0; i < INITIAL_ROWS; i++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int j = 0; j < COLUMN_COUNT; j++) {
                row.add("");
            }
            data.add(row);
        }

        // Set the data items of the TableView
        this.setItems(data);

        // Wrap the TableView in a ScrollPane to enable scrolling
        ScrollPane scrollPane = new ScrollPane(this);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);


        layout.setCenter(this);
	}
}
