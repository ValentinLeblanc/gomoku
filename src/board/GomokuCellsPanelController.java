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

	public GomokuCellsPanelController(GomokuCellsPanel panel) {
		this.panel = panel;
		gomokuModel = new GomokuModel(panel.getColumnCount(), panel.getRowCount());
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
					if (evt.getPropertyName().equals(GomokuModel.MOVE_UPDATE)) {
						MoveData moveData = (MoveData) evt.getNewValue();
						handleMoveUpdate(moveData);
					} else if (evt.getPropertyName().equals(GomokuModel.WIN_UPDATE)) {
						GomokuCellsPanelController.this.winData = (int[][]) evt.getNewValue();
						handleWinUpdate();
					} else if (evt.getPropertyName().equals(GomokuModel.BOARD_RESET)) {
						handleBoardReset();
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
		case 0:
			paintGomokuCell(gomokuCell, null);
			break;
		case -1:
			paintGomokuCell(gomokuCell, Color.WHITE);
			break;
		case 1:
			paintGomokuCell(gomokuCell, Color.BLACK);
			break;
		}
		
		currentPlayingColor = currentPlayingColor == GomokuModel.BLACK ? GomokuModel.WHITE : GomokuModel.BLACK;
	}
	
	private void handleWinUpdate() {
		panel.repaint();
	}
	
	private void handleBoardReset() {
		
		GomokuCellsPanelController.this.winData = null;
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
}
