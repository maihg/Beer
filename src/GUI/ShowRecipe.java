package GUI;

import beer.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;

public class ShowRecipe {

    private final BorderPane pane = new BorderPane();
    private final BeerRegister register = Controller.getRegister();
    private String beerName;
    private Beer selectedBeer;

    public BorderPane getPane(String beerName) {
        this.beerName = beerName;
        Notes notes = register.findNotes(beerName);
        Label title = new Label(beerName);
        title.setId("Title");
        Label title1 = new Label("Instruksjoner");
        title1.setId("Subtitle");
        TableView<Instructions> tableView = createTable();
        Button changeInstructionBtn = new Button("Endre instruksjon"); // Knapp her for å endre på instruksjoner
        Label title2 = new Label("Alle ferdige mekkinger av denne ølen");
        title2.setId("Subtitle");
        TableView<Beer> oldMakingsTable = createTableOldMakings();
        Label title3 = new Label("Notater");
        title3.setId("Subtitle");
        TextArea notesArea = new TextArea(notes.getNotes());
        notesArea.setEditable(true);
        // Vil at området bare skal gjøre seg større istedenfor å få en scrollbar
        Button updateNotes = new Button("Lagre notatendringer");
        updateNotes.setOnAction(e -> {
            String newNotes = notesArea.getText();
            register.editNotes(newNotes, beerName);
            Dialog dialog = new Dialog("info", "Endringer lagret", "Dine notatendringer er nå lagret");
            dialog.display();
        });

        VBox centerBox = new VBox(10);
        centerBox.getChildren().addAll(title1, tableView, changeInstructionBtn, title2, oldMakingsTable, title3, notesArea, updateNotes);
        ScrollPane scrollPane = new ScrollPane(centerBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        //scrollPane.fitToWidthProperty().setValue(true);
        scrollPane.setFitToWidth(true);
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
        daysColumn.getStyleClass().add("center_aligned");
        TableColumn<Instructions, Integer> hoursColumn = new TableColumn<>("Timer");
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("hours"));
        hoursColumn.setStyle("-fx-alignment: center;");
        hoursColumn.getStyleClass().add("center_aligned");

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

    private TableView<Beer> createTableOldMakings(){
        // Set up the columns
        TableColumn<Beer, String> nameColumn = new TableColumn<>("Navn");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Beer, LocalDateTime> startColumn = new TableColumn<>("Starttid");
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        // Create the table instance
        TableView<Beer> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(register.getMakingsOfType(beerName)));
        tableView.getColumns().addAll(nameColumn, startColumn);
        tableView.setFixedCellSize(25);
        tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));
        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        nameColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 50 ); // 50% width
        startColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 50 ); // 50% width

        // Add listener for clicks on row
        tableView.setOnMousePressed(mouseEvent -> {
            if(mouseEvent.isPrimaryButtonDown()){
                Beer selected = tableView.getSelectionModel().getSelectedItem();

                if(mouseEvent.getClickCount() == 1){
                    selectedBeer = selected;
                }else if(mouseEvent.getClickCount() == 2){
                    System.out.println("--> Gonna go to specific making ");
                    Controller.goToShowMaking(mouseEvent, selectedBeer);
                }
            }
        });

        return tableView;
    }
}
