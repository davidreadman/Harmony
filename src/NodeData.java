import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.symbology.TacticalSymbol;

public class NodeData
{
     Double Lat;
     Double Lon;
     LatLon currentLocation;
     LatLon nextLocation;
     String NodeUUID;
     String type;
     String symbol;
     TacticalSymbol symbolIdentifier;
     NodeData ()
     {
     }
    NodeData (String newUUID, String newtype, LatLon newPosition, String newSymbol)
    {
        // constructor
        NodeUUID =newUUID;
        type = newtype;
        currentLocation = newPosition;
        nextLocation = newPosition;
        symbol=newSymbol;
    }

     
 
}
