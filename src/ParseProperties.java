import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;


public class ParseProperties
{
	/**
	 * Call parseConfig with the inbuilt configuration file
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<NodeData> parseConfig() throws IOException {
		return parseConfig("config.properties");
	}

	/**
	 * Generate a list of nodes based on any configuration file.
	 * @param propFileName
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<NodeData> parseConfig(String propFileName) throws IOException
	{
		InputStream inputStream = null;
		try {
			Properties prop = new Properties();
			//the following line is to read from a properties file embedded in the jar
			//inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			inputStream = new FileInputStream(propFileName);

			if (inputStream !=null){
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" +propFileName + " config.properties not found");
				
			}
			Date time = new Date (System.currentTimeMillis());
			//get the number of nodes defined in the properties file
			int NumberOfNodes = Integer.parseInt(prop.getProperty("Nodes"));
			System.out.println("number of nodes: " + NumberOfNodes);
			//create an array to hold these nodes
			NodeData[] theseNodes = new NodeData[NumberOfNodes];
			//loop through the number of nodes to fill array
			//assume #nodes > 0

			for (int i = 0 ; i<NumberOfNodes ;i++)
			{
				theseNodes[i] = new NodeData();
				theseNodes[i].NodeUUID = prop.getProperty("Node"+(i+1)+"UUID");
				double Lat = Double.parseDouble(prop.getProperty("Node"+(i+1)+"Lat"));
				double Lon = Double.parseDouble(prop.getProperty("Node"+(i+1)+"Lon"));
				theseNodes[i].currentLocation= new Position(LatLon.fromDegrees(Lat,Lon), 0);
				theseNodes[i].nodeIFF = prop.getProperty("Node"+(i+1)+"IFF");
				theseNodes[i].nodeType = prop.getProperty("Node"+(i+1)+"Type");
				theseNodes[i].symbol = prop.getProperty("Node"+(i+1)+"2525B");
				theseNodes[i].operationalSpeedInKmH =  Double.parseDouble(prop.getProperty("Node"+(i+1)+"OperationalSpeed"));
				theseNodes[i].maximumSpeedInKmH = Double.parseDouble(prop.getProperty("Node"+(i+1)+"MaxSpeed"));
				theseNodes[i].detectionRadiusInKm = Double.parseDouble(prop.getProperty("Node"+(i+1)+"RadiusOfDetectionInKm"));
			}
			System.out.println(theseNodes[0].NodeUUID);
			return new ArrayList<>(Arrays.asList(theseNodes));
		}
		catch (Exception e)
		{
			System.out.println("Exception: " + e);
			//because it has to have a return
			NodeData[] tempArray = new NodeData[1];
			
			return new ArrayList<NodeData>(Arrays.asList(tempArray));
		
		}
		finally
		{
			inputStream.close();
			
		}
		
		
	}

}
