package com.mavroo.dialer;

import android.text.format.DateUtils;

public class DateHelper {
    private static final DateHelper ourInstance = new DateHelper();

    public static DateHelper getInstance() {
        return ourInstance;
    }

    private DateHelper() {
    }

    public String getDateString(Long date) {
        long now = System.currentTimeMillis();
        return (String) DateUtils.getRelativeTimeSpanString(date, now, DateUtils.FORMAT_ABBREV_TIME);
    }

    public String getTimeDiff(long secondsTimeDiff)
    {
        long secondsInOneDay = 84600;
        int maxDaysAgo = 10;

        if ( secondsTimeDiff < secondsInOneDay)
        {
            return "today";
        }
        else if ( secondsTimeDiff < 2*secondsInOneDay)
        {
            return "yesterday";
        }
        else if ( secondsTimeDiff < maxDaysAgo*secondsInOneDay)
        {
            int days = (int) (secondsTimeDiff / secondsInOneDay);
            return days + " days ago";
        }
        else
        {
            //use normal DateUtils logic here...
            return "....";
        }
    }
}
