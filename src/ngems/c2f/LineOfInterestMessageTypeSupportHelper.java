package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class LineOfInterestMessageTypeSupportHelper
{

    public static ngems.c2f.LineOfInterestMessageTypeSupport narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.LineOfInterestMessageTypeSupport) {
            return (ngems.c2f.LineOfInterestMessageTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.LineOfInterestMessageTypeSupport unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.LineOfInterestMessageTypeSupport) {
            return (ngems.c2f.LineOfInterestMessageTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
