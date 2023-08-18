package application;

import java.util.Stack;

import application.GUI.UpdateType;

public class UndoRedoStack {
	public enum ActionType { CELL_CHANGE, ROW_ADDED, ROW_DELETED }

	static Stack<Cell> undoStackCells = new Stack<>();
	static Stack<Integer> undoStackNumber = new Stack<>();
	static Stack<ActionType> undoStackType = new Stack<>();
	private static Stack<Cell> redoStackCells = new Stack<>();
	private static Stack<Integer> redoStackNumber = new Stack<>();
	private static Stack<ActionType> redoStackType = new Stack<>();

	public static void undo() {
		if (undoStackType.isEmpty()) {
			GUI.printlnLog("Ne postoji akcija koja se moze vratiti");
			return;
		}
		ActionType type = undoStackType.pop();
		switch (type) {
		case CELL_CHANGE:
			// vraca format/sadrzaj promenjenim celijama u jednom koraku
			int numberOfActions = undoStackNumber.pop();
			redoStackType.push(ActionType.CELL_CHANGE);
			redoStackNumber.push(numberOfActions);
			while (numberOfActions-- > 0) {
				Cell returnCell = undoStackCells.pop();
				Cell currentCell = Main.table.getCell(returnCell.getRow(), returnCell.getCol());
				redoStackCells.push(currentCell);
				Main.table.setCell(returnCell.getRow(), returnCell.getCol(), returnCell);
			}
			GUI.updateGUI(UpdateType.CELL_CHANGE);
			break;
		case ROW_ADDED:
			// uklanja dodati red
			int addedRowIndex = undoStackNumber.pop();
			redoStackNumber.push(addedRowIndex);
			redoStackType.push(ActionType.ROW_ADDED);
			Main.table.deleteRow(addedRowIndex);
			GUI.updateGUI(UpdateType.TABLE_CHANGE);
			break;
		case ROW_DELETED:
			// vraca obrisani red
			int deletedRowIndex = undoStackNumber.pop();
			redoStackType.push(ActionType.ROW_DELETED);
			redoStackNumber.push(deletedRowIndex);
			Main.table.addRow(deletedRowIndex);
			for (int i = 0; i < Table.NUMBER_OF_COLUMNS; i++) {
				Cell c = undoStackCells.pop();
				Main.table.setCell(deletedRowIndex, 25 - i, c);
			}
			GUI.updateGUI(UpdateType.TABLE_CHANGE);
			break;
		default:
			break;
		}

	}

	public static void clearUndoStack() {
		undoStackCells = new Stack<>();
		undoStackNumber = new Stack<>();
		undoStackType = new Stack<>();
	}
	
	public static void clearRedoStack() {
		redoStackCells = new Stack<>();
		redoStackNumber = new Stack<>();
		redoStackType = new Stack<>();
	}
	
	public static void clearUndoRedoStack() {
		clearUndoStack();
		clearRedoStack();
	}

	public static void redo() {
		if (redoStackType.isEmpty()) {
			GUI.printlnLog("Ne postoji akcija koja se moze ponoviti");
			return;
		}
		ActionType type = redoStackType.pop();
		switch (type) {
		case CELL_CHANGE:
			int numberOfActions = redoStackNumber.pop();
			undoStackType.push(ActionType.CELL_CHANGE);
			undoStackNumber.push(numberOfActions);
			while (numberOfActions-- > 0) {
				Cell returnCell = redoStackCells.pop();
				Cell currentCell = Main.table.getCell(returnCell.getRow(), returnCell.getCol());
				undoStackCells.push(currentCell);
				Main.table.setCell(returnCell.getRow(), returnCell.getCol(), returnCell);
			}
			break;
		case ROW_ADDED:
			// vraca red koji je bio obrisan
			int newRowIndex = redoStackNumber.pop();
			undoStackType.push(ActionType.ROW_ADDED);
			undoStackNumber.push(newRowIndex);
			Main.table.addRow(newRowIndex);
			GUI.updateGUI(UpdateType.TABLE_CHANGE);
			break;
		case ROW_DELETED:
			// brise red koji je bio vracen
			int toDeleteIndex = redoStackNumber.pop();
			undoStackType.push(ActionType.ROW_DELETED);
			undoStackNumber.push(toDeleteIndex);
			for (int i = 0; i < Table.NUMBER_OF_COLUMNS; i++) {
				Cell c = Main.table.getCell(toDeleteIndex, i);
				undoStackCells.push(c);
			}
			Main.table.deleteRow(toDeleteIndex);
			GUI.updateGUI(UpdateType.TABLE_CHANGE);
			break;
		default:
			break;
		}
	}

}
