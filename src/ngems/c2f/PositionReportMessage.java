package ngems.c2f;

import java.io.Serializable;

public final class PositionReportMessage implements Serializable{

	private static final long serialVersionUID = -8395221547725890073L;
	public ngems.c2f.MessageHeader header = new ngems.c2f.MessageHeader();
    public double latitude;
    public double longitude;
    public java.lang.String state = "";

    public PositionReportMessage() {
    }

    public PositionReportMessage(
        ngems.c2f.MessageHeader _header,
        double _latitude,
        double _longitude,
        java.lang.String _state)
    {
        header = _header;
        latitude = _latitude;
        longitude = _longitude;
        state = _state;
    }

}
