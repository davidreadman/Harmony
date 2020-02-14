import java.io.IOException;
import java.util.ArrayList;

/*
This project aims to provide a structure for the creation of multiple scenarios,
 utilising external software to analyse current 'node' locations and return a set of metrics
 that will allow for
 a) a score based on these metrics or internal analysis
 b) options for the next location of each node
 c) a record of the node positions, the metrics returned and the decision made for the next location

 The records made should provide the basis for an analysis for machine learning which can then be
 used back into the program to refine the decision making process.

 The primary interfaces in this project are:
 a)initialisation file
 	- this file will contain the initial starting locations of the nodes and later extend
 	to providing the basic plan for the nodes.
 	- initial design is to use the java .properties file
 b)data to external analysis software
 	- this will be implemented as DDS as this is the middleware being used for the Generic Vehicle
 	Architecture (GVA)
 	- target analysis software (SMARTNet) has an existing DDS implementation
 c)data from external analysis software
 	- Decisions made as per b
 d)recorded data
 	- this will be recorded as a comma separated variable (CSV) file to allow for simple import
 	and analysis by as many programs as possible.
 	- CSV chosen over XML due to time sequenced events
 */
public class Harmony
{
    public static void main(String[] args) throws IOException
    {
        /*
        exploring the possibility of running a batch file at program start to enable
        opensplice environment variable setup

        Runtime.
                getRuntime().
                exec("echo \"test\"");

         */
        //Create initialisations
		/*
		ParseProperties class reads in values from .properties file for nodal information
		 */
        //set up data publisher (could also be done within the subclass guiactions)
        HarmonyDataPublisher publishdata = new HarmonyDataPublisher();

        //Read in initialisation file and load into the NodeData array
        ArrayList<NodeData> allNodes = ParseProperties.parsePlan();
        SimulationSettings simulationSettings = ParseProperties.parseConfig();
        DecisionEngine decisionEngine = new DecisionEngine();
        decisionEngine.setUpNodes(allNodes);
        new JFrameGuiActions( publishdata, decisionEngine, simulationSettings).setVisible(true);

    }

}
