import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.symbology.TacticalSymbol;

import java.util.ArrayList;
import java.util.List;


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
     double detectionRadiusInKm;
     List<DetectedNode> detectedNodes = new ArrayList<>();

    NodeData() {}


    public void updateNodesDetectedByMe(List<DetectedNode> newList) {
         detectedNodes.clear();
         detectedNodes.addAll(newList);
         analyseDetectedNodes();
    }

    public void updatePosition(Position newPosition) {
         nextLocation = newPosition;
         symbolIdentifier.setPosition(newPosition);
         currentLocation = newPosition;
    }

    /**
     * Analyse the nodes we just detected and make a decision accordingly
     */
    private void analyseDetectedNodes() {
        for (DetectedNode detectedNode : detectedNodes) {
        }
    }
}
