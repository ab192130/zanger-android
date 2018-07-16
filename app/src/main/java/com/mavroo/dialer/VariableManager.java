package com.mavroo.dialer;

import java.util.List;

public class VariableManager {
    private static final VariableManager ourInstance = new VariableManager();

    public static VariableManager getInstance() {
        return ourInstance;
    }

    private VariableManager() {
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

    public static boolean hasSize(List list) {
        return list != null && list.size() > 0;
    }

}
