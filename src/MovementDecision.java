import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;

import java.util.concurrent.ThreadLocalRandom;

//note: distanceInRadians = distanceInMeters / globe.getRadius();
public class MovementDecision
{
    public static final int VARIATION_DEGREES = 20;

    public enum MovementDirection {
        NORTH,
        SOUTH,
        EAST,
        WEST,
        NORTH_EAST,
        NORTH_WEST,
        SOUTH_EAST,
        SOUTH_WEST
    }


    DisplayWW movementDisplayWW;
    NodeData[] movementNodeData;
    //need a Globe to do positional calculations on great circles
    Globe tempGlobe;

    public MovementDecision(NodeData[] nodeData)
    {

        movementNodeData = nodeData;
        tempGlobe = new Earth();

    }

    /**
     * Create a new position so that it moves towards or away from a target.
     * @param current Position we are moving from
     * @param target Position we are either moving towards or away from
     * @param distanceInMeters how much movement there is
     * @param variation true implies that we will want to move with variation
     * @param moveTowards true implies we want to move our current position towards the target, false implies we want to move away
     * @return a new position towards/away from the target after movement
     */
    public Position moveWithoutDirection(Position current, Position target, Double distanceInMeters, boolean variation, boolean moveTowards) {
        Angle bearing = Position.greatCircleDistance(current, target);
        Double bearingInDegrees = moveTowards ? bearing.degrees : (bearing.degrees + 180) % 360;

        if(variation) {
            return moveWithVariation(current, bearingInDegrees, distanceInMeters);
        }
        else {
            return moveToNewLocationWithAngle(current, bearing, distanceInMeters);
        }
    }

    public Position selectDistanceFromRangeThenMoveWithoutDirection(Position current, Position target, Double minDistance, Double maxDistance, boolean variation, boolean moveTowards) {
        return moveWithoutDirection(current,target,ThreadLocalRandom.current().nextDouble(minDistance, maxDistance+0.01), variation, moveTowards);
    }

    private Position moveWithVariation(Position center, double bearingInDegrees, Double distanceInMeters) {
        return moveToNewLocationWithAngle(center, Angle.fromDegrees(ThreadLocalRandom.current().nextDouble(bearingInDegrees-VARIATION_DEGREES, bearingInDegrees+VARIATION_DEGREES+0.01)), distanceInMeters * Math.random());
    }

    public Position moveToNewLocationWithAngle(Position center, Angle angle, Double distanceInMeters) {
        double distanceRadians = distanceInMeters/ tempGlobe.getRadius();
        LatLon newLatLon= LatLon.greatCircleEndPosition(center,angle.radians,distanceRadians);
        return new Position(newLatLon,0);
    }

    public Position selectDistanceFromRangeThenMoveWithDirection(Position center, MovementDirection direction, Double minDistance, Double maxDistance, boolean variation) {
        return moveWithDirection(center, direction, ThreadLocalRandom.current().nextDouble(minDistance, maxDistance+0.01), variation);
    }

    public Position moveWithDirection(Position center, MovementDirection direction, Double distanceInMeters, boolean variation) {
        int lowerBound = -1;
        int upperBound = -1;

        switch(direction) {
            case EAST:
                lowerBound = 0;
                upperBound = 1;
                break;
            case NORTH_EAST:
                lowerBound = 1;
                upperBound = 90;
                break;
            case NORTH:
                lowerBound = 90;
                upperBound = 91;
                break;
            case NORTH_WEST:
                lowerBound = 91;
                upperBound = 180;
                break;
            case WEST:
                lowerBound = 180;
                upperBound = 181;
                break;
            case SOUTH_WEST:
                lowerBound = 181;
                upperBound = 270;
                break;
            case SOUTH:
                lowerBound = 270;
                upperBound = 271;
                break;
            case SOUTH_EAST:
                lowerBound = 271;
                upperBound = 360;
                break;
            default:
                break;
        }
        int randomisedValue = ThreadLocalRandom.current().nextInt(lowerBound, upperBound+1);
        if(variation) {
            return moveWithVariation(center, randomisedValue, distanceInMeters);
        }
        else {
            return moveToNewLocationWithAngle(center, Angle.fromDegrees(randomisedValue), distanceInMeters);
        }
    }
}
