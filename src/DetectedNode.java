import gov.nasa.worldwind.geom.Angle;

public class DetectedNode {

    private final NodeData nodeDetected;
    private final MovementDirection.DirectionType direction;
    private final Angle azimuthAngle;
    private final double distanceFromNode;

    public DetectedNode(NodeData nodeDetected, MovementDirection.DirectionType direction, Angle azimuthAngle, double distanceFromNode) {
        this.nodeDetected = nodeDetected;
        this.direction = direction;
        this.azimuthAngle = azimuthAngle;
        this.distanceFromNode = distanceFromNode;
    }

    public NodeData getNodeDetected() {
        return nodeDetected;
    }

    public MovementDirection.DirectionType getDirection() {
        return direction;
    }

    public Angle getAzimuthAngle() {
        return azimuthAngle;
    }

    public double getDistanceFromNode() {
        return distanceFromNode;
    }
}
