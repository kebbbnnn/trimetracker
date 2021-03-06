package android.tracking.com.trimetracker1.data;

import java.util.Collections;
import java.util.List;

public class UserData {
    public String id;
    public String name;
    public String email;
    public String mobile;
    public long createdAt;
    public List<UserData> contacts = Collections.emptyList();

    //@formatter:off
    public UserData(){}
    //@formatter:on

    public UserData(String id, String name, String email, String mobile, long createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.createdAt = createdAt;
    }

    public UserData(String id, String name, String email, String mobile) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.createdAt = System.currentTimeMillis();
    }
}
