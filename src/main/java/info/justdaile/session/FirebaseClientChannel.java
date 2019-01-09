package info.justdaile.session;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import info.justdaile.firebase.data.ObservableDatabaseReference;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FirebaseClientChannel extends ObservableDatabaseReference {

    private static final String CHANNEL_SUFFIX = "/live";

    private FirebaseClientChannel(String context) {
        super(context);
    }

    public static final FirebaseClientChannel createClientChannel(FirebaseSocketClient client) throws FirebaseAuthException {
        if(!client.isAuthorized()){
            try {
                FirebaseToken firebaseToken;
                String idToken = client.getIdToken();
                if(idToken != null){
                    firebaseToken = FirebaseAuth.getInstance()
                            .verifySessionCookieAsync(idToken)
                            .get();
                    client.setFirebaseToken(firebaseToken);
                }else{
                    Logger.getGlobal().log(Level.INFO, "Server: MISSING_ID_TOKEN Client idToken not found. " + client.getSocketSession().getId());
                    throw new FirebaseAuthException("MISSING_ID_TOKEN", "Client idToken not found.");
                }
                Logger.getGlobal().log(Level.INFO, "Server: authorized client " + client.getSocketSession().getId());
                return new FirebaseClientChannel(
                        firebaseToken.getEmail().replace("@", "-").replace(".", "-").concat(FirebaseClientChannel.CHANNEL_SUFFIX)
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Logger.getGlobal().log(Level.INFO, "Server: BAD_AUTHENTICATION_ATTEMPT Already authorized. " + client.getSocketSession().getId());
        throw new FirebaseAuthException("BAD_AUTHENTICATION_ATTEMPT", "Already authorized.");
    }

}
