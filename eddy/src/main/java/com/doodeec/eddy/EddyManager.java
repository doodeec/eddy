package com.doodeec.eddy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dusan Bartos
 */
public abstract class EddyManager extends BroadcastReceiver implements IEddyManager {

    // threshold for scanning for specific device
//    private static int AREA_THRESHOLD = 1000 * 30;
    private static int AREA_THRESHOLD = 1000 * 5;
    // interval between scanning procedures
//    private static int SCAN_INTERVAL = 1000 * 60 * 2;
    private static int SCAN_INTERVAL = 1000 * 10;

    protected boolean mScanActive = false;
    protected BluetoothManager mBTManager;
    protected PowerManager mPowManager;
    protected AlarmManager mAlarmManager;
    protected PowerManager.WakeLock mWL;

    protected BluetoothAdapter mBTAdapter;

    protected List<String> mLastFoundDevices = new ArrayList<>();
    protected List<String> mFoundDevices;
    protected Handler mHandler = new Handler();
    protected Runnable mScanCB = new Runnable() {
        @Override
        public void run() {
            resolveListeners();
            mLastFoundDevices.clear();
            mLastFoundDevices.addAll(mFoundDevices);
        }
    };

    protected Map<String, List<IEddyListener>> mListenerMap = new HashMap<>();

    protected EddyManager(Context context) {
        mBTManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mPowManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mBTAdapter = mBTManager.getAdapter();
        mWL = mPowManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG);


        Intent intent = new Intent(context, getClass());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        mAlarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                AREA_THRESHOLD,
                SCAN_INTERVAL,
                alarmIntent);

        Log.i(getClass().getSimpleName(), "alarm set");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mWL.acquire();
        Log.d(getClass().getSimpleName(), "onReceive");

        mFoundDevices = new ArrayList<>();
        mHandler.postDelayed(mScanCB, AREA_THRESHOLD);
    }

    @Override
    public void startWatchDog(String macAddr, IEddyListener listener) {
        Log.d(getClass().getSimpleName(), "startWatchDog");
        if (!mListenerMap.containsKey(macAddr)) {
            mListenerMap.put(macAddr, new ArrayList<IEddyListener>());
        }

        mListenerMap.get(macAddr).add(listener);
        //TODO

        if (!mScanActive) {
            startScanning();
        }
    }

    @Override
    public void stopWatchDog(String macAddr, IEddyListener listener) {
        Log.d(getClass().getSimpleName(), "stopWatchDog");
        if (mListenerMap.containsKey(macAddr)) {
            mListenerMap.get(macAddr).remove(listener);
        }
        //TODO
    }

    protected void foundDevice(String macAddr) {
        Log.d(getClass().getSimpleName(), "foundDevice - " + macAddr);
        if (!mFoundDevices.contains(macAddr)) {
            mFoundDevices.add(macAddr);
        }
    }

    protected void cancelHandler() {
        Log.d(getClass().getSimpleName(), "cancelHandler");
        mHandler.removeCallbacks(mScanCB);
        mWL.release();
    }

    private void resolveListeners() {
        //TODO
    }
}
