package GUI;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.logging.Logger;

public class Dialog {
    private final String type;
    private final String title;
    private final String message;

    public Dialog(String type, String title, String message){
        this.type = type;
        this.title = title;
        this.message = message;
    }
    public void display(){
        switch(type){
            case "info":
                displayAlertInformation();
                break;
            case "celebration":
                displayAlertCelebration();
                break;
            case "confirm":
                System.out.println("Should confirm something");
                break;
            default:
                Logger.getGlobal().warning("Failed to display dialog");
        }
    }

    private Alert createAlert(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(message);
        DialogPane alertPane = alert.getDialogPane();
        alertPane.getStylesheets().add("GUI/styles.css");
        return alert;
    }
    private void displayAlertInformation(){
        Alert alert = createAlert();
        alert.setTitle("Info");
        alert.show();
    }

    private void displayAlertCelebration(){
        Alert alert = createAlert();
        alert.setTitle("Woho!");
        Image img = new Image(getClass().getResource("/beerImg.jpg").toExternalForm()); // TODO: get image from beer.resources
        ImageView imgView = new ImageView(img);
        imgView.setPreserveRatio(true);
        imgView.setFitHeight(100);
        imgView.setFitWidth(100);
        alert.setGraphic(imgView);
        alert.show();
    }
}
