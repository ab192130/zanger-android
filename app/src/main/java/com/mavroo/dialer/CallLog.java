package com.mavroo.dialer;

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
    public int repeats;

    public CallLog(String number) {
        this.number = number;
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
