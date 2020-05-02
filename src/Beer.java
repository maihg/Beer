import javafx.collections.ObservableList;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Beer {
    @Id
    private int id;
    private String name;
    private String type;
    private int timesMade = 0;
    //private List<Double> values; // Passer visst dårlig her
    //private ObservableList<Instructions> recipe;

    public Beer(){} // Empty constructor

    // get and set methods for all

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTimesMade() {
        return timesMade;
    }

    public void setTimesMade(int timesMade) {
        this.timesMade = timesMade;
    }

    /*public ArrayList<Double> getValues() {
        return values;
    }

    public void setValues(ArrayList<Double> values) {
        this.values = values;
    }

    public ObservableList<Instructions> getRecipe() {
        return recipe;
    }

    public void setRecipe(ObservableList<Instructions> recipe) {
        this.recipe = recipe;
    }*/
}
