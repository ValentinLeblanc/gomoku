package engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.GomokuModel;
import model.MoveData;

public class GomokuEngine {

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int DIAGONAL1 = 2;
	public static final int DIAGONAL2 = 3;

	private static final double OPPONENT_EVALUATION_FACTOR = 1;
	
	private GomokuModel model;
	
	private Comparator<? super int[]> moveComparator = new Comparator<int[]>() {

		@Override
		public int compare(int[] move1, int[] move2) {
			
			int sizeX = model.getColumnCount();
			int sizeY = model.getRowCount();
			
			int middleX = (sizeX - 1) / 2;
			int middleY = (sizeY - 1) / 2;
			
			int distanceToMiddle1 = (move1[0] - middleX) * (move1[0] - middleX) + (move1[1] - middleY) * (move1[1] - middleY);
			int distanceToMiddle2 = (move2[0] - middleX) * (move2[0] - middleX) + (move2[1] - middleY) * (move2[1] - middleY);
			
			return distanceToMiddle1 - distanceToMiddle2 > 0 ? 1 : distanceToMiddle1 == distanceToMiddle2 ? 0 : -1;
		}
		
	};

	public GomokuEngine(GomokuModel model) {
		this.model = model;
	}

	public double computeEvaluation(int[][] data, int color) {

		double eval = 0;

		int rowCount = data[0].length;
		int columnCount = data.length;

		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < columnCount; col++) {
				if (data[col][row] == color) {

					List<int[][]> stripes = new ArrayList<int[][]>();

					// east stripes
					int startX = Math.max(0, col - 4);
					for (int k = 0; startX + k < col + 1 && startX + k + 4 < data.length; k++) {
						int[][] stripe = new int[5][2];
						for (int f = 0; f < 5; f++) {
							stripe[f][0] = startX + k + f;
							stripe[f][1] = row;
						}
						stripes.add(stripe);
					}
					eval += computeStripes(data, stripes, color);
					stripes.clear();

					// south stripes
					int startY = Math.max(0, row - 4);
					for (int k = 0; startY + k < row + 1 && startY + k + 4 < data[0].length; k++) {
						int[][] stripe = new int[5][2];
						for (int f = 0; f < 5; f++) {
							stripe[f][0] = col;
							stripe[f][1] = startY + k + f;
						}
						stripes.add(stripe);
					}
					eval += computeStripes(data, stripes, color);
					stripes.clear();

					// southeast stripes
					startX = col;
					startY = row;
					int h = 0;
					while (startX > 0 && startY > 0 && h < 4) {
						h++;
						startX--;
						startY--;
					}

					for (int k = 0; startX + k < col + 1 && startX + k + 4 < data.length && startY + k < row + 1
							&& startY + k + 4 < data[0].length; k++) {
						int[][] stripe = new int[5][2];
						for (int f = 0; f < 5; f++) {
							stripe[f][0] = startX + k + f;
							stripe[f][1] = startY + k + f;
						}
						stripes.add(stripe);
					}
					eval += computeStripes(data, stripes, color);
					stripes.clear();

					// southwest stripes
					startX = col;
					startY = row;
					h = 0;
					while (startX < data.length - 1 && startY > 0 && h < 4) {
						h++;
						startX++;
						startY--;
					}

					for (int k = 0; startX - k > col - 1 && startX - k - 4 > -1 && startY + k < row + 1
							&& startY + k + 4 < data[0].length; k++) {
						int[][] stripe = new int[5][2];
						for (int f = 0; f < 5; f++) {
							stripe[f][0] = startX - k - f;
							stripe[f][1] = startY + k + f;
						}
						stripes.add(stripe);
					}
					eval += computeStripes(data, stripes, color);
					stripes.clear();

				}
			}

		}

		return eval;
	}

	public double computeNewEvaluation(int[][] data, int playingColor) {

		double eval = 0;

		int rowCount = data[0].length;
		int columnCount = data.length;

		for (int row = 0; row < rowCount; row++) {
			int[][] horizontalStripe = new int[columnCount][2];
			for (int col = 0; col < columnCount; col++) {
				horizontalStripe[col][0] = col;
				horizontalStripe[col][1] = row;
			}
			eval += computeStripePotential(data, horizontalStripe, HORIZONTAL, playingColor);
		}

		for (int col = 0; col < columnCount; col++) {
			int[][] verticalStripe = new int[rowCount][2];
			for (int row = 0; row < rowCount; row++) {
				verticalStripe[row][0] = col;
				verticalStripe[row][1] = row;
			}
			eval += computeStripePotential(data, verticalStripe, VERTICAL, playingColor);
		}

		for (int row = 0; row < rowCount; row++) {
			int[][] diagonal1Stripe = new int[columnCount - row][2];
			for (int col = 0; col < columnCount - row; col++) {
				diagonal1Stripe[col][0] = col;
				diagonal1Stripe[col][1] = row + col;
			}
			eval += computeStripePotential(data, diagonal1Stripe, DIAGONAL1, playingColor);
		}

		for (int col = 1; col < columnCount; col++) {
			int[][] diagonal1Stripe = new int[rowCount - col][2];
			for (int row = 0; row < rowCount - col; row++) {
				diagonal1Stripe[row][0] = col + row;
				diagonal1Stripe[row][1] = row;
			}
			eval += computeStripePotential(data, diagonal1Stripe, DIAGONAL1, playingColor);
		}

		for (int col = 0; col < columnCount; col++) {
			int[][] diagonal2Stripe = new int[rowCount - col][2];
			for (int row = rowCount - 1; row >= col; row--) {
				diagonal2Stripe[rowCount - 1 - row][0] = col - row + rowCount - 1;
				diagonal2Stripe[rowCount - 1 - row][1] = row;
			}
			eval += computeStripePotential(data, diagonal2Stripe, DIAGONAL2, playingColor);
		}

		for (int row = rowCount - 2; row >= 0; row--) {
			int[][] diagonal2Stripe = new int[row][2];
			for (int col = 0; col < row; col++) {
				diagonal2Stripe[col][0] = col;
				diagonal2Stripe[col][1] = row - col;
			}
			eval += computeStripePotential(data, diagonal2Stripe, DIAGONAL2, playingColor);
		}

		return eval;
	}

	private double computeStripePotential(int[][] data, int[][] stripe, int direction, int playingColor) {

		double stripePotential = 0;

		List<CellGroup> cellGroups = new ArrayList<CellGroup>();

		CellGroup currentCellGroup = null;
		int k = 0;

		while (k < stripe.length) {
			if (data[stripe[k][0]][stripe[k][1]] == playingColor) {
				if (currentCellGroup == null) {
					currentCellGroup = new CellGroup(data, direction);
					cellGroups.add(currentCellGroup);
				}
				currentCellGroup.addCell(new Cell(stripe[k][0], stripe[k][1]));
			} else if (data[stripe[k][0]][stripe[k][1]] == -playingColor) {
				currentCellGroup = null;
			}
			k++;
		}

		for (CellGroup cellGroup : cellGroups) {
			stripePotential += cellGroup.computePotential();
		}

		return stripePotential;
	}
	
	private List<int[]> findPotentialDefensiveMoves(int[][] data, int[][] stripe, int direction, int playingColor, int number) {
		
		List<int[]> potentielDefensiveMoves = new ArrayList<int[]>();
		
		List<CellGroup> cellGroups = new ArrayList<CellGroup>();
		
		CellGroup currentCellGroup = null;
		int k = 0;
		int clear = 0;
		
		while (k < stripe.length) {
			if (data[stripe[k][0]][stripe[k][1]] == playingColor) {
				if (currentCellGroup == null) {
					currentCellGroup = new CellGroup(data, direction);
					cellGroups.add(currentCellGroup);
				}
				currentCellGroup.addCell(new Cell(stripe[k][0], stripe[k][1]));
			} else if (data[stripe[k][0]][stripe[k][1]] == -playingColor) {
				currentCellGroup = null;
			} else if (currentCellGroup != null && data[stripe[k][0]][stripe[k][1]] == GomokuModel.UNPLAYED) {
				clear++;
				if (currentCellGroup.getCellList().size() + clear == 5) {
					currentCellGroup = null;
					clear = 0;
				}
			}
			k++;
		}
		
		for (CellGroup cellGroup : cellGroups) {
			List<int[]> cellGroupPotentielDefensiveMoves = cellGroup.findPotentialDefensiveMoves(number);
			if (cellGroupPotentielDefensiveMoves != null) {
				potentielDefensiveMoves.addAll(cellGroupPotentielDefensiveMoves);
			}
		}
		
		return potentielDefensiveMoves;
	}

	private double computeStripes(int[][] data, List<int[][]> stripes, int playingColor) {
		int eval = 0;

		for (int[][] stripe : stripes) {
			eval += computeStripe(data, stripe, playingColor);
		}
		return eval;
	}

	private double computeStripe(int[][] data, int[][] stripe, int playingColor) {

		double eval = 1;

		int[] weightStripe = new int[5];

		weightStripe[0] = 2;
		weightStripe[1] = 4;
		weightStripe[2] = 8;
		weightStripe[3] = 4;
		weightStripe[4] = 2;

		for (int k = 0; k < 5; k++) {
			int colIndex = stripe[k][0];
			int rowIndex = stripe[k][1];

			int value = data[colIndex][rowIndex];

			if (value == playingColor) {
				eval = eval * weightStripe[k];
			} else if (value == -playingColor) {
				eval = 1;
				return eval;
			}
		}
		return eval;
	}

	public int[][] checkForWin(int[][] data, int color) {

		int[][] result = new int[2][2];
		for (int i = 0; i < data[0].length; i++) {
			for (int j = 0; j < data.length; j++) {
				if (data[j][i] == color) {
					// check south
					int k = 1;
					int linedUpCount = 1;
					result[0][0] = j;
					result[0][1] = i;
					while (i + k < data[0].length && data[j][i + k] == color) {
						linedUpCount++;
						if (linedUpCount == 5) {
							result[1][0] = j;
							result[1][1] = i + k;
							return result;
						}
						k++;
					}
					// check east
					k = 1;
					linedUpCount = 1;
					result[0][0] = j;
					result[0][1] = i;
					while (j + k < data.length && data[j + k][i] == color) {
						linedUpCount++;
						if (linedUpCount == 5) {
							result[1][0] = j + k;
							result[1][1] = i;
							return result;
						}
						k++;
					}
					// check southeast
					k = 1;
					linedUpCount = 1;
					result[0][0] = j;
					result[0][1] = i;
					while (j + k < data.length && i + k < data[0].length && data[j + k][i + k] == color) {
						linedUpCount++;
						if (linedUpCount == 5) {
							result[1][0] = j + k;
							result[1][1] = i + k;
							return result;
						}
						k++;
					}
					// check southwest
					k = 1;
					linedUpCount = 1;
					result[0][0] = j;
					result[0][1] = i;
					while (j - k > -1 && i + k < data[0].length && data[j - k][i + k] == color) {
						linedUpCount++;
						if (linedUpCount == 5) {
							result[1][0] = j - k;
							result[1][1] = i + k;
							return result;
						}
						k++;
					}
				}
			}
		}

		return null;
	}

	public int[] computeMove(int[][] data, int playingColor) {

		int[] obviousMove = findObviousMove(data, playingColor);

		if (obviousMove != null) {
			return obviousMove;
		}

		List<int[]> analysedMoves = new ArrayList<int[]>();
		
		for (int i = 0; i < data[0].length; i++) {
			for (int j = 0; j < data.length; j++) {
				if (data[j][i] == GomokuModel.UNPLAYED) {
					int[] analyzedMove = new int[2];
					analyzedMove[0] = j;
					analyzedMove[1] = i;
					analysedMoves.add(analyzedMove);
				}
			}
		}
		
		analysedMoves.sort(moveComparator);
		
		int[] minMax = findBestMoveAmongTheseMoves(data, playingColor, analysedMoves);
		
		System.out.println("minMax");

		return minMax;
	}
	
	private int[] findBestMoveUsingMinMax(int[][] data, int playingColor, List<int[]> analysedMoves) {
		
		double maxFirstEvaluation = Double.NEGATIVE_INFINITY;

		int[] bestFirstMove = new int[2];
		
		for (int[] firstMove : analysedMoves) {
			
			model.firePropertyChange(GomokuModel.ANALYSED_MOVE, firstMove);
			
			double minFirstEvaluation = Double.POSITIVE_INFINITY;
			int[] bestFirstOpponentMove = new int[2];

			data[firstMove[0]][firstMove[1]] = playingColor;
			
			for (int[] firstOpponentMove : analysedMoves) {
				
				double maxSecondEvaluation = Double.NEGATIVE_INFINITY;
				int[] bestSecondMove = new int[2];

				if (data[firstOpponentMove[0]][firstOpponentMove[1]] == GomokuModel.UNPLAYED) {
					
					data[firstOpponentMove[0]][firstOpponentMove[1]] = -playingColor;

					for (int[] secondMove : analysedMoves) {
						
						double minSecondEvaluation = Double.POSITIVE_INFINITY;
						int[] bestSecondOpponentMove = new int[2];
						
						if (data[secondMove[0]][secondMove[1]] == GomokuModel.UNPLAYED) {
							
							data[secondMove[0]][secondMove[1]] = playingColor;
							
							for (int[] secondOpponentMove : analysedMoves) {
								
								if (data[secondOpponentMove[0]][secondOpponentMove[1]] == GomokuModel.UNPLAYED) {
									
									data[secondOpponentMove[0]][secondOpponentMove[1]] = -playingColor;
									
									double secondOpponentEvaluation = computeEvaluation(data, playingColor) - OPPONENT_EVALUATION_FACTOR * computeEvaluation(data, -playingColor);
									if (secondOpponentEvaluation < minSecondEvaluation) {
										minSecondEvaluation = secondOpponentEvaluation;
										bestSecondOpponentMove = secondOpponentMove;
									}
									
									data[secondOpponentMove[0]][secondOpponentMove[1]] = GomokuModel.UNPLAYED;
									
									if (minSecondEvaluation < maxSecondEvaluation) {
										break;
									}
									
								}
							}
							
							data[secondMove[0]][secondMove[1]] = GomokuModel.UNPLAYED;

							data[bestSecondOpponentMove[0]][bestSecondOpponentMove[1]] = -playingColor;
							
							double secondEvaluation = computeEvaluation(data, playingColor) - OPPONENT_EVALUATION_FACTOR * computeEvaluation(data, -playingColor);
							if (secondEvaluation > maxSecondEvaluation) {
								maxSecondEvaluation = secondEvaluation;
								bestSecondMove = secondMove;
							}
							
							data[bestSecondOpponentMove[0]][bestSecondOpponentMove[1]] = GomokuModel.UNPLAYED;
							
							if (maxSecondEvaluation > minFirstEvaluation) {
								break;
							}
						}
						

					}
					
					data[firstOpponentMove[0]][firstOpponentMove[1]] = GomokuModel.UNPLAYED;
							
					data[bestSecondMove[0]][bestSecondMove[1]] = playingColor;
					
					double firstOpponentEvaluation = computeEvaluation(data, playingColor) - OPPONENT_EVALUATION_FACTOR * computeEvaluation(data, -playingColor);
					if (firstOpponentEvaluation < minFirstEvaluation) {
						minFirstEvaluation = firstOpponentEvaluation;
						bestFirstOpponentMove = firstOpponentMove;
					}
					
					data[bestSecondMove[0]][bestSecondMove[1]] = GomokuModel.UNPLAYED;
					
					if (minFirstEvaluation < maxFirstEvaluation) {
						break;
					}
				}
				

			}
			
			data[bestFirstOpponentMove[0]][bestFirstOpponentMove[1]] = -playingColor;
			
			double firstEvaluation = computeEvaluation(data, playingColor) - OPPONENT_EVALUATION_FACTOR * computeEvaluation(data, -playingColor);
			if (firstEvaluation > maxFirstEvaluation) {
				maxFirstEvaluation = firstEvaluation;
				bestFirstMove = firstMove;
			}
			
			data[bestFirstOpponentMove[0]][bestFirstOpponentMove[1]] = GomokuModel.UNPLAYED;
			
			data[firstMove[0]][firstMove[1]] = GomokuModel.UNPLAYED;

		}
		
		return bestFirstMove;
	}

	@SuppressWarnings("unchecked")
	private int[] findObviousMove(int[][] data, int playingColor) {

		// check for a win
		int[] winningMove = findWinningMove(data, playingColor);
		if (winningMove != null) {
			System.out.println("win");
			return winningMove;
		}
		
		// defend from an enemy win
		int[] opponentWinningMove = findWinningMove(data, -playingColor);
		if (opponentWinningMove != null) {
			System.out.println("counter win");
			return opponentWinningMove;
		}
		
		// check for a double strike
		int[] doubleAttackMove = findDouble4AttackMove(data, playingColor);
		if (doubleAttackMove != null) {
			System.out.println("imminent 4-line strike found");
			return doubleAttackMove;
		}
		
		// check for another strike
		Object[] result = findStrikeWinningMove(data, playingColor, null);
		
		if (result != null) {
			System.out.println("4-line strike found");
			int[] strikeWinningMove = (int[]) result[0];
			return strikeWinningMove;
		}
		
		// defend from an enemy 3 stone attack
		List<int[]> defendingMoves = findPotentialDefensiveMoves(data, -playingColor, 3);

		if (!defendingMoves.isEmpty()) {
			System.out.println("counter imminent 4-line strike found");
			return findBestMoveAmongTheseMoves(data, playingColor, defendingMoves);
		}
		
		// defend from an enemy strike
		result = findStrikeWinningMove(data, -playingColor, null);
		if (result != null) {
			defendingMoves = (List<int[]>) result[1];
			
			if (!defendingMoves.isEmpty()) {
				System.out.println("counter 4-line strike found");
				
				int[] defendingMove = findBestMoveAmongTheseMoves(data, playingColor, defendingMoves);
				
				data[defendingMove[0]][defendingMove[1]] = playingColor;
				
				while (findStrikeWinningMove(data, -playingColor, null) != null) {
					data[defendingMove[0]][defendingMove[1]] = GomokuModel.UNPLAYED;
					defendingMoves.remove(defendingMove);
					defendingMove = findBestMoveAmongTheseMoves(data, playingColor, defendingMoves);
					data[defendingMove[0]][defendingMove[1]] = playingColor;
				}
				
				data[defendingMove[0]][defendingMove[1]] = GomokuModel.UNPLAYED;
				return defendingMove;
				
			}
		}
		
		// check for a double 3-stone attack
		int[] double3AttackMove = findDouble3AttackMove(data, playingColor);
		if (double3AttackMove != null) {
			System.out.println("imminent 3-line strike found");
			return double3AttackMove;
		}
		
		// check for a double 3-free stone connection
//		result = findDoubleStrikeConnectionMove(data, playingColor);
//		if (result != null) {
//			System.out.println("3-line strike found");
//			int[] strikeWinningMove = (int[]) result[0];
//			return strikeWinningMove;
//		}

		return null;
	}
	
	public int getMovePlayedNumber(int[][] data) {
		
		int number = 0;
		
		for (int i = 0; i < data[0].length; i++) {
			for (int j = 0; j < data.length; j++) {
				if (data[j][i] != GomokuModel.UNPLAYED) {
					number++;
				}
			}
		}
		
		return number;
	}

	private int[] findWinningMove(int[][] data, int playingColor) {

		int[] winningMove = new int[2];

		for (int i = 0; i < data[0].length; i++) {
			for (int j = 0; j < data.length; j++) {
				if (data[j][i] == GomokuModel.UNPLAYED) {
					
					data[j][i] = playingColor;
					
					// check for win
					int[][] winData = checkForWin(data, playingColor);
					
					data[j][i] = GomokuModel.UNPLAYED;
					
					if (winData != null) {
						winningMove = new int[2];
						winningMove[0] = j;
						winningMove[1] = i;
						return winningMove;
					}
				}
			}
		}

		return null;
	}

	private List<int[]> findAttack4Moves(int[][] data, int playingColor) {

		List<int[]> attackMoves = new ArrayList<int[]>();

		for (int i = 0; i < data[0].length; i++) {
			for (int j = 0; j < data.length; j++) {
				if (data[j][i] == GomokuModel.UNPLAYED) {
					
					data[j][i] = playingColor;

					// find a winning move
					int[] winData = findWinningMove(data, playingColor);
					
					data[j][i] = GomokuModel.UNPLAYED;
					
					if (winData != null) {
						int[] attackMove = new int[2];
						attackMove[0] = j;
						attackMove[1] = i;
						attackMoves.add(attackMove);
					}
				}
			}
		}

		return attackMoves;
	}
	
	@SuppressWarnings("unchecked")
	private Object[] findAttackMoves(int[][] data, int playingColor, int number) {
		
		Object[] result = new Object[2];
		
		result[0] = new ArrayList<int[]>();
		result[1] = new ArrayList<int[]>();
		
		List<int[]> attackMoves = (List<int[]>) result[0];
		List<int[]> potentialDefendingMoves = (List<int[]>) result[1];
		
		for (int i = 0; i < data[0].length; i++) {
			for (int j = 0; j < data.length; j++) {
				if (data[j][i] == GomokuModel.UNPLAYED) {
					
					data[j][i] = playingColor;
					
					// find a winning move
					potentialDefendingMoves = findPotentialDefensiveMoves(data, playingColor, number);
					
					data[j][i] = GomokuModel.UNPLAYED;
					
					if (!potentialDefendingMoves.isEmpty()) {
						int[] attackMove = new int[2];
						attackMove[0] = j;
						attackMove[1] = i;
						attackMoves.add(attackMove);
						((List<int[]>) result[1]).addAll(potentialDefendingMoves);
					}
				}
			}
		}
		
		return result;
	}

	private List<int[]> findPotentialDefensiveMoves(int[][] data, int playingColor, int number) {

		List<int[]> potentialDefensiveMoves = new ArrayList<int[]>();
		
		int rowCount = data[0].length;
		int columnCount = data.length;

		for (int row = 0; row < rowCount; row++) {
			int[][] horizontalStripe = new int[columnCount][2];
			for (int col = 0; col < columnCount; col++) {
				horizontalStripe[col][0] = col;
				horizontalStripe[col][1] = row;
			}
			potentialDefensiveMoves.addAll(findPotentialDefensiveMoves(data, horizontalStripe, HORIZONTAL, playingColor, number));
		}

		for (int col = 0; col < columnCount; col++) {
			int[][] verticalStripe = new int[rowCount][2];
			for (int row = 0; row < rowCount; row++) {
				verticalStripe[row][0] = col;
				verticalStripe[row][1] = row;
			}
			potentialDefensiveMoves.addAll(findPotentialDefensiveMoves(data, verticalStripe, VERTICAL, playingColor, number));
		}

		for (int row = 0; row < rowCount; row++) {
			int[][] diagonal1Stripe = new int[columnCount - row][2];
			for (int col = 0; col < columnCount - row; col++) {
				diagonal1Stripe[col][0] = col;
				diagonal1Stripe[col][1] = row + col;
			}
			potentialDefensiveMoves.addAll(findPotentialDefensiveMoves(data, diagonal1Stripe, DIAGONAL1, playingColor, number));
		}

		for (int col = 1; col < columnCount; col++) {
			int[][] diagonal1Stripe = new int[rowCount - col][2];
			for (int row = 0; row < rowCount - col; row++) {
				diagonal1Stripe[row][0] = col + row;
				diagonal1Stripe[row][1] = row;
			}
			potentialDefensiveMoves.addAll(findPotentialDefensiveMoves(data, diagonal1Stripe, DIAGONAL1, playingColor, number));
		}

		for (int col = 0; col < columnCount; col++) {
			int[][] diagonal2Stripe = new int[rowCount - col][2];
			for (int row = rowCount - 1; row >= col; row--) {
				diagonal2Stripe[rowCount - 1 - row][0] = col - row + rowCount - 1;
				diagonal2Stripe[rowCount - 1 - row][1] = row;
			}
			potentialDefensiveMoves.addAll(findPotentialDefensiveMoves(data, diagonal2Stripe, DIAGONAL2, playingColor, number));
		}

		for (int row = rowCount - 2; row >= 0; row--) {
			int[][] diagonal2Stripe = new int[row][2];
			for (int col = 0; col < row; col++) {
				diagonal2Stripe[col][0] = col;
				diagonal2Stripe[col][1] = row - col;
			}
			potentialDefensiveMoves.addAll(findPotentialDefensiveMoves(data, diagonal2Stripe, DIAGONAL2, playingColor, number));
		}

		return potentialDefensiveMoves;
	}

	private int[] findDouble4AttackMove(int[][] data, int playingColor) {
		
		List<int[]> attackMoves = findAttack4Moves(data, playingColor);
	
		for (int[] attackMove : attackMoves) {
			// attack
			data[attackMove[0]][attackMove[1]] = playingColor;
			
			// defend
			int[] defendingMove = findWinningMove(data, playingColor);
			data[defendingMove[0]][defendingMove[1]] = -playingColor;
			
			// look for another win
			int[] otherAttack = findWinningMove(data, playingColor);
			
			data[attackMove[0]][attackMove[1]] = GomokuModel.UNPLAYED;
			data[defendingMove[0]][defendingMove[1]] = GomokuModel.UNPLAYED;
			
			if (otherAttack != null) {
				return attackMove;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private int[] findDouble3AttackMove(int[][] data, int playingColor) {
		
		Object[] result = findAttackMoves(data, playingColor, 3);
		List<int[]> attackMoves = (List<int[]>) result[0];
		List<int[]> defendingMoves = (List<int[]>) result[1];
		
		for (int[] attackMove : attackMoves) {
			// attack
			data[attackMove[0]][attackMove[1]] = playingColor;
			
			// defend
			
			boolean defenseWorked = false;
			for (int[] defendingMove : defendingMoves) {
				
				if (data[defendingMove[0]][defendingMove[1]] == GomokuModel.UNPLAYED) {
					
					data[defendingMove[0]][defendingMove[1]] = -playingColor;
					
					// look for another attack
					Object[] otherResult = findAttackMoves(data, playingColor, 4);
					
					List<int[]> otherAttackMoves = (List<int[]>) otherResult[0];
					
					data[defendingMove[0]][defendingMove[1]] = GomokuModel.UNPLAYED;
					
					if (otherAttackMoves.isEmpty()) {
						defenseWorked = true;
						break;
					}
				}
			}
			
			data[attackMove[0]][attackMove[1]] = GomokuModel.UNPLAYED;
			
			if (!defenseWorked) {
				return attackMove;
			}
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private Object[] findStrikeWinningMove(int[][] data, int playingColor, List<int[]> alreadyTriedMoves) {

		Object[] result = null;
		
		// opponent attack
		int[] opponentWinningMove = findWinningMove(data, -playingColor);
		if (opponentWinningMove != null) {
			
			// defend
			data[opponentWinningMove[0]][opponentWinningMove[1]] = playingColor;
			
			// ... and attack again
			int[] winningMove = findWinningMove(data, playingColor);
			if (winningMove != null) {
				
				// opponent defense
				data[winningMove[0]][winningMove[1]] = -playingColor;
				
				Object[] nextAttemp = findStrikeWinningMove(data, playingColor, alreadyTriedMoves);
				
				data[opponentWinningMove[0]][opponentWinningMove[1]] = GomokuModel.UNPLAYED;
				data[winningMove[0]][winningMove[1]] = GomokuModel.UNPLAYED;
				
				if (nextAttemp != null) {
					return nextAttemp;
				}
			}
			
			data[opponentWinningMove[0]][opponentWinningMove[1]] = GomokuModel.UNPLAYED;

		} else {
			List<int[]> attackMoves = findAttack4Moves(data, playingColor);
			
			attackMoveLoop : for (int[] attackMove : attackMoves) {
				
				if (alreadyTriedMoves != null) {
					for (int[] alreadyTriedMove : alreadyTriedMoves) {
						if (alreadyTriedMove[0] == attackMove[0] && alreadyTriedMove[1] == attackMove[1]) {
							continue attackMoveLoop;
						}
					}
				}
				
				// attack
 				data[attackMove[0]][attackMove[1]] = playingColor;
				
				// defend
				int[] defendingMove = findWinningMove(data, playingColor);
				data[defendingMove[0]][defendingMove[1]] = -playingColor;
				
				// look for another win
				int[] otherAttack = findWinningMove(data, playingColor);
				
				if (otherAttack != null) {
					result = new Object[2];
					result[0] = attackMove;
					
					data[attackMove[0]][attackMove[1]] = GomokuModel.UNPLAYED;
					data[defendingMove[0]][defendingMove[1]] = GomokuModel.UNPLAYED;
					
					result[1] = findPotentialDefensiveMoves(data, playingColor, 3);
					
					return result;
				}
				
				
				Object[] nextAttemp = findStrikeWinningMove(data, playingColor, alreadyTriedMoves);
				
				data[attackMove[0]][attackMove[1]] = GomokuModel.UNPLAYED;
				data[defendingMove[0]][defendingMove[1]] = GomokuModel.UNPLAYED;
				
				if (nextAttemp != null) {
					result = new Object[2];
					result[0] = attackMove;
					result[1] = nextAttemp[1];
					
					((List<int[]>) result[1]).add(attackMove);
					((List<int[]>) result[1]).add(defendingMove);

					return result;
				}
				
				if (alreadyTriedMoves == null) {
					alreadyTriedMoves = new ArrayList<int[]>();
				}
				
				alreadyTriedMoves.add(attackMove);
			}
			
		}
		
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private Object[] findDoubleStrikeConnectionMove(int[][] data, int playingColor) {

		Object[] result = null;
		
		// case of an opponent attack
		int[] opponentWinningMove = findWinningMove(data, -playingColor);
		if (opponentWinningMove != null) {
			
			// defend
			data[opponentWinningMove[0]][opponentWinningMove[1]] = playingColor;
			
			// ... and attack again
			int[] winningMove = findWinningMove(data, playingColor);
			if (winningMove != null) {
				
				// opponent defense
				data[winningMove[0]][winningMove[1]] = -playingColor;
				
				Object[] nextAttemp = findDoubleStrikeConnectionMove(data, playingColor);
				
				data[opponentWinningMove[0]][opponentWinningMove[1]] = GomokuModel.UNPLAYED;
				data[winningMove[0]][winningMove[1]] = GomokuModel.UNPLAYED;
				
				if (nextAttemp != null) {
					return nextAttemp;
				}
			}
			return null;
		}
		
		// case of an opponent strike
		Object[] opponentWinningStrike = findStrikeWinningMove(data, -playingColor, null);
		if (opponentWinningStrike != null) {
			
			// defend
			List<int[]> defendingMoves = (List<int[]>) opponentWinningStrike[1];
			for (int[] defendingMove : defendingMoves) {
				data[defendingMove[0]][defendingMove[1]] = playingColor;

				
			}
		}
		
		Object[] findAttackResult = findAttackMoves(data, playingColor, 3);
		
		List<int[]> attackMoves = (List<int[]>) findAttackResult[0];
		
		for (int[] attackMove : attackMoves) {
			// attack
			data[attackMove[0]][attackMove[1]] = playingColor;
			
			// defend
			boolean defenseWorked = false;
			
			List<int[]> defendingMoves = findPotentialDefensiveMoves(data, playingColor, 3);

 			for (int[] defendingMove : defendingMoves) {
				
				data[defendingMove[0]][defendingMove[1]] = -playingColor;
				
				// look for another attack
				Object[] otherFindAttackResult = findAttackMoves(data, playingColor, 4);

				List<int[]> otherAttackMoves = (List<int[]>) otherFindAttackResult[0];
				
				data[defendingMove[0]][defendingMove[1]] = GomokuModel.UNPLAYED;
				
				if (otherAttackMoves.isEmpty()) {
					defenseWorked = true;
					break;
				}
			}
			
			
			if (!defenseWorked) {
				
				data[attackMove[0]][attackMove[1]] = GomokuModel.UNPLAYED;
				
				result = new Object[2];
				result[0] = attackMove;
				
				return result;
			}
			
			Object[] nextAttemp = findDoubleStrikeConnectionMove(data, playingColor);
			if (nextAttemp != null) {
				result = new Object[2];
				result[0] = attackMove;
				
				data[attackMove[0]][attackMove[1]] = GomokuModel.UNPLAYED;

				return result;
			}
			
			data[attackMove[0]][attackMove[1]] = GomokuModel.UNPLAYED;
				
		}
		
		return result;
	}

	/**
	 * 
	 * For each analyzed move, computer plays the best answer according to evaluation.
	 * Then returns highest resulting evaluation.
	 * 
	 * @param data
	 * @param playingColor
	 * @param analysedMoves
	 * @return
	 */
	private int[] findBestMoveAmongTheseMoves(int[][] data, int playingColor, List<int[]> analysedMoves) {
		
		double maxEvaluation = Double.NEGATIVE_INFINITY;
		
		int[] bestMove = new int[2];
		
		for (int[] analysedMove : analysedMoves) {
			
			int[] opponentBestMove = new int[2];
			
			double minEvaluation = Double.POSITIVE_INFINITY;
			
			data[analysedMove[0]][analysedMove[1]] = playingColor;
			
			for (int i = 0; i < data[0].length; i++) {
				for (int j = 0; j < data.length; j++) {
					if (data[j][i] == GomokuModel.UNPLAYED) {
						data[j][i] = -playingColor;
						
						double evaluation = computeEvaluation(data, playingColor) - OPPONENT_EVALUATION_FACTOR * computeEvaluation(data, -playingColor);
						if (evaluation < minEvaluation) {
							minEvaluation = evaluation;
							opponentBestMove[0] = j;
							opponentBestMove[1] = i;
						}
						
						data[j][i] = GomokuModel.UNPLAYED;
						
						if (minEvaluation < maxEvaluation) { 
							break;
						}
					}
				}
			}
			
			data[opponentBestMove[0]][opponentBestMove[1]] = -playingColor;

			double evaluation = computeEvaluation(data, playingColor) - OPPONENT_EVALUATION_FACTOR * computeEvaluation(data, -playingColor);
			if (evaluation > maxEvaluation) {
				maxEvaluation = evaluation;
				bestMove = analysedMove;
			}
			
			data[opponentBestMove[0]][opponentBestMove[1]] = GomokuModel.UNPLAYED;
			data[analysedMove[0]][analysedMove[1]] = GomokuModel.UNPLAYED;
		}
		
		return bestMove;
	}
	
	

}
