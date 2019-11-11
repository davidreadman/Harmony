package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class HeartbeatMessageTypeSupportHelper
{

    public static ngems.c2f.HeartbeatMessageTypeSupport narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.HeartbeatMessageTypeSupport) {
            return (ngems.c2f.HeartbeatMessageTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.HeartbeatMessageTypeSupport unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.HeartbeatMessageTypeSupport) {
            return (ngems.c2f.HeartbeatMessageTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
