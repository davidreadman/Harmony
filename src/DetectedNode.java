import gov.nasa.worldwind.geom.Angle;

/**
 * This class stores information about the node detected
 * The fields are the following
 * 1. That node that was detected.
 * 2. The angle from the detected node
 * 3. The distance from the detected node.
 * 4. The node that reported this information.
 *  This information can be used to either move towards/away from the detected node.
 */
public class DetectedNode {

    private final NodeData reportingNode;
    private final NodeData nodeDetected;
    private final Angle azimuthAngle;
    private final double distanceFromNode;

    public DetectedNode(NodeData reportingNode, NodeData nodeDetected, Angle azimuthAngle, double distanceFromNode) {
        this.reportingNode = reportingNode;
        this.nodeDetected = nodeDetected;
        this.azimuthAngle = azimuthAngle;
        this.distanceFromNode = distanceFromNode;
    }

    public NodeData getReportingNode() {
        return reportingNode;
    }

    public NodeData getNodeDetected() {
        return nodeDetected;
    }

    public Angle getAzimuthAngle() {
        return azimuthAngle;
    }

    public double getDistanceFromNode() {
        return distanceFromNode;
    }
}
