import gov.nasa.worldwind.awt.WorldWindowGLCanvas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class JFrameGuiActions
{
    boolean myMessageFlag = false;
    public JFrameGuiActions(DisplayWW displayWW, HarmonyDataPublisher publishdata, NodeData[] nodeData)
    {
        //setup the data subscriber here so it can create an event.
        DDSPositionMessage dDSPositionMessage = new DDSPositionMessage();

        PropertyChangeListener pcl = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getSource() instanceof DDSPositionMessage) {
                    DDSPositionMessage pd = (DDSPositionMessage)evt.getSource();
                    System.out.println(String.format("Received event from %s: %s has been changed from %s to %s", pd.getDDSPositionMessage(),evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
                }
            }
        };
        dDSPositionMessage.addPropertyChangeListener(pcl);

        new HarmonyDataSubscriber(null, dDSPositionMessage);
        //new HarmonyMetricsSubscriber();
        System.out.println("waiting for data");
        //setup logger
        WriteLog logger = new WriteLog();


        // frame.getContentPane().setLayout(new FlowLayout());
        JFrame frame = new JFrame("World Wind");
        JButton publishButton = new JButton("Publish", new ImageIcon("publish.png"));
        publishButton.setBounds(100, 100, 140, 40);
        frame.add(publishButton);

        JButton logButton = new JButton("Log", new ImageIcon("publish.png"));
        logButton.setBounds(250, 110, 140, 40);
        frame.add(logButton);

        JButton cloButton = new JButton("close", new ImageIcon("publish.png"));
        cloButton.setBounds(400, 120, 140, 40);
        frame.add(cloButton);

        JButton lastButton = new JButton("last", new ImageIcon("publish.png"));
        lastButton.setBounds(100, 300, 140, 40);
        frame.add(lastButton);

        JTextField JT = new JTextField("Nodes");
        JT.setBounds(1000, 100, 200, 500);
        frame.add(JT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // build Java swing interface

        frame.add(displayWW.canvas);
        frame.setSize(2000, 2000);
        frame.setVisible(true);


        logButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {


                //send out data for all nodes
                int numberOfNodes = nodeData.length;
                String writeableString = logger.getTimeStamp()+ ",";
                for (int i = 0; i < numberOfNodes; i++)
                {
                    writeableString = writeableString + nodeData[i].NodeUUID + ",";
                    writeableString = writeableString + nodeData[i].Lat + ",";
                    writeableString = writeableString + nodeData[i].Lon + ",";
                    writeableString = writeableString + "This will be a future metric,";
                    writeableString = writeableString + "This will be a future decision,";
                }
                writeableString = writeableString + "\n";
                logger.writeStringToFile(writeableString);
                System.out.println("written " + writeableString);
                System.out.println("messageflag " + myMessageFlag);
                logger.Flush();
                //JDialog d = new JDialog(frame, "Hello", true);

                //d.setLocationRelativeTo(frame);
                // d.setVisible(true);
            }
        });
        cloButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                logger.closeAndFlush();
                System.out.println("closed");
            }
        });
        publishButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                //send out data for all nodes
                int numberOfNodes = nodeData.length;
                for (int i = 0; i < numberOfNodes; i++)
                {
                    publishdata.HarmonyPublish(nodeData[i]);
                }
                System.out.println("published");
                //JDialog d = new JDialog(frame, "Hello", true);

                //d.setLocationRelativeTo(frame);
                // d.setVisible(true);
            }

        });

    }
}
