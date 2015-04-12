package ru.gzheyts.jnetworkviewer.gui;

import com.javadocking.dock.Dock;
import com.javadocking.dock.SingleDock;
import com.javadocking.dockable.*;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.drag.DragListener;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.mxGraphComponent;
import net.inference.database.dto.Cluster;
import ru.gzheyts.jnetworkviewer.loader.DatabaseLoader;
import ru.gzheyts.jnetworkviewer.model.Network;
import ru.gzheyts.jnetworkviewer.model.convertеrs.ToStringConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * @author gzheyts
 */
public class TwinClusterView extends JPanel implements DraggableContent {

    private Cluster firstCluster;
    private Cluster secondCluster;
    private mxRubberband rubberband;

    private mxGraphComponent graphComponent;

    private ClusterAuthorTableView tableView;
    private JSplitPane splitPane;

    private SingleDock dock;



    public TwinClusterView(Network network, Cluster first, Cluster second) {

        super(new BorderLayout());

        graphComponent = new mxGraphComponent(network);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        tableView = new ClusterAuthorTableView(new Cluster[]{first, second});

        rubberband = new mxRubberband(graphComponent);

        this.firstCluster = first;
        this.secondCluster = second;

        setPreferredSize(new Dimension(200, 300));


        splitPane.setRightComponent(graphComponent);
        splitPane.setLeftComponent(tableView);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(300);

        add(splitPane, BorderLayout.CENTER);

        setupListeners();
        //todo : load in background
        initNetwork();

    }

    private void setupListeners() {
       graphComponent. addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    graphComponent.zoomIn();
                } else {
                    graphComponent.zoomOut();
                }

            }
        });
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
        Network graph = (Network) graphComponent.getGraph();
        DatabaseLoader.loadClusters(graph, new Cluster[]{firstCluster, secondCluster});
        new mxCircleLayout(graph).execute(graph.getDefaultParent());

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
