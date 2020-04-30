package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Stone {

	private int x;
	private int y;
	
	private int potential = 0;
	
	private List<StoneGroup> stoneGroups;
	private int threatEvaluation;
	
	private PotentialMap singleThreat5Potential = new PotentialMap();
	private PotentialMap singleThreat4Potential = new PotentialMap();
	private PotentialMap singleThreat3Potential = new PotentialMap();
	private PotentialMap singleThreat2Potential = new PotentialMap();
	
	private PotentialMap doubleThreat4Potential = new PotentialMap();
	private PotentialMap doubleThreat3Potential = new PotentialMap();
	private PotentialMap doubleThreat2Potential = new PotentialMap();
	
	private PotentialMap singleThreat5SpreadPotential = new PotentialMap();
	private PotentialMap singleThreat4SpreadPotential = new PotentialMap();
	private PotentialMap singleThreat3SpreadPotential = new PotentialMap();
	private PotentialMap singleThreat2SpreadPotential = new PotentialMap();

	private PotentialMap doubleThreat5SpreadPotential = new PotentialMap();
	private PotentialMap doubleThreat4SpreadPotential = new PotentialMap();
	private PotentialMap doubleThreat3SpreadPotential = new PotentialMap();
	private PotentialMap doubleThreat2SpreadPotential = new PotentialMap();
	
	public Stone(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public int getPotential() {
		return potential;
	}

	public List<StoneGroup> getStoneGroups() {
		if (stoneGroups == null) {
			stoneGroups = new ArrayList<StoneGroup>();
		}
		return stoneGroups;
	}

	public void incrementPotential(int potentialIncrement) {
		potential += potentialIncrement;
	}

	public void setThreatEvaluation(int threatType) {
		this.threatEvaluation = threatType;
	}

	public int getThreatEvaluation() {
		return threatEvaluation;
	}
	
	private int getThreatTypeEvaluation(ThreatType threatType) {
		switch(threatType) {
		
		case SINGLE_THREAT_5 :
			return StoneData.SINGLE_THREAT_5_EVALUATION;
		case SINGLE_THREAT_4 :
			return StoneData.SINGLE_THREAT_4_EVALUATION;
		case SINGLE_THREAT_3 :
			return StoneData.SINGLE_THREAT_3_EVALUATION;
		case SINGLE_THREAT_2 :
			return StoneData.SINGLE_THREAT_2_EVALUATION;
		case DOUBLE_THREAT_4 :
			return StoneData.DOUBLE_THREAT_4_EVALUATION;
		case DOUBLE_THREAT_3 :
			return StoneData.DOUBLE_THREAT_3_EVALUATION;
		case DOUBLE_THREAT_2 :
			return StoneData.DOUBLE_THREAT_2_EVALUATION;
//		case SINGLE_SPREAD_THREAT_5 :
//			return StoneData.SINGLE_THREAT_4_EVALUATION;
//		case SINGLE_SPREAD_THREAT_4 :
//			return StoneData.SINGLE_THREAT_3_EVALUATION;
//		case SINGLE_SPREAD_THREAT_3 :
//			return StoneData.SINGLE_THREAT_2_EVALUATION;
//		case SINGLE_SPREAD_THREAT_2 :
//			return 1;
//			
//		case DOUBLE_SPREAD_THREAT_5 :
//			return StoneData.DOUBLE_THREAT_4_EVALUATION;
//		case DOUBLE_SPREAD_THREAT_4 :
//			return StoneData.DOUBLE_THREAT_3_EVALUATION;
//		case DOUBLE_SPREAD_THREAT_3 :
//			return StoneData.DOUBLE_THREAT_2_EVALUATION;
//		case DOUBLE_SPREAD_THREAT_2 :
//			return 1;
		default:
			break;
		}
		
		return 0;
	}
	
	private int getThreatTypeHigherEvaluation(ThreatType threatType) {
		switch(threatType) {
		
		case SINGLE_THREAT_4 :
			return StoneData.DOUBLE_THREAT_4_EVALUATION;
		case DOUBLE_THREAT_3 :
			return StoneData.DOUBLE_THREAT_4_EVALUATION;
		case DOUBLE_THREAT_2 :
			return StoneData.DOUBLE_THREAT_3_EVALUATION;
		default:
			return 0;
		}
		
	}
	
	private int getThreatCombinationEvaluation(ThreatType threatType1, ThreatType threatType2) {
		
		int result = 0;
		
		ThreatType weakerThreat = getThreatTypeEvaluation(threatType1) < getThreatTypeEvaluation(threatType2) ? threatType1 : threatType2;
		
		if (threatType1 == ThreatType.SINGLE_THREAT_4) {
			if (threatType2 == ThreatType.SINGLE_THREAT_4) {
				int number = 0;
				for (int potential : singleThreat4Potential.values()) {
					number += potential;
				}
				
				if (number > 1) {
					result += getThreatTypeEvaluation(ThreatType.DOUBLE_THREAT_4);
				}
			} else {
				for (int direction : getPotentialMap(threatType1).keySet()) {
					for (int otherDirection : getPotentialMap(threatType2).keySet()) {
						if (direction != otherDirection) {
							result += getThreatTypeEvaluation(threatType2);
						}
					}
				}
			}
		} else {
			for (int direction : getPotentialMap(threatType1).keySet()) {
				for (int otherDirection : getPotentialMap(threatType2).keySet()) {
					if (direction != otherDirection) {
						result += getThreatTypeEvaluation(weakerThreat);
					}
				}
			}
		}
		
		
		return result;
	}
	
	private PotentialMap getPotentialMap(ThreatType threatType) {
		
		switch(threatType) {
		
		case SINGLE_THREAT_5 :
			return singleThreat5Potential;
		case SINGLE_THREAT_4 :
			return singleThreat4Potential;
		case SINGLE_THREAT_3 :
			return singleThreat3Potential;
		case SINGLE_THREAT_2 :
			return singleThreat2Potential;
		case DOUBLE_THREAT_4 :
			return doubleThreat4Potential;
		case DOUBLE_THREAT_3 :
			return doubleThreat3Potential;
		case DOUBLE_THREAT_2 :
			return doubleThreat2Potential;
			
	}
		
		return null;
	}
	
	private PotentialMap getSpreadPotentialMap(ThreatType threatType) {
		
		switch(threatType) {
		
		case SINGLE_THREAT_5 :
			return singleThreat5SpreadPotential;
		case SINGLE_THREAT_4 :
			return singleThreat4SpreadPotential;
		case SINGLE_THREAT_3 :
			return singleThreat3SpreadPotential;
		case SINGLE_THREAT_2 :
			return singleThreat2SpreadPotential;
		case DOUBLE_THREAT_4 :
			return doubleThreat4SpreadPotential;
		case DOUBLE_THREAT_3 :
			return doubleThreat3SpreadPotential;
		case DOUBLE_THREAT_2 :
			return doubleThreat2SpreadPotential;
		default:
			break;
		}
		
		return null;
	}

	public void addThreatTypePotential(int direction, ThreatType threatType, int depth) {
		
		if (depth == 0) {
			int potential = getPotentialMap(threatType).get(direction) == null ? 0 : getPotentialMap(threatType).get(direction);
			potential++;
			getPotentialMap(threatType).put(direction, potential);
		} else if (depth == 1) {
			int potential = getSpreadPotentialMap(threatType).get(direction) == null ? 0 : getSpreadPotentialMap(threatType).get(direction);
			potential++;
			getSpreadPotentialMap(threatType).put(direction, potential);
		}
	}

	public void computePotential() {
		
		potential = 0;
		
		for (ThreatType threatType : ThreatType.values()) {
			for (int direction : getPotentialMap(threatType).keySet()) {
				potential += getPotentialMap(threatType).get(direction) * getThreatTypeEvaluation(threatType);
			}
			
			for (ThreatType otherThreatType : ThreatType.values()) {
				potential += getThreatCombinationEvaluation(threatType, otherThreatType);
			}
		}
	}
	
	private class PotentialMap extends HashMap<Integer, Integer> {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public PotentialMap() {
			super();
		}
		
	}
	
}
