package com.mavroo.dialer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.util.Log;

public class CallManager {
    private static final CallManager ourInstance = new CallManager();

    public static CallManager getInstance() {
        return ourInstance;
    }

    private CallManager() {
    }

    @Deprecated
    public void makeCall(Context context, String number) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("MYAPP", "You need to request for permission!");
            return;
        }

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        context.startActivity(intent);
    }
}
