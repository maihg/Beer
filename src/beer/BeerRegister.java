package beer;


import com.mysql.cj.conf.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Denne klassen er tilsvarende AddressBookDBHandler i O5A, og BookDAO i leksjon.
 * Skal dytte opp og hente ned info fra DB.
 */
public class BeerRegister implements Serializable {
    private EntityManagerFactory emf;

    public BeerRegister(EntityManagerFactory emf){
        this.emf = emf;
    }

    public List<Beer> getAllBeers() {
        EntityManager em = getEM();
        try {
            Query q = em.createQuery("SELECT OBJECT(o) FROM Beer o");
            return q.getResultList();
        }finally {
            closeEM(em);
        }
    }

    public List<Beer> getOngoingBeers(){
        List<Beer> allBeers = getAllBeers();
        List<Beer> ongoingBeers = new ArrayList<>();
        for(Beer beer: allBeers){
            LocalDate now = LocalDateTime.now().toLocalDate();
            if((now.isAfter(beer.getStartTime().toLocalDate()) || now.isEqual(beer.getStartTime().toLocalDate()))&& (now.isBefore(getLastDayOfMaking(beer).toLocalDate()) || now.isEqual(getLastDayOfMaking(beer).toLocalDate()))){
                ongoingBeers.add(beer);
            }
        }
        return ongoingBeers;
    }

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

    public boolean addNewBeer(Beer beer){
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.persist(beer);
            em.getTransaction().commit();
        }catch (IllegalArgumentException e){
            Logger.getGlobal().warning("Couldn't add new beer");
            return false; // Er dette ok??
        }finally {
            closeEM(em);
        }
        return true;
    }

    public int noOfTimesMade(String name){
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

    public void editBeerInstruction(){
        // Er fint mulig å faile, så burde ha en måte å endre det på
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

    public Instructions getNextInstruction(Beer beer){

        List<Instructions> instructions = this.getInstructionsForBeer(beer.getName());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = null;
        LocalDateTime date;
        Instructions nextInstruction = null;

        for (Instructions instruction : instructions) {
            date = beer.getStartTime().plusDays(instruction.getDaysAfterStart());
            if (!date.isBefore(now) && (next== null || date.isBefore(next))) {
                next = date;
                nextInstruction = instruction;
            }
        }
        /*
        Instructions instruction = null;
        EntityManager em = getEM();
        try {
            int id = nextInstruction.getInstructionId();
            em.getTransaction().begin();
            instruction = em.find(Instructions.class, id);
            em.getTransaction().commit();
        }finally {
            closeEM(em);
        }*/

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

    public List<Instructions> getInstructionsForBeer(String beerName){
        EntityManager em = getEM();
        List<Instructions> instructions = null;
        try {
            em.getTransaction().begin();
            Query q = em.createQuery("SELECT OBJECT(o) FROM Instructions o WHERE o.beerName LIKE :name").setParameter("name", beerName);
            instructions = q.getResultList();
            em.getTransaction().commit();
        }finally {
            closeEM(em);
        }
        return instructions;
    }


    public boolean regValues(Beer beer, double value, int day){
        // Legg til verdi i beer-objektet dag 1 (value1) eller dag 2 (value2)
        if(day == 1) beer.setValue1(value);
        else if(day == 2) beer.setValue2(value);
        else throw new IllegalArgumentException("Day can only be 1 or 2");
        // Sjekk om det er klart for tapping
        if(day == 1) return false; // Må sjekke to dager på rad, så dag en er den ikke klar
        return ready(beer); // Evnt. ta inn beerId og ha bruk en findmetode for å finne riktig objekt
    }

    private boolean ready(Beer beer){
        if(beer.getValue1() == -1 && beer.getValue2() == -1) return false;
        return Math.abs(beer.getValue1() - beer.getValue2()) <= 0.1; // TODO: sjekk - verdi ok?
    }

    public Beer findBeer(Beer beer){
        EntityManager em = getEM();
        try{
            return em.find(Beer.class, beer.getId());
        }finally {
            closeEM(em);
        }
    }


    private EntityManager getEM(){
        return emf.createEntityManager();
    }
    private void closeEM(EntityManager em){
        if(em != null && em.isOpen()) em.close();
    }


    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        try {
            emf = Persistence.createEntityManagerFactory("beer-pu");
            BeerRegister register = new BeerRegister(emf);

            Beer beer1 = new Beer("Henriks beste", "Lager", LocalDateTime.of(2020, 1, 13,0, 0));
            Beer beer2 = new Beer("Sommerøl", "Pils", LocalDateTime.of(2020, 6, 20, 12, 0));
            Beer beer3 = new Beer("Henriks beste", "Lager", LocalDateTime.of(2020, 5, 3, 0,0));
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

            boolean try1 = register.regValues(beer1, 0.3, 1);
            boolean try2 = register.regValues(beer1, 0.19, 2);
            boolean try3 = register.regValues(beer1, 0.25, 1);
            boolean try4 = register.regValues(beer1, 0.24, 2);
            //System.out.println("1: " + try1 + ", 2: " + try2 + ", 3: "+ try3 +", 4: "+ try4);

            register.addInstructionToBeer("Start", 0, 0, "Henriks beste");
            register.addInstructionToBeer("Tapp på flaske", 28, 0, "Henriks beste");
            register.addInstructionToBeer("Noe", 10,0, "Sommerøl");
            List<Instructions> recipe = register.getInstructionsForBeer("Henriks beste");
            System.out.println("-- RECIPE for Henriks beste");
            for(Instructions i: recipe) System.out.println(i.toString());

            System.out.println("-- Finding the last instruction");
            System.out.println(register.getLastInstruction("Henriks beste").toString());
            System.out.println("Date for last instruction on beer1 (Henriks beste): " + register.getLastDayOfMaking(beer3));

            System.out.println("-- Ongoing beers");
            register.getOngoingBeers().forEach(System.out::println);

            System.out.println("-- Next Instruction");
            System.out.println(register.getNextInstruction(beer3));

            System.out.println("-- All beer types");
            register.getAllBeerTypes().forEach(System.out::println);


        }finally {
            assert emf != null;
            emf.close();
        }
    }
}
