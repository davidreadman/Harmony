package ngems.c2f;

public class HeartbeatMessageTypeSupport extends org.opensplice.dds.dcps.TypeSupportImpl implements DDS.TypeSupportOperations
{
    private static final long serialVersionUID = 1L;

    private long copyCache;

    public HeartbeatMessageTypeSupport()
    {
        super("ngems::c2f::HeartbeatMessage",
              "",
              "header.senderName,header.messageId",
              null,
              ngems.c2f.HeartbeatMessageMetaHolder.metaDescriptor);
    }

    @Override
    protected DDS.DataWriter create_datawriter ()
    {
        return new HeartbeatMessageDataWriterImpl(this);
    }

    @Override
    protected DDS.DataReader create_datareader ()
    {
        return new HeartbeatMessageDataReaderImpl(this);
    }

    @Override
    protected DDS.DataReaderView create_dataview ()
    {
        return new HeartbeatMessageDataReaderViewImpl(this);
    }
}
