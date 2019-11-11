package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class PositionReportMessageDataReaderHelper
{

    public static ngems.c2f.PositionReportMessageDataReader narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PositionReportMessageDataReader) {
            return (ngems.c2f.PositionReportMessageDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.PositionReportMessageDataReader unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PositionReportMessageDataReader) {
            return (ngems.c2f.PositionReportMessageDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
