package ru.gzheyts.jnetworkviewer.model;

import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.view.mxGraph;
import org.apache.log4j.Logger;
import ru.gzheyts.jnetworkviewer.generator.RandomNetworkGenerator;

/**
 * @author gzheyts
 */
public class Network extends mxGraph {

    public static final String DEFAULT_EDGE_STYLE = "shape=connector;endArrow=classic;verticalAlign=middle;align=center;strokeColor=#6482B9;fontColor=#446299;strokeColor=green;noEdgeStyle=1;";
    public static final String DEFAULT_VERTEX_STYLE = "shape=ellipse;strokeColor=white;fillColor=grey;gradientColor=none;labelPosition=right;spacingLeft=8";
    public static final int DEFAULT_VERTEX_SIZE= 25;

    private static Logger logger = Logger.getLogger(Network.class);

    public Network() {
        initialize();
    }

    private void initialize() {
        setCellsResizable(false);
        setupListeners();

    }

    private void setupListeners() {
        getSelectionModel().addListener(mxEvent.CHANGE, new mxIEventListener() {
            @Override
            public void invoke(Object sender, mxEventObject evt) {
/*
                Object added = evt.getProperty("added");
                Object removed = evt.getProperty("removed");
                Object[] selectedCells = getSelectionModel().getCells();
                if (selectedCells != null && selectedCells.length == 2) {
                    VD vd1 = (VD) ((mxCell) selectedCells[0]).getValue();
                    VD vd2 = (VD) ((mxCell) selectedCells[1]).getValue();
                    logger.info("=========================================>");
                    logger.info(vd1 + " | " + vd1.stringified());
                    logger.info(vd2 + " | " + vd2.stringified());
                    logger.info("<==========================================");

                }
*/

            }
        });
    }


    public Object insertVertex(String id, Object value) {
        return insertVertex(getDefaultParent(), id, value, 0, 0, DEFAULT_VERTEX_SIZE, DEFAULT_VERTEX_SIZE, DEFAULT_VERTEX_STYLE);
    }

    public Object insertEdge( String id, Object value, Object source, Object target) {
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
}
