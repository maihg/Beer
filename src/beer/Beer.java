package beer;

import javafx.collections.ObservableList;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
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
    private LocalDateTime startTime;

    public Beer(){} // Empty constructor

    public Beer(String name, String type, LocalDateTime startTime) throws IllegalArgumentException{
        if(name == null || type == null) throw new IllegalArgumentException("Name and type cannot be empty (null)");
        this.name = name;
        this.type = type;
        this.value1 = -1;
        this.value2 = -1;
        this.startTime = startTime;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /*@OneToMany
    public List<beer.Instructions> getRecipe() {
        return recipe;
    }

    public void setRecipe(List<beer.Instructions> recipe) {
        this.recipe = recipe;
    }*/

    @Override
    public String toString() {
        return "beer.Beer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value1=" + value1 +
                ", value2=" + value2 +
                ", startTime=" + startTime +
                '}';
    }
}
