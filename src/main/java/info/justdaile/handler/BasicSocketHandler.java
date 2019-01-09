package info.justdaile.handler;

import com.google.firebase.auth.FirebaseAuthException;
import info.justdaile.pojos.BasicMessage;
import info.justdaile.pojos.ErrorMessage;
import info.justdaile.pojos.ErrorType;
import info.justdaile.session.FirebaseClientChannel;
import info.justdaile.session.FirebaseSocketClient;
import info.justdaile.firebase.data.ObservableDatabaseReference;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicSocketHandler extends TextWebSocketHandler {

    private Map<String, FirebaseSocketClient> sessions = new HashMap<>();
    private Map<String, Object> services = new HashMap<>();

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Logger.getGlobal().log(Level.INFO, "Server: connection closed " + session.getId());
        sessions.remove(session.getId());
        Object o = services.remove(session.getId());
        if(o instanceof ObservableDatabaseReference){
            Logger.getGlobal().log(Level.INFO, "Server: disposing of odr service " + session.getId());
            ((ObservableDatabaseReference) o).dispose();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Logger.getGlobal().log(Level.INFO, "Server: connection established " + session.getId());
        sessions.put(session.getId(), new FirebaseSocketClient(session));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        String sessionCommand = textMessage.getPayload();

        Logger.getGlobal().log(Level.INFO, "Server: handling command " + sessionCommand);

        // client session will request certain services and have to supply operands
        FirebaseSocketClient activeSocketClient = sessions.get(session.getId());

        // firebase-listen-channel:authIdToken
        String[] command = sessionCommand.split(":");
        switch(command[0]){
            case "authorize":
                Logger.getGlobal().log(Level.INFO, "Server: authorizing");
                activeSocketClient.quickMessage(new BasicMessage("attempting authentication for user channel"));
                try {
                    activeSocketClient.setIdToken(command[1]);
                    FirebaseClientChannel firebaseClientChannel = FirebaseClientChannel.createClientChannel(activeSocketClient);
                    firebaseClientChannel.addObserver(activeSocketClient);
                    firebaseClientChannel.start();
                    activeSocketClient.quickMessage(new BasicMessage("authenticated success, " + activeSocketClient.getFirebaseToken().getEmail()));
                    this.services.put(session.getId(), firebaseClientChannel);
                } catch (FirebaseAuthException e) {
                    activeSocketClient.quickMessage(new ErrorMessage(e.getMessage(), ErrorType.Authentication));
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

    }

}
