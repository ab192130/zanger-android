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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CallLogListFragment extends Fragment{

    Activity mActivity;
    RecyclerView callLogRecyclerView;
    CallLogAdapter callLogAdapter;
    Cursor cursorCallLog;
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
        cursorCallLog = getCursor();

        listCallLog = new ArrayList<>();

        try {
//            while(callLogsCursor.moveToNext()) {
            while(cursorCallLog.moveToNext()) {

                CallLog callLog = new CallLog(cursorCallLog);

                listCallLog.add(callLog);
            }
        } finally {
            cursorCallLog.close();
        }

        List<CallLog> repeatList = new ArrayList<>();

        int repeatCount = 0;

        for (int i = 0; i < listCallLog.size(); i++) {
            CallLog aLog = listCallLog.get(i);
            int position = i + 1;

            Log.e("MYAPP:LIST", "i: " + i + ", n: " + aLog.number + ", d: " + aLog.direction);

            if(listCallLog.size() != position) {
                CallLog nLog = listCallLog.get(i + 1);

                if(aLog.number.equals(nLog.number) && aLog.direction == nLog.direction) {
                    repeatList.add(aLog);
                    repeatCount += 1;
                } else {
                    aLog.repeats = repeatCount + 1;
                    repeatCount = 0;
                    Log.e("MYAPP:REPEATS", "" + aLog.repeats);
                }
            } else {
                aLog.repeats = repeatCount + 1;
                repeatCount = 0;
            }

            Log.e("MYAPP:REPEATLIST", "" + TextUtils.join(",", repeatList));

        }

        if (repeatList.size() > 0) {

            for (CallLog rCallLog : repeatList) {
                listCallLog.remove(rCallLog);
            }

            repeatList.clear();
        }

        return listCallLog;
    }

    private Cursor getCursor() {
        return CallLogHelper.getCallLogs(mActivity, mActivity.getContentResolver());
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
