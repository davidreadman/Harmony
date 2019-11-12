import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.util.BasicDragger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class JFrameGuiActions
{

    public JFrameGuiActions(DisplayWW displayWW, HarmonyDataPublisher publishData, NodeData[] nodeData)
    {
        /* set up logger to write to CSV files */
        WriteLog logger = new WriteLog();

        /* setup the binding of properties to allow for change monitoring across threads */
        DDSPositionMessage dDSPositionMessage = new DDSPositionMessage();
        PropertyChangeListener pcl = new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                if (evt.getSource() instanceof DDSPositionMessage)
                {
                    DDSPositionMessage pd = (DDSPositionMessage) evt.getSource();
                    System.out.println(String.format("Received event from %s: %s has been changed from %s to %s", pd.getDDSPositionMessage(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
                }
            }
        };
        dDSPositionMessage.addPropertyChangeListener(pcl);

        /* set up DDS Subscriber/Listeners with bound properties */
        new HarmonyDataSubscriber(null, dDSPositionMessage);
        //new HarmonyMetricsSubscriber();

        /* set up the GUI items */
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
        /* add the worldwind canvas to the JFrame */
        frame.add(displayWW.canvas);
        frame.setSize(2000, 2000);
        frame.setVisible(true);


        logButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                //send out data for all nodes
                int numberOfNodes = nodeData.length;
                String writeableString = logger.getTimeStamp() + ",";
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

                logger.Flush();

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
                    publishData.HarmonyPublish(nodeData[i]);
                }
                System.out.println("published");

            }

        });



    }
}
