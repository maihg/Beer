package GUI;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.logging.Logger;

public class Dialog {
    private final String type;
    private final String title;
    private final String message;
    private boolean yesNo;

    public Dialog(String type, String title, String message){
        this.type = type;
        this.title = title;
        this.message = message;
        this.yesNo = false;
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
                displayAlertConfirmation();
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
        // Add logo
        //Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        //stage.getIcons().add(new Image(getClass().getResource("noenoe").toString()));
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

    private void displayAlertConfirmation(){
        Alert alert = createAlert();
        alert.setTitle("Bekreft handling");
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        ButtonType btnYes = new ButtonType("Ja");
        ButtonType btnNo = new ButtonType("Nei");
        alert.getButtonTypes().setAll(btnYes, btnNo);
        Optional<ButtonType> res = alert.showAndWait();
        if(res.isEmpty()){
            this.yesNo = false;
        }else if(res.get() == btnYes){
            this.yesNo = true;
        }else if(res.get() == btnNo){
            this.yesNo = false;
        }
    }

    public boolean isYesNo() {
        return yesNo;
    }
}
