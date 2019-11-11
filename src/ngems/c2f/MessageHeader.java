package ngems.c2f;

import java.io.Serializable;

public final class MessageHeader implements Serializable{
	private static final long serialVersionUID = 4884247105213254594L;
	public java.lang.String senderName = "";
    public java.lang.String entityName = "";
    public java.lang.String entityType = "";
    public java.lang.String[] recipientNames = new java.lang.String[0];
    public long timestampMs;
    public int messageId;

    public MessageHeader() {
    }

    public MessageHeader(
        java.lang.String _senderName,
        java.lang.String _entityName,
        java.lang.String _entityType,
        java.lang.String[] _recipientNames,
        long _timestampMs,
        int _messageId)
    {
        senderName = _senderName;
        entityName = _entityName;
        entityType = _entityType;
        recipientNames = _recipientNames;
        timestampMs = _timestampMs;
        messageId = _messageId;
    }

}
