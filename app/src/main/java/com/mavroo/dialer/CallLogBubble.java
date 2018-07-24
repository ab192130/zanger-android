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

    public String contactKey;
    public String contactName;
    public String contactPhoto;

    public List<String> keys;
    public String key; // _ID from CallLog Cursor

    CallLogBubble() {
        this.repeats = 1;
        this.keys = new ArrayList<>();
    }

    public void setTargetContactData(Context context) {
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

            this.contactKey = contactKey;
            this.contactName = contactName;
            this.contactPhoto = contactPhoto;
            //holder.setContactData();
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
}
