package application;


//Emmy Eriksson, 920910-3184
import javafx.scene.paint.Color;

public class Category {

	private String name;
	private Color color;

	public Category(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public String setName(String name) {
		return this.name = name;
	}

	public Color getColor() {
		return color;
	}

}
