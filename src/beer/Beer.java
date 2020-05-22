package beer;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Beer implements Serializable {
    @Id
    @GeneratedValue // Blir det AUTO nå? Ja.
    private int id;
    private String name;
    private String type;
    private double value1;
    private double value2;
    private double OG;
    private LocalDateTime startTime;
    private int plusDays;
    private String notes;

    public Beer(){} // Empty constructor

    public Beer(String name, String type, LocalDateTime startTime) throws IllegalArgumentException{
        if(name == null || type == null) throw new IllegalArgumentException("Name and type cannot be empty (null)");
        this.name = name;
        this.type = type;
        this.value1 = -1;
        this.value2 = -1;
        this.OG = -1;
        this.startTime = startTime;
        this.plusDays = 2;
        this.notes = "Notater";
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

    public double getOG() {
        return OG;
    }

    public void setOG(double OG) {
        this.OG = OG;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getPlusDays() { return plusDays; }

    public void setPlusDays(int plusDays) { this.plusDays = plusDays;  }

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
