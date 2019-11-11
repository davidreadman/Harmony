package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class HeartbeatMessageDataWriterHelper
{

    public static ngems.c2f.HeartbeatMessageDataWriter narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.HeartbeatMessageDataWriter) {
            return (ngems.c2f.HeartbeatMessageDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.HeartbeatMessageDataWriter unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.HeartbeatMessageDataWriter) {
            return (ngems.c2f.HeartbeatMessageDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
