package engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.GomokuData;
import model.GomokuModel;

public class GomokuEngine {

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int DIAGONAL1 = 2;
	public static final int DIAGONAL2 = 3;

	public static final double OPPONENT_EVALUATION_FACTOR = 0.99;

	private GomokuModel model;

	private List<List<int[]>> secondaryStrikeMovesList = new ArrayList<List<int[]>>();
	
	private List<int[][]> alreadyCheckedDatas = new ArrayList<int[][]>();
	
	private int maxDepthAllowed = 8;
	
	private static final int WIN_EVALUATION = 10000;
	private static final int DOUBLE_THREAT_5_EVALUATION = 1000;
	private static final int DOUBLE_THREAT_4_EVALUATION = 100;
	private static final int DOUBLE_THREAT_3_EVALUATION = 10;
	private static final int DOUBLE_THREAT_2_EVALUATION = 1;
	
	private static final int STRIKE_POTENTIAL = 100;
	private static final int SECONDARY_STRIKE_POTENTIAL = 10;
	private static final int CONNECTION_POTENTIAL = 1;
	
	private Comparator<? super int[]> moveComparator = new Comparator<int[]>() {

		@Override
		public int compare(int[] move1, int[] move2) {

			int sizeX = model.getColumnCount();
			int sizeY = model.getRowCount();

			int middleX = (sizeX - 1) / 2;
			int middleY = (sizeY - 1) / 2;

			int distanceToMiddle1 = (move1[0] - middleX) * (move1[0] - middleX)
					+ (move1[1] - middleY) * (move1[1] - middleY);
			int distanceToMiddle2 = (move2[0] - middleX) * (move2[0] - middleX)
					+ (move2[1] - middleY) * (move2[1] - middleY);

			return distanceToMiddle1 - distanceToMiddle2 > 0 ? 1 : distanceToMiddle1 == distanceToMiddle2 ? 0 : -1;
		}

	};

	public GomokuEngine(GomokuModel model) {
		this.model = model;
	}

	public int[] computeMove(int[][] data, int playingColor) throws Exception {
	
			String color = playingColor == GomokuModel.BLACK ? "BLACK " : "WHITE ";
	
			int[][] dataCopy = new int[data.length][data[0].length];
			
			for (int rowIndex = 0; rowIndex < dataCopy[0].length; rowIndex++) {
				for (int columnIndex = 0; columnIndex < dataCopy.length; columnIndex++) {
					dataCopy[columnIndex][rowIndex] = data[columnIndex][rowIndex];
				}
			}
			
			int[] obviousMove = findObviousMove(dataCopy, playingColor);
	
			if (obviousMove != null) {
				return obviousMove;
			}
	
			List<int[]> analysedMoves = new ArrayList<int[]>();
	
			for (int i = 0; i < dataCopy[0].length; i++) {
				for (int j = 0; j < dataCopy.length; j++) {
					if (dataCopy[j][i] == GomokuModel.UNPLAYED) {
						int[] analyzedMove = new int[2];
						analyzedMove[0] = j;
						analyzedMove[1] = i;
						analysedMoves.add(analyzedMove);
					}
				}
			}
	
			analysedMoves.sort(moveComparator);
	
			System.out.println(color + "searching for MinMax...");
			long start = System.currentTimeMillis();
			int[] minMax = findBestMoveAmongTheseMoves(dataCopy, playingColor, analysedMoves, true);
			long end = System.currentTimeMillis();
			
			System.out.println(color + "minMax found in " + (end - start) + " ms");
	
			return minMax;
		}

	public double computeEvaluation(int[][] data, int playingColor) throws Exception {

		double eval = 0;

		GomokuData gomokuData = new GomokuData(data, playingColor);

//		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
//			eval += cellGroup.computePotential();
//		}

//		eval += computeRealConnections(gomokuData, playingColor);
//		eval += computePotentialConnections(gomokuData, playingColor);

		int[] result = computeMaxThreatPotential(gomokuData, playingColor, false);
		
		return result[2];
	}

	public void computeThreatEvaluation(int[][] data, int playingColor) throws Exception {
		
		GomokuData gomokuData = new GomokuData(data, playingColor);
		
		computeMaxThreatPotential(gomokuData, playingColor, true);
	}

	private int[] computeMaxThreatPotential(GomokuData gomokuData, int playingColor, boolean display) throws Exception {
		
		int maxPotential = 0;

		int[] bestThreat = new int[2];
				
		int[][] threatTable = new int[gomokuData.getData().length][gomokuData.getData()[0].length];
		
		for (int rowIndex = 0; rowIndex < threatTable[0].length; rowIndex++) {
			for (int columnIndex = 0; columnIndex < threatTable.length; columnIndex++) {
				threatTable[columnIndex][rowIndex] = 0;
			}
		}

		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			
			List<int[]> doubleThreat5List = cellGroup.getDoubleThreatMoves(4);
			for (int[] doubleThreat5 : doubleThreat5List) {
				Cell threatCell = gomokuData.get(doubleThreat5[0], doubleThreat5[1]);
				threatCell.setDoubleThreat5Potential(threatCell.getDoubleThreat5Potential() + 1);
			}
			
			List<int[]> singleThreat5List = cellGroup.getSingleThreatMoves(4);
			for (int[] singleThreat5 : singleThreat5List) {
				Cell threatCell = gomokuData.get(singleThreat5[0], singleThreat5[1]);
				threatCell.setSingleThreat5Potential(threatCell.getSingleThreat5Potential() + 1);
			}
			
			List<int[]> doubleThreat4List = cellGroup.getDoubleThreatMoves(3);
			for (int[] doubleThreat4 : doubleThreat4List) {
				Cell threatCell = gomokuData.get(doubleThreat4[0], doubleThreat4[1]);
				threatCell.setDoubleThreat4Potential(threatCell.getDoubleThreat4Potential() + 1);
			}
			
			List<int[]> singleThreat4List = cellGroup.getSingleThreatMoves(3);
			for (int[] singleThreat4 : singleThreat4List) {
				Cell threatCell = gomokuData.get(singleThreat4[0], singleThreat4[1]);
				threatCell.setSingleThreat4Potential(threatCell.getSingleThreat4Potential() + 1);
				threatCell.addSingleThreat4Group(cellGroup);
			}
			
			List<int[]> doubleThreat3List = cellGroup.getDoubleThreatMoves(2);
			for (int[] doubleThreat3 : doubleThreat3List) {
				Cell threatCell = gomokuData.get(doubleThreat3[0], doubleThreat3[1]);
				threatCell.setDoubleThreat3Potential(threatCell.getDoubleThreat3Potential() + 1);
			}
			
			List<int[]> singleThreat3List = cellGroup.getSingleThreatMoves(2);
			for (int[] singleThreat3 : singleThreat3List) {
				Cell threatCell = gomokuData.get(singleThreat3[0], singleThreat3[1]);
				threatCell.setSingleThreat3Potential(threatCell.getSingleThreat3Potential() + 1);
			}
			
			List<int[]> doubleThreat2List = cellGroup.getDoubleThreatMoves(1);
			for (int[] doubleThreat2 : doubleThreat2List) {
				Cell threatCell = gomokuData.get(doubleThreat2[0], doubleThreat2[1]);
				threatCell.setDoubleThreat2Potential(threatCell.getDoubleThreat2Potential() + 1);
			}
			
			List<int[]> singleThreat2List = cellGroup.getSingleThreatMoves(1);
			for (int[] singleThreat2 : singleThreat2List) {
				Cell threatCell = gomokuData.get(singleThreat2[0], singleThreat2[1]);
				threatCell.setSingleThreat2Potential(threatCell.getSingleThreat2Potential() + 1);
			}
		}
		
		for (int rowIndex = 0; rowIndex < threatTable[0].length; rowIndex++) {
			for (int columnIndex = 0; columnIndex < threatTable.length; columnIndex++) {
				
				Cell threatCell = gomokuData.get(columnIndex, rowIndex);
				
				// SINGLE THREATS
				
				if (threatCell.getSingleThreat5Potential() > 0) {
					threatTable[columnIndex][rowIndex] += 10000 * threatCell.getSingleThreat5Potential();
				}
				
				if (threatCell.getSingleThreat5Potential() > 0) {
					threatTable[columnIndex][rowIndex] += 10000 * threatCell.getSingleThreat5Potential();
				}
				
				if (threatCell.getSingleThreat4Potential() > 0) {
					threatTable[columnIndex][rowIndex] += 4 * threatCell.getSingleThreat4Potential();
					
					if (threatCell.getSingleThreat4Potential() > 1 && threatCell.areSingle4ThreatsCompatible()) {
						threatTable[columnIndex][rowIndex] += 10000 * threatCell.getSingleThreat4Potential();
					}
					
					if (threatCell.getDoubleThreat3Potential() > 0) {
						threatTable[columnIndex][rowIndex] += 1000 * threatCell.getDoubleThreat3Potential();
					}
					
					if (threatCell.getDoubleThreat2Potential() > 0) {
						threatTable[columnIndex][rowIndex] += 100 * threatCell.getDoubleThreat2Potential();
					}
				}
				
				if (threatCell.getSingleThreat3Potential() > 0) {
					threatTable[columnIndex][rowIndex] += 3 * threatCell.getSingleThreat3Potential();
				}
				
				if (threatCell.getSingleThreat2Potential() > 0) {
					threatTable[columnIndex][rowIndex] += 2 * threatCell.getSingleThreat2Potential();
				}
				
				// DOUBLE THREATS
				
				if (threatCell.getDoubleThreat4Potential() > 0) {
					threatTable[columnIndex][rowIndex] += 1000 * threatCell.getDoubleThreat4Potential();
				}
				
				if (threatCell.getDoubleThreat3Potential() > 0) {
					
					threatTable[columnIndex][rowIndex] += 100 * threatCell.getDoubleThreat3Potential();
					
					if (threatCell.getDoubleThreat2Potential() > 0) {
						threatTable[columnIndex][rowIndex] += 1 * threatCell.getDoubleThreat2Potential();
					}
				}
				
				if (threatCell.getSingleThreat3Potential() > 0) {
					threatTable[columnIndex][rowIndex] += 1 * threatCell.getSingleThreat3Potential();
				}
				
				if (threatCell.getDoubleThreat2Potential() > 0) {
					threatTable[columnIndex][rowIndex] += 1 * threatCell.getDoubleThreat2Potential();
				}
				
				if (threatCell.getSingleThreat2Potential() > 0) {
					threatTable[columnIndex][rowIndex] += 1 * threatCell.getSingleThreat2Potential();
				}
			}
		}
		
		for (int rowIndex = 0; rowIndex < threatTable[0].length; rowIndex++) {
			for (int columnIndex = 0; columnIndex < threatTable.length; columnIndex++) {
				
				int finalPotential = threatTable[columnIndex][rowIndex];
				
				if (finalPotential > maxPotential) {
					maxPotential = finalPotential;
					bestThreat[0] = columnIndex;
					bestThreat[1] = rowIndex;
				}
			}
		}
		
		int[] result = new int[3];
		
		result[0] = bestThreat[0];
		result[1] = bestThreat[1];
		result[2] = maxPotential;
		
		
		return result;
	}

	private double computeRealConnections(GomokuData gomokuData, int playingColor) throws Exception {

		double value = 0;

		List<CellGroup> single5Groups = getAll5Groups(gomokuData, playingColor);
		
		List<CellGroup> doubleThreat5Groups = getAllDoubleThreat5Groups(gomokuData, playingColor);
		List<CellGroup> doubleThreat4Groups = getAllDoubleThreat4Groups(gomokuData, playingColor);
		List<CellGroup> doubleThreat3Groups = getAllDoubleThreat3Groups(gomokuData, playingColor);
		List<CellGroup> doubleThreat2Groups = getAllDoubleThreat2Groups(gomokuData, playingColor);
		
		List<CellGroup> singleThreat5Groups = getAllSingleThreat5Groups(gomokuData, playingColor);
		List<CellGroup> singleThreat4Groups = getAllSingleThreat4Groups(gomokuData, playingColor);
		List<CellGroup> singleThreat3Groups = getAllSingleThreat3Groups(gomokuData, playingColor);
		List<CellGroup> singleThreat2Groups = getAllSingleThreat2Groups(gomokuData, playingColor);
		
		// 5
		value += WIN_EVALUATION * single5Groups.size();
		
		// DOUBLE THREAT-5 
		value += DOUBLE_THREAT_5_EVALUATION * doubleThreat5Groups.size();
		
		// DOUBLE THREAT-4 
		value += DOUBLE_THREAT_4_EVALUATION * doubleThreat4Groups.size();
		
		// DOUBLE THREAT-3
		value += DOUBLE_THREAT_3_EVALUATION * doubleThreat3Groups.size();
		
		// DOUBLE THREAT-2
		value += DOUBLE_THREAT_2_EVALUATION * doubleThreat2Groups.size();
		
		// SINGLE THREAT-5 
		value += 4 * singleThreat5Groups.size();
		
		// SINGLE THREAT-4 
		value += 3 * singleThreat4Groups.size();
		
		// SINGLE THREAT-3
		value += 2 * singleThreat3Groups.size();
		
		return value;
	}

	private double computePotentialConnections(GomokuData gomokuData, int playingColor) throws Exception {
	
		double value = 0;
	
		List<CellGroup> single5Groups = getAll5Groups(gomokuData, playingColor);
		
		List<CellGroup> doubleThreat5Groups = getAllDoubleThreat5Groups(gomokuData, playingColor);
		List<CellGroup> doubleThreat4Groups = getAllDoubleThreat4Groups(gomokuData, playingColor);
		List<CellGroup> doubleThreat3Groups = getAllDoubleThreat3Groups(gomokuData, playingColor);
		List<CellGroup> doubleThreat2Groups = getAllDoubleThreat2Groups(gomokuData, playingColor);
		
		List<CellGroup> singleThreat5Groups = getAllSingleThreat5Groups(gomokuData, playingColor);
		List<CellGroup> singleThreat4Groups = getAllSingleThreat4Groups(gomokuData, playingColor);
		List<CellGroup> singleThreat3Groups = getAllSingleThreat3Groups(gomokuData, playingColor);
		List<CellGroup> singleThreat2Groups = getAllSingleThreat2Groups(gomokuData, playingColor);
		
		List<CellGroup> oneCellGroups = getAllOneCellGroups(gomokuData, playingColor);
		List<CellGroup> twoCellGroups = getAllTwoCellGroups(gomokuData, playingColor);
		
		// DOUBLE THREAT-4 <=> DOUBLE THREAT-4
		value += DOUBLE_THREAT_4_EVALUATION * checkDoublePotentialCrossings(doubleThreat4Groups);
		
		// SINGLE THREAT-4 <=> DOUBLE THREAT-4
		value += DOUBLE_THREAT_4_EVALUATION * checkSingleToDoublePotentialCrossings(singleThreat4Groups, doubleThreat4Groups);
		
		// SINGLE THREAT-4 <=> DOUBLE THREAT-3
		value += DOUBLE_THREAT_3_EVALUATION * checkSingleToDoublePotentialCrossings(singleThreat4Groups, doubleThreat3Groups);
		
		// DOUBLE THREAT-3 <=> DOUBLE THREAT-3
		value += DOUBLE_THREAT_3_EVALUATION * checkDoublePotentialCrossings(doubleThreat3Groups);
		
		// SINGLE THREAT-4 <=> DOUBLE THREAT-2
		value += DOUBLE_THREAT_2_EVALUATION * checkSingleToDoublePotentialCrossings(singleThreat4Groups, doubleThreat2Groups);
		
		// DOUBLE THREAT-3 <=> DOUBLE THREAT-2
		value += DOUBLE_THREAT_2_EVALUATION * checkDoublePotentialCrossings(doubleThreat3Groups, doubleThreat2Groups);
		
		// DOUBLE THREAT-2 <=> DOUBLE THREAT-2
		value += DOUBLE_THREAT_2_EVALUATION * checkDoublePotentialCrossings(doubleThreat2Groups);
		
		return value;
	}

	private int checkCrossings(List<CellGroup> groups1, List<CellGroup> groups2) {
		
		int value = 0;
		
		for (CellGroup firstGroup : groups1) {
			for (CellGroup secondGroup : groups2) {
				int[] cross = firstGroup.isCrossing(secondGroup);
				if (cross != null) {
					value += 1;
				}
			}
		}
		
		return value;
	}

	private double checkCrossings(List<CellGroup> groups) {
		
		int value = 0;
		
		for (int i = 0; i < groups.size(); i++) {
			CellGroup firstGroup = groups.get(i);
			for (int j = i + 1; j < groups.size(); j++) {
				CellGroup secondGroup = groups.get(j);
				int[] cross = firstGroup.isCrossing(secondGroup);
				if (cross != null) {
					value += 1;
				}
			}
		}
		
		return value;
	}

	private int checkDoublePotentialCrossings(List<CellGroup> groups) {
		int value = 0;

		for (int i = 0; i < groups.size(); i++) {
			CellGroup firstGroup = groups.get(i);
			int size = firstGroup.getCellList().size();
			List<int[]> threats = firstGroup.getDoubleThreatMoves(size);
			for (int[] threat : threats) {
				for (int j = i + 1; j < groups.size(); j++) {
					CellGroup secondGroup = groups.get(j);

					if (firstGroup.isCrossing(secondGroup) == null && secondGroup.hasDoubleThreatPotentialWith(threat)) {
						secondGroup.getDoubleThreatMoves(secondGroup.getCellList().size());
						value += 1;
					}
				}
			}
		}
		
		return value;
	}
	
	private int checkDoublePotentialCrossings(List<CellGroup> groups1, List<CellGroup> groups2) {
		int value = 0;
		
		for (int i = 0; i < groups1.size(); i++) {
			CellGroup firstGroup = groups1.get(i);
			int size = firstGroup.getCellList().size();
			List<int[]> threats = firstGroup.getDoubleThreatMoves(size);
			for (int[] threat : threats) {
				for (int j = i + 1; j < groups2.size(); j++) {
					CellGroup secondGroup = groups2.get(j);
					
					if (firstGroup.isCrossing(secondGroup) == null && secondGroup.hasDoubleThreatPotentialWith(threat)) {
						secondGroup.getDoubleThreatMoves(secondGroup.getCellList().size());
						value += 1;
					}
				}
			}
		}
		
		return value;
	}

	private int checkSingleToDoublePotentialCrossings(List<CellGroup> singleGroups, List<CellGroup> doubleGroups) {
		int value = 0;

		for (int i = 0; i < singleGroups.size(); i++) {
			CellGroup firstGroup = singleGroups.get(i);
			int size = firstGroup.getCellList().size();
			List<int[]> threats = firstGroup.getSingleThreatMoves(size);
			for (int[] threat : threats) {
				for (int j = 0; j < doubleGroups.size(); j++) {
					CellGroup secondGroup = doubleGroups.get(j);

					if (firstGroup.isCrossing(secondGroup) == null && secondGroup.hasDoubleThreatPotentialWith(threat)) {
						value += 1;
					}
				}
			}
		}
		
		return value;
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

	private int[] findObviousMove(int[][] data, int playingColor) throws Exception {

		long start;
		long end;

		String color = playingColor == GomokuModel.BLACK ? "BLACK " : "WHITE ";
		
		// check for a strike
		start = System.currentTimeMillis();
		int[] strike = findStrike(data, playingColor, new ArrayList<int[]>());
		end = System.currentTimeMillis();
		if (strike != null) {
			System.out.println(color + "strike found in " + (end - start) + " ms");
			return strike;
		}

		// defend from an opponent strike
		int[] opponentStrike = findStrike(data, -playingColor, new ArrayList<int[]>());
		if (opponentStrike != null) {
			System.out.println(color + "searching for strike defense...");
			start = System.currentTimeMillis();
			int[] defendFromStrike = defendFromStrike(data, playingColor);
			end = System.currentTimeMillis();
			System.out.println(color + "defense from strike found in " + (end - start) + " ms");
			return defendFromStrike;
		}

		if (!secondaryStrikeMovesList.isEmpty()) {
			int[] lastMove = model.getModelController().getLastMove();

			if (secondaryStrikeMovesList.get(0).get(0)[0] == lastMove[0]
					&& secondaryStrikeMovesList.get(0).get(0)[1] == lastMove[1]) {
				int[] secondaryStrikeMove = secondaryStrikeMovesList.get(0).get(1);
				secondaryStrikeMovesList.remove(0);
				return secondaryStrikeMove;
			}
		}

		secondaryStrikeMovesList.clear();

		System.out.println(color + "searching for secondary strike...");
		start = System.currentTimeMillis();
		int[] secondaryStrike = findSecondaryStrike(data, playingColor, 0, true);
		end = System.currentTimeMillis();
		if (secondaryStrike != null) {
			System.out.println("secondary strike found in " + (end - start) + " ms");
			return secondaryStrike;
		}
		
		System.out.println(color + "searching for opponent secondary strike...");
		int[] opponentSecondaryStrike = findSecondaryStrike(data, -playingColor, 0, false);
		if (opponentSecondaryStrike != null) {
			System.out.println(color + "searching for secondary strike defense...");
			start = System.currentTimeMillis();
			int[] defendFromSecondaryStrike = defendFromSecondaryStrike(data, playingColor);
			end = System.currentTimeMillis();
			System.out.println(color + "defense from secondary strike found in " + (end - start) + " ms");
			return defendFromSecondaryStrike;
		}

		return null;
	}

	private int[] findStrike(int[][] data, int playingColor, ArrayList<int[]> alreadyChecked) throws Exception {

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

	private int[] findSecondaryStrike(int[][] data, int playingColor, int depth, boolean showAnalysis) throws Exception {

		if (depth == maxDepthAllowed) {
			return null;
		}
		
		if (depth == 0) {
			alreadyCheckedDatas.clear();
		}
		
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
				if (showAnalysis) {
					model.firePropertyChange(GomokuModel.ANALYSED_MOVE, defendFromStrike);
				}

				// check for a new strike
				int[] newStrike = findStrike(data, playingColor, new ArrayList<int[]>());

				if (newStrike != null) {
					int[] opponentDefendFromStrike = defendFromStrike(data, -playingColor);

					if (opponentDefendFromStrike == null) {
						data[defendFromStrike[0]][defendFromStrike[1]] = GomokuModel.UNPLAYED;
						if (showAnalysis) {
							model.firePropertyChange(GomokuModel.ANALYSED_DONE, defendFromStrike);
						}

						System.out.println("[]" + "[] => [" + +newStrike[0] + "]" + "[" + newStrike[1] + "]");
						return defendFromStrike;
					}

					data[opponentDefendFromStrike[0]][opponentDefendFromStrike[1]] = -playingColor;
					if (showAnalysis) {
						model.firePropertyChange(GomokuModel.ANALYSED_MOVE, opponentDefendFromStrike);
					}

					int[] newAttempt = findSecondaryStrike(data, playingColor, depth + 1, showAnalysis);

					data[opponentDefendFromStrike[0]][opponentDefendFromStrike[1]] = GomokuModel.UNPLAYED;
					if (showAnalysis) {
						model.firePropertyChange(GomokuModel.ANALYSED_DONE, opponentDefendFromStrike);
					}

					if (newAttempt != null) {
						data[defendFromStrike[0]][defendFromStrike[1]] = GomokuModel.UNPLAYED;
						if (showAnalysis) {
							model.firePropertyChange(GomokuModel.ANALYSED_DONE, defendFromStrike);
						}

						System.out.println("[" + opponentDefendFromStrike[0] + "]" + "[" + opponentDefendFromStrike[1]
								+ "] => [" + +newAttempt[0] + "]" + "[" + newAttempt[1] + "]");
						List<int[]> newCouple = new ArrayList<int[]>();
						newCouple.add(opponentDefendFromStrike);
						newCouple.add(newAttempt);
						secondaryStrikeMovesList.add(0, newCouple);
						return defendFromStrike;
					}
				}

				data[defendFromStrike[0]][defendFromStrike[1]] = GomokuModel.UNPLAYED;
				if (showAnalysis) {
					model.firePropertyChange(GomokuModel.ANALYSED_DONE, defendFromStrike);
				}
			}

			return null;
		}

		int[] doubleThreat3 = null;
		
		GomokuData gomokuData = new GomokuData(data, playingColor);
		
		List<CellGroup> doubleThreats3Groups = getAllDoubleThreat3Groups(gomokuData, playingColor);
		
		groupLoop : for (CellGroup firstGroup : doubleThreats3Groups) {
			for (int[] firstThreat : firstGroup.getDoubleThreatMoves(2)) {
				for (CellGroup secondGroup : doubleThreats3Groups) {
					if (firstGroup != secondGroup) {
						for (int[] secondThreat : secondGroup.getDoubleThreatMoves(2)) {
							if (secondThreat[0] == firstThreat[0] && secondThreat[1] == firstThreat[1]) {
								doubleThreat3 = firstThreat;
								break groupLoop;
							}
						}
					}
				}
			}
		}
		
		if (doubleThreat3 != null) {
			
			data[doubleThreat3[0]][doubleThreat3[1]] = playingColor;
			if (showAnalysis) {
				model.firePropertyChange(GomokuModel.ANALYSED_MOVE, doubleThreat3);
			}

			int[] counterMove = defendFromSecondaryStrike(data, -playingColor);
			
			if (counterMove == null) {
				data[doubleThreat3[0]][doubleThreat3[1]] = GomokuModel.UNPLAYED;
				if (showAnalysis) {
					model.firePropertyChange(GomokuModel.ANALYSED_DONE, doubleThreat3);
				}
				
				return doubleThreat3;
			}
			
			data[counterMove[0]][counterMove[1]] = -playingColor;
			if (showAnalysis) {
				model.firePropertyChange(GomokuModel.ANALYSED_MOVE, counterMove);
			}

			int[] nextAttempt = findSecondaryStrike(data, playingColor, depth + 1, showAnalysis);
			
			data[counterMove[0]][counterMove[1]] = GomokuModel.UNPLAYED;
			if (showAnalysis) {
				model.firePropertyChange(GomokuModel.ANALYSED_DONE, counterMove);
			}

			data[doubleThreat3[0]][doubleThreat3[1]] = GomokuModel.UNPLAYED;
			if (showAnalysis) {
				model.firePropertyChange(GomokuModel.ANALYSED_DONE, doubleThreat3);
			}

			
			if (nextAttempt != null) {
				System.out.println("[" + counterMove[0] + "]" + "[" + counterMove[1] + "] => [" + +nextAttempt[0] + "]"
						+ "[" + nextAttempt[1] + "]");
				List<int[]> newCouple = new ArrayList<int[]>();
				newCouple.add(counterMove);
				newCouple.add(nextAttempt);
				secondaryStrikeMovesList.add(0, newCouple);
				return doubleThreat3;
			}
		}
		
		// find all single threat4 moves
		List<int[]> singleThreat4Moves = getAllThreat4MoveList(data, playingColor);

		// find all double threat3 moves
		List<int[]> doubleThreat3Moves = getAllDoubleThreat3MoveList(data, playingColor);

		List<int[]> threatMoves = new ArrayList<int[]>();

		threatMoves.addAll(singleThreat4Moves);
		threatMoves.addAll(doubleThreat3Moves);

		int numberOfThreatsAnalysed = 0;
		
		while (!threatMoves.isEmpty() && numberOfThreatsAnalysed < 2) {

			if (depth == 0) {
				maxDepthAllowed = Math.max(12 - threatMoves.size(), 2);
			}
			int[] threatMove = findBestMoveAmongTheseMoves(data, playingColor, threatMoves, showAnalysis);

			numberOfThreatsAnalysed++;
			
			data[threatMove[0]][threatMove[1]] = playingColor;
			if (showAnalysis) {
				model.firePropertyChange(GomokuModel.ANALYSED_MOVE, threatMove);
			}

			int[] counterMove = defendFromSecondaryStrike(data, -playingColor);

			if (counterMove == null) {
				data[threatMove[0]][threatMove[1]] = GomokuModel.UNPLAYED;
				if (showAnalysis) {
					model.firePropertyChange(GomokuModel.ANALYSED_DONE, threatMove);
				}
				return threatMove;
			}

			data[counterMove[0]][counterMove[1]] = -playingColor;
			if (showAnalysis) {
				model.firePropertyChange(GomokuModel.ANALYSED_MOVE, counterMove);
			}

			boolean alreadyChecked = true;
			
			checkLoop : for (int[][] alreadyCheckedData : alreadyCheckedDatas) {
				for (int i = 0; i < data[0].length; i++) {
					for (int j = 0; j < data.length; j++) {
						if (data[j][i] != alreadyCheckedData[j][i]) {
							alreadyChecked = false;
							break checkLoop;
						}
					}
				}
			}
			
			if (!alreadyCheckedDatas.isEmpty() && alreadyChecked) {
				data[counterMove[0]][counterMove[1]] = GomokuModel.UNPLAYED;
				if (showAnalysis) {
					model.firePropertyChange(GomokuModel.ANALYSED_DONE, counterMove);
				}
				data[threatMove[0]][threatMove[1]] = GomokuModel.UNPLAYED;
				if (showAnalysis) {
					model.firePropertyChange(GomokuModel.ANALYSED_DONE, threatMove);
				}
			}
			
			int[] nextAttempt = findSecondaryStrike(data, playingColor, depth + 1, showAnalysis);

			int[][] dataCopy = new int[data.length][data[0].length];
			
			for (int i = 0; i < data[0].length; i++) {
				for (int j = 0; j < data.length; j++) {
					dataCopy[j][i] = data[j][i];
				}
			}
			
			data[counterMove[0]][counterMove[1]] = GomokuModel.UNPLAYED;
			if (showAnalysis) {
				model.firePropertyChange(GomokuModel.ANALYSED_DONE, counterMove);
			}
			data[threatMove[0]][threatMove[1]] = GomokuModel.UNPLAYED;
			if (showAnalysis) {
				model.firePropertyChange(GomokuModel.ANALYSED_DONE, threatMove);
			}

			if (nextAttempt != null) {
				System.out.println("[" + counterMove[0] + "]" + "[" + counterMove[1] + "] => [" + +nextAttempt[0] + "]"
						+ "[" + nextAttempt[1] + "]");
				List<int[]> newCouple = new ArrayList<int[]>();
				newCouple.add(counterMove);
				newCouple.add(nextAttempt);
				secondaryStrikeMovesList.add(0, newCouple);
				return threatMove;
			}

			threatMoves.remove(threatMove);
			
			alreadyCheckedDatas.add(dataCopy);
		}
		return null;
	}

	private int[] findBestThreatMove(int[][] data, int playingColor, List<int[]> threatMoves) throws Exception {
		
		double maxEvaluation = 0;
		
		int[] bestMove = null;
		
		for (int[] threatMove : threatMoves) {
			double evaluation = computeEvaluation(data, playingColor);
			
			if (evaluation > maxEvaluation) {
				maxEvaluation = evaluation;
				bestMove = threatMove;
			}
		}
		
		return null;
	}

	private int[] defendFromStrike(int[][] data, int playingColor) throws Exception {

		List<int[]> opponentWinThreats = getAllThreat5MoveList(data, -playingColor);

		if (!opponentWinThreats.isEmpty()) {
			return opponentWinThreats.get(0);
		}
		
//		List<int[]> strikeDefendingFromStrikeList = findStrikeDefendingFromStrikeMoves(data, playingColor);

		List<int[]> defendFromStrikeMoves = findDefendingFromStrikeMoves(data, playingColor);
//		defendFromStrikeMoves.addAll(strikeDefendingFromStrikeList);
		
		if (defendFromStrikeMoves.size() == 1) {
			return defendFromStrikeMoves.get(0);
		}
		
		return findBestMoveAmongTheseMoves(data, playingColor, defendFromStrikeMoves, false);
	}

	private int[] defendFromSecondaryStrike(int[][] data, int playingColor) throws Exception {

		int[] defendFromStrike = defendFromStrike(data, playingColor);

		if (defendFromStrike != null) {
			return defendFromStrike;
		}

		return findBestMoveAmongTheseMoves(data, playingColor, findDefendingFromSecondaryStrikeMoves(data, playingColor), false);
	}

	private List<int[]> findDefendingFromStrikeMoves(int[][] data, int playingColor) throws Exception {

		List<int[]> defendingMoves = new ArrayList<int[]>();
		
		List<int[]> threat4Moves = getAllThreat4MoveList(data, playingColor);

		for (int i = 0; i < data[0].length; i++) {
			for (int j = 0; j < data.length; j++) {
				if (data[j][i] == GomokuModel.UNPLAYED) {

					boolean alreadyFound = false;
					
					for (int[] threat4Move : threat4Moves) {
						if (threat4Move[0] == j && threat4Move[1] == i) {
							alreadyFound = true;
							break;
						}
					}
					
					if (!alreadyFound) {
						data[j][i] = playingColor;
						
						int[] opponentStrike = findStrike(data, -playingColor, new ArrayList<int[]>());
						
						if (opponentStrike == null) {
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

	private List<int[]> findStrikeDefendingFromStrikeMoves(int[][] data, int playingColor) throws Exception {
		
		List<int[]> defendingMoves = new ArrayList<int[]>();
		
		List<int[]> threat4Moves = getAllThreat4MoveList(data, playingColor);

		for (int[] threat4Move : threat4Moves) {
			
			data[threat4Move[0]][threat4Move[1]] = playingColor;
			List<int[]> threat5Moves = getAllThreat5MoveList(data, playingColor);
			
			data[threat5Moves.get(0)[0]][threat5Moves.get(0)[1]] = -playingColor;

			int[] newOpponentStrike = findStrike(data, -playingColor, new ArrayList<int[]>());
			
			if (newOpponentStrike == null) {
				defendingMoves.add(threat4Move);
			} else {
				List<int[]> otherAttempt = findStrikeDefendingFromStrikeMoves(data, playingColor);
				if (!otherAttempt.isEmpty()) {
					defendingMoves.add(threat4Move);
				} else {
					List<int[]> regularDefense = findDefendingFromStrikeMoves(data, playingColor);
					
					if (!regularDefense.isEmpty()) {
						defendingMoves.add(threat4Move);
					}
				}
			}
			
			data[threat5Moves.get(0)[0]][threat5Moves.get(0)[1]] = GomokuModel.UNPLAYED;
			data[threat4Move[0]][threat4Move[1]] = GomokuModel.UNPLAYED;
		}
		
		return defendingMoves;
	}

	private List<int[]> findDefendingFromSecondaryStrikeMoves(int[][] data, int playingColor) throws Exception {

		List<int[]> defendingMoves = new ArrayList<int[]>();

		for (int i = 0; i < data[0].length; i++) {
			for (int j = 0; j < data.length; j++) {
				if (data[j][i] == GomokuModel.UNPLAYED) {

					data[j][i] = playingColor;

					if (findSecondaryStrike(data, -playingColor, 0, false) == null) {
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

	private List<CellGroup> getAllOneCellGroups(GomokuData gomokuData, int playingColor) throws Exception {

		List<CellGroup> result = new ArrayList<CellGroup>();

		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			if (cellGroup.getCellList().size() == 1) {
				result.add(cellGroup);
			}
		}

		return result;
	}
	
	private List<CellGroup> getAllTwoCellGroups(GomokuData gomokuData, int playingColor) throws Exception {
		
		List<CellGroup> result = new ArrayList<CellGroup>();
		
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			if (cellGroup.getCellList().size() == 2) {
				result.add(cellGroup);
			}
		}
		
		return result;
	}

	public List<CellGroup> getAll5Groups(GomokuData gomokuData, int playingColor) throws Exception {
	
		List<CellGroup> result = new ArrayList<CellGroup>();
	
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			if (cellGroup.getCellList().size() == 5) {
				result.add(cellGroup);
			}
		}
	
		return result;
	}
	
	public List<CellGroup> getAllDoubleThreat5Groups(GomokuData gomokuData, int playingColor) throws Exception {
		
		List<CellGroup> result = new ArrayList<CellGroup>();
		
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			if (cellGroup.getDoubleThreatMoves(4).size() > 0) {
				result.add(cellGroup);
			}
		}
		
		return result;
	}

	public List<CellGroup> getAllSingleThreat5Groups(GomokuData gomokuData, int playingColor) throws Exception {
		
		List<CellGroup> result = new ArrayList<CellGroup>();
		
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			if (cellGroup.getSingleThreatMoves(4).size() > 0) {
				result.add(cellGroup);
			}
		}
		
		return result;
	}

	public List<CellGroup> getAllDoubleThreat4Groups(GomokuData gomokuData, int playingColor) throws Exception {
	
		List<CellGroup> result = new ArrayList<CellGroup>();
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			if (cellGroup.getDoubleThreatMoves(3).size() > 0) {
				result.add(cellGroup);
			}
		}
	
		return result;
	}

	public List<CellGroup> getAllSingleThreat4Groups(GomokuData gomokuData, int playingColor) throws Exception {
		
		List<CellGroup> result = new ArrayList<CellGroup>();
		
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			if (cellGroup.getSingleThreatMoves(3).size() > 0) {
				result.add(cellGroup);
			}
		}
		
		return result;
	}
	
	public List<CellGroup> getAllDoubleThreat3Groups(GomokuData gomokuData, int playingColor) throws Exception {
		
		List<CellGroup> result = new ArrayList<CellGroup>();
		
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			if (cellGroup.getDoubleThreatMoves(2).size() > 0) {
				result.add(cellGroup);
			}
		}
		
		return result;
	}

	public List<CellGroup> getAllSingleThreat3Groups(GomokuData gomokuData, int playingColor) throws Exception {
		
		List<CellGroup> result = new ArrayList<CellGroup>();
		
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			if (cellGroup.getSingleThreatMoves(2).size() > 0) {
				result.add(cellGroup);
			}
		}
		
		return result;
	}
	
	public List<CellGroup> getAllDoubleThreat2Groups(GomokuData gomokuData, int playingColor) throws Exception {
		
		List<CellGroup> result = new ArrayList<CellGroup>();
		
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			if (cellGroup.getDoubleThreatMoves(1).size() > 0) {
				result.add(cellGroup);
			}
		}
		
		return result;
	}

	public List<CellGroup> getAllSingleThreat2Groups(GomokuData gomokuData, int playingColor) throws Exception {
		
		List<CellGroup> result = new ArrayList<CellGroup>();
		
		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			if (cellGroup.getSingleThreatMoves(1).size() > 0) {
				result.add(cellGroup);
			}
		}
		
		return result;
	}

	public List<int[]> getAllThreat5MoveList(int[][] data, int playingColor) throws Exception {

		List<int[]> result = new ArrayList<int[]>();

		GomokuData gomokuData = new GomokuData(data, playingColor);

		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			result.addAll(cellGroup.getSingleThreatMoves(4));
		}

		return result;
	}

	public List<int[]> getAllThreat4MoveList(int[][] data, int playingColor) throws Exception {

		List<int[]> result = new ArrayList<int[]>();

		GomokuData gomokuData = new GomokuData(data, playingColor);

		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			result.addAll(cellGroup.getSingleThreatMoves(3));
		}

		return result;
	}

	public List<int[]> getAllDoubleThreat4MoveList(int[][] data, int playingColor) throws Exception {

		List<int[]> result = new ArrayList<int[]>();

		GomokuData gomokuData = new GomokuData(data, playingColor);

		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			result.addAll(cellGroup.getDoubleThreatMoves(3));
		}

		return result;
	}

	public List<int[]> getAllDoubleThreat3MoveList(int[][] data, int playingColor) throws Exception {

		List<int[]> result = new ArrayList<int[]>();

		GomokuData gomokuData = new GomokuData(data, playingColor);

		for (CellGroup cellGroup : gomokuData.getCellGroups()) {
			result.addAll(cellGroup.getDoubleThreatMoves(2));
		}

		return result;
	}

	/**
	 * 
	 * For each analyzed move, computer plays the best answer according to
	 * evaluation. Then returns highest resulting evaluation.
	 * 
	 * @param data
	 * @param playingColor
	 * @param analysedMoves
	 * @param showAnalysis 
	 * @return
	 * @throws Exception 
	 */
	private int[] findBestMoveAmongTheseMoves(int[][] data, int playingColor, List<int[]> analysedMoves, boolean showAnalysis) throws Exception {

		double maxEvaluation = Double.NEGATIVE_INFINITY;

		int[] bestMove = null;

		int[] bestOpponentMove = null;

		for (int[] analysedMove : analysedMoves) {
			
			if (Thread.interrupted()) {
				throw new Exception();
			}

			int[] opponentBestMove = new int[2];

			double minEvaluation = Double.POSITIVE_INFINITY;

			data[analysedMove[0]][analysedMove[1]] = playingColor;
			if (showAnalysis) {
				model.firePropertyChange(GomokuModel.ANALYSED_MOVE, analysedMove);
			}

			boolean defendFromStrikeFound = false;
			
//			int[] strike = findStrike(data, playingColor, new ArrayList<int[]>());
//			if (strike != null) {
//				
//				List<int[]> defendFromStrikeMoves = findDefendingFromStrikeMoves(data, -playingColor);
//				
//				if (defendFromStrikeMoves.size() == 1) {
//					opponentBestMove = defendFromStrikeMoves.get(0);
//				} else if (!defendFromStrikeMoves.isEmpty()) {
//					for (int[] defendFromStrikeMove : defendFromStrikeMoves) {
//						data[defendFromStrikeMove[0]][defendFromStrikeMove[1]] = -playingColor;
//						
//						double evaluation = computeEvaluation(data, playingColor) - OPPONENT_EVALUATION_FACTOR * computeEvaluation(data, -playingColor);
//						if (evaluation < minEvaluation) {
//							minEvaluation = evaluation;
//							opponentBestMove = defendFromStrikeMove;
//						}
//						
//						data[defendFromStrikeMove[0]][defendFromStrikeMove[1]] = GomokuModel.UNPLAYED;
//						
//						if (minEvaluation < maxEvaluation) {
//							break;
//						}
//						if (minEvaluation < maxEvaluation) {
//							break;
//						}
//					}
//					
//					defendFromStrikeFound = true;
//				}
//			}
			
			if (!defendFromStrikeFound) {
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
			if (showAnalysis) {
				model.firePropertyChange(GomokuModel.ANALYSED_MOVE, opponentBestMove);
			}
			double evaluation = computeEvaluation(data, playingColor)
					- OPPONENT_EVALUATION_FACTOR * computeEvaluation(data, -playingColor);
			if (evaluation > maxEvaluation) {
				maxEvaluation = evaluation;
				bestMove = analysedMove;
				bestOpponentMove = opponentBestMove;
			}

			data[opponentBestMove[0]][opponentBestMove[1]] = GomokuModel.UNPLAYED;
			data[analysedMove[0]][analysedMove[1]] = GomokuModel.UNPLAYED;
			if (showAnalysis) {
				model.firePropertyChange(GomokuModel.ANALYSED_DONE, opponentBestMove);
				model.firePropertyChange(GomokuModel.ANALYSED_DONE, analysedMove);
			}
		}

		return bestMove;
	}

}
