package model;

public class MoveData {

	private int columnIndex;
	private int rowIndex;
	private int value;
	
	private MoveData previousMove;
	private MoveData nextMove;
	
	public MoveData(int columnIndex, int rowIndex, int value) {
		this.columnIndex = columnIndex;
		this.rowIndex = rowIndex;
		this.value = value;
	}
	
	public MoveData(MoveData moveData) {
		this(moveData.getColumnIndex(), moveData.getRowIndex(), moveData.getValue());
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

	public MoveData getPreviousMove() {
		return previousMove;
	}

	public void setPreviousMove(MoveData previousMove) {
		this.previousMove = previousMove;
	}

	public MoveData getNextMove() {
		return nextMove;
	}

	public void setNextMove(MoveData nextMove) {
		this.nextMove = nextMove;
	}

}
