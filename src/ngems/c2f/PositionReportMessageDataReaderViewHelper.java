package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class PositionReportMessageDataReaderViewHelper
{

    public static ngems.c2f.PositionReportMessageDataReaderView narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PositionReportMessageDataReaderView) {
            return (ngems.c2f.PositionReportMessageDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.PositionReportMessageDataReaderView unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PositionReportMessageDataReaderView) {
            return (ngems.c2f.PositionReportMessageDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
