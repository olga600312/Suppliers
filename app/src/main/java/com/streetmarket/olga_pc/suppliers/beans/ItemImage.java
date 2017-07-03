package com.streetmarket.olga_pc.suppliers.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Olga-PC on 5/12/2016.
 */
public class ItemImage  implements Parcelable {
    private String fileName;
    private String code;
    private byte[] image;
    private int status;

    protected ItemImage(Parcel in) {
        fileName = in.readString();
        code = in.readString();
        image = in.createByteArray();
        status = in.readInt();
    }

    public static final Creator<ItemImage> CREATOR = new Creator<ItemImage>() {
        @Override
        public ItemImage createFromParcel(Parcel in) {
            return new ItemImage(in);
        }

        @Override
        public ItemImage[] newArray(int size) {
            return new ItemImage[size];
        }
    };

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(code);
        dest.writeByteArray(image);
        dest.writeInt(status);
    }
}
