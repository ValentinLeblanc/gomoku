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

	private boolean humanVscomputer = false;
	private boolean computerVscomputer = false;
	private boolean computerTurn = false;
	private boolean isUndoing = false;
	private GomokuCell analyzedCell;
	private GomokuCell secondAnalyzedCell;
	private GomokuCell lastMoveCell;
	
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
							if (computerTurn && !isUndoing) {
								requestEngineMove();
							}
						} else if (computerVscomputer && winData == null && !isUndoing) {
							requestEngineMove();
						}
					} else if (evt.getPropertyName().equals(GomokuModel.WIN_UPDATE)) {
						GomokuCellsPanelController.this.winData = (int[][]) evt.getNewValue();
						handleWinUpdate();
					} else if (evt.getPropertyName().equals(GomokuModel.RESET_UPDATE)) {
						handleResetUpdate();
					} else if (evt.getPropertyName().equals(GomokuModel.ANALYSED_MOVE)) {
						handleAnalysedMoveUpdate((int[]) evt.getNewValue());
					} else if (evt.getPropertyName().equals(GomokuModel.SECOND_ANALYSED_MOVE)) {
						handleSecondAnalysedMoveUpdate((int[]) evt.getNewValue());
					} else if (evt.getPropertyName().equals(GomokuModel.LAST_MOVE)) {
						handleLastMoveUpdate((MoveData) evt.getNewValue());
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
		
		currentPlayingColor = currentPlayingColor == GomokuModel.BLACK ? GomokuModel.WHITE : GomokuModel.BLACK;
	}
	
	private void handleWinUpdate() {
		panel.repaint();
	}
	
	private void handleResetUpdate() {
		
		winData = null;
		currentPlayingColor = GomokuModel.BLACK;

		for (Component c : panel.getComponents()) {
			if (c instanceof GomokuCell) {
				GomokuCell gomokuCell = (GomokuCell) c;
				paintGomokuCell(gomokuCell, null);
			}
		}
		panel.repaint();
	}
	
	private void handleAnalysedMoveUpdate(int[] analysedMove) {
		
		if (analyzedCell != null) {
			analyzedCell.setAnalysed(false);
			analyzedCell.setCircleColor(null);
		}
		
		if (analysedMove != null) {
			GomokuCell gomokuCell = (GomokuCell) panel.getComponent(analysedMove[1] * panel.getColumnCount() + analysedMove[0]);
			
			analyzedCell = gomokuCell;
			
			Color color = currentPlayingColor == GomokuModel.BLACK ? Color.BLACK : Color.WHITE;
			analyzedCell.setCircleColor(color);
			analyzedCell.setAnalysed(true);
		}
		
		
		panel.repaint();
	}
	
	private void handleSecondAnalysedMoveUpdate(int[] secondAnalysedMove) {
		
		if (secondAnalyzedCell != null) {
			secondAnalyzedCell.setSecondAnalysed(false);
			secondAnalyzedCell.setCircleColor(null);
		}
		
		if (secondAnalysedMove != null) {
			GomokuCell gomokuCell = (GomokuCell) panel.getComponent(secondAnalysedMove[1] * panel.getColumnCount() + secondAnalysedMove[0]);
			
			secondAnalyzedCell = gomokuCell;
			
			Color color = currentPlayingColor == GomokuModel.BLACK ? Color.WHITE : Color.BLACK;
			secondAnalyzedCell.setCircleColor(color);
			secondAnalyzedCell.setSecondAnalysed(true);
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
	
	public void requestEngineMove() {
		gomokuModel.firePropertyChange(GomokuModel.ENGINE_MOVE_REQUEST, currentPlayingColor);
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
		return isUndoing;
	}

	public void setUndoing(boolean isUndoing) {
		this.isUndoing = isUndoing;
	}
	
}
