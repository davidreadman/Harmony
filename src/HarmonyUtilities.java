import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;

import java.util.*;

/**
 * This class is responsible for working behind the scenes while the GUI is operating
 * This includes simulating a decision for each node at every epoch
 */
public class HarmonyUtilities
{
    int movementCounter = 0,logCounter =0, pubCounter = 0;
    WriteLog logger;
    HarmonyDataPublisher publishData;
    StoreProperties storeProperties = new StoreProperties();

    //The nodes within the simulation. This includes friends and hostiles
    private ArrayList<NodeData> nodes;

    public HarmonyUtilities(NodeData[] nodeData, HarmonyDataPublisher publishData)
    {
        nodes = new ArrayList<>(Arrays.asList(nodeData));
        this.publishData = publishData;
    }

    public void updateWithLatestNodeArray(NodeData[] nodeData) {
        nodes = new ArrayList<>(Arrays.asList(nodeData));
    }

    public ArrayList<NodeData> getCurrentNodes()
    {
        return nodes;
    }

    public void addNode(NodeData node)
    {
        nodes.add(node);
    }

    public void removeNode(NodeData node)
    {
        nodes.remove(node);
    }

    public String getAllCurrentNodePositionsAsAString(){
        List<String> nodesWithTheirCurrentPositions = new ArrayList<>();
        for(NodeData currentNode: nodes) {
            double[] positionAsArray = currentNode.currentLocation.asDegreesArray();
            nodesWithTheirCurrentPositions.add(String.format("%s: %f, %f", currentNode.NodeUUID, positionAsArray[0], positionAsArray[1]));
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
            if(!logger.linesWritten.stream().anyMatch(line -> line.startsWith(timestamp))) {
                logger.writeStringToFile(writableString);
            }
            logger.Flush();
        }
    }

    public void triggerMovementForEachNode() {
        for(NodeData currentNode: nodes) {
            movementCounter++;
            HarmonyMovement.makeDecision(currentNode, "Move Raspberry Ck");
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

    //set this up in HarmonyMovement, dont want to changfe this too much as it shows how to
    //do the arraylists
    public void situationalAwareness() {
        for (int i = 0; i < nodes.size(); i++) {
            NodeData currentNode = nodes.get(i);
            double maxDistanceInMetres = HarmonyMovement.KM_TO_METRES * currentNode.detectionRadiusInKm;
            for (int j = 0; j < nodes.size(); j++) {
                //Skip node at index j since it's the same as the node at index i.
                if (i == j) {
                    continue;
                }
                //The purpose here is to detect nodes with the current nodes detection radius.
                double distance = HarmonyMovement.distanceToTargetInMeters(currentNode.currentLocation, nodes.get(j).currentLocation);
                if (distance <= maxDistanceInMetres) {
                    double angleInDegrees = HarmonyMovement.bearingToTargetInDegrees(currentNode.currentLocation, nodes.get(j).currentLocation);
                    DetectedNode detectedNode = new DetectedNode(nodes.get(j).NodeUUID, nodes.get(j).currentLocation, -1, angleInDegrees, distance, currentNode.NodeUUID, nodes.get(j).nodeType, -1, nodes.get(j).nodeIFF);
                    switch(detectedNode.getNodeIFF()) {
                        case "FRIEND":
                            currentNode.friendNodesSeen.add(detectedNode);
                            break;
                        case "HOSTILE":
                            currentNode.hostileNodesSeen.add(detectedNode);
                            break;
                        case "NEUTRAL":
                            currentNode.neutralNodesSeen.add(detectedNode);
                            break;
                        default:
                            break;
                    }
                }
            }

            Comparator<DetectedNode> detectedNodeComparator = new Comparator<DetectedNode>() {
                @Override
                //Compare by distance
                public int compare(DetectedNode o1, DetectedNode o2) {
                    if (o1.getDistanceToTargetInMeters() > o2.getDistanceToTargetInMeters()) {
                        return 1;
                    } else if (o1.getDistanceToTargetInMeters() == o2.getDistanceToTargetInMeters()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            };
            //Arrange hostileNodesSeen in order of ascending distance from current node and assign the first node to be the closest hostile
            if(currentNode.nodeIFF.equals("FRIEND")) {
                Collections.sort(currentNode.hostileNodesSeen, detectedNodeComparator);
                currentNode.closestEnemy = currentNode.hostileNodesSeen.get(0).getnodeUUID();
            }
            else if(currentNode.nodeIFF.equals("HOSTILE")) {
                Collections.sort(currentNode.friendNodesSeen, detectedNodeComparator);
                currentNode.closestEnemy = currentNode.friendNodesSeen.get(0).getnodeUUID();
            }
        }
    }

    public void makeDecisionsForEachNode() {
        for(NodeData currentNode: nodes) {

        }
    }
}
