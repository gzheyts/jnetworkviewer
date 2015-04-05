package ru.gzheyts.jnetworkviewer.loader;

import com.mxgraph.model.mxGraphModel;
import net.inference.Config;
import net.inference.database.DatabaseApi;
import net.inference.database.dto.Author;
import net.inference.sqlite.SqliteApi;
import net.inference.sqlite.dto.AuthorImpl;
import org.apache.log4j.Logger;
import ru.gzheyts.jnetworkviewer.model.Network;

import java.util.List;

/**
 * @author george
 */
public class DatabaseLoader {
    private static  DatabaseApi api;
    private static final Logger logger = Logger.getLogger(DatabaseLoader.class);
    static {
        api = new SqliteApi(Config.Database.TEST, false);
    }


    public static void laod(Network network) {

        beforeLoad();
        logger.info("loading network...");
        network.getModel().beginUpdate();

        try {
            List<AuthorImpl> authors = api.author().findAllAuthors();
            logger.info("found " + authors.size() + " authors");

            for (Author author : authors) {
                network.insertVertex(String.valueOf(author.getId()), author.getName());
            }

            for (Author author : authors) {
                api.author().findCoauthors(author);
                for (Author coauthor : api.author().findCoauthors(author)) {

                    Object authorCell = ((mxGraphModel) network.getModel()).getCell(String.valueOf(author.getId()));
                    Object coauthorCell = ((mxGraphModel) network.getModel()).getCell(String.valueOf(coauthor.getId()));

                    network.insertEdge(String.valueOf(author.getId() + "-" + coauthor.getId()), null, authorCell, coauthorCell);
                }
            }

        } finally {
            network.getModel().endUpdate();

        }
        logger.info("network loaded");
        afterLoad();
    }

    private static  void beforeLoad() {
        //api.onStart();
    }

    private static void afterLoad() {
        api.onStop();
    }

}
