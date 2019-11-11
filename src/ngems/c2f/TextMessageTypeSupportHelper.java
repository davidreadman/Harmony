package ngems.c2f;

import org.opensplice.dds.dcps.Utilities;

public final class TextMessageTypeSupportHelper
{

    public static ngems.c2f.TextMessageTypeSupport narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.TextMessageTypeSupport) {
            return (ngems.c2f.TextMessageTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static ngems.c2f.TextMessageTypeSupport unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof ngems.c2f.TextMessageTypeSupport) {
            return (ngems.c2f.TextMessageTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
