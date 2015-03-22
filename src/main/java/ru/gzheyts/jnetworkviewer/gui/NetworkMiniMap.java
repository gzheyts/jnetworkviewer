package ru.gzheyts.jnetworkviewer.gui;

import com.javadocking.dock.Dock;
import com.javadocking.dock.SingleDock;
import com.javadocking.dockable.*;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.drag.DragListener;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * @author gzheyts
 */
public class NetworkMiniMap extends JScrollPane implements DraggableContent {

    private mxGraphOutline outline;

    private SingleDock dock;

    private static final int MINIMAP_WIDTH = 200;
    private static final int MINIMAP_HEIGHT = 200;

    private String title;
    int dockingModes;

    public NetworkMiniMap(mxGraphComponent component, String title, int dockingModes) {

        outline = new mxGraphOutline(component);

        this.title = title;
        this.dockingModes = dockingModes;

        outline.setPreferredSize(new Dimension(MINIMAP_WIDTH, MINIMAP_HEIGHT));

        setViewportView(outline);

        setupListeners();

    }

    private void setupListeners() {
        outline.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getSource() instanceof mxGraphOutline
                        || e.isControlDown()) {
                    if (e.getWheelRotation() < 0) {
                        outline.getGraphComponent().zoomIn();
                    } else {
                        outline.getGraphComponent().zoomOut();
                    }

                }
            }
        });
    }

    @Override
    public void addDragListener(DragListener dragListener) {
        addMouseListener(dragListener);
        addMouseMotionListener(dragListener);
    }


    public Dock getDock() {
        if (dock == null) {
            createDock();
        }
        return dock;
    }

    public Dockable getDockable() {
        return dock.getDockable(0);
    }

    private void createDock() {
        dock = new SingleDock();

        DefaultDockable dockable = new DefaultDockable(getClass().getSimpleName(), this, title, null,
                dockingModes);

        Dockable wrapper = new StateActionDockable(dockable, new DefaultDockableStateActionFactory(), new int[0]);
        int[] states = {DockableState.NORMAL, DockableState.MINIMIZED, DockableState.EXTERNALIZED};
        wrapper = new StateActionDockable(wrapper, new DefaultDockableStateActionFactory(), states);

        dock.addDockable(wrapper, SingleDock.SINGLE_POSITION);

    }
}
