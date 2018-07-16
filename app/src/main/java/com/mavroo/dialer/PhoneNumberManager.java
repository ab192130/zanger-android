package com.mavroo.dialer;

import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

class PhoneNumberManager {
    private static final String DEFAULT_REGION = "AZ";
    private PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
//    private PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    PhoneNumberManager() {
    }

    public String format(String number) {
        String formattedNumber = number;

        try {
            Phonenumber.PhoneNumber swissNumberProto = getNumberParsed(number);
            Log.e("MYAPP:NUMBER_TYPE", String.valueOf(phoneUtil.getNumberType(swissNumberProto)));

            boolean isValid = phoneUtil.isValidNumber(swissNumberProto);
            if(!isValid) {
                return number;
            }

            formattedNumber = phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        return formattedNumber;
    }

    private Phonenumber.PhoneNumber getNumberParsed(String number) throws NumberParseException {
        return phoneUtil.parse(number, DEFAULT_REGION);
    }

    public static boolean isRunnable(String number) {
        try {
            return VariableManager.getLastChar(number).equals("#");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return false;
    }
}
