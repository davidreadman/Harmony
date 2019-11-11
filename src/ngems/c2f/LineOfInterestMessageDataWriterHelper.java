package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class LineOfInterestMessageDataWriterHelper
{

    public static ngems.c2f.LineOfInterestMessageDataWriter narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.LineOfInterestMessageDataWriter) {
            return (ngems.c2f.LineOfInterestMessageDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.LineOfInterestMessageDataWriter unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.LineOfInterestMessageDataWriter) {
            return (ngems.c2f.LineOfInterestMessageDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
