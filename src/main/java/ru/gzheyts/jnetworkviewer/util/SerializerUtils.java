package ru.gzheyts.jnetworkviewer.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.mxgraph.model.mxCell;

/**
 * @author gzheyts
 */
public class SerializerUtils {
    private SerializerUtils() {
    }

    public static String cellToJsonString(mxCell cell, boolean prettyPrint) {
        GsonBuilder builder = new GsonBuilder().setExclusionStrategies(new CellExclusionStrategy());

        if (prettyPrint) {
            builder.setPrettyPrinting();
        }
        return builder.create().toJson(cell);
    }


    static class CellExclusionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            if ("parent".equals(f.getName())
                    || "edges".equals(f.getName())
                    || "geometry".equals(f.getName())
                    ) {
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}
