import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreProperties
{
    public static void writeConfig(ArrayList<NodeData> nodes)
    {
        //create a file
        String propFileName = "newconfig.properties";
        FileOutputStream outputStream;
        File file;
        try
        {
            byte[] contentInBytes;
            file = new File (propFileName);
            outputStream = new FileOutputStream(file);

            if (!file.exists()) {
                file.createNewFile();
            }
            String content = "Nodes = "+nodes.size()+"\n\n";
            contentInBytes = content.getBytes();
            outputStream.write(contentInBytes);

            //pass through the nodes and create the properties
            for (int i = 0; i < nodes.size(); i++)
            {
                List<String> nodeDetails = new ArrayList<>();
                double[] currentLocationDegreesArray = nodes.get(i).currentLocation.asDegreesArray();
                nodeDetails.add(String.format("Node%dUUID = %s\n",i+1,nodes.get(i).nodeUUID));
                nodeDetails.add(String.format("Node%dLat = %f\n",i+1,currentLocationDegreesArray[0]));
                nodeDetails.add(String.format("Node%dLon = %f\n",i+1,currentLocationDegreesArray[1]));
                nodeDetails.add(String.format("Node%d2525B = %s\n",i+1,nodes.get(i).symbol));
                nodeDetails.add(String.format("Node%dMaxSpeed = %f\n",i+1,nodes.get(i).maxOperationalSpeedInKmH));
                nodeDetails.add(String.format("Node%dStrategyCSV = %s\n",i+1,String.join(",",nodes.get(i).strategies)));
                if(nodes.get(i).myCommander != null) {
                    nodeDetails.add(String.format("Node%dCommanderUUID = %s\n",i+1,nodes.get(i).myCommander.nodeUUID));
                }

                content = String.format("%s\n\n\n",String.join("", nodeDetails));
                contentInBytes = content.getBytes();
                outputStream.write(contentInBytes);

            }

            // get the content in bytes

            outputStream.flush();
            outputStream.close();

            System.out.println("Prop file stored");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        //close the file



    }
}
