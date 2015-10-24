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

    /**
     * General listeners
     * All listeners without specific mac address
     * These listeners are notified about every enter/leave event with every device address
     * so make sure you don't do any duplicate jobs when these are used
     */
    protected List<IEddyListener> mGeneralListeners = new ArrayList<>();
    /**
     * Specific listeners
     * All listeners with specific mac address they listen to
     * These listeners are notified only when event occurs on specific mac address
     */
    protected Map<String, List<IEddyListener>> mListenerMap = new HashMap<>();

    protected EddyManager(Context context) {
        mBTManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mPowManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mBTAdapter = mBTManager.getAdapter();
        mWL = mPowManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG);

        //TODO move alarm manager elsewhere (startWD method?)
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
        Log.d(getClass().getSimpleName(), "startWatchDog - " + macAddr);
        if (macAddr == null) {
            mGeneralListeners.add(listener);
        } else {
            if (!mListenerMap.containsKey(macAddr)) {
                mListenerMap.put(macAddr, new ArrayList<IEddyListener>());
            }

            mListenerMap.get(macAddr).add(listener);
        }

        if (!mScanActive) {
            startScanning();
        }
    }

    @Override
    public void stopWatchDog(String macAddr, IEddyListener listener) {
        Log.d(getClass().getSimpleName(), "stopWatchDog - " + macAddr);
        if (macAddr == null) {
            mGeneralListeners.remove(listener);
        } else {
            if (mListenerMap.containsKey(macAddr)) {
                mListenerMap.get(macAddr).remove(listener);
                if (mListenerMap.get(macAddr).size() == 0) {
                    mListenerMap.remove(macAddr);
                }
            }
        }

        if (mListenerMap.size() == 0 && mGeneralListeners.size() == 0) {
            stopScanning();
        }
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

    /**
     * Resolves all listeners after scan procedure is finished and before wakelock is released
     */
    private void resolveListeners() {
        List<String> localFound = new ArrayList<>();
        localFound.addAll(mFoundDevices);

        for (String macAddr : mLastFoundDevices) {
            if (!localFound.contains(macAddr)) {
                notifyListeners(macAddr, false);
                notifyGeneralListeners(macAddr, false);
            } else {
                // simplify next for cycle, if address was found in both lists,
                // there won't be any notification anyway
                localFound.remove(macAddr);
            }
        }

        for (String macAddr : localFound) {
            if (!mLastFoundDevices.contains(macAddr)) {
                notifyListeners(macAddr, true);
                notifyGeneralListeners(macAddr, true);
            }
        }
    }

    /**
     * Notifies all specific listeners
     *
     * @param macAddr mac address of BT device
     * @param entered true if area was entered, false if area was left
     */
    private void notifyListeners(String macAddr, boolean entered) {
        internalNotify(macAddr, entered, mListenerMap.get(macAddr));
    }

    /**
     * Notifies all general listeners
     *
     * @param macAddr mac address of BT device
     * @param entered true if area was entered, false if area was left
     *
     * @see #mGeneralListeners
     */
    private void notifyGeneralListeners(String macAddr, boolean entered) {
        internalNotify(macAddr, entered, mGeneralListeners);
    }

    /**
     * Loops through listeners and notifies each of them about entering/leaving the area
     *
     * @param macAddr   mac address of BT device
     * @param entered   true if area was entered, false if area was left
     * @param listeners list of listeners
     */
    private void internalNotify(String macAddr, boolean entered, List<IEddyListener> listeners) {
        for (IEddyListener eddyListener : listeners) {
            if (entered) {
                eddyListener.onEnteredArea(macAddr);
            } else {
                eddyListener.onExitedArea(macAddr);
            }
        }
    }
}
