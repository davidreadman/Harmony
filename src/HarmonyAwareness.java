import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HarmonyAwareness {

    private static ArrayList<PlaceOfInterest> PLACES_OF_INTEREST = generatePlacesOfInterest();
    private static double VERY_CLOSE_DISTANCE_THRESHOLD = 100.0;
    private static double NODE_DETECTION_THRESHOLD = 1.00;
    public static double DEFAULT_DETECTION_RADIUS_IN_METRES = 500.0;
    public static int REACHED_RASPBERRY_CREEK = 1;
    public static int NODE_IS_DESTROYED = 2;
    public static int NODE_IS_STILL_ACTIVE = 0;

    public static int currentStateOfNode(NodeData currentNode) {
        if(currentNode.symbol.charAt(3) == 'X')
            return NODE_IS_DESTROYED;
        else {
            if(HarmonyMovement.hasNodeReachedRaspberryCreek(currentNode))
                return REACHED_RASPBERRY_CREEK;
            else
                return NODE_IS_STILL_ACTIVE;
        }
    }

    private static ArrayList<PlaceOfInterest> generatePlacesOfInterest() {
        return new ArrayList<>();
    }

    public static boolean isNodeVeryCloseToTheEnemy(NodeData currentNode) {
        return HarmonyMovement.isNodeWithinSightOfLocation(currentNode, currentNode.closestEnemy.currentLocation, VERY_CLOSE_DISTANCE_THRESHOLD);
    }

    public static void setNodeToFollow(NodeData currentNode, String nodeToFollowUUID, ArrayList<NodeData> nodes) {
        currentNode.nodeToFollow = nodes.stream().filter(node -> node.nodeUUID.equals(nodeToFollowUUID)).findFirst().orElse(null);
    }

    public static List<NodeData> getNodesOfSpecificIFF(ArrayList<NodeData> nodes, char iffChar){
        return nodes.stream().filter(node -> node.symbol.charAt(1) == iffChar).collect(Collectors.toList());
    }

    public static boolean checkIfSpecificNodesHasSpecificStatus(ArrayList<NodeData> nodes, char iffChar, int status) {
        return getNodesOfSpecificIFF(nodes, iffChar).stream().allMatch(node -> node.currentState == status);
    }

    public static boolean noActiveNodesRemaining(ArrayList<NodeData> nodes){
        return nodes.stream().noneMatch(node -> node.currentState == NODE_IS_STILL_ACTIVE);
    }

    public static int calculateNumberOfNets(ArrayList<NodeData> nodes) {
        return 0;
    }

    public static int calculateNumberOfNodeCollisions(ArrayList<NodeData> nodes) {
        int numCollisions = 0;
        for(int i=0;i<nodes.size();i++) {
            for(int j=i+1;j<nodes.size();j++) {
                if(HarmonyMovement.isNodeWithinSightOfLocation(nodes.get(i), nodes.get(j).currentLocation, NODE_DETECTION_THRESHOLD) &&
                   HarmonyMovement.isNodeWithinSightOfLocation(nodes.get(j), nodes.get(i).currentLocation, NODE_DETECTION_THRESHOLD))
                    numCollisions++;
            }
        }
        return numCollisions;
    }

    //We are looking at the number of distinct hostiles detected at a given timestep
    public static int calculateNumberOfHostilesDetected(ArrayList<NodeData> nodes) {
        List<NodeData> friendlies = getNodesOfSpecificIFF(nodes,'F');
        List<NodeData> hostiles = getNodesOfSpecificIFF(nodes,'H');

        if(hostiles.isEmpty())
            return 0;

        List<String> hostileUUIDs = new ArrayList<>();
        for(NodeData friend: friendlies) {
            double detectionRadiusInMetres = (friend.detectionRadiusInMetres > 0.0) ? friend.detectionRadiusInMetres : DEFAULT_DETECTION_RADIUS_IN_METRES;
            List<NodeData> hostilesWithinSight = hostiles.stream().filter(hostile -> HarmonyMovement.isNodeWithinSightOfLocation(friend, hostile.currentLocation, detectionRadiusInMetres)).collect(Collectors.toList());
            for(NodeData hostile: hostilesWithinSight) {
                if(!hostileUUIDs.contains(hostile.nodeUUID))
                    hostileUUIDs.add(hostile.nodeUUID);
            }
        }

        return hostileUUIDs.size();
    }

    /**
     * Try to assign the closest enemy for a node. If there are no nearby enemies within a detection radius, then the assigned value is null
     */
    public static void assignClosestEnemy(NodeData currentNode, ArrayList<NodeData> nodes) {
        if(currentNode.symbol.charAt(1) == 'N')
            return;

        Predicate<NodeData> isNeutralNode = node -> node.symbol.charAt(1) == 'N';
        Predicate<NodeData> isNodeDestroyed = node -> node.symbol.charAt(3) == 'X';
        Predicate<NodeData> twoNodesOfTheSameIFF = node -> node.symbol.charAt(1) == currentNode.symbol.charAt(1);
        Predicate<NodeData> withinSight = node -> HarmonyMovement.isNodeWithinSightOfLocation(currentNode, node.currentLocation, node.detectionRadiusInMetres);

        Map<NodeData, Double> distancesToEnemy = new HashMap<>();
        List<NodeData> filtered = nodes.stream().filter(isNeutralNode.negate().and(isNodeDestroyed.negate()).and(twoNodesOfTheSameIFF.negate()).and(withinSight)).collect(Collectors.toList());
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
