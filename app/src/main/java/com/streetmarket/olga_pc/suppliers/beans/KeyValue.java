package com.streetmarket.olga_pc.suppliers.beans;

/**
 * Created by Olga-PC on 5/12/2016.
 */
public class KeyValue {
    private int key;
    private String value;

    public KeyValue() {
        super();
    }

    public KeyValue(int key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
