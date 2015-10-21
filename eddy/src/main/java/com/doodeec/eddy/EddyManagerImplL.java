package com.doodeec.eddy;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import java.util.List;

/**
 * @author Dusan Bartos
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class EddyManagerImplL extends ScanCallback implements IEddyManager {

    BluetoothAdapter mBTAdapter;

    protected EddyManagerImplL(BluetoothAdapter BTAdapter) {
        mBTAdapter = BTAdapter;
    }

    @Override
    public void startScanning() {
        mBTAdapter.getBluetoothLeScanner().startScan(this);
    }

    @Override
    public void stopScanning() {
        mBTAdapter.getBluetoothLeScanner().stopScan(this);
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
    public void onScanResult(int callbackType, ScanResult result) {
        //TODO
        Log.d(getClass().getSimpleName(), "onScanResult");
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        //TODO
        Log.d(getClass().getSimpleName(), "onBatchScanResults");
    }

    @Override
    public void onScanFailed(int errorCode) {
        //TODO
        Log.d(getClass().getSimpleName(), "onScanFailed");
    }
}
