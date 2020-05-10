package GUI;

import beer.Beer;
import beer.BeerRegister;
import beer.Instructions;
import beer.SpecificInstruction;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ShowMaking {
    private final BorderPane pane = new BorderPane();
    private Beer selectedBeer;
    private int instructionId;
    private final BeerRegister register = Controller.getRegister();
    private ObservableList<Instructions> tableWrapper;
    private boolean readyBoolean = false;

    public BorderPane getPane(Beer beer) {
        this.selectedBeer = beer;
        this.readyBoolean = register.ready(selectedBeer);
        Label title = new Label(beer.getName());
        title.setId("Title");
        TableView<Instructions> tableView = createTable();
        TextArea notesArea = new TextArea();
        notesArea.setText(register.findBeer(selectedBeer).getNotes());
        notesArea.setEditable(true);Button updateNotes = new Button("Lagre notatendringer");
        updateNotes.setOnAction(e -> {
            selectedBeer.setNotes(notesArea.getText());
            register.editBeer(selectedBeer);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText("Endringer lagret");
            String s ="Dine notatendringer er nå lagret";
            alert.setContentText(s);
            DialogPane alertPane = alert.getDialogPane();
            alertPane.getStylesheets().add("GUI/styles.css");
            alert.show();
        });
        GridPane valuesPane = getValuesPane();


        VBox centerBox = new VBox(10);
        centerBox.getChildren().addAll(tableView, notesArea,updateNotes, valuesPane);
        ScrollPane scrollPane = new ScrollPane(centerBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        pane.setTop(title);
        pane.setCenter(scrollPane);
        pane.setPadding(new Insets(10,10,10,10));

        return pane;
    }

    private GridPane getValuesPane(){
        Label val1 = new Label("FG1: ");
        Label val2 = new Label("FG2: ");
        Label val3 = new Label("OG: ");
        Label obs1 = new Label(String.valueOf(selectedBeer.getValue1()));
        Label obs2 = new Label(String.valueOf(selectedBeer.getValue2()));
        Label obs3 = new Label(String.valueOf(selectedBeer.getOG()));
        //obs1.textProperty().bind(new SimpleDoubleProperty(value1).asString());
        //obs2.textProperty().bind(new SimpleDoubleProperty(value2).asString()); // bind(val2Input.textProperty) vil gjøre sånn at den viser hva som står i tekstfielden
        TextField val1Input = new TextField();
        TextField val2Input = new TextField();
        TextField val3Input = new TextField();
        val1Input.setPromptText("Ny verdi");
        val2Input.setPromptText("Ny verdi");
        val3Input.setPromptText("Ny verdi");
        Button changeVal1Btn = new Button("Endre FG1");
        Button changeVal2Btn = new Button("Endre FG2");
        Button changeVal3Btn = new Button("Registrer OG");
        Label ready = new Label(readyBoolean ? "Klar for tapping!":"-");
        //ready.setFont(Font.font("Calibri", FontWeight.BOLD,15));
        ready.setId("Subtitle");
        ready.setStyle(readyBoolean ? "-fx-text-fill: darkorange" : "");

        Label changeLabel = new Label("Ny starttid");
        TextField changeFieldDay = new TextField();
        changeFieldDay.setPromptText("åååå-mm-ddThh:mm");
        Button changeStartTime = new Button("Endre starttid");

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
        var ref = new Object() {
            LocalDateTime dateTime;
        };
        changeStartTime.setOnAction(e -> {
            System.out.println(changeFieldDay.getText());
            ref.dateTime = LocalDateTime.parse(changeFieldDay.getText());
            selectedBeer.setStartTime(ref.dateTime);
            register.editBeer(selectedBeer);
            updateTable();
            System.out.println("Fra beer-objekt i klassen: " + selectedBeer.getStartTime());
        });

        changeVal1Btn.setOnAction(e -> {
            selectedBeer.setValue1(Double.parseDouble(val1Input.getText()));
            register.editBeer(selectedBeer);
            obs1.setText(val1Input.getText());
            val1Input.clear();
            updateValues(ready);
        });
        changeVal2Btn.setOnAction(e -> {
            selectedBeer.setValue2(Double.parseDouble(val2Input.getText()));
            register.editBeer(selectedBeer);
            obs2.setText(val2Input.getText());
            val2Input.clear();
            updateValues(ready);
        });

        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.add(ready, 0,0,3,1); // col, row, colspan, rowspan
        pane.add(changeLabel, 0, 1);
        pane.add(changeFieldDay, 2,1);
        pane.add(changeStartTime, 3, 1);
        pane.add(val3, 0,2);
        pane.add(obs3, 1, 2);
        pane.add(val3Input, 2, 2);
        pane.add(changeVal3Btn, 3, 2);
        pane.add(val1, 0,3); // col, row
        pane.add(obs1, 1, 3);
        pane.add(val1Input, 2, 3);
        pane.add(changeVal1Btn, 3, 3);
        pane.add(val2, 0, 4);
        pane.add(obs2, 1, 4);
        pane.add(val2Input, 2,4);
        pane.add(changeVal2Btn,3, 4);

        return pane;
    }

    private void updateValues(Label ready){
        this.readyBoolean = register.ready(selectedBeer);
        ready.setText(readyBoolean ? "Klar for tapping!":"-");
    }

    private TableView<Instructions> createTable(){
        // Set up the columns
        TableColumn<Instructions, String> descriptionColumn = new TableColumn<>("Hva");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Instructions, LocalDateTime> whenColumn = new TableColumn<>("Når");
        whenColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(selectedBeer.getStartTime().plusDays(data.getValue().getDaysAfterStart()).plusHours(data.getValue().getHours())));
        //daysColumn.setStyle("-fx-alignment: center;");
        TableColumn<Instructions, String> doneColumn = new TableColumn<>("Gjort");
        doneColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(register.findSpecificInstruction(data.getValue().getInstructionId(), selectedBeer.getId()).isDone() ? "Ja":"Nei")); // Er noe som heter CheckBox. Kan nok være fint å bruke
        doneColumn.setStyle("-fx-alignment: center;");
        doneColumn.getStyleClass().add("center_aligned");

        // Create the table instance
        TableView<Instructions> tableView = new TableView<>();
        tableView.setItems(getTableWrapper());
        tableView.getColumns().addAll(descriptionColumn, whenColumn, doneColumn);
        tableView.setFixedCellSize(25);
        tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));
        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        descriptionColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 50 ); // 50% width
        whenColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 30 ); // 30% width
        doneColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 20 ); // 20% width
        tableView.setMinWidth(460);

        // Add listener for clicks on row
        tableView.setOnMousePressed(mouseEvent -> {
            if(mouseEvent.isPrimaryButtonDown()){
                int selected = tableView.getSelectionModel().getSelectedItem().getInstructionId();

                if(mouseEvent.getClickCount() == 1){
                    instructionId = selected;
                }else if(mouseEvent.getClickCount() == 2){
                    System.out.println("--> Gonna change done ");
                    SpecificInstruction instruction = register.findSpecificInstruction(instructionId, selectedBeer.getId());
                    instruction.setDone(!instruction.isDone());
                    register.editSpecificInstruction(instruction);
                    updateTable();
                }
            }
        });

        return tableView;
    }

    private ObservableList<Instructions> getTableWrapper() {
        // create an observable list from the different instructions for the selected beer
        tableWrapper = FXCollections.observableArrayList(register.getInstructionsForBeer(selectedBeer.getName()));
        return tableWrapper;
    }

    private void updateTable(){
        this.tableWrapper.setAll(register.getInstructionsForBeer(selectedBeer.getName()));
    }
}
