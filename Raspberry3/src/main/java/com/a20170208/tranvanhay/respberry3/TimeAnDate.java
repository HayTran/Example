package com.a20170208.tranvanhay.respberry3;

import android.os.Handler;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by Tran Van Hay on 3/27/2017.
 */

public class TimeAnDate {
    private static String currentTimeOffline = "";
    // Instance for a handler
    private Handler mHandler = new Handler();
    private Runnable fetchCurrentTime = new Runnable() {
        @Override
        public void run() {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss --- dd/MM/yyyy");
            format.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
            new Firebase().sendTimestampToFirebase(format.format(date));
            currentTimeOffline = format.format(date) + "";
            mHandler.postDelayed(fetchCurrentTime,1000);
        }
    };
    public void showCurrentTime(){
        mHandler.post(fetchCurrentTime);
    }
}
