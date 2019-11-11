package ngems.c2f;

public class PointOfInterestMessageTypeSupport extends org.opensplice.dds.dcps.TypeSupportImpl implements DDS.TypeSupportOperations
{
    private static final long serialVersionUID = 1L;

    private long copyCache;

    public PointOfInterestMessageTypeSupport()
    {
        super("ngems::c2f::PointOfInterestMessage",
              "",
              "header.senderName,header.messageId",
              null,
              ngems.c2f.PointOfInterestMessageMetaHolder.metaDescriptor);
    }

    @Override
    protected DDS.DataWriter create_datawriter ()
    {
        return new PointOfInterestMessageDataWriterImpl(this);
    }

    @Override
    protected DDS.DataReader create_datareader ()
    {
        return new PointOfInterestMessageDataReaderImpl(this);
    }

    @Override
    protected DDS.DataReaderView create_dataview ()
    {
        return new PointOfInterestMessageDataReaderViewImpl(this);
    }
}
