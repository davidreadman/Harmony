import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

//note: distanceInRadians = distanceInMeters / globe.getRadius();
public class HarmonyMovement
{
    private static final Position RASPBERRY_CK = Position.fromDegrees(-22.71220, 150.40076, 1);
    private static final int NORTH = 0;
    public static final Globe tempGlobe = new Earth();
    private static final double MILES_TO_KM = 1.60944;
    public static final int KM_TO_METRES = 1000;
    public static final double ONE_HUNDRED_METERS = 100;


     /*
     Make decision function will expand dramatically in future, at the start it will just move node towards Raspberry Ck
      */

    /**
     * @param currentNode
     * @param decision
     * @return
     */
    public static Position makeDecision(NodeData currentNode, String decision)
    {
         /*future work: use the current direction of movement as the initialiser for bearing so the default faces same
         direction on break(do nothing)
          */
        double bearingInDegrees = 0;
        double distanceToTravel = 0;
        //initial Raspberry cK
        //find out what direction raspberry ck is in
        System.out.println("decision made : " + decision);
        switch (decision)
        {
            case "Move Raspberry Ck":
                bearingInDegrees = Position.greatCircleAzimuth(currentNode.currentLocation, RASPBERRY_CK).degrees;
                distanceToTravel = ONE_HUNDRED_METERS;
                break;
            case "Move North":
                bearingInDegrees = NORTH;
                break;
            default:
                //do nothing
                System.out.println("do nothing ");
        }

        //and move towards it
        Position nextPosition = moveDirectionDistance(currentNode.currentLocation, bearingInDegrees, distanceToTravel);
        //and update the node next and current Position with the location of the new position
        updatePosition(nextPosition, currentNode);


        return (nextPosition);
    }


    /*
     * This function is used by all the functions below it.
     * We could have copied and pasted the lines of code to help understand the logic as to what is happening in each function
     * However, the approach below was taken to assist maintenance if issues arise.
     */

    private static Position moveDirectionDistance(Position currentLocation, Angle bearingAsAngle, double distanceInMeters)
    {
        //Convert a distance values in miles to radians
        double distanceRadians = distanceInMeters / tempGlobe.getRadius();
        //Use great circle end position to get the next position from the current location.
        return new Position(LatLon.greatCircleEndPosition(currentLocation, bearingAsAngle.radians, distanceRadians), 0);
    }

    private static Position moveDirectionDistance(Position currentLocation, double bearingAsDegrees, double distanceInMeters)
    {
        //Convert a distance values in miles to radians
        double distanceRadians = distanceInMeters / tempGlobe.getRadius();
        //Generate Angle object with a value in degrees before calling the moveDirection()
        Angle bearing = Angle.fromDegrees(bearingAsDegrees);
        //Use great circle end position to get the next position from the current location.
        return new Position(LatLon.greatCircleEndPosition(currentLocation, bearing.radians, distanceRadians), 0);
    }

    private static Position moveVDirectionDistance(Position currentLocation, double bearingAsDegrees, double variationAsDegrees, double distanceInMeters)
    {
        //Get a value of the bearing +/- variation
        //https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadLocalRandom.html
        double randomizedBearing = ThreadLocalRandom.current().nextDouble(bearingAsDegrees - variationAsDegrees, bearingAsDegrees + variationAsDegrees + 0.01);
        return moveDirectionDistance(currentLocation, randomizedBearing, distanceInMeters);
    }

    private static Position moveDirectionVDistance(Position currentLocation, double bearingAsDegrees, double minimumDistanceInMeters, double maximumDistanceInMeters)
    {
        //Get a distance value between the minimum and maximum distance
        double randomisedDistanceInMeters = ThreadLocalRandom.current().nextDouble(minimumDistanceInMeters, maximumDistanceInMeters);
        return moveDirectionDistance(currentLocation, bearingAsDegrees, randomisedDistanceInMeters);
    }

    private static Position moveVDirectionVDistance(Position currentLocation, double bearingAsDegrees, double variationAsDegrees, double minDistanceInMeters, double maxDistanceInMeters)
    {
        //Get a value of the bearing +/- variation
        double randomizedBearing = ThreadLocalRandom.current().nextDouble(bearingAsDegrees - variationAsDegrees, bearingAsDegrees + variationAsDegrees + 0.01);
        //Get a distance value between the minimum and maximum distance
        double randomisedDistanceInMeters = ThreadLocalRandom.current().nextDouble(minDistanceInMeters, maxDistanceInMeters + 0.01);

        return moveDirectionDistance(currentLocation, randomizedBearing, randomisedDistanceInMeters);
    }

    //identify the bearing to the target in degrees
    private static double bearingToTargetInDegrees(Position currentLocation, Position targetLocation)
    {
        double bearingInDegrees = Position.greatCircleAzimuth(currentLocation, targetLocation).degrees;
        return (bearingInDegrees);
    }

    //identify the distance to the target in meters
    private static double distanceToTargetInMeters(Position currentLocation, Position targetLocation)
    {
        double distanceInRadians = Position.greatCircleDistance(currentLocation, targetLocation).radians;
        //to get a distance in meters, multiply the distance in radians by the radius of the globe
        //https://nasaworldwind.github.io/WorldWindJava/gov/nasa/worldwind/geom/LatLon.html
        //see notes on greatCircleDistance
        double distanceInMeters = distanceInRadians * tempGlobe.getRadius();
        return (distanceInMeters);
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

    /* update the locations and direction of travel */
    public static void updatePosition(Position newPosition, NodeData nodeData)
    {
        nodeData.previousLocation = nodeData.currentLocation;
        nodeData.currentLocation = newPosition;
        nodeData.directionOfTravel = bearingToTargetInDegrees(nodeData.previousLocation, nodeData.currentLocation);
        //and set the symbol object for the displayed symbol
        nodeData.symbolIdentifier.setPosition(newPosition);
    }
    //initially just loading the 'closest enemy' into the node
    public static void situationalAwareness(NodeData[] nodeData,NodeData thisNode)
    {
        double closestFoe;
        //sweep through all the nodes to see which is closest
        for (int i = 0; i < nodeData.length; i++)
        {
            //Skip node since it's the same as the node at index i.
            if (nodeData[i]==thisNode)
            {
                continue;
            }



        }
    }




}

