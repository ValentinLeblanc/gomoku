package engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.GomokuModel;

public class StoneData {

	public static final int WIN_EVALUATION = 100000;
	
	public static final int SINGLE_THREAT_5_EVALUATION = 1000;
	public static final int SINGLE_THREAT_4_EVALUATION = 3;
	public static final int SINGLE_THREAT_3_EVALUATION = 2;
	public static final int SINGLE_THREAT_2_EVALUATION = 0;
	
	public static final int DOUBLE_THREAT_5_EVALUATION = 16;
	public static final int DOUBLE_THREAT_4_EVALUATION = 100;
	public static final int DOUBLE_THREAT_3_EVALUATION = 10;
	public static final int DOUBLE_THREAT_2_EVALUATION = 2;

	int depth;
	int[][] data;
	int evaluatedColor;
	
	int count = 0;
	
	private List<StoneGroup> stoneGroups;
	private List<StoneGroup> opponentStoneGroups;
	
	Map<Integer, Map<Integer, Stone>> stoneMap;
	
	private Comparator<StoneGroup> sizeComparator = new Comparator<StoneGroup>() {

		@Override
		public int compare(StoneGroup o1, StoneGroup o2) {
			return o1.getStoneList().size() - o2.getStoneList().size() > 0 ? 1 : o1.getStoneList().size() - o2.getStoneList().size() < 0 ? -1 : 0;
		}
		
	};
	
	public StoneData(int depth, int[][] data, int evaluatedColor) throws Exception {
		this.depth = depth;
		this.data = data;
		this.evaluatedColor = evaluatedColor;
		
		initialize();
		
		computeStoneGroups();
	}

	private void initialize() throws Exception {
		
		stoneGroups = new ArrayList<StoneGroup>();
		opponentStoneGroups = new ArrayList<StoneGroup>();
		
		int columnCount = data.length;
		int rowCount = data[0].length;
		
		stoneMap = new HashMap<Integer, Map<Integer, Stone>>();
		
		for (int j = 0; j < columnCount; j++) {
			stoneMap.put(j, new HashMap<Integer, Stone>());
			for (int i = 0; i < rowCount; i++) {
				Stone newStone = new Stone(j, i);
				stoneMap.get(j).put(i, newStone);
			}
		}
	}

	private void computeStoneGroups() throws Exception {
		
		int columnCount = data.length;
		int rowCount = data[0].length;

		for (int row = 0; row < rowCount; row++) {
			int[][] horizontalStripe = new int[columnCount][2];
			for (int col = 0; col < columnCount; col++) {
				horizontalStripe[col][0] = col;
				horizontalStripe[col][1] = row;
			}
			computeStoneGroups(horizontalStripe, GomokuEngine.HORIZONTAL);
//			computeOpponentStoneGroups(horizontalStripe, GomokuEngine.HORIZONTAL);
		}

		for (int col = 0; col < columnCount; col++) {
			int[][] verticalStripe = new int[rowCount][2];
			for (int row = 0; row < rowCount; row++) {
				verticalStripe[row][0] = col;
				verticalStripe[row][1] = row;
			}
			computeStoneGroups(verticalStripe, GomokuEngine.VERTICAL);
//			computeOpponentStoneGroups(verticalStripe, GomokuEngine.VERTICAL);
		}

		for (int row = 0; row < rowCount; row++) {
			int[][] diagonal1Stripe = new int[columnCount - row][2];
			for (int col = 0; col < columnCount - row; col++) {
				diagonal1Stripe[col][0] = col;
				diagonal1Stripe[col][1] = row + col;
			}
			computeStoneGroups(diagonal1Stripe, GomokuEngine.DIAGONAL1);
//			computeOpponentStoneGroups(diagonal1Stripe, GomokuEngine.DIAGONAL1);
		}

		for (int col = 1; col < columnCount; col++) {
			int[][] diagonal1Stripe = new int[rowCount - col][2];
			for (int row = 0; row < rowCount - col; row++) {
				diagonal1Stripe[row][0] = col + row;
				diagonal1Stripe[row][1] = row;
			}
			computeStoneGroups(diagonal1Stripe, GomokuEngine.DIAGONAL1);
//			computeOpponentStoneGroups(diagonal1Stripe, GomokuEngine.DIAGONAL1);
		}

		for (int col = 0; col < columnCount; col++) {
			int[][] diagonal2Stripe = new int[rowCount - col][2];
			for (int row = rowCount - 1; row >= col; row--) {
				diagonal2Stripe[rowCount - 1 - row][0] = col - row + rowCount - 1;
				diagonal2Stripe[rowCount - 1 - row][1] = row;
			}
			computeStoneGroups(diagonal2Stripe, GomokuEngine.DIAGONAL2);
//			computeOpponentStoneGroups(diagonal2Stripe, GomokuEngine.DIAGONAL2);
		}

		for (int row = rowCount - 2; row >= 0; row--) {
			int[][] diagonal2Stripe = new int[row + 1][2];
			for (int col = 0; col <= row; col++) {
				diagonal2Stripe[col][0] = col;
				diagonal2Stripe[col][1] = row - col;
			}
			computeStoneGroups(diagonal2Stripe, GomokuEngine.DIAGONAL2);
//			computeOpponentStoneGroups(diagonal2Stripe, GomokuEngine.DIAGONAL2);
		}

		stoneGroups.sort(sizeComparator);
		opponentStoneGroups.sort(sizeComparator);
	}
	
	private void computeStoneGroups(int[][] stripe, int direction) throws Exception {

		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		StoneGroup currentStoneGroup = null;
		int k = 0;
		while (k < stripe.length - 4) {
			for (int h = 0; h < 5; h++) {
				if (data[stripe[k + h][0]][stripe[k + h][1]] == evaluatedColor) {
					if (currentStoneGroup == null) {
						currentStoneGroup = new StoneGroup(data, direction, 0);
					}
					Stone stone = stoneMap.get(stripe[k + h][0]).get(stripe[k + h][1]);
					stone.getStoneGroups().add(currentStoneGroup);
					currentStoneGroup.getStoneList().add(stone);
					currentStoneGroup.updateGroup();
				} else if (data[stripe[k + h][0]][stripe[k + h][1]] == -evaluatedColor) {
					currentStoneGroup = null;
					break;
				}
			}
			
			if (currentStoneGroup != null) {
				stoneGroups.add(currentStoneGroup);
			}
			
			currentStoneGroup = null;
			
			k++;
		}
		
		List<StoneGroup> toRemoveList = new ArrayList<StoneGroup>();
		
		for (int i = 0; i < stoneGroups.size(); i++) {
			for (int j = i + 1; j < stoneGroups.size(); j++) {
				if (stoneGroups.get(j).containsAll(stoneGroups.get(i))) {
					toRemoveList.add(stoneGroups.get(i));

					for (Stone stone : stoneGroups.get(i).getStoneList()) {
						stone.getStoneGroups().remove(stoneGroups.get(i));
					}

					break;
				}
			}
		}

		stoneGroups.removeAll(toRemoveList);
	}
	
//	private void computeOpponentStoneGroups(int[][] stripe, int direction) throws Exception {
//		
//		if (Thread.interrupted()) {
//			throw new InterruptedException();
//		}
//		
//		StoneGroup currentStoneGroup = null;
//		int k = 0;
//		while (k < stripe.length - 4) {
//			for (int h = 0; h < 5; h++) {
//				if (data[stripe[k + h][0]][stripe[k + h][1]] == -evaluatedColor) {
//					if (currentStoneGroup == null) {
//						currentStoneGroup = new StoneGroup(data, direction, depth);
//					}
//					Stone stone = stoneMap.get(stripe[k + h][0]).get(stripe[k + h][1]);
//					stone.getStoneGroups().add(currentStoneGroup);
//					currentStoneGroup.getStoneList().add(stone);
//					currentStoneGroup.updateGroup();
//				} else if (data[stripe[k + h][0]][stripe[k + h][1]] == evaluatedColor) {
//					currentStoneGroup = null;
//					break;
//				}
//			}
//			
//			if (currentStoneGroup != null) {
//				opponentStoneGroups.add(currentStoneGroup);
//			}
//			
//			currentStoneGroup = null;
//			
//			k++;
//		}
//		
//		List<StoneGroup> toRemoveList = new ArrayList<StoneGroup>();
//		
//		for (int i = 0; i < opponentStoneGroups.size(); i++) {
//			for (int j = i + 1; j < opponentStoneGroups.size(); j++) {
//				if (opponentStoneGroups.get(j).containsAll(opponentStoneGroups.get(i))) {
//					toRemoveList.add(opponentStoneGroups.get(i));
//					
//					for (Stone stone : opponentStoneGroups.get(i).getStoneList()) {
//						stone.getStoneGroups().remove(opponentStoneGroups.get(i));
//					}
//					
//					break;
//				}
//			}
//		}
//		
//		opponentStoneGroups.removeAll(toRemoveList);
//	}
	
	public static int getEvaluation(int depthGap, int originalEvaluation) {
		
		int k = 0;
		
		while (k < depthGap) {
			originalEvaluation = originalEvaluation / 10;
			k++;
		}
		
		return originalEvaluation;
	}

	public int computeEvaluation(GomokuModel model) throws Exception {
		
		int evaluation = 0;
		
		for (StoneGroup stoneGroup : stoneGroups) {
			if (stoneGroup.is5Group()) {
				return WIN_EVALUATION;
			}
		}
		
		for (StoneGroup stoneGroup : opponentStoneGroups) {
			if (stoneGroup.is5Group()) {
				return 0;
			}
		}
		
		evaluateThreats();
		
		int[][] threatPotentials = new int[data.length][data[0].length];
		for (int j = 0; j < data.length; j++) {
			for (int i = 0; i < data[0].length; i++) {
				Stone stone = stoneMap.get(j).get(i);
				
				stone.computePotential();

				threatPotentials[j][i] = stone.getPotential();
				evaluation += stone.getPotential();
			}
		}
		
		if (model != null) {
			model.firePropertyChange(GomokuModel.ENGINE_THREAT_EVALUATION_UPDATE, threatPotentials);
		}
		
		return evaluation;
	}
	
	private void evaluateThreats() throws Exception {
		
		for (StoneGroup stoneGroup : stoneGroups) {
			
			List<int[]> singleThreat5List = stoneGroup.getSingleThreatMoves(4);
			for (int[] threat : singleThreat5List) {
				evaluateThreat(stoneGroup, threat, ThreatType.SINGLE_THREAT_5);
			}
			
			List<int[]> doubleThreat4List = stoneGroup.getDoubleThreatMoves(3);
			for (int[] threat : doubleThreat4List) {
				evaluateThreat(stoneGroup, threat, ThreatType.DOUBLE_THREAT_4);
			}
			
			List<int[]> doubleThreat3List = stoneGroup.getDoubleThreatMoves(2);
			for (int[] threat : doubleThreat3List) {
				evaluateThreat(stoneGroup, threat, ThreatType.DOUBLE_THREAT_3);
			}
			
			List<int[]> doubleThreat2List = stoneGroup.getDoubleThreatMoves(1);
			for (int[] threat : doubleThreat2List) {
				evaluateThreat(stoneGroup, threat, ThreatType.DOUBLE_THREAT_2);
			}
			
			List<int[]> singleThreat4List = stoneGroup.getSingleThreatMoves(3);
			for (int[] threat : singleThreat4List) {
				evaluateThreat(stoneGroup, threat, ThreatType.SINGLE_THREAT_4);
			}
			
			List<int[]> singleThreat3List = stoneGroup.getSingleThreatMoves(2);
			for (int[] threat : singleThreat3List) {
				evaluateThreat(stoneGroup, threat, ThreatType.SINGLE_THREAT_3);
			}
			
			List<int[]> singleThreat2List = stoneGroup.getSingleThreatMoves(1);
			for (int[] threat : singleThreat2List) {
				evaluateThreat(stoneGroup, threat, ThreatType.SINGLE_THREAT_2);
			}
			
		}
	}

	private void evaluateThreat(StoneGroup stoneGroup, int[] threat, ThreatType threatType) throws Exception {
		
		List<int[]> spreadVectors = stoneGroup.getSpreadVectors(threat);
		
		if (data[threat[0]][threat[1]] == GomokuModel.UNPLAYED) {
			stoneMap.get(threat[0]).get(threat[1]).addThreatTypePotential(stoneGroup.getDirection(), threatType, 0);
		}
		
		for (int[] spreadVector : spreadVectors) {
			spreadThreat(stoneGroup.getDirection(), threat, threatType, spreadVector);
		}
	}

	private void spreadThreat(int direction, int[] threat, ThreatType threatType, int[] spreadVector) {
		
		int k = 1;
		
		int spreadX = threat[0] + spreadVector[0];
		int spreadY = threat[1] + spreadVector[1];
		
		while (k < 5 && spreadX >= 0 && spreadX < data.length && spreadY >= 0 && spreadY < data[0].length) {
			if (data[spreadX][spreadY] == -evaluatedColor) {
				break;
			}
			
			if (spreadX + spreadVector[0] >= 0 && spreadX + spreadVector[0] < data.length && spreadY + spreadVector[1] >= 0 && spreadY + spreadVector[1] < data[0].length) {
				
				if (data[spreadX + spreadVector[0]][spreadY + spreadVector[1]] == GomokuModel.UNPLAYED) {
					if (data[spreadX][spreadY] == GomokuModel.UNPLAYED) {
						stoneMap.get(spreadX).get(spreadY).addThreatTypePotential(direction, threatType, 1);
					}
				}
			}
			
			k++;
			spreadX += spreadVector[0];
			spreadY += spreadVector[1];
		}
	}

//	private void computeStoneGroupsThreatPotentials(int depth, StoneGroup stoneGroup, int[] threat, int threatEvaluation) throws Exception {
//		
//		int groupDepth = stoneGroup.getDepth();
//
//		Stone threatStone = stoneMap.get(threat[0]).get(threat[1]);
//		
//		int oldDepth = threatStone.getDepth();
//		int oldThreatEvaluation = threatStone.getThreatEvaluation();
//		
//		threatStone.setDepth(depth + 1);
//		threatStone.setThreatEvaluation(threatEvaluation);
//		
//		List<StoneGroup> newStoneGroups = new ArrayList<StoneGroup>();
//		
//		if (depth == 0 && groupDepth == 0) {
//			threatStone.incrementPotential(getEvaluation(0, threatStone.getThreatEvaluation()));
//		} else {
//			for (Stone stone : stoneGroup.getStoneList()) {
//				if (stone.getDepth() < groupDepth) {
//					stone.incrementPotential(getEvaluation(groupDepth - stone.getDepth(), threatStone.getThreatEvaluation()));
//				}
//			}
//		}
//		
//		data[threat[0]][threat[1]] = evaluatedColor;
//		
//		int minX = Math.max(0, threat[0] - 4);
//		int maxX = Math.min(data.length - 1, threat[0] + 4);
//		
//		int[][] horizontalStripe = new int[maxX - minX + 1][2];
//		for (int col = 0; col < maxX - minX + 1; col++) {
//			horizontalStripe[col][0] = minX + col;
//			horizontalStripe[col][1] = threat[1];
//		}
//		
//		int minY = Math.max(0, threat[1] - 4);
//		int maxY = Math.min(data.length - 1, threat[1] + 4);
//		
//		int[][] verticalStripe = new int[maxY - minY + 1][2];
//		for (int row = 0; row < maxY - minY + 1; row++) {
//			verticalStripe[row][0] = threat[0];
//			verticalStripe[row][1] = minY + row;
//		}
//		
//		int startX1 = threat[0];
//		int startY1 = threat[1];
//		
//		for (int k = 0; k < 4; k++) {
//			if (startX1 == 0 || startY1 == 0) {
//				break;
//			}
//			startX1 -= 1;
//			startY1 -= 1;
//		}
//		
//		int endX1 = threat[0];
//		int endY1 = threat[1];
//		
//		for (int k = 0; k < 4; k++) {
//			if (endX1 == data.length - 1 || endY1 == data.length - 1) {
//				break;
//			}
//			endX1 += 1;
//			endY1 += 1;
//		}
//		
//		int[][] diagonal1Stripe = new int[endX1 - startX1 + 1][2];
//		for (int index = 0; index < endX1 - startX1 + 1; index++) {
//			diagonal1Stripe[index][0] = startX1 + index;
//			diagonal1Stripe[index][1] = startY1 + index;
//		}
//		
//		int startX2 = threat[0];
//		int endY2 = threat[1];
//
//		for (int k = 0; k < 4; k++) {
//			if (startX2 == 0 || endY2 == data.length - 1) {
//				break;
//			}
//			startX2 -= 1;
//			endY2 += 1;
//		}
//		
//		int endX2 = threat[0];
//		int startY2 = threat[1];
//		
//		for (int k = 0; k < 4; k++) {
//			if (startY2 == 0 || endX2 == data.length - 1) {
//				break;
//			}
//			endX2 += 1;
//			startY2 -= 1;
//		}
//		
//		int[][] diagonal2Stripe = new int[endX2 - startX2 + 1][2];	
//		for (int index = 0; index < endX2 - startX2 + 1; index++) {
//			diagonal2Stripe[index][0] = startX2 + index;
//			diagonal2Stripe[index][1] = endY2 - index;
//		}
//
//		newStoneGroups.addAll(computeStoneGroups(horizontalStripe, GomokuEngine.HORIZONTAL, evaluatedColor, threat));
//		newStoneGroups.addAll(computeStoneGroups(verticalStripe, GomokuEngine.VERTICAL, evaluatedColor, threat));
//		newStoneGroups.addAll(computeStoneGroups(diagonal1Stripe, GomokuEngine.DIAGONAL1, evaluatedColor, threat));
//		newStoneGroups.addAll(computeStoneGroups(diagonal2Stripe, GomokuEngine.DIAGONAL2, evaluatedColor, threat));
//
////		computeStoneGroupsThreatPotentials(depth + 1, newStoneGroups);
//		
//		threatStone.setDepth(oldDepth);
//		threatStone.setThreatEvaluation(oldThreatEvaluation);
//
//		data[threat[0]][threat[1]] = GomokuModel.UNPLAYED;
//	}
	
	private List<StoneGroup> computeStoneGroups(int[][] stripe, int direction, int playingColor, int[] move) {
		
		List<StoneGroup> stoneGroups = new ArrayList<StoneGroup>();
		
		StoneGroup currentStoneGroup = null;
		int k = 0;
		while (k < stripe.length - 4) {
			for (int h = 0; h < 5; h++) {
				if (data[stripe[k + h][0]][stripe[k + h][1]] == playingColor) {
					if (currentStoneGroup == null) {
						currentStoneGroup = new StoneGroup(data, direction, 0);
					}
					Stone stone = stoneMap.get(stripe[k + h][0]).get(stripe[k + h][1]);
					stone.getStoneGroups().add(currentStoneGroup);
					currentStoneGroup.getStoneList().add(stone);
					currentStoneGroup.updateGroup();
				} else if (data[stripe[k + h][0]][stripe[k + h][1]] == -playingColor) {
					currentStoneGroup = null;
					break;
				}
			}
			
			if (currentStoneGroup != null) {
				stoneGroups.add(currentStoneGroup);
			}
			
			currentStoneGroup = null;
			
			k++;
		}
		
		List<StoneGroup> toRemoveList = new ArrayList<StoneGroup>();
		
		for (int i = 0; i < stoneGroups.size(); i++) {
			for (int j = i + 1; j < stoneGroups.size(); j++) {
				if (stoneGroups.get(j).containsAll(stoneGroups.get(i))) {
					toRemoveList.add(stoneGroups.get(i));

					for (Stone stone : stoneGroups.get(i).getStoneList()) {
						stone.getStoneGroups().remove(stoneGroups.get(i));
					}

					break;
				}
			}
		}

		stoneGroups.removeAll(toRemoveList);
		
		return stoneGroups;
	}

}
