import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;

import java.util.concurrent.ThreadLocalRandom;

//note: distanceInRadians = distanceInMeters / globe.getRadius();
public class HarmonyMovement
{
    public static final Position RASPBERRY_CK = Position.fromDegrees(-22.71220, 150.40076, 1);
    private static final int NORTH = 0;
    private static final int EAST = 90;
    private static final int SOUTH = 180;
    private static final int WEST = 270;
    public static final Globe tempGlobe = new Earth();
    private static final double METRES_PER_SECOND_TO_KM_PER_HOUR = 3.6;
    public static final int VARIATION_AS_DEGREES = 20;


     /*
     Make decision function will expand dramatically in future, at the start it will just move node towards Raspberry Ck
      */

    /**
     * @param currentNode
     * @param decision
     * @return
     */
    public static Position carryOutDecision(NodeData currentNode, String decision, boolean debugEnabled)
    {
         /*future work: use the current direction of movement as the initialiser for bearing so the default faces same
         direction on break(do nothing)
          */
        double bearingInDegrees = 0;
        double distanceToTargetInMetres = 0;
        switch (decision)
        {
            case "Move to the next location":
                bearingInDegrees = bearingToTargetInDegrees(currentNode.currentLocation, currentNode.nextLocation);
                distanceToTargetInMetres = distanceToTargetInMeters(currentNode.currentLocation, currentNode.nextLocation);
                break;
            case "Move North":
                bearingInDegrees = NORTH;
                distanceToTargetInMetres = currentNode.operationalSpeedInKmH/METRES_PER_SECOND_TO_KM_PER_HOUR;
                break;
            case "Move South":
                bearingInDegrees = SOUTH;
                distanceToTargetInMetres = currentNode.operationalSpeedInKmH/METRES_PER_SECOND_TO_KM_PER_HOUR;
                break;
            case "Move West":
                bearingInDegrees = WEST;
                distanceToTargetInMetres = currentNode.operationalSpeedInKmH/METRES_PER_SECOND_TO_KM_PER_HOUR;
                break;
            case "Move East":
                bearingInDegrees = EAST;
                distanceToTargetInMetres = currentNode.operationalSpeedInKmH/METRES_PER_SECOND_TO_KM_PER_HOUR;
                break;
            case "Move away from Closest Enemy":
                bearingInDegrees = oppositeDirection(bearingToTargetInDegrees(currentNode.currentLocation, currentNode.closestEnemy.currentLocation));
                //Attempt to travel at maximum speed to get away from the enemy.
                distanceToTargetInMetres = currentNode.maxOperationalSpeedInKmH /METRES_PER_SECOND_TO_KM_PER_HOUR;
                break;
            case "Move to the Closest Enemy":
                bearingInDegrees = bearingToTargetInDegrees(currentNode.currentLocation, currentNode.closestEnemy.currentLocation);
                distanceToTargetInMetres = distanceToTargetInMeters(currentNode.currentLocation, currentNode.closestEnemy.currentLocation);
                break;
            case "Follow Commander":
                bearingInDegrees = bearingToTargetInDegrees(currentNode.currentLocation, currentNode.myCommander.currentLocation);
                distanceToTargetInMetres = distanceToTargetInMeters(currentNode.currentLocation, currentNode.myCommander.currentLocation);
                break;
            case "Follow Node":
                bearingInDegrees = bearingToTargetInDegrees(currentNode.currentLocation, currentNode.nodeToFollow.currentLocation);
                distanceToTargetInMetres = distanceToTargetInMeters(currentNode.currentLocation, currentNode.nodeToFollow.currentLocation);
                break;
            default:
                break;
        }

        //Only move if we really need to.
        Position nextPosition;
        if(distanceToTargetInMetres > 0) {
            nextPosition = moveVDirectionSpeed(currentNode, bearingInDegrees, VARIATION_AS_DEGREES,distanceToTargetInMetres);
        }
        else {
            nextPosition = currentNode.currentLocation;
        }
        //and update the node next and current Position with the location of the new position
        updatePosition(nextPosition, currentNode);
        if(debugEnabled)
            System.out.println(String.format("Decision taken by %s: %s", currentNode.nodeUUID, decision));

        currentNode.currentDecision = decision;
        return (nextPosition);
    }

    private static Position moveDirectionSpeed(NodeData currentNode, double bearingAsDegrees, double distanceToTargetInMeters)
    {
        //Set the travelling distance of node to the minimum value between distance to target location and the node's metres per second rate.
        double distanceToTravel = Math.min(distanceToTargetInMeters, currentNode.operationalSpeedInKmH/METRES_PER_SECOND_TO_KM_PER_HOUR);

        //Convert a distance values in miles to radians
        double distanceRadians = distanceToTravel / tempGlobe.getRadius();
        //Generate Angle object with a value in degrees before calling the moveDirection()
        Angle bearing = Angle.fromDegrees(bearingAsDegrees);
        //Use great circle end position to get the next position from the current location.
        return new Position(LatLon.greatCircleEndPosition(currentNode.currentLocation, bearing.radians, distanceRadians), 0);
    }

    private static Position moveVDirectionSpeed(NodeData currentNode, double bearingAsDegrees, double variationAsDegrees, double distanceInMeters)
    {
        //Get a value of the bearing +/- variation
        //https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadLocalRandom.html
        double randomizedBearing = ThreadLocalRandom.current().nextDouble(bearingAsDegrees - variationAsDegrees, bearingAsDegrees + variationAsDegrees + 0.01);
        return moveDirectionSpeed(currentNode, randomizedBearing, distanceInMeters);
    }

    //identify the bearing to the target in degrees
    public static double bearingToTargetInDegrees(Position currentLocation, Position targetLocation)
    {
        double bearingInDegrees = Position.greatCircleAzimuth(currentLocation, targetLocation).degrees;
        //For the consistent direction of travel to be positive, we need to add 360 if the bearing is negative.
        //This consistency is required for ML.
        if(bearingInDegrees < 0)
            bearingInDegrees += 360.0;

        return bearingInDegrees;
    }

    //identify the distance to the target in meters
    public static double distanceToTargetInMeters(Position currentLocation, Position targetLocation)
    {
        Angle angularDistance = Position.greatCircleDistance(currentLocation, targetLocation);
        if(angularDistance == Angle.ZERO) {
            return 0.0;
        }
        else {
            //to get a distance in meters, multiply the distance in radians by the radius of the globe
            //https://nasaworldwind.github.io/WorldWindJava/gov/nasa/worldwind/geom/LatLon.html
            //see notes on greatCircleDistance
            double distanceInMeters = angularDistance.radians * tempGlobe.getRadius();
            return Math.abs(distanceInMeters);
        }
    }

    /* for any 0 to 360 degrees angle, the opposite angle is 180 plus that angle mod 360 */
    private static double oppositeDirection(double bearingAsDegrees)
    {
        double theActualBearingIs;
        if (bearingAsDegrees < 0)
        {
            theActualBearingIs = bearingAsDegrees + 360;
        } else if (bearingAsDegrees > 360)
        {
            theActualBearingIs = bearingAsDegrees % 360;
        } else
        {
            theActualBearingIs = bearingAsDegrees;
        }

        return ((180 + theActualBearingIs) % 360);

    }

    public static boolean isNodeWithinSightOfLocation(NodeData currentNode, Position otherLocation, double detectionRadiusInMetres) {
        return distanceToTargetInMeters(currentNode.currentLocation, otherLocation) < detectionRadiusInMetres+0.01;
    }

    public static boolean hasNodeReachedLocation(NodeData currentNode, Position targetLocation) {
        return isNodeWithinSightOfLocation(currentNode, targetLocation, 0.0);
    }

    public static boolean hasNodeReachedRaspberryCreek(NodeData currentNode) {
        return hasNodeReachedLocation(currentNode, RASPBERRY_CK);
    }

    /* update the locations and direction of travel */
    public static void updatePosition(Position newPosition, NodeData nodeData)
    {
        nodeData.previousLocation = nodeData.currentLocation;
        nodeData.currentLocation = newPosition;
        //and set the symbol object for the displayed symbol
        nodeData.symbolIdentifier.setPosition(newPosition);
    }

}

