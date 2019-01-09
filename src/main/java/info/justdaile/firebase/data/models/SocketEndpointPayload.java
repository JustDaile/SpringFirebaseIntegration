package info.justdaile.firebase.data.models;

import com.google.firebase.database.DataSnapshot;

public class SocketEndpointPayload {

    public static String ADDED = "added";
    public static String CHANGED = "changed";
    public static String REMOVED = "removed";
    public static String MOVED = "moved";

    public String id;
    public String type;
    public Object data;

    public SocketEndpointPayload(String id, String type, DataSnapshot snapshot) {
        this.id = id;
        this.type = type;
        this.data = snapshot.getValue();
    }

}
