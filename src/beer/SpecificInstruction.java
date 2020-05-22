package beer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class SpecificInstruction implements Serializable {
    @Id
    @GeneratedValue
    private int specificId;
    private int instructionId;
    private int beerId;
    private boolean done;
    private boolean delay;

    public SpecificInstruction(){};
    public SpecificInstruction(int instructionId, int beerId){
        this.instructionId = instructionId;
        this.beerId = beerId;
        this.done = false;
        this.delay = false;
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

    public boolean isDelay() {
        return delay;
    }

    public void setDelay(boolean delay) {
        this.delay = delay;
    }
}
