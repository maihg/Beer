package GUI;

import beer.Beer;
import beer.BeerRegister;
import beer.Instructions;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ShowMaking {
    private final BorderPane pane = new BorderPane();
    private Beer selectedBeer;
    private final BeerRegister register = Controller.getRegister();
    private ObservableList<Instructions> tableWrapper;

    public BorderPane getPane(Beer beer) {
        this.selectedBeer = beer;
        Label title = new Label(beer.getName());
        title.setFont(Font.font("Calibri", FontWeight.BOLD,18));
        TableView<Instructions> tableView = createTable();
        Label changeLabel = new Label("Ny starttid");
        TextField changeFieldDay = new TextField();
        changeFieldDay.setPromptText("åååå-mm-ddThh:mm");
        Button changeStartTime = new Button("Endre starttid");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
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
        HBox changeStartTimeBox = new HBox(10);
        changeStartTimeBox.setAlignment(Pos.BOTTOM_LEFT);
        changeStartTimeBox.getChildren().addAll(changeLabel, changeFieldDay, changeStartTime);

        VBox centerBox = new VBox(10);
        centerBox.getChildren().addAll(tableView, changeStartTimeBox);
        pane.setTop(title);
        pane.setCenter(centerBox);
        pane.setPadding(new Insets(10,10,10,10));

        return pane;
    }

    private TableView<Instructions> createTable(){
        // Set up the columns
        TableColumn<Instructions, String> descriptionColumn = new TableColumn<>("Hva");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Instructions, LocalDateTime> whenColumn = new TableColumn<>("Når");
        whenColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(selectedBeer.getStartTime().plusDays(data.getValue().getDaysAfterStart()).plusHours(data.getValue().getHours())));
        //daysColumn.setStyle("-fx-alignment: center;");
        TableColumn<Instructions, String> doneColumn = new TableColumn<>("Gjort");
        doneColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper("-")); // Er noe som heter CheckBox. Kan nok være fint å bruke
        doneColumn.setStyle("-fx-alignment: center;");

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

        return tableView;
    }

    private ObservableList<Instructions> getTableWrapper() {
        // create an observable list from the different instructions for the selected beer
        tableWrapper = FXCollections.observableArrayList(register.getInstructionsForBeer(selectedBeer.getName()));
        return tableWrapper;
    }

    public void updateTable(){
        this.tableWrapper.setAll(register.getInstructionsForBeer(selectedBeer.getName()));
    }
}
