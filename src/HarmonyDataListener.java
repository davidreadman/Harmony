
import java.util.ArrayList;
import java.util.List;

import ngems.c2f.PositionReportMessage;
import org.omg.dds.core.GuardCondition;
import org.omg.dds.core.event.DataAvailableEvent;
import org.omg.dds.core.event.RequestedDeadlineMissedEvent;
import org.omg.dds.sub.DataReader;
import org.omg.dds.sub.DataReaderAdapter;
import org.omg.dds.sub.Sample;
import org.omg.dds.sub.Subscriber.DataState;
import org.omg.dds.sub.ViewState;

import ngems.c2f.*;

public class HarmonyDataListener extends DataReaderAdapter<PositionReportMessage>
{


    boolean closed = false;
    GuardCondition guardCond = null;
    String receievedMessage ="";
    boolean thisMessageFlag = false;
    public HarmonyDataListener(GuardCondition myGC, boolean myMessageFlag)
    {
        guardCond = myGC;
        thisMessageFlag = myMessageFlag;
    }

    @Override
    public void onDataAvailable(DataAvailableEvent<PositionReportMessage> status)
    {
        /* Handle the incoming data here. */
        DataReader<PositionReportMessage> reader = status.getSource();
        DataState ds = reader.getParent().createDataState();
        ds = ds.withAnySampleState()
                .with(ViewState.NEW)
                .withAnyInstanceState();
        List<Sample<PositionReportMessage>> samples = new ArrayList<Sample<PositionReportMessage>>();

        reader.select().dataState(ds).read(samples);

        boolean hasValidData = false;
        if (samples.size() > 0)
        {
            System.out.println("=== [ListenerDataListener.on_data_available] - msgList.length : " + samples.size());
            String tempMessage ="";
            for (Sample<PositionReportMessage> sample : samples)
            {
                PositionReportMessage msg = sample.getData();
                if (msg != null)
                {
                    hasValidData = true;
                    System.out.println("    --- message received ---");
                    System.out.println("    userID  : " + msg.header.entityName);
                    System.out.println("    latitude : \"" + msg.latitude + "\"");
                    System.out.println("    longitude : \"" + msg.longitude + "\"");
                }
            }

            if (hasValidData)
            {
                // unblock the Waitset in Subscriber main loop
                guardCond.setTriggerValue(true);
                thisMessageFlag = true;
            } else
            {
                System.out.println("=== [ListenerDataListener.on_data_available] ===> hasValidData is false!");
            }
        }
    }

    @Override
    public void onRequestedDeadlineMissed(RequestedDeadlineMissedEvent<PositionReportMessage> status)
    {

       // System.out.println("=== [ListenerDataListener.on_requested_deadline_missed] : triggered");
        //System.out.println("=== [ListenerDataListener.on_requested_deadline_missed] : stopping");
        // unblock the waitset in Subscriber main loop
        guardCond.setTriggerValue(true);
        closed = true;
    }
}
