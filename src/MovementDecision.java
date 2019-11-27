import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

//note: distanceInRadians = distanceInMeters / globe.getRadius();
public class MovementDecision
{
    private static final int VARIATION_DEGREES = 20;

    public static final Globe tempGlobe = new Earth();

    private static final List<MovementDirection> possibleDirections = populateList();

    private static List<MovementDirection> populateList() {
        List<MovementDirection> possibleDirections = new ArrayList<>();
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.NORTH,0));
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.NORTH_EAST,0.01,89.99));
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.NORTH_EAST,-359.99,-270.01));
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.EAST,90));
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.EAST,-270));
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.SOUTH_EAST,90.01,179.99));
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.SOUTH_EAST,-269.99,-180.01));
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.SOUTH,180));
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.SOUTH,-180));
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.SOUTH_WEST,180.01,269.99));
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.SOUTH_WEST,-179.99,-90.01));
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.WEST,270));
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.WEST,-90));
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.NORTH_WEST,270.01,359.99));
        possibleDirections.add(new MovementDirection(MovementDirection.DirectionType.NORTH_WEST,-89.99,-0.01));
        return possibleDirections;
    }

    public static MovementDirection findElement(MovementDirection.DirectionType directionType) {
        return possibleDirections.stream().filter(e -> e.direction == directionType).findFirst().orElse(null);
    }

    public static MovementDirection findElement(double bearingInDegrees) {
        return possibleDirections.stream().filter(e -> e.minimum <= bearingInDegrees && bearingInDegrees <= e.maximum).findFirst().orElse(null);
    }

    /**
     *
     * @param center starting position
     * @param bearingInDegrees angle of which we are travelling towards or away to our intended target
     * @param distanceInMeters how much we want to travel
     * @param angleVariation  If true, then we return a value of the bearing degrees values +/- the variation.
     * @param distanceVariation If true, then the distance will be randomized.
     * @param moveTowards flip the bearing degrees value if we're moving away from a target.
     * @return a new position based on the parameters.
     */
    public static Position moveAtAngle(Position center, double bearingInDegrees, double distanceInMeters, boolean angleVariation, boolean distanceVariation, boolean moveTowards) {
        //flip the bearing degrees value if we're moving away from a target.
        if(!moveTowards) {
            bearingInDegrees = (bearingInDegrees + 180) % 360;
        }

        if(angleVariation) {
            double old = bearingInDegrees;
            bearingInDegrees = ThreadLocalRandom.current().nextDouble(old-VARIATION_DEGREES, old+VARIATION_DEGREES+0.01);
        }

        if(distanceVariation) {
            distanceInMeters *= Math.random();
        }


        Angle bearing = Angle.fromDegrees(bearingInDegrees);
        double distanceRadians = distanceInMeters/tempGlobe.getRadius();
        LatLon newLatLon =  LatLon.greatCircleEndPosition(center,bearing.radians,distanceRadians);
        return new Position(newLatLon,0);
    }

    public static Position moveInSpecificDirection(Position current, MovementDirection.DirectionType direction, double distanceInMeters, boolean angleVariation, boolean distanceVariation, boolean moveTowards) {
        MovementDirection movementDirection = findElement(direction);
        double bearingInDegrees = ThreadLocalRandom.current().nextDouble(movementDirection.minimum, movementDirection.maximum+0.01);
        return moveAtAngle(current, bearingInDegrees, distanceInMeters, angleVariation, distanceVariation, moveTowards);
    }

}

