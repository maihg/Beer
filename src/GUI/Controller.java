package GUI;

import beer.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


// This will be my GUI.Controller-class
public class Controller extends Application {
    private static BeerRegister register;
    private static Stage stage;
    private static Home home;
    private static Makings makings;
    private static ShowRecipe showRecipe;
    private static ShowMaking showMaking;
    private static VBox contents;

    private static String lastScene = "home";
    private static String newScene = "home";
    private static String selectedBeerName;
    private static Beer selectedBeer;


    // The JavaFX ObservableListWrapper used to connect tot he underlying AddressBook
    private ObservableList<Beer> homeTableWrapper;

    // Need to keep track of the TableView-instance since we need to access it
    // from different places in our GUI (menu, doubleclicking, toolbar etc.)
    private TableView<Beer> homeTable;


    @Override
    public void start(Stage stage) throws Exception {
        Controller.stage = stage;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("beer-pu");
        register = new BeerRegister(emf);
        contents = new VBox(10);
        home = new Home();
        makings = new Makings();
        showRecipe = new ShowRecipe();
        showMaking = new ShowMaking();

        Button homeBtn = new Button("Hjem");
        homeBtn.setOnAction(Controller::goToHome);
        Button goBackBtn = new Button("<-- Tilbake");
        goBackBtn.setOnAction(e -> goBack(e, lastScene));
        VBox topV = new VBox(homeBtn, goBackBtn);
        Button fileHandleBtn = new Button("Filhåndtering");
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        HBox top = new HBox(topV, region, fileHandleBtn);
        contents.getChildren().addAll(top, home.getPane());
        contents.setAlignment(Pos.TOP_CENTER);

        stage.setTitle("MH -- Hjem");
        Scene scene = new Scene(contents, 600, 600);
        scene.getStylesheets().add("GUI/styles.css");
        stage.setScene(scene);
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

    public static void goToShowMaking(Event event, Beer beer){
        stage.setTitle("MH -- Se spesifikk mekking");
        selectedBeer = beer;
        contents.getChildren().set(1, showMaking.getPane(beer));
        changeNewScene("showMaking");
    }

    public static void goToShowRecipe(Event event, String beerName){
        stage.setTitle("MH -- Om " + beerName);
        selectedBeerName = beerName;
        contents.getChildren().set(1, showRecipe.getPane(beerName));
        changeNewScene("showRecipe");
    }

    public static void changeNewScene(String theNewScene){
        lastScene = newScene;
        newScene = theNewScene;
    }

    public static void goBack(Event event, String lastScene){ // TODO: gjør det mulig å gå flere sider bakover, ikke bare i loop
        switch (lastScene){
            case "home":
                goToHome((ActionEvent) event);
                break;
            case "makings":
                goToMakings((ActionEvent) event);
                break;
            case "showMaking":
                goToShowMaking(event, selectedBeer);
                break;
            case "showRecipe":
                goToShowRecipe(event, selectedBeerName);
                break;
            default:
                System.out.println("Couldn't find a last scene to go to");
                break;
        }

    }

    public static void goBack(Event event, Object o){ // TODO: idé for løsning (se over)
        // if(o instance of *klasse*){ goTo*naturlig forrige element*; }
    }

    public static String getSelectedBeerName(){ return selectedBeerName; }

    public static BeerRegister getRegister() {
        return register;
    }

    public static void main(String[] args) {
        launch(args);
    }

}