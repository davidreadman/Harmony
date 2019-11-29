/**
 * This class stores speed values in m/s, kmh and mph
 * Rather than convert the speeds from one unit to another as a function,
 * we do this all in a class in the following:
 * 1. If the value is in m/s, we get a value in kmh and mph
 * 2. If the value is in kmh, we get a value in m/s and mph
 * 3. If the value is in mph, we get a value in m/s and kmh
 */
public class Speed {

    private double metresPerSecond;
    private double kilometersPerHour;
    private double milesPerHour;

    //used to convert from km/h to m/s and vice versa;
    private static final double KMH_TO_MS = 3.6;
    private static final double MILES_TO_KM = 1.60944;

    public Speed(double metresPerSecond, double kilometersPerHour, double milesPerHour) {
        this.metresPerSecond = metresPerSecond;
        this.kilometersPerHour = kilometersPerHour;
        this.milesPerHour = milesPerHour;
    }

    public static Speed fromMetresPerSecond (double metresPerSecond) {
        return new Speed(metresPerSecond, metresPerSecond * KMH_TO_MS, metresPerSecond * (KMH_TO_MS/MILES_TO_KM));
    }

    public static Speed fromKilometresPerHour (double kilometersPerHour) {
        return new Speed(kilometersPerHour/ KMH_TO_MS, kilometersPerHour, kilometersPerHour/MILES_TO_KM);
    }

    public static Speed fromMilesPerHour(double milesPerHour) {
        return new Speed(milesPerHour * (MILES_TO_KM/KMH_TO_MS), milesPerHour*MILES_TO_KM, milesPerHour);
    }

    public double getMetresPerSecond() {
        return metresPerSecond;
    }

    public double getKilometersPerHour() {
        return kilometersPerHour;
    }

    public double getMilesPerHour() { return milesPerHour; }
}
