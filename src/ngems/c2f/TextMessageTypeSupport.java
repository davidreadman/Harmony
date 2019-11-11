package ngems.c2f;

public class TextMessageTypeSupport extends org.opensplice.dds.dcps.TypeSupportImpl implements DDS.TypeSupportOperations
{
    private static final long serialVersionUID = 1L;

    private long copyCache;

    public TextMessageTypeSupport()
    {
        super("ngems::c2f::TextMessage",
              "",
              "header.senderName,header.messageId",
              null,
              ngems.c2f.TextMessageMetaHolder.metaDescriptor);
    }

    @Override
    protected DDS.DataWriter create_datawriter ()
    {
        return new TextMessageDataWriterImpl(this);
    }

    @Override
    protected DDS.DataReader create_datareader ()
    {
        return new TextMessageDataReaderImpl(this);
    }

    @Override
    protected DDS.DataReaderView create_dataview ()
    {
        return new TextMessageDataReaderViewImpl(this);
    }
}
