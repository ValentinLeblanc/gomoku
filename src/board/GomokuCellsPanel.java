package board;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import model.GomokuModel;

public class GomokuCellsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int CELL_WIDTH = 50;
	public static final int CELL_HEIGHT = 50;

	public static final Color BLACK_COLOR = Color.BLACK;
	public static final Color WHITE_COLOR = Color.WHITE;

	private GomokuCellsPanelController controller;
	
	private int columnCount;
	private int rowCount;
	
	public GomokuCellsPanel(GomokuModel model) {
		this.rowCount = model.getRowCount();
		this.columnCount = model.getColumnCount();
		this.controller = new GomokuCellsPanelController(this, model);
		setLayout(new GridLayout(rowCount, columnCount));
		setSize(new Dimension(columnCount * (CELL_WIDTH + 2), rowCount * (CELL_HEIGHT + 3)));

		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
				GomokuCell gomokuCell = new GomokuCell(columnIndex, rowIndex);
				gomokuCell.addMouseListener(controller.getCellMouseListener());
				add(gomokuCell, rowIndex * columnCount + columnIndex);
			}
		}

	}
	
	public int getColumnCount() {
		return columnCount;
	}
	
	public int getRowCount() {
		return rowCount;
	}
	
	public GomokuCellsPanelController getController() {
		return controller;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (controller.getWinData() != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.RED);
			int x1 = GomokuCell.CELL_WIDTH * controller.getWinData()[0][0] + GomokuCell.CELL_WIDTH / 2;
			int y1 = GomokuCell.CELL_HEIGHT * controller.getWinData()[0][1] + GomokuCell.CELL_HEIGHT / 2;
			int x2 = GomokuCell.CELL_WIDTH * controller.getWinData()[1][0] + GomokuCell.CELL_WIDTH / 2;
			int y2 = GomokuCell.CELL_HEIGHT * controller.getWinData()[1][1] + GomokuCell.CELL_HEIGHT / 2;
			
			g2.setStroke(new BasicStroke(5));
			g2.draw(new Line2D.Float(x1, y1, x2, y2));
		}
	}

}
