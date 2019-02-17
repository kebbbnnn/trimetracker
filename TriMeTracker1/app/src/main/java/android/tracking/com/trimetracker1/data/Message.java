package android.tracking.com.trimetracker1.data;

public class Message {
    private String title, sender_name, sender_id, message;

    //@formatter:off
    public Message() {
    }
    //@formatter:on

    public Message(String title, String sender_name, String sender_id, String message) {
        this.title = title;
        this.sender_name = sender_name;
        this.sender_id = sender_id;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getSenderName() {
        return sender_name;
    }

    public String getSenderId() {
        return sender_id;
    }

    public String getMessage() {
        return message;
    }
}