package com.mavroo.dialer;

public class CircleItem {
    public String  name;
    public String  number;
    public int     imageId;
    public int     defaultType;

    CircleItem(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    CircleItem(String name, int imageId, int type) {
        this.name = name;
        this.imageId = imageId;

        setDefaultType(type);
    }

    public void setDefaultType(int defaultType) {
        this.defaultType = defaultType;
    }

    public boolean isDefault() {
        return (this.defaultType > 0);
    }
}
