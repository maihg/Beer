package GUI;

import beer.Beer;
import beer.BeerRegister;
import beer.Instructions;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;

public class ShowMaking {
    private final BorderPane pane = new BorderPane();
    private Beer selectedBeer;
    private final BeerRegister register = Controller.getRegister();

    public BorderPane getPane(Beer beer) {
        this.selectedBeer = beer;
        Label title = new Label(beer.getName());
        title.setFont(Font.font("Calibri", FontWeight.BOLD,18));
        TableView<Instructions> tableView = createTable();

        pane.setTop(title);
        pane.setCenter(tableView);
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
        doneColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper("-"));
        doneColumn.setStyle("-fx-alignment: center;");

        // Create the table instance
        TableView<Instructions> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(register.getInstructionsForBeer(selectedBeer.getName())));
        tableView.getColumns().addAll(descriptionColumn, whenColumn, doneColumn);
        tableView.setFixedCellSize(25);
        tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));
        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        descriptionColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 50 ); // 50% width
        whenColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 30 ); // 30% width
        doneColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 20 ); // 20% width

        return tableView;
    }
}
