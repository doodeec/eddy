package com.doodeec.eddy;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.util.Log;

/**
 * @author Dusan Bartos
 */
@TargetApi(VERSION_CODES.KITKAT_WATCH)
public class EddyManagerImpl20 extends EddyManager implements BluetoothAdapter.LeScanCallback {

    protected EddyManagerImpl20(Context context) {
        super(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void startScanning() {
        mScanActive = true;
        mBTAdapter.startLeScan(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void stopScanning() {
        mBTAdapter.stopLeScan(this);
        mScanActive = false;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.d(getClass().getSimpleName(), "onLeScan");
        foundDevice(device.getAddress());
    }
}
