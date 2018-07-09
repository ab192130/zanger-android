package com.mavroo.dialer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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

public class CallLogListFragment extends Fragment{

    Activity mActivity;
    RecyclerView callLogRecyclerView;
    CallLogCursorAdapter callLogAdapter;
    Cursor callLogsCursor;
    NestedScrollView nestedScrollView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();

        View v = inflater.inflate(R.layout.fragment_call_log_list, container, false);

        callLogRecyclerView = v.findViewById(R.id.recycler_view);
        callLogsCursor = CallLogHelper.getCallLogs(mActivity, mActivity.getContentResolver());


        nestedScrollView =         mActivity.findViewById(R.id.nested_scroll_view);

        if(!CallLogHelper.hasLogs(callLogsCursor))
            return null;

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(mActivity);
        callLogRecyclerView.setLayoutManager(layoutManager);
        callLogRecyclerView.setItemAnimator(new DefaultItemAnimator());

        callLogAdapter = new CallLogCursorAdapter(mActivity, callLogsCursor);
        //callLogRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        callLogRecyclerView.setAdapter(callLogAdapter);
        callLogAdapter.notifyDataSetChanged();

        // scroll to the end
        scrollToEnd();

        return v;
    }

    private void scrollToEnd() {

        if(!CallLogHelper.hasLogs(callLogsCursor))
            return;

        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        callLogRecyclerView.scrollToPosition(callLogsCursor.getCount() - 1);
    }
}
