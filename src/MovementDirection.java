public class MovementDirection {


    public enum DirectionType {
        NORTH,
        SOUTH,
        EAST,
        WEST,
        NORTH_EAST,
        NORTH_WEST,
        SOUTH_EAST,
        SOUTH_WEST
    }

    DirectionType direction;
    double minimum;
    double maximum;

    MovementDirection(DirectionType direction, double minimum, double maximum) {
        this.direction = direction;
        this.maximum = maximum;
        this.minimum = minimum;
    }

    MovementDirection(DirectionType direction, double value) {
        this(direction,value,value);
    }
}
