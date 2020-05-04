package GUI;

import GUI.Controller;
import beer.Beer;
import beer.BeerRegister;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class Home {
    private final BorderPane pane = new BorderPane();
    private TableView<Beer> tableView;
    private String selectedBeerName;
    private final BeerRegister register = Controller.getRegister();
    private ObservableList<Beer> homeTableWrapper;

    public BorderPane getPane(){
        Label title = new Label("PÅ TIDE Å MEKKE LITT?");
        title.setFont(Font.font("Calibri", FontWeight.BOLD,18));
        Label madeTheMost = new Label("Mekket mest: lager");
        tableView = createHomeTable();
        Button newMakingBtn = new Button("Ny mekking");
        newMakingBtn.setOnAction(e -> {
            System.out.println(selectedBeerName);
        });
        Button ongoingMakingsBtn = new Button("Se pågående mekkinger");
        ongoingMakingsBtn.setOnAction(Controller::goToMakings);

        // Set up the layout
        // Helping panes
        VBox centerBox = new VBox(10);
        HBox buttonContainer = new HBox(10);
        buttonContainer.getChildren().addAll(newMakingBtn, ongoingMakingsBtn);
        buttonContainer.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(madeTheMost, tableView, buttonContainer);
        centerBox.setAlignment(Pos.CENTER);
        // Aligning stuff
        pane.setTop(title);
        pane.setCenter(centerBox);
        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        pane.setPadding(new Insets(10,10,10,10)); // top, right, bottom, left

        return pane;
    }

    public void setCenterArea(TableView<Beer> tableView){
        this.tableView = tableView;
    }

    private TableView<Beer> createHomeTable(){
        // Set up the columns
        TableColumn<Beer, String> beerNameColumn = new TableColumn<>("Navn");
        beerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        beerNameColumn.setMinWidth(200);
        TableColumn<Beer, String> beerTypeColumn = new TableColumn<>("Type");
        beerTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        beerTypeColumn.setMinWidth(130);
        TableColumn<Beer, Integer> timesMadeColumn = new TableColumn<>();
        Label title = new Label("Antall ganger mekket");
        title.setWrapText(true);
        timesMadeColumn.setGraphic(title);
        timesMadeColumn.setCellValueFactory(data -> register.noOfTimes(data.getValue().getName()));
        timesMadeColumn.setMinWidth(148);

        // Create the TableView instance
        tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(register.getAllBeerTypesProperty()));
        tableView.getColumns().addAll(beerNameColumn, beerTypeColumn, timesMadeColumn);
        tableView.setStyle("-fx-wrap-text: true");
        tableView.setMaxWidth(480);
        //homeTable.setMaxHeight(200);

        // Add listener for clicks on row
        tableView.setOnMousePressed(mouseEvent -> {
            if(mouseEvent.isPrimaryButtonDown()){
                String beerName = tableView.getSelectionModel().getSelectedItem().getName();

                if(mouseEvent.getClickCount() == 1){
                    selectedBeerName = beerName;
                }else if(mouseEvent.getClickCount() == 2){
                    System.out.println("--> Gonna show you the recipe for " + selectedBeerName);
                    Controller.goToShowRecipe(mouseEvent, selectedBeerName);
                }
            }
        });

        return tableView;
    }

    private ObservableList<Beer> getHomeTableWrapper() {
        // create an observable list from the different types of beer
        homeTableWrapper = FXCollections.observableArrayList(register.getAllBeerTypesProperty());
        return homeTableWrapper;
    }

    public void updateHomeTable(){ // TODO: use this when adding and editing a type of beer
        this.homeTableWrapper.setAll(register.getAllBeerTypesProperty());
    }
}
