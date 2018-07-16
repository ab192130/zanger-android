package com.mavroo.dialer;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CallLogListFragment extends Fragment{

    Activity mActivity;
    RecyclerView callLogRecyclerView;
    CallLogAdapter callLogAdapter;
    Cursor callLogsCursor;
    NestedScrollView nestedScrollView;
    private List<CallLog> listCallLog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();

        View v = inflater.inflate(R.layout.fragment_call_log_list, container, false);

        callLogRecyclerView = v.findViewById(R.id.recycler_view);

        listCallLog = getList();

        nestedScrollView = mActivity.findViewById(R.id.nested_scroll_view);

        if (!showList()) return null;

        return v;
    }

    private boolean showList() {
        if(!VariableManager.hasSize(listCallLog))
            return false;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mActivity);
        callLogRecyclerView.setLayoutManager(layoutManager);
        callLogRecyclerView.setItemAnimator(new DefaultItemAnimator());

        callLogAdapter = new CallLogAdapter(mActivity, listCallLog);
        //callLogRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        callLogRecyclerView.setAdapter(callLogAdapter);
        callLogAdapter.notifyDataSetChanged();

        // scroll to the end
        scrollToEnd();

        return true;
    }

    private List<CallLog> getList() {
        callLogsCursor = CallLogHelper.getCallLogs(mActivity, mActivity.getContentResolver());

        listCallLog = new ArrayList<>();

        try {
            while(callLogsCursor.moveToNext()) {
                CallLog callLog = new CallLog();

                String number    = callLogsCursor.getString(callLogsCursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                String duration  = callLogsCursor.getString(callLogsCursor.getColumnIndex(android.provider.CallLog.Calls.DURATION));
                String date      = callLogsCursor.getString(callLogsCursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
                int    direction = callLogsCursor.getInt(callLogsCursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
                date             = DateHelper.getInstance().getDateString(Long.valueOf(date));

                callLog.setNumber(number);
                callLog.setdate(date);
                callLog.setType(CallLog.TYPE_CALL);

                callLog.setDirection(CallLog.DIRECTION_INCOMING);

                if (direction == android.provider.CallLog.Calls.OUTGOING_TYPE)
                    callLog.setDirection(CallLog.DIRECTION_OUTGOING);

                listCallLog.add(callLog);
            }
        } finally {
            callLogsCursor.close();
        }

        return listCallLog;
    }

    private void scrollToEnd() {

        if(!VariableManager.hasSize(listCallLog))
            return;

        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        callLogRecyclerView.scrollToPosition(listCallLog.size() - 1);
    }
}
