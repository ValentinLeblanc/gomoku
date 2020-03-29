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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

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
	
	private JLabel globalEvaluationLabel;
	private JLabel blackEvaluationLabel;
	private JLabel whiteEvaluationLabel;
	
	private GomokuMainBoardController controller;
	
	private GomokuModel model;
	
	public GomokuMainBoard(boolean computer) {
		this(DEFAULT_ROW_COUNT, DEFAULT_COLUMN_COUNT, computer);
	}

	public GomokuMainBoard(int rowCount, int columnCount, boolean computer) {
		super("Gomoku");
		model = new GomokuModel(columnCount, rowCount);
		
		boolean computerTurn = false;
		
		if (computer) {
			int answer = JOptionPane.showConfirmDialog(null, "Play as black ? ", UIManager.getString("OptionPane.titleText"), JOptionPane.YES_NO_OPTION);
			
			if (answer == JOptionPane.NO_OPTION) {
				computerTurn = true;
			}
		}
		
		this.controller = new GomokuMainBoardController(this);
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		initialize(rowCount, columnCount, computer, computerTurn);
	}

	private void initialize(int rowCount, int columnCount, boolean computer, boolean computerTurn) {
		setLayout(new GridBagLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(getGomokuCellsPanel(), constraints);
		getGomokuCellsPanel().getController().setComputer(computer);
		getGomokuCellsPanel().getController().setComputerTurn(computerTurn);
		
		constraints.gridy++;
		add(getAnalysisPanel(), constraints);
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
			constraints.gridwidth = 2;
			
			JPanel buttonsPanel = new JPanel();
			buttonsPanel.add(getResetButton());
			buttonsPanel.add(getUndoButton());
			buttonsPanel.add(getRedoButton());
			
			constraints.gridwidth = 1;
			analysisPanel.add(new JLabel("Global evaluation : "), constraints);
			constraints.gridx++;
			analysisPanel.add(getGlobalEvaluationLabel(), constraints);
			
			constraints.gridx = 0;
			constraints.gridy++;
			analysisPanel.add(new JLabel("Black evaluation : "), constraints);
			constraints.gridx++;
			analysisPanel.add(getBlackEvaluationLabel(), constraints);
			
			constraints.gridx = 0;
			constraints.gridy++;
			analysisPanel.add(new JLabel("White evaluation : "), constraints);
			constraints.gridx++;
			analysisPanel.add(getWhiteEvaluationLabel(), constraints);
			
			constraints.gridx = 0;
			constraints.gridy++;
			analysisPanel.add(buttonsPanel, constraints);
		}
		return analysisPanel;
	}
	
	public JLabel getGlobalEvaluationLabel() {
		if (globalEvaluationLabel == null) {
			globalEvaluationLabel = new JLabel();
		}
		return globalEvaluationLabel;
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
