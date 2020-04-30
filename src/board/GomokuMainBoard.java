package board;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;

import model.GomokuModel;

public class GomokuMainBoard extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_ROW_COUNT = 15;
	private static final int DEFAULT_COLUMN_COUNT = 15;

	private int columnCount = DEFAULT_COLUMN_COUNT;
	private int rowCount = DEFAULT_ROW_COUNT;
	
	public static final int HUMAN_VS_HUMAN = 0;
	public static final int HUMAN_VS_COMPUTER = 1;
	public static final int COMPUTER_VS_COMPUTER = 2;

	private GomokuCellsPanel gomokuCellsPanel;
	private JPanel analysisPanel;
	private JButton resetButton;
	private JButton undoButton;
	private JButton redoButton;
	private JButton computeMoveButton;
	private JButton stopButton;
	private JCheckBox displayAnalysedMovesCheckBox;
	private JCheckBox displayThreatEvaluationCheckBox;
	private JRadioButton whiteThreatEvaluationButton;
	private JRadioButton blackThreatEvaluationButton;
	
	private JLabel globalEvaluationLabel;
	private JLabel blackEvaluationLabel;
	private JLabel whiteEvaluationLabel;
	
	private GomokuModel model;

	public GomokuMainBoard(int rule) {
		this(DEFAULT_ROW_COUNT, DEFAULT_COLUMN_COUNT, rule);
	}

	public GomokuMainBoard(int rowCount, int columnCount, int rule) {
		super("Gomoku");
		model = new GomokuModel(columnCount, rowCount);
		
		boolean computerTurn = false;
		
		if (rule == HUMAN_VS_COMPUTER) {
			int answer = JOptionPane.showConfirmDialog(null, "Play as black ? ", UIManager.getString("OptionPane.titleText"), JOptionPane.YES_NO_OPTION);
			
			if (answer == JOptionPane.NO_OPTION) {
				computerTurn = true;
			}
		} else if (rule == COMPUTER_VS_COMPUTER) {
			computerTurn = true;
		}
		
		new GomokuMainBoardController(this);
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		initialize(rowCount, columnCount, rule, computerTurn);
		
		getGomokuCellsPanel().getController().startNewGame();
	}

	private void initialize(int rowCount, int columnCount, int rule, boolean computerTurn) {
		setLayout(new GridBagLayout());
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		
		add(new JLabel(), constraints);
		
		constraints.gridx++;
		JPanel topNumbersPanel = new JPanel(new GridLayout(1, columnCount));
		for (int i = 0; i < columnCount; i++) {
			JLabel numberLabel = new JLabel("" + (i));
			numberLabel.setHorizontalAlignment(JLabel.CENTER);
			topNumbersPanel.add(numberLabel);
		}
		
		add(topNumbersPanel, constraints);

		constraints.gridx = 0;
		constraints.gridy++;
		JPanel leftNumbersPanel = new JPanel(new GridLayout(rowCount, 1));
		for (int i = 0; i < rowCount; i++) {
			JLabel numberLabel = new JLabel("" + (i));
			numberLabel.setHorizontalAlignment(JLabel.CENTER);
			leftNumbersPanel.add(numberLabel);
		}
		
		add(leftNumbersPanel, constraints);

		constraints.gridx++;
		add(getGomokuCellsPanel(), constraints);
		getGomokuCellsPanel().getController().setHumanVsComputer(rule == HUMAN_VS_COMPUTER);
		getGomokuCellsPanel().getController().setComputerVsComputer(rule == COMPUTER_VS_COMPUTER);
		getGomokuCellsPanel().getController().setComputerTurn(computerTurn);
		
		constraints.gridy = 0;
		
		constraints.gridx++;
		constraints.gridy++;
		JPanel rightNumbersPanel = new JPanel(new GridLayout(rowCount, 1));
		for (int i = 0; i < rowCount; i++) {
			JLabel numberLabel = new JLabel("" + (i));
			numberLabel.setHorizontalAlignment(JLabel.CENTER);
			rightNumbersPanel.add(numberLabel);
		}
		add(rightNumbersPanel, constraints);
		
		constraints.gridy++;
		constraints.gridy++;
		constraints.gridx = 1;
		JPanel buttomNumbersPanel = new JPanel(new GridLayout(1, columnCount));
		for (int i = 0; i < columnCount; i++) {
			JLabel numberLabel = new JLabel("" + (i));
			numberLabel.setHorizontalAlignment(JLabel.CENTER);
			buttomNumbersPanel.add(numberLabel);
		}
		
		add(buttomNumbersPanel, constraints);

		constraints.gridy++;

		add(getAnalysisPanel(), constraints);
		
		if (rule == HUMAN_VS_COMPUTER || rule == COMPUTER_VS_COMPUTER) {
			getComputeMoveButton().setEnabled(false);
			getStopButton().setEnabled(true);
		}
		
		if (rule == COMPUTER_VS_COMPUTER) {
			getResetButton().setEnabled(false);
			getUndoButton().setEnabled(false);
			getRedoButton().setEnabled(false);
		}
		
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
			constraints.fill = GridBagConstraints.HORIZONTAL;
			
			JPanel buttonsPanel = new JPanel();
			buttonsPanel.add(getResetButton());
			buttonsPanel.add(getUndoButton());
			buttonsPanel.add(getRedoButton());
			buttonsPanel.add(getComputeMoveButton());
			buttonsPanel.add(getStopButton());
			buttonsPanel.add(getDisplayCheckBox());

			JPanel displayThreatEvaluationPanel = new JPanel(new GridBagLayout());
			
			ButtonGroup buttonGroup = new ButtonGroup();
			
			buttonGroup.add(getBlackThreatEvaluationButton());
			buttonGroup.add(getWhiteThreatEvaluationButton());
			
			GridBagConstraints constraints2 = new GridBagConstraints();
			
			constraints2.gridx = 0;
			constraints2.gridy = 0;
			constraints2.gridwidth = 2;
			displayThreatEvaluationPanel.add(getDisplayDisplayThreatEvaluationCheckBox(), constraints2);
			
			constraints2.gridy++;
			constraints2.gridwidth = 1;
			displayThreatEvaluationPanel.add(getBlackThreatEvaluationButton(), constraints2);
			
			constraints2.gridx++;
			displayThreatEvaluationPanel.add(getWhiteThreatEvaluationButton(), constraints2);
			
			buttonsPanel.add(displayThreatEvaluationPanel);

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
			constraints.gridwidth = 2;
			analysisPanel.add(buttonsPanel, constraints);
		}
		return analysisPanel;
	}
	
	public JLabel getGlobalEvaluationLabel() {
		if (globalEvaluationLabel == null) {
			globalEvaluationLabel = new JLabel("0.0");
		}
		return globalEvaluationLabel;
	}
	
	public JLabel getBlackEvaluationLabel() {
		if (blackEvaluationLabel == null) {
			blackEvaluationLabel = new JLabel("0.0");
		}
		return blackEvaluationLabel;
	}
	
	public JLabel getWhiteEvaluationLabel() {
		if (whiteEvaluationLabel == null) {
			whiteEvaluationLabel = new JLabel("0.0");
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
	
	public JButton getComputeMoveButton() {
		if (computeMoveButton == null) {
			computeMoveButton = new JButton("Start computing");
		}
		return computeMoveButton;
	}
	
	public JButton getStopButton() {
		if (stopButton == null) {
			stopButton = new JButton("Stop computing");
			stopButton.setEnabled(false);
		}
		return stopButton;
	}
	
	public JCheckBox getDisplayCheckBox() {
		if (displayAnalysedMovesCheckBox == null) {
			displayAnalysedMovesCheckBox = new JCheckBox("Display analysis");
		}
		return displayAnalysedMovesCheckBox;
	}
	
	public JCheckBox getDisplayDisplayThreatEvaluationCheckBox() {
		if (displayThreatEvaluationCheckBox == null) {
			displayThreatEvaluationCheckBox = new JCheckBox("Display threat evaluation");
		}
		return displayThreatEvaluationCheckBox;
	}
	
	public JRadioButton getBlackThreatEvaluationButton() {
		if (blackThreatEvaluationButton == null) {
			blackThreatEvaluationButton = new JRadioButton("Black");
			blackThreatEvaluationButton.setSelected(true);
			blackThreatEvaluationButton.setEnabled(false);
		}
		return blackThreatEvaluationButton;
	}
	
	public JRadioButton getWhiteThreatEvaluationButton() {
		if (whiteThreatEvaluationButton == null) {
			whiteThreatEvaluationButton = new JRadioButton("White");
			whiteThreatEvaluationButton.setEnabled(false);
		}
		return whiteThreatEvaluationButton;
	}
	
	public int getColumnCount() {
		return columnCount;
	}
	
	public int getRowCount() {
		return rowCount;
	}

}
