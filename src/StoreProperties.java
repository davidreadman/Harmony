import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StoreProperties
{
    public StoreProperties()
    {
        // TODO Auto-generated constructor stub
    }
    public void writeConfig(NodeData[] nodeData)
    {
        //create a file
        String propFileName = "newconfig1.properties";
        FileOutputStream outputStream;
        File file;
        String content = "Nodes = "+nodeData.length+"\n\n";
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
            for (int i = 0; i < nodeData.length; i++)
            {

                String line1= "Node"+(i+1)+"UUID = " + nodeData[i].NodeUUID+"\n";
               // Node1Lat = -22.76840992443997
                String line2= "Node"+(i+1)+"Lat = " + nodeData[i].currentLocation.getLatitude()+"\n";
             //   Node1Lon = 150.36795105104991
                String line3= "Node"+(i+1)+"Lon = " + nodeData[i].currentLocation.getLongitude()+"\n";
              //  Node1Type = Armoured Wheeled Vehicle
                String line4= "Node"+(i+1)+"Type = " + nodeData[i].nodeType+"\n";
             //   Node1IFF = B
                String line5= "Node"+(i+1)+"IFF = " + nodeData[i].NodeUUID+"\n";
             //   Node12525B = SFGPUCRVA-bf---
                String line6= "Node"+(i+1)+"2525B = " + nodeData[i].NodeUUID+"\n";
            //            Node1OperationalSpeed = 40
                String line7= "Node"+(i+1)+"OperationalSpeed = " + nodeData[i].NodeUUID+"\n";
            //    Node1MaxSpeed = 100
                String line8= "Node"+(i+1)+"MaxSpeed = " + nodeData[i].NodeUUID+"\n";
            //    Node1RadiusOfDetectionInKm = 20
                String line9= "Node"+(i+1)+"RadiusOfDetectionInKm = " + nodeData[i].NodeUUID+"\n";

                content = (""+line1);
                contentInBytes = content.getBytes();
                outputStream.write(contentInBytes);

            }

            // get the content in bytes

            outputStream.flush();
            outputStream.close();

            System.out.println("Done");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        //close the file



    }
}
