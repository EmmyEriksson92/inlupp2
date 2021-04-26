package application;

//Emmmy Eriksson, 920910-3184
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class NamedPlace extends Place {

	public NamedPlace(String name, Position position, Color c, EventHandler<MouseEvent> mouseEvent, Category category) {
		super(name, position, c, mouseEvent, category);

	}

	@Override
	public String toString() {
		return super.toString() + "\n";
	}
}
