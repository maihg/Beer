import GUI.Controller;
import javafx.collections.ObservableList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
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

    private List<Beer> getAllBeers() {
        EntityManager em = getEM();
        try {
            Query q = em.createQuery("SELECT OBJECT(o) FROM Beer o");
            return q.getResultList();
        }finally {
            closeEM(em);
        }
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

    /*
    Akkurat nå er planen at man ikke har en direkte linje mellom Beer og Instructions.
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

    /*
    private void addRecipeToBeer(List<Instructions> recipe, Beer beer){
        beer.setRecipe(recipe);
    }*/

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

    public Beer findBeer(String name){
        EntityManager em = getEM();
        try{
            return em.find(Beer.class, name);
        }finally {
            closeEM(em);
        }
    }

    public void editBeerInstruction(){
        // Er fint mulig å faile, så burde ha en måte å endre det på
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

            Beer beer1 = new Beer("Henriks beste", "Lager");
            Beer beer2 = new Beer("Sommerøl", "Pils");
            Beer beer3 = new Beer("Henriks beste", "Lager");
            register.addNewBeer(beer1);
            register.addNewBeer(beer2);
            register.addNewBeer(beer3);

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
            List<Instructions> recipe = register.getInstructionsForBeer("Henriks beste");
            System.out.println("-- RECIPE for Henriks beste");
            for(Instructions i: recipe) System.out.println(i.toString());

        }finally {
            assert emf != null;
            emf.close();
        }
    }
}
