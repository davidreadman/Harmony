import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.util.BasicDragger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class JFrameGuiActions
{

    WriteLog logger;
    boolean loggingFlag = false;

    public JFrameGuiActions(DisplayWW displayWW, HarmonyDataPublisher publishData, NodeData[] nodeData)
    {
        /* set up logger to write to CSV files */
        /*disabled so new logfiles are not created each time */
       //

        /* setup the binding of properties to allow for change monitoring across threads */
        DDSPositionMessage dDSPositionMessage = new DDSPositionMessage();
        /* listen for a change in the position message */
        PropertyChangeListener pcl = new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                if (evt.getSource() instanceof DDSPositionMessage)
                {
                    DDSPositionMessage pd = (DDSPositionMessage) evt.getSource();
                    System.out.println(String.format("Received event from %s: %s has been changed from %s to %s", pd.getDDSPositionMessage(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
                    //on a DDS Message is the *current* best time to make a decision on a new position
                    //a nice quick test could be a new random location based on existing location
                    //curently living in timer, but move to here next after the position message has been altered to include node data
                }
            }
        };
        dDSPositionMessage.addPropertyChangeListener(pcl);

        /* set up DDS Subscriber/Listeners with bound properties */
        new HarmonyDataSubscriber(null, dDSPositionMessage);
        //new HarmonyMetricsSubscriber();

        /* set up the GUI items */
        // frame.getContentPane().setLayout(new FlowLayout());
    /* set up default UI fonts */
        setUIFont (new javax.swing.plaf.FontUIResource("Serif",Font.PLAIN,30));

        JFrame frame = new JFrame("World Wind");
        frame.setDefaultLookAndFeelDecorated(true);


       /*
        https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
        */
        JMenuBar menuBar;
        JMenu  menu,submenu,aboutMenu;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbMenuItem, dDSNodeMenuItem, pubMenuItem, logMenuItem;
        JRadioButtonMenuItem  dDSMetMenuItem, stopPubMenuItem, stopLogMenuItem;

        menuBar = new JMenuBar();
        menu = new JMenu("Options");
        menuBar.add(menu);

        menuItem = new JMenuItem("");
        menu.add(menuItem);


        //a group of radio button menu items
        menu.addSeparator();

        rbMenuItem = new JRadioButtonMenuItem("Enable Logging");
        rbMenuItem.setSelected(false);

        menu.add(rbMenuItem);
        //a group of JMenuItems
        //a group of radio box menu items
        menu.addSeparator();
        ButtonGroup dDSGroup = new ButtonGroup();
        dDSNodeMenuItem = new JRadioButtonMenuItem("Send Node information/Receive Metrics");
        dDSGroup.add(dDSNodeMenuItem);
        dDSNodeMenuItem.setSelected(true);
        menu.add(dDSNodeMenuItem);

        dDSMetMenuItem = new JRadioButtonMenuItem("Receive Node Information/Send Metrics");
        dDSGroup.add(dDSMetMenuItem);
        menu.add(dDSMetMenuItem);

        /*start and stop publishing */
        menu.addSeparator();
        ButtonGroup pubGroup = new ButtonGroup();
        pubMenuItem = new JRadioButtonMenuItem("Start Publishing DDS Messages");

        pubGroup.add(pubMenuItem);
        pubMenuItem.setSelected(false);
        menu.add(pubMenuItem);

        stopPubMenuItem = new JRadioButtonMenuItem("Stop Publishing DDS Messages");
        pubGroup.add(stopPubMenuItem);
        stopPubMenuItem.setSelected(true);
        menu.add(stopPubMenuItem);
        /*start and stop logging */
        menu.addSeparator();
        ButtonGroup logGroup = new ButtonGroup();
        logMenuItem = new JRadioButtonMenuItem("Start Logging Positions");
        logGroup.add(logMenuItem);
        logMenuItem.setSelected(false);
        menu.add(logMenuItem);

        stopLogMenuItem = new JRadioButtonMenuItem("Stop Logging Positions");
        logGroup.add(stopLogMenuItem);
        stopLogMenuItem.setSelected(true);
        menu.add(stopLogMenuItem);

        //Build second menu in the menu bar.
        aboutMenu = new JMenu("About");
        menuItem = new JMenuItem("DST - LD - SITN");
        aboutMenu.add(menuItem);

        menuBar.add(aboutMenu);

        frame.setJMenuBar (menuBar);




        JButton debugButton = new JButton("debug", new ImageIcon("debug.png"));
        debugButton.setBounds(400, 120, 140, 40);
       // frame.add(cloButton);



        JTextField JT = new JTextField("");

        JT.setBounds(0, 300, 200, 500);
        JT.setBackground(new Color(0,0,0,200));
       JT.setOpaque(true);
        JT.setAlignmentX(Component.RIGHT_ALIGNMENT);
        frame.add(JT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /* add the worldwind canvas to the JFrame */

        frame.add(displayWW.canvas);
        frame.setSize(2000, 2000);
        frame.setVisible(true);



/*
Set up the Gui Listeners
 */


        logMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                if (!loggingFlag)//first time selected
                {
                    logger = new WriteLog();
                    loggingFlag = true;
                }
            }
        });

        debugButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                System.out.println("canvas node 1");

            }

        });
        ActionListener timerListener = new ActionListener()
        {

            public void actionPerformed(ActionEvent actionEvent)
            {
               /*set current position of the nodes to the 'next' position*/
                int NumberOfNodes = nodeData.length;
                for (int i = 0; i < NumberOfNodes; i++)
                {

                    Position newRandomPosition = new Position(displayWW.randomNELocation(nodeData[i].currentLocation,100.1),0);
                    nodeData[i].nextLocation = newRandomPosition;
                    Position newPosition = new Position(nodeData[i].nextLocation,0);
                    nodeData[i].symbolIdentifier.setPosition(newPosition);
                    nodeData[i].currentLocation=newPosition;
                }
                displayWW.canvas.redraw();
                /////////////////////////////////////////////
                /* set up and log data to csv file */
                //if logging is enabled
                //if this is the first time logging, set up csv file and set a flag
                //else just log the data
                if(loggingFlag && logMenuItem.isSelected())
                {
                    //send out data for all nodes
                    int numberOfNodes = nodeData.length;
                    String writeableString = logger.getTimeStamp() + ",";
                    for (int i = 0; i < numberOfNodes; i++)
                    {
                        writeableString = writeableString + nodeData[i].NodeUUID + ",";
                        writeableString = writeableString + nodeData[i].currentLocation.asDegreesArray()[0] + ",";
                        writeableString = writeableString + nodeData[i].currentLocation.asDegreesArray()[1] + ",";
                        writeableString = writeableString + "This will be a future metric,";
                        writeableString = writeableString + "This will be a future decision,";
                    }
                    writeableString = writeableString + "\n";
                    logger.writeStringToFile(writeableString);
                    System.out.println("written " + writeableString);
                    logger.Flush();
                }
                /////////////////////////////////////////////
                /* publish DDS messages */
                //send out data for all nodes
                if(pubMenuItem.isSelected())
                {
                    int numberOfNodes = nodeData.length;
                    for (int i = 0; i < numberOfNodes; i++)
                    {
                        publishData.HarmonyPublish(nodeData[i]);
                    }
                    System.out.println("published");
                }
                //////////////////////////////////////////////
            }
        };
        Timer timer = new Timer(1000, timerListener);
        timer.start();




    }

    public static void setUIFont (javax.swing.plaf.FontUIResource f){
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, f);
        }
    }
}
