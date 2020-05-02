import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Instructions {
    @Id
    private int instructionId; // Trenger vi den? Er vel grei for å klare å finne igjen den
    private String description;
    private String date;
    private int beerId;

    public Instructions(){} // Empty

    public Instructions(int beerId){
        this.beerId = beerId;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getBeerId() {
        return beerId;
    }

    public void setBeerId(int beerId) {
        this.beerId = beerId;
    }
}
