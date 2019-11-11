package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class PointOfInterestMessageDataReaderViewHelper
{

    public static ngems.c2f.PointOfInterestMessageDataReaderView narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PointOfInterestMessageDataReaderView) {
            return (ngems.c2f.PointOfInterestMessageDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.PointOfInterestMessageDataReaderView unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.PointOfInterestMessageDataReaderView) {
            return (ngems.c2f.PointOfInterestMessageDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
