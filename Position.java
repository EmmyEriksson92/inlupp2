package application;

//Emmy Eriksson,920910-3184
public class Position {

	private double x;
	private double y;

	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Position) {
			Position p = (Position) other;
			return x == p.x && y == p.y;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (int) (x * 1000 + y);
	}

}
