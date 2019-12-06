import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.symbology.TacticalSymbol;
import gov.nasa.worldwind.util.BasicDragger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class JFrameGuiActions extends JFrame
{

    HarmonyUtilities harmonyUtilities;
    boolean loggingFlag = false;
    JPanel panel2525B, nodeLocPanel;
    JLabel NodeUUIDText;
    DisplayWW displayWW;
    JMenuBar menuBar;
    JMenu menu;
    JMenu aboutMenu;
    JMenu informationMenu;
    JMenuItem menuItem,enableMovementMenuItem;
    JRadioButtonMenuItem rbMenuItem;
    JRadioButtonMenuItem dDSNodeMenuItem;
    JRadioButtonMenuItem pubMenuItem;
    JRadioButtonMenuItem dDSMetMenuItem;
    JRadioButtonMenuItem stopPubMenuItem;
    JRadioButtonMenuItem toggle2525B, toggleNodeLocPanel;
    JRadioButtonMenuItem buttonMenuItem;
    JRadioButtonMenuItem configCreatorMenuItem;
    NodeData[] nodeData;
    NodeData selectedNode;
    //declared the JPanel components because they are shared between dragger and 2525Bpanel
    JComboBox iFFList;
    JComboBox nodeList;
    JLabel SymbolString;
    String[] iFFStrings = {"FRIEND", "HOSTILE", "NEUTRAL"};
    JTextArea nodePositionsTextArea = new JTextArea();

    public JFrameGuiActions(HarmonyDataPublisher publishData, NodeData[] nodeData)
    {
        this.harmonyUtilities = new HarmonyUtilities(nodeData, publishData);
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
                    //curently living in timer, but moveAtAngle to here next after the position message has been altered to include node data
                }
            }
        };
        dDSPositionMessage.addPropertyChangeListener(pcl);
        /* set up DDS Subscriber/Listeners with bound properties */
        new HarmonyDataSubscriber(null, dDSPositionMessage);

        /* set up default UI fonts */
        setUIFont(new javax.swing.plaf.FontUIResource("Serif", Font.PLAIN, 30));
        this.setTitle("Harmony");
        setDefaultLookAndFeelDecorated(true);

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
        JPanel setup2525BHandle = this.setup2525B();
        this.add(setup2525BHandle, BorderLayout.NORTH);
        this.add(this.nodeLocations(), BorderLayout.WEST);
        this.pack();
        // this.add(displayWW);
        //this.add(displayWW.canvas);
        this.setSize(1800, 1800);




/*
Set up the Gui Listeners
 */

        rbMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                loggingFlag = !loggingFlag;
                harmonyUtilities.createLogFile();
            }
        });
        buttonMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
               harmonyUtilities.createNewConfigPropertiesFile();
            }
        });
        configCreatorMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                harmonyUtilities.createHoconFile();
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
        /*
        dragger is the worldwind gui detector for when an object in the layers is dragged with the mouse
         */
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
                        //check to see which object has been clicked on
                        Object object = event.getTopPickedObject().getObject();
                        if (object == nodeData[i].symbolIdentifier)
                        {
                            //update the dragged symbol node with the new location
                            nodeData[i].currentLocation = nodeData[i].symbolIdentifier.getPosition();
                            //and let the 2525B routing know which one is selected
                            selectedNode = nodeData[i];
                            NodeUUIDText.setText(nodeData[i].NodeUUID);
                            //testing to see how to address the object
                            //identify the iff in the string, adjust the iff in the dropdown iFFList
                            int selectedIndex = -1;
                            for(int iffIndex = 0; iffIndex< iFFStrings.length;iffIndex++) {
                                if(iFFStrings[iffIndex].equals(selectedNode.nodeIFF)) {
                                    selectedIndex = iffIndex;
                                    break;
                                }
                            }
                            iFFList.setSelectedIndex(selectedIndex);
                            SymbolString.setText(selectedNode.symbol);
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
                if(enableMovementMenuItem.isSelected())
                {
                   harmonyUtilities.triggerMovementForEachNode();
                   nodePositionsTextArea.setText(harmonyUtilities.getAllCurrentNodePositionsAsAString());
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
                if (loggingFlag)
                {
                    harmonyUtilities.logToCSV();
                }
                /////////////////////////////////////////////
                /* publish DDS messages */
                //send out data for all nodes
                if (pubMenuItem.isSelected())
                {
                    harmonyUtilities.publishDataForEachNode();
                }
                //////////////////////////////////////////////
            }
        };
        Timer secondTimer = new Timer(3000, secondTimerListener);
        secondTimer.start();


    }

    /**
     *
     * @param f
     *
     * Sets the default font to a larger style
     */
    public static void setUIFont(javax.swing.plaf.FontUIResource f)
    {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements())
        {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }

    /**
     *
     * @return
     */
    private JMenuBar setupMenuBar()
    {
         /*
        https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
        */


        menuBar = new JMenuBar();
        menu = new JMenu("Options");
        menuBar.add(menu);




        //a group of radio button menu items
        menu.addSeparator();
        buttonMenuItem = new JRadioButtonMenuItem("Write current config to new config file");
        buttonMenuItem.setSelected(false);
        menu.add(buttonMenuItem);

        configCreatorMenuItem = new JRadioButtonMenuItem("Write current config to HOCON file");
        configCreatorMenuItem.setSelected(false);
        menu.add(configCreatorMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("Enable Logging");
        rbMenuItem.setSelected(false);
        menu.add(rbMenuItem);

        enableMovementMenuItem = new JRadioButtonMenuItem("Enable Movement");
        enableMovementMenuItem.setSelected(false);

        menu.add(enableMovementMenuItem);
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
        return (menuBar);


    }

    /**
     *
     * @return
     */
    private JPanel nodeLocations()
    {
        final JLabel NodeLocationLabel = new JLabel("Node Locations");
        nodePositionsTextArea.setLineWrap(true);

        this.nodeLocPanel = new JPanel(new GridLayout(2, 2));
        nodeLocPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        nodeLocPanel.add(NodeLocationLabel);
        nodeLocPanel.add(nodePositionsTextArea);

        // nodeLocPanel.setBackground(new Color(0,0,0,200));
        //nodeLocPanel.setOpaque(true);
        nodePositionsTextArea.setText(harmonyUtilities.getAllCurrentNodePositionsAsAString());
        nodeLocPanel.setVisible(false);
        return nodeLocPanel;
    }

    /**
     *
     * @return
     */
    public JPanel setup2525B()
    {
        //set up a default node
        selectedNode = nodeData[0];
        /* set up the drop down lists*/
        char[] iFFChars = {'F', 'H', 'N'};
        StringBuilder MilSymString = new StringBuilder(nodeData[0].symbolIdentifier.getIdentifier());

        JLabel NodeLabel = new JLabel("Node");
        //as null pointers are bad, identify first node as selected
        NodeUUIDText = new JLabel(nodeData[0].NodeUUID);
        final JLabel AffiliationLabel = new JLabel("Affiliation");
        final JLabel StringLabel = new JLabel("String");
        //load up MilSymString with existingstring (ie nodeData.symbol
        SymbolString = new JLabel(MilSymString.toString());


        /*set up the string of node names */
        int NumberOfNodes = nodeData.length;
        String[] nodeNames = new String[NumberOfNodes];

        for (int i = 0; i < NumberOfNodes; i++)
        {
            nodeNames[i] = nodeData[i].NodeUUID;
        }

        //Create the combo box, select item at index 4.
        //Indices start at 0, so 4 specifies the pig.
        iFFList = new JComboBox(iFFStrings);
        nodeList = new JComboBox(nodeNames);
        iFFList.setSelectedIndex(0);
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

                //the combobox for iFFList has been activated, so first set the char in the stringbuilder
                MilSymString.setCharAt(1, iFFChars[iFFList.getSelectedIndex()]);
                //this sets the stringbuilder output to test functionality
                SymbolString.setText(MilSymString.toString());
                //set the node affiliation based on the selected index from the iFFList
                selectedNode.nodeIFF = iFFStrings[iFFList.getSelectedIndex()];
                //need to set the string in the node to the new value
                selectedNode.symbol = MilSymString.toString();
                selectedNode.nodeIFF = iFFStrings[iFFList.getSelectedIndex()];
                //need to update the tactical symbol to this
                //selectedNode.symbolIdentifier is the symbol object
                //selectedNode.currentLocation is the location
                //remove this symbol from the renderable layer
                //https://worldwind.arc.nasa.gov/java/latest/javadoc/gov/nasa/worldwind/Model.html getModel
                //getlayers returns a list of layers in the model
                Layer symbolLayer = displayWW.canvas.getModel().getLayers().getLayerByName("symbolLayer");
                //create a new symbol for the changed 2525B string
                TacticalSymbol replacementSymbol = displayWW.setupSymbol(selectedNode.symbol, selectedNode.currentLocation);
                //and load this into the node as a replacement for the old symbol and symbolidentifier
                // bad - selectedNode.symbolIdentifier = replacementSymbol;
                //we have this layer stored as a layer, we can remove this entire layer from the model layers
                displayWW.canvas.getModel().getLayers().remove(symbolLayer);
               // need to create a new renderable layer because the symbollayer is converted to a standard layer when added to the model
                RenderableLayer replaceLayer;
                replaceLayer = (RenderableLayer) symbolLayer;
                replaceLayer.setName("symbolLayer");
                replaceLayer.removeRenderable(selectedNode.symbolIdentifier);
                selectedNode.symbolIdentifier = replacementSymbol;
                replaceLayer.addRenderable(replacementSymbol);
                displayWW.canvas.getModel().getLayers().add(replaceLayer);
                //so symbollayer can be removed and added
              // is the symbolLayer the same in both instances? renderable and basic?

                //symbolLayer does not appear to have a change item in symbol



                //create a new symbol with the new string
                //add this new symbol to the renderable layer
                //replace the symbol in the node with this new symbol
            }
        });


        panel2525B.setVisible(false);
        return panel2525B;
    }
}
