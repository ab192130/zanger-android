package com.mavroo.dialer;

public class CircleItem {
    public String  name;
    public String  number;
    public int     imageId;
    public int     type;

    CircleItem(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    CircleItem(String name, int imageId, int type) {
        this.name = name;
        this.imageId = imageId;

        setType(type);
    }

    public void setType(int defaultType) {
        this.type = defaultType;
    }

    public boolean isDefault() {
        return (this.type > 0);
    }

    public String getTypeName() {
        String defaultKey = null;

        switch (type) {
            case CirclesAdapter.DEFAULT_TYPE_HOME:
                defaultKey = "default_number_home";
                break;

            case CirclesAdapter.DEFAULT_TYPE_TAXI:
                defaultKey = "default_number_taxi";
                break;

            case CirclesAdapter.DEFAULT_TYPE_RESTAURANT:
                defaultKey = "default_number_restaurant";
                break;

            case CirclesAdapter.DEFAULT_TYPE_HOTEL:
                defaultKey = "default_number_hotel";
                break;

            default:
                defaultKey = "default_number_home";
                break;
        }

        return defaultKey;
    }
}
