import java.io.IOException;
import java.time.Duration;
import java.util.*;

/**
 * This class is responsible for working behind the scenes while the GUI is operating
 * This includes simulating a decision for each node at every epoch
 */
public class HarmonyUtilities
{
    int logCounter =0, pubCounter = 0;
    WriteLog logger;
    HarmonyDataPublisher publishData;
    StoreProperties storeProperties = new StoreProperties();
    DecisionEngine decisionEngine;

    public HarmonyUtilities(HarmonyDataPublisher publishData, DecisionEngine decisionEngine)
    {
        this.publishData = publishData;
        this.decisionEngine = decisionEngine;
    }


    public void restartSimulation() throws IOException {
        decisionEngine.restartSimulation();
    }

    public void setMaxEpochCounter(Duration duration) {
        decisionEngine.setMaxMovementCounter(duration);
    }

    //This is used to display the current positions on the GUI.
    public String getAllCurrentNodePositionsAsAString(){
        List<String> nodesWithTheirCurrentPositions = new ArrayList<>();
        for(NodeData currentNode: decisionEngine.nodes) {
            double[] positionAsArray = currentNode.currentLocation.asDegreesArray();
            nodesWithTheirCurrentPositions.add(String.format("%s: \n%f, %f", currentNode.NodeUUID, positionAsArray[0], positionAsArray[1]));
        }
        return String.join("\n", nodesWithTheirCurrentPositions);
    }

    public void publishDataForEachNode(boolean debugEnabled) {
        for(NodeData currentNode: decisionEngine.nodes) {
            pubCounter++;
            publishData.HarmonyPublish(currentNode, debugEnabled);
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
            for(NodeData currentNode: decisionEngine.nodes) {
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
        decisionEngine.triggerMovementForEachNode(debugEnabled);
    }

    public void createHoconFile() {
        HoconFileGenerator.writeToHocon(decisionEngine.nodes);
        System.out.println("Created new HOCON file for SMARTNet");
    }

    public void createNewConfigPropertiesFile() {
        storeProperties.writeConfig(decisionEngine.nodes);
        System.out.println("Created new Config properties file");
    }

    /**
     * Return the current state of the simulation.
     * Basically if it's running return -1 or return a positive value to indicate the simulation is over
     * @return 0 if it's incomplete,
     *          1 if all nodes have reached raspberry creek,
     *          2 if we ran out of time
     */
    public int currentStateOfSimulation() {
        return decisionEngine.currentStateOfSimulation();
    }

    //placeholder for 'measure of goodness based on the number of nodes within given distance of each other
    public int calculateNumberOfNets()
    {
        return 0;
    }
}
