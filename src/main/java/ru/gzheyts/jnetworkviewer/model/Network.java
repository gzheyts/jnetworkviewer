package ru.gzheyts.jnetworkviewer.model;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.view.mxGraph;
import org.apache.log4j.Logger;
import ru.gzheyts.jnetworkviewer.util.networkGeneratorUtils;

import java.util.List;

/**
 * @author gzheyts
 */
public class Network extends mxGraph {

    private static Logger logger = Logger.getLogger(Network.class);

    public Network(boolean random) {
        initialize();

        if (random) {
            generateRandom();
        }
    }

    private void initialize() {
        setCellsResizable(false);
        setupListeners();

    }

    private void generateRandom() {
        long start = System.currentTimeMillis();


        getModel().beginUpdate();
        try {
            selectAll();
            removeCells();

            List vertices = networkGeneratorUtils.generateVertices(this, 1000, 10000);

            networkGeneratorUtils.randomConnect(this, vertices);

        } finally {
            getModel().endUpdate();
        }

        logger.info("network generation time: " + (System.currentTimeMillis() - start) + " ms");
    }

    private void setupListeners() {
        getSelectionModel().addListener(mxEvent.CHANGE, new mxIEventListener() {
            @Override
            public void invoke(Object sender, mxEventObject evt) {
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

            }
        });
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
