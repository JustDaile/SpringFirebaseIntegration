package info.justdaile.session;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import info.justdaile.firebase.data.DatabaseReferenceObserver;
import info.justdaile.firebase.data.ObservableDatabaseReference;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FirebaseSocketClient extends DatabaseReferenceObserver {

    private WebSocketSession webSocketSession;
    private FirebaseToken firebaseToken;
    private String idToken;

    private static final ObjectMapper mapper = new ObjectMapper();

    public FirebaseSocketClient(WebSocketSession webSocketSession) {
        super(webSocketSession);
        this.webSocketSession = webSocketSession;
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    }

    public boolean isAuthorized(){
        return this.firebaseToken != null;
    }

    public void setFirebaseToken(FirebaseToken firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getIdToken() {
        return this.idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public FirebaseToken getFirebaseToken() {
        return this.firebaseToken;
    }

    public WebSocketSession getSocketSession() {
        return this.webSocketSession;
    }

    public void quickMessage(Object obj){
        try {
            String jsonMsg = mapper.writeValueAsString(obj);
            this.webSocketSession.sendMessage(new TextMessage(
                    jsonMsg
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
