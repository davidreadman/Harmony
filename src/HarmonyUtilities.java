import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;

import java.util.ArrayList;

/**
 * This class is responsible for working behind the scenes while the GUI is operating
 * This includes simulating a decision for each node at every epoch
 */
public class HarmonyUtilities
{

    //Track how many epochs have passed in the simulation
    private int epochCounter = 0;

    //The nodes within the simulation. This includes friends and hostiles
    private ArrayList<NodeData> nodes = new ArrayList<NodeData>();

    public HarmonyUtilities(NodeData[] nodeData) {
        for(NodeData node: nodeData) {
            nodes.add(node);
        }
    }

    public void incrementEpochCounter() {
        epochCounter++;
    }

    public int getCurrentNumberOfEpochs() {
        return epochCounter;
    }

    public ArrayList<NodeData> getCurrentNodes() {
        return nodes;
    }

    public void addNode(NodeData node) {
        nodes.add(node);
    }

    public void removeNode(NodeData node) {
        nodes.remove(node);
    }

    public void detectNodes() {
        for(int i=0;i<nodes.size();i++) {
            ArrayList<DetectedNode> nodesDetectedByMe = new ArrayList<>();
            NodeData currentNode = nodes.get(i);
            for(int j=0;j<nodes.size();j++) {
                //Skip node at index j since it's the same as the node at index i.
                if(i == j) {
                    continue;
                }
                //The purpose here is to detect nodes with the current nodes detection radius.

             //   Distance distanceFromNode = Distance.fromMiles(Position.greatCircleDistance(currentNode.currentLocation, nodes.get(j).currentLocation).radians * HarmonyMovement.tempGlobe.getRadius());
                //If the distance is within the detection radius, then add to the list of detected nodes
              //  if(distanceFromNode.compareTo(currentNode.detectionRadius) <= 0) {
                    //Calculate the azimuth angle from the current node to the node detected.
                    Angle azimuth = Position.greatCircleAzimuth(currentNode.currentLocation, nodes.get(j).currentLocation);
                //    DetectedNode detectedNode = new DetectedNode(currentNode, nodes.get(j), azimuth, distanceFromNode);
           //         nodesDetectedByMe.add(detectedNode);
              //  }
            }
           // currentNode.updateNodesDetectedByMe(epochCounter, nodesDetectedByMe);
        }
        //Increment the epoch counter;
        epochCounter++;
    }
}
