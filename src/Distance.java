/**
 * This class is to store a distance value in miles, kilometres and metres.
 * Rather than say convert a value from Miles to Kilometres as a function,
 * we just need to do either of the following:
 * 1. If we feed a value in miles, we get a value in both kilometres and metres
 * 2. If we feed a value in kilometres, we get a value in both miles and metres
 * 3. If we feed a value in metres, we get a value in both miles and kilometres.
 */
public class Distance {

    private static final double MILES_TO_KM = 1.60944;
    public static final int KM_TO_METRES = 1000;

    private double miles, kilometres, metres;

    public Distance(double miles, double kilometres, double metres) {
        this.metres = metres;
        this.kilometres = kilometres;
        this.miles = miles;
    }

    public static Distance fromMiles(double miles) {
        return new Distance(miles, miles * MILES_TO_KM, miles * (MILES_TO_KM/ KM_TO_METRES));
    }

    public static Distance fromKilometres(double kilometres) {
        return new Distance(kilometres/MILES_TO_KM, kilometres, kilometres/KM_TO_METRES);
    }

    public static Distance fromMetres(double metres) {
        return new Distance(metres * (MILES_TO_KM/KM_TO_METRES), metres/KM_TO_METRES, metres);
    }

    public double getDistanceInMetres() {
        return metres;
    }

    public double getDistanceInKilometres() {
        return kilometres;
    }

    public double getDistanceInMiles() {
        return miles;
    }

    /**
     * This function will be used to compare two distance objects
     * Main case for using this function will be to compare if the distance between two nodes is within
     * a specific threshold.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Distance)) {
            return false;
        }
        Distance other = (Distance) obj;

        return (other.miles == this.miles || other.kilometres == this.kilometres || other.metres == this.metres);
    }

    public int compareTo(Object obj) {
        if(!(obj instanceof Distance)) {
            return -1;
        }
        Distance other = (Distance) obj;
        if(this.equals(other)) {
            return 0;
        }
        else {
            if(this.miles > other.miles || this.kilometres > other.kilometres || this.metres > other.metres) {
                return 1;
            }
            else {
                return -1;
            }
        }

    }
}
