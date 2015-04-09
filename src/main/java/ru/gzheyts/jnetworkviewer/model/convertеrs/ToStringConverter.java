package ru.gzheyts.jnetworkviewer.model.convertеrs;

import net.inference.database.dto.Author;
import net.inference.database.dto.Cluster;
import net.inference.database.dto.Evolution;
import net.inference.database.dto.EvolutionSlice;

/**
 * @author gzheyts
 */
public class ToStringConverter {
    private ToStringConverter() {
    }

    public static String convert(Object obj) {

        if (obj instanceof Author) {
            return ((Author) obj).getName() + " " + ((Author) obj).getSurname();
        } else if (obj instanceof Cluster) {
            return "cluster-" + ((Cluster) obj).getId();
        } else if (obj instanceof Evolution) {
            return "evolution-" + ((Evolution) obj).getId();
        } else if (obj instanceof EvolutionSlice) {
            return "slice-" + ((EvolutionSlice) obj).getId();
        }

        return null;
    }

}
