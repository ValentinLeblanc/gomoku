package board;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.Line2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class GomokuCell extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int columnIndex;
	private int rowIndex;
	
	private static final Color CELL_COLOR = Color.orange.darker();
	
	private Color circleColor;
	private Color analysedColor;

	private boolean lastMove = false;
	private boolean lastAnalysed = false;
	private boolean displayAnalysis = false;
	private boolean displayThreatEvaluation = true;
	
	private JLabel blackThreatEvaluationLabel;
	private JLabel whiteThreatEvaluationLabel;
	
	private int blackThreatEvaluation = 0;
	private int whiteThreatEvaluation = 0;

	public static final int CELL_WIDTH = 50;
	public static final int CELL_HEIGHT = 50;

	private static final int CIRCLE_GAP = 4;
	private static final int CIRCLE_GAP_RED = 20;
	
	public GomokuCell(int columnIndex, int rowIndex) {
		this.columnIndex = columnIndex;
		this.rowIndex = rowIndex;
		initialize();
	}

	private void initialize() {
		setLayout(new GridLayout(2, 1));
		setPreferredSize(new Dimension(CELL_WIDTH, CELL_HEIGHT));
		setBackground(CELL_COLOR);
		
		blackThreatEvaluationLabel = new JLabel("");
		blackThreatEvaluationLabel.setPreferredSize(new Dimension(50, 40));
		blackThreatEvaluationLabel.setHorizontalAlignment(JLabel.CENTER);
		blackThreatEvaluationLabel.setForeground(Color.BLACK);
		add(blackThreatEvaluationLabel);
		
		whiteThreatEvaluationLabel = new JLabel("");
		whiteThreatEvaluationLabel.setPreferredSize(new Dimension(50, 40));
		whiteThreatEvaluationLabel.setHorizontalAlignment(JLabel.CENTER);
		whiteThreatEvaluationLabel.setForeground(Color.WHITE);
		add(whiteThreatEvaluationLabel);
	}

	public int getRowIndex() {
		return rowIndex;
	}
	
	public int getColumnIndex() {
		return columnIndex;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
        Graphics2D g2 = (Graphics2D) g;

		g2.setStroke(new BasicStroke(1));
		g2.draw(new Line2D.Float(0, 0, 0, CELL_HEIGHT - 1));
		g2.draw(new Line2D.Float(CELL_WIDTH - 1, 0, CELL_WIDTH - 1, CELL_HEIGHT - 1));
		g2.draw(new Line2D.Float(0, 0, CELL_WIDTH - 1, 0));
		g2.draw(new Line2D.Float(0, CELL_HEIGHT - 1, CELL_WIDTH - 1, CELL_HEIGHT - 1));
		
		if (analysedColor != null) {
			if (displayAnalysis) {
				g.setColor(analysedColor);
				g.fillOval(2 * CIRCLE_GAP, 2 * CIRCLE_GAP, CELL_WIDTH - 4 * CIRCLE_GAP - 1, CELL_HEIGHT - 4 * CIRCLE_GAP - 1);
				if (lastAnalysed && displayAnalysis) {
				    g.setColor(Color.RED);
				    g.fillOval(CIRCLE_GAP_RED, CIRCLE_GAP_RED, CELL_WIDTH - 2 * CIRCLE_GAP_RED - 1, CELL_HEIGHT - 2 * CIRCLE_GAP_RED - 1);
				}
			} else {
				g.setColor(null);
			}
		} else if (circleColor != null) {
			g.setColor(circleColor);
			g.fillOval(CIRCLE_GAP, CIRCLE_GAP, CELL_WIDTH - 2 * CIRCLE_GAP - 1, CELL_HEIGHT - 2 * CIRCLE_GAP - 1);
			if (lastMove) {
				g.setColor(Color.RED);
				g.fillOval(CIRCLE_GAP_RED, CIRCLE_GAP_RED, CELL_WIDTH - 2 * CIRCLE_GAP_RED - 1, CELL_HEIGHT - 2 * CIRCLE_GAP_RED - 1);
			}
		}
		
		if (displayThreatEvaluation) {
			
			if (blackThreatEvaluation != 0) {
				blackThreatEvaluationLabel.setText("" + blackThreatEvaluation);
			} else {
				blackThreatEvaluationLabel.setText("");
			}
			
			if (whiteThreatEvaluation != 0) {
				whiteThreatEvaluationLabel.setText("" + whiteThreatEvaluation);
			} else {
				whiteThreatEvaluationLabel.setText("");
			}
		}
	}

	public void setCircleColor(Color circleColor) {
		this.circleColor = circleColor;
	}
	
	public void setAnalysedColor(Color analysedColor) {
		this.analysedColor = analysedColor;
	}
	
	public Color getAnalysedColor() {
		return analysedColor;
	}

	public void setLastMove(boolean lastMove) {
		this.lastMove = lastMove;
	}
	
	public void setLastAnalysed(boolean lastAnalysed) {
		this.lastAnalysed = lastAnalysed;
	}

	public void setDisplayAnalysis(boolean display) {
		this.displayAnalysis = display;
	}
	
	public boolean isDisplayAnalysis() {
		return displayAnalysis;
	}

	public void setBlackThreatEvaluation(int threatEvaluation) {
		this.blackThreatEvaluation = threatEvaluation;
	}
	
	public void setWhiteThreatEvaluation(int threatEvaluation) {
		this.whiteThreatEvaluation = threatEvaluation;
	}
}
