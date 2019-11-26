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
import gov.nasa.worldwind.event.SelectListener;
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
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.BasicTacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.TacticalSymbol;
import gov.nasa.worldwind.symbology.TacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalSymbol;
import gov.nasa.worldwind.util.BasicDragger;
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
/*
The DisplayWW Class is used to control and update the WorldWind Canvas, a
 */
class DisplayWW extends JPanel
{
    WorldWindowGLCanvas canvas;
    LatLon defaultPosition = Position.fromDegrees(-22.509187, 150.096047, 1);
    LatLon rCPosition = Position.fromDegrees(-22.71220, 150.40076, 1);



    public DisplayWW(NodeData[] nodeData)
    {
        //set up default configurations for Shoalwater bay
        configure();

        // create main object
        canvas = new WorldWindowGLCanvas();
        canvas.setModel(new BasicModel());
        canvas.setSize(new Dimension(600,600));
        setLayout(new BorderLayout(5, 5));
        add(canvas,BorderLayout.CENTER);
        // remove world map
        Layer worldMapLayer = canvas.getModel().getLayers().getLayerByName("World Map");
        canvas.getModel().getLayers().remove(worldMapLayer);
        //remove compass
        Layer compassLayer = canvas.getModel().getLayers().getLayerByName("Compass");
        canvas.getModel().getLayers().remove(compassLayer);

        //load array of nodes into worldwind as symbols to be displayed
        //set up a symbolLayer containing all the symbols
        RenderableLayer symbolLayer = new RenderableLayer();
        int NumberOfNodes = nodeData.length;
        System.out.println("number of nodes: " + nodeData.length);
        for (int i = 0; i < NumberOfNodes; i++)
        {
            System.out.println("symbol: " + nodeData[i].symbol);
            //pass in the node, have the node updated with the tactical symbol
            symbolLayer = addSymbol(symbolLayer,nodeData[i]);
            //check if identifier is updated
            System.out.println("symbol id: " + nodeData[i].symbolIdentifier);
        }
        //load the symbolLayer into the canvas
        canvas.getModel().getLayers().add(symbolLayer);

        RenderableLayer shapesLayer = new RenderableLayer();
        shapesLayer.setName("Shapes to Track");
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setInteriorMaterial(Material.BLUE);
        attrs.setOutlineMaterial(new Material(WWUtil.makeColorBrighter(Color.BLUE)));
        attrs.setInteriorOpacity(0.5);
        SurfaceCircle circle = new SurfaceCircle(attrs, rCPosition, 50d);
        shapesLayer.addRenderable(circle);
        canvas.getModel().getLayers().add(shapesLayer);


        RenderableLayer linesLayer = new RenderableLayer();
        Position pointA = nodeData[0].currentLocation;
        Position pointB = nodeData[1].currentLocation;
        Path path = new Path(pointA,pointB);
         linesLayer.addRenderable(path);
        canvas.getModel().getLayers().add(linesLayer);

        canvas.addSelectListener(new SelectListener()
        {
            protected BasicDragger dragger = new BasicDragger(canvas);

            public void selected(SelectEvent event)
            {
                // Delegate dragging computations to a dragger.
                this.dragger.selected(event);

                if (event.getEventAction().equals(SelectEvent.DRAG))

                {

                    for (int i = 0; i < NumberOfNodes; i++)
                    {

                        Object object = event.getTopPickedObject().getObject();
                        if (object == nodeData[i].symbolIdentifier)
                        {
                            //update the dragged symbol node with the new location
                            nodeData[i].currentLocation = nodeData[i].symbolIdentifier.getPosition();
                        }
                        //pass in the node, have the node updated with the tactical symbol
                    }

                }
            }
        });
    }
    public void configure()
    {
        // configure start position
        Configuration.setValue(AVKey.INITIAL_PITCH, 16.0);
        Configuration.setValue(AVKey.INITIAL_HEADING, 358.0);
        Configuration.setValue(AVKey.INITIAL_LATITUDE, -22.71220);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, 150.40076);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 20000.0);
        // make offline (true is offline false is online)
        Configuration.setValue(AVKey.OFFLINE_MODE, true);
        // make flat
        Configuration.setValue(AVKey.GLOBE_CLASS_NAME, EarthFlat.class.getName());
        // Configuration.setValue(AVKey.VIEW_CLASS_NAME, FlatOrbitView.class.getName());
        //if the 2525B symbols are not available online the following variable needs to be set according to
        //https://forum.worldwindcentral.com/forum/world-wind-java-forums/symbology-and-tactical-graphics/15478-milstd2525iconretrieverpath-for-offline-use
        //<Property name="gov.nasa.worldwind.avkey.MilStd2525IconRetrieverPath" value="jar:file:milstd2525-symbols.zip!"/>
        Configuration.setValue(AVKey.MIL_STD_2525_ICON_RETRIEVER_PATH , "jar:file:milstd2525-symbols.zip!");
        //Note: if running from intellij the zip file needs to be in the root directory path, if from console it needs to be next to the jar
    }
    /*takes the Renderable layer and adds a tactical symbol to it, returning that updated layer and adding symbol to node
     */
    public RenderableLayer addSymbol(RenderableLayer symbolLayer,NodeData nodeData)
    {

        TacticalSymbol newSymbol = setupSymbol(nodeData.symbol, nodeData.currentLocation);
        symbolLayer.addRenderable(newSymbol);
        //load this symbol into the Node
        nodeData.symbolIdentifier = newSymbol;
        return symbolLayer;
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


}
