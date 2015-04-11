package ru.gzheyts.jnetworkviewer.gui;

import com.javadocking.dock.Dock;
import com.javadocking.dock.SingleDock;
import com.javadocking.dockable.*;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.drag.DragListener;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import net.inference.database.dto.Cluster;
import ru.gzheyts.jnetworkviewer.loader.DatabaseLoader;
import ru.gzheyts.jnetworkviewer.model.Network;
import ru.gzheyts.jnetworkviewer.model.convertеrs.ToStringConverter;

import java.awt.*;

/**
 * @author gzheyts
 */
public class TwinClusterView extends mxGraphComponent implements DraggableContent {

    private Cluster firstCluster;
    private Cluster secondCluster;
    private SingleDock dock;


    public TwinClusterView(Network network, Cluster first, Cluster second) {
        super(network);
        this.firstCluster = first;
        this.secondCluster = second;

//        setEnabled(false);
        setPreferredSize(new Dimension(200, 400));
        //todo : load in background
        initNetwork();

    }

    public void addDragListener(DragListener dragListener) {
        addMouseListener(dragListener);
        addMouseMotionListener(dragListener);
    }


    public String getTitle() {
        return ToStringConverter.convert(firstCluster)
                + ","
                + ToStringConverter.convert(secondCluster);
    }


    public void initNetwork() {
        DatabaseLoader.loadClusters((Network) getGraph(), new Cluster[]{firstCluster, secondCluster});
        new mxCircleLayout(getGraph()).execute(getGraph().getDefaultParent());

    }

    public Dock getDock() {
        if (dock == null) {
            createDock();
        }

        return dock;
    }

    private void createDock() {
        dock = new SingleDock();

        DefaultDockable dockable = new DefaultDockable(getClass().getSimpleName(), this, getTitle(), null,
                DockingMode.SINGLE + DockingMode.BOTTOM);

        Dockable wrapper = new StateActionDockable(dockable, new DefaultDockableStateActionFactory(), new int[0]);
        int[] states = {DockableState.NORMAL, DockableState.CLOSED};
        wrapper = new StateActionDockable(wrapper, new DefaultDockableStateActionFactory(), states);

        dock.addDockable(wrapper, SingleDock.SINGLE_POSITION);
    }

}
