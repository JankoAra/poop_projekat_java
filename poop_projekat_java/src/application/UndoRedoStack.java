package application;

import java.util.Stack;

import application.GUI.UpdateType;

public class UndoRedoStack {
	public enum ActionType {
		CELL_CHANGE, ROW_CHANGE
	}

	final static Stack<Cell> undoStackCells = new Stack<>();
	final static Stack<Integer> undoStackNumber = new Stack<>();
	final static Stack<ActionType> undoStackType = new Stack<>();
	static Stack<Cell> redoStackCells = new Stack<>();
	static Stack<Integer> redoStackNumber = new Stack<>();
	static Stack<ActionType> redoStackType = new Stack<>();

	public static void undo() {
		if (undoStackType.isEmpty()) {
			GUI.printlnLog("Ne postoji akcija koja se moze vratiti");
			return;
		}
		ActionType type = undoStackType.pop();
		switch (type) {
		case CELL_CHANGE:
			int numberOfActions = undoStackNumber.pop();
			redoStackType.push(ActionType.CELL_CHANGE);
			redoStackNumber.push(numberOfActions);
			while (numberOfActions-- > 0) {
				Cell returnCell = undoStackCells.pop();
				Cell currentCell = Main.table.getCell(returnCell.getRow(), returnCell.getCol());
				redoStackCells.push(currentCell);
				Main.table.setCell(returnCell.getRow(), returnCell.getCol(), returnCell);
			}
			break;
		case ROW_CHANGE:
			break;
		default:
			break;
		}

	}
	
	public static void clearRedoStack() {
		redoStackCells = new Stack<>();
		redoStackNumber = new Stack<>();
		redoStackType = new Stack<>();
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
		case ROW_CHANGE:
			break;
		default:
			break;
		}
	}

}
