package com.mavroo.dialer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class CallLogManager {

    private Context mContext;
    private Activity mActivity;
    private Cursor callLogsCursor;
    private RecyclerView callLogRecyclerView;
    private CallLogCursorAdapter callLogAdapter;
    private CallLogListFragment callLogListFragment;
    private CallLogNoResultFragment callLogNoResultFragment;

    CallLogManager(Context context) {
        mContext = context;
        mActivity = (Activity) mContext;

        loadCallLogs();
    }

    public void loadCallLogs() {
        FragmentTransaction fragmentTransaction = mActivity.getFragmentManager()
                .beginTransaction();

        callLogsCursor = CallLogHelper.getCallLogs(mContext, mActivity.getContentResolver());

        if(!hasLogs()) {
            callLogNoResultFragment = new CallLogNoResultFragment();
            fragmentTransaction.replace(R.id.layout_frame_call_log, callLogNoResultFragment);
            fragmentTransaction.commit();

            return;
        }

        callLogListFragment = new CallLogListFragment();
        fragmentTransaction
                .replace(R.id.layout_frame_call_log, callLogListFragment)
                .commit();
    }

    private boolean hasLogs() {
        return CallLogHelper.hasLogs(callLogsCursor);
    }
}
