package model;

import java.awt.Component;

public class GomokuModel extends Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GomokuModelController modelController;
	
	private int[][] data;
	
	public static final int UNPLAYED = 0;
	public static final int BLACK = 1;
	public static final int WHITE = -1;
	
	public static final String MOVE_REQUEST = "moveRequest";
	public static final String MOVE_UPDATE = "moveUpdate";
	public static final String WIN_UPDATE = "winUpdate";
	public static final String RESET_REQUEST = "resetRequest";
	public static final String UNDO_REQUEST = "undoRequest";
	public static final String REDO_REQUEST = "redoRequest";
	public static final String BOARD_RESET = "boardReset";
	
	private int columnCount;
	private int rowCount;
	
	public GomokuModel(int columnCount, int rowCount) {
		setModelController(new GomokuModelController(this));
		
		initData(columnCount, rowCount);
	}

	private void initData(int columnCount, int rowCount) {
		this.columnCount = columnCount;
		this.rowCount = rowCount;
		
		data = new int[columnCount][rowCount];
		
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
				data[columnIndex][rowIndex] = 0;
			}
		}
	}
	
	public int getValue(int columnIndex, int rowIndex) {
		return data[columnIndex][rowIndex];
	}
	
	public void setValue(int columnIndex, int rowIndex, int value) {
		data[columnIndex][rowIndex] = value;
	}

	public GomokuModelController getModelController() {
		return modelController;
	}

	public void setModelController(GomokuModelController modelController) {
		this.modelController = modelController;
	}
	
	public boolean putData(int columnIndex, int rowIndex, int i) {
		if (data[columnIndex][rowIndex] == UNPLAYED) {
			data[columnIndex][rowIndex] = i;
			return true;
		}
		return false;
	}

	public void firePropertyChange(String propertyName, Object data) {
		super.firePropertyChange(propertyName, null, data);
	}
	
	public void firePropertyChange(String propertyName) {
		super.firePropertyChange(propertyName, null, null);
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

}
