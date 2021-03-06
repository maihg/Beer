package beer;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Denne klassen er tilsvarende AddressBookDBHandler i O5A, og BookDAO i leksjon.
 * Skal dytte opp og hente ned info fra DB.
 */
public class BeerRegister implements Serializable {
    private EntityManagerFactory emf;

    // CONSTRUCTOR - Initialize the EntitymanagerFactory
    public BeerRegister(EntityManagerFactory emf){
        this.emf = emf;
    }

    // GET LISTS OF BEERS / GET BEER TYPE
    public List<Beer> getAllBeers() {
        EntityManager em = getEM();
        try {
            Query q = em.createQuery("SELECT OBJECT(o) FROM Beer o ORDER BY o.name");
            return q.getResultList();
        }finally {
            closeEM(em);
        }
    }

    // Lists all Beer-objects that are not done and where the start time has been
    public List<Beer> getOngoingBeers(){
        List<Beer> allBeers = getAllBeers();
        List<Beer> ongoingBeers = new ArrayList<>();
        for(Beer beer: allBeers){
            LocalDateTime now = LocalDateTime.now();
            boolean finished = true; // Assumes that it is done until the opposite is proved
            for(Instructions i: getInstructionsForBeer(beer.getName())){
                if(!(findSpecificInstruction(i.getInstructionId(), beer.getId()).isDone())){
                    finished = false;
                    break;
                }
            }
            if((now.isAfter(beer.getStartTime()) || now.isEqual(beer.getStartTime())) && (now.isBefore(getLastDayOfMaking(beer)) || now.isEqual(getLastDayOfMaking(beer)))){
                ongoingBeers.add(beer);
            }
            if(now.isAfter(getLastDayOfMaking(beer)) && !finished) ongoingBeers.add(beer);
        }
        return ongoingBeers;
    }

    // Lists all the Beer-objects with a startTime in the future
    public List<Beer> getComingBeers(){
        List<Beer> allBeers = getAllBeers();
        List<Beer> comingBeers = new ArrayList<>();
        for(Beer beer: allBeers){
            LocalDate now = LocalDateTime.now().toLocalDate();
            if(now.isBefore(beer.getStartTime().toLocalDate())){
                comingBeers.add(beer);
            }
        }
        return comingBeers;
    }

    // Q: Brukes bare i madeTheMost() her og den kunne egentlig brukt getAllBeerTypesProperty()...
    //    Fjerne denne metoden og så endre navn på ..BeerTypesProperty til ..BeerTypes() ??
    public List<String> getAllBeerTypes(){
        List<Beer> allBeers = getAllBeers();
        List<String> beers = new ArrayList<>();
        for(Beer beer: allBeers){
            if(beers.size()==0 || !beers.contains(beer.getName())){
                beers.add(beer.getName());
            }
        }
        return beers;
    }
    public List<Beer> getAllBeerTypesProperty(){
        List<Beer> list = new ArrayList<>();
        List<Beer> types = getAllBeers();
        List<String> names = new ArrayList<>();
        for(Beer beer: types){
            if(names.size() == 0 || !names.contains(beer.getName())){
                list.add(beer);
                names.add(beer.getName());
            }
        }
        return list;
    }
    public String getBeerType(String beerName){

        List<Beer> types = getAllBeers();
        for(Beer beer: types){
            if(!(types.size() == 0)){
                if(beer.getName().equals(beerName)) return beer.getType();
            }
        }
        return null;
    }

    /**
     * Get all makings (Beer-objects) with given name that are finished
     * @param beerName name of the beer
     * @return list of beers
     */
    public List<Beer> getMakingsOfType(String beerName){
        List<Beer> theList;
        EntityManager em = getEM();
        try {
            Query q = em.createQuery("SELECT OBJECT(o) FROM Beer o WHERE o.name LIKE :name").setParameter("name", beerName);
            theList = q.getResultList();
            theList.removeIf(beer -> !finished(beer));
        }finally {
            closeEM(em);
        }
        return theList;
    }


    // METHODS FOR USE ON BEER-OBJECTS
    public void addNewBeer(Beer beer){
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.persist(beer);
            em.getTransaction().commit();
        }catch (IllegalArgumentException e){
            Logger.getGlobal().warning("Couldn't add new beer");
        }finally {
            closeEM(em);
        }
    }

    public boolean okName(String beerName){
        for(Beer beer: getAllBeerTypesProperty()){
            if(beer.getName().equalsIgnoreCase(beerName)) return false;
        }
        return true;
    }

    public void editBeer(Beer beer){
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.merge(beer);
            em.getTransaction().commit();
        }finally {
            closeEM(em);
        }
    }

    public void deleteBeer(Beer beer){
        // Delete all specific instructions connected to this Beer-object
        deleteAllSpecificInstructions(beer.getId());
        EntityManager em = getEM();
        try {
            // If it is the only Beer-object with that beerName, delete the notes (general for that beer) from Notes
            if(noOfTimesMade(beer.getName()) == 1){
                em.getTransaction().begin();
                Query q = em.createQuery("DELETE FROM Notes o WHERE o.beerName LIKE :name").setParameter("name", beer.getName());
                q.executeUpdate();
                em.getTransaction().commit();
            }
            // Delete the Beer-object itself
            em.getTransaction().begin();
            Beer current = beer;
            if(!em.contains(beer)){ current = em.merge(beer);}
            em.remove(current);
            em.getTransaction().commit();
        }finally {
            closeEM(em);
        }
    }

    // TODO: decide whether or not to delete this method... only used in the BeerRegister.main()
    public boolean regValues(Beer beer, double value, int valType){
        // Legg til verdi i beer-objektet dag 1 (value1) eller dag 2 (value2)
        if(valType == 1) beer.setValue1(value);
        else if(valType == 2) beer.setValue2(value);
        else if(valType == 3) beer.setOG(value);
        else throw new IllegalArgumentException("Day can only be 1 or 2");
        // Sjekk om det er klart for tapping
        if(valType == 1) return false; // NB: Må sjekke to dager på rad, så dag en er den ikke klar
        return ready(beer);        // Evnt. ta inn beerId og ha bruk en find metode for å finne riktig objekt
    }

    // NB: FG og OG usually is in the interval [1.000, 1.160] and OG < FG
    public boolean ready(Beer beer){
        if(beer.getValue1() == -1 && beer.getValue2() == -1) return false;
        return Math.abs(beer.getValue1() - beer.getValue2()) <= 0.01; // TODO: sjekk - verdi ok?
    }

    // NB: finished = all instructions are marked as done
    public boolean finished(Beer beer){
        List<Instructions> instructionsList = getInstructionsForBeer(beer.getName());
        // Loop through the instructions to see if one or more instructions are not done
        for(Instructions instructions: instructionsList){
            if(!findSpecificInstruction(instructions.getInstructionId(), beer.getId()).isDone()) return false;
        }
        return true;
    }

    /**
     * Calculates the alcohol by volume
     * @param beer Beer-object
     * @return the percent as double
     */
    public double getABV(Beer beer) {
        if(!ready(beer)) return -1;
        return (beer.getValue2()-beer.getOG())*131.25;
    }

    public Beer findBeer(Beer beer){
        EntityManager em = getEM();
        try{
            return em.find(Beer.class, beer.getId());
        }finally {
            closeEM(em);
        }
    }


    // DIV STUFF
    private int noOfTimesMade(String name){
        EntityManager em = getEM();
        Long ans;
        try {
            em.getTransaction().begin();
            Query q = em.createQuery("SELECT COUNT (o) from Beer o WHERE o.name LIKE :name").setParameter("name", name);
            ans = (Long) q.getSingleResult();
            em.getTransaction().commit();
        }finally {
            closeEM(em);
        }
        return ans.intValue();
    }
    public ObservableValue<Integer> noOfTimes(String name){
        int times = noOfTimesMade(name);
        ObservableValue<Integer> no = new SimpleIntegerProperty(times).asObject();
        return no;
    }

    public String mostMadeBeer(){
        List<String> beers = getAllBeerTypes();
        String theMost = "-";
        int mostNo = 0;
        for (String beer: beers){
            int times = noOfTimesMade(beer);
            if (times > mostNo){
                mostNo = times;
                theMost = beer;
            }else if(times == mostNo){
                theMost += ", " + beer;
            }
        }

        return theMost;
    }



    // METHODS - INSTRUCTIONS
    /*
    Akkurat nå er planen at man ikke har en direkte linje mellom beer.Beer og beer.Instructions.
    Man henter riktige instructions ved å velge navnet på ølen.
    Når man ser på en spesifikk mekking så legger man til dager på starttidspunkt før man
    viser i lista.
     */
    public void addInstructionToBeer(String description, int daysAfterStart, int hours, String beerName){
        EntityManager em = getEM();
        try {
            Instructions instruction = new Instructions(description, daysAfterStart, hours, beerName);
            em.getTransaction().begin();
            em.persist(instruction);
            em.getTransaction().commit();
        }finally {
            closeEM(em);
        }
    }

    // Q: Beholde eller forkaste denne metoden? Brukes ikke og er lite stress å skrive på nytt
    public void editBeerInstruction(Instructions instruction){
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.merge(instruction);
            em.getTransaction().commit();
        }finally {
            closeEM(em);
        }
    }

    public Instructions getNextInstruction(Beer beer){
        List<Instructions> instructions = this.getInstructionsForBeer(beer.getName());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = null;
        LocalDateTime date;
        Instructions nextInstruction = null;

        for (Instructions instruction : instructions) {
            date = beer.getStartTime().plusDays(instruction.getDaysAfterStart()).plusHours(instruction.getHours());
            boolean done = findSpecificInstruction(instruction.getInstructionId(), beer.getId()).isDone();
            if ((!date.isBefore(now) || date.isEqual(now) || !done) && (next == null || date.isBefore(next))) {
                next = date;
                nextInstruction = instruction;
            }
        }

        assert nextInstruction != null;
        return nextInstruction;
    }

    public String getNextInstructionDescription(Beer beer) {
        Instructions instruction = getNextInstruction(beer);
        return instruction.getDescription();
    }

    public LocalDateTime getNextInstructionDate(Beer beer){
        Instructions instruction = getNextInstruction(beer);
        return beer.getStartTime().plusDays(instruction.getDaysAfterStart());
    }
    public Instructions getLastInstruction(String beerName){
        List<Instructions> instructions = getInstructionsForBeer(beerName);
        Instructions instr = null;
        int days = 0;
        for(Instructions i: instructions){
            if(i.getDaysAfterStart() > days){
                days = i.getDaysAfterStart();
                instr = i;
            }
        }
        return instr;
    }
    public LocalDateTime getLastDayOfMaking(Beer beer){
        if(findBeer(beer) == null) throw new IllegalArgumentException("Couldn't find beer"); // TODO: sjekk om det er mulig å bruke metoden med et ikke eksisterende objekt

        List<Instructions> instructions = getInstructionsForBeer(beer.getName());
        int days = 0;
        for(Instructions i: instructions){
            if(i.getDaysAfterStart() > days){
                days = i.getDaysAfterStart();
            }
        }
        return beer.getStartTime().plusDays(days);
    }

    public List<Instructions> getInstructionsForBeer(String beerName){
        EntityManager em = getEM();
        List<Instructions> instructions = null;
        try {
            em.getTransaction().begin();
            Query q = em.createQuery("SELECT OBJECT(o) FROM Instructions o WHERE o.beerName LIKE :name ORDER BY o.daysAfterStart + o.hours").setParameter("name", beerName);
            // NB: ^er litt usikker på den order by delen, men tror den skal være grei altså (sånn hvis ting blir lagt inn i tilfeldig rekkefølge)
            instructions = q.getResultList();
            em.getTransaction().commit();
        }finally {
            closeEM(em);
        }
        return instructions;
        // TODO: make sure this list is sorted so that the first steps actually comes first in the table view
        // Q: Har vi fått til dette nå som det er order by o.daysAfterStart+o.hours?
    }


    // METHODS - SPECIFIC_INSTRUCTION
    public void addSpecificInstruction(SpecificInstruction specificInstruction){
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.persist(specificInstruction);
            em.getTransaction().commit();
        }finally {
            closeEM(em);
        }
    }

    public void editSpecificInstruction(SpecificInstruction specificInstruction){
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.merge(specificInstruction);
            em.getTransaction().commit();
        }finally {
            closeEM(em);
        }
    }

    public SpecificInstruction findSpecificInstruction(int instructionId, int beerID){
        List<SpecificInstruction> instructions = getAllSpecificInstructions(beerID);
        for(SpecificInstruction i: instructions){
            if(i.getInstructionId() == instructionId) return i;
        }
        return null;
    }

    private List<SpecificInstruction> getAllSpecificInstructions(int beerId){
        EntityManager em = getEM();
        List<SpecificInstruction> instructions = new ArrayList<>();
        try {
            em.getTransaction().begin();
            Query q = em.createQuery("SELECT OBJECT(o) FROM SpecificInstruction o");
            instructions = q.getResultList();
            em.getTransaction().commit();
        }finally {
            closeEM(em);
        }

        if(instructions != null){
            instructions.removeIf(s -> s.getBeerId() != beerId);
        }
        return instructions;
    }

    private void deleteAllSpecificInstructions(int beerId){
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM SpecificInstruction o WHERE o.beerId = (:id)").setParameter("id", beerId);
            //q.executeUpdate();
            em.getTransaction().commit();
        }finally {
            closeEM(em);
        }
    }

    // METHODS FOR NOTES - NOTES-CLASS
    // NB: These three notes methods are connected to the Notes-class and are general for a type of beer (e.g. "Sommerøl")
    public void addNotesToBeer(String beerName){
        EntityManager em = getEM();
        try {
            Notes note = new Notes(beerName);
            note.setNotes("Notater");
            em.getTransaction().begin();
            em.persist(note);
            em.getTransaction().commit();
        }finally {
            closeEM(em);
        }
    }

    public Notes findNotes(String beerName) {
        EntityManager em = getEM();
        try {
            return em.find(Notes.class, beerName);
        }finally {
            closeEM(em);
        }
    }

    public void editNotes(String newNotes, String beerName){
        EntityManager em = getEM();
        try {
            Notes notes = findNotes(beerName);
            notes.setNotes(newNotes);
            em.getTransaction().begin();
            em.merge(notes);
            em.getTransaction().commit();
        }finally {
            closeEM(em);
        }
    }

   /* public void exportToCsv(String tableName, String fileName){
        EntityManager em = getEM();
        Query q = em.createQuery("SELECT OBJECT(o) FROM tableName o INTO OUTFILE (fileName) FIELDS TERMINATED BY ';%20'").setParameter("tableName", tableName).setParameter("fileName", fileName);
    }*/


    // METHODS CONCERNING THE ENTITY MANANGER FACTORY
    private EntityManager getEM(){
        return emf.createEntityManager();
    }
    private void closeEM(EntityManager em){
        if(em != null && em.isOpen()) em.close();
    }


    // MAIN - For the testing of different methods and creating some content to the DB
    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        try {
            emf = Persistence.createEntityManagerFactory("beer-pu");
            BeerRegister register = new BeerRegister(emf);

            Beer beer1 = new Beer("Henriks beste", "Lager", LocalDateTime.of(2020, 1, 13,0, 0));
            Beer beer2 = new Beer("Sommerøl", "Pils", LocalDateTime.of(2020, 6, 20, 12, 0));
            Beer beer3 = new Beer("Henriks beste", "Lager", LocalDateTime.of(2020, 5, 9, 0,0));
            Beer beer4 = new Beer("Sommerøl", "Pils", LocalDateTime.of(2020, 5, 1, 0,0));
            Beer beer5 = new Beer("BøffBay", "Lager", LocalDateTime.of(2020, 5, 3, 0,0));
            Beer beer6 = new Beer("Mai(s)maker", "Pils", LocalDateTime.of(2020, 5, 2, 0,0));
            register.addNewBeer(beer1);
            register.addNewBeer(beer2);
            register.addNewBeer(beer3);
            register.addNewBeer(beer4);
            register.addNewBeer(beer5);
            register.addNewBeer(beer6);

            List<Beer> beers = register.getAllBeers();
            System.out.println("-- ALL BEERS");
            for (Beer b: beers) System.out.println("* " + b.toString());

            System.out.println("-- Antall mekkinger av Henriks beste: "+ register.noOfTimesMade("Henriks beste"));

            boolean try1 = register.regValues(beer1, 1.16, 1);
            boolean try2 = register.regValues(beer1, 1.19, 2);
            boolean try3 = register.regValues(beer1, 1.15, 1);
            boolean try4 = register.regValues(beer1, 1.14, 2);
            //System.out.println("1: " + try1 + ", 2: " + try2 + ", 3: "+ try3 +", 4: "+ try4);

            register.addInstructionToBeer("Start", 0, 0, "Henriks beste");
            register.addInstructionToBeer("Ha i humle", 0, 2, "Henriks beste");
            register.addInstructionToBeer("Tapp på flaske", 28, 0, "Henriks beste");
            register.addInstructionToBeer("Start", 0, 0, "Sommerøl");
            register.addInstructionToBeer("Noe", 10,0, "Sommerøl");
            register.addInstructionToBeer("Start", 0, 0, "BøffBay");
            register.addInstructionToBeer("Start", 0,0, "Mai(s)maker");

            List<Instructions> recipe = register.getInstructionsForBeer("Henriks beste");
            System.out.println("-- RECIPE for Henriks beste");
            for(Instructions i: recipe) System.out.println(i.toString());

            System.out.println("-- Adding specific instructions to beers");
            ArrayList<Beer> beerList = new ArrayList<>();
            beerList.add(beer1); beerList.add(beer2); beerList.add(beer3); beerList.add(beer4); beerList.add(beer5); beerList.add(beer6);
            for(Beer b: beerList) {
                for (Instructions i : register.getInstructionsForBeer(b.getName())) {
                    SpecificInstruction instruction = new SpecificInstruction(i.getInstructionId(), b.getId());
                    register.addSpecificInstruction(instruction);
                }
            }

            System.out.println("-- Finding the last instruction");
            System.out.println(register.getLastInstruction("Henriks beste").toString());
            System.out.println("Date for last instruction on beer1 (Henriks beste): " + register.getLastDayOfMaking(beer3));

            System.out.println("-- Ongoing beers");
            register.getOngoingBeers().forEach(System.out::println);

            System.out.println("-- Next Instruction");
            System.out.println(register.getNextInstruction(beer3));

            System.out.println("-- All beer types");
            register.getAllBeerTypes().forEach(System.out::println);

            register.addNotesToBeer("Henriks beste");
            register.addNotesToBeer("Sommerøl");
            register.addNotesToBeer("BøffBay");
            register.addNotesToBeer("Mai(s)maker");


        }finally {
            assert emf != null;
            emf.close();
        }
    }
}
