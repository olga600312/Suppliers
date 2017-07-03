package com.streetmarket.olga_pc.suppliers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Olga-PC on 5/12/2016.
 */
public class ItemSearchCriteria implements Parcelable {
    public final static int NOT_FILTERED=-1;
    public final static int FILTERED_FOUND=1;
    public final static int FILTERED_NOT_FOUND=0;

    private String code;
    private String name;
    private int supplier =-1;

    public ItemSearchCriteria() {

    }

    public boolean isEmpty() {
        return (code == null || code.trim().isEmpty()) && (name == null || name.trim().isEmpty())&&supplier<0;
    }

    public int getSupplier() {
        return supplier;
    }

    public void setSupplier(int supplier) {
        this.supplier = supplier;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
        dest.writeInt(supplier);
    }

    protected ItemSearchCriteria(Parcel in) {
        code = in.readString();
        name = in.readString();
        supplier=in.readInt();
    }

    public static final Creator<ItemSearchCriteria> CREATOR = new Creator<ItemSearchCriteria>() {
        @Override
        public ItemSearchCriteria createFromParcel(Parcel in) {
            return new ItemSearchCriteria(in);
        }

        @Override
        public ItemSearchCriteria[] newArray(int size) {
            return new ItemSearchCriteria[size];
        }
    };

}

