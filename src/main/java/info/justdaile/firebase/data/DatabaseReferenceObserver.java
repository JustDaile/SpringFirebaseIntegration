package info.justdaile.firebase.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.justdaile.firebase.data.models.SocketEndpointPayload;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseReferenceObserver implements Observer {

    public WebSocketSession session;

    public DatabaseReferenceObserver(WebSocketSession session){
        this.session = session;
    }

    @Override
    public void update(Observable observable, Object value) {
        Logger.getGlobal().log(Level.INFO, "DatabaseReferenceObserver " + session.getId() + ": Update");
        if(session.isOpen()) {
            SocketEndpointPayload snapshot = (SocketEndpointPayload) value;

            try{
                session.sendMessage(new TextMessage(
                        new ObjectMapper().writeValueAsString(snapshot)
                ));
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            observable.deleteObserver(this);
            Logger.getGlobal().log(Level.INFO, "DatabaseReferenceObserver " + session.getId() + ": Closed|Unsubscribed");
        }
    }

    protected String getId(){
        return this.session.getId();
    }

}
