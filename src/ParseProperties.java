import gov.nasa.worldwind.geom.Position;

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
			
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			
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
			theseNodes[i].currentLocation=Position.fromDegrees(theseNodes[i].Lat,theseNodes[i].Lon);
			theseNodes[i].symbol = prop.getProperty("Node"+(i+1)+"2525B");
				System.out.println("symbol: " +theseNodes[i].symbol);
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
