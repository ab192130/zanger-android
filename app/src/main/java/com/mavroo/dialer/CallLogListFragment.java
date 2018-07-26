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
import java.util.List;
import java.util.Random;

public class CallLogListFragment extends Fragment{

    Activity mActivity;
    RecyclerView callLogRecyclerView;
    CallLogAdapter callLogAdapter;
    CallLogAdapterNew callLogAdapterNew;
    Cursor cursorCallLog;
    NestedScrollView nestedScrollView;

    @Deprecated
    private List<CallLog>  listCallLog;

    private List<CallLogNew> listCallLogNew;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();

        View v = inflater.inflate(R.layout.fragment_call_log_list, container, false);

        callLogRecyclerView = v.findViewById(R.id.recycler_view);

        listCallLogNew = getListNew2();

        nestedScrollView = mActivity.findViewById(R.id.nested_scroll_view);

        if (!showListNew()) return null;

        return v;
    }

    private boolean showListNew() {
        if(!VariableManager.hasSize(listCallLogNew))
            return false;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mActivity);
        callLogRecyclerView.setLayoutManager(layoutManager);
        callLogRecyclerView.setItemAnimator(new DefaultItemAnimator());

        callLogAdapterNew = new CallLogAdapterNew(mActivity, listCallLogNew);
        //callLogRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        callLogRecyclerView.setAdapter(callLogAdapterNew);
        callLogAdapterNew.notifyDataSetChanged();

        // scroll to the end
        scrollToEnd();

        return true;
    }

    private List<CallLogNew> getListNew2() {
        cursorCallLog = getCursor();

        listCallLogNew = new ArrayList<>();

        try {

            int lastIndex = 0;

            while(cursorCallLog.moveToNext()) {
                String key       = cursorCallLog.getString(cursorCallLog.getColumnIndex(android.provider.CallLog.Calls._ID));
                String number    = cursorCallLog.getString(cursorCallLog.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                int status       = cursorCallLog.getInt(cursorCallLog.getColumnIndex(android.provider.CallLog.Calls.TYPE));
                String duration  = cursorCallLog.getString(cursorCallLog.getColumnIndex(android.provider.CallLog.Calls.DURATION));
                String date      = cursorCallLog.getString(cursorCallLog.getColumnIndex(android.provider.CallLog.Calls.DATE));
                date             = DateHelper.getInstance().getDateString(Long.valueOf(date));
                int type         = CallLogBubble.TYPE_CALL;

                int direction = CallLogNew.DIRECTION_INCOMING;

                if (status == android.provider.CallLog.Calls.OUTGOING_TYPE)
                    direction = CallLogNew.DIRECTION_OUTGOING;

                CallLogBubble bubble = new CallLogBubble();
                bubble.number   = number;
                bubble.date     = date;
                bubble.status   = status;
                bubble.type     = CallLogBubble.TYPE_CALL;
                bubble.duration = duration;
                bubble.keys.add(key);
                CallLogNew callLog;

                /*if(cursorCallLog.getPosition() > 0
                        && lastIndex < listCallLogNew.size()
                        &&

                        ((listCallLogNew.get(lastIndex).actorName.equals(number)
                        && listCallLogNew.get(lastIndex).direction == CallLogNew.DIRECTION_INCOMING) ||
                        (listCallLogNew.get(lastIndex).direction == CallLogNew.DIRECTION_OUTGOING
                         && direction == CallLogNew.DIRECTION_OUTGOING))) {*/
                if(cursorCallLog.getPosition() > 0
                        && lastIndex < listCallLogNew.size()
                        && listCallLogNew.get(lastIndex).actorName.equals(number)
                        && listCallLogNew.get(lastIndex).direction == direction
                        && direction == CallLogNew.DIRECTION_INCOMING) {
                    callLog = listCallLogNew.get(lastIndex);
                } else if (cursorCallLog.getPosition() > 0
                        && lastIndex < listCallLogNew.size()
                        && listCallLogNew.get(lastIndex).direction == direction
                        && direction == CallLogNew.DIRECTION_OUTGOING) {
                    callLog = listCallLogNew.get(lastIndex);
                } else {
                    callLog = new CallLogNew();
                    callLog.direction = direction;
                    callLog.actorName = number;

                    listCallLogNew.add(callLog);
                    lastIndex = listCallLogNew.indexOf(callLog);

                    // @todo: add contact key as an actor key, if direction is left ;
                }

                if(direction == CallLogNew.DIRECTION_INCOMING)
                    bubble.setMiniContactData(mActivity);
                else
                    bubble.setContactData(mActivity);

                callLog.addBubble(bubble);
//                lastIndex = listCallLogNew.indexOf(callLog);

                if(!callLog.hasContact() && direction == CallLogNew.DIRECTION_INCOMING)
                    callLog.setActorContactData(mActivity); //@todo
            }
        } finally {
            cursorCallLog.close();
        }

        return listCallLogNew;
    }

    private Cursor getCursor() {
        return CallLogHelper.getCallLogs(mActivity, mActivity.getContentResolver());
    }

    private void scrollToEnd() {

        if(!VariableManager.hasSize(listCallLogNew))
            return;

        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        callLogRecyclerView.scrollToPosition(listCallLogNew.size() - 1);
    }

    @Deprecated
    private List<CallLogNew> getListNew() {
        listCallLogNew = new ArrayList<>();

        for (int j = 0; j < 7; j++) {
            CallLogNew callLog = new CallLogNew();
            callLog.setActorName("Burhan Aghazada");

            Random random = new Random();
            int max = CallLogNew.DIRECTION_OUTGOING;
            int min = CallLogNew.DIRECTION_INCOMING;
            callLog.direction = random.nextInt(max) + min;

            List<CallLogBubble> listBubbles = new ArrayList<>();

            for (int i = 0; i < 2; i++) {
//            for (int i = 0; i < (random.nextInt(6) + 1); i++) {
                CallLogBubble bubble = new CallLogBubble();
                bubble.number = "0505005091";
                bubble.date = "2h ago";

                if(callLog.direction == CallLogNew.DIRECTION_OUTGOING)
                    bubble.status = android.provider.CallLog.Calls.OUTGOING_TYPE;
                else
                    bubble.status = (random.nextInt(3) + 1);

                bubble.type = CallLogBubble.TYPE_CALL;

                listBubbles.add(bubble);
            }

            callLog.setBubbles(listBubbles);

            listCallLogNew.add(callLog);
        }

        return listCallLogNew;
    }

    @Deprecated
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

    @Deprecated
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
}
