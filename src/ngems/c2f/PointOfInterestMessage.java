package ngems.c2f;

public final class PointOfInterestMessage {

    public ngems.c2f.MessageHeader header = new ngems.c2f.MessageHeader();
    public double latitude;
    public double longitude;
    public java.lang.String label = "";
    public java.lang.String notes = "";
    public java.lang.String groupName = "";

    public PointOfInterestMessage() {
    }

    public PointOfInterestMessage(
        ngems.c2f.MessageHeader _header,
        double _latitude,
        double _longitude,
        java.lang.String _label,
        java.lang.String _notes,
        java.lang.String _groupName)
    {
        header = _header;
        latitude = _latitude;
        longitude = _longitude;
        label = _label;
        notes = _notes;
        groupName = _groupName;
    }

}
