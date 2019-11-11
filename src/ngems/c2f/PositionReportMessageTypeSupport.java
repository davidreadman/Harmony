package ngems.c2f;

public class PositionReportMessageTypeSupport extends org.opensplice.dds.dcps.TypeSupportImpl implements DDS.TypeSupportOperations
{
    private static final long serialVersionUID = 1L;

    private long copyCache;

    public PositionReportMessageTypeSupport()
    {
        super("ngems::c2f::PositionReportMessage",
              "",
              "header.senderName,header.messageId",
              null,
              ngems.c2f.PositionReportMessageMetaHolder.metaDescriptor);
    }

    @Override
    protected DDS.DataWriter create_datawriter ()
    {
        return new PositionReportMessageDataWriterImpl(this);
    }

    @Override
    protected DDS.DataReader create_datareader ()
    {
        return new PositionReportMessageDataReaderImpl(this);
    }

    @Override
    protected DDS.DataReaderView create_dataview ()
    {
        return new PositionReportMessageDataReaderViewImpl(this);
    }
}
