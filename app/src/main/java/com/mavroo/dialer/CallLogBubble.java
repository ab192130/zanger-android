package com.mavroo.dialer;

class CallLogBubble {
    public static final int TYPE_CALL          = 1;
    public static final int TYPE_MESSAGE       = 2;

    public String number;
    public String date;
    public int type;
    public String duration;
    public int status;
    public int repeats;

    CallLogBubble() {
        this.repeats = 1;
    }
}
