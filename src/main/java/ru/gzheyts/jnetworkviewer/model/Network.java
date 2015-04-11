package ru.gzheyts.jnetworkviewer.model;

import com.javadocking.DockingManager;
import com.javadocking.dock.BorderDock;
import com.javadocking.dock.CompositeLineDock;
import com.javadocking.dock.Dock;
import com.javadocking.dock.Position;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphSelectionModel;
import net.inference.database.dto.Cluster;
import net.inference.database.dto.Entity;
import org.apache.log4j.Logger;
import ru.gzheyts.jnetworkviewer.gui.ClusterView;
import ru.gzheyts.jnetworkviewer.gui.TwinClusterView;
import ru.gzheyts.jnetworkviewer.model.convertеrs.ToStringConverter;

/**
 * @author gzheyts
 */
public class Network extends mxGraph {

    public static final String DEFAULT_EDGE_STYLE = "shape=connector;endArrow=classic;verticalAlign=middle;align=center;strokeColor=#6482B9;fontColor=#446299;strokeColor=green;noEdgeStyle=1;";
    public static final String DEFAULT_VERTEX_STYLE = "shape=ellipse;strokeColor=white;fillColor=grey;gradientColor=none;labelPosition=right;spacingLeft=8";
    public static final int DEFAULT_VERTEX_SIZE = 25;

    private static final String CELL_ID_PREFIX = "nc";

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

                    Object[] clusterCells = mxGraphModel.filterCells(((mxGraphSelectionModel) sender).getCells(), new mxGraphModel.Filter() {
                        public boolean filter(Object cell) {
                            return ((mxCell) cell).getValue() instanceof Cluster;
                        }
                    });

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

                    if (clusterCells.length > 2) {
                        return;
                    } else if (clusterCells.length == 2) {
                        previewContainer.addChildDock(new TwinClusterView(Network.empty(),
                                (Cluster) ((mxCell) clusterCells[0]).getValue(),
                                (Cluster) ((mxCell) clusterCells[1]).getValue())
                                .getDock(), new Position(0));
                    } else if (clusterCells.length == 1) {
                        previewContainer.addChildDock(new ClusterView(Network.empty(), (Cluster) ((mxCell) clusterCells[0]).getValue()).getDock(), new Position(0));
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

    public Object insertVertex(Entity node, String group) {
        return insertVertex(getDefaultParent(), cellId(node, group), node, 0, 0, DEFAULT_VERTEX_SIZE, DEFAULT_VERTEX_SIZE, DEFAULT_VERTEX_STYLE);
    }


    public Object insertVertex(Entity node) {
        return insertVertex(node, null);
    }


    public Object insertVertex(Entity node, String group, int width, int height) {
        return insertVertex(getDefaultParent(), cellId(node, group), node, 0, 0, width, height, DEFAULT_VERTEX_STYLE);
    }

    public Object insertVertex(Entity node, int width, int height) {
        return insertVertex(node, null, width, height);
    }

    public Object insertEdge(Object value, Entity source, Entity target, String group) {
        Object sourceCell = ((mxGraphModel) getModel()).getCell(cellId(source, group));
        Object targetCell = ((mxGraphModel) getModel()).getCell(cellId(target, group));
        if (sourceCell == null || targetCell == null) {
            logger.warn("no cell for entity");
        }
        return insertEdge(getDefaultParent(), cellId(source, target, group), value, sourceCell, targetCell, DEFAULT_EDGE_STYLE);
    }


    public Object insertEdge(Object value, Entity source, Entity target) {
        return insertEdge(value, source, target, null);
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

    // jgraphx ...
    public static String cellId(Object obj) {
        if (obj instanceof String) {
            return CELL_ID_PREFIX + obj;
        } else if (obj instanceof Entity) {
            return CELL_ID_PREFIX + ((Entity) obj).getId();
        }
        return null;
    }

    public static String cellId(long id) {
        return CELL_ID_PREFIX + id;
    }

    public static String cellId(Object obj1, Object obj2) {
        return cellId(obj1) + "-" + cellId(obj2);
    }


    public static String cellId(long id, String group) {
        return (group == null || group.length() == 0) ? cellId(id) : cellId(id) + "-" + group;
    }

    public static String cellId(Object obj1, Object obj2, String group) {
        return (group == null || group.length() == 0) ? cellId(obj1, obj2) : cellId(obj1, obj2) + "-" + group;
    }


}
