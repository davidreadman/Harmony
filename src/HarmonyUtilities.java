import java.io.IOException;
import java.time.Duration;
import java.util.*;

/**
 * This class is responsible for working behind the scenes while the GUI is operating
 * This includes simulating a decision for each node at every epoch
 */
public class HarmonyUtilities
{
    int movementCounter = 0,logCounter =0, pubCounter = 0;
    //A value to indicate a time limit for the simulation;
    long maxEpochCounter = 0;
    WriteLog logger;
    HarmonyDataPublisher publishData;
    StoreProperties storeProperties = new StoreProperties();

    private static final double DISTANCE_THRESHOLD_FOR_DIRECT_CONTACT_IN_METRES = 500;

    //A Map to show which blue spots are equally close to a red.
    //When two or more blue spots are no more than 500 metres of each other, we know that the red spot is 'trapped'
    HashMap<String, ArrayList<String>> friendsThatAreVeryCloseToHostile = new HashMap<>();

    //The nodes within the simulation. This includes friends and hostiles
    ArrayList<NodeData> nodes;

    public HarmonyUtilities(ArrayList<NodeData> nodeData, HarmonyDataPublisher publishData)
    {
        nodes = nodeData;
        this.publishData = publishData;
    }


    public void restartSimulation() throws IOException {
        //Assuming that the we haven't added/removed any nodes. Update each node to it's starting position from the plan.properties
        movementCounter = 0;
        ArrayList<NodeData> nodesFromConfig = ParseProperties.parsePlan();
        for(NodeData configNode: nodesFromConfig) {
            for(NodeData currentNode: this.nodes) {
                if(configNode.NodeUUID.equals(currentNode.NodeUUID)) {
                    HarmonyMovement.updatePosition(configNode.currentLocation, currentNode);
                    break;
                }
            }
        }
    }

    public void setMaxEpochCounter(Duration duration) {
        //Going by one epoch per second
        this.maxEpochCounter = duration.getSeconds();
    }

    //This is used to display the current positions on the GUI.
    public String getAllCurrentNodePositionsAsAString(){
        List<String> nodesWithTheirCurrentPositions = new ArrayList<>();
        for(NodeData currentNode: nodes) {
            double[] positionAsArray = currentNode.currentLocation.asDegreesArray();
            nodesWithTheirCurrentPositions.add(String.format("%s: \n%f, %f", currentNode.NodeUUID, positionAsArray[0], positionAsArray[1]));
        }
        return String.join("\n", nodesWithTheirCurrentPositions);
    }

    public void publishDataForEachNode() {
        for(NodeData currentNode: nodes) {
            pubCounter++;
            publishData.HarmonyPublish(currentNode);
        }
        System.out.println("published");
    }

    public void createLogFile() {
        if(logger == null) {
            logger = new WriteLog();
        }
    }

    public void logToCSV() {
        if(logger != null) {
            logCounter++;
            List<String> nodesAsCSVFormat = new ArrayList<>();
            for(NodeData currentNode: nodes) {
                //Develop csv for each node
                nodesAsCSVFormat.add(String.format("%s,%f,%f,%s,%s", currentNode.NodeUUID, currentNode.currentLocation.asDegreesArray()[0], currentNode.currentLocation.asDegreesArray()[1],currentNode.currentMetric,currentNode.currentDecision));
            }
            String timestamp = logger.getTimeStamp();
            String writableString = String.format("%s,%s\n", timestamp, String.join(",", nodesAsCSVFormat));

            //Only write line to file starting with timestamp if we haven't done so already.
            if(logger.linesWritten.stream().noneMatch(line -> line.startsWith(timestamp))) {
                logger.writeStringToFile(writableString);
            }
            logger.Flush();
        }
    }

    public void closeLogFile() {
        if(logger != null) {
            logger.closeAndFlush();
        }
    }

    public void triggerMovementForEachNode(boolean debugEnabled) {
        movementCounter++;
        for(NodeData currentNode: nodes) {
            String decision = "Move Raspberry Ck";

            if(!currentNode.closestEnemy.isEmpty()) {
                if(currentNode.nodeIFF.equals("FRIEND")) {
                    decision = "Move Towards Closest Enemy";
                } else if(currentNode.nodeIFF.equals("HOSTILE")) {
                    decision = "Move Away from Closest Enemy";
                }
            }

            if(currentNode.nodeIFF.equals("FRIEND")) {
                //If friendly node is in direct contact with a hostile along with at least another friendly
                //They have surrounded the hostile and don't need to move.
                if(friendsThatAreVeryCloseToHostile.containsKey(currentNode.closestEnemy)) {
                    boolean isHostileTrapped = friendsThatAreVeryCloseToHostile.get(currentNode.closestEnemy).size() >= 2;
                    if(isHostileTrapped) {
                        decision = "Stay in current position. Trapped a hostile with at least one other friendly";
                    }
                }
            }
            if(currentNode.nodeIFF.equals("HOSTILE")) {
                //Check if hostile is close to two or more friendlies. If so, they're 'trapped' and can't move.
                if(friendsThatAreVeryCloseToHostile.containsKey(currentNode.NodeUUID)) {
                    boolean isTrapped = friendsThatAreVeryCloseToHostile.get(currentNode.NodeUUID).size() >=2;
                    if(isTrapped) {
                        decision = "Stay in current position. Trapped by at least two friendlies";
                    }
                }
            }
            HarmonyMovement.makeDecision(currentNode, decision, debugEnabled);
        }
    }

    public void createHoconFile() {
        HoconFileGenerator.writeToHocon(nodes);
        System.out.println("Created new HOCON file for SMARTNet");
    }

    public void createNewConfigPropertiesFile() {
        storeProperties.writeConfig(nodes);
        System.out.println("Created new Config properties file");
    }

    /**
     * Return the current state of the simulation.
     * Basically if it's running return -1 or return a positive value to indicate the simulation is over
     * @return -1 if it's incomplete,
     *          1 if all nodes have reached raspberry creek,
     *          2 if all hostiles are 'dead',
     *          3 if we ran out of time
     */
    public int currentStateOfSimulation() {
        if(nodes.stream().allMatch(node -> HarmonyMovement.hasNodeReachedRaspberryCreek(node))) {
            return 1;
        }
        else if(nodes.stream().noneMatch(node -> node.nodeIFF.equals("HOSTILE"))) {
            return 2;
        }
        else if(maxEpochCounter > 0 && movementCounter == maxEpochCounter) {
            return 3;
        }
        else {
            return -1;
        }
    }
}
