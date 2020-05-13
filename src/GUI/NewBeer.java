package GUI;

import beer.Beer;
import beer.BeerRegister;
import beer.Instructions;
import beer.SpecificInstruction;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.text.WordUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class NewBeer {
    private final BorderPane pane = new BorderPane();
    private final BeerRegister register = Controller.getRegister();
    private ArrayList<Instructions> instructionsList;
    private ObservableList<Instructions> tableWrapper;
    private Instructions selectedInstruction = null;
    private String name;
    private String type;

    public BorderPane getPane(LocalDateTime startTime) {
        instructionsList = new ArrayList<>();

        GridPane grid = new GridPane();
        Label name = new Label("Navn: ");
        Label type = new Label("Type: ");
        TextField nameField = new TextField();
        TextField typeField = new TextField();
        nameField.setPromptText("navn");
        typeField.setPromptText("f.eks. lager eller IPA");
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(typeField, Priority.ALWAYS);

        grid.add(name, 0,0);
        grid.add(nameField,1,0);
        grid.add(type, 2, 0);
        grid.add(typeField, 3, 0);
        grid.setHgap(10);

        Label title = new Label("Lag en ny type mekk");
        title.setId("Title");
        Label startTimeLbl = new Label("Starttid: " + startTime.toString());
        TableView<Instructions> tableView = createTable();
        Button addInstructionBtn = new Button("Legg til instruksjon");
        Button editInstructionBtn = new Button("Rediger instruksjon");
        Button deleteInstructionBtn = new Button("Fjern instruksjon");
        HBox btnBox = new HBox(10);
        btnBox.getChildren().addAll(addInstructionBtn, editInstructionBtn, deleteInstructionBtn);
        btnBox.setAlignment(Pos.CENTER);
        Button saveInDBBtn = new Button("Klar, ferdig, mekk!");

        addInstructionBtn.setOnAction(e -> instructionsWindow("Legg til"));
        editInstructionBtn.setOnAction(e -> {
            if(selectedInstruction != null) instructionsWindow("Rediger");
        });
        deleteInstructionBtn.setOnAction(e -> {
            if(selectedInstruction != null){
                instructionsList.remove(selectedInstruction);
                updateTable();
            }
        });
        saveInDBBtn.setOnAction(e -> {
            System.out.println("Skal lagre alt i DB");
            if(nameField.getText().trim().isEmpty() || typeField.getText().trim().isEmpty()){
                System.out.println(nameField.getText());
                Dialog dialog = new Dialog("info", "Navn og type mangler", "Du har glemt å fylle inn navn og type");
                dialog.display();
            }else if(!instructionsOK()){
                new Dialog("info", "Upsi!", "Programmet er ikke supert ennå, så du får ikke lov til å ha flere instruksjoner med" +
                        "samme kombinasjon av dager og timer. Rediger og prøv igjen").display();
            }else if(instructionsList.size() == 0) {
                Dialog dialog = new Dialog("info", "Mangler instruksjoner", "Legg til noen instruksjoner før du lagrer ølen");
                dialog.display();
            }else if(!register.okName(nameField.getText())) {
                Dialog dialog = new Dialog("info", "Navn i bruk", "Navnet du valgte er i bruk allerede. Prøv et annet navn");
                dialog.display();
            }else {
                Beer beer = new Beer(nameField.getText(), typeField.getText(), startTime);
                System.out.println(beer.toString());
                register.addNewBeer(beer);
                System.out.println(register.findBeer(beer).toString());
                for (Instructions instr : instructionsList) {
                    instr.setBeerName(beer.getName());
                    register.addInstructionToBeer(instr.getDescription(), instr.getDaysAfterStart(), instr.getHours(), beer.getName());
                }
                for (Instructions i : register.getInstructionsForBeer(beer.getName())) {
                    SpecificInstruction instruction = new SpecificInstruction(i.getInstructionId(), beer.getId());
                    register.addSpecificInstruction(instruction);
                }
                register.addNotesToBeer(beer.getName());
                Controller.goToShowMaking(e, beer);

            }
        });

        VBox contents = new VBox(10);
        contents.getChildren().addAll(startTimeLbl, grid, tableView, btnBox, saveInDBBtn);
        contents.setPadding(new Insets(10, 0, 10, 0)); // Get some space between the title and the contents
        contents.setAlignment(Pos.CENTER);
        ScrollPane scrollBox = new ScrollPane(contents);
        scrollBox.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollBox.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollBox.setFitToWidth(true);


        // TODO: planen
        //  - opprett Beer-Object med tom konstruktør, eller sånn her new Beer("temp%20Name", "temp%20type", startTime);
        //  - opprett Instructions-objekter og bruk navn="temp%20Name"
        //  - legg Instructions-objektene i en List i bakgrunnen (som så brukes til å fylle en tabell)
        //  - la folk legge til og endre på Instructions-objektene, samt fjerne objekter fra lista i bakrunnen (marker øl i lista før endre og slett)
        //  - når folk trykker på "Klar, ferdig, mekk!"
        //      * sjekk om navnet er ledig og ok (foreslå <ønsket_navn>#<tall>, <ønsket_navn>_v2.0 eller et helt nytt forsøk)
        //          - if(getAllBeerTypes().contains(<ønsket_navn>) dialog.display();
        //      * beerObj.setName(nameField.getText())
        //      * beerObj.setType(typeField.getText())
        //      * register.addNewBeer(beerObj)
        //      * instructionsList.foreach(instr -> register.addInstructionToBeer(instr.getDescription(), instr.getDaysAfterStart(), instr.getHours(), beerObj.getName()))
        //      * add specific instructions (see Home --> makeAgainBtn.setOnAction())
        //      * goToShowMaking(event, beerObj) [event fra trykket på knappen]

        pane.setTop(title);
        pane.setCenter(scrollBox);
        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        pane.setPadding(new Insets(10,10,10,10)); // top, right, bottom, left

        return pane;
    }

    private boolean instructionsOK(){
        ArrayList<Integer> listOfValues = new ArrayList<>();
        for(Instructions instr: instructionsList){
            String code = ""+instr.getDaysAfterStart()+instr.getHours();
            Integer codeInt = Integer.parseInt(code);
            if(listOfValues.size() == 0){
                listOfValues.add(codeInt);
            }else if(listOfValues.contains(codeInt)){
                return false;
            }else{
                listOfValues.add(codeInt);
            }
        }
        return true;
    }

    private TableView<Instructions> createTable(){
        // Set up the columns
        TableColumn<Instructions, String> descriptionColumn = new TableColumn<>("Hva");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description")); // Q: Hvordan få til flere linjer på en rad??
        descriptionColumn.setCellFactory(column->{
            return new TableCell<Instructions, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item==null || empty) {
                        setGraphic(null);
                    } else {
                        // NB: dette funker ift til å få flere linjer, men wrapLength er lik om det er lite eller stort vindu
                        //     Funker ikke med fixedCellSize for å regne ut høyden på tabellen nå...
                        VBox vbox = new VBox();
                        String F = WordUtils.wrap(item, 45);
                        String[] F1 =  F.split(System.lineSeparator());
                        for(String s: F1){
                            vbox.getChildren().add(new Label(s));
                        }
                        setGraphic(vbox);
                    }
                }
            };
        });
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
        Label placeHolderTxt = new Label("Legg til din første instruksjon ved å trykke på knappen 'Legg til instruksjon' \nunder. " +
                "Instruksjonene blir vist i en liste her og lagres \nved å trykke på knappen 'Klar, ferdig, mekk!'");
        placeHolderTxt.setTextAlignment(TextAlignment.CENTER);
        tableView.setPlaceholder(placeHolderTxt);
        tableView.setItems(getTableWrapper());
        tableView.getColumns().addAll(descriptionColumn, daysColumn, hoursColumn);

        //tableView.setFixedCellSize(25);
        //tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.10)));
        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY ); //Constrained_resize_policy
        descriptionColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 50 ); // 50% width
        daysColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 30 ); // 30% width
        hoursColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 20 ); // 20% width
        tableView.setPrefHeight(150); //300 ok

        // Add listener for clicks on row
        tableView.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY){
                Instructions selected = tableView.getSelectionModel().getSelectedItem();

                if(mouseEvent.getClickCount() == 1){
                    selectedInstruction = selected;
                }else if(mouseEvent.getClickCount() == 2){
                    //Noe
                    System.out.println("Du dobbeltklikka på " + selectedInstruction.toString());
                }
            }
        });

        return tableView;
    }

    private ObservableList<Instructions> getTableWrapper(){
        tableWrapper = FXCollections.observableArrayList(instructionsList);
        return tableWrapper;
    }
    private void updateTable(){
        this.tableWrapper.setAll(instructionsList);
    }

    private void instructionsWindow(String whatToDo){
        Stage stage = new Stage();

        Label title = new Label(whatToDo + " instruksjon");
        title.setId("Title");
        Label description = new Label("Beskrivelse (hva skal gjøres):");
        Label days = new Label("Dager etter start:");
        Label hours = new Label("Timer:");
        TextField descriptionField = new TextField();
        TextField daysField = new TextField();
        TextField hoursField = new TextField();
        descriptionField.setPrefWidth(Double.MAX_VALUE);
        daysField.setPrefWidth(30);
        hoursField.setPrefWidth(60);
        Button saveBtn = new Button("Lagre");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);

        gridPane.add(description, 0, 0,2,1);
        gridPane.add(descriptionField, 0, 1, 6, 1);
        gridPane.add(days, 0, 2);
        gridPane.add(daysField, 1, 2);
        gridPane.add(hours, 3, 2);
        gridPane.add(hoursField, 4, 2);

        VBox contents = new VBox(20);
        contents.getChildren().addAll(title, gridPane, saveBtn);
        contents.setAlignment(Pos.CENTER);
        contents.setPadding(new Insets(10,10,10,10));

        if(whatToDo.equals("Rediger")){
            descriptionField.setText(selectedInstruction.getDescription());
            daysField.setText(String.valueOf(selectedInstruction.getDaysAfterStart()));
            hoursField.setText(String.valueOf(selectedInstruction.getHours()));
        }

        saveBtn.setOnAction(e -> {
            try {
                if (whatToDo.equals("Legg til")) {
                    Instructions newInstruction = new Instructions(descriptionField.getText(), Integer.parseInt(daysField.getText()), Integer.parseInt(hoursField.getText()), "temp%20Name");
                    instructionsList.add(newInstruction);
                } else if (whatToDo.equals("Rediger")) {
                    selectedInstruction.setDescription(descriptionField.getText());
                    selectedInstruction.setDaysAfterStart(Integer.parseInt(daysField.getText()));
                    selectedInstruction.setHours(Integer.parseInt(hoursField.getText()));
                }
                updateTable();
                stage.close();
            }catch (Exception ex){
                Dialog dialog = new Dialog("info", "Feil i inndata", "Feil skjedde. Prøv igjen. Dager og timer skal være heltall (1,2,3,..)");
                dialog.display();
            }
        });

        stage.setTitle("MH -- " + whatToDo); // whatToDo = "Legg til" V "Rediger"
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(contents, 500, 300);
        scene.getStylesheets().add("GUI/styles.css");
        stage.setScene(scene);
        stage.show();
    }
}
