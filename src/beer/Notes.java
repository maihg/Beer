package beer;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Notes {
    @Id
    private String beerName;
    private String notes;

    public Notes(){}

    public Notes(String beerName){
        this.beerName = beerName;
        this.notes = "Notater";
    }

    public String getBeerName() {
        return beerName;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
