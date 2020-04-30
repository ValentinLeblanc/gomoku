package engine;

import java.util.ArrayList;
import java.util.List;

import model.GomokuModel;

public class StoneGroup {

	private int[][] data;
	private int direction;
	private int color;
	
	private int length = 0;

	private List<Stone> stoneList;
	
	List<int[]> threatMoves;
	
	private int clearBefore;
	private int clearAfter;

	public StoneGroup(int[][] data, int direction, int color) {
		this.data = data;
		this.direction = direction;
		this.color = color;
	}

	public List<Stone> getStoneList() {
		if (stoneList == null) {
			stoneList = new ArrayList<Stone>();
		}
		return stoneList;
	}
	
	public boolean is5Group() {
		return stoneList.size() == 5;
	}
	
	public boolean containsAll(StoneGroup stoneGroup) {
		
		if (stoneGroup.getDirection() != getDirection()) {
			return false;
		}
		
		if (stoneGroup.stoneList.size() != stoneList.size()) {
			return false;
		}
		
		for (Stone stone : stoneGroup.getStoneList()) {
			if (!getStoneList().contains(stone)) {
				return false;
			}
		}
			
		return true;
	}
	
	void updateGroup() {
		
		clearBefore = 0;
		clearAfter = 0;
		
		Stone firstCell = stoneList.get(0);
		Stone lastCell = stoneList.get(stoneList.size() -1);
		
		int firstX = firstCell.getX();
		int firstY = firstCell.getY();
		
		int lastX = lastCell.getX();
		int lastY = lastCell.getY();

		
		if (getDirection() == GomokuEngine.HORIZONTAL) {
			
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
		} else if (getDirection() == GomokuEngine.VERTICAL) {
			
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
		} else if (getDirection() == GomokuEngine.DIAGONAL1) {
			
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
		} else if (getDirection() == GomokuEngine.DIAGONAL2) {
			
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

	public List<int[]> getSingleThreatMoves(int number) {
		
		if (threatMoves == null) {
			// if the size is not compatible
			if (stoneList.size() != number) {
				return threatMoves;
			}
			
			// if there is not enough space
			if (length + clearBefore + clearAfter < 5) {
				return threatMoves;
			}
			
			Stone firstCell = stoneList.get(0);
			Stone lastCell = stoneList.get(stoneList.size() - 1);
			
			int firstX = firstCell.getX();
			int firstY = firstCell.getY();
			
			int lastX = lastCell.getX();
			int lastY = lastCell.getY();
			
			
			int xIncrement = 0;
			int yIncrement = 0;
			
			if (getDirection() == GomokuEngine.HORIZONTAL) {
				xIncrement = 1;
			} else if (getDirection() == GomokuEngine.VERTICAL) {
				yIncrement = 1;
			} else if (getDirection() == GomokuEngine.DIAGONAL1) {
				xIncrement = 1;
				yIncrement = 1;
			} else if (getDirection() == GomokuEngine.DIAGONAL2) {
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
					
					for (Stone cell : stoneList) {
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
		}
		
		return threatMoves;
	}
	
	
	public List<int[]> getDoubleThreatMoves(int number) {
		
		if (threatMoves == null) {
			
			// if the size is not compatible
			if (stoneList.size() != number) {
				return threatMoves;
			}
			
			if (length == 5) {
				return threatMoves;
			}
			
			// if there is not enough space
			if (clearBefore == 0 || clearAfter == 0 || length + clearBefore + clearAfter < 6) {
				return threatMoves;
			}
			
			Stone firstCell = stoneList.get(0);
			Stone lastCell = stoneList.get(stoneList.size() - 1);
			
			int firstX = firstCell.getX();
			int firstY = firstCell.getY();
			
			int lastX = lastCell.getX();
			int lastY = lastCell.getY();
			
			
			int xIncrement = 0;
			int yIncrement = 0;
			
			if (getDirection() == GomokuEngine.HORIZONTAL) {
				xIncrement = 1;
			} else if (getDirection() == GomokuEngine.VERTICAL) {
				yIncrement = 1;
			} else if (getDirection() == GomokuEngine.DIAGONAL1) {
				xIncrement = 1;
				yIncrement = 1;
			} else if (getDirection() == GomokuEngine.DIAGONAL2) {
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
					
					for (Stone cell : stoneList) {
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
		}
		
		return threatMoves;
	}

	public int getDepth() {
		
		int depth = 0;
		
		for (Stone stone : stoneList) {
//			if (stone.getDepth() > depth) {
//				depth = stone.getDepth();
//			}
		}
		
		return depth;
	}

	public int getDirection() {
		return direction;
	}

	public List<int[]> getSpreadVectors(int[] threat) {
		
		List<int[]> spreadVectors = new ArrayList<int[]>();
		
		int[] eastVector = { 1, 0 };
		int[] westVector = { -1, 0 };
		int[] southVector = { 0, 1 };
		int[] northVector = { 0, -1 };
		int[] southEastVector = { 1, 1 };
		int[] northWestVector = { -1, -1 };
		int[] northEastVector = { 1, -1 };
		int[] southWestVector = { -1, 1 };
		
		spreadVectors.add(eastVector);
		spreadVectors.add(westVector);
		spreadVectors.add(southVector);
		spreadVectors.add(northVector);
		spreadVectors.add(southEastVector);
		spreadVectors.add(northWestVector);
		spreadVectors.add(northEastVector);
		spreadVectors.add(southWestVector);
		
		if (getDirection() == GomokuEngine.HORIZONTAL) {
			spreadVectors.remove(eastVector);
			spreadVectors.remove(westVector);
		} else if (getDirection() == GomokuEngine.VERTICAL) {
			spreadVectors.remove(southVector);
			spreadVectors.remove(northVector);
		} else if (getDirection() == GomokuEngine.DIAGONAL1) {
			spreadVectors.remove(southEastVector);
			spreadVectors.remove(northWestVector);
		} else if (getDirection() == GomokuEngine.DIAGONAL2) {
			spreadVectors.remove(northEastVector);
			spreadVectors.remove(southWestVector);
		}
		
		return spreadVectors;
	}
	
	private boolean isAtEast(int[] threat) {
		
		int maxX = Math.max(stoneList.get(0).getX(), stoneList.get(stoneList.size() - 1).getX());
		
		return threat[0] > maxX;
	}

	private boolean isInside(int[] threat) {
		
		int minX = Math.min(stoneList.get(0).getX(), stoneList.get(stoneList.size() - 1).getX());
		int minY = Math.min(stoneList.get(0).getY(), stoneList.get(stoneList.size() - 1).getY());
		int maxX = Math.max(stoneList.get(0).getX(), stoneList.get(stoneList.size() - 1).getX());
		int maxY = Math.max(stoneList.get(0).getY(), stoneList.get(stoneList.size() - 1).getY());
		
		if (threat[0] < minX || threat[0] > maxX || threat[1] < minY || threat[1] > maxY) {
			return false;
		}
		
		return true;
	}

	public int getColor() {
		return color;
	}

	public List<int[]> getCounterMoves(int[] threat) {
		
		
		
		return null;
	}
}
