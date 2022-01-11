package retea.reteadesocializare.domain.observer;

import java.util.ArrayList;
import java.util.List;

public abstract class ListObservable {
    List<ListObserver> observers = new ArrayList<>();

    public void addObserver(ListObserver observer){
        observers.add(observer);
    }

    public void notifyObservers(){
        for(ListObserver observer:observers)
            observer.update();
    }
}
