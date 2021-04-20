package com.mavroo.dialer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;

public class ContactHelper {
    private static final ContactHelper ourInstance = new ContactHelper();
    private static final int CODE_REQUEST_PERMISSION_CONTACTS_READ = 209;

    private String[] lookupProjection;
    private String[] contactsProjection;

    public static ContactHelper getInstance() {
        return ourInstance;
    }

    private ContactHelper() {
         lookupProjection = new String[] {
                ContactsContract.PhoneLookup.LOOKUP_KEY,
                ContactsContract.PhoneLookup.TYPE,
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI,
                ContactsContract.PhoneLookup.PHOTO_URI};

         contactsProjection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI};
    }

    private static boolean checkPermission(Context context) {
        if(ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            String[] permissions = new String[] {Manifest.permission.READ_CONTACTS};
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    permissions,
                    CODE_REQUEST_PERMISSION_CONTACTS_READ);

            return true;
        }
        return false;
    }

    public static Cursor getByPhoneNumber(Context context, String phoneNumber) {

        if (checkPermission(context)) return null;

        if(phoneNumber != null && phoneNumber.length() > 0) {
            Uri contactsUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            String[] projection = new String[] {
                    ContactsContract.PhoneLookup.LOOKUP_KEY,
                    ContactsContract.PhoneLookup.TYPE,
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI,
                    ContactsContract.PhoneLookup.PHOTO_URI};
            Cursor cursor = context.getContentResolver()
                    .query(contactsUri, projection, null, null, null);

            if(cursor != null)
                return cursor;
        }

        return null;
    }

    public static Cursor getDeviceByPhoneNumber(Context context, String phoneNumber) {

        if (checkPermission(context)) return null;

        if(phoneNumber != null && phoneNumber.length() > 0) {
            Uri contactsUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            String[] projection = new String[] {
                    ContactsContract.PhoneLookup.LOOKUP_KEY,
                    ContactsContract.PhoneLookup.TYPE
            };
            Cursor cursor = context.getContentResolver()
                    .query(contactsUri, projection, null, null, null);

            if(cursor != null)
                return cursor;
        }

        return null;
    }

    private Cursor get(Context context, Uri uri, String[] projection, String selection, String[] args, String order) {
        if (checkPermission(context)) return null;

        if(uri == null)
            uri = ContactsContract.Contacts.CONTENT_URI;

        if(projection == null)
            projection = contactsProjection;

        Cursor cursor = context.getContentResolver()
                .query(uri, projection, selection, args, order);

        if(cursor == null)
            return null;

        return cursor;
    }

    private Cursor getByPhoneNumber(Context context, String phoneNumber, String[] projection,
                                           String selection, String[] args, String order) {

        if(phoneNumber == null || phoneNumber.length() <= 0)
            return null;

        Uri contactsUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        return get(context, contactsUri, projection, selection, args, order);
    }

    public Cursor getFavourites(Context context) {
        return get(context, null, null, "starred=?",
                new String[] {"1"}, null);
    }
}
