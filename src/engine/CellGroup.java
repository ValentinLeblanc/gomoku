package engine;

import java.util.ArrayList;
import java.util.List;

import model.GomokuModel;

public class CellGroup {

	private int direction;
	private int length = 0;
	private List<Cell> cellList;
	private int clearBefore;
	private int clearAfter;
	private int[][] data;

	public CellGroup(int[][] data, int direction) {
		this.data = data;
		this.direction = direction;
		cellList = new ArrayList<Cell>();
	}
	
	private void updateGroup() {
		
		clearBefore = 0;
		clearAfter = 0;
		
		Cell firstCell = cellList.get(0);
		Cell lastCell = cellList.get(cellList.size() -1);
		
		int firstX = firstCell.getX();
		int firstY = firstCell.getY();
		
		int lastX = lastCell.getX();
		int lastY = lastCell.getY();

		
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

	}
	
	public double computeOldPotential() {
		
		int groupValue = cellList.size() + 1;
		
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
	
	public double computePotential() {
		
		double value = 0;
		
		if (cellList.size() == 5) {
			value += 10000;
		} else if (cellList.size() == 4) {
			int singleThreatNumber = getSingleThreatMoves(4).size();

			if (singleThreatNumber >= 2) {
				value += 1000;
			} else if (singleThreatNumber == 1) {
				value += 100;
			} 
		} else if (cellList.size() == 3) {
			int singleThreatNumber = getSingleThreatMoves(3).size();
			int doubleThreatNumber = getDoubleThreatMoves(3).size();
			value += 100 * doubleThreatNumber;
			value += 20 * singleThreatNumber;
		} else if (cellList.size() == 2) {
			int singleThreatNumber = getSingleThreatMoves(2).size();
			int doubleThreatNumber = getDoubleThreatMoves(2).size();
			
			value += 5 * doubleThreatNumber;
			value += 2 * singleThreatNumber;
		} else if (cellList.size() == 1) {
			int singleThreatNumber = getSingleThreatMoves(1).size();
			int doubleThreatNumber = getDoubleThreatMoves(1).size();

			value += doubleThreatNumber;
			value += singleThreatNumber;
		}
		
		return value;
	}

	public void addCell(Cell cell) {
		cellList.add(cell);
		cell.getCellGroups().add(this);
		updateGroup();
	}

	public List<int[]> findPotentialDefensiveMoves(int number) {
		Cell firstCell = cellList.get(0);
		Cell lastCell = cellList.get(cellList.size() - 1);
		
		int firstX = firstCell.getX();
		int firstY = firstCell.getY();
		
		int lastX = lastCell.getX();
		int lastY = lastCell.getY();
		
		if (cellList.size() == number) {
			if (length < 5) {
				if (clearBefore > 0 && clearAfter > 0 && (length + clearBefore + clearAfter > 5)) {
					
					List<int[]> defendingMoves = new ArrayList<int[]>();
					
					if (direction == GomokuEngine.HORIZONTAL) {
						
						int lowerBound = length + clearAfter < 5 ? 5 - length : 1;
						int upperBound = length + clearBefore < 5 ? 5 - length : 1;
						
						int currentX = firstX - lowerBound;
						int currentY = firstY;
						while (currentX <= lastX + upperBound) {
							if (!contains(currentX, currentY)) {
								int[] defendingMove = new int[2];
								defendingMove[0] = currentX;
								defendingMove[1] = currentY;
								defendingMoves.add(defendingMove);
							}
							currentX++;
						}
					} else if (direction == GomokuEngine.VERTICAL) {
						
						int lowerBound = length + clearAfter < 5 ? 5 - length : 1;
						int upperBound = length + clearBefore < 5 ? 5 - length : 1;
						
						int currentX = firstX;
						int currentY = firstY - lowerBound;
						while (currentY <= lastY + upperBound) {
							if (!contains(currentX, currentY)) {
								int[] defendingMove = new int[2];
								defendingMove[0] = currentX;
								defendingMove[1] = currentY;
								defendingMoves.add(defendingMove);
							}
							currentY++;
						}
					} else if (direction == GomokuEngine.DIAGONAL1) {
						
						int lowerBound = length + clearAfter < 5 ? 5 - length : 1;
						int upperBound = length + clearBefore < 5 ? 5 - length : 1;
						
						int currentX = firstX - lowerBound;
						int currentY = firstY - lowerBound;
						while (currentX <= lastX + upperBound) {
							if (!contains(currentX, currentY)) {
								int[] defendingMove = new int[2];
								defendingMove[0] = currentX;
								defendingMove[1] = currentY;
								defendingMoves.add(defendingMove);
							}
							currentX++;
							currentY++;
						}
					} else if (direction == GomokuEngine.DIAGONAL2) {
						
						int lowerBound = length + clearAfter < 5 ? 5 - length : 1;
						int upperBound = length + clearBefore < 5 ? 5 - length : 1;
						
						int currentX = firstX - lowerBound;
						int currentY = firstY + lowerBound;
						while (currentX <= lastX + upperBound) {
							if (!contains(currentX, currentY)) {
								int[] defendingMove = new int[2];
								defendingMove[0] = currentX;
								defendingMove[1] = currentY;
								defendingMoves.add(defendingMove);
							}
							currentX++;
							currentY--;
						}
					}
					
					return defendingMoves;
				} else if (number == 4) {
					
				}
			}
		}
		
		return null;
	}
	
	public boolean contains(int x, int y) {
		for (Cell cell : cellList) {
			if (cell.getX() == x && cell.getY() == y) {
				return true;
			}
		}
		return false;
	}

	public List<Cell> getCellList() {
		return cellList;
	}

	public boolean isWinGroup() {
		return cellList.size() == 5;
	}

	public boolean isGroupSingleThreat(int number) {
		return cellList.size() == number && hasEnoughSpace(false);
	}
	
	public boolean isGroupDoubleThreat(int number) {
		return cellList.size() == number && hasEnoughSpace(true);
	}
	
	public boolean hasEnoughSpace(boolean doubleThreat) {
		if (doubleThreat) {
			return clearBefore > 0 && clearAfter > 0 && length + clearBefore + clearAfter >= 6;
		}
		return length + clearBefore + clearAfter >= 5;
	}
	
	public List<int[]> getSingleThreatMoves(int number) {
		
		List<int[]> threatMoves = new ArrayList<int[]>();
		
		// if the size is not compatible
		if (cellList.size() != number) {
			return threatMoves;
		}
		
		// if there is not enough space
		if (length + clearBefore + clearAfter < 5) {
			return threatMoves;
		}
		
		Cell firstCell = cellList.get(0);
		Cell lastCell = cellList.get(cellList.size() - 1);
		
		int firstX = firstCell.getX();
		int firstY = firstCell.getY();
		
		int lastX = lastCell.getX();
		int lastY = lastCell.getY();
		
		
		int xIncrement = 0;
		int yIncrement = 0;
		
		if (direction == GomokuEngine.HORIZONTAL) {
			xIncrement = 1;
		} else if (direction == GomokuEngine.VERTICAL) {
			yIncrement = 1;
		} else if (direction == GomokuEngine.DIAGONAL1) {
			xIncrement = 1;
			yIncrement = 1;
		} else if (direction == GomokuEngine.DIAGONAL2) {
			xIncrement = 1;
			yIncrement = -1;
		}
		
		// in-between moves
		if (number > 1) {
			int[] currentMove = new int[2];
			currentMove[0] = firstX + xIncrement;
			currentMove[1] = firstY + yIncrement;
			
			while (currentMove[0] != lastX || currentMove[1] != lastY) {
				
				boolean belongsToGroup = false;
				
				for (Cell cell : cellList) {
					if (currentMove[0] == cell.getX() && currentMove[1] == cell.getY()) {
						belongsToGroup = true;
						break;
					}
				}
				
				if (!belongsToGroup) {
					int[] threatMove = new int[2];
					threatMove[0] = currentMove[0];
					threatMove[1] = currentMove[1];
					threatMoves.add(threatMove);
				}
				
				currentMove[0] += xIncrement;
				currentMove[1] += yIncrement;
			}
		}

		
		// outside moves
		if (length == 4) {
			if (clearBefore > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = firstX - xIncrement;
				threatMove1[1] = firstY - yIncrement;
				threatMoves.add(threatMove1);
			}
			
			if (clearAfter > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = lastX + xIncrement;
				threatMove1[1] = lastY + yIncrement;
				threatMoves.add(threatMove1);
			}
		} else if (length == 3) {
			if (clearBefore > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = firstX - xIncrement;
				threatMove1[1] = firstY - yIncrement;
				threatMoves.add(threatMove1);
				
				if (clearBefore > 1) {
					int[] threatMove2 = new int[2];
					threatMove2[0] = threatMove1[0] - xIncrement;
					threatMove2[1] = threatMove1[1] - yIncrement;
					threatMoves.add(threatMove2);
				}
			}
			
			if (clearAfter > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = lastX + xIncrement;
				threatMove1[1] = lastY + yIncrement;
				threatMoves.add(threatMove1);
				
				if (clearAfter > 1) {
					int[] threatMove2 = new int[2];
					threatMove2[0] = threatMove1[0] + xIncrement;
					threatMove2[1] = threatMove1[1] + yIncrement;
					threatMoves.add(threatMove2);
				}
			}
		} else if (length == 2) {
			if (clearBefore > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = firstX - xIncrement;
				threatMove1[1] = firstY - yIncrement;
				threatMoves.add(threatMove1);
				
				if (clearBefore > 1) {
					int[] threatMove2 = new int[2];
					threatMove2[0] = threatMove1[0] - xIncrement;
					threatMove2[1] = threatMove1[1] - yIncrement;
					threatMoves.add(threatMove2);
					
					if (clearBefore > 2) {
						int[] threatMove3 = new int[2];
						threatMove3[0] = threatMove2[0] - xIncrement;
						threatMove3[1] = threatMove2[1] - yIncrement;
						threatMoves.add(threatMove3);
					}
				}
			}
			if (clearAfter > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = lastX + xIncrement;
				threatMove1[1] = lastY + yIncrement;
				threatMoves.add(threatMove1);
				
				if (clearAfter > 1) {
					int[] threatMove2 = new int[2];
					threatMove2[0] = threatMove1[0] + xIncrement;
					threatMove2[1] = threatMove1[1] + yIncrement;
					threatMoves.add(threatMove2);
					
					if (clearAfter > 2) {
						int[] threatMove3 = new int[2];
						threatMove3[0] = threatMove2[0] + xIncrement;
						threatMove3[1] = threatMove2[1] + yIncrement;
						threatMoves.add(threatMove3);
					}
				}
			}
		} else if (length == 1) {
			if (clearBefore > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = firstX - xIncrement;
				threatMove1[1] = firstY - yIncrement;
				threatMoves.add(threatMove1);
				
				if (clearBefore > 1) {
					int[] threatMove2 = new int[2];
					threatMove2[0] = threatMove1[0] - xIncrement;
					threatMove2[1] = threatMove1[1] - yIncrement;
					threatMoves.add(threatMove2);
					
					if (clearBefore > 2) {
						int[] threatMove3 = new int[2];
						threatMove3[0] = threatMove2[0] - xIncrement;
						threatMove3[1] = threatMove2[1] - yIncrement;
						threatMoves.add(threatMove3);
						
						if (clearBefore > 3) {
							int[] threatMove4 = new int[2];
							threatMove4[0] = threatMove3[0] - xIncrement;
							threatMove4[1] = threatMove3[1] - yIncrement;
							threatMoves.add(threatMove4);
						}
					}
				}
			}
			if (clearAfter > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = lastX + xIncrement;
				threatMove1[1] = lastY + yIncrement;
				threatMoves.add(threatMove1);
				
				if (clearAfter > 1) {
					int[] threatMove2 = new int[2];
					threatMove2[0] = threatMove1[0] + xIncrement;
					threatMove2[1] = threatMove1[1] + yIncrement;
					threatMoves.add(threatMove2);
					
					if (clearAfter > 2) {
						int[] threatMove3 = new int[2];
						threatMove3[0] = threatMove2[0] + xIncrement;
						threatMove3[1] = threatMove2[1] + yIncrement;
						threatMoves.add(threatMove3);
						
						if (clearAfter > 3) {
							int[] threatMove4 = new int[2];
							threatMove4[0] = threatMove3[0] + xIncrement;
							threatMove4[1] = threatMove3[1] + yIncrement;
							threatMoves.add(threatMove4);
						}
					}
				}
			}
		} 
		
		return threatMoves;
	}
	
	
	public List<int[]> getDoubleThreatMoves(int number) {
		
		List<int[]> threatMoves = new ArrayList<int[]>();
		
		// if the size is not compatible
		if (cellList.size() != number) {
			return threatMoves;
		}
		
		if (length == 5) {
			return threatMoves;
		}
		
		// if there is not enough space
		if (clearBefore == 0 || clearAfter == 0 || length + clearBefore + clearAfter < 6) {
			return threatMoves;
		}
		
		Cell firstCell = cellList.get(0);
		Cell lastCell = cellList.get(cellList.size() - 1);
		
		int firstX = firstCell.getX();
		int firstY = firstCell.getY();
		
		int lastX = lastCell.getX();
		int lastY = lastCell.getY();
		
		
		int xIncrement = 0;
		int yIncrement = 0;
		
		if (direction == GomokuEngine.HORIZONTAL) {
			xIncrement = 1;
		} else if (direction == GomokuEngine.VERTICAL) {
			yIncrement = 1;
		} else if (direction == GomokuEngine.DIAGONAL1) {
			xIncrement = 1;
			yIncrement = 1;
		} else if (direction == GomokuEngine.DIAGONAL2) {
			xIncrement = 1;
			yIncrement = -1;
		}
		
		// in-between moves
		if (number > 1) {
			int[] currentMove = new int[2];
			currentMove[0] = firstX + xIncrement;
			currentMove[1] = firstY + yIncrement;
			
			while (currentMove[0] != lastX || currentMove[1] != lastY) {
				
				boolean belongsToGroup = false;
				
				for (Cell cell : cellList) {
					if (currentMove[0] == cell.getX() && currentMove[1] == cell.getY()) {
						belongsToGroup = true;
						break;
					}
				}
				
				if (!belongsToGroup) {
					int[] threatMove = new int[2];
					threatMove[0] = currentMove[0];
					threatMove[1] = currentMove[1];
					threatMoves.add(threatMove);
				}
				
				currentMove[0] += xIncrement;
				currentMove[1] += yIncrement;
			}
		}
		
		
		// outside moves
		if (length == 4 && cellList.size() == 4) {
			int[] threatMove1 = new int[2];
			threatMove1[0] = lastX + xIncrement;
			threatMove1[1] = lastY + yIncrement;
			threatMoves.add(threatMove1);
			
			int[] threatMove2 = new int[2];
			threatMove2[0] = firstX - xIncrement;
			threatMove2[1] = firstY - yIncrement;
			threatMoves.add(threatMove2);
		} else if (length == 3) {
			if (clearAfter > 1) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = lastX + xIncrement;
				threatMove1[1] = lastY + yIncrement;
				threatMoves.add(threatMove1);
			}

			if (clearBefore > 1) {
				int[] threatMove2 = new int[2];
				threatMove2[0] = firstX - xIncrement;
				threatMove2[1] = firstY - yIncrement;
				threatMoves.add(threatMove2);
			}

		} else if (length == 2) {
			
			if (clearAfter > 1) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = lastX + xIncrement;
				threatMove1[1] = lastY + yIncrement;
				threatMoves.add(threatMove1);
				if (clearAfter > 2) {
					int[] threatMove2 = new int[2];
					threatMove2[0] = threatMove1[0] + xIncrement;
					threatMove2[1] = threatMove1[1] + yIncrement;
					threatMoves.add(threatMove2);
				}
			}
			
			if (clearBefore > 1) {
				int[] threatMove3 = new int[2];
				threatMove3[0] = firstX - xIncrement;
				threatMove3[1] = firstY - yIncrement;
				threatMoves.add(threatMove3);
				if (clearBefore > 2) {
					int[] threatMove4 = new int[2];
					threatMove4[0] = threatMove3[0] - xIncrement;
					threatMove4[1] = threatMove3[1] - yIncrement;
					threatMoves.add(threatMove4);
				}
			}
			
		} else if (length == 1) {
			
			if (clearBefore > 1) {
				int[] threatMove3 = new int[2];
				threatMove3[0] = firstX - xIncrement;
				threatMove3[1] = firstY - yIncrement;
				threatMoves.add(threatMove3);
				if (clearBefore > 2) {
					int[] threatMove4 = new int[2];
					threatMove4[0] = threatMove3[0] - xIncrement;
					threatMove4[1] = threatMove3[1] - yIncrement;
					threatMoves.add(threatMove4);
					if (clearBefore > 3) {
						int[] threatMove5 = new int[2];
						threatMove5[0] = threatMove4[0] - xIncrement;
						threatMove5[1] = threatMove4[1] - yIncrement;
						threatMoves.add(threatMove5);
					}
				}
			}
			
			if (clearAfter > 1) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = lastX + xIncrement;
				threatMove1[1] = lastY + yIncrement;
				threatMoves.add(threatMove1);
				if (clearAfter > 2) {
					int[] threatMove2 = new int[2];
					threatMove2[0] = threatMove1[0] + xIncrement;
					threatMove2[1] = threatMove1[1] + yIncrement;
					threatMoves.add(threatMove2);
					if (clearAfter > 3) {
						int[] threatMove3 = new int[2];
						threatMove3[0] = threatMove2[0] + xIncrement;
						threatMove3[1] = threatMove2[1] + yIncrement;
						threatMoves.add(threatMove3);
					}
				}
			}
		}
		
		return threatMoves;
	}

	
	// for 4-size groups only
	public List<int[]> getThreat5Moves() {
		
		List<int[]> threatMoves = new ArrayList<int[]>();
		
		if (cellList.size() < 4) {
			return threatMoves;
		}
		
		if (length + clearBefore + clearAfter < 5) {
			return threatMoves;
		}
		
		Cell firstCell = cellList.get(0);
		Cell lastCell = cellList.get(cellList.size() - 1);
		
		int firstX = firstCell.getX();
		int firstY = firstCell.getY();
		
		int lastX = lastCell.getX();
		int lastY = lastCell.getY();
		
		
		int xIncrement = 0;
		int yIncrement = 0;
		
		if (direction == GomokuEngine.HORIZONTAL) {
			xIncrement = 1;
		} else if (direction == GomokuEngine.VERTICAL) {
			yIncrement = 1;
		} else if (direction == GomokuEngine.DIAGONAL1) {
			xIncrement = 1;
			yIncrement = 1;
		} else if (direction == GomokuEngine.DIAGONAL2) {
			xIncrement = 1;
			yIncrement = -1;
		}
		
		// in-between moves
		int[] currentMove = new int[2];
		currentMove[0] = firstX + xIncrement;
		currentMove[1] = firstY + yIncrement;
		
		while (currentMove[0] != lastX || currentMove[1] != lastY) {
			
			boolean belongsToGroup = false;
			
			for (Cell cell : cellList) {
				if (currentMove[0] == cell.getX() && currentMove[1] == cell.getY()) {
					belongsToGroup = true;
					break;
				}
			}
			
			if (!belongsToGroup) {
				int[] threatMove = new int[2];
				threatMove[0] = currentMove[0];
				threatMove[1] = currentMove[1];
				threatMoves.add(threatMove);
			}
			
			currentMove[0] += xIncrement;
			currentMove[1] += yIncrement;
		}
		
		// outside moves
		if (length == 4) {
			if (clearBefore > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = firstX - xIncrement;
				threatMove1[1] = firstY - yIncrement;
				threatMoves.add(threatMove1);
			}
			
			if (clearAfter > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = lastX + xIncrement;
				threatMove1[1] = lastY + yIncrement;
				threatMoves.add(threatMove1);
			}
		}
		
		return threatMoves;
	}

	// for 3-size groups only
	public List<int[]> getThreat4Moves() {
		
		List<int[]> threatMoves = new ArrayList<int[]>();
		
		if (length + clearBefore + clearAfter < 5) {
			return threatMoves;
		}
		
		Cell firstCell = cellList.get(0);
		Cell lastCell = cellList.get(cellList.size() - 1);
		
		int firstX = firstCell.getX();
		int firstY = firstCell.getY();
		
		int lastX = lastCell.getX();
		int lastY = lastCell.getY();
		
		
		int xIncrement = 0;
		int yIncrement = 0;
		
		if (direction == GomokuEngine.HORIZONTAL) {
			xIncrement = 1;
		} else if (direction == GomokuEngine.VERTICAL) {
			yIncrement = 1;
		} else if (direction == GomokuEngine.DIAGONAL1) {
			xIncrement = 1;
			yIncrement = 1;
		} else if (direction == GomokuEngine.DIAGONAL2) {
			xIncrement = 1;
			yIncrement = -1;
		}
		
		// in-between moves
		int[] currentMove = new int[2];
		currentMove[0] = firstX + xIncrement;
		currentMove[1] = firstY + yIncrement;
		
		while (currentMove[0] != lastX || currentMove[1] != lastY) {
			
			boolean belongsToGroup = false;
			
			for (Cell cell : cellList) {
				if (currentMove[0] == cell.getX() && currentMove[1] == cell.getY()) {
					belongsToGroup = true;
					break;
				}
			}
			
			if (!belongsToGroup) {
				int[] threatMove = new int[2];
				threatMove[0] = currentMove[0];
				threatMove[1] = currentMove[1];
				threatMoves.add(threatMove);
			}
			
			currentMove[0] += xIncrement;
			currentMove[1] += yIncrement;
		}
		
		// outside moves
		if (length == 3) {
			if (clearBefore > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = firstX - xIncrement;
				threatMove1[1] = firstY - yIncrement;
				threatMoves.add(threatMove1);
				
				if (clearBefore > 1) {
					int[] threatMove2 = new int[2];
					threatMove2[0] = threatMove1[0] - xIncrement;
					threatMove2[1] = threatMove1[1] - yIncrement;
					threatMoves.add(threatMove2);
				}
			}
			
			if (clearAfter > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = lastX + xIncrement;
				threatMove1[1] = lastY + yIncrement;
				threatMoves.add(threatMove1);
				
				if (clearAfter > 1) {
					int[] threatMove2 = new int[2];
					threatMove2[0] = threatMove1[0] + xIncrement;
					threatMove2[1] = threatMove1[1] + yIncrement;
					threatMoves.add(threatMove2);
				}
			}
		} else if (length == 4) {
			if (clearBefore > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = firstX - xIncrement;
				threatMove1[1] = firstY - yIncrement;
				threatMoves.add(threatMove1);
			}
			if (clearAfter > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = lastX + xIncrement;
				threatMove1[1] = lastY + yIncrement;
				threatMoves.add(threatMove1);
			}
		}
		
		return threatMoves;
	}
	
	// for 2-size groups only
	public List<int[]> getThreat3Moves() {
		
		List<int[]> threatMoves = new ArrayList<int[]>();
		
		if (length + clearBefore + clearAfter < 5) {
			return threatMoves;
		}
		
		if (length == 5) {
			return threatMoves;
		}
		
		Cell firstCell = cellList.get(0);
		Cell lastCell = cellList.get(cellList.size() - 1);
		
		int firstX = firstCell.getX();
		int firstY = firstCell.getY();
		
		int lastX = lastCell.getX();
		int lastY = lastCell.getY();
		
		
		int xIncrement = 0;
		int yIncrement = 0;
		
		if (direction == GomokuEngine.HORIZONTAL) {
			xIncrement = 1;
		} else if (direction == GomokuEngine.VERTICAL) {
			yIncrement = 1;
		} else if (direction == GomokuEngine.DIAGONAL1) {
			xIncrement = 1;
			yIncrement = 1;
		} else if (direction == GomokuEngine.DIAGONAL2) {
			xIncrement = 1;
			yIncrement = -1;
		}
		
		// in-between moves
		int[] currentMove = new int[2];
		currentMove[0] = firstX + xIncrement;
		currentMove[1] = firstY + yIncrement;
		
		while (currentMove[0] != lastX || currentMove[1] != lastY) {
			
			boolean belongsToGroup = false;
			
			for (Cell cell : cellList) {
				if (currentMove[0] == cell.getX() && currentMove[1] == cell.getY()) {
					belongsToGroup = true;
					break;
				}
			}
			
			if (!belongsToGroup) {
				int[] threatMove = new int[2];
				threatMove[0] = currentMove[0];
				threatMove[1] = currentMove[1];
				threatMoves.add(threatMove);
			}
			
			currentMove[0] += xIncrement;
			currentMove[1] += yIncrement;
		}
		
		// outside moves
		if (length == 2) {
			if (clearBefore > 0 && clearAfter > 2) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = lastX + xIncrement;
				threatMove1[1] = lastY + yIncrement;
				threatMoves.add(threatMove1);
				
				int[] threatMove2 = new int[2];
				threatMove2[0] = threatMove1[0] + xIncrement;
				threatMove2[1] = threatMove1[1] + yIncrement;
				threatMoves.add(threatMove2);
			}
			if (clearBefore > 2 && clearAfter > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = firstX - xIncrement;
				threatMove1[1] = firstY - yIncrement;
				threatMoves.add(threatMove1);
				
				int[] threatMove2 = new int[2];
				threatMove2[0] = threatMove1[0] - xIncrement;
				threatMove2[1] = threatMove1[1] - yIncrement;
				threatMoves.add(threatMove2);
			}
			
		} else if (length == 3) {
			if (clearBefore > 0 && clearAfter > 1) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = lastX + xIncrement;
				threatMove1[1] = lastY + yIncrement;
				threatMoves.add(threatMove1);
			}
			
			if (clearBefore > 1 && clearAfter > 0) {
				int[] threatMove1 = new int[2];
				threatMove1[0] = firstX - xIncrement;
				threatMove1[1] = firstY - yIncrement;
				threatMoves.add(threatMove1);
			}
		}
		
		return threatMoves;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getDirection() {
		return direction;
	}

	public boolean containsAll(CellGroup cellGroup) {
		
		if (cellGroup.getDirection() != getDirection()) {
			return false;
		}
		
		for (Cell cell : cellGroup.getCellList()) {
			if (!getCellList().contains(cell)) {
				return false;
			}
		}
			
		return true;
	}

	public boolean hasDoubleThreatPotentialWith(int[] threat) {
		
		if (cellList.size() == 1 && hasEnoughSpace(true)) {
			
			int firstX = threat[0];
			int firstY = threat[1];
			int secondX = cellList.get(0).getX();
			int secondY = cellList.get(0).getY();

			if (direction == GomokuEngine.HORIZONTAL) {
				if (firstY == secondY) {
					if (firstX < secondX) {
						return secondX - firstX <= 3 && secondX - firstX < clearBefore;
					} else {
						return firstX - secondX <= 3 && firstX - secondX < clearAfter;
					}
				}
			} else if (direction == GomokuEngine.VERTICAL) {
				if (firstX == secondX) {
					if (firstY < secondY) {
						return secondY - firstY <= 3 && secondY - firstY < clearBefore;
					} else {
						return firstY - secondY <= 3 && firstY - secondY < clearAfter;
					}
				}
			} else if (direction == GomokuEngine.DIAGONAL1) {
				if (firstX - secondX == firstY - secondY) {
					if (firstX < secondX) {
						return secondX - firstX <= 3 && secondX - firstX < clearBefore;
					} else {
						return firstX - secondX <= 3 && firstX - secondX < clearAfter;
					}
				}
			} else if (direction == GomokuEngine.DIAGONAL2) {
				if (firstX - secondX == secondY - firstY) {
					if (firstX < secondX) {
						return secondX - firstX <= 3 && secondX - firstX < clearBefore;
					} else {
						return firstX - secondX <= 3 && firstX - secondX < clearAfter;
					}
				}
			}
		} else if (cellList.size() == 2 && hasEnoughSpace(true)) {
			
			int firstX = threat[0];
			int firstY = threat[1];
			int secondX = cellList.get(0).getX();
			int secondY = cellList.get(0).getY();
			int thirdX = cellList.get(1).getX();
			int thirdY = cellList.get(1).getY();
			
			if (direction == GomokuEngine.HORIZONTAL) {
				if (firstY == secondY) {
					if (Math.abs(firstX - secondX) <= 3) {
						return true;
					}
					if (Math.abs(firstX - thirdX) <= 3) {
						return true;
					}
				}
			} else if (direction == GomokuEngine.VERTICAL) {
				if (firstX == secondX) {
					if (Math.abs(firstY - secondY) <= 3) {
						return true;
					}
					if (Math.abs(firstY - thirdY) <= 3) {
						return true;
					}
				}
			} else if (direction == GomokuEngine.DIAGONAL1) {
				if (firstX - secondX == firstY - secondY) {
					if (Math.abs(firstX - secondX) <= 3) {
						return true;
					}
					if (Math.abs(firstX - thirdX) <= 3) {
						return true;
					}
				}
			} else if (direction == GomokuEngine.DIAGONAL2) {
				if (firstX - secondX == secondY - firstY) {
					if (Math.abs(firstX - secondX) <= 3) {
						return true;
					}
					if (Math.abs(firstX - thirdY) <= 3) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	public int[] isCrossing(CellGroup secondGroup) {
		for (Cell cell1 : getCellList()) {
			for (Cell cell2 : secondGroup.getCellList()) {
				if (cell1.getX() == cell2.getX() && cell1.getY() == cell2.getY()) {
					int[] cross = new int[2];
					cross[0] = cell1.getX();
					cross[1] = cell1.getY();
					return cross;
				}
			}
		}
		return null;
	}
}
