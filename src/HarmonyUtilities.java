import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
    long maxMovementCounter = 0;
    WriteLog logger;
    HarmonyDataPublisher publishData;
    StoreProperties storeProperties = new StoreProperties();

    public HarmonyUtilities(HarmonyDataPublisher publishData)
    {
        this.publishData = publishData;
    }


    public void restartSimulation(ArrayList<NodeData> nodes) throws IOException {
        movementCounter = 0;
        ArrayList<NodeData> nodesFromConfig = ParseProperties.parsePlan();
        for(NodeData configNode: nodesFromConfig) {
            for(NodeData currentNode: nodes) {
                if(configNode.nodeUUID.equals(currentNode.nodeUUID)) {
                    HarmonyMovement.updatePosition(configNode.currentLocation, currentNode);
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
            nodeHeadersAsCSVFormat.add(String.format("NODE%dUUID,NODE%dIFF,NODE%dISCOMMANDER,NODE%dSTATUS,NODE%dSTATE,NODE%dLAT,NODE%dLON,NODE%dMETRIC,NODE%dDECISION,NODE%dCOMMANDER,NODE%dISFOLLOWING,NODE%dCLOSESTENEMY",i+1,i+1,i+1,i+1,i+1,i+1,i+1,i+1,i+1,i+1,i+1,i+1));
        }
        String writableString = String.format("TIME,%s,STATE\n", String.join(",", nodeHeadersAsCSVFormat));
        logger.writeStringToFile(writableString);
        logger.Flush();
    }

    public void logToCSV(ArrayList<NodeData> nodes) {
        if(logger != null) {
            logCounter++;
            List<String> nodesAsCSVFormat = new ArrayList<>();
            for(NodeData currentNode: nodes) {
                //Develop csv for each node
                nodesAsCSVFormat.add(String.format("%s,%c,%d,%c,%d,%f,%f,%s,%s,%s,%s,%s", currentNode.nodeUUID, currentNode.symbol.charAt(1), isNodeTheCommander(currentNode,nodes) ? 1:0,currentNode.symbol.charAt(3),currentStateOfNode(currentNode),currentNode.currentLocation.asDegreesArray()[0], currentNode.currentLocation.asDegreesArray()[1],currentNode.currentMetric,currentNode.currentDecision,
                        currentNode.myCommander == null ? "None": currentNode.myCommander.nodeUUID,
                        currentNode.nodeToFollow == null ? "None": currentNode.nodeToFollow.nodeUUID,
                        currentNode.closestEnemy == null ? "None": currentNode.closestEnemy.nodeUUID));
            }
/*            String timestamp = logger.getTimeStamp();*/
            String writableString = String.format("%d,%s,%d\n", movementCounter, String.join(",", nodesAsCSVFormat), currentStateOfSimulation(nodes));
            //Only write line to file starting with timestamp if we haven't done so already.
            //if(logger.linesWritten.stream().noneMatch(line -> line.startsWith(timestamp))) {
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

    private int currentStateOfNode(NodeData currentNode) {
        if(currentNode.symbol.charAt(3) == 'X')
            return 2;
        else {
            if(HarmonyMovement.hasNodeReachedRaspberryCreek(currentNode))
                return 1;
            else
                return 0;
        }
    }

    public void closeLogFile() {
        if(logger != null) {
            logger.closeAndFlush();
            logger = null;
        }
    }

    public void triggerMovementForEachNode(ArrayList<NodeData> nodes, boolean debugEnabled) {
        movementCounter++;
        HarmonyDecision.makeDecisionForEachNode(nodes, debugEnabled);
    }

    public void createHoconFile(ArrayList<NodeData> nodes) {
        HoconFileGenerator.writeToHocon(nodes);
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
     *          5 if at least one active hostile has reached raspberry creek
     */
    public int currentStateOfSimulation(ArrayList<NodeData> nodes) {
        if(HarmonyAwareness.hasAllRemainingActiveFriendliesReachedRaspberryCreek(nodes)) {
            return 1;
        }
        else if(maxMovementCounter > 0 && movementCounter == maxMovementCounter) {
            return 2;
        }
        else if(HarmonyAwareness.hasAllFriendliesBeenDestroyed(nodes)) {
            return 3;
        }
        else if(HarmonyAwareness.hasAllHostilesBeenDestroyed(nodes)) {
            return 4;
        }
        else if(HarmonyAwareness.hasOneActiveHostileReachedRaspberryCreek(nodes)) {
            return 5;
        }
        else {
            return 0;
        }
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

    //placeholder for 'measure of goodness based on the number of nodes within given distance of each other
    public int calculateNumberOfNets()
    {
        return 0;
    }
}
