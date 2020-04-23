package engine;

import java.util.ArrayList;
import java.util.List;

import model.GomokuModel;

public class Cell {

	private int x;
	private int y;
	private int value = GomokuModel.UNPLAYED;
	
	private List<CellGroup> cellGroups;
	
	public Cell(int x, int y) {
		setX(x);
		setY(y);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public List<CellGroup> getCellGroups() {
		if (cellGroups == null) {
			cellGroups = new ArrayList<CellGroup>();
		}
		return cellGroups;
	}

	public CellGroup getWinGroup() {
		for (CellGroup cellGroup : getCellGroups()) {
			if (cellGroup.isWinGroup()) {
				return cellGroup;
			}
		}
		
		return null;
	}

}
