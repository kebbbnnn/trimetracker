package android.tracking.com.trimetracker1.data;

public class Message {
    private String sessionId, event, senderName, senderId, receiverId, plateNumber;
    private long createdAt;

    //@formatter:off
    public Message() {
    }
    //@formatter:on

    public Message(String sessionId, String event, String senderName, String senderId, String receiverId, String plateNumber, long createdAt) {
        this.sessionId = sessionId;
        this.event = event;
        this.senderName = senderName;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.plateNumber = plateNumber;
        this.createdAt = createdAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getEvent() {
        return event;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}