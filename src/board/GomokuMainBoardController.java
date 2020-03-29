package board;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import model.GomokuModel;

public class GomokuMainBoardController {

	private GomokuMainBoard gomokuBoard;
	private ActionListener actionListener;
	private PropertyChangeListener propertyChangeListener;
	
	public GomokuMainBoardController(GomokuMainBoard gomokuBoard) {
		this.gomokuBoard = gomokuBoard;
		gomokuBoard.getResetButton().addActionListener(getActionListener());
		gomokuBoard.getUndoButton().addActionListener(getActionListener());
		gomokuBoard.getRedoButton().addActionListener(getActionListener());
		
		gomokuBoard.getModel().addPropertyChangeListener(getPropetyChangeListener());
	}
	
	public PropertyChangeListener getPropetyChangeListener() {
		if (propertyChangeListener == null) {
			propertyChangeListener = new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals(GomokuModel.BLACK_EVALUATION_UPDATE)) {
						gomokuBoard.getBlackEvaluationLabel().setText("" + gomokuBoard.getModel().getBlackEvaluation());
						gomokuBoard.getGlobalEvaluationLabel().setText("" + (gomokuBoard.getModel().getBlackEvaluation() - gomokuBoard.getModel().getWhiteEvaluation()));
					} else if (evt.getPropertyName().equals(GomokuModel.WHITE_EVALUATION_UPDATE)) {
						gomokuBoard.getWhiteEvaluationLabel().setText("" + gomokuBoard.getModel().getWhiteEvaluation());
						gomokuBoard.getGlobalEvaluationLabel().setText("" + (gomokuBoard.getModel().getBlackEvaluation() - gomokuBoard.getModel().getWhiteEvaluation()));
					}
				}
			};
		}
		
		
		return propertyChangeListener;
	}
	
	public ActionListener getActionListener() {
		if (actionListener == null) {
			actionListener = new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == gomokuBoard.getResetButton()) {
						gomokuBoard.getGomokuCellsPanel().getController().requestReset();
					} else  if (e.getSource() == gomokuBoard.getUndoButton()) {
						gomokuBoard.getGomokuCellsPanel().getController().requestUndo();
					} else  if (e.getSource() == gomokuBoard.getRedoButton()) {
						gomokuBoard.getGomokuCellsPanel().getController().requestRedo();
					}
				}
			};
		}
		return actionListener;
	}

	public GomokuMainBoard getGomokuBoard() {
		return gomokuBoard;
	}

}
