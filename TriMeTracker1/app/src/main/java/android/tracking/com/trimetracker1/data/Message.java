package android.tracking.com.trimetracker1.data;

public class Message {
    private String session_id, event, sender_name, sender_id, receiver_id, plate_number;

    //@formatter:off
    public Message() {
    }
    //@formatter:on

    public Message(String session_id, String event, String sender_name, String sender_id, String receiver_id, String plate_number) {
        this.session_id = session_id;
        this.event = event;
        this.sender_name = sender_name;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.plate_number = plate_number;
    }

    public String getSessionId() {
        return session_id;
    }

    public String getEvent() {
        return event;
    }

    public String getSenderName() {
        return sender_name;
    }

    public String getSenderId() {
        return sender_id;
    }

    public String getReceiverId() {
        return receiver_id;
    }

    public String getPlateNumber() {
        return plate_number;
    }
}