package com.mavroo.dialer;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CallLogNew {
    public static final int DIRECTION_INCOMING = 1;
    public static final int DIRECTION_OUTGOING = 2;

    private List<CallLogBubble> bubbles;
    String actorName;
    String actorNumber;
    public int direction;

    CallLogNew() {
        this.bubbles = new ArrayList<>();
    }

    CallLogNew(Cursor cursor) {
        int status = cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));

        this.direction = CallLog.DIRECTION_INCOMING;

        if (status == android.provider.CallLog.Calls.OUTGOING_TYPE)
            this.direction = CallLog.DIRECTION_OUTGOING;
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
