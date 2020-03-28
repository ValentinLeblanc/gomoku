package engine;

public class GomokuEngine {

	public double computeEvaluation(int color, int[][] data) {
		
		double eval = 0;
		
		int rowCount = data[0].length;
		int columnCount = data.length;
		
		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < columnCount; col++) {
				if (data[col][row] == color) {
					eval++;
				}
			}

		}
		
		return eval;
	}
	
}
