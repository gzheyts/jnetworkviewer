package jung;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import net.inference.Config;
import net.inference.database.DatabaseApi;
import net.inference.database.dto.Author;
import net.inference.sqlite.SqliteApi;
import net.inference.sqlite.dto.AuthorImpl;

import javax.swing.*;
import java.awt.*;

/**
 * @author gzheyts
 */
public class JungNetworkViewer extends JPanel {

    DirectedGraph<String, String> graph;
    VisualizationViewer<String, String> vv;

    public JungNetworkViewer() {
        super(new BorderLayout());

        graph = new DirectedSparseMultigraph<String, String>();

        laodGraph();
        Layout<String, String> layout = new SpringLayout<String, String>(graph);


        layout.setSize(new Dimension(1000, 1000));


        vv = new VisualizationViewer<String, String>(layout, new Dimension(640, 480));
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        vv.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<String>(vv.getPickedEdgeState(), Color.black, Color.cyan));

        vv.setBackground(Color.lightGray);

        vv.getRenderContext().setVertexLabelRenderer(new DefaultVertexLabelRenderer(Color.cyan));
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
        vv.getRenderContext().setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(Color.cyan));
        vv.setVertexToolTipTransformer(new ToStringLabeller<String>());
        vv.setEdgeToolTipTransformer(new ToStringLabeller<String>());

        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);

        final DefaultModalGraphMouse<String, String> graphMouse = new DefaultModalGraphMouse<String, String>();
        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());

//        vv.getModel().getRelaxer().stop();
        add(panel, BorderLayout.CENTER);
    }

    private void laodGraph() {
        DatabaseLoader.laod(graph);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        content.add(new JungNetworkViewer());
        frame.pack();
        frame.setVisible(true);

    }

    static class DatabaseLoader {
        private static DatabaseApi api;

        static {
            api = new SqliteApi(Config.Database.TEST, false);
        }


        public static void laod(Graph<String, String> network) {

            beforeLoad();
            DirectedSparseMultigraph<String, String> graph = (DirectedSparseMultigraph) network;
            java.util.List<AuthorImpl> authors = api.author().findAll();
            System.out.println("found " + authors.size() + " authors");

            for (Author author : authors) {
                graph.addVertex(String.valueOf(author.getName()));
            }

            for (final Author author : authors) {
                api.author().findCoauthors(author);
                for (Author coauthor : api.author().findCoauthors(author)) {


                    ((DirectedSparseMultigraph) network).addEdge(author.getId() + "" + coauthor.getId()
                            , author.getName(), coauthor.getName(), EdgeType.DIRECTED);
                }
            }

            afterLoad();
        }

        private static void beforeLoad() {
            api.onStart();
        }

        private static void afterLoad() {
            api.onStop();
        }
    }
}
