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

    public static ContactHelper getInstance() {
        return ourInstance;
    }

    private ContactHelper() {
    }

    public static Cursor getByPhoneNumber(Context context, String phoneNumber) {

        if(ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            String[] permissions = new String[] {Manifest.permission.READ_CONTACTS};
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    permissions,
                    CODE_REQUEST_PERMISSION_CONTACTS_READ);

            return null;
        }

        if(phoneNumber != null && phoneNumber.length() > 0) {
            Uri contactsUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            String[] projection = new String[] {
                    ContactsContract.PhoneLookup.LOOKUP_KEY,
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI,
                    ContactsContract.PhoneLookup.PHOTO_URI};
            Cursor cursor = context.getContentResolver()
                    .query(contactsUri, projection, null, null, null);

            if(cursor != null) {
                return cursor;
            }
        }

        return null;
    }
}
