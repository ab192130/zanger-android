package com.mavroo.dialer;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class CircleItem {
    public static final int TYPE_DEFAULT = 1;
    public static final int TYPE_CONTACT = 2;

    public String  name;
    public String  number;
    public int     imageId;
    public String  imageUri;
    public int     defaultType;
    public int     type;

    public String contactKey;

    CircleItem(String name, int imageId, int type, int defaultType) {
        this.name = name;
        this.imageId = imageId;
        this.type = type;

        if (type <= 0)
            this.type = TYPE_DEFAULT;

        if(isDefault())
            setDefaultType(defaultType);
    }

    CircleItem(String name, String imageUri) {
        this.name = name;
        this.imageUri = imageUri;
    }

    public void setDefaultType(int defaultType) {
        this.defaultType = defaultType;
    }

    public boolean isDefault() {
        return this.type == TYPE_DEFAULT;
    }

    public boolean hasContact() {
        return this.type == TYPE_CONTACT;
    }

    public String getDefaultTypeName() {
        String defaultKey = null;

        switch (defaultType) {
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

    public void addContact(int id, Activity activity) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        Uri dataUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Data.CONTENT_DIRECTORY);

        Cursor dataCursor = activity.getContentResolver().query(
                dataUri, null,
                ContactsContract.Data.MIMETYPE+"=?",
                new String[] {ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE},
                null
        );

        if(dataCursor.getCount() <= 0)
            return;

        
}
