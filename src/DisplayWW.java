import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.EarthFlat;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.BasicTacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.TacticalSymbol;
import gov.nasa.worldwind.symbology.TacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalSymbol;
import gov.nasa.worldwind.util.BasicDragger;
import gov.nasa.worldwind.util.WWUtil;

import javax.swing.*;
import java.awt.*;
/*
The DisplayWW Class is used to control and update the WorldWind Canvas, a
 */
class DisplayWW extends JPanel
{
    WorldWindowGLCanvas canvas;
    Position defaultPosition = Position.fromDegrees(-22.509187, 150.096047, 1);
    Position rCPosition = Position.fromDegrees(-22.71220, 150.40076, 1);



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
        symbolLayer.setName("symbolLayer");
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
        //////////////////////////////////////

    //    TacticalSymbol newSymbol = setupSymbol("SFGPUCRVA-bf---", rCPosition);
      //  symbolLayer.addRenderable(newSymbol);

        /////////////////////////////////////////////
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
        symname.setModifier("test",1);
        symname.setShowTextModifiers(true);
        symname.setAttributes(attrs);
        symname.setShowLocation(true);
        return symname;

    }


}
