import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.symbology.TacticalGraphic;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class HarmonyDecision {

    public static void makeDecisionForEachNode(ArrayList<NodeData> nodes, boolean debugEnabled) {
        for(NodeData currentNode: nodes) {
            String decision = "";
            if(currentNode.nextLocation != null) {
                decision = "Move to the next location";
            }
            HarmonyMovement.makeDecision(currentNode, decision, debugEnabled);
        }
    }

    //Uncomment and move code once tactical graphics are implemented into code
/*    public void assignCheckpointsForEachNode(ArrayList<NodeData> nodes, Map<NodeData, TacticalGraphic> nodeDataTacticalGraphicMap) {
        for(NodeData currentNode : nodes) {
            if(nodeDataTacticalGraphicMap.containsKey(currentNode)) {
                Iterable<? extends Position> itr = nodeDataTacticalGraphicMap.get(currentNode).getPositions();
                List<Position> positionsToVisit = new ArrayList<>();
                while (itr.iterator().hasNext()) {
                    Position position = itr.iterator().next();
                    positionsToVisit.add(position);
                }
                currentNode.updatePlanCheckpoints(positionsToVisit);
                currentNode.isCarryingOutAPlan = true;
            }
        }
    }*/
}
