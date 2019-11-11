package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class TextMessageDataWriterHelper
{

    public static ngems.c2f.TextMessageDataWriter narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.TextMessageDataWriter) {
            return (ngems.c2f.TextMessageDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.TextMessageDataWriter unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.TextMessageDataWriter) {
            return (ngems.c2f.TextMessageDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
