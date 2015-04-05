package ru.gzheyts.jnetworkviewer.generator;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.view.mxGraph;
import org.apache.log4j.Logger;
import ru.gzheyts.jnetworkviewer.model.Network;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author gzheyts
 */
public final class RandomNetworkGenerator {

    private static final Logger logger = Logger.getLogger(RandomNetworkGenerator.class);

    private RandomNetworkGenerator() {
    }

    public static void randomConnect(mxGraph graph, List vertices) {
        int vLength = vertices.size();

        Object source, target;
        int connectVertSize;

        for (int vInd = 0; vInd < vLength - 1; vInd++) {
            source = vertices.get(vInd);
            connectVertSize = randInt(0, 3);

            for (int i = 0; i < connectVertSize; i++) {
                target = vertices.get(randInt(vInd + 1, vLength - 1));

                createEdge(graph, source, target);
            }
        }
    }

    public static List generateVertices(mxGraph graph, int size, int gridsize) {
        List vertices = new ArrayList(size);
        for (int i = 0; i < size; i++) {

            Object defaultParent = graph.getDefaultParent();

            vertices.add(createVertex(graph, defaultParent, "v" + i,
                    new Point(randInt(0, gridsize), randInt(0, gridsize)), Network.DEFAULT_VERTEX_SIZE));
        }

        return vertices;
    }

    private static Object createEdge(mxGraph graph, Object source, Object target) {
        String edgeLabel = "";

        if (source instanceof mxCell) {
            edgeLabel += ((mxCell) source).getValue();
        }
        if (target instanceof mxCell) {
            edgeLabel = edgeLabel + "-" + ((mxCell) source).getValue();
        }

        return graph.insertEdge(graph.getDefaultParent(), null, edgeLabel, source, target, Network.DEFAULT_EDGE_STYLE);

    }

    private static Object createVertex(mxGraph graph, Object parent, String label, Point point,
                                       int size) {
        return graph.insertVertex(parent, null, label, point.x, point.y, size, size, Network.DEFAULT_VERTEX_STYLE
        );
    }


    public static void setGraphStyle(mxGraph graph) {
        Object parent = graph.getDefaultParent();
        Object[] vertices = graph.getChildVertices(parent);
        mxIGraphModel model = graph.getModel();

        for (int i = 0; i < vertices.length; i++) {
            model.setStyle(vertices[i], Network.DEFAULT_VERTEX_STYLE);
        }

        Object[] edges = graph.getChildEdges(parent);

        for (int i = 0; i < edges.length; i++) {
            model.setStyle(edges[i], Network.DEFAULT_EDGE_STYLE);
        }
    }

    private static int randInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static void generate(Network network) {

        long start = System.currentTimeMillis();


        network.getModel().beginUpdate();
        try {
            network.selectAll();
            network.removeCells();

            List vertices = generateVertices(network, 1000, 10000);

            randomConnect(network, vertices);

        } finally {
            network.getModel().endUpdate();
        }

        logger.info("network generation time: " + (System.currentTimeMillis() - start) + " ms");

    }



}
