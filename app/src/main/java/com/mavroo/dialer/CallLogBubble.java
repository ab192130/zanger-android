package com.mavroo.dialer;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

class CallLogBubble {
    public static final int TYPE_CALL          = 1;
    public static final int TYPE_MESSAGE       = 2;

    public String number;
    public String date;
    public int type;
    public String duration;
    public int status;
    public int repeats;
    public int device;

    public String contactKey;
    public String contactName;
    public String contactPhoto;
    public int contactDevice;

    public List<String> keys;

    CallLogBubble() {
        this.repeats = 1;
        this.keys = new ArrayList<>();
    }

    public void setContactData(Context context) {
        String number = this.number;

        final Cursor contactCursor = ContactHelper.getByPhoneNumber(context, number);

        try {
            if(!contactCursor.moveToFirst())
                return;

            final String contactKey = contactCursor.getString(contactCursor
                    .getColumnIndex(ContactsContract.PhoneLookup.LOOKUP_KEY));
            final String contactName = contactCursor.getString(contactCursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            String contactPhoto = contactCursor.getString(contactCursor
                    .getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI));
            int contactType = contactCursor.getInt(contactCursor
                    .getColumnIndex(ContactsContract.PhoneLookup.TYPE));

            this.contactKey = contactKey;
            this.contactName = contactName;
            this.contactPhoto = contactPhoto;
            this.contactDevice = contactType;
            //holder.setContactData();
        } finally {
            if (contactCursor != null)
                contactCursor.close();
        }
    }

    public void setMiniContactData(Context context) {
        String number = this.number;

        final Cursor contactCursor = ContactHelper.getDeviceByPhoneNumber(context, number);

        try {
            if(!contactCursor.moveToFirst())
                return;

            final String contactKey = contactCursor.getString(contactCursor
                    .getColumnIndex(ContactsContract.PhoneLookup.LOOKUP_KEY));
            int contactType = contactCursor.getInt(contactCursor
                    .getColumnIndex(ContactsContract.PhoneLookup.TYPE));

            this.contactKey = contactKey;
            this.contactDevice = contactType;
        } finally {
            if (contactCursor != null)
                contactCursor.close();
        }
    }

    public boolean hasContact() {
        return (this.contactKey != null);
    }

    public String getDurationText() {
        return this.duration + " sec";
    }

    public String getRepeatsText() {
        return "(" + this.repeats + ")";
    }

    // @todo
    public String getDeviceName() {
        String name = null;

        switch (contactDevice) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                name = "mobile";
                break;
            default:
                name = "other";
                break;
        }

        return name;
    }

    // @todo
    public int getDeviceIconRes() {
        int name;

        switch (contactDevice) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                name = R.drawable.image_icon_mobile;
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                name = R.drawable.image_icon_home;
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                name = R.drawable.image_icon_building;
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                name = R.drawable.image_icon_fax;
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                name = R.drawable.image_icon_fax;
                break;
            default:
                name = -1;
                break;
        }

        return name;
    }
}
