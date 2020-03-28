package engine;

import java.util.ArrayList;
import java.util.List;

import model.GomokuModel;

public class GomokuEngine {

	private int[][] data;
	private int color;
	
	public double computeEvaluation(int color, int[][] data) {
		this.data = data;
		this.color = color;
		
		double eval = 0;
		
		int rowCount = data[0].length;
		int columnCount = data.length;
		
		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < columnCount; col++) {
				if (data[col][row] == color) {
					
				List<int[][]> stripes = new ArrayList<int[][]>();
					 
					// east stripes
					int startX = Math.max(0, col - 4);
					for (int k = 0; startX + k < col + 1 && startX + k + 5 < data.length + 1; k++) {
						int[][] stripe = new int[5][2];
						for (int f = 0; f < 5; f++) {
							stripe[f][0] = startX + k + f;
							stripe[f][1] = row;
						}
						stripes.add(stripe);
					}
					eval += computeStripes(stripes);
					
					// south stripes
					int startY = Math.max(0, row - 4);
					for (int k = 0; startY + k < row + 1 && startY + k + 5 < data[0].length + 1; k++) {
						int[][] stripe = new int[5][2];
						for (int f = 0; f < 5; f++) {
							stripe[f][0] = startY + k + f;
							stripe[f][1] = col;
						}
						stripes.add(stripe);
					}
					eval += computeStripes(stripes);

				}
			}

		}
		
		return eval;
	}

	private double computeStripes(List<int[][]> stripes) {
		int eval = 0;
		
		for (int[][] stripe : stripes) {
			eval += computeStripe(stripe);
		}
		return eval;
	}

	private int computeStripe(int[][] stripe) {
		
		int eval = 0;
		
		int[] weightStripe = new int[5];
		
		weightStripe[0] = 1;
		weightStripe[1] = 2;
		weightStripe[2] = 3;
		weightStripe[3] = 2;
		weightStripe[4] = 1;
		
		for (int k = 0; k < 5; k++) {
			int colIndex = stripe[k][0];
			int rowIndex = stripe[k][1];
			eval += data[colIndex][rowIndex] == color ? weightStripe[k] : data[colIndex][rowIndex] == GomokuModel.UNPLAYED ? 0 : -weightStripe[k];
		}
		return eval;
	}
	
}
