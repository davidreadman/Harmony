package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class PositionReportMessageDataWriterHelper
{

    public static ngems.c2f.PositionReportMessageDataWriter narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PositionReportMessageDataWriter) {
            return (ngems.c2f.PositionReportMessageDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.PositionReportMessageDataWriter unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PositionReportMessageDataWriter) {
            return (ngems.c2f.PositionReportMessageDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
