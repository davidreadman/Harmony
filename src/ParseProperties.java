import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;

import javax.xml.soap.Node;
import java.io.FileInputStream;
import java.util.Properties;
import java.lang.Double;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;


public class ParseProperties
{

	public ParseProperties()
	{
		// TODO Auto-generated constructor stub
	}
	public NodeData[] parseConfig() throws IOException
	{
		
		InputStream inputStream = null;
		double lat;
		double lon;
		try {
			Properties prop = new Properties();

			String propFileName = "config.properties";
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
			//create an array to hold these nodes
			NodeData[] theseNodes = new NodeData[NumberOfNodes];
			//loop through the number of nodes to fill array
			//assume #nodes > 0

			for (int i = 0 ; i<NumberOfNodes ;i++)
			{
				System.out.println(i);
				theseNodes[i] = new NodeData();
				theseNodes[i].NodeUUID = prop.getProperty("Node"+(i+1)+"UUID");
				theseNodes[i].Lat = Double.parseDouble(prop.getProperty("Node"+(i+1)+"Lat"));
				theseNodes[i].Lon = Double.parseDouble(prop.getProperty("Node"+(i+1)+"Lon"));
				theseNodes[i].currentLocation= new Position(LatLon.fromDegrees(theseNodes[i].Lat,theseNodes[i].Lon), 0);
				NodeData.NodeType nodeType;
				String propertyIFF = prop.getProperty("Node"+(i+1)+"IFF");
				switch(propertyIFF) {
					case "B":
						nodeType = NodeData.NodeType.FRIEND;
						break;
					case "R":
						nodeType = NodeData.NodeType.HOSTILE;
						break;
					case "G":
						nodeType = NodeData.NodeType.NEUTRAL;
						break;
					default:
						nodeType = NodeData.NodeType.NULL;
						break;
				}
				theseNodes[i].nodeType = nodeType;
				theseNodes[i].symbol = prop.getProperty("Node"+(i+1)+"2525B");
				theseNodes[i].operationalSpeed = Speed.fromKilometresPerHour(Double.parseDouble(prop.getProperty("Node"+(i+1)+"OperationalSpeed")));
				theseNodes[i].maximumSpeed = Speed.fromKilometresPerHour(Double.parseDouble(prop.getProperty("Node"+(i+1)+"MaxSpeed")));
				theseNodes[i].detectionRadius = Distance.fromKilometres(Double.parseDouble(prop.getProperty("Node"+(i+1)+"RadiusOfDetectionInKm")));
			}
			
			
			System.out.println(theseNodes[0].NodeUUID);
			
			return theseNodes;	
		}
		catch (Exception e)
		{
			System.out.println("Exception: " + e);
			//because it has to have a return
			NodeData[] tempArray = new NodeData[1];
			
			return tempArray;
		
		}
		finally
		{
			inputStream.close();
			
		}
		
		
	}

}
