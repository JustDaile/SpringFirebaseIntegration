package info.justdaile.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.*;
import com.google.firebase.database.FirebaseDatabase;
import info.justdaile.firebase.FirebaseAuthRestEndpoints;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping(method = RequestMethod.POST)
public class BasicRestController {

    @RequestMapping(value = "/order")
    public boolean changeStatusOfOrder(@RequestParam("orderId") String orderId, @RequestParam("status") String status, @RequestParam("idtoken") String idToken) {
        try {
            // add updated status and query database
            Map<String, Object> updated = new HashMap<>();
            updated.put("status", status);
            FirebaseDatabase.getInstance()
                    .getReference(getReferenceFor(idToken) + "/active_session/" + orderId)
                    .updateChildrenAsync(updated).get();
            return true;
        } catch (Exception e) {
            // user isn't authenticated so we got a FirebaseAuthException
            return false;
        }
    }

    @RequestMapping(value = "/status")
    public boolean toggleAccountOnlineStatus(@RequestParam("idtoken") String idToken, @RequestParam("toggle") boolean toggle) {
        try {
            // add updated status and query database
            Map<String, Object> updated = new HashMap<>();
            updated.put("online", toggle);
            FirebaseDatabase.getInstance()
                    .getReference(getReferenceFor(idToken))
                    .updateChildrenAsync(updated).get();
            return true;
        } catch (Exception e) {
            // user isn't authenticated so we got a FirebaseAuthException
            return false;
        }
    }

    @RequestMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public void UserLogin(@RequestParam("email") String email, @RequestParam("password") String password, HttpServletResponse httpServletResponse){
        Logger.getGlobal().log(Level.INFO, "Server: attempting login");

        /*
                Shouldn't have to worry about mysql injection attacks here right?
                - its not sql after-all
                - sending bad email to api will just result in failure
                - this is a calculated decision
                - don't bash my bad code!
         */
        final String jsonLoginRequest =
                "{'email':'" + email + "','password':'" + password + "','returnSecureToken':'true'}";

        try {
            int attempts = 0;
            int responseCode = 0;

            HttpURLConnection firebaseServerConnection = (HttpURLConnection) new URL(
                    FirebaseAuthRestEndpoints.verifyPasswordEndpoint()
            ).openConnection();

            while(responseCode != HttpURLConnection.HTTP_OK){
                Logger.getGlobal().log(Level.INFO, "Server: connecting to firebase authentication endpoint");

                firebaseServerConnection.setRequestMethod("POST");
                firebaseServerConnection.setDoOutput(true);
                firebaseServerConnection.setRequestProperty("content-type", "text/json");
                OutputStream requestStream = firebaseServerConnection.getOutputStream();
                requestStream.write(jsonLoginRequest.getBytes());
                requestStream.flush();

                responseCode = firebaseServerConnection.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK){
                    int timeout = 1000 * (10 * attempts + 1);
                    Logger.getGlobal().log(Level.INFO, "Server: unable to login retrying in " + timeout / 1000 + " seconds");
                    Thread.sleep(timeout);
                }else{
                    requestStream.close();
                    Logger.getGlobal().log(Level.INFO, "Server: closing request stream");
                }
            }

            JsonNode node = new ObjectMapper().readTree(firebaseServerConnection.getInputStream());
            Logger.getGlobal().log(Level.INFO, "Server: json from login endpoint " + node.toString());

            SessionCookieOptions options = SessionCookieOptions.builder()
                    .setExpiresIn(TimeUnit.DAYS.toMillis(7))
                    .build();

            Cookie sessionCookie = new Cookie("DSESSION",
                    FirebaseAuth.getInstance().createSessionCookieAsync(node.get("idToken").asText(), options).get()
            );

            sessionCookie.setMaxAge(-1);
            httpServletResponse.addCookie(sessionCookie);
        } catch (IOException e1){
            // Trying to open a connection to URL that doesn't exist
            //  - shouldn't happen unless firebase changes it endpoint or connection is blocked
            // Failed to open output stream
            //  - Unlikely to happen unless connection somehow disconnects after connecting to URL
            // Failed to get response code after sending request
            //  - Unlikely to happen unless connection somehow disconnects after sending request to server
            // Unable to close input stream
            //  - Likely already closed
            // Json unable to parse string into JsonNode
            //  - Shouldn't happen unless 'jsonLoginRequest' is modified.
            e1.printStackTrace();
        } catch (InterruptedException | ExecutionException  e2){
            // Unable to create session cookie async
            //  - Unlikely thread will be interrupted
            //  - Computational errors should happen with valid token is created
            //    & application is configure correctly
            e2.printStackTrace();
        }
    }

    @RequestMapping("/logout")
    public void DestroyUserCookie(HttpServletResponse httpServletResponse) {
        Cookie sessionCookie = new Cookie("DSESSION", ""); // clear out data
        sessionCookie.setMaxAge(0); // zero deletes cookie
        httpServletResponse.addCookie(sessionCookie); // overwrite cookie
    }

    private final String getReferenceFor(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance()
                .verifySessionCookie(idToken).getEmail()
                .replace("@", "-")
                .replace(".", "-");
    }

}
