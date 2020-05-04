package GUI;

import beer.Beer;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ShowMaking {
    private final BorderPane pane = new BorderPane();
    private Beer selectedBeer;

    public BorderPane getPane(Beer beer) {
        Label title = new Label(beer.getName());
        title.setFont(Font.font("Calibri", FontWeight.BOLD,18));

        pane.setTop(title);
        pane.setPadding(new Insets(10,10,10,10));

        return pane;
    }
}
