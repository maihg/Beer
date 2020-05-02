import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class Makings {
    private VBox pane = new VBox();

    public VBox getPane() {
        Label titel = new Label("Pågående mekkinger");
        TextArea centerArea = new TextArea("Her kommer en liste over pågående mekkinger");
        centerArea.setEditable(false);
        pane.getChildren().addAll(titel, centerArea);

        pane.setPadding(new Insets(10,10,10,10));
        return pane;
    }
}
