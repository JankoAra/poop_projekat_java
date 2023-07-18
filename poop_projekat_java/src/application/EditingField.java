package application;

import javafx.scene.control.TextField;

public class EditingField extends TextField {
	public EditingField(int r, int c, String startVal) {
		row = r;
		column = c;
		setText(startVal);
	}
	public EditingField() {}

	private int row, column;
}
