import gov.nasa.worldwind.geom.Position;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class HarmonyDecision {

    public static Map<String, Position> makeDecisionForEachNode(ArrayList<NodeData> nodes, boolean debugEnabled) {
        Map<String, Position> waypoints = new HashMap<>();
        for(NodeData node: nodes) {
            if(node.currentState != HarmonyAwareness.NODE_IS_STILL_ACTIVE) {
                if(debugEnabled)
                    System.out.println(String.format("Current state of %s: %d", node.nodeUUID, node.currentState));
                continue;
            }
            String decision = "";
            for (int i = node.strategies.size() - 1; i >= 0; i--) {
                String strategy = node.strategies.get(i);
                String[] wordsInStrategy = strategy.split(" ");
                if (strategy.startsWith("Detect")) {
                    double detectionRadius = 0.0;
                    if(strategy.contains("within")){
                       String unit = wordsInStrategy[wordsInStrategy.length-1].toLowerCase();
                       detectionRadius = Double.parseDouble(wordsInStrategy[wordsInStrategy.length-2]);
                       if(unit.contains("km") || unit.equals("kilometres")) {
                           detectionRadius *= 1000;
                       }
                    }
                    else {
                        detectionRadius = HarmonyAwareness.DEFAULT_DETECTION_RADIUS_IN_METRES;
                    }
                    node.detectionRadiusInMetres = detectionRadius;
                    HarmonyAwareness.assignClosestEnemy(node, nodes);
                    if(node.closestEnemy != null) {
                        if (strategy.toLowerCase().contains("and attack")) {
                            if(HarmonyAwareness.isNodeVeryCloseToTheEnemy(node)) {
                                decision = "Attack closest enemy";
                                node.closestEnemy.symbol.replace(node.closestEnemy.symbol.charAt(3), 'X');
                            }
                            else {
                                decision = "Move to the Closest enemy";
                            }
                            break;
                        }
                        else if(strategy.toLowerCase().contains("and avoid")) {
                            if(HarmonyAwareness.isNodeVeryCloseToTheEnemy(node)) {
                                //Node gets killed
                                node.symbol.replace(node.symbol.charAt(3), 'X');
                            }
                            else {
                                decision = "Move away from Closest enemy";
                            }
                            break;
                        }
                    }

                } else if (strategy.startsWith("Move towards Raspberry Creek") || strategy.startsWith("Move towards Raspberry Ck")) {
                    node.nextLocation = HarmonyMovement.RASPBERRY_CK;
                    if(strategy.endsWith("ASAP")) {
                        node.operationalSpeedInKmH = node.maxOperationalSpeedInKmH;
                    }
                    else {
                        node.operationalSpeedInKmH = ThreadLocalRandom.current().nextDouble(1, node.maxOperationalSpeedInKmH+0.01);
                    }
                    if(debugEnabled)
                        System.out.println(String.format("Travelling speed of %s: %.2f", node.nodeUUID, node.operationalSpeedInKmH));
                    decision = "Move to the next location";
                    break;
                } else if (strategy.startsWith("Move")) {
                    decision = strategy;
                    break;
                } else if (strategy.startsWith("Follow")) {
                    if(wordsInStrategy.length == 2) {
                        if(wordsInStrategy[1].toLowerCase().equals("commander")) {
                            if(node.myCommander == null || node.myCommander.currentState == HarmonyAwareness.NODE_IS_DESTROYED)
                                continue;

                            decision = strategy;
                            node.operationalSpeedInKmH = node.myCommander.operationalSpeedInKmH;
                            if(debugEnabled)
                                System.out.println(String.format("Travelling speed of %s: %.2f", node.nodeUUID, node.operationalSpeedInKmH));
                        }
                        else {
                            HarmonyAwareness.setNodeToFollow(node, wordsInStrategy[1], nodes);
                            if(node.nodeToFollow == null || node.nodeToFollow.currentState == HarmonyAwareness.NODE_IS_DESTROYED)
                                continue;

                            decision = "Follow Node";
                            node.operationalSpeedInKmH = node.nodeToFollow.operationalSpeedInKmH;
                            if(debugEnabled)
                                System.out.println(String.format("Travelling speed of %s: %.2f", node.nodeUUID, node.operationalSpeedInKmH));
                        }
                        break;
                    }
                }
            }
            if(!decision.isEmpty())
                waypoints.put(node.nodeUUID, HarmonyMovement.carryOutDecision(node, decision, debugEnabled));

            node.currentState = HarmonyAwareness.currentStateOfNode(node);
        }
        return waypoints;
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
