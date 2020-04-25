package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.Cell;
import engine.CellGroup;
import engine.GomokuEngine;

public class GomokuData {

	Map<Integer, Map<Integer, Cell>> cellMap;
	private int[][] data;
	private int rowCount;
	private int columnCount;
	
	List<CellGroup> cellGroups = new ArrayList<CellGroup>();
	
	private Comparator<CellGroup> sizeComparator = new Comparator<CellGroup>() {

		@Override
		public int compare(CellGroup o1, CellGroup o2) {
			return o1.getCellList().size() - o2.getCellList().size() > 0 ? 1 : o1.getCellList().size() - o2.getCellList().size() < 0 ? -1 : 0;
		}
		
	};
	
	public GomokuData(int[][] data, int playingColor) throws Exception {
		
		this.columnCount = data.length;
		this.rowCount = data[0].length;
		this.data = data;
		
		cellMap = new HashMap<Integer, Map<Integer,Cell>>();
		
		for (int j = 0; j < columnCount; j++) {
			cellMap.put(j, new HashMap<Integer, Cell>());
			
			for (int i = 0; i < rowCount; i++) {
				Cell newCell = new Cell(j, i);
				newCell.setValue(data[j][i]);
				cellMap.get(j).put(i, newCell);
			}
		}
		
		computeCellGroups(playingColor);
	}
	
	public Cell get(int j, int i) {
		return cellMap.get(j).get(i);
	}
	
	public List<CellGroup> getCellGroups() {
		return cellGroups;
	}

	public void computeCellGroups(int playingColor) throws Exception {
		
		cellGroups = new ArrayList<CellGroup>();
		
		for (int j = 0; j < columnCount; j++) {
			for (int i = 0; i < rowCount; i++) {
				cellMap.get(j).get(i).getCellGroups().clear();
			}
		}
		
		for (int row = 0; row < rowCount; row++) {
			int[][] horizontalStripe = new int[columnCount][2];
			for (int col = 0; col < columnCount; col++) {
				horizontalStripe[col][0] = col;
				horizontalStripe[col][1] = row;
			}
			computeCellGroups(horizontalStripe, GomokuEngine.HORIZONTAL, playingColor);
		}

		for (int col = 0; col < columnCount; col++) {
			int[][] verticalStripe = new int[rowCount][2];
			for (int row = 0; row < rowCount; row++) {
				verticalStripe[row][0] = col;
				verticalStripe[row][1] = row;
			}
			computeCellGroups(verticalStripe, GomokuEngine.VERTICAL, playingColor);
		}

		for (int row = 0; row < rowCount; row++) {
			int[][] diagonal1Stripe = new int[columnCount - row][2];
			for (int col = 0; col < columnCount - row; col++) {
				diagonal1Stripe[col][0] = col;
				diagonal1Stripe[col][1] = row + col;
			}
			computeCellGroups(diagonal1Stripe, GomokuEngine.DIAGONAL1, playingColor);
		}

		for (int col = 1; col < columnCount; col++) {
			int[][] diagonal1Stripe = new int[rowCount - col][2];
			for (int row = 0; row < rowCount - col; row++) {
				diagonal1Stripe[row][0] = col + row;
				diagonal1Stripe[row][1] = row;
			}
			computeCellGroups(diagonal1Stripe, GomokuEngine.DIAGONAL1, playingColor);
		}

		for (int col = 0; col < columnCount; col++) {
			int[][] diagonal2Stripe = new int[rowCount - col][2];
			for (int row = rowCount - 1; row >= col; row--) {
				diagonal2Stripe[rowCount - 1 - row][0] = col - row + rowCount - 1;
				diagonal2Stripe[rowCount - 1 - row][1] = row;
			}
			computeCellGroups(diagonal2Stripe, GomokuEngine.DIAGONAL2, playingColor);
		}

		for (int row = rowCount - 2; row >= 0; row--) {
			int[][] diagonal2Stripe = new int[row + 1][2];
			for (int col = 0; col <= row; col++) {
				diagonal2Stripe[col][0] = col;
				diagonal2Stripe[col][1] = row - col;
			}
			computeCellGroups(diagonal2Stripe, GomokuEngine.DIAGONAL2, playingColor);
		}

		List<CellGroup> toRemoveList = new ArrayList<CellGroup>();
		
		cellGroups.sort(sizeComparator);
		
		for (int i = 0; i < cellGroups.size(); i++) {
			for (int j = i + 1; j < cellGroups.size(); j++) {
				if (cellGroups.get(j).containsAll(cellGroups.get(i))) {
					toRemoveList.add(cellGroups.get(i));
					
					for (Cell cell : cellGroups.get(i).getCellList()) {
						cell.getCellGroups().remove(cellGroups.get(i));
					}
					
					break;
				}
			}
		}
		
		cellGroups.removeAll(toRemoveList);
	}

	private void computeCellGroups(int[][] stripe, int direction, int playingColor) throws Exception {

		if (Thread.interrupted()) {
			throw new Exception();
		}
		
		CellGroup currentCellGroup = null;
		int k = 0;
		while (k < stripe.length - 4) {
			for (int h = 0; h < 5; h++) {
				if (getData()[stripe[k + h][0]][stripe[k + h][1]] == playingColor) {
					if (currentCellGroup == null) {
						currentCellGroup = new CellGroup(getData(), direction);
					}
					currentCellGroup.addCell(get(stripe[k + h][0], stripe[k + h][1]));
				} else if (getData()[stripe[k + h][0]][stripe[k + h][1]] == -playingColor) {
					break;
				}
			}
			
			if (currentCellGroup != null) {
				cellGroups.add(currentCellGroup);
			}
			
			currentCellGroup = null;
			
			k++;
		}
		
	}

	public int[][] getData() {
		return data;
	}
}
