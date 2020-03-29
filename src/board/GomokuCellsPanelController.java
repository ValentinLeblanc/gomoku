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

	private boolean computer;
	private boolean computerTurn = false;
	
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
						if (computer) {
							computerTurn = !computerTurn;
							if (computerTurn) {
								gomokuModel.firePropertyChange(GomokuModel.ENGINE_MOVE_REQUEST, currentPlayingColor);
							}
						}
					} else if (evt.getPropertyName().equals(GomokuModel.WIN_UPDATE)) {
						GomokuCellsPanelController.this.winData = (int[][]) evt.getNewValue();
						handleWinUpdate();
					} else if (evt.getPropertyName().equals(GomokuModel.RESET_UPDATE)) {
						handleResetUpdate();
					}
				}
			};

		}
		return modelListener;
	}
	
	private void handleCellPressedEvent(GomokuCell gomokuCell) {
		gomokuModel.firePropertyChange(GomokuModel.MOVE_REQUEST, new MoveData(gomokuCell.getColumnIndex(), gomokuCell.getRowIndex(), currentPlayingColor));
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
	
	public void requestReset() {
		gomokuModel.firePropertyChange(GomokuModel.RESET_REQUEST);
	}
	
	public void requestUndo() {
		gomokuModel.firePropertyChange(GomokuModel.UNDO_REQUEST);
	}
	
	public void requestRedo() {
		gomokuModel.firePropertyChange(GomokuModel.REDO_REQUEST);
	}
	
	public void paintGomokuCell(GomokuCell gomokuCell, Color color) {
		gomokuCell.setCircleColor(color);
		gomokuCell.repaint();
	}
	
	public int[][] getWinData() {
		return winData;
	}
	
	public void setComputer(boolean computer) {
		this.computer= computer; 
	}
	
	public boolean getComputer() {
		return computer; 
	}
	
	public void setComputerTurn(boolean computerTurn) {
		this.computerTurn= computerTurn; 
	}
	
}
