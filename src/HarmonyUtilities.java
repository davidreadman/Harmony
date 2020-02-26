import gov.nasa.worldwind.geom.Position;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class is responsible for working behind the scenes while the GUI is operating
 * This includes simulating a decision for each node at every epoch
 */
public class HarmonyUtilities
{
    int logCounter =0, pubCounter = 0, movementCounter = 0;
    int cumulativeNumNets = 0, cumulativeNumCollisions = 0, cumulativeNumHostilesDetected = 0;
    long maxMovementCounter = 0;
    WriteLog logger;
    HarmonyDataPublisher publishData;
    StoreProperties storeProperties = new StoreProperties();
    public static int SIMULATION_STILL_RUNNING = 0;
    public static int ACTIVE_FRIENDLIES_REACHED_RASPBERRY_CREEK = 1;
    public static int SIMULATION_RAN_OUT_OF_TIME = 2;
    public static int ALL_FRIENDLIES_ARE_DEAD = 3;
    public static int ALL_HOSTILES_ARE_DEAD = 4;
    public static int ACTIVE_HOSTILES_REACHED_RASPBERRY_CREEK = 5;
    public static int NO_ACTIVE_NODES_REMAINING = 6;
    int currentSimulationState = SIMULATION_STILL_RUNNING;

    Map<Integer, Map<String, Position>> waypoints = new HashMap<>();

    public HarmonyUtilities(HarmonyDataPublisher publishData, ArrayList<NodeData> nodes)
    {
        this.publishData = publishData;
        addInitialWaypoints(nodes);
    }


    private void addInitialWaypoints(ArrayList<NodeData> nodes) {
        waypoints.putIfAbsent(0, new HashMap<>());
        for(NodeData node: nodes){
            waypoints.get(0).put(node.nodeUUID,node.currentLocation);
        }
    }

    public void restartSimulation(ArrayList<NodeData> nodes) throws IOException {
        movementCounter = 0;
        currentSimulationState = SIMULATION_STILL_RUNNING;
        waypoints.clear();
        addInitialWaypoints(nodes);
        ArrayList<NodeData> nodesFromConfig = ParseProperties.parsePlan();
        for(NodeData configNode: nodesFromConfig) {
            for(NodeData currentNode: nodes) {
                if(configNode.nodeUUID.equals(currentNode.nodeUUID)) {
                    HarmonyMovement.updatePosition(configNode.currentLocation, currentNode);
                    currentNode.currentState = HarmonyAwareness.NODE_IS_STILL_ACTIVE;
                    break;
                }
            }
        }
    }

    public void setMaxEpochCounter(Duration duration) {
        maxMovementCounter = duration.getSeconds();
    }

    //This is used to display the current positions on the GUI.
    public String getAllCurrentNodePositionsAsAString(ArrayList<NodeData> nodes){
        List<String> nodesWithTheirCurrentPositions = new ArrayList<>();
        for(NodeData currentNode: nodes) {
            double[] positionAsArray = currentNode.currentLocation.asDegreesArray();
            nodesWithTheirCurrentPositions.add(String.format("%s: \n%f, %f", currentNode.nodeUUID, positionAsArray[0], positionAsArray[1]));
        }
        return String.join("\n", nodesWithTheirCurrentPositions);
    }

    public void publishDataForEachNode(ArrayList<NodeData> nodes, boolean debugEnabled) {
        for(NodeData currentNode: nodes) {
            pubCounter++;
            publishData.HarmonyPublish(currentNode, debugEnabled);
        }
        System.out.println("published");
    }

    public void createLogFile(ArrayList<NodeData> nodes) {
        if(logger == null) {
            logger = new WriteLog(createNewDirectory());
        }
        List<String> nodeHeadersAsCSVFormat = new ArrayList<>();
        for(int i=0;i<nodes.size();i++) {
            nodeHeadersAsCSVFormat.add(String.format("NODE%dUUID,NODE%dIFF,NODE%dISCOMMANDER,NODE%dSTATE,NODE%dLAT,NODE%dLON,NODE%dDISTANCE_TRAVELLED,NODE%dDIRECTION_OF_TRAVEL,NODE%dMETRIC_TYPE,NODE%dMETRIC_VALUE,NODE%dDECISION,NODE%dCOMMANDER,NODE%dISFOLLOWING,NODE%dCLOSESTENEMY",i+1,i+1,i+1,i+1,i+1,i+1,i+1,i+1,i+1,i+1,i+1,i+1,i+1,i+1));
        }
        String writableString = String.format("TIME,%s,CUM_NUM_NETS,CUM_NUM_COLLISIONS,CUM_NUM_HOSTILES_DETECTED,STATE\n", String.join(",", nodeHeadersAsCSVFormat));
        logger.writeStringToFile(writableString);
        logger.Flush();
    }

    public void logToCSV(ArrayList<NodeData> nodes) {
        if(logger != null) {
            logCounter++;
            List<String> nodesAsCSVFormat = new ArrayList<>();
            for(NodeData currentNode: nodes) {
                //Develop csv for each node
                nodesAsCSVFormat.add(String.format("%s,%c,%d,%d,%.4f,%.4f,%.2f,%.2f,%s,%.2f,%s,%s,%s,%s", currentNode.nodeUUID, currentNode.symbol.charAt(1), isNodeTheCommander(currentNode,nodes) ? 1:0,HarmonyAwareness.currentStateOfNode(currentNode),currentNode.currentLocation.asDegreesArray()[0], currentNode.currentLocation.asDegreesArray()[1],
                        HarmonyMovement.distanceToTargetInMeters(currentNode.previousLocation, currentNode.currentLocation),
                        HarmonyMovement.bearingToTargetInDegrees(currentNode.previousLocation, currentNode.currentLocation),
                        currentNode.currentMetric.getKey(),
                        currentNode.currentMetric.getValue(),
                        currentNode.currentDecision,
                        currentNode.myCommander == null ? "None": currentNode.myCommander.nodeUUID,
                        currentNode.nodeToFollow == null ? "None": currentNode.nodeToFollow.nodeUUID,
                        currentNode.closestEnemy == null ? "None": currentNode.closestEnemy.nodeUUID));
            }
/*            String timestamp = logger.getTimeStamp();*/
            String writableString = String.format("%d,%s,%d,%d,%d,%d\n", movementCounter, String.join(",", nodesAsCSVFormat),cumulativeNumNets,cumulativeNumCollisions,cumulativeNumHostilesDetected,currentSimulationState);
            //Only write line to file starting with timestamp if we haven't done so already.
            if(logger.linesWritten.stream().noneMatch(line -> line.startsWith(String.format("%d", movementCounter)))){
                logger.writeStringToFile(writableString);
            }
            //logger.writeStringToFile(writableString);
            logger.Flush();
        }
    }

    private boolean isNodeTheCommander(NodeData currentNode, ArrayList<NodeData> nodes){
        return nodes.stream().anyMatch(node -> node.myCommander != null && node.myCommander.nodeUUID.equals(currentNode.nodeUUID));
    }

    public void closeLogFile() {
        if(logger != null) {
            logger.closeAndFlush();
            logger = null;
        }
    }

    public void triggerMovementForEachNode(ArrayList<NodeData> nodes, boolean debugEnabled) {
        if(currentSimulationState == SIMULATION_STILL_RUNNING) {
            movementCounter++;
            waypoints.put(movementCounter,HarmonyDecision.makeDecisionForEachNode(nodes, debugEnabled));
            updateCurrentSimulationState(nodes);
            updateMetrics(nodes);
        }

    }

    public void createHoconFile(ArrayList<NodeData> nodes) {
        HoconFileGenerator.writeToHocon(nodes,waypoints,movementCounter, maxMovementCounter);
        System.out.println("Created new HOCON file for SMARTNet");
    }

    public void createNewConfigPropertiesFile(ArrayList<NodeData> nodes) {
        storeProperties.writeConfig(nodes);
        System.out.println("Created new Config properties file");
    }

    public String createNewDirectory() {
        String directoryName = "";
        LocalDateTime date = LocalDateTime.now();
        //format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        directoryName = String.format("Harmony_Simulation_%s", date.format(formatter));
        File directory = new File(directoryName);
        if(!directory.exists())
            directory.mkdir();
        return directoryName;
    }

    /**
     * Return the current state of the simulation.
     * @return  0 if it's incomplete,
     *          1 if all remaining active friendly nodes have reached raspberry creek,
     *          2 if we ran out of time
     *          3 if all friendlies have been killed
     *          4 if all hostiles have been killed
     *          5 if all remaining active hostile nodes has reached raspberry creek
     */
    public void updateCurrentSimulationState(ArrayList<NodeData> nodes) {
        if(HarmonyAwareness.checkIfSpecificNodesHasSpecificStatus(nodes, 'F', HarmonyAwareness.REACHED_RASPBERRY_CREEK)) {
            currentSimulationState = ACTIVE_FRIENDLIES_REACHED_RASPBERRY_CREEK;
        }
        else if(maxMovementCounter > 0 && movementCounter == maxMovementCounter) {
            currentSimulationState = SIMULATION_RAN_OUT_OF_TIME;
        }
        else if(HarmonyAwareness.checkIfSpecificNodesHasSpecificStatus(nodes, 'F', HarmonyAwareness.NODE_IS_DESTROYED)) {
            currentSimulationState = ALL_FRIENDLIES_ARE_DEAD;
        }
        else if(HarmonyAwareness.checkIfSpecificNodesHasSpecificStatus(nodes, 'H', HarmonyAwareness.NODE_IS_DESTROYED)) {
            currentSimulationState = ALL_HOSTILES_ARE_DEAD;
        }
        else if(HarmonyAwareness.checkIfSpecificNodesHasSpecificStatus(nodes, 'H', HarmonyAwareness.REACHED_RASPBERRY_CREEK)) {
            currentSimulationState = ACTIVE_HOSTILES_REACHED_RASPBERRY_CREEK;
        }
        else if(HarmonyAwareness.noActiveNodesRemaining(nodes)){
            currentSimulationState = NO_ACTIVE_NODES_REMAINING;
        }
    }

    public boolean hasSimulationEnded() {
        return currentSimulationState > SIMULATION_STILL_RUNNING;
    }

    public int getArrayOfNames(ArrayList<String> animalNames) throws IOException
    {
        String tempString;
        int counter = 0;
        FileReader fileReader = new FileReader("Animals.csv") ;
        BufferedReader br = new BufferedReader(fileReader);
        while((tempString=br.readLine()) != null)
        {
            animalNames.add(tempString);
            counter++;
        }

    return counter;
    }
    public String grabAName(ArrayList<String> animalNames, ArrayList<NodeData> nodes)
    {
        String randString ="";
        Random generator = new Random();
            int index =    generator.nextInt(animalNames.size()) ;
            randString =animalNames.get(index);

        //check for duplication
        for(NodeData currentNode : nodes)
        {
            if (currentNode.nodeUUID == randString)
            {
                randString = grabAName(animalNames,nodes);
                break;
            }

        }
        return randString;
    }

    private void updateMetrics(ArrayList<NodeData> nodes) {
        cumulativeNumNets += HarmonyAwareness.calculateNumberOfNets(nodes);
        cumulativeNumCollisions += HarmonyAwareness.calculateNumberOfNodeCollisions(nodes);
        cumulativeNumHostilesDetected += HarmonyAwareness.calculateNumberOfHostilesDetected(nodes);
    }
}
