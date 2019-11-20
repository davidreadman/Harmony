import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;

//note: distanceInRadians = distanceInMeters / globe.getRadius();
public class MovementDecision
{

    DisplayWW movementDisplayWW;
    NodeData[] movementNodeData;
    //need a Globe to do positional calculations on great circles
    Globe tempGlobe;

    public MovementDecision(NodeData[] nodeData, DisplayWW displayWW)
    {
        movementDisplayWW = displayWW;
        movementNodeData = nodeData;
        tempGlobe = new Earth();

    }



    /* the following modules are designed for deciding the next node location */
    /*moveTowards moves a node towards a location with a given variation */
    public void moveTowardsWithVariation()
    {

    }
    /*moveTowards moves a node towards a location */
    public void moveTowards()
    {

    }
    /*moveTowards moves a node away from location with a given variation */
    public void moveAwayWithVariation()
    {

    }
    /*moveTowardsWithVariation moves a node away from a location */
    public void moveAway()
    {

    }
    //routine to create a random location based on a known location and a max distance in miles
    public LatLon randomLocation(LatLon centre, Double distanceInMeters)
    {

        //distance = angle in radians * globe radius in miles
        //miles to kms 1.60944kms to a mile
        //https://worldwind.arc.nasa.gov/java/latest/javadoc/gov/nasa/worldwind/geom/Angle.html
        //random angle
        Angle bearing = Angle.fromDegrees(Math.random()*360);
        distanceInMeters = distanceInMeters * Math.random();
        double distanceRadians = distanceInMeters/ tempGlobe.getRadius();
        LatLon newLatLon= LatLon.greatCircleEndPosition(centre,bearing.radians,distanceRadians);

        return newLatLon;
    }
    //routine to create a random northeasterlylocation based on a known location and a max distance in miles
    public LatLon randomNELocation(LatLon centre,Double distanceInMeters)
    {

        //distance = angle in radians * globe radius in miles
        //miles to kms 1.60944kms to a mile
        //https://worldwind.arc.nasa.gov/java/latest/javadoc/gov/nasa/worldwind/geom/Angle.html
        //random angle
        Angle bearing = Angle.fromDegrees(Math.random()*90);// 0 to 90 degrees
        distanceInMeters = distanceInMeters * Math.random();
        double distanceRadians = distanceInMeters/ tempGlobe.getRadius();
        LatLon newLatLon= LatLon.greatCircleEndPosition(centre,bearing.radians,distanceRadians);

        return newLatLon;
    }

}
