package GUI;

import beer.Beer;
import beer.BeerRegister;
import beer.Instructions;
import beer.Notes;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ShowRecipe {

    private final BorderPane pane = new BorderPane();
    private final BeerRegister register = Controller.getRegister();
    private String beerName;

    public BorderPane getPane(String beerName) {
        this.beerName = beerName;
        Notes notes = register.findNotes(beerName);
        Label title = new Label(beerName);
        title.setFont(Font.font("Calibri", FontWeight.BOLD,18));
        TableView<Instructions> tableView = createTable();
        Button changeInstructionBtn = new Button("Endre instruksjon"); // Knapp her for å endre på instruksjoner
        TextArea notesArea = new TextArea(notes.getNotes());
        notesArea.setEditable(true);
        notesArea.setWrapText(true);
        // Vil at området bare skal gjøre seg større istedenfor å få en scrollbar
        Button updateNotes = new Button("Lagre notatendringer");
        updateNotes.setOnAction(e -> {
            String newNotes = notesArea.getText();
            register.editNotes(newNotes, beerName);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText("Endringer lagret");
            String s ="Dine notatendringer er nå lagret";
            alert.setContentText(s);
            alert.show();
        });

        VBox centerBox = new VBox(10);
        centerBox.getChildren().addAll(tableView, changeInstructionBtn, notesArea, updateNotes);
        ScrollPane scrollPane = new ScrollPane(centerBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.fitToWidthProperty().setValue(true);
        pane.setTop(title);
        pane.setCenter(scrollPane);
        pane.setPadding(new Insets(10,10,10,10));
        BorderPane.setAlignment(title, Pos.TOP_CENTER);

        return pane;
    }

    private TableView<Instructions> createTable(){
        // Set up the columns
        TableColumn<Instructions, String> descriptionColumn = new TableColumn<>("Hva");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Instructions, Integer> daysColumn = new TableColumn<>("Dager etter start");
        daysColumn.setCellValueFactory(new PropertyValueFactory<>("daysAfterStart"));
        daysColumn.setStyle("-fx-alignment: center;");
        TableColumn<Instructions, Integer> hoursColumn = new TableColumn<>("Timer");
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("hours"));
        hoursColumn.setStyle("-fx-alignment: center;");

        // Create the table instance
        TableView<Instructions> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(register.getInstructionsForBeer(beerName)));
        tableView.getColumns().addAll(descriptionColumn, daysColumn, hoursColumn);
        tableView.setFixedCellSize(25);
        tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));
        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        descriptionColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 50 ); // 50% width
        daysColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 30 ); // 30% width
        hoursColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 20 ); // 20% width

        return tableView;
    }
}
