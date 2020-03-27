package board;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GomokuBoardController {

	private GomokuMainBoard gomokuBoard;
	private ActionListener actionListener;
	
	public GomokuBoardController(GomokuMainBoard gomokuBoard) {
		this.gomokuBoard = gomokuBoard;
		gomokuBoard.getResetButton().addActionListener(getActionListener());
		gomokuBoard.getUndoButton().addActionListener(getActionListener());
		gomokuBoard.getRedoButton().addActionListener(getActionListener());
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
