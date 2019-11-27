import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class JFrameGuiActions extends JFrame
{

    WriteLog logger;
    boolean loggingFlag = false;
    JPanel panel2525B,nodeLocPanel;
    DisplayWW displayWW;
    JMenuBar menuBar;
    JMenu  menu,submenu,aboutMenu,informationMenu;
    JMenuItem menuItem;
    JRadioButtonMenuItem rbMenuItem, dDSNodeMenuItem, pubMenuItem, logMenuItem;
   JRadioButtonMenuItem  dDSMetMenuItem, stopPubMenuItem, stopLogMenuItem;
   JToggleButton toggle2525B,toggleNodeLocPanel;

    public JFrameGuiActions(HarmonyDataPublisher publishData, NodeData[] nodeData)
    {

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
                    //curently living in timer, but moveAtAngle to here next after the position message has been altered to include node data
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
        this.setUIFont (new javax.swing.plaf.FontUIResource("Serif",Font.PLAIN,30));
        this.setTitle("Harmony");
        this.setDefaultLookAndFeelDecorated(true);

        /*method setupMenuBar*/
        this.setJMenuBar(setupMenuBar());


    /*
    textfield and button
        JButton debugButton = new JButton("debug", new ImageIcon("debug.png"));
        debugButton.setBounds(400, 120, 140, 40);
       // frame.add(cloButton);




        */
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /* add the worldwind canvas to the JFrame */
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout());

        // Create two World Windows that share resources.



        // Add the World Windows to the card panel.

        /*displayWW is set up as a JPanel */
       this.displayWW = new DisplayWW(nodeData);
        cardPanel.add(displayWW, "World Window A");
        /* add the panel to the frame */
         // Add the card panel to the frame.
        this.add(cardPanel, BorderLayout.CENTER);
        this.add(this.setup2525B((CardLayout) cardPanel.getLayout(), cardPanel), BorderLayout.NORTH);
        this.add(this.nodeLocations((CardLayout) cardPanel.getLayout(), cardPanel), BorderLayout.WEST);
        this.pack();
       // this.add(displayWW);
        //this.add(displayWW.canvas);
        this.setSize(1800, 1800);




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
        toggle2525B.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                /*the toggle button call .isSelected returns false or true, use this to show or hide panel*/
                panel2525B.setVisible(toggle2525B.isSelected());

            }
        });

        toggleNodeLocPanel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                /*the toggle button call .isSelected returns false or true, use this to show or hide panel*/
                nodeLocPanel.setVisible(toggleNodeLocPanel.isSelected());

            }
        });
        ActionListener timerListener = new ActionListener()
        {

            public void actionPerformed(ActionEvent actionEvent)
            {
               /*
                * For each node, find all the nodes that are within it's detection radius.
                * For each detected node, calculate the distance, angle and direction from that node.
                */

                for(int i=0;i<nodeData.length;i++) {
                    ArrayList<DetectedNode> nodesDetected = new ArrayList<>();
                    for(int j=0;j<nodeData.length;j++) {
                        if(i == j) {
                            continue;
                        }
                        double distance = Position.greatCircleDistance(nodeData[i].currentLocation, nodeData[j].currentLocation).radians * MovementDecision.tempGlobe.getRadius();
                        if(distance <= nodeData[i].detectionRadiusInKm) {
                            Angle azimuthAngle = Position.greatCircleAzimuth(nodeData[i].currentLocation, nodeData[j].currentLocation);
                            MovementDirection movementDirection = MovementDecision.findElement(azimuthAngle.degrees);
                            nodesDetected.add(new DetectedNode(nodeData[j],movementDirection.direction,azimuthAngle,distance));
                        }
                    }
                    nodeData[i].updateNodesDetectedByMe(nodesDetected);
                }
                displayWW.canvas.redraw();

            }
        };
        Timer timer = new Timer(1000, timerListener);
        timer.start();
        ActionListener secondTimerListener = new ActionListener()
        {

            public void actionPerformed(ActionEvent actionEvent)
            {

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
                   // System.out.println("written " + writeableString);
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
        Timer secondTimer = new Timer(3000, secondTimerListener);
        secondTimer.start();


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
    private JMenuBar setupMenuBar()
    {
         /*
        https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
        */


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

        //Build Information menu
        informationMenu = new JMenu("Information");
        toggleNodeLocPanel = new JToggleButton("show/hide Node locations");
        toggleNodeLocPanel.setSelected(false);
        informationMenu.add(toggleNodeLocPanel);
        toggle2525B = new JToggleButton("show/hide 2525B");
        toggle2525B.setSelected(false);
        informationMenu.add(toggle2525B);
        menuBar.add(informationMenu);


        //Build about menu in the menu bar.
        aboutMenu = new JMenu("About");
        menuItem = new JMenuItem("DST - LD - SITN");
        aboutMenu.add(menuItem);



        menuBar.add(aboutMenu);
        return(menuBar);


    }
    private JPanel nodeLocations(final CardLayout cardLayout, final JPanel cardLayoutParent)
    {
        final JLabel NodeLocationLabel = new JLabel("Node Locations");

        JTextField JT = new JTextField("");


        this.nodeLocPanel = new JPanel(new GridLayout(2, 2));
        nodeLocPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        nodeLocPanel.add(NodeLocationLabel);
        nodeLocPanel.add(JT);

       // nodeLocPanel.setBackground(new Color(0,0,0,200));
        //nodeLocPanel.setOpaque(true);
        JT.setText("Node A\n latitude, longitude\nNodeB\n latitude, longitude");



        nodeLocPanel.setVisible(false);
        return nodeLocPanel;
    }
    private JPanel setup2525B(final CardLayout cardLayout, final JPanel cardLayoutParent)
    {
        final JLabel NodeLabel = new JLabel("Node");
        final JButton buttonA = new JButton("Button A");

        final JLabel AffiliationLabel = new JLabel("Affiliation");
        final JLabel labC = new JLabel(" Button C");

        final JLabel labD = new JLabel(" Button D");

        final JButton buttonB = new JButton(" Button B");
        //buttonA.setBackground(new Color(0,0,0,200));
        //buttonA.setOpaque(true);
        this.panel2525B = new JPanel(new GridLayout(2, 7));
        panel2525B.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel2525B.add(NodeLabel);
        panel2525B.add(AffiliationLabel);
        panel2525B.add(labC);
        panel2525B.add(buttonA);
        panel2525B.add(buttonB);
       // panel2525B.setBackground(new Color(0,0,0,200));
       // panel2525B.setOpaque(true);
        buttonA.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                cardLayout.show(cardLayoutParent, "World Window A");
                buttonA.setEnabled(false);
                buttonB.setEnabled(true);
                displayWW.canvas.redraw();
            }
        });

        buttonB.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                cardLayout.show(cardLayoutParent, "World Window B");
                buttonA.setEnabled(true);
                buttonB.setEnabled(false);
                displayWW.canvas.redraw();

            }
        });

        buttonA.setEnabled(false);


        panel2525B.setVisible(false);
        return panel2525B;
    }
}
