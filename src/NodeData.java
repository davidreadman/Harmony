import gov.nasa.worldwind.geom.Angle;
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
     /*initially ;used currentLocation and nextLocation, but these were changed at the same time, previousLocation
     allows more information to use for direction of travel
      */
     Position currentLocation,previousLocation;
     String nodeType;
     String nodeIFF;
     String symbol;
     TacticalSymbol symbolIdentifier;
     double operationalSpeedInKmH;
     double maximumSpeedInKmH;
     double detectionRadiusInKm;
     String closestHostile;
     //could put in a 'closest road'
     /* the next information is that evaluated by Harmony from the list of nodes and the current node positions */
    /*initial implementation, this is not created, can be inferred from currentLocation and nextLocation, may need
    a previousLocation
     */
    double directionOfTravel;
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
