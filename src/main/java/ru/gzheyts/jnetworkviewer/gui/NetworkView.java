package ru.gzheyts.jnetworkviewer.gui;

import com.javadocking.dock.Dock;
import com.javadocking.dock.SingleDock;
import com.javadocking.dockable.DefaultDockable;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import java.awt.*;

/**
 * @author gzheyts
 */
public class NetworkView extends mxGraphComponent {
    private mxKeyboardHandler keyboardHandler;

    private SingleDock dock;
    private String title;
    private int dockingModes;

    public NetworkView(mxGraph graph, String title, int dockingModes) {
        super(graph);

        keyboardHandler = new mxKeyboardHandler(this);
        this.dockingModes = dockingModes;
        this.title = title;


        setGridVisible(true);
        getViewport().getView().setBackground(Color.LIGHT_GRAY);
    }


    public Dock getDock() {
        if (dock == null) {
            createDock();
        }
        return dock;
    }

    private void createDock() {
        dock = new SingleDock();
        dock.addDockable(new DefaultDockable(getClass().getSimpleName(), this, title, null, dockingModes), SingleDock.SINGLE_POSITION);

    }
}
