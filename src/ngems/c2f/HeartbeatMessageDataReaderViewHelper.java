package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class HeartbeatMessageDataReaderViewHelper
{

    public static ngems.c2f.HeartbeatMessageDataReaderView narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.HeartbeatMessageDataReaderView) {
            return (ngems.c2f.HeartbeatMessageDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.HeartbeatMessageDataReaderView unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.HeartbeatMessageDataReaderView) {
            return (ngems.c2f.HeartbeatMessageDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
