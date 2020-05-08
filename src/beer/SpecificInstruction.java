package beer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SpecificInstruction {
    @Id
    @GeneratedValue
    private int specificId;
    private int instructionId;
    private int beerId;
    private boolean done;

    public SpecificInstruction(){};
    public SpecificInstruction(int instructionId, int beerId){
        this.instructionId = instructionId;
        this.beerId = beerId;
        this.done = false;
    }

    public int getSpecificId() {
        return specificId;
    }

    public void setSpecificId(int specificId) {
        this.specificId = specificId;
    }

    public int getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(int instructionId) {
        this.instructionId = instructionId;
    }

    public int getBeerId() {
        return beerId;
    }

    public void setBeerId(int beerId) {
        this.beerId = beerId;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
