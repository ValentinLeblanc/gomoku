package engine;

import java.util.ArrayList;
import java.util.List;

import model.GomokuModel;

public class CellGroup {

	private int[][] data;
	private int direction;
	private int length = 0;
	private List<Cell> cellList;

	public CellGroup(int[][] data, int direction) {
		this.data = data;
		this.direction = direction;
		cellList = new ArrayList<Cell>();
	}
	
	public double computePotential() {
		Cell firstCell = cellList.get(0);
		Cell lastCell = cellList.get(cellList.size() -1);
		
		int firstX = firstCell.getX();
		int firstY = firstCell.getY();
		
		int lastX = lastCell.getX();
		int lastY = lastCell.getY();
		
		int clearBefore = 0;
		int clearAfter = 0;
		
		int groupValue = cellList.size() + 1;
		
		if (direction == GomokuEngine.HORIZONTAL) {
			
			length = Math.abs(lastX - firstX) + 1;
			
			int k = 1;
			while (k < 5 && firstX - k >= 0 && data[firstX - k][firstY] == GomokuModel.UNPLAYED) {
				clearBefore++;
				k++;
			}
			
			k = 1;
			while (k < 5 && lastX + k < data.length && data[lastX + k][lastY] == GomokuModel.UNPLAYED) {
				clearAfter++;
				k++;
			}
		} else if (direction == GomokuEngine.VERTICAL) {
			
			length = Math.abs(lastY - firstY) + 1;
			
			int k = 1;
			while (k < 5 && firstY - k >= 0 && data[firstX][firstY - k] == GomokuModel.UNPLAYED) {
				clearBefore++;
				k++;
			}
			
			k = 1;
			while (k < 5 && lastY + k < data.length && data[lastX][lastY + k] == GomokuModel.UNPLAYED) {
				clearAfter++;
				k++;
			}
		} else if (direction == GomokuEngine.DIAGONAL1) {
			
			length = Math.abs(lastY - firstY) + 1;
			
			int k = 1;
			while (k < 5 && firstX - k >= 0 && firstY - k >= 0 && data[firstX - k][firstY - k] == GomokuModel.UNPLAYED) {
				clearBefore++;
				k++;
			}
			
			k = 1;
			while (k < 5 && lastX + k < data.length && lastY + k < data.length && data[lastX + k][lastY + k] == GomokuModel.UNPLAYED) {
				clearAfter++;
				k++;
			}
		} else if (direction == GomokuEngine.DIAGONAL2) {
			
			length = Math.abs(lastY - firstY) + 1;
			
			int k = 1;
			while (k < 5 && firstX - k >= 0 && firstY + k < data.length && data[firstX - k][firstY + k] == GomokuModel.UNPLAYED) {
				clearBefore++;
				k++;
			}
			
			k = 1;
			while (k < 5 && lastX + k < data.length && lastY - k >= 0 && data[lastX + k][lastY - k] == GomokuModel.UNPLAYED) {
				clearAfter++;
				k++;
			}
		}
		
		if (length + clearBefore + clearAfter < 5) {
			/***
			 *  not enough space
			 */
			return 0;
		}
		
		if (length - cellList.size() == 0) {
			/**
			 * |X|X|X|X|X|    |X|X|X|X|    |X|X|X|    |X|X|    |X|
			 */
			if (cellList.size() == 5) {
				return 100000;
			}
			
			if (clearBefore > 0 && clearAfter > 0 && cellList.size() + clearBefore + clearAfter > 5) {
				return  groupValue * groupValue * groupValue * groupValue;
			}
			
			if ((clearBefore == 0 || clearAfter == 0) && cellList.size() + clearBefore + clearAfter >= 5) {
				return  groupValue * groupValue * groupValue;
			}
			
			if (cellList.size() + clearBefore + clearAfter == 5) {
				return groupValue * groupValue;
			}
		} else if (length - cellList.size() == 1) {
			/**
			 * |X|X| |X|X|    |X| |X|X|X|    |X| |X|X|    |X| |X|
			 */
			
			if (clearBefore > 0 && clearAfter > 0 && cellList.size() + clearBefore + clearAfter > 5) {
				return  groupValue * groupValue;
			}
			
			if ((clearBefore == 0 || clearAfter == 0) && cellList.size() + clearBefore + clearAfter >= 5) {
				return  groupValue;
			}
		} else if (length - cellList.size() == 2) {
			/**
			 * |X| | |X|X|    |X| |X| |X|    |X| | |X|
			 */
			
			if (clearBefore > 0 && clearAfter > 0 && cellList.size() + clearBefore + clearAfter > 5) {
				return  groupValue;
			}
			
			if ((clearBefore == 0 || clearAfter == 0) && cellList.size() + clearBefore + clearAfter >= 5) {
				return  groupValue;
			}
		} else if (length - cellList.size() == 3) {
			/**
			 * |X| | | |X|
			 */
			
			if (clearBefore > 0 && clearAfter > 0 && cellList.size() + clearBefore + clearAfter > 5) {
				return  groupValue;
			}
			
			if ((clearBefore == 0 || clearAfter == 0) && cellList.size() + clearBefore + clearAfter >= 5) {
				return  groupValue;
			}
		}
		
		return 0;
	}
	
	public void addCell(Cell cell) {
		cellList.add(cell);
	}

	public boolean isAnAttack(int number) {
		Cell firstCell = cellList.get(0);
		Cell lastCell = cellList.get(cellList.size() -1);
		
		int firstX = firstCell.getX();
		int firstY = firstCell.getY();
		
		int lastX = lastCell.getX();
		int lastY = lastCell.getY();
		
		int clearBefore = 0;
		int clearAfter = 0;
		
		if (direction == GomokuEngine.HORIZONTAL) {
			
			length = Math.abs(lastX - firstX) + 1;
			
			int k = 1;
			while (k < 5 && firstX - k >= 0 && data[firstX - k][firstY] == GomokuModel.UNPLAYED) {
				clearBefore++;
				k++;
			}
			
			k = 1;
			while (k < 5 && lastX + k < data.length && data[lastX + k][lastY] == GomokuModel.UNPLAYED) {
				clearAfter++;
				k++;
			}
		} else if (direction == GomokuEngine.VERTICAL) {
			
			length = Math.abs(lastY - firstY) + 1;
			
			int k = 1;
			while (k < 5 && firstY - k >= 0 && data[firstX][firstY - k] == GomokuModel.UNPLAYED) {
				clearBefore++;
				k++;
			}
			
			k = 1;
			while (k < 5 && lastY + k < data.length && data[lastX][lastY + k] == GomokuModel.UNPLAYED) {
				clearAfter++;
				k++;
			}
		} else if (direction == GomokuEngine.DIAGONAL1) {
			
			length = Math.abs(lastY - firstY) + 1;
			
			int k = 1;
			while (k < 5 && firstX - k >= 0 && firstY - k >= 0 && data[firstX - k][firstY - k] == GomokuModel.UNPLAYED) {
				clearBefore++;
				k++;
			}
			
			k = 1;
			while (k < 5 && lastX + k < data.length && lastY + k < data.length && data[lastX + k][lastY + k] == GomokuModel.UNPLAYED) {
				clearAfter++;
				k++;
			}
		} else if (direction == GomokuEngine.DIAGONAL2) {
			
			length = Math.abs(lastY - firstY) + 1;
			
			int k = 1;
			while (k < 5 && firstX - k >= 0 && firstY + k < data.length && data[firstX - k][firstY + k] == GomokuModel.UNPLAYED) {
				clearBefore++;
				k++;
			}
			
			k = 1;
			while (k < 5 && lastX + k < data.length && lastY - k >= 0 && data[lastX + k][lastY - k] == GomokuModel.UNPLAYED) {
				clearAfter++;
				k++;
			}
		}
		
		if (cellList.size() == number) {
			if (number < 5 - (length - cellList.size())) {
				if (clearBefore > 0 && clearAfter > 0 && (length + clearBefore + clearAfter > 5)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
}
