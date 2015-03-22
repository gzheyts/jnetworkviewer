package ru.gzheyts.jnetworkviewer.util;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.view.mxGraph;
import ru.gzheyts.jnetworkviewer.model.VD;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author gzheyts
 */
public final class networkGeneratorUtils {
    private networkGeneratorUtils() {
    }


    private static final String EDGE_STYLE = "shape=connector;endArrow=classic;verticalAlign=middle;align=center;strokeColor=#6482B9;fontColor=#446299;strokeColor=green;noEdgeStyle=1;";
    private static final String VERTEX_STYLE = "shape=ellipse;strokeColor=white;fillColor=black;gradientColor=none;labelPosition=right";


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
                    new Point(randInt(0, gridsize), randInt(0, gridsize)), 25));
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

        return graph.insertEdge(graph.getDefaultParent(), null, edgeLabel, source, target, EDGE_STYLE);

    }

    private static Object createVertex(mxGraph graph, Object parent, String label, Point point,
                                       int size) {
        return graph.insertVertex(parent, null, new VD(label, 3, 3, 7845), point.x, point.y, size, size, VERTEX_STYLE
        );
    }

    public static void setGraphStyle(mxGraph graph) {
        Object parent = graph.getDefaultParent();
        Object[] vertices = graph.getChildVertices(parent);
        mxIGraphModel model = graph.getModel();

        for (int i = 0; i < vertices.length; i++) {
            model.setStyle(vertices[i], VERTEX_STYLE);
        }

        Object[] edges = graph.getChildEdges(parent);

        for (int i = 0; i < edges.length; i++) {
            model.setStyle(edges[i], EDGE_STYLE);
        }
    }

    private static int randInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }


}
