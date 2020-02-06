import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.symbology.TacticalSymbol;
import gov.nasa.worldwind.util.BasicDragger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

public class JFrameGuiActions extends JFrame
{
    boolean simulationOver = false;
    HarmonyUtilities harmonyUtilities;
    boolean loggingFlag = false;
    JLayeredPane panel2525B, nodeLocPanel;
    JLabel NodeUUIDText;
    DisplayWW displayWW;
    JMenuBar menuBar;
    JMenu menu;
    JMenu aboutMenu;
    JMenu informationMenu;
    JMenuItem menuItem, enableMovementMenuItem, restartMenuItem, buttonMenuItem, configCreatorMenuItem, setDurationItem, currentDurationItem;
    JRadioButtonMenuItem rbMenuItem;
    JRadioButtonMenuItem dDSNodeMenuItem;
    JRadioButtonMenuItem pubMenuItem;
    JRadioButtonMenuItem dDSMetMenuItem;
    JRadioButtonMenuItem stopPubMenuItem;
    JRadioButtonMenuItem toggle2525B, toggleNodeLocPanel;
    NodeData selectedNode;
    //declared the JPanel components because they are shared between dragger and 2525Bpanel
    JComboBox<String> iFFList,hQList,levelList,functionList;
    JComboBox<String> nodeList;
    JLabel SymbolString;
    String[] iFFStrings = {"FRIEND", "HOSTILE", "NEUTRAL"};
    String[] hQStrings = {"Not","HQ","TFHQ"};
    String[] levelStrings = {"Null","Team","Squad","Section","Platoon","Company","Battalion","Regiment","Brigade"};
    String[] functionStrings={"Null","Air Defence","Armor","AntiArmor","Aviation","Infantry","Engineer","Field Artillery",
            "Recon","Missile","Internal Security Forces"};
    java.util.List<String> iffStringsList = new ArrayList<>(Arrays.asList(iFFStrings));
    JTextArea nodePositionsTextArea;
    String durationStringAsSetByTheUser = "";

    public JFrameGuiActions(HarmonyDataPublisher publishData, ArrayList<NodeData> nodeData, SimulationSettings simulationSettings)
    {
        this.harmonyUtilities = new HarmonyUtilities(nodeData, publishData);
        this.nodePositionsTextArea = new JTextArea();
        this.nodePositionsTextArea.setRows(nodeData.size());
        //this.nodeData = nodeData;
        /* setup the binding of properties to allow for change monitoring across threads */
        DDSPositionMessage dDSPositionMessage = new DDSPositionMessage();
        /* listen for a change in the position message */
        PropertyChangeListener pcl = evt -> {
            if (evt.getSource() instanceof DDSPositionMessage)
            {
                DDSPositionMessage pd = (DDSPositionMessage) evt.getSource();
                System.out.println(String.format("Received event from %s: %s has been changed from %s to %s", pd.getDDSPositionMessage(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
                //on a DDS Message is the *current* best time to make a decision on a new position
                //a nice quick test could be a new random location based on existing location
                //curently living in timer, but moveAtAngle to here next after the position message has been altered to include node data
            }
        };
        dDSPositionMessage.addPropertyChangeListener(pcl);
        /* set up DDS Subscriber/Listeners with bound properties */
        new HarmonyDataSubscriber(null, dDSPositionMessage);

        /* set up default UI fonts */
        setUIFont(new javax.swing.plaf.FontUIResource("Serif", Font.PLAIN, 40));
        this.setTitle("Harmony");
        setDefaultLookAndFeelDecorated(true);

        this.loggingFlag = simulationSettings.enableLogging;
        /*method setupMenuBar*/
        this.setJMenuBar(setupMenuBar(simulationSettings));


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /* add the worldwind canvas to the JFrame */
        /*using cardpanel, look at changing this at a later stage to JLayeredPane */
        //JPanel cardPanel = new JPanel();
        JLayeredPane cardPanel = new JLayeredPane();
        cardPanel.setLayout(new CardLayout());


        // Add the World Windows to the card panel.

        /*displayWW is set up as a JPanel */
        this.displayWW = new DisplayWW(nodeData);
        /*add this panel to the cardpanel*/
        cardPanel.add(displayWW, "World Wind");
        /* add the panel to the frame */
        // Add the card panel to the frame.
        this.add(cardPanel, BorderLayout.CENTER);
        this.setup2525B(simulationSettings.show2525B);
        this.add(this.panel2525B, BorderLayout.NORTH);
        this.setUpNodeLocationsPane(simulationSettings.showNodeLocations);
        this.add(this.nodeLocPanel, BorderLayout.WEST);
        this.pack();
        this.setSize(1800, 1800);




/*
Set up the Gui Listeners
 */
        restartMenuItem.addActionListener(e -> {
            try {
                harmonyUtilities.restartSimulation();
                simulationOver = false;
                //update the positions on view.
                nodePositionsTextArea.setText(harmonyUtilities.getAllCurrentNodePositionsAsAString());
                currentDurationItem.setText(generateDurationString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        rbMenuItem.addActionListener(e -> {
            loggingFlag = !loggingFlag;
            harmonyUtilities.createLogFile();
        });
        buttonMenuItem.addActionListener(e -> harmonyUtilities.createNewConfigPropertiesFile());
        configCreatorMenuItem.addActionListener(e -> harmonyUtilities.createHoconFile());
        toggle2525B.addActionListener(e -> {
            /*the toggle button call .isSelected returns false or true, use this to show or hide panel*/
            panel2525B.setVisible(toggle2525B.isSelected());

        });

        toggleNodeLocPanel.addActionListener(e -> {
            /*the toggle button call .isSelected returns false or true, use this to show or hide panel*/
            nodeLocPanel.setVisible(toggleNodeLocPanel.isSelected());

        });

        setDurationItem.addActionListener(e -> {
            JTextField txtDurationValue = new JTextField(5);
            String[] durationUnitsArr = {"day(s)", "hour(s)", "minute(s)", "second(s)"};
            JComboBox cbDurationUnits = new JComboBox(durationUnitsArr);
            JPanel myPanel = new JPanel();
            myPanel.add(txtDurationValue);
            myPanel.add(cbDurationUnits);
            int result = JOptionPane.showConfirmDialog(null, myPanel, "Specify duration of the Simulation",JOptionPane.OK_CANCEL_OPTION);
            if(result == JOptionPane.OK_OPTION) {
                 Integer durationValue = Integer.parseInt(txtDurationValue.getText());
                 Duration duration;
                 switch(cbDurationUnits.getSelectedIndex()) {
                     case 0:
                         duration = Duration.ofDays(durationValue);
                         break;
                     case 1:
                         duration = Duration.ofHours(durationValue);
                         break;
                     case 2:
                         duration = Duration.ofMinutes(durationValue);
                         break;
                     case 3:
                         duration = Duration.ofSeconds(durationValue);
                         break;
                     default:
                         throw new IllegalStateException("Unexpected value: " + cbDurationUnits.getSelectedIndex());
                 }
                 durationStringAsSetByTheUser = String.format("%d %s", durationValue, durationUnitsArr[cbDurationUnits.getSelectedIndex()]);
                 harmonyUtilities.setMaxEpochCounter(duration);
                 setDurationItem.setEnabled(false);
            }
        });

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
                if(event.getObjects() != null && event.getObjects().size() >= 1) {
                    Object object = event.getTopPickedObject().getObject();
                    for(NodeData currentNode : harmonyUtilities.nodes) {
                        if(object == currentNode.symbolIdentifier) {
                            String selectedEvent = event.getEventAction();
                            switch(selectedEvent) {
                                case SelectEvent.LEFT_CLICK:
                                    selectedNode = currentNode;
                                    NodeUUIDText.setText(selectedNode.NodeUUID);
                                    //testing to see how to address the object
                                    //need to change the selected nodes drop down list at this point else all up to this are changed
                                    SymbolString.setText(selectedNode.symbol);

                                    //identify the iff in the string, adjust the iff in the dropdown iFFList
                                    iFFList.setSelectedIndex(iffStringsList.indexOf(selectedNode.nodeIFF));
                                    break;
                                case SelectEvent.DRAG:
                                    currentNode.currentLocation = currentNode.symbolIdentifier.getPosition();
                                    nodePositionsTextArea.setText(harmonyUtilities.getAllCurrentNodePositionsAsAString());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
        });

        ActionListener timerListener = actionEvent -> {
            int currentStateOfSimulation = harmonyUtilities.currentStateOfSimulation();
            if(currentStateOfSimulation == -1) {
                if (enableMovementMenuItem.isSelected())
                {
                    harmonyUtilities.triggerMovementForEachNode();
                    nodePositionsTextArea.setText(harmonyUtilities.getAllCurrentNodePositionsAsAString());
                }
                displayWW.canvas.redraw();
            }
            else {
                if(!simulationOver) {
                    String reasonForEndOfSimulation = "";
                    switch(currentStateOfSimulation) {
                        case 1:
                            reasonForEndOfSimulation = "Simulation has ended as all nodes have reached Raspberry Creek";
                            break;
                        case 2:
                            reasonForEndOfSimulation = "Simulation has ended as all Hostile nodes are dead";
                            break;
                        case 3:
                            reasonForEndOfSimulation = "Simulation has reached the specified duration of " + durationStringAsSetByTheUser;
                            break;
                        default:
                            break;
                    }
                    JOptionPane.showMessageDialog(new JFrame("End Of simulation"), String.format("%s.", reasonForEndOfSimulation), "End of simulation", JOptionPane.INFORMATION_MESSAGE);
                    simulationOver = true;
                    harmonyUtilities.closeLogFile();
                    enableMovementMenuItem.setSelected(false);
                    setDurationItem.setEnabled(true);
                }
            }
            currentDurationItem.setText(generateDurationString());
        };
        Timer timer = new Timer(1000, timerListener);
        timer.start();
        ActionListener secondTimerListener = actionEvent -> {

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
        };
        Timer secondTimer = new Timer(3000, secondTimerListener);
        secondTimer.start();


    }

    /**
     * Generate a string to show how long the simulation has been running for
     * or to show how much time is remaining in the simulation.
     * @return
     */
    private String generateDurationString() {
        if(harmonyUtilities.maxEpochCounter > 0) {
            long epochRemainingCounter = harmonyUtilities.maxEpochCounter - harmonyUtilities.movementCounter;
            long numHoursRemaining = epochRemainingCounter/3600;
            long numMinutesRemaining = (epochRemainingCounter%3600)/60;
            long numSecondsRemaining = epochRemainingCounter % 60;
            return String.format("Time remaining: %02d:%02d:%02d", numHoursRemaining, numMinutesRemaining, numSecondsRemaining);
        }
        else {
            int numHours = harmonyUtilities.movementCounter /3600;
            int numMinutes = (harmonyUtilities.movementCounter % 3600) / 60;
            int numSeconds = harmonyUtilities.movementCounter % 60;
            return String.format("Time elapsed: %02d:%02d:%02d", numHours, numMinutes, numSeconds);
        }

    }

    /**
     * @param f Sets the default font to a larger style
     */
    public static void setUIFont(javax.swing.plaf.FontUIResource f)
    {
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements())
        {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }

    private JMenuBar setupMenuBar(SimulationSettings simulationSettings)
    {
         /*
        https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
        */


        menuBar = new JMenuBar();
        menu = new JMenu("Options");
        menuBar.add(menu);


        //a group of radio button menu items
        menu.addSeparator();
        buttonMenuItem = new JMenuItem("Write current config to new config file");
        menu.add(buttonMenuItem);

        configCreatorMenuItem = new JMenuItem("Write current config to HOCON file");
        menu.add(configCreatorMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("Enable Logging");
        rbMenuItem.setSelected(simulationSettings.enableLogging);
        menu.add(rbMenuItem);

        enableMovementMenuItem = new JRadioButtonMenuItem("Enable Movement");
        enableMovementMenuItem.setSelected(simulationSettings.startSimulation);

        menu.add(enableMovementMenuItem);
        //a group of JMenuItems
        //a group of radio box menu items
        menu.addSeparator();
        ButtonGroup dDSGroup = new ButtonGroup();
        dDSNodeMenuItem = new JRadioButtonMenuItem("Send Node information/Receive Metrics");
        dDSGroup.add(dDSNodeMenuItem);
        dDSNodeMenuItem.setSelected(simulationSettings.sendNodeInformation);
        menu.add(dDSNodeMenuItem);

        dDSMetMenuItem = new JRadioButtonMenuItem("Receive Node Information/Send Metrics");
        dDSMetMenuItem.setSelected(simulationSettings.sendMetrics);
        dDSGroup.add(dDSMetMenuItem);
        menu.add(dDSMetMenuItem);

        /*start and stop publishing */
        menu.addSeparator();
        ButtonGroup pubGroup = new ButtonGroup();
        pubMenuItem = new JRadioButtonMenuItem("Start Publishing DDS Messages");

        pubGroup.add(pubMenuItem);
        pubMenuItem.setSelected(simulationSettings.publishDDSMessages);
        menu.add(pubMenuItem);

        stopPubMenuItem = new JRadioButtonMenuItem("Stop Publishing DDS Messages");
        pubGroup.add(stopPubMenuItem);
        stopPubMenuItem.setSelected(!simulationSettings.publishDDSMessages);
        menu.add(stopPubMenuItem);

        setDurationItem = new JMenuItem("Set Duration of Simulation");
        menu.add(setDurationItem);

        restartMenuItem = new JMenuItem("Restart Simulation");
        menu.add(restartMenuItem);

        /*start and stop logging */
        menu.addSeparator();

        //Build Information menu
        informationMenu = new JMenu("Information");
        toggleNodeLocPanel = new JRadioButtonMenuItem("show/hide Node locations");
        toggleNodeLocPanel.setSelected(simulationSettings.showNodeLocations);
        informationMenu.add(toggleNodeLocPanel);
        toggle2525B = new JRadioButtonMenuItem("show/hide 2525B");
        toggle2525B.setSelected(simulationSettings.show2525B);
        informationMenu.add(toggle2525B);
        menuBar.add(informationMenu);


        //Build about menu in the menu bar.
        aboutMenu = new JMenu("About");
        menuItem = new JMenuItem("DST - LD - SITN");
        aboutMenu.add(menuItem);


        menuBar.add(aboutMenu);

        if(simulationSettings.simulatonDuration != null) {
            harmonyUtilities.setMaxEpochCounter(simulationSettings.simulatonDuration);
            durationStringAsSetByTheUser = simulationSettings.durationString;
            setDurationItem.setEnabled(false);
        }

        currentDurationItem = new JMenuItem(generateDurationString());
        menuBar.add(currentDurationItem);

        return (menuBar);
    }

    private void setUpNodeLocationsPane(boolean isVisible)
    {
        JLabel nodeLocationLabel = new JLabel("Node Locations");
        nodeLocationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nodePositionsTextArea.setLineWrap(true);
        nodePositionsTextArea.setFont(new javax.swing.plaf.FontUIResource("Serif", Font.PLAIN, 25));
        this.nodeLocPanel = new JLayeredPane();
        nodeLocPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

       // nodeLocPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        gbc.gridx = 0;
        gbc.gridy =0;
        gbc.gridheight =1;
       gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.weighty=1;
        nodeLocPanel.add(nodeLocationLabel,gbc);
        nodePositionsTextArea.setText(harmonyUtilities.getAllCurrentNodePositionsAsAString());

        nodeLocPanel.setVisible(isVisible);
        gbc.ipady = 950;
        gbc.gridx = 0;
        gbc.gridy =1;
        gbc.gridheight =40;
        gbc.weighty=0;
        //Button button = new Button();
        //nodeLocPanel.add(button,gbc);
        nodeLocPanel.add(nodePositionsTextArea,gbc);
    }


    public void setup2525B(boolean isVisible)
    {
        //set up a default node
        selectedNode = harmonyUtilities.nodes.get(0);
        /* set up the drop down lists*/
        char[] iFFChars = {'F', 'H', 'N'};
        //http://www.mapsymbs.com/ms2525c.pdf pg 52
        char[] hQChars = {'-', 'A', 'B'};
        StringBuilder MilSymString = new StringBuilder(selectedNode.symbolIdentifier.getIdentifier());

        JLabel nodeLabel = new JLabel("Node");
        Font labelFont = nodeLabel.getFont();
        labelFont = labelFont.deriveFont(labelFont.getStyle() | Font.BOLD);
        nodeLabel.setFont(labelFont);

        //as null pointers are bad, identify first node as selected
        NodeUUIDText = new JLabel(selectedNode.NodeUUID);
        JLabel affiliationLabel = new JLabel("Affiliation");
        affiliationLabel.setFont(labelFont);
        //table A-II from Mil-std-2525C Table A-II modifier codes
        //HQ, Level
        JLabel typeLabel = new JLabel("Function");
        typeLabel.setFont(labelFont);
        JLabel hQLabel = new JLabel("HQ");
        hQLabel.setFont(labelFont);
        JLabel levelLabel = new JLabel("Level");
        levelLabel.setFont(labelFont);
        JLabel stringLabel = new JLabel("String");
        stringLabel.setFont(labelFont);
        JLabel cloneLabel = new JLabel("Clone");
        cloneLabel.setFont(labelFont);
        //load up MilSymString with existingstring (ie nodeData.symbol
        SymbolString = new JLabel(MilSymString.toString());


        /*set up the string of node names */
        int numberOfNodes = harmonyUtilities.nodes.size();
        String[] nodeNames = new String[numberOfNodes];

        for (int i = 0; i < numberOfNodes; i++)
        {
            nodeNames[i] = harmonyUtilities.nodes.get(i).NodeUUID;
        }


        iFFList = new JComboBox<>(iFFStrings);
        hQList = new JComboBox<>(hQStrings);
        levelList = new JComboBox<>(levelStrings);
        functionList = new JComboBox<>(functionStrings);
        //set up lists here

        nodeList = new JComboBox<>(nodeNames);

        JButton cloneButton = new JButton("clone");

        iFFList.setSelectedIndex(0);
        hQList.setSelectedIndex(0);
        levelList.setSelectedIndex(0);
        functionList.setSelectedIndex(0);
        nodeList.setSelectedIndex(0);

        //buttonA.setBackground(new Color(0,0,0,200));
        //buttonA.setOpaque(true);
        panel2525B = new JLayeredPane();
        panel2525B.setLayout(new GridLayout(3, 3));
        panel2525B.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel2525B.add(nodeLabel);
        panel2525B.add(affiliationLabel);
        panel2525B.add(hQLabel);
        panel2525B.add(levelLabel);
        panel2525B.add(typeLabel);
        panel2525B.add(stringLabel);
        panel2525B.add(cloneLabel);
        //panel2525B.add(nodeList);
        panel2525B.add(NodeUUIDText);
        panel2525B.add(iFFList);
        panel2525B.add(hQList);
        panel2525B.add(levelList);
        panel2525B.add(functionList);
        panel2525B.add(SymbolString);
        panel2525B.add(cloneButton);
/*using popupmenu listener instead of actionlistener because we modify the ifflist value in the draggable
and that invokes the actionlistener, if we do want to invoke the ifflist value as a listener then itemListener can be used
used the invisible because the selection of new dropdown is invoked at this point
 */
        iFFList.addPopupMenuListener(new PopupMenuListener()
        {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
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

            @Override
            public void popupMenuCanceled(PopupMenuEvent e)
            {

            }

        });
        /*Positions 11 and 12, symbol modifier indicator, identify indicators present on the symbol
such as echelon, feint/dummy, installation, task force, headquarters staff, and equipment mobility.
Table A-II contains the specific values used in this field.
*/

        hQList.addPopupMenuListener(new PopupMenuListener()
        {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {
            // MilSymString is a string constructed from the currently selected node symbol
                //the combobox for iFFList has been activated, so first set the char in the stringbuilder
               MilSymString.setCharAt(10, hQChars[hQList.getSelectedIndex()]);
                //this sets the stringbuilder output to test functionality
                SymbolString.setText(MilSymString.toString());
                //set the node affiliation based on the selected index from the iFFList
                selectedNode.nodeHQ = hQStrings[hQList.getSelectedIndex()];
                //need to set the string in the node to the new value
                selectedNode.symbol = MilSymString.toString();
                selectedNode.nodeHQ = hQStrings[hQList.getSelectedIndex()];
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

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
            {

            }
            @Override
            public void popupMenuCanceled(PopupMenuEvent e)
            {

            }
        });
        levelList.addPopupMenuListener(new PopupMenuListener()
        {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
            {

            }
            @Override
            public void popupMenuCanceled(PopupMenuEvent e)
            {

            }
        });
        functionList.addPopupMenuListener(new PopupMenuListener()
        {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
            {

            }
            @Override
            public void popupMenuCanceled(PopupMenuEvent e)
            {

            }
        });
        iFFList.addPopupMenuListener(new PopupMenuListener()
        {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
            {

            }
            @Override
            public void popupMenuCanceled(PopupMenuEvent e)
            {

            }
        });
        // panel2525B.setOpaque(true);
        panel2525B.setVisible(isVisible);
    }
}
