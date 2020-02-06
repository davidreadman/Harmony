/*
Modified from Opensplice ListenerDataPublisher
 *
 */

import ngems.c2f.*;
import org.omg.dds.core.Duration;
import org.omg.dds.core.InstanceHandle;
import org.omg.dds.core.ServiceEnvironment;
import org.omg.dds.core.policy.*;
import org.omg.dds.core.status.Status;
import org.omg.dds.domain.DomainParticipant;
import org.omg.dds.domain.DomainParticipantFactory;
import org.omg.dds.pub.DataWriter;
import org.omg.dds.pub.DataWriterQos;
import org.omg.dds.pub.Publisher;
import org.omg.dds.pub.PublisherQos;
import org.omg.dds.topic.Topic;
import org.omg.dds.topic.TopicQos;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class HarmonyDataPublisher
{
    static String partitionName = "SNAnalysis";

    public HarmonyDataPublisher()
    {

    }
    void HarmonyPublish(NodeData thisNodeData, boolean debugEnabled)
    {
        try
        {
            System.setProperty(
                    ServiceEnvironment.IMPLEMENTATION_CLASS_NAME_PROPERTY,
                    "org.opensplice.dds.core.OsplServiceEnvironment");
            ServiceEnvironment env = ServiceEnvironment
                    .createInstance(HarmonyDataPublisher.class.getClassLoader());

            DomainParticipantFactory dpf = DomainParticipantFactory.getInstance(env);

            // create Domain Participant
            DomainParticipant participant = dpf.createParticipant();

            // create Topic with reliable, transient and deadline qos
            PolicyFactory policyFactory = env.getSPI().getPolicyFactory();
            Reliability reliable = policyFactory.Reliability().withReliable();
            Durability durability = policyFactory.Durability().withTransient();
            Deadline deadline = policyFactory.Deadline().withPeriod(Duration.newDuration(1, TimeUnit.SECONDS, env));
            TopicQos topicQos = participant.getDefaultTopicQos().withPolicies(reliable, durability, deadline);
            Collection<Class<? extends Status>> status = new HashSet<Class<? extends Status>>();
            Topic<ngems.c2f.PositionReportMessage> topic = participant.createTopic("ListenerData_Msg", ngems.c2f.PositionReportMessage.class, topicQos, null, status);

            // create Publisher with partition qos
            Partition partition = policyFactory.Partition().withName(partitionName);
            PublisherQos pubQos = participant.getDefaultPublisherQos().withPolicy(partition);
            Publisher pub = participant.createPublisher(pubQos);

            // create DataWriter with autDisposeUnregisteredInstances qos
            WriterDataLifecycle wdlq = policyFactory.WriterDataLifecycle().withAutDisposeUnregisteredInstances(false);
            DataWriterQos dwQos = pub.copyFromTopicQos(pub.getDefaultDataWriterQos().withPolicy(wdlq), topic.getQos());
            DataWriter<ngems.c2f.PositionReportMessage> writer = pub.createDataWriter(topic, dwQos);

            ngems.c2f.PositionReportMessage positionReportMessage = new ngems.c2f.PositionReportMessage();



            if(debugEnabled)
                System.out.println("publishing node "+thisNodeData.NodeUUID);

            positionReportMessage.header.entityName = thisNodeData.NodeUUID;
            positionReportMessage.latitude = thisNodeData.currentLocation.asDegreesArray()[0];
            positionReportMessage.longitude = thisNodeData.currentLocation.asDegreesArray()[1];

                writer.write(positionReportMessage, InstanceHandle.nilHandle(env));



         /*   try
            {
                Thread.sleep(3000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }*/
            // clean up
            participant.close();
        } catch (Exception e)
        {
            if(debugEnabled)
                System.out.println("Error occured: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
