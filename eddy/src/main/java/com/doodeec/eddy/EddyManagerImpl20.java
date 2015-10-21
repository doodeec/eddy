package com.doodeec.eddy;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build.VERSION_CODES;
import android.util.Log;

/**
 * @author Dusan Bartos
 */
@TargetApi(VERSION_CODES.KITKAT_WATCH)
public class EddyManagerImpl20 implements IEddyManager,
        BluetoothAdapter.LeScanCallback {

    BluetoothAdapter mBTAdapter;

    protected EddyManagerImpl20(BluetoothAdapter BTAdapter) {
        mBTAdapter = BTAdapter;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void startScanning() {
        mBTAdapter.startLeScan(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void stopScanning() {
        mBTAdapter.stopLeScan(this);
    }

    @Override
    public void startWatchDog(IEddyListener listener) {
        //TODO
    }

    @Override
    public void stopWatchDog(IEddyListener listener) {
        //TODO
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        //TODO
        Log.d(getClass().getSimpleName(), "onLeScan");
    }
}
