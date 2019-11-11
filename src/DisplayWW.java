import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Collection;
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
import gov.nasa.worldwind.geom.Sector;
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
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwind.view.orbit.FlatOrbitView;
import gov.nasa.worldwindx.examples.KeepingObjectsInView;
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

class DisplayWW
{
    WorldWindowGLCanvas canvas;
    Position defaultPosition = Position.fromDegrees(-22.509187, 150.096047, 1);
    NodeData[] nodeData;

    public DisplayWW(NodeData[] nodeData)
    {
        //set up default configurations for Shoalwater bay
        configure();

        // create main object
        canvas = new WorldWindowGLCanvas();
        canvas.setModel(new BasicModel());

        // remove world map
        Layer worldMapLayer = canvas.getModel().getLayers().getLayerByName("World Map");
        canvas.getModel().getLayers().remove(worldMapLayer);
        //remove compass
        Layer compassLayer = canvas.getModel().getLayers().getLayerByName("Compass");
        canvas.getModel().getLayers().remove(compassLayer);

        //load array of nodes into worldwind as symbols to be displayed
        int NumberOfNodes = nodeData.length;
        System.out.println("number of nodes: " + nodeData.length);
        for (int i = 0; i < NumberOfNodes; i++)
        {
            System.out.println("symbol: " + nodeData[i].symbol);
            addSymbol(nodeData[i].symbol, nodeData[i].currentLocation);

        }

    }
    public void configure()
    {
        // configure start position
        Configuration.setValue(AVKey.INITIAL_PITCH, 16.0);
        Configuration.setValue(AVKey.INITIAL_HEADING, 358.0);
        Configuration.setValue(AVKey.INITIAL_LATITUDE, -22.550);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, 150.230);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 50000.0);
        // make offline (true is offline false is online)
        Configuration.setValue(AVKey.OFFLINE_MODE, true);
        // make flat
        Configuration.setValue(AVKey.GLOBE_CLASS_NAME, EarthFlat.class.getName());
        // Configuration.setValue(AVKey.VIEW_CLASS_NAME, FlatOrbitView.class.getName());
    }
    public void addSymbol(String sidc, LatLon pos)
    {
        //create symbol layer
        RenderableLayer symbolLayer = new RenderableLayer();
        TacticalSymbol newSymbol = setupSymbol(sidc, pos);
        symbolLayer.addRenderable(newSymbol);
        canvas.getModel().getLayers().add(symbolLayer);
    }
    public TacticalSymbol setupSymbol(String sidc, Position pos)
    {
        // place a tactical symbol
        // http://goworldwind.org/developers-guide/symbology/tactical-symbols/
        TacticalSymbol symname = new MilStd2525TacticalSymbol(sidc, pos);
        symname.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        // G*GPGPUY------- SFAPMFQM------A sfgaucvrm------
        // RenderableLayer symbolLayer = new RenderableLayer();
        TacticalSymbolAttributes attrs = new BasicTacticalSymbolAttributes();
        attrs.setScale(0.75); // Make the symbol 75% its normal size.
        attrs.setOpacity(0.5); // Make the symbol 50% transparent.
        symname.setAttributes(attrs);
        symname.setShowLocation(false);
        return symname;

    }
    public TacticalSymbol setupSymbol(String sidc, LatLon pos)
    {
        // place a tactical symbol
        // http://goworldwind.org/developers-guide/symbology/tactical-symbols/
        Position temppos = new Position(pos,0);
        TacticalSymbol symname = new MilStd2525TacticalSymbol(sidc,temppos);
        symname.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        // G*GPGPUY------- SFAPMFQM------A sfgaucvrm------
        // RenderableLayer symbolLayer = new RenderableLayer();
        TacticalSymbolAttributes attrs = new BasicTacticalSymbolAttributes();
        attrs.setScale(0.75); // Make the symbol 75% its normal size.
        attrs.setOpacity(0.5); // Make the symbol 50% transparent.
        symname.setAttributes(attrs);
        symname.setShowLocation(false);
        return symname;

    }
    //routine to create a random location based on a known location and a max distance in miles
    protected static LatLon randomLocation(LatLon centre,Double distance)
    {
        Globe tempGlobe = new Earth();
        //distance = angle in radians * globe radius in miles
        //miles to kms 1.60944kms to a mile
        //https://worldwind.arc.nasa.gov/java/latest/javadoc/gov/nasa/worldwind/geom/Angle.html
        //random angle
        Angle bearing = Angle.fromDegrees(Math.random()*360);
        distance = distance * Math.random();
        double distanceRadians = distance/ tempGlobe.getRadius();
        LatLon newLatLon= LatLon.greatCircleEndPosition(centre,bearing.radians,distanceRadians);

        return newLatLon;

    }

}
