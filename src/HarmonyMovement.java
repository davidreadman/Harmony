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


     /*
     Make decision function will expand dramatically in future, at the start it will just move node towards Raspberry Ck
      */

    /**
     *
     * @param currentNode
     * @param decision
     * @return
     */
     public static Position makeDecision( NodeData currentNode,int decision)
     {
         double bearingInDegrees;
         //initial Raspberry cK
         //find out what direction raspberry ck is in
         switch(decision)
         {
             case 1:
                 break;
             case 2:
                 break;
             default:
                 //do nothing
         }
         if(decision ==1)
         {
             //travel towards raspberry ck
             bearingInDegrees = Position.greatCircleAzimuth(currentNode.currentLocation, RASPBERRY_CK).degrees;
         }
         else
         {
             bearingInDegrees = NORTH;
         }

         //and move towards it
         Position nextPosition = moveDirectionDistance(currentNode.currentLocation, bearingInDegrees,Distance.fromMiles(100));
         //and update the node next and current Position with the location of the new position
         currentNode.nextLocation = nextPosition;
         currentNode.currentLocation = nextPosition;
         //and set the symbol object for the displayed symbol
         currentNode.symbolIdentifier.setPosition(nextPosition);

         return(nextPosition);
     }


    /*
     * This function is used by all the functions below it.
     * We could have copied and pasted the lines of code to help understand the logic as to what is happening in each function
     * However, the approach below was taken to assist maintenance if issues arise.
     */

    private static Position moveDirection(Position currentLocation, Angle bearingAsAngle, Distance distanceToTravel)
    {
        //Convert a distance values in miles to radians
        double distanceRadians = distanceToTravel.getDistanceInMiles()/ tempGlobe.getRadius();

        //Use great circle end position to get the next position from the current location.
        return new Position(LatLon.greatCircleEndPosition(currentLocation, bearingAsAngle.radians, distanceRadians),0);
    }

    private static Position moveDirectionDistance(Position currentLocation, double bearingAsDegrees, Distance distanceToTravel)
    {
        //Generate Angle object with a value in degrees before calling the moveDirection()
        Angle bearing = Angle.fromDegrees(bearingAsDegrees);
        return moveDirection(currentLocation, bearing, distanceToTravel);
    }
    private static Position moveVDirectionDistance(Position currentLocation, double bearingAsDegrees, double variationAsDegrees, Distance distanceToTravel)
    {
        //Get a value of the bearing +/- variation
        double randomizedBearing = ThreadLocalRandom.current().nextDouble(bearingAsDegrees-variationAsDegrees, bearingAsDegrees+variationAsDegrees+0.01);
        return moveDirectionDistance(currentLocation, randomizedBearing, distanceToTravel);
    }
    private static Position moveDirectionVDistance(Position currentLocation, double bearingAsDegrees, Distance minDistance, Distance maxDistance)
    {
        //Get a distance value between the minimum and maximum distance
        double randomizedDistanceInMiles = ThreadLocalRandom.current().nextDouble(minDistance.getDistanceInMiles(), maxDistance.getDistanceInMiles()+0.01);
        Distance distanceToTravel = Distance.fromMiles(randomizedDistanceInMiles);
        return moveDirectionDistance(currentLocation, bearingAsDegrees, distanceToTravel);
    }
    private static Position moveVDirectionVDistance(Position currentLocation, double bearingAsDegrees, double variationAsDegrees, Distance minDistance, Distance maxDistance)
    {
        //Get a value of the bearing +/- variation
        double randomizedBearing = ThreadLocalRandom.current().nextDouble(bearingAsDegrees-variationAsDegrees, bearingAsDegrees+variationAsDegrees+0.01);
        //Get a distance value between the minimum and maximum distance
        double randomizedDistanceInMiles = ThreadLocalRandom.current().nextDouble(minDistance.getDistanceInMiles(), maxDistance.getDistanceInMiles()+0.01);
        Distance distanceToTravel = Distance.fromMiles(randomizedDistanceInMiles);
        return moveDirectionDistance(currentLocation, randomizedBearing, distanceToTravel);
    }
    /* for any 0 to 360 degrees angle, the opposite angle is 360 minus that angle */
    private static double oppositeDirection(double bearingAsDegrees)
    {
        double theActualBearingIs;
                if (bearingAsDegrees<0)
                {
                    theActualBearingIs = bearingAsDegrees +360;
                }
                else if (bearingAsDegrees>360)
                {
                    theActualBearingIs = bearingAsDegrees%360;
                }
                else
                {
                    theActualBearingIs = bearingAsDegrees;
                }

       return (360 - theActualBearingIs);

    }

        /* propose, instead of these, just apply 'oppositeDirection' to make something move away
        this allows us to use all the variable stuff from the main methods

    private static Position moveTowardsAtOperationalSpeed(NodeData currentNode, DetectedNode detectedNode)
    {
        //max distance to travel is based on the current node's operational speed.
        Distance maxDistanceToTravel = Distance.fromMetres(currentNode.operationalSpeed.getMetresPerSecond());
        //If the distance to our target is less than the maximum distance a node can travel
        //Set the distanceToTravel to be that distance.
        Distance distanceToTravel = (maxDistanceToTravel.compareTo(detectedNode.getDistanceFromNode()) == 1) ? detectedNode.getDistanceFromNode() : maxDistanceToTravel;
        return moveDirection(currentNode.currentLocation, detectedNode.getAzimuthAngle(), distanceToTravel);
    }

    private static Position moveTowardsAtMaximumSpeed(NodeData currentNode, DetectedNode detectedNode) {
        //max distance to travel is based on the current node's maximum speed
        Distance maxDistanceToTravel = Distance.fromMetres(currentNode.maximumSpeed.getMetresPerSecond());
        //If the distance to our target is less than the maximum distance a node can travel
        //Set the distanceToTravel to be that distance.
        Distance distanceToTravel = (maxDistanceToTravel.compareTo(detectedNode.getDistanceFromNode()) == 1) ? detectedNode.getDistanceFromNode() : maxDistanceToTravel;
        return moveDirection(currentNode.currentLocation, detectedNode.getAzimuthAngle(), distanceToTravel);
    }

    private static Position moveAwayAtOperationalSpeed(NodeData currentNode, DetectedNode detectedNode)
    {
        //distance to travel is based on the current node's operational speed.
        Distance distanceToTravel = Distance.fromMetres(currentNode.operationalSpeed.getMetresPerSecond());
        //Add 180 degrees as we are moving away from the detected node
        return moveDirection(currentNode.currentLocation, detectedNode.getAzimuthAngle().add(Angle.POS180), distanceToTravel);
    }

    private static Position moveAwayAtMaximumSpeed(NodeData currentNode, DetectedNode detectedNode) {
        //distance to travel is based on the current node's maximum speed
        Distance distanceToTravel = Distance.fromMetres(currentNode.maximumSpeed.getMetresPerSecond());
        //Add 180 degrees as we are moving away from the detected node
        return moveDirection(currentNode.currentLocation, detectedNode.getAzimuthAngle().add(Angle.POS180), distanceToTravel);
    }
    */

}

