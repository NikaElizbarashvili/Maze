import java.util.Random;

public class MazeBack {
	private static int mazeSize = 2 * Main.mazeSize + 1;
	private static int[][] mazeMatrix = new int[mazeSize][mazeSize];
	private static int iT, jT; //Theseus
	private static int shortestPath;

	public MazeBack() {
		setupMaze();
//		print();
	}
	
	public void setupMaze() {
		newMaze();		
	}
	
	private void newMaze() {
		// initial setup maze and edges array
		int edgeSize = Main.mazeSize * (Main.mazeSize - 1) * 2;
		int[][] edges = new int[edgeSize][2];
		int count = 1;
		int edgeCount = 0;
		int i = 0, j = 0, i1 = 0, j1 = 0;
		for (i = 0; i < mazeSize; i++)
			for (j = 0; j < mazeSize; j++)
				if ((i % 2 == 1) & (j % 2 == 1))
					mazeMatrix[i][j] = count++;
				else {
					mazeMatrix[i][j] = -1;
					if (!((i % 2 == 0) & (j % 2 == 0)) & (i > 0) & (j > 0) & (i < mazeSize - 1) & (j < mazeSize - 1)) {
						edges[edgeCount][0] = i;
						edges[edgeCount][1] = j;
						edgeCount++;
					}
				}
		
		// shuffle edges array
		Random r = new Random();
		int temp1, temp2;
		for (i = 0; i < edgeSize - 1; i++) {
			j = r.nextInt(i + 1, edgeSize);
			temp1 = edges[i][0];
			temp2 = edges[i][1];
			edges[i][0] = edges[j][0];
			edges[i][1] = edges[j][1];
			edges[j][0] = temp1;
			edges[j][1] = temp2;
		}
		
		// delete edges to create passages
		for (int k = 0; k < edgeSize; k++) {
			// calculate non-edge coordinates from edge coordinates
			if (edges[k][0] % 2 == 0) {
				i = edges[k][0] - 1;
				i1 = edges[k][0] + 1;
				j = edges[k][1];
				j1 = j;
			} else {
				j = edges[k][1] - 1;
				j1 = edges[k][1] + 1;
				i = edges[k][0];
				i1 = i;
			}
			// check for different trees and combine
			temp1 = mazeMatrix[i1][j1];
			if (mazeMatrix[i][j] != mazeMatrix[i1][j1]) {
				for (int i2 = 0; i2 < mazeSize; i2++)
					for (int j2 = 0; j2 < mazeSize; j2++)
						if (mazeMatrix[i2][j2] == temp1)
							mazeMatrix[i2][j2] = mazeMatrix[i][j];
				mazeMatrix[edges[k][0]][edges[k][1]] = mazeMatrix[i][j];
			}
		}
		
		// make all passages equal to 0
		for (i = 0; i < mazeSize; i++)
			for (j = 0; j < mazeSize; j++)
				if (mazeMatrix[i][j] != -1)
					mazeMatrix[i][j] = 0;
		
		//make exits
		j = r.nextInt(0, Main.mazeSize) * 2 + 1;
		mazeMatrix[j][0] = 0;//left
		j = r.nextInt(0, Main.mazeSize) * 2 + 1;
		mazeMatrix[0][j] = 0;//top
		j = r.nextInt(0, Main.mazeSize) * 2 + 1;
		mazeMatrix[j][mazeSize - 1] = 0;//right
		j = r.nextInt(0, Main.mazeSize) * 2 + 1;
		mazeMatrix[mazeSize - 1][j] = 0;//bottom
		
		// place Theseus
		if (Main.mazeSize%2==1) 
			iT=jT=Main.mazeSize;
		else
			iT=jT=Main.mazeSize+1;
		mazeMatrix[iT][jT] = 1;					
		
		//calculate steps for the shortest route		
		shortestPath = shortestPath(1, false);

		// clean the maze
		cleanMaze(shortestPath + 1, true);
	}

	private int shortestPath(int count, boolean found) {
		if (found)
			return count - 1;
		for (int i = 1; i < mazeSize; i ++)
			for (int j = 1; j < mazeSize; j ++)
				if (mazeMatrix[i][j] == count) {
					if (mazeMatrix[i - 1][j] == 0)
						if (i - 1 > 0)
							mazeMatrix[i - 1][j]  = count + 1;
						else {
							mazeMatrix[i - 1][j] = count + 1;
							found = true;
						}
					if (mazeMatrix[i + 1][j] == 0)
						if (i + 1 < mazeSize - 1)
							mazeMatrix[i + 1][j]  = count + 1;
						else {
							mazeMatrix[i + 1][j] = count + 1;
							found = true;
						}
					if (mazeMatrix[i][j - 1] == 0)
						if (j - 1 > 0)
							mazeMatrix[i][j - 1] = count + 1;
						else {
							mazeMatrix[i][j - 1] = count + 1;
							found = true;
						}
					if (mazeMatrix[i][j + 1] == 0)
						if (j + 1 < mazeSize - 1)
							mazeMatrix[i][j + 1]  = count + 1;
						else {
							mazeMatrix[i][j + 1] = count + 1;
							found = true;
						}
				}
		count = shortestPath(count+1, found);
		return count;
	}

	private void cleanMaze(int step, boolean firstTimeCall) {
		for (int i = 0; i < mazeSize; i++)
			for (int j = 0; j < mazeSize; j++)
				if (mazeMatrix[i][j] == step) {
					if ((firstTimeCall) & ((i == 0) || (j == 0) || (i == mazeSize - 1) || (j == mazeSize - 1)))
						mazeMatrix[i][j] = -2;
					else if ((mazeMatrix[i - 1][j] == -2) || (mazeMatrix[i + 1][j] == -2)
							|| (mazeMatrix[i][j - 1] == -2) || (mazeMatrix[i][j + 1] == -2))
						mazeMatrix[i][j] = -2;
					else
						mazeMatrix[i][j] = 0;
				}
		if (step > 1)
			cleanMaze(step - 1, false);
		else
			for (int i = 0; i < mazeSize; i++)
				for (int j = 0; j < mazeSize; j++)
					if(mazeMatrix[i][j]==-2)
						mazeMatrix[i][j] = 1;
	}
	
	private void print() {
		for (int j = 0; j < mazeSize; j++) {
			for (int i = 0; i < mazeSize; i++) {
				if ((mazeMatrix[i][j] >= 0) & (mazeMatrix[i][j]<10))
					System.out.print(" ");
				System.out.print(mazeMatrix[i][j] + "  ");
			}
			System.out.println();
		}
		System.out.println("-------------------");
	}

	public static int getiT() {
		return iT;
	}

	public static int getShortestPath() {
		return shortestPath;
	}

	public static void setiT(int iT) {
		MazeBack.iT = iT;
	}

	public static int getjT() {
		return jT;
	}

	public static void setjT(int jT) {
		MazeBack.jT = jT;
	}

	
	public static int getMazeSize() {
		return mazeSize;
	}

	public static int getMatrixElement(int i, int j) {
		return mazeMatrix[i][j];
	}

}
