public class Speed {

    private double metresPerSecond;
    private double kilometersPerHour;

    //used to convert from km/h to m/s and vice versa;
    private static final double SPEED_CONVERSION = 3.6;

    public Speed(double metresPerSecond, double kilometersPerHour) {
        this.metresPerSecond = metresPerSecond;
        this.kilometersPerHour = kilometersPerHour;
    }

    public static Speed fromMetresPerSecond (double metresPerSecond) {
        return new Speed(metresPerSecond, metresPerSecond *SPEED_CONVERSION);
    }

    public static Speed fromKilometresPerHour (double kilometersPerHour) {
        return new Speed(kilometersPerHour/SPEED_CONVERSION, kilometersPerHour);
    }

    public double calculateDistanceInMetresWithTimeInSeconds(int seconds) {
        return metresPerSecond * seconds;
    }

    public double calculateDistanceInKilometresWithTimeInHours(double hours) {
        return kilometersPerHour * hours;
    }

    public double getMetresPerSecond() {
        return metresPerSecond;
    }

    public double getKilometersPerHour() {
        return kilometersPerHour;
    }
}
