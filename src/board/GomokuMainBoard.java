package board;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
	
	private GomokuBoardController controller;

	public GomokuMainBoard() {
		this(DEFAULT_ROW_COUNT, DEFAULT_COLUMN_COUNT);
	}

	public GomokuMainBoard(int rowCount, int columnCount) {
		super("Gomoku");
		this.controller = new GomokuBoardController(this);
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

	public GomokuCellsPanel getGomokuCellsPanel() {
		if (gomokuCellsPanel == null) {
			gomokuCellsPanel = new GomokuCellsPanel(rowCount, columnCount);
		}
		return gomokuCellsPanel;
	}
	
	public JPanel getAnalysisPanel() {
		if (analysisPanel == null) {
			analysisPanel = new JPanel();
			analysisPanel.add(getResetButton());
			analysisPanel.add(getUndoButton());
			analysisPanel.add(getRedoButton());
		}
		return analysisPanel;
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
