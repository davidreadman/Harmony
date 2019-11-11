package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class PointOfInterestMessageTypeSupportHelper
{

    public static ngems.c2f.PointOfInterestMessageTypeSupport narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PointOfInterestMessageTypeSupport) {
            return (ngems.c2f.PointOfInterestMessageTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.PointOfInterestMessageTypeSupport unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PointOfInterestMessageTypeSupport) {
            return (ngems.c2f.PointOfInterestMessageTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
