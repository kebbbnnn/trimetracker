package android.tracking.com.trimetracker1.data;

import java.util.Collections;
import java.util.List;

public class UserData {
    public String id;
    public String name;
    public String email;
    public long createdAt;
    public List<String> contacts = Collections.emptyList();

    public UserData(String id, String name, String email, long createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    public UserData(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = System.currentTimeMillis();
    }
}
