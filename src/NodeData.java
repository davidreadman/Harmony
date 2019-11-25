import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.symbology.TacticalSymbol;


public class NodeData
{
     public enum NodeType {BLUE, RED, GREEN};

     Double Lat;
     Double Lon;
     Position currentLocation;
     Position nextLocation;
     String NodeUUID;
     String type;
     String symbol;
     TacticalSymbol symbolIdentifier;
     NodeType nodeType;
     double minSpeed;
     double maxSpeed;

    NodeData (String newUUID, String newtype, Position newPosition, String newSymbol, NodeType nodeType, double minSpeed, double maxSpeed)
    {
        // constructor
        NodeUUID =newUUID;
        type = newtype;
        currentLocation = newPosition;
        nextLocation = newPosition;
        symbol=newSymbol;
        this.nodeType = nodeType;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
    }

    NodeData() {}
     
 
}
