package engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.GomokuModel;

public class StonePack {

	private List<int[]> stoneList;
	
	private int direction;
	
	private int[][] data;
	private int color;
	
	private int clearBefore;
	private int clearAfter;
	
	private int length;
	
	Comparator<int[]> stoneComparator = new Comparator<int[]>() {

		@Override
		public int compare(int[] stone1, int[] stone2) {
			
			if (direction != GomokuEngine.DIAGONAL2) {
				return stone1[0] + stone1[1] > stone2[0] + stone2[1] ? 1 : stone1[0] + stone1[1] < stone2[0] + stone2[1] ? -1 : 0;
			}
			
			return stone1[0] - stone1[1] > stone2[0] - stone2[1] ? 1 : stone1[0] - stone1[1] < stone2[0] - stone2[1] ? -1 : 0;
		}
		
	};

	public StonePack(int direction, int[][] data, int color) {
		this.direction = direction;
		this.data = data;
		this.color = color;
	}
	
	public StonePack(StonePack stonePack) {
		this.direction = stonePack.direction;
		this.data = stonePack.data;
		this.color = stonePack.color;
		
		for (int[] stone : stonePack.getStoneList()) {
			getStoneList().add(stone);
		}
	}

	public List<int[]> getStoneList() {
		if (stoneList == null) {
			stoneList = new ArrayList<int[]>();
		}
		return stoneList;
	}

	public int getDirection() {
		return direction;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StonePack) {
			return equals((StonePack) obj);
		}
		return super.equals(obj);
	}
	
	public boolean equals(StonePack otherThreat) {
		
		if (direction != otherThreat.direction) {
			return false;
		}
		
		if (stoneList.size() != otherThreat.stoneList.size()) {
			return false;
		}
		
		for (int[] stone : stoneList) {
			
			boolean contains = false;
			
			for (int[] otherStone : otherThreat.stoneList) {
				if (otherStone[0] == stone[0] && otherStone[1] == stone[1]) {
					contains = true;
					break;
				}
			}
			
			if (!contains) {
				return false;
			}
		}
		
		return true;
	}

	public void updateSides() {
		
		clearBefore = 0;
		clearAfter = 0;
		
		int[] firstCell = stoneList.get(0);
		int[] lastCell = stoneList.get(stoneList.size() -1);
		
		int firstX = firstCell[0];
		int firstY = firstCell[1];
		
		int lastX = lastCell[0];
		int lastY = lastCell[1];
		
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

	public List<int[]> computeSingleAttackMoves() {
		
		List<int[]> threatMoves = new ArrayList<int[]>();
		
		int[] firstCell = stoneList.get(0);
		int[] lastCell = stoneList.get(stoneList.size() -1);
		
		int firstX = firstCell[0];
		int firstY = firstCell[1];
		
		int lastX = lastCell[0];
		int lastY = lastCell[1];
		
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
		if (stoneList.size() > 1) {
			int[] currentMove = new int[2];
			currentMove[0] = firstX + xIncrement;
			currentMove[1] = firstY + yIncrement;
			
			while (currentMove[0] != lastX || currentMove[1] != lastY) {
				
				boolean belongsToGroup = false;
				
				for (int[] stone : stoneList) {
					if (currentMove[0] == stone[0] && currentMove[1] == stone[1]) {
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

	public List<int[]> computeDoubleAttackMoves() {
		
		List<int[]> threatMoves = new ArrayList<int[]>();
		
		int[] firstCell = stoneList.get(0);
		int[] lastCell = stoneList.get(stoneList.size() -1);
		
		int firstX = firstCell[0];
		int firstY = firstCell[1];
		
		int lastX = lastCell[0];
		int lastY = lastCell[1];
		
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
		if (stoneList.size() > 1) {
			int[] currentMove = new int[2];
			currentMove[0] = firstX + xIncrement;
			currentMove[1] = firstY + yIncrement;
			
			while (currentMove[0] != lastX || currentMove[1] != lastY) {
				
				boolean belongsToGroup = false;
				
				for (int[] stone : stoneList) {
					if (currentMove[0] == stone[0] && currentMove[1] == stone[1]) {
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
		if (length == 4 && stoneList.size() == 4) {
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
	
	public List<int[]> getDoubleThreatCounterMoves(int[] threat) {
		
		List<int[]> counterMoves = new ArrayList<int[]>();

		StonePack newStonePack = new StonePack(this);
		
		data[threat[0]][threat[1]] = color;
		
		newStonePack.getStoneList().add(threat);
		newStonePack.getStoneList().sort(stoneComparator);
		newStonePack.updateSides();
		
		counterMoves.addAll(newStonePack.getDoubleThreatKillingMoves());
		
		data[threat[0]][threat[1]] = GomokuModel.UNPLAYED;

		return counterMoves;
	}
	
	public List<int[]> getDoubleThreatNextAttackMoves(int[] threat) {
		
		List<int[]> nextAttackMoves = new ArrayList<int[]>();
		
		StonePack newStonePack = new StonePack(this);
		
		data[threat[0]][threat[1]] = color;
		
		newStonePack.getStoneList().add(threat);
		newStonePack.getStoneList().sort(stoneComparator);
		newStonePack.updateSides();
		
		nextAttackMoves.addAll(newStonePack.computeDoubleAttackMoves());
		
		data[threat[0]][threat[1]] = GomokuModel.UNPLAYED;
		
		return nextAttackMoves;
	}
	
	public List<int[]> getDoubleThreatKillingMoves() {
		
		List<int[]> counterMoves = new ArrayList<int[]>();
		
		int[] firstCell = stoneList.get(0);
		int[] lastCell = stoneList.get(stoneList.size() -1);
		
		int firstX = firstCell[0];
		int firstY = firstCell[1];
		
		int lastX = lastCell[0];
		int lastY = lastCell[1];
		
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
		if (stoneList.size() > 1) {
			int[] currentMove = new int[2];
			currentMove[0] = firstX + xIncrement;
			currentMove[1] = firstY + yIncrement;
			
			while (currentMove[0] != lastX || currentMove[1] != lastY) {
				
				boolean belongsToGroup = false;
				
				for (int[] stone : stoneList) {
					if (currentMove[0] == stone[0] && currentMove[1] == stone[1]) {
						belongsToGroup = true;
						break;
					}
				}
				
				if (!belongsToGroup) {
					int[] threatMove = new int[2];
					threatMove[0] = currentMove[0];
					threatMove[1] = currentMove[1];
					counterMoves.add(threatMove);
				}
				
				currentMove[0] += xIncrement;
				currentMove[1] += yIncrement;
			}
		}
		
		int[] beforeMove = new int[2];
		beforeMove[0] = firstX - xIncrement;
		beforeMove[1] = firstY - yIncrement;
		
		counterMoves.add(beforeMove);
		
		int[] afterMove = new int[2];
		afterMove[0] = lastX + xIncrement;
		afterMove[1] = lastY + yIncrement;
		
		counterMoves.add(afterMove);
		
		if (length < 4) {
			if (clearBefore < 3) {
				int[] afterAfterMove = new int[2];
				afterAfterMove[0] = firstX + 3 * xIncrement;
				afterAfterMove[1] = firstY + 3 * yIncrement;
				
				boolean contains = false;
				
				for (int[] counterMove : counterMoves) {
					if (counterMove[0] == afterAfterMove[0] && counterMove[1] == afterAfterMove[1]) {
						contains = true;
						break;
					}
				}
				
				if (!contains) {
					counterMoves.add(afterAfterMove);
				}
				
				if (clearBefore == 1) {
					int[] afterAfterAfterMove = new int[2];
					afterAfterAfterMove[0] = firstX + 4 * xIncrement;
					afterAfterAfterMove[1] = firstY + 4 * yIncrement;
					
					contains = false;
					
					for (int[] counterMove : counterMoves) {
						if (counterMove[0] == afterAfterAfterMove[0] && counterMove[1] == afterAfterAfterMove[1]) {
							contains = true;
							break;
						}
					}
					
					if (!contains) {
						counterMoves.add(afterAfterAfterMove);
					}
				}
			}
			if (clearAfter < 3) {
				int[] beforeBeforeMove = new int[2];
				beforeBeforeMove[0] = lastX - 3 * xIncrement;
				beforeBeforeMove[1] = lastY - 3 * yIncrement;
				
				boolean contains = false;
				
				for (int[] counterMove : counterMoves) {
					if (counterMove[0] == beforeBeforeMove[0] && counterMove[1] == beforeBeforeMove[1]) {
						contains = true;
						break;
					}
				}
				
				if (!contains) {
					counterMoves.add(beforeBeforeMove);
				}
				
				if (clearAfter == 1) {
					int[] beforeBeforeBeforeMove = new int[2];
					beforeBeforeBeforeMove[0] = lastX - 4 * xIncrement;
					beforeBeforeBeforeMove[1] = lastY - 4 * yIncrement;
					
					contains = false;
					
					for (int[] counterMove : counterMoves) {
						if (counterMove[0] == beforeBeforeBeforeMove[0] && counterMove[1] == beforeBeforeBeforeMove[1]) {
							contains = true;
							break;
						}
					}
					
					if (!contains) {
						counterMoves.add(beforeBeforeBeforeMove);
					}
				}
			} 
		}
		return counterMoves;
	}

	public int getClearBefore() {
		return clearBefore;
	}
	
	public int getClearAfter() {
		return clearAfter;
	}

	public int getColor() {
		return color;
	}
}
