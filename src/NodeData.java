import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.symbology.TacticalSymbol;

import java.util.ArrayList;
import java.util.List;

/*
The NodeData class holds the information about each node and an overview god mode shared by all NodeData types
 */
public class NodeData
{
    /*the order of the items in this class should reflect as much as possible the order in the plan.properties file*/
     String NodeUUID;
     /*initially ;used currentLocation and nextLocation, but these were changed at the same time, previousLocation
     allows more information to use for direction of travel
     Next location indicates the next known position that the node needs to travel.
      */
     Position currentLocation;
    Position previousLocation;
    Position nextLocation;
    String nodeType;
     String symbol;
     TacticalSymbol symbolIdentifier;
     double operationalSpeedInKmH;
     double maximumSpeedInKmH;
     /* the next information is that evaluated by Harmony from the list of nodes and the current node positions */
    /*initial implementation, this is not created, can be inferred from currentLocation and nextLocation, may need
    a previousLocation
     */
    double directionOfTravel;

    /* Used for logging purposes as node is the source of truth */
    /* We want to log the current decision that it made and the corresponding metric */
    String currentMetric = "";
    String currentDecision = "";

    NodeData closestEnemy;

    List<String> strategies = new ArrayList<>();
}
