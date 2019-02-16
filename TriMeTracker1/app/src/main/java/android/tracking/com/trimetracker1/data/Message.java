package android.tracking.com.trimetracker1.data;

public class Message {
    private String title, sender, message;

    //@formatter:off
    public Message() {
    }
    //@formatter:on

    public Message(String title, String sender, String message) {
        this.title = title;
        this.sender = sender;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}