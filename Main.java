package application;

//Emmy Eriksson.
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

enum state {
	observe, createNew
}

public class Main extends Application {
	private state currentState = state.observe;
	boolean change = false;

	private HashMap<Position, Place> positionMap = new HashMap<>();
	private HashMap<String, List<Place>> perName = new HashMap<>();
	private HashMap<String, List<Place>> catPlace = new HashMap<>();
	private ArrayList<Place> markPlace = new ArrayList<Place>();
	private String[] categories = { "Bus", "Underground", "Train" };
	private ObservableList list = (ObservableList) FXCollections.observableArrayList(categories);
	private ListView<String> listView = new ListView(list);

	private MusLyss musLyss = new MusLyss();
	private ImageView imageView = new ImageView();
	private Image image;
	private Stage primaryStage;
	private BorderPane root;
	private Pane pane;
	private ButtonType yesButton, cancelButton;
	private RadioButton namedButton, describedButton;
	private Button newButton, searchButton, hideButton, removeButton, coordinatesButton, hideCategory;
	private MenuItem f1, f2, f3, f4;
	private TextField searchField = new TextField("Search");
	private PlaceClickListener componentLyss = new PlaceClickListener();

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;

		root = new BorderPane();
		pane = new Pane();
		root.setCenter(pane);
		VBox top = new VBox();
		root.setTop(top);

		MenuBar menuBar = new MenuBar();
		Menu menuFile = new Menu("File");
		menuBar.getMenus().add(menuFile);
		top.getChildren().add(menuBar);
		f1 = new MenuItem("Load map");
		f2 = new MenuItem("Load places");
		f3 = new MenuItem("Save");
		f4 = new MenuItem("Exit");
		menuFile.getItems().addAll(f1, f2, f3, f4);
		f1.setOnAction(new FileListener());
		f2.setOnAction(new FileListener());
		f3.setOnAction(new FileListener());
		f4.setOnAction(new FileListener());

		newButton = new Button("New");
		namedButton = new RadioButton("Named");
		namedButton.setSelected(true);
		describedButton = new RadioButton("Described");
		ToggleGroup group = new ToggleGroup();
		namedButton.setToggleGroup(group);
		describedButton.setToggleGroup(group);
		VBox radiobuttons = new VBox();
		radiobuttons.getChildren().addAll(namedButton, describedButton);

		searchButton = new Button("Search");
		hideButton = new Button("Hide");
		;
		removeButton = new Button("Remove");
		coordinatesButton = new Button("Coordinates");
		hideCategory = new Button("Hide category");
		HBox topCenter = new HBox();
		topCenter.setPadding(new Insets(5));
		topCenter.getChildren().add(newButton);
		topCenter.getChildren().add(radiobuttons);
		topCenter.getChildren().addAll(searchField, searchButton, hideButton, removeButton, coordinatesButton);
		topCenter.setAlignment(Pos.TOP_CENTER);
		top.getChildren().add(topCenter);

		VBox right = new VBox();
		Label categories = new Label("Categories");
		categories.setPadding(new Insets(10));
		right.setSpacing(15);
		right.getChildren().addAll(categories, listView);
		listView.getSelectionModel().selectedItemProperty().addListener(new ListHandler());
		right.getChildren().add(hideCategory);
		hideCategory.setOnAction(new HideCLyss());
		root.setRight(right);
		listView.setPrefSize(125, 400);
		listView.setPadding(new Insets(5));
		listView.setCellFactory(lv -> {
			ListCell<String> cell = new ListCell<>();
			cell.textProperty().bind(cell.itemProperty());
			cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
				listView.requestFocus();
				if (!cell.isEmpty()) {
					int index = cell.getIndex();
					if (listView.getSelectionModel().getSelectedIndices().contains(index)) {
						listView.getSelectionModel().clearSelection(index);
					} else {
						listView.getSelectionModel().select(index);
					}
					event.consume();
				}
			});
			return cell;
		});
		newButton.setOnAction(new OperationListener());
		searchButton.setOnAction(new OperationListener());
		hideButton.setOnAction(new OperationListener());
		removeButton.setOnAction(new OperationListener());
		coordinatesButton.setOnAction(new OperationListener());

		pane.getChildren().add(imageView);
		root.setCenter(pane);

		Scene scene = new Scene(root);
		primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new ExitHandler());

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	class ListHandler implements ChangeListener<String> {
		public void changed(ObservableValue obs, String old, String nev) {
			String temp = listView.getSelectionModel().getSelectedItem();

			for (Map.Entry<String, List<Place>> entry : catPlace.entrySet()) {
				if (entry.getKey().equals(temp)) {
					List<Place> place = entry.getValue();
					for (Place p : place) {
						System.out.println("Showing " + temp);
						p.setVisible(true);

					}
				}
			}

		}
	}

	class ExitHandler implements EventHandler<WindowEvent> {
		public void handle(WindowEvent event) {
			System.out.println("Stage is closing");
			if (!change) {
				System.exit(0);
			} else if (change) {
				Alert alert = getExitAlert();
				alert.showAndWait().ifPresent(type -> {
					if (type == yesButton) {
						System.exit(0);
						return;
					} else if (type == cancelButton) {
						event.consume();
					}

				});
			}
		}

	}

	class HideCLyss implements EventHandler<ActionEvent> {
		public void handle(ActionEvent ave) {
			String i = listView.getSelectionModel().getSelectedItem();
			int index = listView.getSelectionModel().getSelectedIndex();
			if (checkIfNoMap()) {
				return;
			}
			if (listView != null) {

				if (index == 0) {
					i = "Bus";
				} else if (index == 1) {
					i = "Underground";
				} else if (index == 2) {
					i = "Train";
				} else {
					i = "No category selected";
				}

				for (Map.Entry<String, List<Place>> entry : catPlace.entrySet()) {
					if (entry.getKey().equals(i)) {
						List<Place> place = entry.getValue();
						for (Place p : place) {
							System.out.println("Hiding " + entry.getKey());
							p.setVisible(false);

						}
					}
				}

			}
		}

	}

	private boolean checkIfNoMap() {
		if (image == null) {
			Alert msg = new Alert(AlertType.ERROR);
			msg.setContentText("Error - you have to load a map first!");
			msg.setTitle("");
			msg.setHeaderText("ERROR!");
			msg.showAndWait();
			return true;

		}
		return false;

	}

	private boolean checkIfStringIsEmpty(String s) {
		if (s.trim().isEmpty()) {
			Alert msg = new Alert(AlertType.ERROR, "Error - the field can't be empty!");
			msg.setHeaderText("ERROR");
			msg.setTitle("");
			msg.showAndWait();
			return true;

		}
		return false;
	}

	private boolean createPlaceAt(double x, double y, Category c) {
		try {
			System.out.println("HellOWorld: " + x + ", " + y);
			Position p = new Position(x, y);
			if (positionMap.containsKey(p)) {
				Alert msg = new Alert(AlertType.ERROR);
				msg.setContentText("Error - a place already exist on these coordinates");
				msg.setHeaderText("ERROR!");
				msg.setTitle("");
				msg.showAndWait();
				return false;

			}

			if (describedButton.isSelected() && image != null) {
				FormulaDescribed fd = new FormulaDescribed();

				fd.setTitle("New place");
				fd.setHeaderText("Registration off DescribedPlace");
				Optional<ButtonType> result = fd.showAndWait();
				if (result.isPresent() && result.get() == ButtonType.OK) {

					String name = fd.getName();
					String description = fd.getDescription();

					if (checkIfStringIsEmpty(name) || checkIfStringIsEmpty(description)) {
						return false;
					}
					PlaceClickListener listener = new PlaceClickListener();

					DescribedPlace dp = new DescribedPlace(name, description, p, c.getColor(), getSelectedListColor(),
							listener);
					dp.setVisible(true);

					List<Place> sameName = perName.get(name);
					if (sameName == null) {
						sameName = new ArrayList<Place>();
						perName.put(name, sameName);
					}

					String catName = getSelectedListColor().getName();
					List<Place> place = catPlace.get(catName);
					if (place == null) {
						place = new ArrayList<Place>();
						catPlace.put(catName, place);
					}
					place.add(dp);
					sameName.add(dp);
					positionMap.put(p, dp);
					dp.setVisible(true);
					pane.getChildren().add(dp);
					change = true;

				}
			} else if (namedButton.isSelected() && image != null) {
				FormularNamed fn = new FormularNamed();

				fn.setTitle("New place");
				fn.setHeaderText("Registration off NamedPlace");
				Optional<ButtonType> result1 = fn.showAndWait();
				if (result1.isPresent() && result1.get() == ButtonType.OK) {

					String name = fn.getName();
					if (checkIfStringIsEmpty(name)) {
						return false;
					}
					PlaceClickListener listener = new PlaceClickListener();
					NamedPlace np = new NamedPlace(name, p, c.getColor(), listener, getSelectedListColor());
					np.setVisible(true);

					List<Place> sameName = perName.get(name);
					if (sameName == null) {
						sameName = new ArrayList<Place>();
						perName.put(name, sameName);
					}

					String catName = getSelectedListColor().getName();
					List<Place> place = catPlace.get(getSelectedListColor().getName());
					if (place == null) {
						place = new ArrayList<Place>();
						catPlace.put(catName, place);
					}
					place.add(np);
					sameName.add(np);
					positionMap.put(p, np);
					np.setVisible(true);
					pane.getChildren().add(np);
					change = true;

				}
			} else {
				System.out.println("No type selected | DescribedPlace NamedPlace");
				return false;
			}
			imageView.setCursor(Cursor.DEFAULT);
			newButton.setDisable(false);
			listView.getSelectionModel().clearSelection();
			return true;

		} catch (Exception e) {
			System.out.println("Exception at method: createPlaceAt -> " + e.getMessage());
			Alert msg = new Alert(AlertType.ERROR);
			msg.setContentText("Error - wrong input!");
			msg.setHeaderText("ERROR!");
			msg.showAndWait();
			return false;
		}
	}

	private void newButton() {
		if (checkIfNoMap()) {
			return;
		}
		newButton.setDisable(true);
		imageView.setCursor(Cursor.CROSSHAIR);
		currentState = state.createNew;
	}

	private void loadMap() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Välj bildfil");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Bildfiler", "*.jpg", "*.png"),
				new FileChooser.ExtensionFilter("Alla filer", "*.*"));
		File file = fileChooser.showOpenDialog(primaryStage);
		if (file != null) {
			String name = file.getAbsolutePath();
			image = new Image("file:" + name);
			imageView.setImage(image);
			primaryStage.sizeToScene();
			imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, musLyss);
		}
	}

	private void searchButton() {
		if (checkIfNoMap()) {
			return;
		}
		try {
			unMark();
			String namn = searchField.getText();
			List<Place> sameName = perName.get(namn);
			for (Place p : sameName) {
				System.out.println("Found place: " + p);
				p.setVisible(true);
				p.setMarked(true);
				p.update();

				markPlace.add(p);
			}

		} catch (Exception e) {
			System.out.println("Exception at method: searchButton -> " + e.getMessage());
			Alert msg = new Alert(AlertType.ERROR);
			msg.setContentText("Error - the place you searched for doesn't exist!");
			msg.setHeaderText("ERROR!");
			msg.setTitle("");
			msg.showAndWait();
		}
	}

	private void save() {
		if (checkIfNoMap()) {
			return;
		}
		try {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showSaveDialog(primaryStage);
			if (file == null)
				return;
			String filnamn = file.getAbsolutePath();

			FileOutputStream fos = new FileOutputStream(filnamn);
			PrintWriter pw = new PrintWriter(fos);

			for (Place places : positionMap.values()) {

				String placeString = "";
				if (places instanceof NamedPlace) {
					placeString += "Named,";
					placeString += places.getCategory().getName() + ",";
					placeString += places.getPos().getX() + ",";
					placeString += places.getPos().getY() + ",";
					placeString += places.getName();
					change = false;

				} else if (places instanceof DescribedPlace) {
					placeString += "Described,";
					placeString += places.getCategory().getName() + ",";
					placeString += places.getPos().getX() + ",";
					placeString += places.getPos().getY() + ",";
					placeString += places.getName() + ",";
					placeString += (((DescribedPlace) places).getDescription());
					change = false;
				}
				pw.println(placeString);
			}
			pw.close();
			fos.close();

		} catch (FileNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR, e.getMessage());
			alert.showAndWait();
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR, e.getMessage());
			alert.showAndWait();
		}
	}

	private Category getSelectedListColor() {

		Category c = null;

		switch (listView.getSelectionModel().getSelectedIndex()) {
		case 0:
			String name = (String) listView.getSelectionModel().getSelectedItem();
			c = new Category(name, Color.RED);
			break;
		case 1:
			String name1 = (String) listView.getSelectionModel().getSelectedItem();
			c = new Category(name1, Color.BLUE);
			break;
		case 2:
			String name2 = (String) listView.getSelectionModel().getSelectedItem();
			c = new Category(name2, Color.GREEN);
			break;

		default:
			String name3 = "None";
			c = new Category(name3, Color.BLACK);
			break;
		}

		return c;
	}

	class FormulaDescribed extends Alert {
		private TextField nameField = new TextField();
		private TextField description = new TextField();

		public FormulaDescribed() {
			super(AlertType.CONFIRMATION);
			GridPane grid = new GridPane();
			grid.addRow(0, new Label("Name: "), nameField);
			grid.addRow(1, new Label("Description: "), description);

			getDialogPane().setContent(grid);
		}

		public String getName() {
			return nameField.getText();
		}

		public String getDescription() {
			return description.getText();
		}
	}

	class FormularNamed extends Alert {

		TextField nameField = new TextField();

		FormularNamed() {
			super(AlertType.CONFIRMATION);
			GridPane grid = new GridPane();
			grid.addRow(0, new Label("Name: "), nameField);
			getDialogPane().setContent(grid);
		}

		public String getName() {
			return nameField.getText();
		}
	}

	class OperationListener implements EventHandler<ActionEvent> {
		public void handle(ActionEvent ave) {
			if (ave.getSource() == newButton) {
				newButton();
				return;
			} else if (ave.getSource() == hideButton) {
				hideButton();
				return;
			} else if (ave.getSource() == removeButton) {
				removeButton();
				return;
			} else if (ave.getSource() == coordinatesButton) {
				coordinatesButton();
				return;
			} else if (ave.getSource() == searchButton) {
				searchButton();
			}
		}
	}

	private void exit(ActionEvent ave) {
		System.out.println("Stage is closing");
		if (!change) {
			System.exit(0);
		} else if (change) {
			Alert alert = getExitAlert();
			alert.showAndWait().ifPresent(type -> {
				if (type == yesButton) {
					System.exit(0);
				} else if (type == cancelButton) {
					ave.consume();
					return;
				}

			});
		}
	}

	private Alert getExitAlert() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText(
				"Warning - you have unsaved changes that will be lost if you decide to continue. \n\n Are you sure you want to continue?");
		alert.setHeaderText("Confirm Navigation");
		alert.setTitle("");
		yesButton = new ButtonType("Yes", ButtonData.YES);
		cancelButton = new ButtonType("CANCEL", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(yesButton, cancelButton);
		return alert;
	}

	class FileListener implements EventHandler<ActionEvent> {
		public void handle(ActionEvent ave) {
			if (ave.getSource() == f1) {
				if (!change) {
					loadMap();
				} else if (change) {
					Alert alert = getExitAlert();
					alert.showAndWait().ifPresent(type -> {
						if (type == yesButton) {
							clearData();
							loadMap();
							return;
						} else if (type == cancelButton) {
							ave.consume();
						}
					});
				}
			} else if (ave.getSource() == f2) {
				if (!change) {
					loadPlaces();
				} else if (change) {
					Alert alert = getExitAlert();
					alert.showAndWait().ifPresent(type -> {
						if (type == yesButton) {
							loadPlaces();
							return;
						} else if (type == cancelButton) {
							ave.consume();
						}
					});
				}
			} else if (ave.getSource() == f3) {
				save();
				return;
			} else if (ave.getSource() == f4) {
				exit(ave);

			}
		}
	}

	private void loadPlaces() {

		if (checkIfNoMap()) {
			return;
		}

		clearData();

		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(primaryStage);
		if (file == null)
			return;
		String fileName = file.getAbsolutePath();

		ArrayList<Place> places = readFile(fileName, componentLyss);

		for (int i = 0; i < places.size(); i++) {

			if (places.get(i) instanceof NamedPlace) {
				System.out.println("Adding NamedPlace");
				positionMap.put(places.get(i).getPos(), places.get(i));
				places.get(i).setVisible(true);
				pane.getChildren().add(places.get(i));

			} else if (places.get(i) instanceof DescribedPlace) {
				System.out.println("Adding DescribedPlace");
				positionMap.put(places.get(i).getPos(), places.get(i));
				places.get(i).setVisible(true);
				pane.getChildren().add(places.get(i));

			}

			String name1 = places.get(i).getCategory().getName();
			List<Place> place = catPlace.get((places.get(i).getCategory().getName()));
			if (place == null) {
				place = new ArrayList<Place>();
				catPlace.put(name1, place);
			}

			List<Place> sameName = perName.get(places.get(i).getName());
			if (sameName == null) {
				sameName = new ArrayList<Place>();
				perName.put(places.get(i).getName(), sameName);
			}
			sameName.add(places.get(i));
			place.add(places.get(i));

		}

	}

	private void clearData() {

		for (Place p : positionMap.values()) {
			System.out.println("Place removed");
			p.setMarked(false);
			p.update();
			removePlace(p);
		}

		positionMap.clear();
		markPlace.clear();
	}

	private ArrayList<Place> readFile(String filePath, PlaceClickListener componentLyss) {

		try {
			FileReader infil = new FileReader(filePath);
			BufferedReader br = new BufferedReader(infil);
			String line;
			ArrayList<Place> places = new ArrayList<Place>();
			while ((line = br.readLine()) != null) {
				places.add(createPlace(line, componentLyss));

			}

			br.close();
			return places;

		} catch (Exception e) {
			System.out.println("FileReaderException: " + e.getMessage());
			return null;
		}
	}

	private Place createPlace(String line, PlaceClickListener componentLyss) {
		String[] tokens = line.split(",");

		String type = tokens[0];
		String category = tokens[1];
		double x_pos = Double.parseDouble(tokens[2]);
		double y_pos = Double.parseDouble(tokens[3]);
		String name = tokens[4];

		Color c = Color.BLACK;
		switch (category) {
		case "Bus":
			c = Color.RED;
			break;
		case "Underground":
			c = Color.BLUE;
			break;
		case "Train":
			c = Color.GREEN;
			break;
		}

		Position pos = new Position(x_pos, y_pos);

		if (type.equals("Named")) {
			NamedPlace np = new NamedPlace(name, pos, c, componentLyss, getSelectedListColor());
			System.out.print(np);
			return np;
		} else if (type.equals("Described")) {
			String description = tokens[5];
			DescribedPlace dp = new DescribedPlace(name, description, pos, c, getSelectedListColor(), componentLyss);
			System.out.print(dp);
			return dp;
		} else {
			System.out.println("Error - Could not read the file contents!");
			return null;
		}

	}

	class FormularCoordinates extends Alert {

		TextField xField = new TextField();
		TextField yField = new TextField();

		FormularCoordinates() {
			super(AlertType.CONFIRMATION);
			GridPane grid = new GridPane();
			grid.addRow(0, new Label("x: "), xField);
			grid.addRow(1, new Label("y: "), yField);

			getDialogPane().setContent(grid);

		}

		public double getXCoordinate() {
			return Double.parseDouble(xField.getText());
		}

		public double getYCoordinate() {
			return Double.parseDouble(yField.getText());
		}

	}

	private void unMark() {
		for (Place p : markPlace) {
			p.setMarked(false);
			p.update();
			change = true;

		}

		markPlace.clear();
	}

	private void hideButton() {
		if (checkIfNoMap()) {
			return;
		}
		for (Place p : markPlace) {
			System.out.println("Hiding place");
			p.setVisible(false);
			p.setMarked(false);
			p.update();
			change = true;

		}

		markPlace.clear();

	}

	private void removeButton() {
		if (checkIfNoMap()) {
			return;
		}
		for (Place p : markPlace) {
			p.setVisible(false);
			p.setMarked(false);
			p.update();
			removePlace(p);
			positionMap.remove(p.getPos());
			System.out.println("place removed");

		}

		change = true;
		markPlace.clear();
	}

	private void removePlace(Place place) {

		pane.getChildren().remove(place);
		list.remove(place);

		List<Place> sameName = perName.get(place.getName());
		sameName.remove(place);
		if (sameName.isEmpty()) {
			perName.remove(place.getName());

		}

		List<Place> cat = catPlace.get(place.getCategory().getName());
		cat.remove(place);
		if (cat.isEmpty()) {
			catPlace.remove(place.getCategory().getName());
		}

	}

	private void coordinatesButton() {
		try {
			if (checkIfNoMap()) {
				return;
			}
			unMark();

			FormularCoordinates fc = new FormularCoordinates();

			fc.setTitle("Input coordinates:");

			Optional<ButtonType> result = fc.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {

				double x = fc.getXCoordinate();
				double y = fc.getYCoordinate();

				pCoordinates(x, y);

			}

		} catch (Exception e) {
			System.out.println("Exception at method: coordinatesButton -> " + e.getMessage());
			Alert msg = new Alert(AlertType.ERROR);
			msg.setContentText("Error - wrong input!");
			msg.setHeaderText("ERROR!");
			msg.setTitle("");
			msg.showAndWait();
			return;
		}
	}

	private void pCoordinates(double x, double y) {

		Position pos = new Position(x, y);

		if (positionMap.containsKey(pos)) {
			markPlace.clear();
			Place p = positionMap.get(pos);
			System.out.println("Place found: " + p);
			p.setVisible(true);
			p.setMarked(true);
			p.update();
			markPlace.add(p);

		} else {
			Alert msg = new Alert(AlertType.ERROR);
			msg.setContentText("ERROR - no place exist on these coordinates!");
			msg.setHeaderText("ERROR!");
			msg.showAndWait();
			return;

		}
		change = true;
	}

	private void mouseClicked(MouseEvent e) {

		if (currentState == state.createNew) {
			Category c = getSelectedListColor();
			if (createPlaceAt(e.getX(), e.getY(), c)) {
				currentState = state.observe;
			}
		}
	}

	class MusLyss implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent mev) {
			System.out.println("Mouse Click registered");

			Main.this.mouseClicked(mev);
		}
	}

	class PlaceClickListener implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent mev) {
			if (mev.getButton() == MouseButton.SECONDARY) {
				if (mev.getSource() instanceof NamedPlace) {
					NamedPlace np = (NamedPlace) mev.getSource();
					System.out.println(np.getName());

					FormulaDescription fd = new FormulaDescription(np.getName(), np.getPos().getX(),
							np.getPos().getY());
					fd.showAndWait();
					return;

				}
				if (mev.getSource() instanceof DescribedPlace) {
					DescribedPlace dp = (DescribedPlace) mev.getSource();
					System.out.println(dp.getName());

					FormulaDescription1 fd1 = new FormulaDescription1(dp.getName(), dp.getPos().getX(),
							dp.getPos().getY(), dp.getDescription());
					fd1.showAndWait();

					return;

				}

			}

			if (mev.getButton() == MouseButton.PRIMARY) {
				if (currentState == state.observe) {
					if (mev.getSource() instanceof Place) {

						Place p = (Place) mev.getSource();
						boolean state = !p.getMarked();
						p.setMarked(state);
						p.update();

						if (state == true) {
							System.out.println("adding mark");
							markPlace.add(p);
						} else {
							System.out.println("Removing mark");
							markPlace.remove(p);

						}
					}

					change = true;
				}

			}

		}

	}

}

class FormulaDescription extends Alert {
	FormulaDescription(String name, double x, double y) {
		super(AlertType.INFORMATION);

		String description = name + "(" + x + ", " + y + ")";
		setTitle("Information");
		setHeaderText("Information about namedPlace");
		setContentText(description);

	}

}

class FormulaDescription1 extends Alert {
	double x, y;

	Position p = new Position(x, y);

	FormulaDescription1(String name, double x, double y, String description) {
		super(Alert.AlertType.INFORMATION);

		String description1 = name + "(" + x + ", " + y + ")\n" + description;
		setTitle("Informaion");
		setHeaderText("Information about describedPlace");
		setContentText(description1);

	}
}
