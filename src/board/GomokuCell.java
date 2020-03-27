package board;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

public class GomokuCell extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int columnIndex;
	private int rowIndex;
	
	private Color circleColor;
	
	public static final int CELL_WIDTH = 50;
	public static final int CELL_HEIGHT = 50;

	private static final int CIRCLE_GAP = 4;
	
	public GomokuCell(int columnIndex, int rowIndex) {
		this.columnIndex = columnIndex;
		this.rowIndex = rowIndex;
		initialize();
	}

	private void initialize() {
		setPreferredSize(new Dimension(CELL_WIDTH, CELL_HEIGHT));
		setBackground(Color.orange);
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

		if (circleColor != null) {
		    g.setColor(circleColor);
		    g.fillOval(CIRCLE_GAP, CIRCLE_GAP, CELL_WIDTH - 2 * CIRCLE_GAP - 1, CELL_HEIGHT - 2 * CIRCLE_GAP - 1);
		}
	}

	public Color getCircleColor() {
		return circleColor;
	}

	public void setCircleColor(Color circleColor) {
		this.circleColor = circleColor;
	}
}
