package GUI;

import beer.Beer;
import beer.BeerRegister;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.time.LocalDateTime;

public class Makings {
    private final BorderPane pane = new BorderPane();
    private final BeerRegister register = Controller.getRegister();
    private String selectedBeerName;

    public BorderPane getPane() {
        Label title = new Label("Pågående mekkinger");
        title.setFont(Font.font("Calibri", FontWeight.BOLD,18));
        TableView<Beer> tableView1 = createTable("ongoing");
        TableView<Beer> tableView2 = createTable("coming");
        Label title2 = new Label("Kommende mekkinger");
        title2.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.ITALIC, 14));
        title2.setAlignment(Pos.CENTER);

        VBox centerBox = new VBox(10);
        centerBox.getChildren().addAll(tableView1, title2, tableView2);
        ScrollPane centerScroll = new ScrollPane(centerBox);
        centerScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        centerScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pane.setTop(title);
        pane.setCenter(centerBox);
        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        pane.setPadding(new Insets(10,10,10,10));

        return pane;
    }

    private TableView<Beer> createTable(String source){
        // Set up the columns
        TableColumn<Beer, String> descriptionColumn = new TableColumn<>("Øl");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setMinWidth(139);
        TableColumn<Beer, String> daysColumn = new TableColumn<>("Hva");
        daysColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(register.getNextInstructionDescription(data.getValue())));
        daysColumn.setMinWidth(200);
        TableColumn<Beer, LocalDateTime> hoursColumn = new TableColumn<>("Dato");
        hoursColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(register.getNextInstructionDate(data.getValue())));
        hoursColumn.setMinWidth(139);

        // Create the table instance
        TableView<Beer> tableView = new TableView<>();
        if(source.equals("ongoing")) {
            tableView.setItems(FXCollections.observableArrayList(register.getOngoingBeers()));
        }else if(source.equals("coming")){
            tableView.setItems(FXCollections.observableArrayList(register.getComingBeers()));
        }
        tableView.getColumns().addAll(descriptionColumn, daysColumn, hoursColumn);
        tableView.setFixedCellSize(25);
        tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));

        // Add listener for clicks on row
        tableView.setOnMousePressed(mouseEvent -> {
            if(mouseEvent.isPrimaryButtonDown()){
                Beer beer = tableView.getSelectionModel().getSelectedItem();

                if(mouseEvent.getClickCount() == 1){
                    selectedBeerName = beer.getName();
                }else if(mouseEvent.getClickCount() == 2){
                    System.out.println("--> Gonna show you the progress for " + selectedBeerName);
                    System.out.println(beer.toString());
                    Controller.goToShowMaking(mouseEvent, beer);
                }
            }
        });

        return tableView;
    }
}
