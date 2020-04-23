package engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import model.GomokuData;
import model.GomokuModel;

public class GomokuEngine {

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int DIAGONAL1 = 2;
	public static final int DIAGONAL2 = 3;

	public static final double OPPONENT_EVALUATION_FACTOR = 1;
	
	private GomokuModel model;
	
	private HashMap<int[], int[]> secondaryStrikeMovesMap = new HashMap<int[], int[]>();
	
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

	public double computeEvaluation(int[][] data, int playingColor) {

		double eval = 0;

		GomokuData gomokuData = new GomokuData(data);
		gomokuData.computeCellGroups(playingColor);
		
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			eval += cellGroup.computePotential();
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
	
	private int[] findObviousMove(int[][] data, int playingColor) {

		long start;
		long end;
		
		// check for a strike
		start = System.currentTimeMillis();
		int[] strikeMove = findStrike(data, playingColor, new ArrayList<int[]>());
		end = System.currentTimeMillis();
		if (strikeMove != null) {
			System.out.println("strike found in " + (end - start) + "ms");
			return strikeMove;
		}
		
		// defend from an opponent strike
		int[] opponentStrikeMove = findStrike(data, -playingColor, new ArrayList<int[]>());
		if (opponentStrikeMove != null) {
			System.out.println("opponent strike found");
			return defendFromStrike(data, playingColor);
		}
		
		start = System.currentTimeMillis();
		int[] secondaryStrikeMove = findSecondaryStrike(data, playingColor, new ArrayList<int[]>());
		end = System.currentTimeMillis();
		if (secondaryStrikeMove != null) {
			System.out.println("secondary strike found in " + (end - start) + "ms");
			return secondaryStrikeMove;
		}
		
		return null;
	}
	
	public List<int[]> getAllThreat5MoveList(int[][] data, int playingColor) {
		
		List<int[]> result = new ArrayList<int[]>();
		
		GomokuData gomokuData = new GomokuData(data);
	
		gomokuData.computeCellGroups(playingColor);
		
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			result.addAll(cellGroup.getSingleThreatMoves(4));
		}
	
		return result;
	}

	public List<int[]> getAllThreat4MoveList(int[][] data, int playingColor) {
		
		List<int[]> result = new ArrayList<int[]>();
		
		GomokuData gomokuData = new GomokuData(data);

		gomokuData.computeCellGroups(playingColor);
		
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			result.addAll(cellGroup.getSingleThreatMoves(3));
		}

		return result;
	}
	
	public List<int[]> getAllDoubleThreat4MoveList(int[][] data, int playingColor) {
		
		List<int[]> result = new ArrayList<int[]>();
		
		GomokuData gomokuData = new GomokuData(data);
		
		gomokuData.computeCellGroups(playingColor);
		
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			result.addAll(cellGroup.getDoubleThreatMoves(3));
		}
		
		return result;
	}
	
	public List<int[]> getAllDoubleThreat3MoveList(int[][] data, int playingColor) {
		
		List<int[]> result = new ArrayList<int[]>();
		
		GomokuData gomokuData = new GomokuData(data);
		
		gomokuData.computeCellGroups(playingColor);
		
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			result.addAll(cellGroup.getDoubleThreatMoves(2));
		}
		
		return result;
	}
	
	private int[] findStrike(int[][] data, int playingColor, ArrayList<int[]> alreadyChecked) {
		
		// check for a threat5
		List<int[]> threat5Moves = getAllThreat5MoveList(data, playingColor);
		if (!threat5Moves.isEmpty()) {
			return threat5Moves.get(0);
		}
		
		// check for an opponent threat5
		List<int[]> opponentWinThreats = getAllThreat5MoveList(data, -playingColor);
		if (!opponentWinThreats.isEmpty()) {
			
			// defend
			int[] opponentThreat5Moves = opponentWinThreats.get(0);
			data[opponentThreat5Moves[0]][opponentThreat5Moves[1]] = playingColor;
			
			// check for another threat5
			List<int[]> newThreat5Moves = getAllThreat5MoveList(data, playingColor);
			
			if (!newThreat5Moves.isEmpty()) {
				
				// opponent defends
				int[] newThreat5Move = newThreat5Moves.get(0);
				data[newThreat5Move[0]][newThreat5Move[1]] = -playingColor;
				
				// check for another strike
				int[] nextAttempt = findStrike(data, playingColor, alreadyChecked);
				
				data[newThreat5Move[0]][newThreat5Move[1]] = GomokuModel.UNPLAYED;
				
				if (nextAttempt != null) {
					data[opponentThreat5Moves[0]][opponentThreat5Moves[1]] = GomokuModel.UNPLAYED;
					return opponentThreat5Moves;
				}
			}
			
			data[opponentThreat5Moves[0]][opponentThreat5Moves[1]] = GomokuModel.UNPLAYED;

			return null;
		}
		
		// check for a double threat4 move
		List<int[]> doubleThreat4Moves = getAllDoubleThreat4MoveList(data, playingColor);
		
		if (!doubleThreat4Moves.isEmpty()) {
			return doubleThreat4Moves.get(0);
		}

		// check for threat4 moves
		List<int[]> threat4Moves = getAllThreat4MoveList(data, playingColor);

		
		ArrayList<int[]> movesToSkip = new ArrayList<int[]>();
		
		for (int[] checkedMove : alreadyChecked) {
			for (int[] threatMove : threat4Moves) {
				if (checkedMove[0] == threatMove[0] && checkedMove[1] == threatMove[1]) {
					movesToSkip.add(threatMove);
				}
			}
		}
		
		threat4Moves.removeAll(movesToSkip);
		
		for (int[] threat4Move : threat4Moves) {
				
			data[threat4Move[0]][threat4Move[1]] = playingColor;
			
			// opponent defends
			List<int[]> counterMoves = getAllThreat5MoveList(data, playingColor);
			int[] counterMove = counterMoves.get(0);
			data[counterMove[0]][counterMove[1]] = -playingColor;
			
			int[] nextAttempt = findStrike(data, playingColor, alreadyChecked);
			
			data[counterMove[0]][counterMove[1]] = GomokuModel.UNPLAYED;
			data[threat4Move[0]][threat4Move[1]] = GomokuModel.UNPLAYED;

			if (nextAttempt != null) {
				return threat4Move;
			}
			
			alreadyChecked.add(threat4Move);
		}
		
		return null;
	}

	private int[] findSecondaryStrike(int[][] data, int playingColor, ArrayList<int[]> alreadyChecked) {
		
		// check for a strike
		int[] strike = findStrike(data, playingColor, new ArrayList<int[]>());
		
		if (strike != null) {
			return strike;
		}
		
		// check for an opponent strike
		int[] opponentStrike = findStrike(data, -playingColor, new ArrayList<int[]>());
		
		if (opponentStrike != null) {
			int[] defendFromStrike = defendFromStrike(data, playingColor);
			
			// defend
			if (defendFromStrike != null) {
				data[defendFromStrike[0]][defendFromStrike[1]] = playingColor;
				
				// check for a new strike
				int[] newStrike = findStrike(data, playingColor, new ArrayList<int[]>());

				if (newStrike != null) {
					int[] opponentDefendFromStrike = defendFromStrike(data, -playingColor);
					
					if (opponentDefendFromStrike == null) {
						data[defendFromStrike[0]][defendFromStrike[1]] = GomokuModel.UNPLAYED;
						System.out.println("[]" + "[] => [" +  + newStrike[0] + "]" + "[" + newStrike[1] + "]");
						return defendFromStrike;
					}
					
					data[opponentDefendFromStrike[0]][opponentDefendFromStrike[1]] = -playingColor;
					
					int[] newAttempt = findSecondaryStrike(data, playingColor, alreadyChecked);
					
					data[opponentDefendFromStrike[0]][opponentDefendFromStrike[1]] = GomokuModel.UNPLAYED;
					
					if (newAttempt != null) {
						data[defendFromStrike[0]][defendFromStrike[1]] = GomokuModel.UNPLAYED;
						System.out.println("[" + opponentDefendFromStrike[0] + "]" + "[" + opponentDefendFromStrike[1] + "] => [" +  + newAttempt[0] + "]" + "[" + newAttempt[1] + "]");
						return defendFromStrike;
					}
				}
				
				data[defendFromStrike[0]][defendFromStrike[1]] = GomokuModel.UNPLAYED;
			}
			
			return null;
		}
		
		// find all single threat4 moves
		List<int[]> singleThreat4Moves = getAllThreat4MoveList(data, playingColor);

		// find all double threat3 moves
		List<int[]> doubleThreat3Moves = getAllDoubleThreat3MoveList(data, playingColor);
		
		List<int[]> threatMoves = new ArrayList<int[]>();
		
		threatMoves.addAll(singleThreat4Moves);
		threatMoves.addAll(doubleThreat3Moves);
		
		ArrayList<int[]> movesToSkip = new ArrayList<int[]>();
		
		for (int[] checkedMove : alreadyChecked) {
			for (int[] threatMove : threatMoves) {
				if (checkedMove[0] == threatMove[0] && checkedMove[1] == threatMove[1]) {
					movesToSkip.add(threatMove);
				}
			}
		}
		
		threatMoves.removeAll(movesToSkip);
		
		while (!threatMoves.isEmpty()) {
			
			int[] threatMove = findBestMoveAmongTheseMoves(data, playingColor, threatMoves);
		
			data[threatMove[0]][threatMove[1]] = playingColor;
			
			List<CellGroup> threat4Groups = getThreat4GroupsConnectedTo(data, playingColor, threatMove);

			int[] counterMove = null;
			
			if (!threat4Groups.isEmpty()) {
				counterMove = threat4Groups.get(0).getThreat5Moves().get(0);
			} else {
				List<CellGroup> threat3Groups = getThreat3GroupsConnectedTo(data, playingColor, threatMove);
				
				List<int[]> counterMoves = new ArrayList<int[]>();
				
				for (CellGroup cellGroup : threat3Groups) {
					counterMoves.addAll(cellGroup.getThreat4Moves());
				}
				
				// opponent defends
				counterMove = findBestMoveAmongTheseMoves(data, -playingColor, counterMoves);
			}
			
			
			data[counterMove[0]][counterMove[1]] = -playingColor;
			
			int[] nextAttempt = findSecondaryStrike(data, playingColor, alreadyChecked);
			
			data[counterMove[0]][counterMove[1]] = GomokuModel.UNPLAYED;
			data[threatMove[0]][threatMove[1]] = GomokuModel.UNPLAYED;
			
			if (nextAttempt != null) {
				System.out.println("[" + counterMove[0] + "]" + "[" + counterMove[1] + "] => [" +  + nextAttempt[0] + "]" + "[" + nextAttempt[1] + "]");
				return threatMove;
			}
			
			threatMoves.remove(threatMove);
			alreadyChecked.add(threatMove);
		}
		return null;
	}

	private List<CellGroup> getThreat4GroupsConnectedTo(int[][] data, int playingColor, int[] move) {
		
		List<CellGroup> result = new ArrayList<CellGroup>();
		GomokuData gomokuData = new GomokuData(data);
		
		gomokuData.computeCellGroups(playingColor);
		
		Cell moveCell = gomokuData.get(move[0], move[1]);
		
		for (CellGroup group : moveCell.getCellGroups()) {
			if (group.isGroupSingleThreat(4)) {
				result.add(group);
			}
		}
		
		return result;
	}
	
	private List<CellGroup> getThreat3GroupsConnectedTo(int[][] data, int playingColor, int[] move) {
		
		List<CellGroup> result = new ArrayList<CellGroup>();
		GomokuData gomokuData = new GomokuData(data);
		
		gomokuData.computeCellGroups(playingColor);
		
		Cell moveCell = gomokuData.get(move[0], move[1]);
		
		for (CellGroup group : moveCell.getCellGroups()) {
			if (group.isGroupDoubleThreat(3)) {
				result.add(group);
			}
		}
		
		return result;
	}

	private int[] defendFromStrike(int[][] data, int playingColor) {
		
		List<int[]> opponentWinThreats = getAllThreat5MoveList(data, -playingColor);

		if (!opponentWinThreats.isEmpty()) {
			return opponentWinThreats.get(0);
		}
		
		return findBestMoveAmongTheseMoves(data, playingColor, findDefendingFromStrikeMoves(data, playingColor));
	}

	private List<int[]> findDefendingFromStrikeMoves(int[][] data, int playingColor) {
		
		List<int[]> defendingMoves = new ArrayList<int[]>();
		
		for (int i = 0; i < data[0].length; i++) {
			for (int j = 0; j < data.length; j++) {
				if (data[j][i] == GomokuModel.UNPLAYED) {
					
					data[j][i] = playingColor;
					
					if (findStrike(data, -playingColor, new ArrayList<int[]>()) == null) {
						int[] defendingMove = new int[2];
						defendingMove[0] = j;
						defendingMove[1] = i;
						defendingMoves.add(defendingMove);
					}
					
					data[j][i] = GomokuModel.UNPLAYED;
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
		
		int[] bestMove = null;
		
		int[] bestOpponentMove = null;
		
		for (int[] analysedMove : analysedMoves) {
			
			int[] opponentBestMove = new int[2];
			
			double minEvaluation = Double.POSITIVE_INFINITY;
			
			data[analysedMove[0]][analysedMove[1]] = playingColor;
			
			List<int[]> obvious5Threats = getAllThreat5MoveList(data, playingColor);
			if (!obvious5Threats.isEmpty()) {
				opponentBestMove = obvious5Threats.get(0);
			} else {
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
					if (minEvaluation < maxEvaluation) {
						break;
					}
				}
			}
			
			
			data[opponentBestMove[0]][opponentBestMove[1]] = -playingColor;

			double evaluation = computeEvaluation(data, playingColor) - OPPONENT_EVALUATION_FACTOR * computeEvaluation(data, -playingColor);
			if (evaluation > maxEvaluation) {
				maxEvaluation = evaluation;
				bestMove = analysedMove;
				bestOpponentMove = opponentBestMove;
			}
			
			data[opponentBestMove[0]][opponentBestMove[1]] = GomokuModel.UNPLAYED;
			data[analysedMove[0]][analysedMove[1]] = GomokuModel.UNPLAYED;
		}
		
		return bestMove;
	}

}
