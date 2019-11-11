package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class LineOfInterestMessageDataReaderHelper
{

    public static ngems.c2f.LineOfInterestMessageDataReader narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.LineOfInterestMessageDataReader) {
            return (ngems.c2f.LineOfInterestMessageDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.LineOfInterestMessageDataReader unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.LineOfInterestMessageDataReader) {
            return (ngems.c2f.LineOfInterestMessageDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
