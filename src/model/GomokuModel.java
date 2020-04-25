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
	
	public static final String WIN_UPDATE = "winUpdate";
	public static final String RESET_UPDATE = "resetUpdate";
	
	public static final String MOVE_REQUEST = "moveRequest";
	public static final String ENGINE_MOVE_REQUEST = "engineMoveRequest";
	public static final String ENGINE_STOP_REQUEST = "engineStopRequest";
	public static final String RESET_REQUEST = "resetRequest";
	public static final String UNDO_REQUEST = "undoRequest";
	public static final String REDO_REQUEST = "redoRequest";
	
	public static final String VALUE_UPDATE = "value";
	
	public static final String BLACK_EVALUATION_UPDATE = "blackEvaluationUpdate";
	public static final String WHITE_EVALUATION_UPDATE = "whiteEvaluationUpdate";

	public static final String ANALYSED_MOVE = "analysedMove";
	public static final String ANALYSED_DONE = "analysedDone";
	public static final String LAST_MOVE = "lastMove";
	public static final String INTERRUPTED = "interrupted";
	
	public static final String ENGINE_THREAT_EVALUATION_REQUEST = "engineThreatEvaluationRequest";
	public static final String ENGINE_THREAT_EVALUATION_UPDATE = "engineThreatEvaluationUpdate";
	
	private int columnCount;
	private int rowCount;
	
	private double blackEvaluation;
	private double whiteEvaluation;
	
	public GomokuModel(int columnCount, int rowCount) {
		initData(columnCount, rowCount);
		setModelController(new GomokuModelController(this));
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
	
	public int[][] getData() {
		return data;
	}
	
	public int getValue(int columnIndex, int rowIndex) {
		return data[columnIndex][rowIndex];
	}
	
	public void setValue(int columnIndex, int rowIndex, int value) {
		data[columnIndex][rowIndex] = value;
		
		MoveData newMove = new MoveData(columnIndex, rowIndex, value);
		firePropertyChange(VALUE_UPDATE, newMove);
		
		firePropertyChange(LAST_MOVE, newMove);
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

	public double getBlackEvaluation() {
		return blackEvaluation;
	}

	public void setBlackEvaluation(double evaluation) {
		this.blackEvaluation = evaluation;
	}
	
	public double getWhiteEvaluation() {
		return whiteEvaluation;
	}
	
	public void setWhiteEvaluation(double evaluation) {
		this.whiteEvaluation = evaluation;
	}

}
