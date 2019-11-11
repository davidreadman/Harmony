package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class PointOfInterestMessageDataWriterHelper
{

    public static ngems.c2f.PointOfInterestMessageDataWriter narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PointOfInterestMessageDataWriter) {
            return (ngems.c2f.PointOfInterestMessageDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.PointOfInterestMessageDataWriter unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PointOfInterestMessageDataWriter) {
            return (ngems.c2f.PointOfInterestMessageDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
