import java.util.*;

public class HarmonyDecision {

    private static double DEFAULT_DETECTION_RADIUS_IN_METRES = 500.0;

    public static void makeDecisionForEachNode(ArrayList<NodeData> nodes, boolean debugEnabled) {
        for(NodeData currentNode: nodes) {
            //If node is destroyed, it can't move so no decision is to be made
            if(currentNode.symbol.charAt(3) == 'X')
                continue;

            String decision = "";
            for (int i = currentNode.strategies.size() - 1; i >= 0; i--) {
                String strategy = currentNode.strategies.get(i);
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
                        detectionRadius = DEFAULT_DETECTION_RADIUS_IN_METRES;
                    }
                    HarmonyAwareness.assignClosestEnemy(currentNode, nodes, detectionRadius);
                    if(currentNode.closestEnemy != null) {
                        if (strategy.toLowerCase().contains("and attack")) {
                            if(HarmonyAwareness.isNodeVeryCloseToTheEnemy(currentNode)) {
                                decision = "Attack closest enemy";
                                currentNode.closestEnemy.symbol.replace(currentNode.closestEnemy.symbol.charAt(3), 'X');
                            }
                            else {
                                decision = "Move to the Closest enemy";
                            }
                            break;
                        }
                        else if(strategy.toLowerCase().contains("and avoid")) {
                            if(HarmonyAwareness.isNodeVeryCloseToTheEnemy(currentNode)) {
                                //Node gets killed
                                currentNode.symbol.replace(currentNode.symbol.charAt(3), 'X');
                            }
                            else {
                                decision = "Move away from Closest enemy";
                            }
                            break;
                        }
                    }

                } else if (strategy.startsWith("Move towards Raspberry Creek") || strategy.startsWith("Move towards Raspberry Ck")) {
                    currentNode.nextLocation = HarmonyMovement.RASPBERRY_CK;
                    if(strategy.endsWith("ASAP")) {
                        currentNode.operationalSpeedInKmH = currentNode.maxOperationalSpeedInKmH;
                    }
                    decision = "Move to the next location";
                    break;
                } else if (strategy.startsWith("Move")) {
                    decision = strategy;
                    break;
                } else if (strategy.startsWith("Follow")) {
                    if(wordsInStrategy.length == 2) {
                        if(wordsInStrategy[1].toLowerCase().equals("commander")) {
                            decision = strategy;
                            currentNode.operationalSpeedInKmH = currentNode.myCommander.operationalSpeedInKmH;
                        }
                        else {
                            HarmonyAwareness.setNodeToFollow(currentNode, wordsInStrategy[1], nodes);
                            decision = "Follow Node";
                            currentNode.operationalSpeedInKmH = currentNode.nodeToFollow.operationalSpeedInKmH;
                        }
                        break;
                    }
                }
            }
            if(!decision.isEmpty())
                HarmonyMovement.carryOutDecision(currentNode, decision, debugEnabled);
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
