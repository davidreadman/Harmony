package ngems.c2f;

public final class TextMessage {

    public ngems.c2f.MessageHeader header = new ngems.c2f.MessageHeader();
    public java.lang.String attributes = "";
    public java.lang.String message = "";

    public TextMessage() {
    }

    public TextMessage(
        ngems.c2f.MessageHeader _header,
        java.lang.String _attributes,
        java.lang.String _message)
    {
        header = _header;
        attributes = _attributes;
        message = _message;
    }

}
