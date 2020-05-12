package GUI;

import beer.BeerRegister;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;

public class NewBeer {
    private final BorderPane pane = new BorderPane();
    private final BeerRegister register = Controller.getRegister();

    public BorderPane getPane(LocalDateTime startTime) {
        Label title = new Label("Lag en ny type mekk");
        title.setId("Title");

        VBox contents = new VBox(10);
        contents.getChildren().addAll(getInfoPane(), new Label(startTime.toString()));
        contents.setPadding(new Insets(10, 0, 10, 0)); // Get some space between the title and the contents
        // TODO: add contents to a scrollPane

        pane.setTop(title);
        pane.setCenter(contents);
        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        pane.setPadding(new Insets(10,10,10,10)); // top, right, bottom, left

        return pane;
    }

    private GridPane getInfoPane(){
        GridPane grid = new GridPane();
        Label name = new Label("Navn: ");
        Label type = new Label("Type: ");
        TextField nameField = new TextField();
        TextField typeField = new TextField();
        nameField.setPromptText(".. skriv et dritkult navn her");
        typeField.setPromptText("Lager, IPA, ...");
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(typeField, Priority.ALWAYS);

        grid.add(name, 0,0);
        grid.add(nameField,1,0);
        grid.add(type, 2, 0);
        grid.add(typeField, 3, 0);
        grid.setHgap(10);

        return grid;
    }
}
