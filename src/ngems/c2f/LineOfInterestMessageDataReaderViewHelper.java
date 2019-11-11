package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class LineOfInterestMessageDataReaderViewHelper
{

    public static ngems.c2f.LineOfInterestMessageDataReaderView narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.LineOfInterestMessageDataReaderView) {
            return (ngems.c2f.LineOfInterestMessageDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.LineOfInterestMessageDataReaderView unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.LineOfInterestMessageDataReaderView) {
            return (ngems.c2f.LineOfInterestMessageDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
