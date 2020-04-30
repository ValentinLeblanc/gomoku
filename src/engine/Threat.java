package engine;

import java.util.ArrayList;
import java.util.List;

public class Threat {

	private ThreatType threatType;

	private int x;
	
	private int y;

	private ArrayList<int[]> counterMoves;
	private ArrayList<int[]> killingMoves;
	private ArrayList<int[]> nextAttackMoves;

	private int direction;
	
	private int color;
	
	private StonePack stonePack;
	
	public Threat(StonePack stonePack, ThreatType threatType, int x, int y, int direction, int color) {
		this.stonePack = stonePack;
		this.threatType = threatType;
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.color = color;
	}
	
	public ThreatType getThreatType() {
		return threatType;
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getPriority() {
		switch (threatType) {
		case SINGLE_THREAT_5:
			return 0;
		case DOUBLE_THREAT_4:
			return 1;
		case SINGLE_THREAT_4:
			return 2;
		case DOUBLE_THREAT_3:
			return 3;
		case DOUBLE_THREAT_2:
			return 4;
		default:
			return 0;
		}
	}
	
	public int getPotentialAfterBeingCountered() {
		switch (threatType) {
		case SINGLE_THREAT_5:
			return 10000;
		case DOUBLE_THREAT_4:
			return 4;
		case SINGLE_THREAT_4:
			return 4;
		case DOUBLE_THREAT_3:
			return 3;
		case DOUBLE_THREAT_2:
			return 2;
		default:
			return 0;
		}
	}
	
	public int getPotentialAfterNotBeingCountered() {
		switch (threatType) {
		case SINGLE_THREAT_5:
			return 10000;
		case DOUBLE_THREAT_4:
			return 10000;
		case SINGLE_THREAT_4:
			return 10000;
		case DOUBLE_THREAT_3:
			return 10000;
		case DOUBLE_THREAT_2:
			return 100;
		default:
			return 0;
		}
	}

	public List<int[]> getCounterMoves() {
		if (counterMoves == null) {
			counterMoves = new ArrayList<int[]>();
		}
		return counterMoves;
	}
	
	public List<int[]> getKillingMoves() {
		if (killingMoves == null) {
			killingMoves = new ArrayList<int[]>();
		}
		return killingMoves;
	}
	
	public List<int[]> getNextAttackMoves() {
		if (nextAttackMoves == null) {
			nextAttackMoves = new ArrayList<int[]>();
		}
		return nextAttackMoves;
	}

	public int getDirection() {
		return direction;
	}

	public int getColor() {
		return color;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		Threat threat = (Threat) obj;
		
		if (threatType != threat.threatType) {
			return false;
		}
		
		if (x != threat.x) {
			return false;
		}
		
		if (y != threat.y) {
			return false;
		}
		
		if (direction != threat.direction) {
			return false;
		}
		
		if (counterMoves == null) {
			return threat.counterMoves == null;
		}
		
		if (threat.counterMoves == null) {
			return false;
		}
		
		for (int[] counterMove : counterMoves) {
			boolean isInside = true;
			for (int[] otherCounterMove : threat.counterMoves) {
				if (otherCounterMove[0] == counterMove[0] && otherCounterMove[1] == counterMove[1]) {
					isInside = true;
					break;
				}
			}
			
			if (!isInside) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return threatType + " - [" + getX() + "][" + getY() + "]";
	}

	public StonePack getStonePack() {
		return stonePack;
	}
}
