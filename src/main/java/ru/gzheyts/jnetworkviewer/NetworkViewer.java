package ru.gzheyts.jnetworkviewer;

import com.javadocking.DockingManager;
import com.javadocking.dock.BorderDock;
import com.javadocking.dock.Position;
import com.javadocking.dock.factory.SingleDockFactory;
import com.javadocking.dockable.DockingMode;
import com.javadocking.model.FloatDockModel;
import com.javadocking.visualizer.FloatExternalizer;
import com.javadocking.visualizer.LineMinimizer;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import org.apache.log4j.Logger;
import ru.gzheyts.jnetworkviewer.gui.NetworkMiniMap;
import ru.gzheyts.jnetworkviewer.gui.NetworkView;
import ru.gzheyts.jnetworkviewer.gui.menu.MenuBar;
import ru.gzheyts.jnetworkviewer.loader.DatabaseLoader;
import ru.gzheyts.jnetworkviewer.model.Network;

import javax.swing.*;
import java.awt.*;


/**
 * @author gzheyts
 */

public class NetworkViewer extends JPanel {


    public static final int FRAME_WIDTH = 600;
    public static final int FRAME_HEIGHT = 450;

    private static final Logger logger = Logger.getLogger(NetworkViewer.class);

    private Network network;
    private NetworkView networkView;
    private NetworkMiniMap networkMiniMap;


    public NetworkViewer(JFrame frame) {
        super(new BorderLayout());

        // Create the dock model for the docks.
        FloatDockModel dockModel = new FloatDockModel();
        dockModel.addOwner("frame0", frame);

        // Give the dock model to the docking manager.
        DockingManager.setDockModel(dockModel);

        network = new Network();
        networkView = new NetworkView(network, "Network", DockingMode.CENTER);
        networkMiniMap = new NetworkMiniMap(networkView,"MiniMap", DockingMode.LEFT  + DockingMode.RIGHT + DockingMode.FLOAT + DockingMode.SINGLE );

       loadAndLayoutNetwork(network);


        BorderDock rootDock = setupDockContainer();


        JPanel rootPanel = setupVisualizers(frame, dockModel, rootDock);

        add(rootPanel, BorderLayout.CENTER);

        frame.setJMenuBar(new MenuBar(frame, networkMiniMap.getDockable()));

        frame.add(this);


    }

    private void loadAndLayoutNetwork(final Network network) {

        DatabaseLoader.laod(network);

        final mxGraphLayout layout = new mxOrganicLayout(network,new Rectangle(0,0,1000,1000));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                layout.execute(network.getDefaultParent());
            }
        });

    }

    private BorderDock setupDockContainer() {
        BorderDock rootDock = new BorderDock(new SingleDockFactory());

        rootDock.addChildDock(networkMiniMap.getDock(), new Position(Position.LEFT));

        rootDock.addChildDock(networkView.getDock(), new Position(Position.CENTER));
        return rootDock;
    }

    private LineMinimizer setupVisualizers(JFrame frame, FloatDockModel dockModel, BorderDock rootDock) {
        dockModel.addRootDock("rootdock", rootDock, frame);


        FloatExternalizer externalizer = new FloatExternalizer(frame);
        dockModel.addVisualizer("externializer", externalizer, frame);

        LineMinimizer minimizer = new LineMinimizer(rootDock);
        dockModel.addVisualizer("minimizer", minimizer,frame);
        return minimizer;
    }


    public static void createAndShowGUI() {

        // Create the appFrame.

        JFrame appFrame = new JFrame("Network Viewer");

        new NetworkViewer(appFrame);
        // Create the panel and add it to the appFrame.


        // Set the appFrame properties and show it.
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        appFrame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
        appFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        appFrame.setVisible(true);

    }

    public static void main(String args[]) {

        logger.info("starting application");

        Runnable doCreateAndShowGUI = new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        };
        SwingUtilities.invokeLater(doCreateAndShowGUI);

    }

}
