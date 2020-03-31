package engine;

import java.util.ArrayList;
import java.util.List;

import model.GomokuModel;

public class GomokuEngine {

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int DIAGONAL1 = 2;
	public static final int DIAGONAL2 = 3;

	private static final double OPPONENT_EVALUATION_FACTOR = 1.5;

	public GomokuEngine() {
	}

	public double computeOldEvaluation(int[][] data, int color) {

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

	public double computeEvaluation(int[][] data, int playingColor) {

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
	
	private int computeAttackCount(int[][] data, int[][] stripe, int direction, int playingColor, int number) {
		
		int attack3Count = 0;
		
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
			attack3Count += cellGroup.isAnAttack(number) ? 1 : 0;
		}
		
		return attack3Count;
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
		
		return findBestMoveAmongTheseMoves(data, playingColor, analysedMoves);
	}

	private int[] findObviousMove(int[][] data, int playingColor) {

		// check for win
		int[] winningMove = findWinningMove(data, playingColor);
		if (winningMove != null) {
			return winningMove;
		}

		// defend from win
		int[] opponentWinningMove = findWinningMove(data, -playingColor);
		if (opponentWinningMove != null) {
			return opponentWinningMove;
		}
		
		// check for attack strike to win
		int[] strikeWinningMove = find4StrikeWinningMove(data, playingColor);
		if (strikeWinningMove != null) {
			return strikeWinningMove;
		}
		
		// defend from double attack
		List<int[]> defendingMoves = findDefendingMoves(data, playingColor, 4);

		if (!defendingMoves.isEmpty()) {
			return findBestMoveAmongTheseMoves(data, playingColor, defendingMoves);
		}
		
		List<int[]> counterOpponentStrikeMoves = findDefendingOponentStrikeMoves(data, playingColor);
		
		if (!counterOpponentStrikeMoves.isEmpty()) {
			return findBestMoveAmongTheseMoves(data, playingColor, counterOpponentStrikeMoves);
		}
		
		strikeWinningMove = find3StrikeWinningMove(data, playingColor);
		if (strikeWinningMove != null) {
			return strikeWinningMove;
		}

		return null;
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
	
	private List<int[]> findAttackMoves(int[][] data, int playingColor, int number) {
		
		List<int[]> attackMoves = new ArrayList<int[]>();
		
		for (int i = 0; i < data[0].length; i++) {
			for (int j = 0; j < data.length; j++) {
				if (data[j][i] == GomokuModel.UNPLAYED) {
					
					data[j][i] = playingColor;
					
					// find a winning move
					int attackCount = findAttackCount(data, playingColor, number);
					
					data[j][i] = GomokuModel.UNPLAYED;
					
					if (attackCount > 0) {
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

	private int findAttackCount(int[][] data, int playingColor, int number) {

		int attack3Count = 0;
		
		int rowCount = data[0].length;
		int columnCount = data.length;

		for (int row = 0; row < rowCount; row++) {
			int[][] horizontalStripe = new int[columnCount][2];
			for (int col = 0; col < columnCount; col++) {
				horizontalStripe[col][0] = col;
				horizontalStripe[col][1] = row;
			}
			attack3Count += computeAttackCount(data, horizontalStripe, HORIZONTAL, playingColor, number);
		}

		for (int col = 0; col < columnCount; col++) {
			int[][] verticalStripe = new int[rowCount][2];
			for (int row = 0; row < rowCount; row++) {
				verticalStripe[row][0] = col;
				verticalStripe[row][1] = row;
			}
			attack3Count += computeAttackCount(data, verticalStripe, VERTICAL, playingColor, number);
		}

		for (int row = 0; row < rowCount; row++) {
			int[][] diagonal1Stripe = new int[columnCount - row][2];
			for (int col = 0; col < columnCount - row; col++) {
				diagonal1Stripe[col][0] = col;
				diagonal1Stripe[col][1] = row + col;
			}
			attack3Count += computeAttackCount(data, diagonal1Stripe, DIAGONAL1, playingColor, number);
		}

		for (int col = 1; col < columnCount; col++) {
			int[][] diagonal1Stripe = new int[rowCount - col][2];
			for (int row = 0; row < rowCount - col; row++) {
				diagonal1Stripe[row][0] = col + row;
				diagonal1Stripe[row][1] = row;
			}
			attack3Count += computeAttackCount(data, diagonal1Stripe, DIAGONAL1, playingColor, number);
		}

		for (int col = 0; col < columnCount; col++) {
			int[][] diagonal2Stripe = new int[rowCount - col][2];
			for (int row = rowCount - 1; row >= col; row--) {
				diagonal2Stripe[rowCount - 1 - row][0] = col - row + rowCount - 1;
				diagonal2Stripe[rowCount - 1 - row][1] = row;
			}
			attack3Count += computeAttackCount(data, diagonal2Stripe, DIAGONAL2, playingColor, number);
		}

		for (int row = rowCount - 2; row >= 0; row--) {
			int[][] diagonal2Stripe = new int[row][2];
			for (int col = 0; col < row; col++) {
				diagonal2Stripe[col][0] = col;
				diagonal2Stripe[col][1] = row - col;
			}
			attack3Count += computeAttackCount(data, diagonal2Stripe, DIAGONAL2, playingColor, number);
		}

		return attack3Count;
	}

	private int[] find4StrikeWinningMove(int[][] data, int playingColor) {

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
				
				int[] nextAttemp = find4StrikeWinningMove(data, playingColor);
				
				if (nextAttemp != null) {
					data[opponentWinningMove[0]][opponentWinningMove[1]] = GomokuModel.UNPLAYED;
					data[winningMove[0]][winningMove[1]] = GomokuModel.UNPLAYED;
					return nextAttemp;
				}
			}
			
			data[opponentWinningMove[0]][opponentWinningMove[1]] = GomokuModel.UNPLAYED;
			
		} else {
			List<int[]> attackMoves = findAttack4Moves(data, playingColor);
			
			for (int[] attackMove : attackMoves) {
				// attack
				data[attackMove[0]][attackMove[1]] = playingColor;
				
				// defend
				int[] defendingMove = findWinningMove(data, playingColor);
				data[defendingMove[0]][defendingMove[1]] = -playingColor;
				
				// look for another win
				int[] otherAttack = findWinningMove(data, playingColor);
				
				// if another win is found, we propagate the winFound boolean to parent calls
				if (otherAttack != null) {
					data[attackMove[0]][attackMove[1]] = GomokuModel.UNPLAYED;
					data[defendingMove[0]][defendingMove[1]] = GomokuModel.UNPLAYED;
					return attackMove;
				} else {
					int[] nextAttemp = find4StrikeWinningMove(data, playingColor);
					if (nextAttemp != null) {
						data[attackMove[0]][attackMove[1]] = GomokuModel.UNPLAYED;
						data[defendingMove[0]][defendingMove[1]] = GomokuModel.UNPLAYED;
						return attackMove;
					}
				}
				
				data[attackMove[0]][attackMove[1]] = GomokuModel.UNPLAYED;
				data[defendingMove[0]][defendingMove[1]] = GomokuModel.UNPLAYED;
			}
			
		}
		
		
		return null;
	}
	
	private int[] find3StrikeWinningMove(int[][] data, int playingColor) {

		// opponent attack
		int[] opponentWinningMove = find4StrikeWinningMove(data, -playingColor);
		if (opponentWinningMove != null) {
			
			// defend
			data[opponentWinningMove[0]][opponentWinningMove[1]] = playingColor;
			
			// ... and attack again
			int[] winningMove = find4StrikeWinningMove(data, playingColor);
			if (winningMove != null) {
				
				// opponent defense
				data[winningMove[0]][winningMove[1]] = -playingColor;
				
				int[] nextAttemp = find3StrikeWinningMove(data, playingColor);
				
				if (nextAttemp != null) {
					data[opponentWinningMove[0]][opponentWinningMove[1]] = GomokuModel.UNPLAYED;
					data[winningMove[0]][winningMove[1]] = GomokuModel.UNPLAYED;
					return nextAttemp;
				}
			}
			
			data[opponentWinningMove[0]][opponentWinningMove[1]] = GomokuModel.UNPLAYED;
			
		} else {
			List<int[]> attackMoves = findAttackMoves(data, playingColor, 3);
			
			for (int[] attackMove : attackMoves) {
				// attack
				data[attackMove[0]][attackMove[1]] = playingColor;
				
				// defend
				List<int[]> defendingMoves = findAttackMoves(data, playingColor, 4);
				data[defendingMoves.get(0)[0]][defendingMoves.get(0)[1]] = -playingColor;
				
				// look for another win
				List<int[]> otherAttacks = findAttackMoves(data, playingColor, 4);
				
				// if another win is found, we propagate the winFound boolean to parent calls
				if (!otherAttacks.isEmpty()) {
					data[attackMove[0]][attackMove[1]] = GomokuModel.UNPLAYED;
					data[defendingMoves.get(0)[0]][defendingMoves.get(0)[1]] = GomokuModel.UNPLAYED;
					return attackMove;
				} else {
					int[] nextAttemp = find3StrikeWinningMove(data, playingColor);
					if (nextAttemp != null) {
						data[attackMove[0]][attackMove[1]] = GomokuModel.UNPLAYED;
						data[defendingMoves.get(0)[0]][defendingMoves.get(0)[1]] = GomokuModel.UNPLAYED;
						return attackMove;
					}
				}
				
				data[attackMove[0]][attackMove[1]] = GomokuModel.UNPLAYED;
				data[defendingMoves.get(0)[0]][defendingMoves.get(0)[1]] = GomokuModel.UNPLAYED;
			}
			
		}
		
		
		return null;
	}


	private List<int[]> findDefendingOponentStrikeMoves(int[][] data, int playingColor) {
		
		List<int[]> defendingOponentStrikeMoves = new ArrayList<int[]>();
		
		int[] opponentStrikeMove = find4StrikeWinningMove(data, -playingColor);
		
		if (opponentStrikeMove != null) {
			for (int i = 0; i < data[0].length; i++) {
				for (int j = 0; j < data.length; j++) {
					
					if (data[j][i] == GomokuModel.UNPLAYED) {
						
						data[j][i] = playingColor;
						if (find4StrikeWinningMove(data, -playingColor) == null) {
							int[] counterMove = new int[2];
							counterMove[0] = j;
							counterMove[1] = i;
							defendingOponentStrikeMoves.add(counterMove);
						}
						
						data[j][i] = GomokuModel.UNPLAYED;
					}
				}
			}
		}
					
		return defendingOponentStrikeMoves;
	}
	
	private List<int[]> findDefendingMoves(int[][] data, int playingColor, int number) {
		
		List<int[]> defendingMoves = new ArrayList<int[]>();
		
		List<int[]> opponentAttackMoves = findAttackMoves(data, -playingColor, number);
		
		if (!opponentAttackMoves.isEmpty()) {
			for (int i = 0; i < data[0].length; i++) {
				for (int j = 0; j < data.length; j++) {
					
					if (data[j][i] == GomokuModel.UNPLAYED) {
						
						data[j][i] = playingColor;
						if (findAttackMoves(data, -playingColor, number).isEmpty()) {
							int[] defendingMove = new int[2];
							defendingMove[0] = j;
							defendingMove[1] = i;
							defendingMoves.add(defendingMove);
						}
						
						data[j][i] = GomokuModel.UNPLAYED;
					}
				}
			}
		}
					
		return defendingMoves;
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
			
			double maxOpponentEvaluation = Double.NEGATIVE_INFINITY;
			
			data[analysedMove[0]][analysedMove[1]] = playingColor;
			
			for (int i = 0; i < data[0].length; i++) {
				for (int j = 0; j < data.length; j++) {
					if (data[j][i] == GomokuModel.UNPLAYED) {
						data[j][i] = -playingColor;
						
						double opponentEvaluation = OPPONENT_EVALUATION_FACTOR * computeEvaluation(data, -playingColor) - computeEvaluation(data, playingColor);
						if (opponentEvaluation > maxOpponentEvaluation) {
							maxOpponentEvaluation = opponentEvaluation;
							opponentBestMove[0] = j;
							opponentBestMove[1] = i;
						}
						
						data[j][i] = GomokuModel.UNPLAYED;
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
