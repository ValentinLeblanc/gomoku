package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import engine.GomokuEngine;

public class GomokuModelController {

	private GomokuModel model;
	private PropertyChangeListener modelListener;
	private MoveData lastMove;
	
	private GomokuEngine engine;
	
	private Thread engineThread;

	public GomokuModelController(GomokuModel model) {
		this.model = model;
		model.addPropertyChangeListener(getModelListener());
		engine = new GomokuEngine(model);
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
					} else if (evt.getPropertyName().equals(GomokuModel.VALUE_UPDATE)) {
						try {
							updateEvaluation();
						} catch (Exception e) {
							e.printStackTrace();
						}
						MoveData moveData = (MoveData) evt.getNewValue();
						if (moveData.getValue() != GomokuModel.UNPLAYED) {
							int[][] winResult = engine.checkForWin(model.getData(), moveData.getValue());
							if (winResult != null) {
								model.firePropertyChange(GomokuModel.WIN_UPDATE, winResult);
							}
						} else {
							int[][] winResult = engine.checkForWin(model.getData(), GomokuModel.BLACK);
							if (winResult != null) {
								model.firePropertyChange(GomokuModel.WIN_UPDATE, winResult);
							} else {
								winResult = engine.checkForWin(model.getData(), GomokuModel.WHITE);
								if (winResult != null) {
									model.firePropertyChange(GomokuModel.WIN_UPDATE, winResult);
								} else {
									model.firePropertyChange(GomokuModel.WIN_UPDATE, null);
								}
							}
						}
					} else if (evt.getPropertyName().equals(GomokuModel.ENGINE_MOVE_REQUEST)) {
						handleEngineMoveRequest((int) evt.getNewValue());
					} else if (evt.getPropertyName().equals(GomokuModel.ENGINE_STOP_REQUEST)) {
						handleEngineStopRequest();
					} else if (evt.getPropertyName().equals(GomokuModel.ENGINE_THREAT_EVALUATION_REQUEST)) {
						handleEngineThreatEvaluationRequest((int) evt.getNewValue());
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
			
			moveData.setPreviousMove(lastMove);
			
			if (lastMove != null) {
				lastMove.setNextMove(moveData);
			}
			
			lastMove = moveData;
		}
	}
	
	private void handleInterruption() {
		model.firePropertyChange(GomokuModel.INTERRUPTED);
	}
	
	private void handleResetRequest() {
		for (int i = 0; i < model.getRowCount(); i++) {
			for (int j = 0; j < model.getColumnCount(); j++) {
				model.getData()[j][i] = GomokuModel.UNPLAYED;
			}
		}
		
		lastMove = null;
		
		getModel().setBlackEvaluation(0);
		model.firePropertyChange(GomokuModel.BLACK_EVALUATION_UPDATE);
		getModel().setWhiteEvaluation(0);
		model.firePropertyChange(GomokuModel.WHITE_EVALUATION_UPDATE);
		
		model.firePropertyChange(GomokuModel.RESET_UPDATE);
	}

	private void handleUndoRequest() {
		if (lastMove != null && lastMove.getPreviousMove() != null) {
			model.setValue(lastMove.getColumnIndex(), lastMove.getRowIndex(), GomokuModel.UNPLAYED);
			lastMove = lastMove.getPreviousMove();
		}
	}
	 
	private void handleRedoRequest() {
		if (lastMove != null && lastMove.getNextMove() != null) {
   			model.setValue(lastMove.getNextMove().getColumnIndex(), lastMove.getNextMove().getRowIndex(), lastMove.getNextMove().getValue());
			lastMove = lastMove.getNextMove();
		}
	}
	
	private void handleEngineMoveRequest(int playingColor) {
		engineThread = new Thread() {
			public void run() {
				int[] engineMove;
				try {
					engineMove = engine.computeMove(model.getData(), playingColor);
					MoveData newMove = new MoveData(engineMove[0], engineMove[1], playingColor);
					handleMoveRequest(newMove);
				} catch (Exception e) {
					System.err.println("INTERRUPTED");
					handleInterruption();
				}
				
			}
		};
		engineThread.start();
	}
	
	private void handleEngineStopRequest() {
		engineThread.interrupt();
	}
	
	private void handleEngineThreatEvaluationRequest(int playingColor) {
		engineThread = new Thread() {
			public void run() {
				try {
					engine.computeThreatEvaluation(model.getData(), playingColor);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		};
		engineThread.start();
	}

	private void updateEvaluation() throws Exception {
		getModel().setBlackEvaluation(engine.computeEvaluation(model.getData(), GomokuModel.BLACK));
		getModel().firePropertyChange(GomokuModel.BLACK_EVALUATION_UPDATE);
		getModel().setWhiteEvaluation(engine.computeEvaluation(model.getData(), GomokuModel.WHITE));
		getModel().firePropertyChange(GomokuModel.WHITE_EVALUATION_UPDATE);
	}
	
	public int[] getLastMove() {
		int[] lastMove = new int[2];
		lastMove[0] = this.lastMove.getColumnIndex();
		lastMove[1] = this.lastMove.getRowIndex();
		
		return lastMove;
	}

}
