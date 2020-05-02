import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Makings {
    private BorderPane pane = new BorderPane();

    public BorderPane getPane() {
        Label title = new Label("Pågående mekkinger");
        title.setFont(Font.font("Calibri", FontWeight.BOLD,18));
        TextArea centerArea = new TextArea("Her kommer en liste over pågående mekkinger");
        centerArea.setEditable(false);

        //pane.getChildren().addAll(title, centerArea);
        pane.setTop(title);
        pane.setCenter(centerArea);
        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        pane.setPadding(new Insets(10,10,10,10));

        return pane;
    }
}
