import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class StoreProperties
{
    public StoreProperties()
    {
        // TODO Auto-generated constructor stub
    }
    public void writeConfig(ArrayList<NodeData> nodeData)
    {
        //create a file
        String propFileName = "newconfig.properties";
        FileOutputStream outputStream;
        File file;
        String content = "Nodes = "+nodeData.size()+"\n\n";
        try
        {
            byte[] contentInBytes;
            file = new File (propFileName);
            outputStream = new FileOutputStream(file);

            if (!file.exists()) {
                file.createNewFile();
            }
            contentInBytes = content.getBytes();
            outputStream.write(contentInBytes);

            //pass through the nodes and create the properties
            for (int i = 0; i < nodeData.size(); i++)
            {

                String line1= "Node"+(i+1)+"UUID = " + nodeData.get(i).nodeUUID +"\n";
                //note: used this to remove degrees symbol present in the angle.degrees number
                int lengthOfString = nodeData.get(i).currentLocation.getLatitude().toString().length();
                String line2= "Node"+(i+1)+"Lat = " + nodeData.get(i).currentLocation.getLatitude().toString().substring(0,lengthOfString-1)+"\n";
                lengthOfString = nodeData.get(i).currentLocation.getLongitude().toString().length();
                String line3= "Node"+(i+1)+"Lon = " + nodeData.get(i).currentLocation.getLongitude().toString().substring(0,lengthOfString-1)+"\n";
              //  Node1Type = Armoured Wheeled Vehicle
                String line4= "Node"+(i+1)+"Type = " + nodeData.get(i).nodeType+"\n";
             //   Node1IFF = B
                String line6= "Node"+(i+1)+"2525B = " + nodeData.get(i).symbol+"\n";
            //            Node1OperationalSpeed = 40
                String line7= "Node"+(i+1)+"OperationalSpeed = " + nodeData.get(i).operationalSpeedInKmH+"\n";
            //    Node1MaxSpeed = 100
                String line8= "Node"+(i+1)+"MaxSpeed = " + nodeData.get(i).maxOperationalSpeedInKmH +"\n";
            //    Node1RadiusOfDetectionInKm = 20

                content = (""+line1+line2+line3+line4+line6+line7+line8+"\n\n\n");
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
