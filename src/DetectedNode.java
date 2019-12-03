import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;

/**
 * This class stores information about the node detected
 * The fields are the following
 * 1. That node that was detected.
 * 2. The angle from the detected node
 * 3. The distance from the detected node.
 * 4. The node that reported this information.
 * This information can be used to either move towards/away from the detected node.
 */
public class DetectedNode
{

    /* what node was detected */
    private final String nodeUUID;
    /* current location of that node */
    private final Position currentLocation;
    /*direction of node travel*/
    private double directionOfTravel;
    /*bearing from the node holding this item to the node in this item */
    private double bearingInDegreesToTarget;
    private double distanceToTargetInMeters;
    /* who reported it (discussion, who reported it first? what about updates? who is tracking? multiple reports? */
    /*initially considering this *just this report for this arraylist*/
    private final String detectedByUUID;
    /* what it is believed to be */
    private final String nodeType;
    /* level of confidence in that assessment */
    /*https://www.dst.defence.gov.au/sites/default/files/publications/documents/DST-Group-TR-3325_0.pdf */
    private final int levelOfConfidence;
    /* friend, hostile or neutral (to be expanded later */
    private final String nodeIFF;
    public DetectedNode(String nodeUUID, Position currentLocation, double directionOfTravel, double distanceToTargetInMeters,
                        double bearingInDegreesToTarget, String detectedByUUID,
                        String nodeType, int levelOfConfidence, String nodeIFF)
    {
        this.nodeUUID = nodeUUID;
        this.currentLocation = currentLocation;
        this.directionOfTravel = directionOfTravel;
        this.bearingInDegreesToTarget = bearingInDegreesToTarget;
        this.detectedByUUID = detectedByUUID;
        this.nodeType = nodeType;
        this.levelOfConfidence = levelOfConfidence;
        this.nodeIFF = nodeIFF;
        this.distanceToTargetInMeters = distanceToTargetInMeters;

    }

    public String getDetectedByUUID()
    {
        return this.detectedByUUID;
    }

    public String getnodeUUID()
    {
        return this.nodeUUID;
    }

    public double getBearingInDegreesToTarget()
    {
        return this.bearingInDegreesToTarget;
    }

    public double getDistanceToTargetInMeters()
    {
        return this.distanceToTargetInMeters;
    }
}
