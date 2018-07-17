
import java.io.BufferedReader;
import java.io.FileWriter;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
//import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import za.ac.wits.snake.DevelopmentAgent;
//import java.util.Scanner;
import java.util.ArrayList;

public class MyAgent extends DevelopmentAgent {

	public static void main(String args[]) throws IOException {
		MyAgent agent = new MyAgent();
		MyAgent.start(agent, args);
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
			String initString = br.readLine();
			String[] temp = initString.split(" ");
			int nSnakes = Integer.parseInt(temp[0]);
			while (true) {
				String line = br.readLine();
				if (line.contains("Game Over")) {
					break;
				}

				String apple1 = line;

				SnakePoint Apple1 = new SnakePoint(apple1, " ");
				String apple2 = br.readLine();
				SnakePoint Apple2 = new SnakePoint(apple2, " ");
				// create apple snakePoint
				// SnakePoint myApple = new SnakePoint(apple2, " ");
				Snake me = null;

				// initialise matrix
				// matrix setup
				int width = Integer.parseInt(temp[1]);
				int height = Integer.parseInt(temp[2]);

				int[][] myMatrix = new int[width][height];
				for (int i = 0; i < height; i++) { // y
					for (int j = 0; j < width; j++) {
						myMatrix[i][j] = -1;
					}
				}

				// do stuff with apples add to matrix
				if (Apple2.getX() > -1) {
					myMatrix[Apple2.getY()][Apple2.getX()] = -2;
				}
				if (Apple1.getX() > -1) {
					myMatrix[Apple1.getY()][Apple1.getX()] = -3;
				}

				String amAlive = "";

				int mySnakeNum = Integer.parseInt(br.readLine());
				for (int i = 0; i < nSnakes; i++) {
					String snakeLine = br.readLine();
					if (i == mySnakeNum) {
						me = new Snake(snakeLine);
						amAlive = snakeLine.split(" ")[0];
						myMatrix = MakeGrid.drawSnake(snakeLine, i, myMatrix);

						if (amAlive.equals("invisible")) {
							// System.out.println("log " + snakeLine);
						}
				
					} else {
						// increase other snake head sizes
						if (snakeLine.split(" ")[0].equals("alive") || snakeLine.split(" ")[0].equals("invisible")) {
							myMatrix = MakeGrid.drawSnake(snakeLine, i, myMatrix);
							myMatrix = MakeGrid.headNeighbours(snakeLine, i, myMatrix, height, width);
						}
					}

				}

				if (me.coordinates.size() > 0) {

					Stack<Cell> tempStack = BFS(myMatrix, height, width, Apple1, Apple2, me);
					if (!(tempStack.isEmpty())) {
						Cell cMove = tempStack.pop();
						cMove = tempStack.pop();
						if (cMove.getX() > me.head().getX()) {
							System.out.println(3);
						} else if (cMove.getX() < me.head().getX()) {
							System.out.println(2);
						} else if (cMove.getY() < me.head().getY()) {
							System.out.println(0);
						} else if (cMove.getY() > me.head().getY()) {
							System.out.println(1);
						} else {
							int move = new Random().nextInt(4);
						}

					} else {
						System.out.println(5);

					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static Stack<Cell> BFS(int[][] playArea, int height, int width, SnakePoint Apple1, SnakePoint Apple2, Snake me) {
		Stack<Cell> moves1 = new Stack<Cell>();
		Stack<Cell> moves2 = new Stack<Cell>();
		Queue<Cell> queue = new LinkedList<Cell>();
		// need distance array
		int[][] distance = new int[height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				distance[i][j] = playArea[i][j];
			}
		}

		// need parent array
		Cell[][] parent = new Cell[height][width];
		Cell cApple1 = new Cell(Apple1.getX(), Apple1.getY());
		Cell cApple2 = new Cell(Apple2.getX(), Apple2.getY());
		Cell cStart = new Cell(me.head().getX(), me.head().getY());
		if ((cApple1.getX() == -1) && (cApple1.getY() == -1) && (cApple2.getX() == -1) && (cApple2.getY() == -1)) {
			Cell cCenter = new Cell(24,24);
                        cApple2 = cCenter;
		}

		distance[cStart.getY()][cStart.getX()] = 0;
		parent[cStart.getY()][cStart.getX()] = null;
		Cell Current = new Cell();
		Cell Temp = new Cell();

		// bfs
		queue.add(cStart);
		while (!queue.isEmpty()) {
			Current = queue.remove();
			Current.setNeighbours(playArea, height, width);

			if (Current.getNeighbours().size() > 0) {
				for (int i = 0; i < Current.getNeighbours().size(); i++) {

					// get each neighbour
					Temp = Current.getNeighbours().get(i);
					// assign cost/distance
					distance[Temp.getY()][Temp.getX()] = (distance[Current.getY()][Current.getX()] + 1);
					// assign parent
					parent[Temp.getY()][Temp.getX()] = Current;
					// set visited
					playArea[Temp.getY()][Temp.getX()] = -5; // -5 visited
					// add to queue
					queue.add(Temp);
				}
			}

		}

		int count = 0;
		String Matrixline = "";

		for (int i = 0; i < height; i++) { // y

			for (int j = 0; j < (width - 1); j++) {
				if ((distance[i][j] < 10) && (distance[i][j] > -1)) {
					Matrixline = Matrixline + " 0" + distance[i][j];
				} else {
					Matrixline = Matrixline + " " + distance[i][j];
				}
				count = j;
			}
			Matrixline = Matrixline + " " + distance[i][count + 1] + "\n";
		}

		count = 0;
		Matrixline = "";
		for (int i = 0; i < height; i++) { // y

			for (int j = 0; j < (width - 1); j++) {
				Matrixline = Matrixline + " " + playArea[i][j];
				count = j;
			}
			Matrixline = Matrixline + " " + playArea[i][count + 1] + "\n";
			// System.out.println("log " + Matrixline);
		}

		if ((cApple2.getX() != -1) && (cApple2.getY() != -1)) {

			cApple2.setAppleNeighbours(playArea, height, width);
			if ((cApple2.getNeighbours().size() > 0) && (cStart.getX() >= 0) && (cStart.getY() >= 0)) {
				int min = 1000;
				Cell minNeighbour = new Cell();
				for (int i = 0; i < cApple2.getNeighbours().size(); i++) {
					if (distance[cApple2.getNeighbours().get(i).getY()][cApple2.getNeighbours().get(i).getX()] < min) {
						minNeighbour = cApple2.getNeighbours().get(i);
						min = distance[cApple2.getNeighbours().get(i).getY()][cApple2.getNeighbours().get(i).getX()];
					}
				}

				moves1.push(cApple2);
				moves1.push(minNeighbour);
				while ((minNeighbour.getX() != cStart.getX()) || (minNeighbour.getY() != cStart.getY())) {
					moves1.push(parent[minNeighbour.getY()][minNeighbour.getX()]);
					minNeighbour = parent[minNeighbour.getY()][minNeighbour.getX()];
					if (minNeighbour == null) {
						break;
					}
				}

			}
		}

		// apple 2
		if ((cApple1.getX() != -1) && (cApple1.getY() != -1))

		{

			cApple1.setAppleNeighbours(playArea, height, width);
			if ((cApple1.getNeighbours().size() > 0) && (cStart.getX() >= 0) && (cStart.getY() >= 0)) {
				int min = 1000;
				Cell minNeighbour = new Cell();
				for (int i = 0; i < cApple1.getNeighbours().size(); i++) {
					if (distance[cApple1.getNeighbours().get(i).getY()][cApple1.getNeighbours().get(i).getX()] < min) {
						minNeighbour = cApple1.getNeighbours().get(i);
						min = distance[cApple1.getNeighbours().get(i).getY()][cApple1.getNeighbours().get(i).getX()];
					}
				}
				moves2.push(cApple1);
				moves2.push(minNeighbour);
				while ((minNeighbour.getX() != cStart.getX()) || (minNeighbour.getY() != cStart.getY())) {
					moves2.push(parent[minNeighbour.getY()][minNeighbour.getX()]);
					minNeighbour = parent[minNeighbour.getY()][minNeighbour.getX()];
					if (minNeighbour == null) {
						break;
					}
				}

			}
		}

                
                return moves1;


	}
}

class SnakePoint {
	int x;
	int y;

	public SnakePoint(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public SnakePoint(String s, String seperator) {
		String[] vals = s.split(seperator);
		this.x = Integer.parseInt(vals[0]);
		this.y = Integer.parseInt(vals[1]);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean above(SnakePoint other) {
		return y < other.y;
	}

	public boolean below(SnakePoint other) {
		return y > other.y;
	}

	public boolean right(SnakePoint other) {
		return x > other.x;
	}

	public boolean left(SnakePoint other) {
		return x < other.x;
	}
}

class Snake {
	public ArrayList<SnakePoint> coordinates;

	public Snake(String snakeLine) {
		coordinates = new ArrayList<SnakePoint>();
		String[] vals = snakeLine.split(" ");
		if (vals[0].equals("invisible")) {
			for (int i = 4; i < vals.length; i++) {
				SnakePoint sp = new SnakePoint(vals[i], ",");
				coordinates.add(sp);
			}
		} else {
			if (vals[0].equals("alive")) {
				for (int i = 3; i < vals.length; i++) {
					SnakePoint sp = new SnakePoint(vals[i], ",");
					coordinates.add(sp);
				}
			}
		}
	}

	public SnakePoint head() {
		return coordinates.get(0);
	}

}

class MakeGrid {
	public static int[][] drawLine(int[][] Matrix, String coord1, String coord2, int snakeNumber) {
		int count = snakeNumber;
		int[][] myMatrix = Matrix;
		// compare x
		if (Integer.parseInt(coord1.split(",")[0]) == Integer.parseInt(coord2.split(",")[0])) {
			int difference = Math.abs(Integer.parseInt(coord2.split(",")[1]) - Integer.parseInt(coord1.split(",")[1]));
			if (Integer.parseInt(coord2.split(",")[1]) > Integer.parseInt(coord1.split(",")[1])) {
				for (int k = 0; k <= difference; k++) {
					myMatrix[Integer.parseInt(coord1.split(",")[1]) + k][Integer
							.parseInt(coord1.split(",")[0])] = count;
				}

			} else {
				for (int k = 0; k <= difference; k++) {
					myMatrix[Integer.parseInt(coord1.split(",")[1]) - k][Integer
							.parseInt(coord1.split(",")[0])] = count;
				}
			}
		} else {
			int difference = Math.abs(Integer.parseInt(coord2.split(",")[0]) - Integer.parseInt(coord1.split(",")[0]));
			if (Integer.parseInt(coord2.split(",")[0]) > Integer.parseInt(coord1.split(",")[0])) {
				for (int k = 0; k <= difference; k++) {
					myMatrix[Integer.parseInt(coord1.split(",")[1])][Integer.parseInt(coord1.split(",")[0])
							+ k] = count;
				}
			} else {
				for (int k = 0; k <= difference; k++) {
					myMatrix[Integer.parseInt(coord1.split(",")[1])][Integer.parseInt(coord1.split(",")[0])
							- k] = count;
				}

			}
		}

		return myMatrix;
	}

	public static int[][] headNeighbours(String input, int snakeNumber, int[][] Matrix, int height, int width) {
		int[][] myMatrix = Matrix;
		String temp[] = input.split(" ");
		String coord[] = temp[3].split(",");
		if (input.split(" ")[0].equals("invisible")) {
			coord = temp[4].split(",");
		} else {
			coord = temp[3].split(",");
		}
		// left
		myMatrix = validateNeighbour(Integer.parseInt(coord[0]) - 1, Integer.parseInt(coord[1]), myMatrix, height,
				width);

		// right
		myMatrix = validateNeighbour(Integer.parseInt(coord[0]) + 1, Integer.parseInt(coord[1]), myMatrix, height,
				width);
		// up
		myMatrix = validateNeighbour(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]) - 1, myMatrix, height,
				width);

		// down
		myMatrix = validateNeighbour(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]) + 1, myMatrix, height,
				width);

		return myMatrix;
	}

	public static int[][] validateNeighbour(int x, int y, int[][] Matrix, int height, int width) {
		int[][] myMatrix = Matrix;
		if ((x >= 0) && (x < width) && (y < height) && (y >= 0)) {
			myMatrix[y][x] = 99;
		}
		return myMatrix;
	}

	public static int[][] drawSnake(String input, int snakeNumber, int[][] Matrix) {
		int[][] myMatrix = Matrix;
		String temp[] = input.split(" ");
		if (temp[0].equals("invisible")) {

			for (int j = 4; (j < temp.length - 1); j++) {
				myMatrix = drawLine(myMatrix, temp[j], temp[j + 1], snakeNumber);
			}
		} else {
			for (int j = 3; (j < temp.length - 1); j++) {
				myMatrix = drawLine(myMatrix, temp[j], temp[j + 1], snakeNumber);

			}
		}
		return myMatrix;
	}

	public static void printBoard(int[][] myMatrix) {
		int count = 0;
		String line = "";

		for (int i = 0; i < height; i++) { // y

			for (int j = 0; j < width - 1; j++) {
				line = line + myMatrix[i][j] + " ";
				count = j;
			}
			line = line + myMatrix[i][count + 1];
			line += "\n";
		}
	}

	public static int width = 0;
	public static int height = 0;
}

class Cell {
	public int x;
	public boolean visited;

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int y;
	public int cost;
	LinkedList<Cell> neighbours = new LinkedList<Cell>();

	public LinkedList<Cell> getNeighbours() {
		return neighbours;
	}

	public Cell parent;

	public Cell getParent() {
		return parent;
	}

	public void setParent(Cell parent) {
		this.parent = parent;
	}

	public Cell() {
		this.x = 0;
		this.y = 0;
		this.parent = null;
		this.visited = false;
		this.cost = 999;

	}

	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
		this.parent = null;
		this.visited = false;
		this.cost = 999;

	}

	public void setAppleNeighbours(int[][] playArea, int height, int width) {
		// left cell
		validateCellApple(new Cell((this.getX() - 1), this.getY()), playArea, height, width);
		// right cell
		validateCellApple(new Cell((this.getX() + 1), this.getY()), playArea, height, width);
		// above cell
		validateCellApple(new Cell(this.getX(), (this.getY() - 1)), playArea, height, width);
		// below cell
		validateCellApple(new Cell(this.getX(), (this.getY() + 1)), playArea, height, width);
	}

	public void validateCellApple(Cell current, int[][] playArea, int height, int width) {
		if ((current.getX() >= 0) && (current.getX() < width) && (current.getY() < height) && (current.getY() >= 0)) {
			this.neighbours.add(current);
		}
	}

	public void setNeighbours(int[][] playArea, int height, int width) {
		// left cell
		validateCell(new Cell((this.getX() - 1), this.getY()), playArea, height, width);
		// right cell
		validateCell(new Cell((this.getX() + 1), this.getY()), playArea, height, width);
		// above cell
		validateCell(new Cell(this.getX(), (this.getY() - 1)), playArea, height, width);
		// below cell
		validateCell(new Cell(this.getX(), (this.getY() + 1)), playArea, height, width);
	}

	public void validateCell(Cell current, int[][] playArea, int height, int width) {
		if ((current.getX() >= 0) && (current.getX() < width) && (current.getY() < height) && (current.getY() >= 0)) {
			if (playArea[current.getY()][current.getX()] == -1) {
				this.neighbours.add(current);
			}

		}
	}

}
