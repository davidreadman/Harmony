package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class TextMessageDataReaderViewHelper
{

    public static ngems.c2f.TextMessageDataReaderView narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.TextMessageDataReaderView) {
            return (ngems.c2f.TextMessageDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.TextMessageDataReaderView unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.TextMessageDataReaderView) {
            return (ngems.c2f.TextMessageDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
