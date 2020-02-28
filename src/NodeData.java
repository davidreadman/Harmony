import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.symbology.TacticalSymbol;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     String symbol;
     TacticalSymbol symbolIdentifier;
     double operationalSpeedInKmH;
     double maxOperationalSpeedInKmH;
     double detectionRadiusInMetres = 0;

    /* Used for logging purposes as node is the source of truth */
    /* We want to log the current decision that it made and the corresponding metric */
    Map.Entry<String, Double> currentMetric = new AbstractMap.SimpleEntry<>("None", 0.0);
    String currentDecision = "None";
    int currentState = HarmonyAwareness.NODE_IS_STILL_ACTIVE;

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
