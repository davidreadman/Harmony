import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.symbology.TacticalGraphic;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class DecisionEngine {

    public static final double DETECTION_THRESHOLD = 1000.0;
    ArrayList<NodeData> nodes;
    ArrayList<Position> placesOfInterest;
    long movementCounter, maxMovementCounter;
    ArrayList<DetectedNode> distancesFromNode;
    Map<NodeData, List<NodeData>> closestEnemies;

    public DecisionEngine() {
        nodes = new ArrayList<>();
        placesOfInterest = new ArrayList<>();
        distancesFromNode = new ArrayList<>();
        movementCounter = 0;
        maxMovementCounter = 0;
        closestEnemies = new HashMap<>();
    }

    public void setMaxMovementCounter(Duration duration) {
        maxMovementCounter = duration.getSeconds();
    }

    public void setUpNodes(ArrayList<NodeData> nodes) {
        this.nodes.clear();
        this.nodes.addAll(nodes);
        this.distancesFromNode.clear();
        for(NodeData currentNode : nodes) {
            closestEnemies.put(currentNode, new ArrayList<>());
            for(NodeData otherNode : nodes) {
                if(currentNode == otherNode)
                    continue;

                distancesFromNode.add(new DetectedNode(currentNode,otherNode));
            }
        }
        calculateDistancesAndAnglesBetweenNodes();
    }

    private boolean isNodeCloseToEnemy(DetectedNode detectedNode) {
        boolean withinContact = detectedNode.distanceFromSourceToFocusInMeters <= DETECTION_THRESHOLD;
        boolean neitherNodesAreNeutral = detectedNode.sourceNode.symbol.toUpperCase().charAt(1) != 'N' && detectedNode.focusNode.symbol.toUpperCase().charAt(1) != 'N';
        return withinContact && neitherNodesAreNeutral && detectedNode.sourceNode.symbol.toUpperCase().charAt(1) != detectedNode.focusNode.symbol.charAt(1);
    }

    private void calculateDistancesAndAnglesBetweenNodes() {
        for(NodeData currentNode: nodes) {
            closestEnemies.get(currentNode).clear();
        }
        for(DetectedNode dn: distancesFromNode) {
            dn.distanceFromSourceToFocusInMeters = HarmonyMovement.distanceToTargetInMeters(dn.sourceNode.currentLocation, dn.focusNode.currentLocation);
            dn.angleFromSourceToFocusInDegrees = HarmonyMovement.bearingToTargetInDegrees(dn.sourceNode.currentLocation, dn.focusNode.currentLocation);
            if(isNodeCloseToEnemy(dn)) {
                closestEnemies.get(dn.sourceNode).add(dn.focusNode);
            }
        }
        for(NodeData currentNode: nodes) {
            if(closestEnemies.containsKey(currentNode)){
                List<NodeData> closestEnemiesOfNode = closestEnemies.get(currentNode);
                List<DetectedNode> filtered = distancesFromNode.stream().filter(dn -> dn.sourceNode == currentNode && closestEnemiesOfNode.contains(dn.sourceNode)).collect(Collectors.toList());
                Collections.sort(filtered, (o1, o2) -> {
                    Double d1 = o1.distanceFromSourceToFocusInMeters;
                    Double d2 = o2.distanceFromSourceToFocusInMeters;

                    return d1.compareTo(d2);
                });
            }
        }
    }

    public void assignCheckpointsForEachNode(Map<NodeData, TacticalGraphic> nodeDataTacticalGraphicMap) {
        for(NodeData currentNode : nodes) {
            if(nodeDataTacticalGraphicMap.containsKey(currentNode)) {
                Iterable<? extends Position> itr = nodeDataTacticalGraphicMap.get(currentNode).getPositions();
                List<Position> positionsToVisit = new ArrayList<>();
                while (itr.iterator().hasNext()) {
                    Position position = itr.iterator().next();
                    positionsToVisit.add(position);
                }
                currentNode.updatePlanCheckpoints(positionsToVisit);
                currentNode.isCarryingOutAPlan = true;
            }
        }
    }

    public void triggerMovementForEachNode(boolean debugEnabled) {
        movementCounter++;
        for(NodeData currentNode: nodes) {
            String decision = "";
            if(currentNode.nextLocation != null) {
                decision = "Move to the next location";
            }
            HarmonyMovement.makeDecision(currentNode, decision, debugEnabled);
        }
        calculateDistancesAndAnglesBetweenNodes();
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

    /**
     * Return the current state of the simulation.
     * Basically if it's running return 0 or return a positive value to indicate the simulation is over
     */
    public int currentStateOfSimulation() {
        if(nodes.stream().allMatch(node -> HarmonyMovement.hasNodeReachedFinalLocation(node) && node.symbol.toUpperCase().charAt(1) == 'F')) {
            return 1;
        }
        else if(maxMovementCounter > 0 && movementCounter == maxMovementCounter) {
            return 2;
        }
        else {
            return 0;
        }
    }
}
