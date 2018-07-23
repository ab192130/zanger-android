package com.mavroo.dialer;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CallLogNew {
    public static final int DIRECTION_INCOMING = 1;
    public static final int DIRECTION_OUTGOING = 2;

    public static final int TYPE_LOG  = 1;
    public static final int TYPE_DATE = 2;

    private List<CallLogBubble> bubbles;
    String actorName;
    String actorNumber;
    public int direction;

    String contactKey;
    String contactName;
    String contactPhoto;

    CallLogNew() {
        this.bubbles = new ArrayList<>();
    }

    public void setActorContactData(Context context) {
        if(this.bubbles.size() <= 0)
            return;

        if(this.direction == CallLogNew.DIRECTION_OUTGOING)
            return;

        String number = this.bubbles.get(0).number;

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

    public List<CallLogBubble> getBubbles() {

        CallLogBubble aBubble;
        CallLogBubble nBubble;

        int offset = 0;
        int repeats = 0;
        while (offset < bubbles.size()) {
            aBubble = bubbles.get(offset);

            if((offset + 1) < bubbles.size()) {
                nBubble = bubbles.get(offset + 1);

                if (aBubble.number.equals(nBubble.number)
                        && aBubble.status == nBubble.status
                        && aBubble.type == CallLogBubble.TYPE_CALL
                        && nBubble.type == CallLogBubble.TYPE_CALL) {
                    aBubble.repeats = aBubble.repeats + 1;
                    bubbles.remove(nBubble);
                } else {
                    offset++;
                }
            } else {
                break;
            }

        }

        return bubbles;
    }

    public void setBubbles(List<CallLogBubble> bubbles) {
        this.bubbles = bubbles;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getActorNumber() {
        return actorNumber;
    }

    public void setActorNumber(String actorNumber) {
        this.actorNumber = actorNumber;
    }

    public void addBubble(CallLogBubble bubble) {
        this.bubbles.add(bubble);
    }
}
