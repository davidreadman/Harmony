package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class PointOfInterestMessageDataReaderHelper
{

    public static ngems.c2f.PointOfInterestMessageDataReader narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PointOfInterestMessageDataReader) {
            return (ngems.c2f.PointOfInterestMessageDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.PointOfInterestMessageDataReader unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PointOfInterestMessageDataReader) {
            return (ngems.c2f.PointOfInterestMessageDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
