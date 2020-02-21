import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HarmonyAwareness {

    private static ArrayList<PlaceOfInterest> PLACES_OF_INTEREST = generatePlacesOfInterest();
    private static double VERY_CLOSE_DISTANCE_THRESHOLD = 100.0;

    private static ArrayList<PlaceOfInterest> generatePlacesOfInterest() {
        return new ArrayList<>();
    }

    public static boolean isNodeVeryCloseToTheEnemy(NodeData currentNode) {
        return HarmonyMovement.isNodeWithinSightOfLocation(currentNode, currentNode.closestEnemy.currentLocation, VERY_CLOSE_DISTANCE_THRESHOLD);
    }

    public static void setNodeToFollow(NodeData currentNode, String nodeToFollowUUID, ArrayList<NodeData> nodes) {
        currentNode.nodeToFollow = nodes.stream().filter(node -> node.nodeUUID.equals(nodeToFollowUUID)).findFirst().orElse(null);
    }

    public static boolean hasAllRemainingActiveFriendliesReachedRaspberryCreek(ArrayList<NodeData> nodes) {
        Predicate<NodeData> isFriendly = node -> node.symbol.charAt(1) == 'F';
        Predicate<NodeData> isNodeDestroyed = node -> node.symbol.charAt(3) == 'X';

        List<NodeData> friendlyNodes = nodes.stream().filter(isFriendly.and(isNodeDestroyed.negate())).collect(Collectors.toList());
        return checkIfSpecificNumberOfNodesHaveReachedRaspberryCreek(nodes, friendlyNodes.size(), 'F');
    }

    public static boolean hasOneActiveHostileReachedRaspberryCreek(ArrayList<NodeData> nodes) {
        return checkIfSpecificNumberOfNodesHaveReachedRaspberryCreek(nodes, 1, 'H');
    }

    public static boolean hasAllHostilesBeenDestroyed(ArrayList<NodeData> nodes) {
        return checkIfSpecificNodesHaveBeenDestroyed(nodes, 'H');
    }

    public static boolean hasAllFriendliesBeenDestroyed(ArrayList<NodeData> nodes) {
        return checkIfSpecificNodesHaveBeenDestroyed(nodes, 'F');
    }

    private static boolean checkIfSpecificNumberOfNodesHaveReachedRaspberryCreek(ArrayList<NodeData> nodes, long numNodesChecker, char iffChar) {
        Predicate<NodeData> isNodeOfSpecificIFF = node -> node.symbol.charAt(1) == iffChar;
        Predicate<NodeData> isNodeDestroyed = node -> node.symbol.charAt(3) == 'X';
        Predicate<NodeData> hasReachedRaspberryCreek = node -> HarmonyMovement.hasNodeReachedRaspberryCreek(node);

        List<NodeData> nodesOfSpecificIFF = nodes.stream().filter(isNodeOfSpecificIFF.and(isNodeDestroyed.negate())).collect(Collectors.toList());
        return(nodesOfSpecificIFF.stream().filter(hasReachedRaspberryCreek).count() == numNodesChecker && !nodesOfSpecificIFF.isEmpty());
    }

    private static boolean checkIfSpecificNodesHaveBeenDestroyed(ArrayList<NodeData> nodes, char iffChar){
        Predicate<NodeData> isNodeOfSpecificIFF = node -> node.symbol.charAt(1) == iffChar;
        Predicate<NodeData> isNodeDestroyed = node -> node.symbol.charAt(3) == 'X';

        List<NodeData> nodesOfSpecificIFF = nodes.stream().filter(isNodeOfSpecificIFF).collect(Collectors.toList());
        return (nodesOfSpecificIFF.stream().filter(isNodeDestroyed).count() == nodesOfSpecificIFF.size() && !nodesOfSpecificIFF.isEmpty());
    }


    public static ArrayList<PlaceOfInterest> getNearestPlacesOfInterest(NodeData currentNode, double detectionRadius) {
        return new ArrayList<>(PLACES_OF_INTEREST.stream().filter(poi -> HarmonyMovement.isNodeWithinSightOfLocation(currentNode, poi.location, detectionRadius)).collect(Collectors.toList()));
    }

    /**
     * Try to assign the closest enemy for a node. If there are no nearby enemies within a detection radius, then the assigned value is null
     * @param currentNode
     * @param nodes
     * @param detectionRadius
     */
    public static void assignClosestEnemy(NodeData currentNode, ArrayList<NodeData> nodes, double detectionRadius) {
        if(currentNode.symbol.charAt(1) == 'N')
            return;

        Predicate<NodeData> isNeutralNode = node -> node.symbol.charAt(1) == 'N';
        Predicate<NodeData> twoNodesOfTheSameIFF = node -> node.symbol.charAt(1) == currentNode.symbol.charAt(1);
        Predicate<NodeData> withinSight = node -> HarmonyMovement.isNodeWithinSightOfLocation(currentNode, node.currentLocation, detectionRadius);

        Map<NodeData, Double> distancesToEnemy = new HashMap<>();
        List<NodeData> filtered = nodes.stream().filter(isNeutralNode.negate().and(twoNodesOfTheSameIFF.negate()).and(withinSight)).collect(Collectors.toList());
        if(filtered.isEmpty()){
            currentNode.closestEnemy = null;
        }
        else {
            for(NodeData other: filtered) {
                distancesToEnemy.put(other, HarmonyMovement.distanceToTargetInMeters(currentNode.currentLocation, other.currentLocation));
            }
            Stream<Map.Entry<NodeData, Double>> sorted = distancesToEnemy.entrySet().stream().sorted(Map.Entry.comparingByValue());
            currentNode.closestEnemy = sorted.findFirst().get().getKey();
        }


    }

}
