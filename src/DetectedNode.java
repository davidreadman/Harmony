import gov.nasa.worldwind.geom.Position;

public class DetectedNode
{
    NodeData sourceNode;
    //Node that was detecetd by the source.
    NodeData focusNode;

    double distanceFromSourceToFocusInMeters;
    double angleFromSourceToFocusInDegrees;

    DetectedNode(NodeData source, NodeData focus) {
        sourceNode = source;
        focusNode = focus;
        distanceFromSourceToFocusInMeters = 0;
        angleFromSourceToFocusInDegrees = 0;
    }

}
