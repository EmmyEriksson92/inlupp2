package application;

//Emmy Eriksson, 920910-3184
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

abstract class Place extends Polygon {

	private String name;
	private Position position;
	private boolean marked = false;
	private Category category;

	public Place(String name, Position position, Color c, EventHandler<MouseEvent> mouseEvent, Category category) {
		super(position.getX(), position.getY(), position.getX() - 25, position.getY() - 50, position.getX() + 25,
				position.getY() - 50);
		this.name = name;
		this.position = position;
		this.category = category;
		setFill(c);
		addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent);
		if (c.equals(Color.BLUE)) {
			category.setName("Underground");
		} else if (c.equals(Color.RED)) {
			category.setName("Bus");
		} else if (c.equals(Color.GREEN)) {
			category.setName("Train");
		} else {
			category.setName("None");
		}

	}

	public void update() {
		if (marked) {
			setStroke(Color.ORCHID);
			setStrokeWidth(4);
		} else {
			setStroke(null);
		}
	}

	public Category getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	public Position getPos() {
		return position;
	}

	public void setMarked(boolean m) {
		marked = m;
	}

	public boolean getMarked() {
		return marked;
	}

	@Override
	public String toString() {
		return "Name: " + name + ", position: " + position.getX() + "," + position.getY();
	}

}
