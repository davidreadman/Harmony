import gov.nasa.worldwind.geom.Position;

public class PlaceOfInterest {

    //e.g. Road, Terrain, Safe_Zone
    String name;

    Position location;

    //How quickly can the nodes travel in this location (if it's a road/terrain)
    //Otherwise it will be 0 km/h
    double maxSpeedInKmH;
}
