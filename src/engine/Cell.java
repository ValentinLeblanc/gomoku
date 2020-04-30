package engine;

import java.util.ArrayList;
import java.util.List;

import model.GomokuModel;

public class Cell {

	private int x;
	private int y;
	private int value = GomokuModel.UNPLAYED;
	
	private int singleThreat5Potential = 0;
	private int singleThreat4Potential = 0;
	private int singleThreat3Potential = 0;
	private int singleThreat2Potential = 0;
	
	private int doubleThreat5Potential = 0;
	private int doubleThreat4Potential = 0;
	private int doubleThreat3Potential = 0;
	private int doubleThreat2Potential = 0;
	
	private List<CellGroup> singleThreat4Group;
	
	private List<CellGroup> cellGroups;
	
	public Cell(int x, int y) {
		setX(x);
		setY(y);
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

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public List<CellGroup> getCellGroups() {
		if (cellGroups == null) {
			cellGroups = new ArrayList<CellGroup>();
		}
		return cellGroups;
	}

	public int getSingleThreat5Potential() {
		return singleThreat5Potential;
	}

	public void setSingleThreat5Potential(int singleThreat5Potential) {
		this.singleThreat5Potential = singleThreat5Potential;
	}

	public int getDoubleThreat4Potential() {
		return doubleThreat4Potential;
	}

	public void setDoubleThreat4Potential(int doubleThreat4Potential) {
		this.doubleThreat4Potential = doubleThreat4Potential;
	}

	public int getDoubleThreat3Potential() {
		return doubleThreat3Potential;
	}

	public void setDoubleThreat3Potential(int doubleThreat3Potential) {
		this.doubleThreat3Potential = doubleThreat3Potential;
	}

	public int getDoubleThreat2Potential() {
		return doubleThreat2Potential;
	}

	public void setDoubleThreat2Potential(int doubleThreat2Potential) {
		this.doubleThreat2Potential = doubleThreat2Potential;
	}

	public int getSingleThreat4Potential() {
		return singleThreat4Potential;
	}

	public void setSingleThreat4Potential(int singleThreat4Potential) {
		this.singleThreat4Potential = singleThreat4Potential;
	}

	public int getDoubleThreat5Potential() {
		return doubleThreat5Potential;
	}

	public void setDoubleThreat5Potential(int doubleThreat5Potential) {
		this.doubleThreat5Potential = doubleThreat5Potential;
	}

	public int getSingleThreat3Potential() {
		return singleThreat3Potential;
	}

	public void setSingleThreat3Potential(int singleThreat3Potential) {
		this.singleThreat3Potential = singleThreat3Potential;
	}

	public int getSingleThreat2Potential() {
		return singleThreat2Potential;
	}

	public void setSingleThreat2Potential(int singleThreat2Potential) {
		this.singleThreat2Potential = singleThreat2Potential;
	}

	public void addSingleThreat4Group(CellGroup cellGroup) {
		if (singleThreat4Group == null) {
			singleThreat4Group = new ArrayList<CellGroup>();
		}
		
		singleThreat4Group.add(cellGroup);
	}

	public boolean areSingle4ThreatsCompatible() {
		
		CellGroup firstGroup = singleThreat4Group.get(0);
		CellGroup secondGroup = singleThreat4Group.get(1);
		
		for (int[] firstThreat : firstGroup.getSingleThreatMoves(3)) {
			boolean isPresent = false;
			
			for (int[] secondThreat : secondGroup.getSingleThreatMoves(3)) {
				if (firstThreat[0] == secondThreat[0] && firstThreat[1] == secondThreat[1]) {
					isPresent = true;
					break;
				}
			}
			
			if (!isPresent) {
				return true;
			}
		}
		
		return false;
	}

}
