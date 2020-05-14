package GUI;

import beer.Beer;
import beer.BeerRegister;
import beer.Instructions;
import beer.SpecificInstruction;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import org.apache.commons.lang3.text.WordUtils;

import java.time.LocalDateTime;
import java.util.Optional;

public class ShowMaking {
    private final BorderPane pane = new BorderPane();
    private Beer selectedBeer;
    private SpecificInstruction instruction = null;
    private final BeerRegister register = Controller.getRegister();
    private ObservableList<Instructions> tableWrapper;
    private Label ready;
    private boolean readyBoolean = false;
    private int plusDays;

    public BorderPane getPane(Beer beer) {
        this.selectedBeer = beer;
        this.readyBoolean = register.ready(selectedBeer);
        this.plusDays = selectedBeer.getPlusDays();
        Label title = new Label(beer.getName());
        title.setId("Title");
        TableView<Instructions> tableView = createTable();

        Label delayLbl = new Label("Antall dager å utsette instruksjon");
        TextField increaseField = new TextField("2");
        increaseField.setPrefWidth(30);
        Button increaseDelay = new Button("Endre antall dager");
        increaseDelay.setOnAction(e -> {
            if(Integer.parseInt(increaseField.getText()) > 10){
                Dialog dialog = new Dialog("info", "Beklager", "Du får dessverre ikke utsette en instruksjon mer enn 10 dager");
                dialog.display();
            }else {
                plusDays = Integer.parseInt(increaseField.getText());
                updateTable();
            }
        } );

        TextArea notesArea = new TextArea();
        notesArea.setText(register.findBeer(selectedBeer).getNotes());
        notesArea.setEditable(true);Button updateNotes = new Button("Lagre notatendringer");
        updateNotes.setOnAction(e -> {
            selectedBeer.setNotes(notesArea.getText());
            register.editBeer(selectedBeer);
            Dialog dialog = new Dialog("info", "Endringer lagret", "Dine notatendringer er nå lagret");
            dialog.display();
        });
        GridPane valuesPane = getValuesPane();
        Button deleteMakingBtn = new Button("**");
        deleteMakingBtn.setOnAction(e -> {
            if(selectedBeer == null) return;
            Dialog dialog = new Dialog("confirm", "Slett mekking?", "Vil du slette denne mekkingen? Handlingen kan ikke angres");
            dialog.display();
            if(dialog.isYesNo()){
                register.deleteBeer(selectedBeer);
                Controller.goBack(e);
            }
        });


        HBox delayBox = new HBox(5);
        delayBox.getChildren().addAll(delayLbl, increaseField, increaseDelay);
        delayBox.setAlignment(Pos.CENTER_LEFT);
        VBox centerBox = new VBox(10);
        centerBox.getChildren().addAll(tableView, delayBox, notesArea,updateNotes, valuesPane, deleteMakingBtn);
        ScrollPane scrollPane = new ScrollPane(centerBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        pane.setTop(title);
        BorderPane.setAlignment(title, Pos.TOP_CENTER);
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
        changeVal1Btn.setMaxWidth(Double.MAX_VALUE);
        changeVal2Btn.setMaxWidth(Double.MAX_VALUE);
        changeVal3Btn.setMaxWidth(Double.MAX_VALUE);
        ready = new Label(" ");
        updateValues(ready, 0);
        ready.setId("Subtitle");

        Label changeLabel = new Label("Ny starttid");
        TextField changeFieldDay = new TextField();
        changeFieldDay.setPromptText("åååå-mm-ddThh:mm");
        Button changeStartTime = new Button("Endre starttid");

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
        // Q: Legge til noe formattering på dato? Hvor vanskelig ville det vært å få til det over alt?
        var ref = new Object() {
            LocalDateTime dateTime;
        };
        changeStartTime.setOnAction(e -> {
            try {
                ref.dateTime = LocalDateTime.parse(changeFieldDay.getText());
                if(ref.dateTime.getYear() > 2050) throw new IllegalArgumentException();
                selectedBeer.setStartTime(ref.dateTime);
                register.editBeer(selectedBeer);
                updateTable();
            }catch (IllegalArgumentException illEx){
                Dialog dialog = new Dialog("info", "For langt frem i tid", "Du har skrevet inn en dato som er for langt frem i tid");
                dialog.display();
            }catch (Exception ex){
                Dialog dialog = new Dialog("info", "Feil inndata",
                        "Det skjedde en feil ved innføring av starttid. Det er viktig at du skriver det akkurat på formen " +
                                "åååå-MM-ddThh:mm hvor åååå=årstall, MM=månedsnummer, dd=dag, T=bare skriv den, hh=time, mm=minutt");
                dialog.display();
            }
        });

        changeVal1Btn.setOnAction(e -> {
            selectedBeer.setValue1(Double.parseDouble(val1Input.getText()));
            obs1.setText(val1Input.getText());
            val1Input.clear();
            updateValues(ready, 1);
        });
        changeVal2Btn.setOnAction(e -> {
            selectedBeer.setValue2(Double.parseDouble(val2Input.getText()));
            obs2.setText(val2Input.getText());
            val2Input.clear();
            updateValues(ready, 2);
        });
        changeVal3Btn.setOnAction(e -> {
            selectedBeer.setOG(Double.parseDouble(val3Input.getText()));
            obs3.setText(val3Input.getText());
            val3Input.clear();
            updateValues(ready, 3);
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

    private void updateValues(Label ready, int val){
        // Save new values to the database
        register.editBeer(selectedBeer);

        // Check if it is done or now ready
        this.readyBoolean = register.ready(selectedBeer); // NB: Er egentlig bare her som readyBoolean blir brukt nå :/
        double abv = register.getABV(selectedBeer);
        if(register.finished(selectedBeer)) {
            ready.setText("ABV: " + String.format("%.2f", abv) + "%");
            return;
        }
        // If it is not done but ready, and the changed value was FG2 (second in a row), display a celebration
        // If val==0 no values has been changed and there's no need for celebrating
        // If val==1 one have to wait a day, register FG2 and hope for a celebration
        if(readyBoolean && (val == 2 || val == 0)) {
            ready.setText("Klar for tapping!");
            if(val == 2) {
                Dialog dialog = new Dialog("celebration", "Klar for tapping", "Verdiene er nå stabile og din mekking er klar for tapping :)");
                dialog.display();
            }
        }else{
            ready.setText("-");
        }
    }

    private TableView<Instructions> createTable(){
        // Set up the columns
        TableColumn<Instructions, String> descriptionColumn = new TableColumn<>("Hva");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(column->{
            return new TableCell<Instructions, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item==null || empty) {
                        setGraphic(null);
                    } else {
                        // NB: dette funker ift til å få flere linjer, men wrapLength er lik om det er lite eller stort vindu
                        //     Funker ikke med fixedCellSize for å regne ut høyden på tabellen nå
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
        TableColumn<Instructions, LocalDateTime> whenColumn = new TableColumn<>("Når");
        whenColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(register.findSpecificInstruction(data.getValue().getInstructionId(), selectedBeer.getId()).isDelay() ? selectedBeer.getStartTime().plusDays(data.getValue().getDaysAfterStart()).plusDays(plusDays).plusHours(data.getValue().getHours()):selectedBeer.getStartTime().plusDays(data.getValue().getDaysAfterStart()).plusHours(data.getValue().getHours())));
        //daysColumn.setStyle("-fx-alignment: center;");
        TableColumn<Instructions, String> doneColumn = new TableColumn<>("Gjort");
        doneColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(register.findSpecificInstruction(data.getValue().getInstructionId(), selectedBeer.getId()).isDone() ? "Ja":"Nei")); // Er noe som heter CheckBox. Kan nok være fint å bruke
        doneColumn.setStyle("-fx-alignment: center;");
        doneColumn.getStyleClass().add("center_aligned");
        TableColumn<Instructions, String> delayColumn = new TableColumn<>("Utsatt");
        delayColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(register.findSpecificInstruction(data.getValue().getInstructionId(), selectedBeer.getId()).isDelay() ? "Ja":"Nei"));
        delayColumn.setStyle("-fx-alignment: center;");
        delayColumn.getStyleClass().add("center_aligned");

        // Create the table instance
        TableView<Instructions> tableView = new TableView<>();
        tableView.setItems(getTableWrapper());
        tableView.getColumns().addAll(descriptionColumn, whenColumn, doneColumn, delayColumn);
        //tableView.setFixedCellSize(25);
        //tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.10)));
        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        descriptionColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 50 ); // 50% width
        whenColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 20 ); // 30% width
        doneColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 20 ); // 20% width
        delayColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 10);
        tableView.setMinWidth(460);
        tableView.setPrefHeight(300);

        // Add listener for clicks on row
        tableView.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY){
                Instructions selected = tableView.getSelectionModel().getSelectedItem();
                TablePosition pos = tableView.getSelectionModel().getSelectedCells().get(0);
                TableColumn col = pos.getTableColumn();

                if(mouseEvent.getClickCount() == 1){
                    instruction = register.findSpecificInstruction(selected.getInstructionId(), selectedBeer.getId());
                }else if(mouseEvent.getClickCount() == 2 && (col.equals(doneColumn) || col.equals(delayColumn))){ // TODO: fiks det at linjene mellom radene blir tjukkere for annethvert dobbeltklikk
                    if(col.equals(doneColumn)) instruction.setDone(!instruction.isDone());
                    else if(col.equals(delayColumn)) instruction.setDelay(!instruction.isDelay());
                    register.editSpecificInstruction(instruction);
                    updateTable();
                    updateValues(ready, 0);
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
