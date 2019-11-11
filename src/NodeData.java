import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.symbology.TacticalSymbol;

public class NodeData
{
     Double Lat;
     Double Lon;
     LatLon currentLocation;
     String NodeUUID;
     String type;
     String symbol;
     NodeData ()
     {
     }
    NodeData (String newUUID, String newtype, LatLon newPosition, String newSymbol)
    {
        // constructor
        NodeUUID =newUUID;
        type = newtype;
        currentLocation = newPosition;
        symbol=newSymbol;
    }

     
 
}
