import gov.nasa.worldwind.geom.Position;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HoconFileGenerator {

    private static String getPositionRecipientsForNode(List<NodeData> nodes, String nodeUUID) {
        List<String> others = new ArrayList<>();
        for(NodeData node: nodes){
            if(!node.nodeUUID.equals(nodeUUID)) {
                others.add(node.nodeUUID);
            }
        }
        return String.join(",", others);
    }

    private static void createNetworkAndWriteToFile(List<NodeData> nodes, String networkName, FileWriter outfile) throws IOException {
        int networkID = 0;
        switch(networkName) {
            case "friends":
                networkID = 1;
                break;
            case "hostiles":
                networkID = 2;
                break;
            case "neutrals":
                networkID = 3;
                break;
            default:
                break;
        }
        outfile.write(String.format("\t\t\t\"%s\": {\n", networkName));
        outfile.write("\t\t\t\ttype: inmemory\n\t\t\t\tradioConfig.maxPacketBytes: 256\n\t\t\t\tmulticastSubscriptions: []\n\t\t\t\tnetmask: \"255.255.255.0\"\n");
        outfile.write("\t\t\t\tinterfaces: {\n");
        for(int i=0;i<nodes.size();i++) {
            NodeData node = nodes.get(i);
            outfile.write(String.format("\t\t\t\t\t\"%s\" { ipAddress: \"10.0.%d.%d\", macAddress: \"%d%d\"}\n", node.nodeUUID, networkID, i+1, networkID, i+1));
        }
        outfile.write("\t\t\t\t}\n");
        outfile.write("\t\t\t\trouteSource: { type: static, routes: [] }\n");
        outfile.write("\t\t\t}\n");
    }

    public static void writeToHocon(ArrayList<NodeData> nodes, Map<Integer, Map<String, Position>> waypoints, int currentMovementCounter, long maxMovementCounter) {
        try {
            List<String> nodeUUIDs = new ArrayList<>();
            List<String> entities = new ArrayList<>();
            List<NodeData> friends = new ArrayList<>();
            List<NodeData> hostiles = new ArrayList<>();
            List<NodeData> neutrals = new ArrayList<>();
            for(NodeData node: nodes) {
                switch (node.symbol.charAt(1)) {
                    case 'F':
                        friends.add(node);
                        break;
                    case 'H':
                        hostiles.add(node);
                        break;
                    case 'N':
                        neutrals.add(node);
                        break;
                    default:
                        break;
                }
            }
            for(int i=0; i<nodes.size();i++) {
                NodeData node = nodes.get(i);
                nodeUUIDs.add(String.format("\"%s\"", node.nodeUUID));
                String positionRecipients = "";
                switch (node.symbol.charAt(1)) {
                    case 'F':
                        positionRecipients = getPositionRecipientsForNode(friends, node.nodeUUID);
                        break;
                    case 'H':
                        positionRecipients = getPositionRecipientsForNode(hostiles, node.nodeUUID);
                        break;
                    case 'N':
                        positionRecipients = getPositionRecipientsForNode(neutrals, node.nodeUUID);
                        break;
                    default:
                        break;
                }
                entities.add(String.format("\"%s\" {urn: %d, positionRecipients: [%s], symbol: \"%s\"}", node.nodeUUID, i+1, positionRecipients, node.symbol));
            }
            LocalDateTime date = LocalDateTime.now();
            //format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmm");
            String text = date.format(formatter);
            LocalDateTime parsedDate = LocalDateTime.parse(text, formatter);
            //remove annoying extra stuff that localtimedate sticks in
            String filePath = parsedDate.toString().replace("-", "")
                    .replace("T", "").replace(":", "");
            filePath = String.format("simulation.inmemory.harmony-%s.conf", filePath);
            File newFile = new File(filePath);
            FileWriter outfile = new FileWriter(newFile);
            outfile.write("include \"shared/common.local.conf\"\n");
            outfile.write("simulation: {\n\trandomSeed: 1234\n");
            outfile.write(String.format("\thosts {\n\t\tlocalhost {\n\t\t\tentities: [%s]\n\t\t}\n\t}\n", String.join(",", nodeUUIDs)));
            outfile.write("\ttransform.type = Simple\n\tdissemination {\n\t\ttype: Static\n\t\tmessageStore { expiryIntervalSeconds: 60 }\n\t\trules { Generic { priority: 10, expiryTime: 1 } }\n\t}\n");
            outfile.write(String.format("\tscenario {\n\t\tid: harmony\n\t\tname: harmony\n\t\tstartTime:\"NOW\"\n\t\tduration:\"%d seconds\"\n\t\tareaOfInterest {}\n\t\tmulticasts {}\n\t\tentities {\n", maxMovementCounter > 0 ? maxMovementCounter : currentMovementCounter));
            for(String entity: entities) {
                outfile.write(String.format("\t\t\t%s\n", entity));
            }
            outfile.write("\t\t}\n"); //complete the entities block inside the scenario block
            outfile.write("\t\tnetworks: {\n");
            //Create a network for friend only nodes if there are any
            if(!friends.isEmpty()) {
                createNetworkAndWriteToFile(friends, "friends", outfile);
            }
            if(!hostiles.isEmpty()) {
                createNetworkAndWriteToFile(hostiles, "hostiles", outfile);
            }
            if(!neutrals.isEmpty()) {
                createNetworkAndWriteToFile(neutrals, "neutrals", outfile);
            }
            outfile.write("\t\t}\n"); //complete the networks block inside the scenario block
            outfile.write("\t}\n"); //complete the scenario block
            outfile.write("\tmasterEventList: {type: inline, events: []}\n");
            outfile.write("\tentityMovement: {\n\t\tsource {\n\t\ttype:waypoints\n\t\tinterval: 1 seconds\n");
            outfile.write("\t\twaypoints {\n");

            for(NodeData node: nodes) {
                Map<Integer, Map<String, Position>> filtered = waypoints.entrySet().stream().filter(a -> a.getValue().containsKey(node.nodeUUID)).collect(Collectors.toMap(e->e.getKey(), e->e.getValue()));
                List<String> waypointStrings = new ArrayList<>();
                for(Map.Entry<Integer,Map<String,Position>> entry : filtered.entrySet()){
                    double[] degreesArray = entry.getValue().get(node.nodeUUID).asDegreesArray();
                    waypointStrings.add(String.format("{at: %d seconds, position: {lat: %f, lon:%f}", entry.getKey(),degreesArray[0],degreesArray[1]));
                }
                outfile.write(String.format("\t\t\t%s: [%s]\n", node.nodeUUID, String.join(",",waypointStrings)));
            }
            outfile.write("\t\t}\n"); //complete the waypoints block inside the entityMovement block
            outfile.write("\t}\n"); //complete the entityMovement block
            outfile.write("}\n"); //complete the simulation block
            outfile.flush();
            outfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
