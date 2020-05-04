package beer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Instructions implements Serializable {
    @Id
    @GeneratedValue
    private int instructionId; // Trenger vi den? Er vel grei for å klare å finne igjen den
    private String description;
    private int daysAfterStart;
    private int hours;
    private String beerName;

    public Instructions(){} // Empty

    public Instructions(String description, int daysAfterStart, int hours, String beerName){
        this.description = description;
        this.beerName = beerName;
        this.daysAfterStart = daysAfterStart;
        this.hours = hours;
    }

    // Get- and set-methods
    public int getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(int instructionId) {
        this.instructionId = instructionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }*/

    public int getDaysAfterStart() {
        return daysAfterStart;
    }

    public void setDaysAfterStart(int daysAfterStart) {
        this.daysAfterStart = daysAfterStart;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public String getBeerName() {
        return beerName;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    @Override
    public String toString() {
        return "beer.Instructions{" +
                "instructionId=" + instructionId +
                ", description='" + description + '\'' +
                ", daysAfterStart=" + daysAfterStart +
                ", hours=" + hours +
                ", beerName='" + beerName + '\'' +
                '}';
    }
}
