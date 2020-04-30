package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.GomokuModel;

public class EngineData {

	public static final int WIN_EVALUATION = 10000;
	
	public static final int DOUBLE_THREAT_4_POTENTIAL = 100;
	public static final int DOUBLE_THREAT_3_POTENTIAL = 10;
	public static final int DOUBLE_THREAT_2_POTENTIAL = 1;
	
	int[][] data;
	
	int count = 0;
	
	private HashMap<Stone, List<Threat>> stoneThreatsMap;
	
	private HashMap<Stone, List<Threat>> emptyStoneThreatsMap;
	
	private HashMap<ThreatType, List<Threat>> threatMap;
	
	Map<Integer, Map<Integer, Stone>> stoneMap;

	private int playingColor;
	
	public EngineData(int[][] data, int playingColor) throws Exception {
		this.data = data;
		this.playingColor = playingColor;
		initialize();
	}

	private void printThreats() {
//		for (ThreatType threatType : threatMap.keySet()) {
			
			for (Threat threat : threatMap.get(ThreatType.SINGLE_THREAT_4)) {
				String direction = "";
				
				switch (threat.getDirection()) {
				case GomokuEngine.HORIZONTAL :
					direction = "HOR";
					break;
				case GomokuEngine.VERTICAL :
					direction = "VER";
					break;
				case GomokuEngine.DIAGONAL1 :
					direction = "DIAG1";
					break;
				case GomokuEngine.DIAGONAL2 :
					direction = "DIAG2";
					break;
				}
				
				
				String threatMove = " - [" + threat.getX() + "][" + threat.getY() + "]";
				
				String counterMoves = " => ";
				
				for (int[] counterMove : threat.getCounterMoves()) {
					counterMoves += " [" + counterMove[0] + "][" + counterMove[1] + "] ";
				}
				
				System.out.println("" + (threat.getColor() == GomokuModel.BLACK ? "BLACK " : "WHITE ") + threat.getThreatType() + " - " + direction + threatMove + counterMoves);
			}
//		}
	}

	private void initialize() throws Exception {
		
		threatMap = new HashMap<ThreatType, List<Threat>>();
		
		threatMap.put(ThreatType.SINGLE_THREAT_5, new ArrayList<Threat>());
		threatMap.put(ThreatType.SINGLE_THREAT_4, new ArrayList<Threat>());
		threatMap.put(ThreatType.SINGLE_THREAT_3, new ArrayList<Threat>());
		threatMap.put(ThreatType.SINGLE_THREAT_2, new ArrayList<Threat>());
		
		threatMap.put(ThreatType.DOUBLE_THREAT_5, new ArrayList<Threat>());
		threatMap.put(ThreatType.DOUBLE_THREAT_4, new ArrayList<Threat>());
		threatMap.put(ThreatType.DOUBLE_THREAT_3, new ArrayList<Threat>());
		threatMap.put(ThreatType.DOUBLE_THREAT_2, new ArrayList<Threat>());
		
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
		
		stoneThreatsMap = new HashMap<Stone, List<Threat>>();
		for (int j = 0; j < columnCount; j++) {
			for (int i = 0; i < rowCount; i++) {
				stoneThreatsMap.put(stoneMap.get(j).get(i), new ArrayList<Threat>());
			}
		}
		
		emptyStoneThreatsMap = new HashMap<Stone, List<Threat>>();
		for (int j = 0; j < columnCount; j++) {
			for (int i = 0; i < rowCount; i++) {
				emptyStoneThreatsMap.put(stoneMap.get(j).get(i), new ArrayList<Threat>());
			}
		}
		
	}
	
	public int computeEvaluation(GomokuModel model) throws Exception {
		
		int evaluation = 0;
		
		// WINNING CASE
		if (checkForWin(data, GomokuModel.BLACK) != null) {
			return WIN_EVALUATION;
		}
		
		if (checkForWin(data, GomokuModel.WHITE) != null) {
			return -WIN_EVALUATION;
		}
		
		computeThreats();
		
		evaluation += evaluateActualThreats();
		
//		printThreats();

		int[][][] threatPotentials = new int[2][data.length][data[0].length];
		
//		evaluation += evaluateEachThreatPotential(threatPotentials);
		
		if (model != null) {
			model.firePropertyChange(GomokuModel.ENGINE_THREAT_EVALUATION_UPDATE, threatPotentials);
		}
		
		return evaluation;
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

	private void computeThreats() {
		
		for (int j = 0; j < stoneMap.keySet().size(); j++) {
			for (int i = 0; i < stoneMap.get(j).keySet().size(); i++) {
				if (data[j][i] != GomokuModel.UNPLAYED) {
					int[] position = { j, i };
					ArrayList<Threat> newThreats = findThreats(data[j][i], position);
					
					Stone stone = stoneMap.get(j).get(i);
					
					for (Threat newThreat : newThreats) {
						if (!stoneThreatsMap.get(stone).contains(newThreat)) {
							stoneThreatsMap.get(stone).add(newThreat);
						}
						if (!threatMap.get(newThreat.getThreatType()).contains(newThreat)) {
							threatMap.get(newThreat.getThreatType()).add(newThreat);
						}
						
						if (!emptyStoneThreatsMap.get(stoneMap.get(newThreat.getX()).get(newThreat.getY())).contains(newThreat)) {
							emptyStoneThreatsMap.get(stoneMap.get(newThreat.getX()).get(newThreat.getY())).add(newThreat);
						}
					}
				}
			}
		}
	}
	
	private int evaluateActualThreats() {
		
		int globalEvaluation = 0;
		
		for (Threat doubleThreat5 : threatMap.get(ThreatType.DOUBLE_THREAT_5)) {
			globalEvaluation += evaluateThreat(doubleThreat5);
		}
		
		for (Threat singleThreat5 : threatMap.get(ThreatType.SINGLE_THREAT_5)) {
			globalEvaluation += evaluateThreat(singleThreat5);
		}
		
		if (Math.abs(globalEvaluation) >= WIN_EVALUATION) {
			return globalEvaluation / Math.abs(globalEvaluation) * WIN_EVALUATION;
		}
		
		for (Threat doubleThreat4 : threatMap.get(ThreatType.DOUBLE_THREAT_4)) {
			globalEvaluation += evaluateThreat(doubleThreat4);
		}
		
		if (Math.abs(globalEvaluation) >= WIN_EVALUATION) {
			return globalEvaluation / Math.abs(globalEvaluation) * WIN_EVALUATION;
		}
		
		for (Threat singleThreat4 : threatMap.get(ThreatType.SINGLE_THREAT_4)) {
			globalEvaluation += evaluateThreat(singleThreat4);
		}
		
		if (Math.abs(globalEvaluation) >= WIN_EVALUATION) {
			return globalEvaluation / Math.abs(globalEvaluation) * WIN_EVALUATION;
		}
		
		for (Threat doubleThreat3 : threatMap.get(ThreatType.DOUBLE_THREAT_3)) {
			globalEvaluation += evaluateThreat(doubleThreat3);
		}
		
		if (Math.abs(globalEvaluation) >= WIN_EVALUATION) {
			return globalEvaluation / Math.abs(globalEvaluation) * WIN_EVALUATION;
		}
		
		for (Threat doubleThreat2 : threatMap.get(ThreatType.DOUBLE_THREAT_2)) {
			globalEvaluation += evaluateThreat(doubleThreat2);
		}
		
		if (Math.abs(globalEvaluation) >= WIN_EVALUATION) {
			return globalEvaluation / Math.abs(globalEvaluation) * WIN_EVALUATION;
		}
		
		return globalEvaluation;
	}

	private int evaluateThreat(Threat threat) {
		int evaluation = 0;
		
		boolean isPlaying = threat.getColor() == playingColor;
		
		if (isPlaying) {
			List<int[]> counterMoves = threat.getCounterMoves();
			
			boolean opponentHasBetterThreatSomewhere = false;
			
			threatTypeLoop : for (ThreatType threatType : ThreatType.values()) {
				for (Threat opponentThreat : threatMap.get(threatType)) {
					if (opponentThreat.getPriority() < threat.getPriority() - 1 && opponentThreat.getColor() != threat.getColor() && (threat.getX() != opponentThreat.getX() || threat.getY() != opponentThreat.getY())) {
						opponentHasBetterThreatSomewhere = true;
						break threatTypeLoop;
					}
				}
			}
			
			if (opponentHasBetterThreatSomewhere) {
				return 0;
			}
			
			
			Stone threatStone = stoneMap.get(threat.getX()).get(threat.getY());
			
			List<Threat> connectedThreats = new ArrayList<Threat>();
			
			for (Threat connectedThreat : emptyStoneThreatsMap.get(threatStone)) {
				if (connectedThreat.getPriority() >= threat.getPriority() && connectedThreat.getColor() == threat.getColor() && !connectedThreat.equals(threat) && !connectedThreats.contains(connectedThreat)) {
					connectedThreats.add(connectedThreat);
				}
			}
			
			int minThreatPotential = WIN_EVALUATION;
			
			for (int[] counterMove : counterMoves) {
				
				int currentPotential = 0;
				
				Stone counterStone = stoneMap.get(counterMove[0]).get(counterMove[1]);
				
				List<Threat> counterThreats = new ArrayList<Threat>();
				
				for (Threat counterThreat : emptyStoneThreatsMap.get(counterStone)) {
					if (counterThreat.getColor() != threat.getColor()) {
						counterThreats.add(counterThreat);
					}
				}
				
				
				for (Threat connectedThreat : connectedThreats) {
					boolean isCounterThreatBetter = false;
					int maxCounterThreatPotential = 0;
					
					for (Threat counterThreat : counterThreats) {
						if (counterThreat.getPriority() < connectedThreat.getPriority()) {
							isCounterThreatBetter = true;
							break;
						} else if (counterThreat.getPotentialAfterNotBeingCountered() > maxCounterThreatPotential) {
							maxCounterThreatPotential = counterThreat.getPotentialAfterNotBeingCountered();
						}
					}
					
					if (!isCounterThreatBetter) {
						currentPotential += connectedThreat.getPotentialAfterNotBeingCountered();
						currentPotential -= maxCounterThreatPotential;
					}
				}
				
				if (currentPotential < minThreatPotential) {
					minThreatPotential = currentPotential;
				}
			}
			
			if (minThreatPotential < 0) {
				minThreatPotential = 0;
			}
			
			evaluation += threat.getColor() * minThreatPotential;
		} else {
			
			List<Threat> connectedThreats = new ArrayList<Threat>();
				
			Stone threatStone = stoneMap.get(threat.getX()).get(threat.getY());
			
			for (Threat connectedThreat : emptyStoneThreatsMap.get(threatStone)) {
				if (connectedThreat.getPriority() >= threat.getPriority() && connectedThreat.getColor() == threat.getColor() && !connectedThreat.equals(threat) && !connectedThreats.contains(connectedThreat)) {
					connectedThreats.add(connectedThreat);
				}
			}
			
			int minThreatPotential = WIN_EVALUATION;
			
			List<int[]> killingMoves = threat.getKillingMoves();
			
			for (int[] killingMove : killingMoves) {
				
				int currentPotential = 0;
				
				Stone killingStone = stoneMap.get(killingMove[0]).get(killingMove[1]);
				
				List<Threat> killingThreats = new ArrayList<Threat>();
				
				for (Threat counterThreat : emptyStoneThreatsMap.get(killingStone)) {
					if (counterThreat.getColor() != threat.getColor()) {
						killingThreats.add(counterThreat);
					}
				}
				
				for (Threat connectedThreat : connectedThreats) {
					boolean iskillingThreatBetter = false;
					for (Threat killingThreat : killingThreats) {
						if (killingThreat.getPriority() < connectedThreat.getPriority()) {
							iskillingThreatBetter = true;
							break;
						}
					}
					
					if (!iskillingThreatBetter) {
						currentPotential += connectedThreat.getPotentialAfterBeingCountered();
					}
				}
				if (currentPotential < minThreatPotential) {
					minThreatPotential = currentPotential;
					if (minThreatPotential == 0) {
						break;
					}
				}
			}
			
			evaluation += threat.getColor() * minThreatPotential;

		}
		
		return evaluation;
	}
	
	private int evaluateSingleThreat5(Threat singleThreat5) {
		
		int evaluation = 0;
		
		if (singleThreat5.getColor() == playingColor) {
			return singleThreat5.getColor() * WIN_EVALUATION;
		}
			
		Stone counterStone = stoneMap.get(singleThreat5.getX()).get(singleThreat5.getY());
		
		boolean countersWithWin = false;
		
		for (Threat threat : threatMap.get(ThreatType.SINGLE_THREAT_5)) {
			if (threat.getColor() == -singleThreat5.getColor()) {
				countersWithWin = true;
				break;
			}
		}
		
		boolean countersWithDoubleThreat5 = false;
		boolean countersWithThreat5 = false;
		boolean countersWithDoubleThreat4 = false;
		boolean countersWithDoubleThreat3 = false;
		
		Threat singleThreat5Counter = null;
		
		for (Threat counterThreat : emptyStoneThreatsMap.get(counterStone)) {
			
			if (counterThreat.getColor() == singleThreat5.getColor()) {
				continue;
			}
			
			if (counterThreat.getThreatType() == ThreatType.DOUBLE_THREAT_4) {
				countersWithDoubleThreat5 = true;
				if (countersWithWin && countersWithThreat5 && countersWithDoubleThreat4 && countersWithDoubleThreat3) {
					break;
				}
			}
			if (counterThreat.getThreatType() == ThreatType.SINGLE_THREAT_4) {
				countersWithThreat5 = true;
				singleThreat5Counter = counterThreat;
				if (countersWithWin && countersWithDoubleThreat5 && countersWithDoubleThreat4 && countersWithDoubleThreat3) {
					break;
				}
			}
			if (counterThreat.getThreatType() == ThreatType.DOUBLE_THREAT_3) {
				countersWithDoubleThreat4 = true;
				if (countersWithWin && countersWithDoubleThreat5 && countersWithThreat5 && countersWithDoubleThreat3) {
					break;
				}
			}
			if (counterThreat.getThreatType() == ThreatType.DOUBLE_THREAT_2) {
				countersWithDoubleThreat3 = true;
				if (countersWithWin && countersWithDoubleThreat5 && countersWithThreat5 && countersWithDoubleThreat4) {
					break;
				}
			}
		}
		
		if (countersWithWin) {
			return -singleThreat5.getColor() * WIN_EVALUATION;
		}
		
		List<Threat> alreadyEncounteredThreats = new ArrayList<Threat>();
		
		for (int[] otherStoneMove : singleThreat5.getStonePack().getStoneList()) {
			
			Stone otherStone = stoneMap.get(otherStoneMove[0]).get(otherStoneMove[1]);
			for (Threat connectedThreat : stoneThreatsMap.get(otherStone)) {
				if (!connectedThreat.equals(singleThreat5) && !alreadyEncounteredThreats.contains(connectedThreat)) {
					if (connectedThreat.getThreatType() == ThreatType.SINGLE_THREAT_5) {
						return singleThreat5.getColor() * WIN_EVALUATION;
					} else if (countersWithDoubleThreat5) {
						return -singleThreat5.getColor() * WIN_EVALUATION;
					} else if (connectedThreat.getThreatType() == ThreatType.DOUBLE_THREAT_4) {
						if (!countersWithThreat5) {
							return singleThreat5.getColor() * WIN_EVALUATION;
						} else {
							if (singleThreat5Counter.getCounterMoves().get(0)[0] == connectedThreat.getX() && singleThreat5Counter.getCounterMoves().get(0)[1] == connectedThreat.getY()) {
								return singleThreat5.getColor() * WIN_EVALUATION;
							}
						}
					} else if (connectedThreat.getThreatType() == ThreatType.DOUBLE_THREAT_3) {
						if (!countersWithThreat5 && !countersWithDoubleThreat4) {
							evaluation += singleThreat5.getColor() * DOUBLE_THREAT_3_POTENTIAL;
						} else if (countersWithThreat5) {
 							if (singleThreat5Counter.getCounterMoves().get(0)[0] == connectedThreat.getX() && singleThreat5Counter.getCounterMoves().get(0)[1] == connectedThreat.getY()) {
 								evaluation += singleThreat5.getColor() * DOUBLE_THREAT_3_POTENTIAL;
							}
						}
					}
				}
				alreadyEncounteredThreats.add(connectedThreat);
			}
		}
		
		return evaluation;
	}
	
	private int evaluateDoubleThreat4(Threat doubleThreat4) {
		
		int evaluation = 0;
		
		if (doubleThreat4.getColor() == playingColor) {
			
			boolean countersWithWin = false;
			
			for (Threat threat : threatMap.get(ThreatType.SINGLE_THREAT_5)) {
				if (threat.getColor() == -doubleThreat4.getColor()) {
					countersWithWin = true;
					break;
				}
			}
	
			if (!countersWithWin) {
				return doubleThreat4.getColor() * WIN_EVALUATION;
			}
			
		} else {
			
			boolean killsWithSingleThreat5 = false;
			boolean killsWithDoubleThreat4 = false;
			boolean killsWithDoubleThreat3 = false;
			
			for (int[] killingMove : doubleThreat4.getKillingMoves()) {
				Stone killingStone = stoneMap.get(killingMove[0]).get(killingMove[1]);
				
				for (Threat killingThreat : emptyStoneThreatsMap.get(killingStone)) {
					
					if (killingThreat.getColor() == doubleThreat4.getColor()) {
						continue;
					}
					
					if (killingThreat.getThreatType() == ThreatType.SINGLE_THREAT_4) {
						killsWithSingleThreat5 = true;
					} else if (killingThreat.getThreatType() == ThreatType.DOUBLE_THREAT_3) {
						killsWithDoubleThreat4 = true;
					} else if (killingThreat.getThreatType() == ThreatType.DOUBLE_THREAT_2) {
						killsWithDoubleThreat3 = true;
					}
				}
			}
			
			if (killsWithSingleThreat5) {
				return 0;
			}
			
			if (killsWithDoubleThreat4) {
				return 0;
			}
			
			List<Threat> alreadyEncounteredThreats = new ArrayList<Threat>();
			
			for (int[] otherStoneMove : doubleThreat4.getStonePack().getStoneList()) {
				
				Stone otherStone = stoneMap.get(otherStoneMove[0]).get(otherStoneMove[1]);
				for (Threat connectedThreat : stoneThreatsMap.get(otherStone)) {
					if (connectedThreat.getDirection() != doubleThreat4.getDirection() && connectedThreat.getStonePack() != doubleThreat4.getStonePack() && !connectedThreat.equals(doubleThreat4) && !alreadyEncounteredThreats.contains(connectedThreat)) {
						if (connectedThreat.getThreatType() == ThreatType.DOUBLE_THREAT_4) {
							evaluation += doubleThreat4.getColor() * DOUBLE_THREAT_4_POTENTIAL;
						} else if (connectedThreat.getThreatType() == ThreatType.DOUBLE_THREAT_3) {
							evaluation += doubleThreat4.getColor() * DOUBLE_THREAT_3_POTENTIAL;
						} else if (connectedThreat.getThreatType() == ThreatType.DOUBLE_THREAT_2) {
							if (!killsWithDoubleThreat3) {
								evaluation += doubleThreat4.getColor() * DOUBLE_THREAT_2_POTENTIAL;
							}
						}
						alreadyEncounteredThreats.add(connectedThreat);
					}
				}
			}
			
		}
		
		return evaluation;
	}

	private int evaluateSingleThreat4(Threat singleThreat4) {
		
		int evaluation = 0;
		
		Stone counterStone = stoneMap.get(singleThreat4.getCounterMoves().get(0)[0]).get(singleThreat4.getCounterMoves().get(0)[1]);
		
		boolean countersWithWin = false;
		
		for (Threat threat : threatMap.get(ThreatType.SINGLE_THREAT_5)) {
			if (threat.getColor() == -singleThreat4.getColor()) {
				countersWithWin = true;
				break;
			}
		}
		
		boolean countersWithThreat5 = false;
		boolean countersWithDoubleThreat4 = false;
		boolean countersWithDoubleThreat3 = false;
		
		Threat singleThreat5Counter = null;

		for (Threat counterThreat : emptyStoneThreatsMap.get(counterStone)) {
			
			if (counterThreat.getColor() == singleThreat4.getColor()) {
				continue;
			}
			
			if (counterThreat.getThreatType() == ThreatType.DOUBLE_THREAT_4) {
				countersWithDoubleThreat4 = true;
			}
			
			if (counterThreat.getThreatType() == ThreatType.SINGLE_THREAT_4) {
				countersWithThreat5 = true;
				singleThreat5Counter = counterThreat;
			}
			
			if (counterThreat.getThreatType() == ThreatType.DOUBLE_THREAT_3) {
				countersWithDoubleThreat4 = true;
			}
			if (counterThreat.getThreatType() == ThreatType.DOUBLE_THREAT_2) {
				countersWithDoubleThreat3 = true;
			}
		}
		
		Stone threatStone = stoneMap.get(singleThreat4.getX()).get(singleThreat4.getY());
		
		for (Threat nextThreat : emptyStoneThreatsMap.get(threatStone)) {
			
			if (nextThreat.getColor() != singleThreat4.getColor()) {
				continue;
			}
			
			if (nextThreat.equals(singleThreat4)) {
				continue;
			}
			
			if (nextThreat.getThreatType() == ThreatType.SINGLE_THREAT_4) {
				if (!countersWithWin) {
					return singleThreat4.getColor() * WIN_EVALUATION;
				}
			} else if (nextThreat.getThreatType() == ThreatType.DOUBLE_THREAT_3) {
				if (!countersWithWin && !countersWithThreat5) {
					if (singleThreat4.getColor() == playingColor) {
						return singleThreat4.getColor() * WIN_EVALUATION;
					}
					evaluation += DOUBLE_THREAT_4_POTENTIAL;
				} else if (!countersWithWin && countersWithThreat5) {
					for (int[] counterMove : singleThreat5Counter.getCounterMoves()) {
						if (counterMove[0] == nextThreat.getNextAttackMoves().get(0)[0] && counterMove[1] == nextThreat.getNextAttackMoves().get(0)[1]) {
							if (singleThreat4.getColor() == playingColor) {
								return nextThreat.getColor() * WIN_EVALUATION;
							}
							evaluation += DOUBLE_THREAT_4_POTENTIAL;
						}
					}
				}
			} else if (nextThreat.getThreatType() == ThreatType.DOUBLE_THREAT_2) {
				if (!countersWithWin && !countersWithThreat5 && !countersWithDoubleThreat4 && !countersWithDoubleThreat3) {
					if (singleThreat4.getColor() == playingColor) {
						evaluation += DOUBLE_THREAT_3_POTENTIAL;
					}
				}
			}
		}
		
		return evaluation;
	}
	
	private int evaluateDoubleThreat3(Threat doubleThreat3) {
		
		int evaluation = 0;
		
		if (doubleThreat3.getColor() == playingColor) {
			
			boolean countersWithWin = false;
			
			for (Threat threat : threatMap.get(ThreatType.SINGLE_THREAT_5)) {
				if (threat.getColor() == -doubleThreat3.getColor()) {
					countersWithWin = true;
					break;
				}
			}
			
			boolean countersWithThreat5 = false;
			boolean countersWithDoubleThreat4 = false;
			boolean countersWithDoubleThreat3 = false;
			
			Stone counterStone = stoneMap.get(doubleThreat3.getCounterMoves().get(0)[0]).get(doubleThreat3.getCounterMoves().get(0)[1]);
			
			for (Threat counterThreat : emptyStoneThreatsMap.get(counterStone)) {
				
				if (counterThreat.getColor() == doubleThreat3.getColor()) {
					continue;
				}
				
				if (counterThreat.getThreatType() == ThreatType.DOUBLE_THREAT_4) {
					countersWithDoubleThreat4 = true;
				}
				
				if (counterThreat.getThreatType() == ThreatType.SINGLE_THREAT_4) {
					countersWithThreat5 = true;
				}
				
				if (counterThreat.getThreatType() == ThreatType.DOUBLE_THREAT_3) {
					countersWithDoubleThreat4 = true;
				}
				if (counterThreat.getThreatType() == ThreatType.DOUBLE_THREAT_2) {
					countersWithDoubleThreat3 = true;
				}
			}
			
			Stone threatStone = stoneMap.get(doubleThreat3.getX()).get(doubleThreat3.getY());
			
			for (Threat nextThreat : emptyStoneThreatsMap.get(threatStone)) {
				
				if (nextThreat.getColor() != doubleThreat3.getColor()) {
					continue;
				}
				
				if (nextThreat.equals(doubleThreat3)) {
					continue;
				}
				
				if (nextThreat.getThreatType() == ThreatType.SINGLE_THREAT_4) {
					if (!countersWithWin) {
						return doubleThreat3.getColor() * WIN_EVALUATION;
					}
				} else if (nextThreat.getThreatType() == ThreatType.DOUBLE_THREAT_3) {
					if (!countersWithWin && !countersWithThreat5) {
						return doubleThreat3.getColor() * WIN_EVALUATION;
					}
				} else if (nextThreat.getThreatType() == ThreatType.DOUBLE_THREAT_2) {
					if (!countersWithWin && !countersWithThreat5 && !countersWithDoubleThreat4 && !countersWithDoubleThreat3) {
						evaluation += DOUBLE_THREAT_3_POTENTIAL;
					}
				}
			}
		} else {
			
			boolean killsWithSingleThreat5 = false;
			boolean killsWithDoubleThreat4 = false;
			boolean killsWithDoubleThreat3 = false;
			
			for (int[] killingMove : doubleThreat3.getKillingMoves()) {
				Stone killingStone = stoneMap.get(killingMove[0]).get(killingMove[1]);
				
				for (Threat killingThreat : emptyStoneThreatsMap.get(killingStone)) {
					
					if (killingThreat.getColor() == doubleThreat3.getColor()) {
						continue;
					}
					
					if (killingThreat.getThreatType() == ThreatType.SINGLE_THREAT_4) {
						killsWithSingleThreat5 = true;
					} else if (killingThreat.getThreatType() == ThreatType.DOUBLE_THREAT_3) {
						killsWithDoubleThreat4 = true;
					} else if (killingThreat.getThreatType() == ThreatType.DOUBLE_THREAT_2) {
						killsWithDoubleThreat3 = true;
					}
				}
			}
			
			if (killsWithSingleThreat5) {
				return 0;
			}
			
			if (killsWithDoubleThreat4) {
				return 0;
			}
			
			List<Threat> alreadyEncounteredThreats = new ArrayList<Threat>();
			
			for (int[] otherStoneMove : doubleThreat3.getStonePack().getStoneList()) {
				
				Stone otherStone = stoneMap.get(otherStoneMove[0]).get(otherStoneMove[1]);
				for (Threat connectedThreat : stoneThreatsMap.get(otherStone)) {
					if (connectedThreat.getStonePack() != doubleThreat3.getStonePack() && !connectedThreat.equals(doubleThreat3) && !alreadyEncounteredThreats.contains(connectedThreat)) {
						if (connectedThreat.getThreatType() == ThreatType.DOUBLE_THREAT_4) {
							evaluation += doubleThreat3.getColor() * DOUBLE_THREAT_4_POTENTIAL;
						} else if (connectedThreat.getThreatType() == ThreatType.DOUBLE_THREAT_3) {
							evaluation += doubleThreat3.getColor() * DOUBLE_THREAT_3_POTENTIAL;
						} else if (connectedThreat.getThreatType() == ThreatType.DOUBLE_THREAT_2) {
							if (!killsWithDoubleThreat3) {
								evaluation += doubleThreat3.getColor() * DOUBLE_THREAT_2_POTENTIAL;
							}
						}
						alreadyEncounteredThreats.add(connectedThreat);
					}
				}
			}
			
		}
		
		return evaluation;
	}

	private int evaluateEachThreatPotential(int[][][] threatPotentials) throws Exception {
		
		int globalEvaluation = 0;
		
		for (Threat threat : threatMap.get(ThreatType.SINGLE_THREAT_5)) {
			int threatValue = evaluateThreat(threat, 0);
			if (threat.getColor() == GomokuModel.BLACK) {
				globalEvaluation += threatValue;
				threatPotentials[0][threat.getX()][threat.getY()] += threatValue;
			} else {
				globalEvaluation -= threatValue;
				threatPotentials[1][threat.getX()][threat.getY()] += threatValue;
			}
		}
		
		for (Threat threat : threatMap.get(ThreatType.SINGLE_THREAT_4)) {
			int threatValue = evaluateThreat(threat, 0);
			if (threat.getColor() == GomokuModel.BLACK) {
				globalEvaluation += threatValue;
				threatPotentials[0][threat.getX()][threat.getY()] += threatValue;
			} else {
				globalEvaluation -= threatValue;
				threatPotentials[1][threat.getX()][threat.getY()] += threatValue;
			}
		}
		
		for (Threat threat : threatMap.get(ThreatType.DOUBLE_THREAT_4)) {
			int threatValue = evaluateThreat(threat, 0);
			if (threat.getColor() == GomokuModel.BLACK) {
				globalEvaluation += threatValue;
				threatPotentials[0][threat.getX()][threat.getY()] += threatValue;
			} else {
				globalEvaluation -= threatValue;
				threatPotentials[1][threat.getX()][threat.getY()] += threatValue;
			}
		}
		
		for (Threat threat : threatMap.get(ThreatType.DOUBLE_THREAT_3)) {
			int threatValue = evaluateThreat(threat, 0);
			if (threat.getColor() == GomokuModel.BLACK) {
				globalEvaluation += threatValue;
				threatPotentials[0][threat.getX()][threat.getY()] += threatValue;
			} else {
				globalEvaluation -= threatValue;
				threatPotentials[1][threat.getX()][threat.getY()] += threatValue;
			}
		}
		
		for (Threat threat : threatMap.get(ThreatType.DOUBLE_THREAT_2)) {
			int threatValue = evaluateThreat(threat, 0);
			if (threat.getColor() == GomokuModel.BLACK) {
				globalEvaluation += threatValue;
				threatPotentials[0][threat.getX()][threat.getY()] += threatValue;
			} else {
				globalEvaluation -= threatValue;
				threatPotentials[1][threat.getX()][threat.getY()] += threatValue;
			}
		}
		
		return globalEvaluation;
	}

	private ArrayList<Threat> findThreats(int color, int[] position) {
		
		ArrayList<Threat> threats = new ArrayList<Threat>();
		
		threats.addAll(findThreats(color, ThreatType.SINGLE_THREAT_5, position));
		threats.addAll(findThreats(color, ThreatType.SINGLE_THREAT_4, position));
//		threats.addAll(findThreats(color, ThreatType.SINGLE_THREAT_3, position));
		
		threats.addAll(findThreats(color, ThreatType.DOUBLE_THREAT_5, position));
		threats.addAll(findThreats(color, ThreatType.DOUBLE_THREAT_4, position));
		threats.addAll(findThreats(color, ThreatType.DOUBLE_THREAT_3, position));
		threats.addAll(findThreats(color, ThreatType.DOUBLE_THREAT_2, position));
		
		return threats;
	}

	private List<Threat> findSingleThreat(int color, int[] position, int stoneNumberGoal) {
		
		List<Threat> threats = new ArrayList<Threat>();
		
		threats.addAll(findSingleThreats(color, position, GomokuEngine.HORIZONTAL, stoneNumberGoal));
		threats.addAll(findSingleThreats(color, position, GomokuEngine.VERTICAL, stoneNumberGoal));
		threats.addAll(findSingleThreats(color, position, GomokuEngine.DIAGONAL1, stoneNumberGoal));
		threats.addAll(findSingleThreats(color, position, GomokuEngine.DIAGONAL2, stoneNumberGoal));
		
		return threats;
	}

	private List<Threat> findSingleThreats(int color, int[] position, int direction, int stoneNumberGoal) {
		
		List<Threat> threats = new ArrayList<Threat>();
		
		int[] vector = new int[2];
		
		switch (direction) {
		case GomokuEngine.HORIZONTAL :
			vector[0] = 1;
			vector[1] = 0;
			break;
		case GomokuEngine.VERTICAL :
			vector[0] = 0;
			vector[1] = 1;
			break;
		case GomokuEngine.DIAGONAL1 :
			vector[0] = 1;
			vector[1] = 1;
			break;
		case GomokuEngine.DIAGONAL2 :
			vector[0] = 1;
			vector[1] = -1;
			break;
		}
		
		ThreatType threatType = null;
		
		switch (stoneNumberGoal) {
		case 2:
			threatType = ThreatType.SINGLE_THREAT_2;
			break;
		case 3:
			threatType = ThreatType.SINGLE_THREAT_3;
			break;
		case 4:
			threatType = ThreatType.SINGLE_THREAT_4;
			break;
		case 5:
			threatType = ThreatType.SINGLE_THREAT_5;
			break;
		}
		
		StonePack stonePack = new StonePack(direction, data, color);
		
		int availableSpace = 0;
		
		int newX = position[0] - vector[0];
		int newY = position[1] - vector[1];
		
		int beforeSpace = 0;
		
		while (newX != -1 && newY != -1 && newX != data.length && newY != data.length && data[newX][newY] != -color) {
			newX -= vector[0];
			newY -= vector[1];
			beforeSpace++;
			if (beforeSpace == 4) {
				break;
			}
		}
		
		newX = position[0] + vector[0];
		newY = position[1] + vector[1];
		
		int afterSpace = 0;
		
		while (newX != -1 && newY != -1 && newX != data.length && newY != data.length && data[newX][newY] != -color) {
			newX += vector[0];
			newY += vector[1];
			afterSpace++;
			if (afterSpace == 4) {
				break;
			}
		}
		
		availableSpace = beforeSpace + afterSpace + 1;
		
		if (availableSpace < 5) {
			return threats;
		}
		
		int currentStoneNumber = 0;
		int currentBlankNumber = 0;
		
		for (int k = -beforeSpace; k < afterSpace + 1; k++) {
			if (data[position[0] + k * vector[0]][position[1] + k * vector[1]] == color) {
				currentStoneNumber++;
				int[] newStone = new int[2];
				newStone[0] = position[0] + k * vector[0];
				newStone[1] = position[1] + k * vector[1];
				stonePack.getStoneList().add(newStone);
				stonePack.updateSides();
			}
			
			if (currentStoneNumber > 0) {
				if (data[position[0] + k * vector[0]][position[1] + k * vector[1]] == GomokuModel.UNPLAYED) {
					currentBlankNumber++;
					int[] counterMove = new int[2];
					counterMove[0] = position[0] + k * vector[0];
					counterMove[1] = position[1] + k * vector[1];
				}
			}
			
			if (currentBlankNumber > 5 - stoneNumberGoal + 1) {
				break;
			}
		}
		
		if (currentStoneNumber == stoneNumberGoal - 1) {
			List<int[]> threatMoves = stonePack.computeSingleAttackMoves();
			
			for (int[] threatMove : threatMoves) {
				Threat threat = new Threat(stonePack, threatType, threatMove[0], threatMove[1], direction, color);
				
				if (threat.getThreatType() != ThreatType.SINGLE_THREAT_5) {
					for (int[] counterMove : threatMoves) {
						if (counterMove != threatMove) {
							threat.getCounterMoves().add(counterMove);
						}
					}
					threat.getKillingMoves().addAll(threat.getCounterMoves());
				}
				
				threat.getKillingMoves().add(threatMove);
				
				threats.add(threat);
			}
		}
		
		return threats;
	}

	private List<Threat> findThreats(int color, ThreatType threatType, int[] position) {
		
		List<Threat> threats = new ArrayList<Threat>();

		switch (threatType) {
		case SINGLE_THREAT_5:
			threats.addAll(findSingleThreat(color, position, 5));
			break;
		case SINGLE_THREAT_4:
			threats.addAll(findSingleThreat(color, position, 4));
			break;
		case SINGLE_THREAT_3:
			threats.addAll(findSingleThreat(color, position, 3));
			break;
		case DOUBLE_THREAT_5:
			threats.addAll(findDoubleThreat(color, position, 5));
			break;
		case DOUBLE_THREAT_4:
			threats.addAll(findDoubleThreat(color, position, 4));
			break;
		case DOUBLE_THREAT_3:
			threats.addAll(findDoubleThreat(color, position, 3));
			break;
		case DOUBLE_THREAT_2:
			threats.addAll(findDoubleThreat(color, position, 2));
			break;
		default:
			break;
		}
		
		return threats;
	}

	private List<Threat> findDoubleThreat(int color, int[] position, int stoneNumberGoal) {
		
		List<Threat> threats = new ArrayList<Threat>();
		
		threats.addAll(findDoubleThreat(color, position, GomokuEngine.HORIZONTAL, stoneNumberGoal));
		threats.addAll(findDoubleThreat(color, position, GomokuEngine.VERTICAL, stoneNumberGoal));
		threats.addAll(findDoubleThreat(color, position, GomokuEngine.DIAGONAL1, stoneNumberGoal));
		threats.addAll(findDoubleThreat(color, position, GomokuEngine.DIAGONAL2, stoneNumberGoal));
		
		return threats;
	}

	private List<Threat> findDoubleThreat(int color, int[] position, int direction, int stoneNumberGoal) {
		
		List<Threat> threats = new ArrayList<Threat>();
		
		int[] vector = new int[2];
		
		switch (direction) {
		case GomokuEngine.HORIZONTAL :
			vector[0] = 1;
			vector[1] = 0;
			break;
		case GomokuEngine.VERTICAL :
			vector[0] = 0;
			vector[1] = 1;
			break;
		case GomokuEngine.DIAGONAL1 :
			vector[0] = 1;
			vector[1] = 1;
			break;
		case GomokuEngine.DIAGONAL2 :
			vector[0] = 1;
			vector[1] = -1;
			break;
		}
		
		ThreatType threatType = null;
		
		switch (stoneNumberGoal) {
		case 2:
			threatType = ThreatType.DOUBLE_THREAT_2;
			break;
		case 3:
			threatType = ThreatType.DOUBLE_THREAT_3;
			break;
		case 4:
			threatType = ThreatType.DOUBLE_THREAT_4;
			break;
		case 5:
			threatType = ThreatType.DOUBLE_THREAT_5;
			break;
		}
		
		StonePack stonePack = new StonePack(direction, data, color);
		
		int availableSpace = 0;
		
		int newX = position[0] - vector[0];
		int newY = position[1] - vector[1];
		
		int beforeSpace = 0;
		
		while (newX != -1 && newY != -1 && newX != data.length && newY != data.length && data[newX][newY] != -color) {
			newX -= vector[0];
			newY -= vector[1];
			beforeSpace++;
			if (beforeSpace == 4) {
				break;
			}
		}
		
		newX = position[0] + vector[0];
		newY = position[1] + vector[1];
	
		int afterSpace = 0;
	
		while (newX != -1 && newY != -1 && newX != data.length && newY != data.length && data[newX][newY] != -color) {
			newX += vector[0];
			newY += vector[1];
			afterSpace++;
			if (afterSpace == 4) {
				break;
			}
		}
		
		availableSpace = beforeSpace + afterSpace + 1;
		
		if (availableSpace < 6) {
			return threats;
		}
		
		if (beforeSpace == 0 || afterSpace == 0) {
			return threats;
		}
		
		int currentStoneNumber = 0;
		int currentBlankNumber = 0;
		
		for (int k = -beforeSpace; k < afterSpace + 1; k++) {
			if (data[position[0] + k * vector[0]][position[1] + k * vector[1]] == color) {
				currentStoneNumber++;
				int[] newStone = new int[2];
				newStone[0] = position[0] + k * vector[0];
				newStone[1] = position[1] + k * vector[1];
				stonePack.getStoneList().add(newStone);
				stonePack.updateSides();
			}
			
			if (currentStoneNumber > 0) {
				if (data[position[0] + k * vector[0]][position[1] + k * vector[1]] == GomokuModel.UNPLAYED) {
					currentBlankNumber++;
					int[] counterMove = new int[2];
					counterMove[0] = position[0] + k * vector[0];
					counterMove[1] = position[1] + k * vector[1];
				}
			}
			
			if (currentBlankNumber > 5 - stoneNumberGoal) {
				break;
			}
		}
		
		if (currentStoneNumber == stoneNumberGoal - 1 && stonePack.getClearBefore() > 0 && stonePack.getClearAfter() > 0) {
			
			List<int[]> threatMoves = stonePack.computeDoubleAttackMoves();
			
			for (int[] threatMove : threatMoves) {
				Threat threat = new Threat(stonePack, threatType, threatMove[0], threatMove[1], direction, color);
				List<int[]> killingMoves = stonePack.getDoubleThreatKillingMoves();
				if (threat.getThreatType() != ThreatType.DOUBLE_THREAT_5) {
					threat.getKillingMoves().addAll(killingMoves);
					if (threat.getThreatType() != ThreatType.DOUBLE_THREAT_4) {
						List<int[]> counterMoves = stonePack.getDoubleThreatCounterMoves(threatMove);
						threat.getCounterMoves().addAll(counterMoves);
					}
				}
				List<int[]> attackMoves = stonePack.getDoubleThreatNextAttackMoves(threatMove);
				threat.getNextAttackMoves().addAll(attackMoves);
				threats.add(threat);
			}
		}
		
		return threats;
	}

	private int evaluateThreat(Threat threat, int depth) throws Exception {
		
		int potential = 0;
		
		int threatColor = threat.getColor();
		
		if (threatColor == playingColor) {
			potential += threat.getPotentialAfterBeingCountered();
		}
		
		if (depth == 0) {
			
			int[] threatMove = new int[2];
			
			threatMove[0] = threat.getX();
			threatMove[1] = threat.getY();
			
			data[threatMove[0]][threatMove[1]] = threatColor;
			
			int minPotential = Integer.MAX_VALUE;
			
			for (int[] counterMove : threat.getCounterMoves()) {
				if (counterMove[0] != threatMove[0] || counterMove[1] != threatMove[1]) {
					
					int currentPotential = 0;
					data[counterMove[0]][counterMove[1]] = -threatColor;
					
					ArrayList<Threat> newThreats = findThreats(threatColor, threatMove);
					
					int maxNewPotential = 0;
					
					for (Threat newThreat : newThreats) {
						if (newThreat.getPotentialAfterBeingCountered() <= threat.getPotentialAfterBeingCountered() && newThreat.getPotentialAfterBeingCountered() > maxNewPotential) {
							maxNewPotential = newThreat.getPotentialAfterBeingCountered();
						}
					}
					
					currentPotential += maxNewPotential;
					
//					int maxOpponentPotential = 0;
//
//					ArrayList<Threat> newOpponentThreats = findThreats(-threatColor, counterMove);
//					
//					for (Threat opponentThreat : newOpponentThreats) {
//						if (opponentThreat.getPotential() > maxOpponentPotential) {
//							maxOpponentPotential = opponentThreat.getPotential();
//						}
//					}
//					
//					currentPotential -= maxOpponentPotential;
					
					data[counterMove[0]][counterMove[1]] = GomokuModel.UNPLAYED;
					
					if (currentPotential < minPotential) {
						minPotential = currentPotential;
					}
				}
			}
			data[threatMove[0]][threatMove[1]] = GomokuModel.UNPLAYED;
			
			if (minPotential == Integer.MAX_VALUE) {
				minPotential = 0;
			}
			
			potential += minPotential;
		} else {
			potential += threat.getPotentialAfterBeingCountered() / (depth + 1);
		}
		
		return potential;
	}

//	private void spreadThreat(int direction, int[] threat, ThreatType threatType, int[] spreadVector) {
//		
//		int k = 1;
//		
//		int spreadX = threat[0] + spreadVector[0];
//		int spreadY = threat[1] + spreadVector[1];
//		
//		while (k < 5 && spreadX >= 0 && spreadX < data.length && spreadY >= 0 && spreadY < data[0].length) {
//			if (data[spreadX][spreadY] == -playingColor) {
//				break;
//			}
//			
//			if (spreadX + spreadVector[0] >= 0 && spreadX + spreadVector[0] < data.length && spreadY + spreadVector[1] >= 0 && spreadY + spreadVector[1] < data[0].length) {
//				
//				if (data[spreadX + spreadVector[0]][spreadY + spreadVector[1]] == GomokuModel.UNPLAYED) {
//					if (data[spreadX][spreadY] == GomokuModel.UNPLAYED) {
////						stoneMap.get(spreadX).get(spreadY).addThreatTypePotential(direction, threatType, 1);
//					}
//				}
//			}
//			
//			k++;
//			spreadX += spreadVector[0];
//			spreadY += spreadVector[1];
//		}
//	}

}
