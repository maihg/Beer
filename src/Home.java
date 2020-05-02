import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class Home {
    private BorderPane pane = new BorderPane();

    public BorderPane getPane(){
        Label title = new Label("PÅ TIDE Å MEKKE LITT?");
        title.setFont(Font.font("Calibri", FontWeight.BOLD,18));
        Label madeTheMost = new Label("Mekket mest: lager");
        TextArea centerArea = new TextArea("Skal komme en fin tabell her");
        centerArea.setEditable(false);
        Button newMakingBtn = new Button("Ny mekking");
        // setOnAction
        Button ongoingMakingsBtn = new Button("Se pågående mekkinger");
        ongoingMakingsBtn.setOnAction(Controller::goToMakings);

        // Set up the layout
        // Helping panes
        VBox centerBox = new VBox(10);
        centerBox.getChildren().addAll(madeTheMost, centerArea, new HBox(newMakingBtn, ongoingMakingsBtn));
        // Aligning stuff
        pane.setTop(title);
        pane.setCenter(centerBox);
        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        pane.setPadding(new Insets(10,10,10,10)); // top, right, bottom, left

        return pane;
    }
}
