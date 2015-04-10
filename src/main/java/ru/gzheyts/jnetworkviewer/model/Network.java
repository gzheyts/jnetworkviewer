package ru.gzheyts.jnetworkviewer.model;

import com.javadocking.DockingManager;
import com.javadocking.dock.BorderDock;
import com.javadocking.dock.CompositeLineDock;
import com.javadocking.dock.Dock;
import com.javadocking.dock.Position;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphSelectionModel;
import net.inference.database.dto.Cluster;
import org.apache.log4j.Logger;
import ru.gzheyts.jnetworkviewer.gui.ClusterView;
import ru.gzheyts.jnetworkviewer.model.convertеrs.ToStringConverter;

/**
 * @author gzheyts
 */
public class Network extends mxGraph {

    public static final String DEFAULT_EDGE_STYLE = "shape=connector;endArrow=classic;verticalAlign=middle;align=center;strokeColor=#6482B9;fontColor=#446299;strokeColor=green;noEdgeStyle=1;";
    public static final String DEFAULT_VERTEX_STYLE = "shape=ellipse;strokeColor=white;fillColor=grey;gradientColor=none;labelPosition=right;spacingLeft=8";
    public static final int DEFAULT_VERTEX_SIZE = 25;

    private static Logger logger = Logger.getLogger(Network.class);

    public Network() {
        initialize();
    }

    private void initialize() {
        setCellsResizable(false);
        setupListeners();

    }

    private void setupListeners() {

        getSelectionModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {
            @Override
            public void invoke(Object sender, mxEventObject evt) {
                if (sender instanceof mxGraphSelectionModel) {
                    for (Object cell : ((mxGraphSelectionModel) sender).getCells()) {
                        Object cellValue = ((mxCell) cell).getValue();
                        if (cellValue instanceof Cluster) {


                            // remove child docks
                            CompositeLineDock previewContainer = (CompositeLineDock) DockingManager.getDockModel().getRootDock("previewContainer");
                            for (int childIndex = 0; childIndex < previewContainer.getChildDockCount(); childIndex++) {
                                previewContainer.emptyChild(previewContainer.getChildDock(childIndex));
                            }

                            // if preview container dock is empty it will be removed from parent dock by docking library --> get it back
                            BorderDock rootContainer = (BorderDock) DockingManager.getDockModel().getRootDock("rootdock");
                            Dock childDockOfPosition = rootContainer.getChildDockOfPosition(Position.BOTTOM);
                            if (childDockOfPosition == null) {
                                rootContainer.addChildDock(previewContainer, new Position(Position.BOTTOM));
                            }

                            if (getSelectionCount() > 2) {
                                return;
                            } else if (getSelectionCount() == 2) {
                                // todo: show composite cluster view for 2 clusters
                            } else if (getSelectionCount() == 1) {
                                previewContainer.addChildDock(new ClusterView(Network.empty(), (Cluster) cellValue).getDock(), new Position(0));
                            }

                        }
                    }
                }
            }
        });

    }

    @Override
    public String convertValueToString(Object cell) {

        if (cell instanceof mxCell) {
            return ToStringConverter.convert(((mxCell) cell).getValue());
        }

        return super.convertValueToString(cell);
    }

    public Object insertVertex(String id, Object value) {
        return insertVertex(getDefaultParent(), id, value, 0, 0, DEFAULT_VERTEX_SIZE, DEFAULT_VERTEX_SIZE, DEFAULT_VERTEX_STYLE);
    }

    public Object insertVertex(String id, Object value, int width, int height) {
        return insertVertex(getDefaultParent(), id, value, 0, 0, width, height, DEFAULT_VERTEX_STYLE);
    }

    public Object insertEdge(String id, Object value, Object source, Object target) {
        return insertEdge(getDefaultParent(), id, value, source, target, DEFAULT_EDGE_STYLE);
    }

    @Override
    public boolean isCellSelectable(Object cell) {
        return getModel().isVertex(cell);
    }

    @Override
    public boolean isCellConnectable(Object cell) {
        return false;
    }


    public static Network empty() {
        return new Network();
    }
}
