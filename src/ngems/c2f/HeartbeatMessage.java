package ngems.c2f;

public final class HeartbeatMessage {

    public ngems.c2f.MessageHeader header = new ngems.c2f.MessageHeader();
    public java.lang.String attributes = "";

    public HeartbeatMessage() {
    }

    public HeartbeatMessage(
        ngems.c2f.MessageHeader _header,
        java.lang.String _attributes)
    {
        header = _header;
        attributes = _attributes;
    }

}
