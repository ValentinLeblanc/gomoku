package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GomokuModelController {

	private GomokuModel model;
	private PropertyChangeListener modelListener;
	private MoveData lastMove;

	public GomokuModelController(GomokuModel model) {
		this.model = model;
		model.addPropertyChangeListener(getModelListener());
	}

	private PropertyChangeListener getModelListener() {
		if (modelListener == null) {
			modelListener = new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals(GomokuModel.MOVE_REQUEST)) {
						MoveData moveData = (MoveData) evt.getNewValue();
						handleMoveRequest(moveData);
					} else if (evt.getPropertyName().equals(GomokuModel.RESET_REQUEST)) {
						handleResetRequest();
					} else if (evt.getPropertyName().equals(GomokuModel.UNDO_REQUEST)) {
						handleUndoRequest();
					} else if (evt.getPropertyName().equals(GomokuModel.REDO_REQUEST)) {
						handleRedoRequest();
					}
				}
			};
		}
		return modelListener;
	}

	public GomokuModel getModel() {
		return model;
	}

	private void handleMoveRequest(MoveData moveData) {
		if (model.getValue(moveData.getColumnIndex(), moveData.getRowIndex()) == GomokuModel.UNPLAYED) {
			model.setValue(moveData.getColumnIndex(), moveData.getRowIndex(), moveData.getValue());
			model.firePropertyChange(GomokuModel.MOVE_UPDATE, moveData);
			
			moveData.setPreviousMove(lastMove);
			
			if (lastMove != null) {
				lastMove.setNextMove(moveData);
			}
			
			lastMove = moveData;
			
			int[][] winResult = checkForWin(moveData.getValue());
			if (winResult != null) {
				model.firePropertyChange(GomokuModel.WIN_UPDATE, winResult);
			}
		}
	}
	
	private void handleResetRequest() {
		for (int i = 0; i < model.getRowCount(); i++) {
			for (int j = 0; j < model.getColumnCount(); j++) {
				model.setValue(j, i, GomokuModel.UNPLAYED);
			}
		}
		model.firePropertyChange(GomokuModel.BOARD_RESET);
	}
	
	private void handleUndoRequest() {
		if (lastMove != null) {
			model.setValue(lastMove.getColumnIndex(), lastMove.getRowIndex(), GomokuModel.UNPLAYED);
			
			MoveData newMove = new MoveData(lastMove);
			newMove.setValue(GomokuModel.UNPLAYED);
			lastMove = lastMove.getPreviousMove();
			model.firePropertyChange(GomokuModel.MOVE_UPDATE, newMove);
		}
	}
	 
	private void handleRedoRequest() {
		if (lastMove != null && lastMove.getNextMove() != null) {
   			model.setValue(lastMove.getNextMove().getColumnIndex(), lastMove.getNextMove().getRowIndex(), lastMove.getNextMove().getValue());
			model.firePropertyChange(GomokuModel.MOVE_UPDATE, lastMove.getNextMove());
		}
	}

	private int[][] checkForWin(int color) {
		
		int[][] result = new int [2][2];
		int linedUpCount = 1;
		for (int i = 0; i < model.getRowCount(); i++) {
			for (int j = 0; j < model.getColumnCount(); j++) {
				if (model.getValue(j, i) == color) {
					// check south
					int k = 1;
					result[0][0] = j;
					result[0][1] = i;
					while (i + k < model.getRowCount() && model.getValue(j, i + k) == color) {
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
					while (j + k < model.getColumnCount() && model.getValue(j + k, i) == color) {
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
					while (j + k < model.getColumnCount() && i + k < model.getRowCount() && model.getValue(j + k, i + k) == color) {
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
					while (j - k > -1 && i + k < model.getRowCount() && model.getValue(j - k, i + k) == color) {
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
}
