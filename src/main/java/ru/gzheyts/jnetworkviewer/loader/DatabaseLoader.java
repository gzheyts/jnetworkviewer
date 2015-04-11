package ru.gzheyts.jnetworkviewer.loader;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import net.inference.Config;
import net.inference.database.DatabaseApi;
import net.inference.database.dto.Author;
import net.inference.database.dto.Cluster;
import net.inference.sqlite.SqliteApi;
import net.inference.sqlite.dto.AuthorImpl;
import net.inference.sqlite.dto.ClusterImpl;
import org.apache.log4j.Logger;
import ru.gzheyts.jnetworkviewer.NetworkViewer;
import ru.gzheyts.jnetworkviewer.model.Network;
import ru.gzheyts.jnetworkviewer.model.convertеrs.ToStringConverter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author gzheyts
 */
public class DatabaseLoader {
    private static DatabaseApi api;
    private static final Logger logger = Logger.getLogger(DatabaseLoader.class);

    static {
        api = new SqliteApi(Config.Database.TEST, false);
    }

    public static void load(Network network) {


        logger.debug("loading network...");
        network.getModel().beginUpdate();

        try {
            List<AuthorImpl> authors = api.author().findAllAuthors();
            logger.debug("found " + authors.size() + " authors");

            for (Author author : authors) {
                logger.debug("[node] --> " + "( " + Network.cellId(author) + " ) " + ToStringConverter.convert(author));

                network.insertVertex(author);
            }

            for (Author author : authors) {
                api.author().findCoauthors(author);
                for (Author coauthor : api.author().findCoauthors(author)) {


                    logger.debug("[edge] --> " + "( " + Network.cellId(author, coauthor) + " ) "
                            + ToStringConverter.convert(author)
                            + " --> "
                            + ToStringConverter.convert(coauthor));

                    network.insertEdge(null, author, coauthor);
                }
            }

        } finally {
            network.getModel().endUpdate();

        }
        logger.debug("network loaded");

//        afterLoad();
    }

    public static void loadClusters(Network network) {

        logger.debug("loading clusters...");

        network.getModel().beginUpdate();
        try {

            for (Cluster cluster : api.cluster().findAllClusters()) {
                logger.debug("[node] --> " + "( " + Network.cellId(cluster) + " ) " + ToStringConverter.convert(cluster));
                network.insertVertex(cluster);
            }
        } finally {
            network.getModel().endUpdate();
        }

        logger.debug("clusters loaded");


    }

    public static void loadCluster(Network network, Cluster cluster) {
        logger.debug("load cluster .. " + ToStringConverter.convert(cluster));
        network.getModel().beginUpdate();
        String group = "cluster" + cluster.getId();
        try {
            List<AuthorImpl> authorsForCluster = api.author().findAuthorsForCluster(cluster);
            // adding author nodes
            for (Author author : authorsForCluster) {
                logger.debug("[node] --> " + "( " + Network.cellId(author, group) + " ) " + author);
                network.insertVertex(author, group);
            }

            // for each coauthor of retrieved author add relation if they both are in same cluster

            for (Author author : authorsForCluster) {
                for (Author coauthor : api.author().findCoauthors(author)) {
                    List<ClusterImpl> coauthorClusters = api.cluster().findClustersForAuthor(coauthor);

                    if (coauthorClusters.contains(cluster)) {

                        logger.debug("[edge] --> " + "( " + Network.cellId(author, coauthor, group) + " ) "
                                + ToStringConverter.convert(author)
                                + " --> "
                                + ToStringConverter.convert(coauthor));

                        network.insertEdge(null, author, coauthor, group);
                    }
                }
            }
        } finally {
            network.getModel().endUpdate();
        }

        logger.debug("cluster loaded  .. " + ToStringConverter.convert(cluster));
    }


    public static void loadClusters(Network network, Cluster[] clusters) {
        Network clusterNetwork = Network.empty();

        for (Cluster cluster : clusters) {
            loadCluster(clusterNetwork, cluster);
            Object[] cells = clusterNetwork.getChildCells(clusterNetwork.getDefaultParent());
            network.addCells(cells);

        }

    }

    private static void beforeLoad() {
        //api.onStart();
    }

    private static void afterLoad() {
        api.onStop();
    }

    public static class Worker extends SwingWorker<Network, Void> {

        private NetworkViewer networkViewer;


        public Worker(NetworkViewer viewer) {
            networkViewer = viewer;
        }

        @Override
        protected void done() {
            try {
                Network network = get();
                networkViewer.getNetworkView().setGraph(network);
                networkViewer.hideLoader();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Network doInBackground() throws Exception {
            Network loaded = new Network();
            load(loaded);

            mxGraphLayout layout = new mxOrganicLayout(loaded, new Rectangle(0, 0, 1000, 1000));
            layout.execute(loaded.getDefaultParent());

            return loaded;
        }
    }

    public static class FetchClustersWorker extends SwingWorker<Network, Void> {

        private NetworkViewer networkViewer;


        public FetchClustersWorker(NetworkViewer viewer) {
            networkViewer = viewer;
        }

        @Override
        protected void done() {
            try {
                Network network = get();
                networkViewer.getNetworkView().setGraph(network);
                networkViewer.hideLoader();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Network doInBackground() throws Exception {
            Network loaded = new Network();
            loadClusters(loaded);

            mxGraphLayout layout = new mxOrganicLayout(loaded, new Rectangle(0, 0, 1000, 1000));
            layout.execute(loaded.getDefaultParent());

            return loaded;
        }
    }
}


