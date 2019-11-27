import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.util.BasicDragger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class JFrameGuiActions extends JFrame
{

    public static final double DEFAULT_DISTANCE_IN_METERS = 100.0;
    WriteLog logger;
    boolean loggingFlag = false;
    JPanel panel2525B,nodeLocPanel;
    JLabel NodeUUIDText;
    DisplayWW displayWW;
    JMenuBar menuBar;
    JMenu  menu,submenu,aboutMenu,informationMenu;
    JMenuItem menuItem;
    JRadioButtonMenuItem rbMenuItem, dDSNodeMenuItem, pubMenuItem, logMenuItem;
    JRadioButtonMenuItem  dDSMetMenuItem, stopPubMenuItem, stopLogMenuItem;
    JRadioButtonMenuItem toggle2525B,toggleNodeLocPanel;
    NodeData[] nodeData;
    NodeData selectedNode;

    public JFrameGuiActions(HarmonyDataPublisher publishData, NodeData[] nodeData)
    {
        this.nodeData = nodeData;
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
        /* setup the MovementDecision class */
        MovementDecision movementDecision = new MovementDecision(nodeData);
        /* set up DDS Subscriber/Listeners with bound properties */
        new HarmonyDataSubscriber(null, dDSPositionMessage);

        /* set up default UI fonts */
        this.setUIFont (new javax.swing.plaf.FontUIResource("Serif",Font.PLAIN,30));
        this.setTitle("Harmony");
        this.setDefaultLookAndFeelDecorated(true);

        /*method setupMenuBar*/
        this.setJMenuBar(setupMenuBar());


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /* add the worldwind canvas to the JFrame */
        /*using cardpanel, look at changing this at a later stage to JLayeredPane */
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout());


        // Add the World Windows to the card panel.

        /*displayWW is set up as a JPanel */
       this.displayWW = new DisplayWW(nodeData);
       /*add this panel to the cardpanel*/
        cardPanel.add(displayWW, "World Wind");
        /* add the panel to the frame */
         // Add the card panel to the frame.
        this.add(cardPanel, BorderLayout.CENTER);
        JPanel setup2525BHandle = this.setup2525B((CardLayout) cardPanel.getLayout(), cardPanel);
        this.add(setup2525BHandle, BorderLayout.NORTH);
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
        int NumberOfNodes = nodeData.length;
        displayWW.canvas.addSelectListener(new SelectListener()
        {
            protected BasicDragger dragger = new BasicDragger(displayWW.canvas);

            public void selected(SelectEvent event)
            {
                // Delegate dragging computations to a dragger.
                this.dragger.selected(event);
                if (event.getEventAction().equals(SelectEvent.DRAG))
                {
                    for (int i = 0; i < NumberOfNodes; i++)
                    {

                        Object object = event.getTopPickedObject().getObject();
                        if (object == nodeData[i].symbolIdentifier)
                        {
                            //update the dragged symbol node with the new location
                            nodeData[i].currentLocation = nodeData[i].symbolIdentifier.getPosition();
                            //and let the 2525B routing know which one is selected
                            selectedNode = nodeData[i];
                            NodeUUIDText.setText(nodeData[i].NodeUUID);
                            //testing to see how to address the object

                        }
                        //pass in the node, have the node updated with the tactical symbol
                    }

                }
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
                    Position newRandomPosition = movementDecision.moveWithoutDirection(nodeData[i].currentLocation,new Position(displayWW.rCPosition,0),nodeData[i].maxSpeed,false,true);
                    nodeData[i].nextLocation = newRandomPosition;
                    Position newPosition = nodeData[i].nextLocation;
                    nodeData[i].symbolIdentifier.setPosition(newPosition);
                    nodeData[i].currentLocation=newPosition;
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
        toggleNodeLocPanel = new JRadioButtonMenuItem("show/hide Node locations");
        toggleNodeLocPanel.setSelected(false);
        informationMenu.add(toggleNodeLocPanel);
        toggle2525B = new JRadioButtonMenuItem("show/hide 2525B");
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
    public JPanel setup2525B(final CardLayout cardLayout, final JPanel cardLayoutParent)
    {
        /* set up the drop down lists*/
        String[] iFFStrings = { "Friend", "Hostile", "Neutral", "Null"};
        char[] iFFChars = { 'F', 'H', 'N','-'};
        StringBuilder MilSymString =  new StringBuilder(nodeData[0].symbolIdentifier.getIdentifier());

        final JLabel NodeLabel = new JLabel("Node");
        //as null pointers are bad, identify first node as selected
        NodeUUIDText = new JLabel(nodeData[0].NodeUUID);
        final JLabel AffiliationLabel = new JLabel("Affiliation");
        final JLabel StringLabel = new JLabel("String");

        final JLabel SymbolString = new JLabel(MilSymString.toString());


    /*set up the string of node names */
        int NumberOfNodes = nodeData.length;
        String[] nodeNames = new String[NumberOfNodes];

        for (int i = 0; i < NumberOfNodes; i++)
        {
            nodeNames[i] = nodeData[i].NodeUUID;
        }

        //Create the combo box, select item at index 4.
        //Indices start at 0, so 4 specifies the pig.
        JComboBox iFFList = new JComboBox(iFFStrings);
        JComboBox nodeList = new JComboBox(nodeNames);
        iFFList.setSelectedIndex(3);
        nodeList.setSelectedIndex(0);

        //buttonA.setBackground(new Color(0,0,0,200));
        //buttonA.setOpaque(true);
        this.panel2525B = new JPanel(new GridLayout(2, 3));
        panel2525B.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel2525B.add(NodeLabel);
        panel2525B.add(AffiliationLabel);
        panel2525B.add(StringLabel);
        //panel2525B.add(nodeList);
        panel2525B.add(NodeUUIDText);
        panel2525B.add(iFFList);
        panel2525B.add(SymbolString);
       // panel2525B.setBackground(new Color(0,0,0,200));
       // panel2525B.setOpaque(true);
        iFFList.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                MilSymString.setCharAt(1,iFFChars[iFFList.getSelectedIndex()]);
                //this sets the stringbuilder output to test functionality
                SymbolString.setText(MilSymString.toString());
                //need to set the string in the node to the new value
                selectedNode.symbol = MilSymString.toString();
                //need to update the tactical symbol to this
                //selectedNode.symbolIdentifier is the symbol object
                //selectedNode.currentLocation is the location
                //remove this symbol from the renderable layer
                Layer symbolLayer = displayWW.canvas.getModel().getLayers().getLayerByName("symbolLayer");


                //create a new symbol with the new string
                //add this new symbol to the renderable layer
                //replace the symbol in the node with this new symbol
            }
        });






        panel2525B.setVisible(false);
        return panel2525B;
    }
}
