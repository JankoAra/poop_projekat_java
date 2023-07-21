package application;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class CellLabel extends Label {

	public CellLabel() {
		initCellLabel(this);
	}

	public CellLabel(String arg0) {
		super(arg0);
		initCellLabel(this);
	}

	public CellLabel(String arg0, Node arg1) {
		super(arg0, arg1);
		initCellLabel(this);
	}
	
	public void selectLabel() {
		setStyle("-fx-background-color:lightgray;-fx-border-color:black;");
	}
	
	public void deselectLabel() {
		setStyle("-fx-background-color:white;-fx-border-color:black;");
	}
	
	private static void initCellLabel(CellLabel label) {
		label.setOnDragDetected(e->{
			int ri = GridPane.getRowIndex(label);
			int ci = GridPane.getColumnIndex(label);
			// indeksi su u gridu
//			System.out.println("drag detected: red " + ri + "/kolona " + ci);
//			System.out.println(e.getSceneX() +" "+e.getSceneY());
			Dragboard dragboard = label.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(ri + "," + ci);
            dragboard.setContent(content);
			//e.consume();
		});
		label.setOnDragEntered(e->{
			int ri = GridPane.getRowIndex(label);
			int ci = GridPane.getColumnIndex(label);
			// indeksi su u gridu
			//System.out.println("Drag over: red " + ri + "/kolona " + ci);
			//System.out.println(e.getSceneX() +" "+e.getSceneY());
			if (/*e.getGestureSource() != label &&*/ e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.ANY);
                Dragboard dragboard = e.getDragboard();
                if (dragboard.hasString()) {
                    String draggedText = dragboard.getString();
                    String[] parts = draggedText.split(",");
                    if (parts.length == 2) {
                        int intValue1 = Integer.parseInt(parts[0]);
                        int intValue2 = Integer.parseInt(parts[1]);
                        System.out.println("Pocetna celija ("+parts[0]+","+parts[1]+"), Krajnja celija ("+ri+","+ci+")");
//                        if(intValue1*intValue1+intValue2*intValue2<ri*ri+ci*ci) {
//                        	paintSelection(intValue1, intValue2, ri, ci);
//                        }
//                        else {
//                        	paintSelection(ri, ci, intValue1, intValue2);
//                        }
                        // Handle the integers here
                    }
                }
            }
			e.consume();
		});
		label.setOnDragOver(e->{
			int ri = GridPane.getRowIndex(label);
			int ci = GridPane.getColumnIndex(label);
			// indeksi su u gridu
			//System.out.println("Drag over: red " + ri + "/kolona " + ci);
			//System.out.println(e.getSceneX() +" "+e.getSceneY());
			if (/*e.getGestureSource() != label &&*/ e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.ANY);
//                Dragboard dragboard = e.getDragboard();
//                if (dragboard.hasString()) {
//                    String draggedText = dragboard.getString();
//                    String[] parts = draggedText.split(",");
//                    if (parts.length == 2) {
//                        int intValue1 = Integer.parseInt(parts[0]);
//                        int intValue2 = Integer.parseInt(parts[1]);
//                        System.out.println("Pocetna celija ("+parts[0]+","+parts[1]+"), Krajnja celija ("+ri+","+ci+")");
//                        // Handle the integers here
//                    }
//                }
            }
			e.consume();
		});
		label.setOnDragDropped(e->{
			int ri = GridPane.getRowIndex(label);
			int ci = GridPane.getColumnIndex(label);
			// indeksi su u gridu
//			System.out.println("Drag exited: red " + ri + "/kolona " + ci);
//			System.out.println(e.getSceneX() +" "+e.getSceneY());
			Dragboard dragboard = e.getDragboard();
            if (dragboard.hasString()) {
                String draggedText = dragboard.getString();
                String[] parts = draggedText.split(",");
                if (parts.length == 2) {
                    int intValue1 = Integer.parseInt(parts[0]);
                    int intValue2 = Integer.parseInt(parts[1]);
                    System.out.println("KRAJ! Pocetna celija ("+parts[0]+","+parts[1]+"), Krajnja celija ("+ri+","+ci+")");
                    // Handle the integers here
                }
            }
            e.setDropCompleted(true);
            e.consume();
		});
		label.setMinWidth(80);
		label.setStyle("-fx-background-color:white;-fx-border-color:black;");
		label.setFont(new Font("Arial", 20));
		label.setPadding(new Insets(5));
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}

}
