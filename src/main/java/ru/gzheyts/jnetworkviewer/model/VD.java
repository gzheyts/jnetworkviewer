package ru.gzheyts.jnetworkviewer.model;

import java.io.Serializable;

/**
 * @author gzheyts
 */
public class VD implements Serializable {

    private String label;

    private int val1, val2, val3;

    public VD(String label, int v1, int v2, int v3) {
        this.label = label;

        val1 = v1;
        val2 = v2;
        val3 = v3;

    }

    @Override
    public String toString() {
        return label.toString();
    }

    public String stringified() {
        return "VD{" +
                "label='" + label + '\'' +
                ", val1=" + val1 +
                ", val2=" + val2 +
                ", val3=" + val3 +
                '}';
    }
}
