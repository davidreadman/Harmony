import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.LinkedList;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.coords.MGRSCoord;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.EarthFlat;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.Earth.MGRSGraticuleLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfaceCircle;
import gov.nasa.worldwind.symbology.BasicTacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.TacticalSymbol;
import gov.nasa.worldwind.symbology.TacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalSymbol;
import gov.nasa.worldwind.view.orbit.FlatOrbitView;
import gov.nasa.worldwindx.examples.util.HighlightController;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import javax.swing.*;

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
        NodeData[] blueNode = ParseProperties.parseConfig();

        //set up worldwind canvas
       // DisplayWW outputToDisplay = new DisplayWW(blueNode);
        //set up gui and all actions thereafter
        new JFrameGuiActions( publishdata, blueNode).setVisible(true);

        // Make Decisions
        //putting this within the guiactions initially
        //Update WorldWind
        //putting this within the guiactions
        //Record this time epoch
        //putting this within the guiactions

    }

}
