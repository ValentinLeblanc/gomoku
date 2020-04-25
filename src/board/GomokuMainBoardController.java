package board;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import engine.GomokuEngine;
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
		gomokuBoard.getComputeMoveButton().addActionListener(getActionListener());
		gomokuBoard.getStopButton().addActionListener(getActionListener());
		gomokuBoard.getDisplayCheckBox().addActionListener(getActionListener());
		gomokuBoard.getDisplayDisplayThreatEvaluationCheckBox().addActionListener(getActionListener());
		gomokuBoard.getBlackThreatEvaluationButton().addActionListener(getActionListener());
		gomokuBoard.getWhiteThreatEvaluationButton().addActionListener(getActionListener());
		
		gomokuBoard.getModel().addPropertyChangeListener(getPropetyChangeListener());
	}
	
	public PropertyChangeListener getPropetyChangeListener() {
		if (propertyChangeListener == null) {
			propertyChangeListener = new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals(GomokuModel.BLACK_EVALUATION_UPDATE)) {
						gomokuBoard.getBlackEvaluationLabel().setText("" + gomokuBoard.getModel().getBlackEvaluation());
						gomokuBoard.getGlobalEvaluationLabel().setText("" + (gomokuBoard.getModel().getBlackEvaluation() - GomokuEngine.OPPONENT_EVALUATION_FACTOR * gomokuBoard.getModel().getWhiteEvaluation()));
					} else if (evt.getPropertyName().equals(GomokuModel.WHITE_EVALUATION_UPDATE)) {
						gomokuBoard.getWhiteEvaluationLabel().setText("" + gomokuBoard.getModel().getWhiteEvaluation());
						gomokuBoard.getGlobalEvaluationLabel().setText("" + (gomokuBoard.getModel().getBlackEvaluation() - gomokuBoard.getModel().getWhiteEvaluation()));
					} else if (evt.getPropertyName().equals(GomokuModel.VALUE_UPDATE)) {
						if (!gomokuBoard.getGomokuCellsPanel().getController().isComputerVsComputer() || !gomokuBoard.getGomokuCellsPanel().getController().isHumanVsComputer()) {
							gomokuBoard.getResetButton().setEnabled(true);
							gomokuBoard.getUndoButton().setEnabled(true);
							gomokuBoard.getRedoButton().setEnabled(true);
							gomokuBoard.getComputeMoveButton().setEnabled(true);
							gomokuBoard.getStopButton().setEnabled(false);
						}
					} else if (evt.getPropertyName().equals(GomokuModel.ENGINE_MOVE_REQUEST)) {
						gomokuBoard.getResetButton().setEnabled(false);
						gomokuBoard.getUndoButton().setEnabled(false);
						gomokuBoard.getRedoButton().setEnabled(false);
						gomokuBoard.getComputeMoveButton().setEnabled(false);
						gomokuBoard.getStopButton().setEnabled(true);
					} else if (evt.getPropertyName().equals(GomokuModel.INTERRUPTED)) {
						if (!gomokuBoard.getGomokuCellsPanel().getController().isComputerVsComputer() || !gomokuBoard.getGomokuCellsPanel().getController().isHumanVsComputer()) {
							gomokuBoard.getResetButton().setEnabled(true);
							gomokuBoard.getUndoButton().setEnabled(true);
							gomokuBoard.getRedoButton().setEnabled(true);
							gomokuBoard.getComputeMoveButton().setEnabled(true);
							gomokuBoard.getStopButton().setEnabled(false);
						}
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
						gomokuBoard.getModel().firePropertyChange(GomokuModel.RESET_REQUEST);
					} else if (e.getSource() == gomokuBoard.getUndoButton()) {
						gomokuBoard.getGomokuCellsPanel().getController().setInterruptComputation(true);
						gomokuBoard.getModel().firePropertyChange(GomokuModel.UNDO_REQUEST);
					} else if (e.getSource() == gomokuBoard.getRedoButton()) {
						gomokuBoard.getModel().firePropertyChange(GomokuModel.REDO_REQUEST);
					} else if (e.getSource() == gomokuBoard.getComputeMoveButton()) {
						gomokuBoard.getGomokuCellsPanel().getController().setInterruptComputation(false);
						gomokuBoard.getGomokuCellsPanel().getController().requestEngineMove();
					} else if (e.getSource() == gomokuBoard.getStopButton()) {
						gomokuBoard.getGomokuCellsPanel().getController().requestEngineStop();
						gomokuBoard.getGomokuCellsPanel().getController().setInterruptComputation(true);
					} else if (e.getSource() == gomokuBoard.getDisplayCheckBox()) {
						gomokuBoard.getGomokuCellsPanel().getController().setDisplayAnalysis(gomokuBoard.getDisplayCheckBox().isSelected());
					} else if (e.getSource() == gomokuBoard.getDisplayDisplayThreatEvaluationCheckBox()) {
						boolean selected = gomokuBoard.getDisplayDisplayThreatEvaluationCheckBox().isSelected();
						gomokuBoard.getGomokuCellsPanel().getController().setThreatEvaluation(selected);
						gomokuBoard.getBlackThreatEvaluationButton().setEnabled(selected);
						gomokuBoard.getWhiteThreatEvaluationButton().setEnabled(selected);
					} else if (e.getSource() == gomokuBoard.getBlackThreatEvaluationButton()) {
						boolean selected = gomokuBoard.getBlackThreatEvaluationButton().isSelected();
						gomokuBoard.getGomokuCellsPanel().getController().setThreatEvaluationColor(GomokuModel.BLACK);
					} else if (e.getSource() == gomokuBoard.getWhiteThreatEvaluationButton()) {
						boolean selected = gomokuBoard.getWhiteThreatEvaluationButton().isSelected();
						gomokuBoard.getGomokuCellsPanel().getController().setThreatEvaluationColor(GomokuModel.WHITE);
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
