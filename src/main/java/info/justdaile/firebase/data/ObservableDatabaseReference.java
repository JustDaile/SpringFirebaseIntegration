package info.justdaile.firebase.data;

import com.google.firebase.database.*;
import info.justdaile.firebase.data.models.SocketEndpointPayload;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObservableDatabaseReference extends Observable implements ChildEventListener, DatabaseReference.CompletionListener {

    private Vector<String> ids = new Vector<>();
    private String context;

    public ObservableDatabaseReference(String context){
        this.context = context;
        Logger.getGlobal().log(Level.INFO, "ObservableDatabaseReference " + this.context + ": Created!");
    }

    public void start(){
        FirebaseDatabase.getInstance()
                .getReference(this.context)
                .addChildEventListener(this);
    }

    public void dispose(){
        this.deleteObservers();
        FirebaseDatabase.getInstance()
                .getReference(this.context)
                .removeEventListener(this);
    }

    @Override
    public void notifyObservers(Object data) {
        this.setChanged();
        super.notifyObservers(data);
    }

    @Override
    public synchronized void addObserver(Observer o) {
        String id = ((DatabaseReferenceObserver) o).getId();
        if(!this.hasObserver(id)){
            ids.add(id);
            super.addObserver(o);
        }
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        super.deleteObserver(o);
        ids.remove(((DatabaseReferenceObserver) o).getId());
    }

    @Override
    public synchronized void deleteObservers() {
        super.deleteObservers();
    }

    public boolean hasObserver(String id){
        return ids.contains(id);
    }

    @Override
    public void onCancelled(DatabaseError error) {
        Logger.getGlobal().log(Level.INFO, "ObservableDatabaseReference " + this.context + ": Cancelled " + error.getDetails());
        this.deleteObservers();
    }

    @Override
    public void onComplete(DatabaseError error, DatabaseReference ref) {
        Logger.getGlobal().log(Level.INFO, "ObservableDatabaseReference " + this.context + ": Completed " + error.getDetails());
    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
        Logger.getGlobal().log(Level.INFO, "ObservableDatabaseReference " + this.context + ": Child added");
        notifyObservers(new SocketEndpointPayload(snapshot.getKey(), SocketEndpointPayload.ADDED, snapshot));
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
        Logger.getGlobal().log(Level.INFO, "ObservableDatabaseReference " + this.context + ": Child changed");
        notifyObservers(new SocketEndpointPayload(snapshot.getKey(), SocketEndpointPayload.CHANGED, snapshot));
    }

    @Override
    public void onChildRemoved(DataSnapshot snapshot) {
        Logger.getGlobal().log(Level.INFO, "ObservableDatabaseReference " + this.context + ": Child removed");
        notifyObservers(new SocketEndpointPayload(snapshot.getKey(), SocketEndpointPayload.REMOVED, snapshot));
    }

    @Override
    public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
        Logger.getGlobal().log(Level.INFO, "ObservableDatabaseReference " + this.context + ": Child moved");
        notifyObservers(new SocketEndpointPayload(snapshot.getKey(), SocketEndpointPayload.MOVED, snapshot));
    }

}
