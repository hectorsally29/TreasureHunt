import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

class Point {
	int x;
	int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Object other) {
		if (other instanceof Point) {
			Point p = (Point) other;
			return (x == p.x) && (y == p.y);
		}
		return false;
	}

	public int hashCode() {
		return x + 31 * y;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}

public class TreasureMap {

	private char[][] location; // grid of maze locations
	// W is wall, period is open space, T is treasure

	private int width; // dimensions of location grid
	private int height; // width is first coordinate

	Point start; // starting location

	Set<Point> paths; // points along paths to treasure

	public TreasureMap(String filename) {
		paths = new HashSet<Point>();

		Scanner input = null;
		try {
			input = new Scanner(new File(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("Error: unable to open file " + filename);
			System.exit(1);
		}

		width = input.nextInt();
		height = input.nextInt();
		input.nextLine();
		location = new char[width][height];
		for (int j = 0; j < height; j++) {
			String line = input.nextLine();
			for (int i = 0; i < width; i++) {
				location[i][j] = line.charAt(i);
				if (location[i][j] == 'S') {
					start = new Point(i, j);
				}
			}
		}
	}

	public void print() {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				char loc = location[i][j];
				if (paths.contains(new Point(i, j)))
					System.out.print("*");
				else
					System.out.print(loc);
			}
			System.out.println();
		}
		System.out.println(); // extra blank line after the map
	}

	public boolean canGo(Point p) {
		// returns whether p is a good direction to go
		// (i.e. not a wall or somewhere we've been)

		char c = location[p.x][p.y];
		return (c == '.') || (c == 'T');
	}

	public void markVisited(Point p) {
		// change p's location to a space so we know we've been there

		char c = location[p.x][p.y];
		if (c == 'T')
			location[p.x][p.y] = 't';
		else if (c == '.')
			location[p.x][p.y] = ' ';
		else {
			System.err.print("ERROR: markVisited called on invalid character: " + c + " " + p);
			System.exit(1);
		}
	}

	public boolean isTreasure(Point p) {
		// returns whether p contains treasure
		char c = location[p.x][p.y];
		return (c == 'T') || (c == 't');
	}

	public int search() {
		Set<Point> found = new HashSet<Point>(); // set of found treasures
		Stack<Point> stack = new Stack<Point>(); // current route
		stack.push(start); // build path starting with starting location
		while (!stack.empty()) {
			Point curr = stack.peek(); // current location
			Point next = new Point(curr.x + 1, curr.y); // try +x direction (right)
			Point newNext = new Point(curr.x, curr.y + 1); // direction down
			Point newNext1 = new Point(curr.x, curr.y - 1); // direction up
			Point newNext2 = new Point(curr.x - 1, curr.y); // direction left

			if (canGo(next)) { // make sure next isn’t a wall or already visited
				stack.push(next); // let’s try this direction
				markVisited(next); // mark so we know not to go there again

				// checks for direction down
			} else if (canGo(newNext)) {
				stack.push(newNext); // let’s try this direction
				markVisited(newNext);

				// checks for direction up
			} else if (canGo(newNext1)) {
				stack.push(newNext1); // let’s try this direction
				markVisited(newNext1);

				// checks for direction left
			} else if (canGo(newNext2)) {
				stack.push(newNext2); // let’s try this direction
				markVisited(newNext2);
			} else
				stack.pop(); // can’t go farther; backtrack

			// if treasure is found add curr
			if (isTreasure(curr)) {
				found.add(curr);
				paths.addAll(stack);
			}
		}

		return found.size();
	}

	public static void main(String[] args) {
		TreasureMap map = new TreasureMap("maze1");
		map.print();

		int num = map.search();
		if (num == 0)
			System.out.println("No treasure found");
		else {
			if (num == 1)
				System.out.println("Found 1 treasure");
			else
				System.out.println("Found " + num + " treasures");
		}
		map.print();
	}
}
