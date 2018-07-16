package com.mavroo.dialer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CallLogManager {

    public static final int LAYOUT_FRAME_CALL_LOG = R.id.layout_frame_call_log;
    private Context mContext;
    private Activity mActivity;
    private Cursor callLogsCursor;
    private RecyclerView callLogRecyclerView;
    private CallLogListFragment callLogListFragment;
    private CallLogNoResultFragment callLogNoResultFragment;
    private List<CallLog> listCallLog;

    CallLogManager(Context context) {
        mContext = context;
        mActivity = (Activity) mContext;

        loadCallLogs();
    }

    public void loadCallLogs() {
        FragmentTransaction fragmentTransaction = mActivity.getFragmentManager()
                .beginTransaction();

        // @todo: learn call log count by querying directly the count.
        callLogsCursor = CallLogHelper.getCallLogs(mContext, mActivity.getContentResolver());

        if(callLogsCursor != null && callLogsCursor.getCount() < 1) {
            showNoCallLogsFragment(fragmentTransaction);

            return;
        }

        showCallLogsFragment(fragmentTransaction);
    }

    private void showCallLogsFragment(FragmentTransaction fragmentTransaction) {
        callLogListFragment = new CallLogListFragment();
        fragmentTransaction
                .replace(LAYOUT_FRAME_CALL_LOG, callLogListFragment)
                .commit();
    }

    private void showNoCallLogsFragment(FragmentTransaction fragmentTransaction) {
        callLogNoResultFragment = new CallLogNoResultFragment();
        fragmentTransaction.replace(LAYOUT_FRAME_CALL_LOG, callLogNoResultFragment);
        fragmentTransaction.commit();
    }
}
