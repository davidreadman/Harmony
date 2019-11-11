package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class HeartbeatMessageDataReaderHelper
{

    public static ngems.c2f.HeartbeatMessageDataReader narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.HeartbeatMessageDataReader) {
            return (ngems.c2f.HeartbeatMessageDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.HeartbeatMessageDataReader unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.HeartbeatMessageDataReader) {
            return (ngems.c2f.HeartbeatMessageDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
