package info.justdaile.firebase.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.*;
import info.justdaile.firebase.data.models.SocketEndpointPayload;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketEndpointSession implements ChildEventListener {

    private String context;
    private WebSocketSession session;

    public SocketEndpointSession(String context, WebSocketSession session){
        this.context = context;
        Logger.getGlobal().log(Level.INFO, "Firebase Endpoint " + this.context + ": Created!");
        FirebaseDatabase.getInstance()
                .getReference(this.context)
                .addChildEventListener(this);
        this.session = session;
    }

    @Override
    public void onCancelled(DatabaseError error) {
        Logger.getGlobal().log(Level.INFO, "Firebase Endpoint " + this.context + ": Cancelled " + error.getDetails());
    }


    @Override
    public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
        Logger.getGlobal().log(Level.INFO, "Firebase Endpoint " + this.context + ": Child added");
        this.send(snapshot, SocketEndpointPayload.ADDED);
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
        Logger.getGlobal().log(Level.INFO, "Firebase Endpoint " + this.context + ": Child changed");
        this.send(snapshot, SocketEndpointPayload.CHANGED);
    }

    @Override
    public void onChildRemoved(DataSnapshot snapshot) {
        Logger.getGlobal().log(Level.INFO, "Firebase Endpoint " + this.context + ": Child removed");
        this.send(snapshot, SocketEndpointPayload.REMOVED);
    }

    @Override
    public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
        Logger.getGlobal().log(Level.INFO, "Firebase Endpoint " + this.context + ": Child moved");
        this.send(snapshot, SocketEndpointPayload.MOVED);
    }

    public void dispose(){
        FirebaseDatabase.getInstance()
                .getReference(this.context)
                .removeEventListener(this);
    }

    private void send(DataSnapshot snapshot, String type){
        if(session.isOpen()) {
            SocketEndpointPayload payload = new SocketEndpointPayload(snapshot.getKey(), type, snapshot);
            try{
                session.sendMessage(new TextMessage(
                        new ObjectMapper().writeValueAsString(payload)
                ));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
