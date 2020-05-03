import javafx.collections.ObservableList;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Beer implements Serializable {
    @Id
    @GeneratedValue // Blir det AUTO nå?
    private int id;
    private String name;
    private String type;
    private double value1;
    private double value2;
    //private List<Instructions> recipe = new ArrayList<>();

    public Beer(){} // Empty constructor

    public Beer(String name, String type) throws IllegalArgumentException{
        if(name == null || type == null) throw new IllegalArgumentException("Name and type cannot be empty (null)");
        this.name = name;
        this.type = type;
        this.value1 = -1;
        this.value2 = -1;
    }

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

    public double getValue1() {
        return value1;
    }

    public void setValue1(double value1) {
        this.value1 = value1;
    }

    public double getValue2() {
        return value2;
    }

    public void setValue2(double value2) {
        this.value2 = value2;
    }

    /*@OneToMany
    public List<Instructions> getRecipe() {
        return recipe;
    }

    public void setRecipe(List<Instructions> recipe) {
        this.recipe = recipe;
    }*/

    @Override
    public String toString() {
        return "Beer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value1=" + value1 +
                ", value2=" + value2 +
                '}';
    }
}
