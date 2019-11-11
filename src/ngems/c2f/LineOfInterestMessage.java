package ngems.c2f;

public final class LineOfInterestMessage {

    public ngems.c2f.MessageHeader header = new ngems.c2f.MessageHeader();
    public double p1_latitude;
    public double p1_longitude;
    public double p2_latitude;
    public double p2_longitude;
    public java.lang.String label = "";
    public java.lang.String notes = "";
    public java.lang.String groupName = "";

    public LineOfInterestMessage() {
    }

    public LineOfInterestMessage(
        ngems.c2f.MessageHeader _header,
        double _p1_latitude,
        double _p1_longitude,
        double _p2_latitude,
        double _p2_longitude,
        java.lang.String _label,
        java.lang.String _notes,
        java.lang.String _groupName)
    {
        header = _header;
        p1_latitude = _p1_latitude;
        p1_longitude = _p1_longitude;
        p2_latitude = _p2_latitude;
        p2_longitude = _p2_longitude;
        label = _label;
        notes = _notes;
        groupName = _groupName;
    }

}
