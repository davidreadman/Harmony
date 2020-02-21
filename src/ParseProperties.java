import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class ParseProperties
{
	/**
	 * Call parsePlan with the inbuilt configuration file
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<NodeData> parsePlan() throws IOException {
		return parsePlan("plan.properties");
	}

	/**
	 * Generate a list of nodes based on any configuration file.
	 * @param propFileName
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<NodeData> parsePlan(String propFileName) throws IOException
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
				throw new FileNotFoundException("property file '" +propFileName + " not found");

			}
			//get the number of nodes defined in the properties file
			int NumberOfNodes = Integer.parseInt(prop.getProperty("Nodes"));
			//create an array to hold these nodes
			NodeData[] theseNodes = new NodeData[NumberOfNodes];
			//loop through the number of nodes to fill array
			//assume #nodes > 0

			Map<String, String> nodeToCommander = new HashMap<>();
			for (int i = 0 ; i<NumberOfNodes ;i++)
			{
				theseNodes[i] = new NodeData();
				theseNodes[i].nodeUUID = prop.getProperty("Node"+(i+1)+"UUID");
				double Lat = Double.parseDouble(prop.getProperty("Node"+(i+1)+"Lat"));
				double Lon = Double.parseDouble(prop.getProperty("Node"+(i+1)+"Lon"));
				theseNodes[i].currentLocation= new Position(LatLon.fromDegrees(Lat,Lon), 0);
				theseNodes[i].nodeType = prop.getProperty("Node"+(i+1)+"Type");
				theseNodes[i].symbol = prop.getProperty("Node"+(i+1)+"2525B");
				theseNodes[i].operationalSpeedInKmH =  Double.parseDouble(prop.getProperty("Node"+(i+1)+"OperationalSpeed"));
				theseNodes[i].minOperationalSpeedInKmH = theseNodes[i].operationalSpeedInKmH;
				theseNodes[i].maxOperationalSpeedInKmH = Double.parseDouble(prop.getProperty("Node"+(i+1)+"MaxSpeed"));
				if(prop.containsKey("Node"+(i+1)+"StrategyCSV")) {
					theseNodes[i].strategies = Arrays.asList(prop.getProperty("Node"+(i+1)+"StrategyCSV").split(","));
				}
				if(prop.containsKey("Node"+(i+1)+"CommanderUUID")) {
					nodeToCommander.put(theseNodes[i].nodeUUID, prop.getProperty("Node"+(i+1)+"CommanderUUID"));
				}
			}
			ArrayList<NodeData> allNodes = new ArrayList<>(Arrays.asList(theseNodes));
			for(NodeData currentNode: allNodes) {
				if(nodeToCommander.containsKey(currentNode.nodeUUID)){
					for(NodeData other: allNodes) {
						if(other.nodeUUID.equals(nodeToCommander.get(currentNode.nodeUUID)))
						{
							currentNode.myCommander = other;
							break;
						}
					}
				}
				else
					currentNode.myCommander = null;
			}

			return allNodes;
		}
		catch (Exception e)
		{
			System.out.println("Exception: " + e);
			//because it has to have a return
			NodeData[] tempArray = new NodeData[1];
			
			return new ArrayList<>(Arrays.asList(tempArray));
		
		}
		finally
		{
			inputStream.close();
			
		}
		
		
	}

	public static SimulationSettings parseConfig() throws IOException {
		return parseConfig("config.properties");
	}

	public static SimulationSettings parseConfig(String propFileName) throws IOException {
		SimulationSettings simulationSettings = new SimulationSettings();

		InputStream inputStream = null;
		try {
			Properties prop = new Properties();
			//the following line is to read from a properties file embedded in the jar
			//inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			inputStream = new FileInputStream(propFileName);

			if (inputStream !=null){
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" +propFileName + " not found");

			}

			if(prop.containsKey("startSimulation")) {
				simulationSettings.startSimulation = prop.getProperty("startSimulation").toLowerCase().equals("yes");
			}
			if(prop.containsKey("simulationDuration")) {
				simulationSettings.setUpDuration(prop.getProperty("simulationDuration"));
			}
			if(prop.containsKey("enableLogging")) {
				simulationSettings.enableLogging = prop.getProperty("enableLogging").toLowerCase().equals("yes");
			}
			if(prop.containsKey("show2525B")) {
				simulationSettings.show2525B = prop.getProperty("show2525B").toLowerCase().equals("yes");
			}
			if(prop.containsKey("showNodeLocations")) {
				simulationSettings.showNodeLocations = prop.getProperty("showNodeLocations").toLowerCase().equals("yes");
			}
			if(prop.containsKey("publishDDSMessages")) {
				simulationSettings.publishDDSMessages = prop.getProperty("publishDDSMessages").toLowerCase().equals("yes");
			}
			if(prop.containsKey("sendNodeInformation")) {
				simulationSettings.sendNodeInformation = prop.getProperty("sendNodeInformation").toLowerCase().equals("yes");
			}
			if(prop.containsKey("sendMetrics")) {
				simulationSettings.sendMetrics = prop.getProperty("sendMetrics").toLowerCase().equals("yes");
			}
			if(prop.containsKey("debugMovementDecision")) {
				simulationSettings.debugMovementDecision = prop.getProperty("debugMovementDecision").toLowerCase().equals("yes");
			}
			if(prop.containsKey("debugTacticalSymbolGeneration")) {
				simulationSettings.debugTacticalSymbolGeneration = prop.getProperty("debugTacticalSymbolGeneration").toLowerCase().equals("yes");
			}
			if(prop.containsKey("debugDataListener")) {
				simulationSettings.debugDataListener = prop.getProperty("debugDataListener").toLowerCase().equals("yes");
			}
			if(prop.containsKey("numOfSimulationsToRun")) {
				simulationSettings.numOfSimulationsToRun = Integer.parseInt(prop.getProperty("numOfSimulationsToRun"));
			}
		}
		catch(Exception e) {
			System.out.println("Exception: " + e);
		}
		finally {
			if(inputStream != null) {
				inputStream.close();
			}
		}
		return simulationSettings;
	}

}
