package com.streetmarket.olga_pc.suppliers.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Hashtable;

/**
 * Created by Olga-PC on 5/12/2016.
 */
public class Item implements Parcelable {
    private String code;
    private String name;
    private float price;
    private float priceNetto;
    private float cost;
    private float count;
    private int grp;
    private int dprt;
    private int supplier;
    private boolean tax;
    private Hashtable<String,Object> extra;

    public Item() {
        extra=new Hashtable<>();
    }

    protected Item(Parcel in) {
        code = in.readString();
        name = in.readString();
        price = in.readFloat();
        priceNetto = in.readFloat();
        cost = in.readFloat();
        count = in.readFloat();
        grp = in.readInt();
        dprt = in.readInt();
        supplier = in.readInt();
        tax=in.readInt()==1;
        extra=new Hashtable<>();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }

    public int getGrp() {
        return grp;
    }

    public void setGrp(int grp) {
        this.grp = grp;
    }

    public int getDprt() {
        return dprt;
    }

    public void setDprt(int dprt) {
        this.dprt = dprt;
    }

    public int getSupplier() {
        return supplier;
    }

    public void setSupplier(int supplier) {
        this.supplier = supplier;
    }

    public Hashtable<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Hashtable<String, Object> extra) {
        this.extra = extra;
    }

    public boolean isTax() {
        return tax;
    }

    public void setTax(boolean tax) {
        this.tax = tax;
    }

    public float getPriceNetto() {
        return priceNetto;
    }

    public void setPriceNetto(float priceNetto) {
        this.priceNetto = priceNetto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
        dest.writeFloat(price);
        dest.writeFloat(priceNetto);
        dest.writeFloat(cost);
        dest.writeFloat(count);
        dest.writeInt(grp);
        dest.writeInt(dprt);
        dest.writeInt(supplier);
        dest.writeInt(tax?1:0);
    }
}
