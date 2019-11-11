package ngems.c2f;

public class LineOfInterestMessageTypeSupport extends org.opensplice.dds.dcps.TypeSupportImpl implements DDS.TypeSupportOperations
{
    private static final long serialVersionUID = 1L;

    private long copyCache;

    public LineOfInterestMessageTypeSupport()
    {
        super("ngems::c2f::LineOfInterestMessage",
              "",
              "header.senderName,header.messageId",
              null,
              ngems.c2f.LineOfInterestMessageMetaHolder.metaDescriptor);
    }

    @Override
    protected DDS.DataWriter create_datawriter ()
    {
        return new LineOfInterestMessageDataWriterImpl(this);
    }

    @Override
    protected DDS.DataReader create_datareader ()
    {
        return new LineOfInterestMessageDataReaderImpl(this);
    }

    @Override
    protected DDS.DataReaderView create_dataview ()
    {
        return new LineOfInterestMessageDataReaderViewImpl(this);
    }
}
