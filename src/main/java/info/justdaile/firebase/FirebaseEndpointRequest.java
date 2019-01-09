package info.justdaile.firebase;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirebaseEndpointRequest {

    public final <T> T doRequest(String url, Object object, Class<T> type) {
        try {
            String json = new ObjectMapper().writeValueAsString(object);

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("content-type", "text/json");
            OutputStream requestStream = connection.getOutputStream();
            requestStream.write(json.getBytes());
            requestStream.flush();
            requestStream.close();

            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                return new ObjectMapper().readValue(connection.getInputStream(), type);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
