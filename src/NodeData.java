import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.symbology.TacticalSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
The NodeData class holds the information about each node and an overview god mode shared by all NodeData types
 */
public class NodeData
{
    /*the order of the items in this class should reflect as much as possible the order in the config.properties file*/
     String NodeUUID;
     Position currentLocation,nextLocation;
     String nodeType;
     String NodeIFF;
     String symbol;
     TacticalSymbol symbolIdentifier;
     double operationalSpeedInKmH;
     double maximumSpeedInKmH;
     double detectionRadiusInKm;
     /* the next information is that evaluated by Harmony from the list of nodes and the current node positions */

     List<DetectedNode> friendNodesSeen = new ArrayList<>();
    List<DetectedNode> hostileNodesSeen = new ArrayList<>();
    List<DetectedNode> neutralNodesSeen = new ArrayList<>();

     /* the next information is metrics received from the external analysis software (SMARTNet) */
    
    NodeData()
    {
        friendNodesSeen = new ArrayList<>();
        hostileNodesSeen = new ArrayList<>();
        neutralNodesSeen = new ArrayList<>();
    }


/* move to harmonyutilities
    public void updateNodesDetectedByMe(int currentEpoch, List<DetectedNode> newList) {
         detectedNodes.clear();
         detectedNodes.addAll(newList);
         analyseDetectedNodes(currentEpoch);
    }

    public void updatePosition(int currentEpoch, Position newPosition) {
        this.nextLocation = newPosition;
        this.currentLocation = newPosition;
        this.symbolIdentifier.setPosition(newPosition);
        positionTravelForEachEpoch.put(currentEpoch,newPosition);
    }

    public void addDecisionForCurrentEpoch(int currentEpoch, int decision) {
        decisionsMadeForEachEpoch.putIfAbsent(currentEpoch, new ArrayList<>());
        decisionsMadeForEachEpoch.get(currentEpoch).add(decision);
    }

    /**
     * Analyse the nodes we just detected and make a decision accordingly
     * Then capture the decisions made at the current epoch.

    private void analyseDetectedNodes(int currentEpoch) {
        decisionsMadeForEachEpoch.putIfAbsent(currentEpoch, new ArrayList<>());
        for(DetectedNode detectedNode : detectedNodes) {
        }
        MovementDecision.MakeDecision(currentEpoch,this, 1);
        addDecisionForCurrentEpoch(currentEpoch,1);

    }
    */
}
