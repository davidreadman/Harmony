import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.symbology.TacticalSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NodeData
{
     public enum NodeType {FRIEND, HOSTILE, NEUTRAL, NULL}

    Double Lat;
     Double Lon;
     Position currentLocation;
     Position nextLocation;
     String NodeUUID;
     String symbol;
     TacticalSymbol symbolIdentifier;
     NodeType nodeType;
     Speed operationalSpeed;
     Speed maximumSpeed;
     Distance detectionRadius;
     List<DetectedNode> detectedNodes = new ArrayList<>();
     //Capture positions travelled at each epoch. This info could be used to fed into SMARTNet
     Map<Integer, Position> positionTravelForEachEpoch = new HashMap<Integer, Position>();
     //Capture decisions made at each epoch. This info could be used to fed into SMARTNet.
     Map<Integer, List<Object>> decisionsMadeForEachEpoch = new HashMap<>();
    NodeData() {}


    public void updateNodesDetectedByMe(int currentEpoch, List<DetectedNode> newList) {
         detectedNodes.clear();
         detectedNodes.addAll(newList);
         analyseDetectedNodes(currentEpoch);
    }

    public void addPositionForEpoch(int currentEpoch, Position newPosition) {
        positionTravelForEachEpoch.put(currentEpoch,newPosition);
    }

    public void addDecisionForCurrentEpoch(int currentEpoch, int decision) {
        decisionsMadeForEachEpoch.putIfAbsent(currentEpoch, new ArrayList<>());
        decisionsMadeForEachEpoch.get(currentEpoch).add(decision);
    }

    /**
     * Analyse the nodes we just detected and make a decision accordingly
     * Then capture the decisions made at the current epoch.
     */
    private void analyseDetectedNodes(int currentEpoch) {
        decisionsMadeForEachEpoch.putIfAbsent(currentEpoch, new ArrayList<>());
        for(DetectedNode detectedNode : detectedNodes) {
        }
        if(nodeType == NodeType.FRIEND){
            MovementDecision.makeDecision(currentEpoch,this,1);
        }
    }
}
