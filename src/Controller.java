import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// This will be my Controller-class
public class Controller extends Application {
    private static Home home;
    private static Makings makings;
    private static VBox contents;

    private static String lastScene = "home";
    private static String newScene = "home";

    @Override
    public void start(Stage stage) throws Exception {
        contents = new VBox(10);
        home = new Home();
        makings = new Makings();

        Button homeBtn = new Button("Hjem");
        Button goBackBtn = new Button("<-- Tilbake");
        goBackBtn.setOnAction(e -> goBack(e, lastScene));
        VBox top = new VBox(homeBtn, goBackBtn);
        contents.getChildren().addAll(top, home.getPane());
        contents.setAlignment(Pos.TOP_CENTER);

        stage.setTitle("MH");
        stage.setScene(new Scene(contents, 500, 400));
        stage.show();
    }


    private static void goToHome(ActionEvent event) {
        contents.getChildren().set(1, home.getPane());
        changeNewScene("home");
    }

    public static void goToMakings(ActionEvent event) {
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