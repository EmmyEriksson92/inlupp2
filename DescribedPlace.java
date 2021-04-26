package application;

//Emmy Eriksson, 920910-3184
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class DescribedPlace extends Place {

	private String description;

	public DescribedPlace(String name, String description, Position position, Color c, Category category,
			EventHandler<MouseEvent> mouseEvent) {
		super(name, position, c, mouseEvent, category);
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return super.toString() + ", " + getDescription() + "\n";
	}

}
