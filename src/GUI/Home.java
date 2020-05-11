package GUI;

import beer.Beer;
import beer.BeerRegister;
import beer.Instructions;
import beer.SpecificInstruction;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;


public class Home {
    private final BorderPane pane = new BorderPane();
    private TableView<Beer> tableView;
    private String selectedBeerName;
    private String selectedBeerType;
    private final BeerRegister register = Controller.getRegister();
    private ObservableList<Beer> homeTableWrapper;

    public BorderPane getPane(){
        Label title = new Label("PÅ TIDE Å MEKKE LITT?");
        title.setId("Title");
        Label madeTheMost = new Label("Mekket mest: " + register.mostMadeBeer());
        tableView = createHomeTable();
        Button newMakingBtn = new Button("Ny mekking");
        newMakingBtn.setOnAction(e -> {
            System.out.println(selectedBeerName);
            showNewMakingWindow();
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
        ScrollPane scrollPane = new ScrollPane(centerBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        // Aligning stuff
        pane.setTop(title);
        pane.setCenter(scrollPane);
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
        //beerNameColumn.setMinWidth(200);
        TableColumn<Beer, String> beerTypeColumn = new TableColumn<>("Type");
        beerTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        //beerTypeColumn.setMinWidth(130);
        TableColumn<Beer, Integer> timesMadeColumn = new TableColumn<>();
        timesMadeColumn.setStyle("-fx-alignment: center;");
        Label title = new Label("Antall ganger mekket");
        title.setWrapText(true);
        timesMadeColumn.setGraphic(title);
        timesMadeColumn.setCellValueFactory(data -> register.noOfTimes(data.getValue().getName()));
        timesMadeColumn.setMinWidth(148);
        timesMadeColumn.getStyleClass().add("center_aligned");

        // Create the TableView instance
        tableView = new TableView<>();
        tableView.setItems(getHomeTableWrapper());
        tableView.getColumns().addAll(beerNameColumn, beerTypeColumn, timesMadeColumn);
        tableView.setStyle("-fx-wrap-text: true");
        tableView.setFixedCellSize(25);
        tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));
        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        beerNameColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 50 ); // 50% width
        beerTypeColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 30 ); // 30% width
        timesMadeColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 20 ); // 20% width


        // Add listener for clicks on row
        tableView.setOnMousePressed(mouseEvent -> {
            if(mouseEvent.isPrimaryButtonDown()){
                String beerName = tableView.getSelectionModel().getSelectedItem().getName();
                String beerType = tableView.getSelectionModel().getSelectedItem().getType();

                if(mouseEvent.getClickCount() == 1){
                    selectedBeerName = beerName;
                    selectedBeerType = beerType;
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

    private void showNewMakingWindow(){
        Stage stage = new Stage();

        VBox contents = new VBox(10);;
        Platform.runLater(contents::requestFocus);
        Label title = new Label("Lag ny mekk");
        title.setId("Title");
        Label markedBeer;
        if(selectedBeerName == null){
            markedBeer = new Label("Markert øl: ingen øl er markert ennå");
        } else{
            markedBeer = new Label("Markert øl: " + selectedBeerName);
        }
        Label startTime = new Label("Starttid: ");
        TextField startTimeField = new TextField();
        startTimeField.setPromptText("åååå-mm-ddThh:mm");
        //startTimeField.setFocusTraversable(false); // Alternativ til Platform.runLater-greia
        System.out.println(startTimeField.getText());
        Button makeAgainBtn = new Button("Lag markert øl");
        Button makeNewBtn = new Button("Lag ny type øl");
        HBox startBox = new HBox(startTime, startTimeField);
        HBox makeBox = new HBox(makeAgainBtn, makeNewBtn);
        contents.getChildren().addAll(title, markedBeer, startBox, makeBox);
        startBox.setAlignment(Pos.CENTER);
        makeBox.setAlignment(Pos.CENTER);
        contents.setAlignment(Pos.CENTER);


        var ref = new Object() {
            LocalDateTime dateTime;
        };
        makeAgainBtn.setOnAction(e -> {
            try {
                ref.dateTime = LocalDateTime.parse(startTimeField.getText()); // Fort gjort å skrive inn feil, så har lagt dette inn i en try-catch

                if(!(selectedBeerName == null)) {
                    System.out.println(ref.dateTime);
                    Beer beer = new Beer(selectedBeerName, selectedBeerType, ref.dateTime);
                    register.addNewBeer(beer);
                    System.out.println(beer.getId());
                    for (Instructions instructions : register.getInstructionsForBeer(selectedBeerName)) {
                        //register.addInstructionToBeer(instructions.getDescription(), instructions.getDaysAfterStart(), instructions.getHours(), instructions.getBeerName());
                        register.addSpecificInstruction(new SpecificInstruction(instructions.getInstructionId(), beer.getId()));
                    }
                    updateHomeTable();
                    stage.close();
                }else{
                    Dialog dialog = new Dialog("info", "Ingen øl er markert", "Du må markere en øl før du kan benytte deg av denne funksjonen");
                    dialog.display();
                }

            }catch (Exception ex){
                Dialog dialog = new Dialog("info", "Feil inndata",
                        "Det skjedde en feil ved innføring av starttid. Det er viktig at du skriver det akkurat på formen " +
                                "åååå-MM-ddThh:mm hvor åååå=årstall, MM=månedsnummer, dd=dag, T=bare skriv den, hh=time, mm=minutt");
                dialog.display();
            }
        });

        makeNewBtn.setOnAction(e -> {
            try{
                ref.dateTime = LocalDateTime.parse(startTimeField.getText()); // Fort gjort å skrive inn feil, så har lagt dette inn i en try-catch

                // Kode kommer her

            }catch (Exception ex){
                Dialog dialog = new Dialog("info", "Feil inndata",
                        "Det skjedde en feil ved innføring av starttid. Det er viktig at du skriver det akkurat på formen " +
                                "åååå-MM-ddThh:mm hvor åååå=årstall, MM=månedsnummer, dd=dag, T=bare skriv den, hh=time, mm=minutt");
                dialog.display();
            }
        });


        Scene scene = new Scene(contents, 300, 300);
        scene.getStylesheets().add("GUI/styles.css");
        stage.setTitle("MH -- Ny mekking");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }
}
