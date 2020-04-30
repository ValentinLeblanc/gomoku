package board;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import model.GomokuModel;
import model.MoveData;

public class GomokuCellsPanelController {

	private GomokuCellsPanel panel;
	private MouseListener cellMouseListener;
	private PropertyChangeListener modelListener;

	private GomokuModel gomokuModel;
	private int[][] winData;

	private int currentPlayingColor = GomokuModel.BLACK;
	private int currentAnalysedColor = currentPlayingColor;

	private boolean humanVscomputer = false;
	private boolean computerVscomputer = false;
	private boolean computerTurn = false;
	private boolean isInterrupted = false;
	private GomokuCell lastMoveCell;
	private GomokuCell lastAnalysedCell;
	private boolean threatEvaluation;
	private int threatEvaluationColor = GomokuModel.BLACK;
	
	public GomokuCellsPanelController(GomokuCellsPanel panel, GomokuModel model) {
		this.panel = panel;
		gomokuModel = model;
		gomokuModel.addPropertyChangeListener(getModelListener());
	}
	
	public MouseListener getCellMouseListener() {
		if (cellMouseListener == null) {
			cellMouseListener = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getSource() instanceof GomokuCell) {
						GomokuCell gomokuCell = (GomokuCell) e.getSource();
						handleCellPressedEvent(gomokuCell);
					}
				}

			};
		}
		return cellMouseListener;
	}
	
	private PropertyChangeListener getModelListener() {
		if (modelListener == null) {
			modelListener = new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {

					if (evt.getPropertyName().equals(GomokuModel.VALUE_UPDATE)) {
						MoveData moveData = (MoveData) evt.getNewValue();
						handleMoveUpdate(moveData);
						if (humanVscomputer) {
							computerTurn = !computerTurn;
							if (computerTurn && !isInterrupted) {
								requestEngineMove();
							}
						} else if (computerVscomputer && winData == null && !isInterrupted) {
							requestEngineMove();
						}
						
						if (threatEvaluation) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							requestEngineThreatEvaluation(threatEvaluationColor);
						}
					} else if (evt.getPropertyName().equals(GomokuModel.WIN_UPDATE)) {
						GomokuCellsPanelController.this.winData = (int[][]) evt.getNewValue();
						handleWinUpdate();
					} else if (evt.getPropertyName().equals(GomokuModel.RESET_UPDATE)) {
						handleResetUpdate();
					} else if (evt.getPropertyName().equals(GomokuModel.ANALYSED_MOVE)) {
						handleAnalysedMoveUpdate((int[]) evt.getNewValue());
					} else if (evt.getPropertyName().equals(GomokuModel.ANALYSED_DONE)) {
						handleAnalysedDoneUpdate((int[]) evt.getNewValue());
					} else if (evt.getPropertyName().equals(GomokuModel.LAST_MOVE)) {
						handleLastMoveUpdate((MoveData) evt.getNewValue());
					} else if (evt.getPropertyName().equals(GomokuModel.ENGINE_THREAT_EVALUATION_UPDATE)) {
						handleThreatEvaluationUpdate((int[][][]) evt.getNewValue());
					}
				}

			};

		}
		return modelListener;
	}
	
	private void handleCellPressedEvent(GomokuCell gomokuCell) {
		if (winData == null && !computerVscomputer && !computerTurn) {
			gomokuModel.firePropertyChange(GomokuModel.MOVE_REQUEST, new MoveData(gomokuCell.getColumnIndex(), gomokuCell.getRowIndex(), currentPlayingColor));
		}
	}
	
	private void handleMoveUpdate(MoveData moveData) {
		GomokuCell gomokuCell = (GomokuCell) panel.getComponent(moveData.getRowIndex() * panel.getColumnCount() + moveData.getColumnIndex());
		
		switch (moveData.getValue()) {
		case GomokuModel.UNPLAYED:
			paintGomokuCell(gomokuCell, null);
			break;
		case GomokuModel.WHITE:
			paintGomokuCell(gomokuCell, GomokuCellsPanel.WHITE_COLOR);
			break;
		case GomokuModel.BLACK:
			paintGomokuCell(gomokuCell, GomokuCellsPanel.BLACK_COLOR);
			break;
		}
		
		currentPlayingColor = -currentPlayingColor;
		currentAnalysedColor = currentPlayingColor;
	}
	
	private void handleWinUpdate() {
		panel.repaint();
	}
	
	private void handleResetUpdate() {
		
		winData = null;
		currentPlayingColor = GomokuModel.BLACK;
		currentAnalysedColor = currentPlayingColor;
		
		for (Component c : panel.getComponents()) {
			if (c instanceof GomokuCell) {
				GomokuCell gomokuCell = (GomokuCell) c;
				paintGomokuCell(gomokuCell, null);
			}
		}
		
		clearAllThreatEvaluation();

		panel.repaint();
	}
	
	private void handleAnalysedMoveUpdate(int[] analysedMove) {
		
		GomokuCell gomokuCell = (GomokuCell) panel.getComponent(analysedMove[1] * panel.getColumnCount() + analysedMove[0]);
		
		Color color = currentAnalysedColor == GomokuModel.BLACK ? Color.BLACK : Color.WHITE;
		gomokuCell.setAnalysedColor(color);
		gomokuCell.setLastAnalysed(true);
		
		if (lastAnalysedCell != null) {
			lastAnalysedCell.setLastAnalysed(false);
		}
		
		lastAnalysedCell = gomokuCell;
		
		currentAnalysedColor = -currentAnalysedColor;
		
		panel.repaint();
	}
	
	private void handleAnalysedDoneUpdate(int[] analysedMove) {
		
		if (analysedMove != null) {
			GomokuCell gomokuCell = (GomokuCell) panel.getComponent(analysedMove[1] * panel.getColumnCount() + analysedMove[0]);
			
			if (gomokuCell.getAnalysedColor() != null) {
				gomokuCell.setAnalysedColor(null);
			}
			
			if (lastAnalysedCell != null) {
				lastAnalysedCell.setLastAnalysed(false);
			}
		}
		
		panel.repaint();
	}
	
	private void handleLastMoveUpdate(MoveData lastMove) {
		
		GomokuCell gomokuCell = (GomokuCell) panel.getComponent(lastMove.getRowIndex() * panel.getColumnCount() + lastMove.getColumnIndex());
		
		if (lastMoveCell != null) {
			lastMoveCell.setLastMove(false);
		}
		
		lastMoveCell = gomokuCell;
		
		if (lastMove.getValue() != GomokuModel.UNPLAYED) {
			lastMoveCell.setLastMove(true);
		}
		
		panel.repaint();
	}
	
	private void handleThreatEvaluationUpdate(int[][][] threatTable) {
		
		for (int i = 0; i < threatTable[0].length; i++) {
			for (int j = 0; j < threatTable[0].length; j++) {
				GomokuCell gomokuCell = (GomokuCell) panel.getComponent(i * panel.getColumnCount() + j);
				
				if (threatTable[0][j][i] != 0) {
					gomokuCell.setBlackThreatEvaluation(threatTable[0][j][i]);
					gomokuCell.repaint();
				} 
				
				if (threatTable[1][j][i] != 0) {
					gomokuCell.setWhiteThreatEvaluation(threatTable[1][j][i]);
					gomokuCell.repaint();
				}
			}
		}
		
		panel.repaint();
	}
	
	public void requestEngineMove() {
		gomokuModel.firePropertyChange(GomokuModel.ENGINE_MOVE_REQUEST, currentPlayingColor);
	}
	
	public void requestEngineStop() {
		gomokuModel.firePropertyChange(GomokuModel.ENGINE_STOP_REQUEST);
		for (int i = 0; i < gomokuModel.getData()[0].length; i++) {
			for (int j = 0; j < gomokuModel.getData().length; j++) {
				int[] move = new int[2];
				move[0] = j;
				move[1] = i;
				handleAnalysedDoneUpdate(move);
			}
		}
		
		if (lastAnalysedCell != null) {
			lastAnalysedCell.setLastAnalysed(false);
			lastAnalysedCell = null;
		}
		
	}
	
	private void requestEngineThreatEvaluation(int playingColor) {
		clearAllThreatEvaluation();
		gomokuModel.firePropertyChange(GomokuModel.ENGINE_THREAT_EVALUATION_REQUEST, playingColor);
	}

	private void clearAllThreatEvaluation() {
		for (int i = 0; i < gomokuModel.getData()[0].length; i++) {
			for (int j = 0; j < gomokuModel.getData().length; j++) {
				GomokuCell gomokuCell = (GomokuCell) panel.getComponent(i * panel.getColumnCount() + j);
				gomokuCell.setBlackThreatEvaluation(0);
				gomokuCell.setWhiteThreatEvaluation(0);
				gomokuCell.repaint();
			}
		}
	}

	public void paintGomokuCell(GomokuCell gomokuCell, Color color) {
		gomokuCell.setCircleColor(color);
		gomokuCell.repaint();
	}
	
	public int[][] getWinData() {
		return winData;
	}
	
	public void setComputerVsComputer(boolean computerVscomputer) {
		this.computerVscomputer= computerVscomputer; 
	}
	
	public void setHumanVsComputer(boolean humanVscomputer) {
		this.humanVscomputer= humanVscomputer; 
	}
	
	public void setComputerTurn(boolean computerTurn) {
		this.computerTurn= computerTurn; 
	}
	
	public boolean isComputerVsComputer() {
		return computerVscomputer;
	}
	
	public boolean isHumanVsComputer() {
		return humanVscomputer; 
	}
	
	public boolean isComputerTurn() {
		return computerTurn; 
	}
	

	public void startNewGame() {
		if (humanVscomputer && computerTurn || computerVscomputer) {
			requestEngineMove();
		}
	}

	public boolean isUndoing() {
		return isInterrupted;
	}

	public void setInterruptComputation(boolean isUndoing) {
		this.isInterrupted = isUndoing;
	}

	public void setDisplayAnalysis(boolean display) {
		for (int i = 0; i < gomokuModel.getData()[0].length; i++) {
			for (int j = 0; j < gomokuModel.getData().length; j++) {
				int[] cell = new int[2];
				cell[0] = j;
				cell[1] = i;
				
				GomokuCell gomokuCell = (GomokuCell) panel.getComponent(cell[1] * panel.getColumnCount() + cell[0]);
				
				gomokuCell.setDisplayAnalysis(display);
			}
		}
	}

	public void setThreatEvaluation(boolean threatEvaluation) {
		
		this.threatEvaluation = threatEvaluation; 
		
		if (threatEvaluation) {
			requestEngineThreatEvaluation(threatEvaluationColor);
		} else {
			clearAllThreatEvaluation();
		}
		
		panel.repaint();
		
	}

	public void setThreatEvaluationColor(int threatEvaluationColor) {
		this.threatEvaluationColor = threatEvaluationColor ;
		requestEngineThreatEvaluation(threatEvaluationColor);
	}
	
}
