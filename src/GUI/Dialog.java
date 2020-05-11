package GUI;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

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
            case "confirm":
                System.out.println("Should confirm something");
                break;
            default:
                Logger.getGlobal().warning("Failed to display dialog");
        }
    }

    private void displayAlertInformation(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(title);
        alert.setContentText(message);
        DialogPane alertPane = alert.getDialogPane();
        alertPane.getStylesheets().add("GUI/styles.css");
        alert.show();
    }
}
