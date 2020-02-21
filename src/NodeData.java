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
     String nodeUUID;
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
     double minOperationalSpeedInKmH;
     double operationalSpeedInKmH;
     double maxOperationalSpeedInKmH;
     /* the next information is that evaluated by Harmony from the list of nodes and the current node positions */
    /*initial implementation, this is not created, can be inferred from currentLocation and nextLocation, may need
    a previousLocation
     */
    double directionOfTravel;

    /* Used for logging purposes as node is the source of truth */
    /* We want to log the current decision that it made and the corresponding metric */
    String currentMetric = "None";
    String currentDecision = "";

    NodeData closestEnemy;

    NodeData myCommander, nodeToFollow;

    /**
     * A list of strategies this node can take.
     * The order of which these strategies appear in the list are of increasing priority
     * e.g : Move towards raspberry creek ASAP, Detect a hostile within 20 kms
     * The first strategy carried out will be to check for closest enemies before moving towards Raspberry Ck
     */
    List<String> strategies = new ArrayList<>();
}
