package com.mavroo.dialer;

public class StringManager {
    private static final StringManager ourInstance = new StringManager();

    public static StringManager getInstance() {
        return ourInstance;
    }

    private StringManager() {
    }

    public static String removeLastChar(String string) {
        int size = string.length();

        if(size < 1)
            return null;

        return string.substring(0, size-1);
    }

    public static String getLastChar(String string) {
        int size = string.length();

        if(size < 1)
            return null;

        return string.substring(size - 1);
    }

}
