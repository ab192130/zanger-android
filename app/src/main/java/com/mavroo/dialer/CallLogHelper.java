package com.mavroo.dialer;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContentResolverCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

public class CallLogHelper {
    private static final CallLogHelper ourInstance = new CallLogHelper();
    private static final int CODE_REQUEST_PERMISSION_CALLLOG_READ  = 109;
    private static final int CODE_REQUEST_PERMISSION_CALLLOG_WRITE = 110;

    public static CallLogHelper getInstance() {
        return ourInstance;
    }

    private CallLogHelper() {
    }

    public static Cursor getCallLogs(Context context, ContentResolver contentResolver) {
        Activity activity = (Activity) context;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            String[] permissions = new String[]{Manifest.permission.READ_CALL_LOG};
            ActivityCompat.requestPermissions(activity, permissions, CODE_REQUEST_PERMISSION_CALLLOG_READ);

            return null;
        }

        String sortOrder = CallLog.Calls.DATE + " ASC";
        Uri callLogUri = CallLog.Calls.CONTENT_URI; //: content://call_log/calls
        return contentResolver.query(callLogUri, null, null, null, sortOrder);
    }

    public static int getCallLogCount(ContentResolver contentResolver) {
        // @todo: get call log count from here!
        return 0;

        /*Uri uriCallLogs = CallLog.Calls.CONTENT_URI;
        String[] projection = new String[] {"COUNT(*) AS count"};
        Cursor cursor = contentResolver
                .query(uriCallLogs, projection, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) return 0;

        return cursor.getInt(0);*/
    }

    public static Cursor getContactByPhoneNumber(Context context, String phoneNumber) {

        if (phoneNumber != null && phoneNumber.length() > 0) {
            Uri contactsUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            String[] projection = new String[]{
                    ContactsContract.PhoneLookup.LOOKUP_KEY,
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI};
            Cursor cursor = context.getContentResolver()
                    .query(contactsUri, projection, null, null, null);

            if (cursor != null) {
                return cursor;
            }
        }

        return null;
    }

    public static void showContact(Context context, String contactKey) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, String.valueOf(contactKey));
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static boolean hasLogs(List<com.mavroo.dialer.CallLog> callLogs) {
        return callLogs != null && callLogs.size() > 0;
    }

    public static void deleteById(Activity activity, String id) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(
                    activity,
                    new String[] {Manifest.permission.WRITE_CALL_LOG},
                    CODE_REQUEST_PERMISSION_CALLLOG_WRITE);
            return;
        }

        String query = "+_ID=" + id;

        activity.getContentResolver().delete(CallLog.Calls.CONTENT_URI, query, null);
    }
}
