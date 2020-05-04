package GUI;

import beer.Beer;
import beer.BeerRegister;
import beer.Instructions;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ShowRecipe {

    private final BorderPane pane = new BorderPane();
    private final BeerRegister register = Controller.getRegister();
    private String beerName;

    public BorderPane getPane(String beerName) {
        this.beerName = beerName;
        Label title = new Label(beerName);
        title.setFont(Font.font("Calibri", FontWeight.BOLD,18));
        TableView<Instructions> tableView = createTable();
        // Trenger en knapp her for å endre på instruksjoner

        pane.setTop(title);
        pane.setCenter(tableView);
        pane.setPadding(new Insets(10,10,10,10));
        BorderPane.setAlignment(title, Pos.TOP_CENTER);

        return pane;
    }

    private TableView<Instructions> createTable(){
        // Set up the columns
        TableColumn<Instructions, String> descriptionColumn = new TableColumn<>("Hva");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setMinWidth(200);
        TableColumn<Instructions, Integer> daysColumn = new TableColumn<>("Dager etter start");
        daysColumn.setCellValueFactory(new PropertyValueFactory<>("daysAfterStart"));
        daysColumn.setMinWidth(139);
        TableColumn<Instructions, Integer> hoursColumn = new TableColumn<>("Timer");
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("hours"));
        hoursColumn.setMinWidth(139);

        // Create the table instance
        TableView<Instructions> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(register.getInstructionsForBeer(beerName)));
        tableView.getColumns().addAll(descriptionColumn, daysColumn, hoursColumn);

        return tableView;
    }
}
