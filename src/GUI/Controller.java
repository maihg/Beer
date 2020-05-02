package GUI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// This will be my GUI.Controller-class
public class Controller extends Application {
    private static Stage stage;
    private static Home home;
    private static Makings makings;
    private static VBox contents;
    private static VBox top;

    private static String lastScene = "home";
    private static String newScene = "home";

    @Override
    public void start(Stage stage) throws Exception {
        Controller.stage = stage;
        contents = new VBox(10);
        home = new Home();
        makings = new Makings();

        Button homeBtn = new Button("Hjem");
        homeBtn.setOnAction(Controller::goToHome);
        Button goBackBtn = new Button("<-- Tilbake");
        goBackBtn.setOnAction(e -> goBack(e, lastScene));
        top = new VBox(homeBtn, goBackBtn);
        contents.getChildren().addAll(top, home.getPane());
        contents.setAlignment(Pos.TOP_CENTER);

        stage.setTitle("MH");
        stage.setScene(new Scene(contents, 500, 400));
        stage.show();
    }


    private static void goToHome(ActionEvent event) {
        stage.setTitle("MH -- Hjem");
        contents.getChildren().set(1, home.getPane());
        changeNewScene("home");
    }

    public static void goToMakings(ActionEvent event) {
        stage.setTitle("MH -- Pågående mekkinger");
        contents.getChildren().set(1, makings.getPane());
        changeNewScene("makings");
    }

    public static void changeNewScene(String theNewScene){
        lastScene = newScene;
        newScene = theNewScene;
    }

    public static void goBack(ActionEvent event, String lastScene){
        switch (lastScene){
            case "home":
                goToHome(event);
                break;
            case "makings":
                goToMakings(event);
                break;
            default:
                System.out.println("Couldn't find a last scene to go to");
                break;
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

}