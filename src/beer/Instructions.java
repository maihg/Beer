package beer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Instructions implements Serializable, Comparable<Instructions> {
    @Id
    @GeneratedValue
    private int instructionId;
    private String description;
    private int daysAfterStart;
    private int hours;
    private String beerName;

    public Instructions(){} // Empty because of Java Bean

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
    public int compareTo(Instructions o) {
        String no = "" + daysAfterStart + hours;
        String noToCompareTo = "" + o.daysAfterStart + o.hours;
        return Integer.parseInt(no) - Integer.parseInt(noToCompareTo);
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
