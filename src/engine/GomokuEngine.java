package engine;

import java.util.ArrayList;
import java.util.List;

import model.GomokuModel;

public class GomokuEngine {

	private int[][] data;
	private int color;
	
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int DIAGONAL1 = 2;
	public static final int DIAGONAL2 = 3;
	
	public GomokuEngine(int[][] data) {
		this.data = data;
	}

	public double computeOldEvaluation(int color) {
		this.color = color;
		
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
					eval += computeStripes(stripes);
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
					eval += computeStripes(stripes);
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
					
					for (int k = 0; startX + k < col + 1 && startX + k + 4 < data.length && startY + k < row + 1 && startY + k + 4 < data[0].length; k++) {
						int[][] stripe = new int[5][2];
						for (int f = 0; f < 5; f++) {
							stripe[f][0] = startX + k + f;
							stripe[f][1] = startY + k + f;
						}
						stripes.add(stripe);
					}
					eval += computeStripes(stripes);
					stripes.clear();

					// southwest stripes
					startX = col;
					startY = row;
					h = 0;
					while (startX < data.length -1 && startY > 0 && h < 4) {
						h++;
						startX++;
						startY--;
					}
					
					for (int k = 0; startX - k > col - 1 && startX - k - 4 > -1 && startY + k < row + 1 && startY + k + 4 < data[0].length; k++) {
						int[][] stripe = new int[5][2];
						for (int f = 0; f < 5; f++) {
							stripe[f][0] = startX - k - f;
							stripe[f][1] = startY + k + f;
						}
						stripes.add(stripe);
					}
					eval += computeStripes(stripes);
					stripes.clear();

				}
			}

		}
		
		return eval;
	}

	public double computeEvaluation(int color) {
		this.color = color;

		double eval = 0;
		
		int rowCount = data[0].length;
		int columnCount = data.length;

		for (int row = 0; row < rowCount; row++) {
			int[][] horizontalStripe = new int [columnCount][2];
			for (int col = 0; col < columnCount; col++) {
				horizontalStripe[col][0] = col;
				horizontalStripe[col][1] = row;
			}
			eval += computeStripePotential(horizontalStripe, HORIZONTAL);
		}
		
		for (int col = 0; col < columnCount; col++) {
			int[][] verticalStripe = new int [rowCount][2];
			for (int row = 0; row < rowCount; row++) {
				verticalStripe[row][0] = col;
				verticalStripe[row][1] = row;
			}
			eval += computeStripePotential(verticalStripe, VERTICAL);
		}
		
		for (int row = 0; row < rowCount; row++) {
			int[][] diagonal1Stripe = new int [columnCount - row][2];
			for (int col = 0; col < columnCount - row; col++) {
				diagonal1Stripe[col][0] = col;
				diagonal1Stripe[col][1] = row + col;
			}
			eval += computeStripePotential(diagonal1Stripe, DIAGONAL1);
		}
		
		for (int col = 1; col < columnCount; col++) {
			int[][] diagonal1Stripe = new int [rowCount - col][2];
			for (int row = 0; row < rowCount - col; row++) {
				diagonal1Stripe[row][0] = col + row;
				diagonal1Stripe[row][1] = row;
			}
			eval += computeStripePotential(diagonal1Stripe, DIAGONAL1);
		}
		
		for (int col = 0; col < columnCount; col++) {
			int[][] diagonal2Stripe = new int [rowCount - col][2];
			for (int row = rowCount - 1; row >= col; row--) {
				diagonal2Stripe[rowCount - 1 - row][0] = col - row + rowCount - 1;
				diagonal2Stripe[rowCount - 1 - row][1] = row;
			}
			eval += computeStripePotential(diagonal2Stripe, DIAGONAL2);
		}
		
		for (int row = rowCount - 2; row >= 0; row--) {
			int[][] diagonal2Stripe = new int [row][2];
			for (int col = 0; col < row; col++) {
				diagonal2Stripe[col][0] = col;
				diagonal2Stripe[col][1] = row - col;
			}
			eval += computeStripePotential(diagonal2Stripe, DIAGONAL2);
		}
		
		return eval;
	}

	private double computeStripePotential(int[][] stripe, int direction) {
		
		double stripePotential = 0;
		
		List<CellGroup> cellGroups = new ArrayList<CellGroup>();
		
		CellGroup currentCellGroup = null;
		int k = 0;

		while (k < stripe.length) {
			if (data[stripe[k][0]][stripe[k][1]] == color) {
				if (currentCellGroup == null) {
					currentCellGroup = new CellGroup(data, direction);
					cellGroups.add(currentCellGroup);
				}
				currentCellGroup.addCell(new Cell(stripe[k][0], stripe[k][1]));
			} else if (data[stripe[k][0]][stripe[k][1]] == -color) {
				currentCellGroup = null;
			}
			k++;
		}
		
		for (CellGroup cellGroup : cellGroups) {
			stripePotential += cellGroup.computePotential();
		}
		
		return stripePotential;
	}

	private double computeStripes(List<int[][]> stripes) {
		int eval = 0;
		
		for (int[][] stripe : stripes) {
			eval += computeStripe(stripe);
		}
		return eval;
	}

	private double computeStripe(int[][] stripe) {
		
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
			
			if (value == color) {
				eval = eval * weightStripe[k];
			} else if (value == -color) {
				eval = 1;
				return eval;
			}
		}
		return eval;
	}
	
	public int[][] checkForWin(int[][] data, int color) {
		
		int[][] result = new int [2][2];
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

	public int[] computeMove(int playingColor) {
		
		int[] engineMove = new int[2];
		int[][] dataCopy = new int[data.length][data[0].length];
		
		for (int i = 0; i < data[0].length; i++) {
			for (int j = 0; j < data.length; j++) {
				dataCopy[j][i] = data[j][i];
			}
		}
		
		GomokuEngine engineCopy = new GomokuEngine(dataCopy);
		
		double maxEvaluation = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < dataCopy[0].length; i++) {
			for (int j = 0; j < dataCopy.length; j++) {
				if (dataCopy[j][i] == GomokuModel.UNPLAYED) {
					
					dataCopy[j][i] = playingColor;
					
					// find opponent best move
 					int[] opponentBestMove = new int[2];
					double minEvaluation = Double.POSITIVE_INFINITY;
					
					for (int k = 0; k < dataCopy[0].length; k++) {
						for (int l = 0; l < dataCopy.length; l++) {
							if (dataCopy[l][k] == GomokuModel.UNPLAYED) {
								
								dataCopy[l][k] = -playingColor;
								double newEvaluation =  engineCopy.computeEvaluation(playingColor) - 2 * engineCopy.computeEvaluation(-playingColor);
								if (newEvaluation < minEvaluation) {
									minEvaluation = newEvaluation;
									opponentBestMove[0] = l;
									opponentBestMove[1] = k;
								}
								dataCopy[l][k] = GomokuModel.UNPLAYED;
							}
						}
					}
					
					dataCopy[opponentBestMove[0]][opponentBestMove[1]] = -playingColor;
					
					double newEvaluation =  engineCopy.computeEvaluation(playingColor) - 2 * engineCopy.computeEvaluation(-playingColor);
					if (newEvaluation > maxEvaluation) {
						maxEvaluation = newEvaluation;
						engineMove[0] = j;
						engineMove[1] = i;
					}
					
					dataCopy[opponentBestMove[0]][opponentBestMove[1]] = GomokuModel.UNPLAYED;
					dataCopy[j][i] = GomokuModel.UNPLAYED;
				}

			}
		}

		return engineMove;
	}
	
}
