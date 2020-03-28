package board;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.GomokuModel;

public class GomokuMainBoard extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_ROW_COUNT = 13;
	private static final int DEFAULT_COLUMN_COUNT = 13;

	private int columnCount = DEFAULT_COLUMN_COUNT;
	private int rowCount = DEFAULT_ROW_COUNT;

	private GomokuCellsPanel gomokuCellsPanel;
	private JPanel analysisPanel;
	private JButton resetButton;
	private JButton undoButton;
	private JButton redoButton;
	
	private JLabel blackEvaluationLabel;
	private JLabel whiteEvaluationLabel;
	
	private GomokuMainBoardController controller;
	
	private GomokuModel model;

	public GomokuMainBoard() {
		this(DEFAULT_ROW_COUNT, DEFAULT_COLUMN_COUNT);
	}

	public GomokuMainBoard(int rowCount, int columnCount) {
		super("Gomoku");
		model = new GomokuModel(columnCount, rowCount);
		this.controller = new GomokuMainBoardController(this);
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		initialize(rowCount, columnCount);
	}

	private void initialize(int rowCount, int columnCount) {
		setLayout(new GridLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		add(getGomokuCellsPanel());
		add(getAnalysisPanel());
		setResizable(false);
		pack();
		setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);
	}
	
	public GomokuModel getModel() {
		return model;
	}

	public GomokuCellsPanel getGomokuCellsPanel() {
		if (gomokuCellsPanel == null) {
			gomokuCellsPanel = new GomokuCellsPanel(model);
		}
		return gomokuCellsPanel;
	}
	
	public JPanel getAnalysisPanel() {
		if (analysisPanel == null) {
			analysisPanel = new JPanel();
			analysisPanel.setLayout(new GridBagLayout());
			
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.gridy = 0;
			
			analysisPanel.add(new JLabel("Black evaluation : "), constraints);
			constraints.gridx++;
			analysisPanel.add(getBlackEvaluationLabel(), constraints);
			
			constraints.gridx = 0;
			constraints.gridy++;
			analysisPanel.add(new JLabel("White evaluation : "), constraints);
			constraints.gridx++;
			analysisPanel.add(getWhiteEvaluationLabel(), constraints);
			
			constraints.gridwidth = 2;
			constraints.gridx = 0;
			constraints.gridy++;
			
			JPanel buttonsPanel = new JPanel();
			buttonsPanel.add(getResetButton());
			buttonsPanel.add(getUndoButton());
			buttonsPanel.add(getRedoButton());
			
			analysisPanel.add(buttonsPanel, constraints);
		}
		return analysisPanel;
	}
	
	public JLabel getBlackEvaluationLabel() {
		if (blackEvaluationLabel == null) {
			blackEvaluationLabel = new JLabel();
		}
		return blackEvaluationLabel;
	}
	
	public JLabel getWhiteEvaluationLabel() {
		if (whiteEvaluationLabel == null) {
			whiteEvaluationLabel = new JLabel();
		}
		return whiteEvaluationLabel;
	}

	public JButton getResetButton() {
		if (resetButton == null) {
			resetButton = new JButton("Reset");
		}
		return resetButton;
	}
	
	public JButton getUndoButton() {
		if (undoButton == null) {
			undoButton = new JButton("Undo");
		}
		return undoButton;
	}
	
	public JButton getRedoButton() {
		if (redoButton == null) {
			redoButton = new JButton("Redo");
		}
		return redoButton;
	}
	
	public int getColumnCount() {
		return columnCount;
	}
	
	public int getRowCount() {
		return rowCount;
	}

}
