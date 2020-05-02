import javafx.collections.ObservableList;

public class BeerRegister {
    private ObservableList<Beer> beers;

    public BeerRegister(){
        // Load all beers from DB and add to list, beers
    }

    public void addNewBeer(){

    }

    // Tar inn id-en til den typen øl (f.eks. sommerøl)
    public void makeAgain(int id){

    }

    public boolean regValues(Beer beer, double value){
        // Legg til verdi i beer-objektet sin liste
        // Sjekk om det er klart for tapping
        return ready(beer); // Evnt. ta inn beerId og ha bruk en findmetode for å finne riktig objekt
    }

    private boolean ready(Object o){
        // Sjekk om de to siste er like
        return false;
    }

    public ObservableList<Beer> getBeers() {
        return beers;
    }
}
