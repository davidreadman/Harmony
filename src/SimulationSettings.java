import java.time.Duration;

public class SimulationSettings {
    public boolean startSimulation;
    public String durationString;
    public Duration simulatonDuration;
    public boolean enableLogging;
    public boolean show2525B;
    public boolean showNodeLocations;
    public boolean publishDDSMessages;
    public boolean sendNodeInformation;
    public boolean sendMetrics;

    public SimulationSettings() {
        startSimulation = true;
        durationString = "";
        simulatonDuration = null;
        enableLogging = false;
        show2525B = false;
        showNodeLocations = false;
        publishDDSMessages = false;
        sendNodeInformation = false;
        sendMetrics = false;
    }

    public void setUpDuration(String durationString) {
        String[] arr = durationString.split(" ");
        if(arr.length == 2) {
            Integer value = Integer.parseInt(arr[0]);
            String unit = arr[1].toLowerCase();
            if(unit.startsWith("d") || unit.startsWith("day")){
                simulatonDuration = Duration.ofDays(value);
                unit = "day(s)";
            }
            else if(unit.startsWith("h") || unit.startsWith("hour") || unit.startsWith("hr")) {
                simulatonDuration = Duration.ofHours(value);
                unit = "hour(s)";
            }
            else if(unit.startsWith("m") || unit.startsWith("min")) {
                simulatonDuration = Duration.ofMinutes(value);
                unit = "minute(s)";
            }
            else if(unit.startsWith("s") || unit.startsWith("sec")) {
                simulatonDuration = Duration.ofSeconds(value);
                unit = "second(s)";
            }
            this.durationString = String.format("%d %s", value, unit);
        }
    }
}
