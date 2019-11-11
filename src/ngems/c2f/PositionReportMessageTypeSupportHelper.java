package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class PositionReportMessageTypeSupportHelper
{

    public static ngems.c2f.PositionReportMessageTypeSupport narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PositionReportMessageTypeSupport) {
            return (ngems.c2f.PositionReportMessageTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.PositionReportMessageTypeSupport unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PositionReportMessageTypeSupport) {
            return (ngems.c2f.PositionReportMessageTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
