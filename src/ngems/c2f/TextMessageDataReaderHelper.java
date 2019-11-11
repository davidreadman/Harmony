package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class TextMessageDataReaderHelper
{

    public static ngems.c2f.TextMessageDataReader narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.TextMessageDataReader) {
            return (ngems.c2f.TextMessageDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.TextMessageDataReader unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.TextMessageDataReader) {
            return (ngems.c2f.TextMessageDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
