import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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
    public static ArrayList<Integer>adjacentVertices[];
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

    public static int calculateNumberOfNets(ArrayList<NodeData> nodes)
    {
        int distance = 2000;
        int index =0;
        List<NodeData> friendlyNodes = getNodesOfSpecificIFF(nodes, 'F');
        List<NodeData> scanningToArray ;
        ArrayList<ArrayList<String>> elementsInNet= new ArrayList<ArrayList<String>>();
        Boolean thisNetExists;
        ArrayList<String>baseArray = new ArrayList<String>();
        ArrayList<ArrayList<String>> dendrogram= new ArrayList<ArrayList<String>>();
        //create a list for each friendly node, this will contain the adjacent nodes

        adjacentVertices=new ArrayList[friendlyNodes.size()];

        for (int i=0; i<friendlyNodes.size()-1;i++)
        {
            baseArray.add(friendlyNodes.get(i).nodeUUID);
            adjacentVertices[i] = new ArrayList();
                    thisNetExists = false;
                    scanningToArray = friendlyNodes.subList(index,friendlyNodes.size());
                    for (int j = index; j< friendlyNodes.size(); j++)
                    {
                    ArrayList<String> nets = new ArrayList<String>();
                        if (friendlyNodes.get(j) != friendlyNodes.get(i))
                        {
                            double nodeDistance = HarmonyMovement.distanceToTargetInMeters(friendlyNodes.get(i).currentLocation, friendlyNodes.get(j).currentLocation);
                            //System.out.println("node checking from " + friendlyNodes.get(i).nodeUUID + " nets");
                          //  System.out.println("checking node " + scanningTo.nodeUUID + " " + nodeDistance + " meters");
                            if (nodeDistance<= distance)
                            {
                                adjacentVertices[i].add(j);
                                nets.add(friendlyNodes.get(i).nodeUUID);
                                nets.add(friendlyNodes.get(j).nodeUUID);
                                dendrogram.add(nets);
                            }
                        }
                }
            index++;
        }
        //and then create the last vertice adjacency so it isn't null
        adjacentVertices[friendlyNodes.size()-1] = new ArrayList();
        baseArray.add(friendlyNodes.get(friendlyNodes.size()-1).nodeUUID);
        System.out.println("base array: " + baseArray);
        System.out.println("dendrogram: " + dendrogram);
        System.out.println("adjacency: " + adjacentVertices);
        boolean visited[] = new boolean[friendlyNodes.size()];
        int count = 0;
        for (int z =0; z<friendlyNodes.size();z++)
        {
            if (!visited[z])
        {
            System.out.println("new net : " + z);
            DFSearch(z, visited);
            count++;
        }
       }
        System.out.println("number of nets : " + count);
        //there cannot be any more nets than the number of pairs < distance plus singletons

     //   System.out.println("distilled dendrogram: " + distillDendrogram);



        // System.out.println("There are "+numberOfNets+" nets");
        return elementsInNet.size();
}
public static void DFSearch(int v, boolean visited[])
{
    visited[v] = true;
    System.out.println("v: " + v+ " "+adjacentVertices[v]);
    Iterator<Integer> i = adjacentVertices[v].listIterator();
    while (i.hasNext())
    {
        int n = i.next();
        if (!visited[n])
            DFSearch(n, visited);
    }
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
