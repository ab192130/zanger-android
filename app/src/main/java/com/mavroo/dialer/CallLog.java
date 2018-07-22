package com.mavroo.dialer;

import android.database.Cursor;

import java.util.List;

public class CallLog {
    public static final int TYPE_CALL          = 1;
    public static final int TYPE_MESSAGE       = 2;

    public static final int DIRECTION_INCOMING = 1;
    public static final int DIRECTION_OUTGOING = 2;

    public String number;
    public String date;
    public int type;
    public String duration;
    public int direction;
    public int status;
    public int repeats;

    public List<CallLogBubble> items;
    public String actorName;
    public String actorNumber;

    CallLog(String number, int direction, String date) {
        this.number = number;
        this.direction = direction;
        this.date = DateHelper.getInstance().getDateString(Long.valueOf(date));
        this.type = TYPE_CALL;
    }

    CallLog(Cursor cursor) {
        this.number    = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
        this.status    = cursor.getInt(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
        this.date      = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
        this.date      = DateHelper.getInstance().getDateString(Long.valueOf(this.date));
        this.type      = TYPE_CALL;

        this.direction = CallLog.DIRECTION_INCOMING;

        if (this.status == android.provider.CallLog.Calls.OUTGOING_TYPE)
            this.direction = CallLog.DIRECTION_OUTGOING;
    }

    CallLog() {

    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber(){
        return number;
    }

    public void setdate(String date) {
        this.date = date;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setType(int type) {
        this.type = type;
    }
}
