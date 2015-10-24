package com.doodeec.eddy;

import android.annotation.TargetApi;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import java.util.List;

/**
 * @author Dusan Bartos
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class EddyManagerImplL extends EddyManager {

    final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(getClass().getSimpleName(), "onScanResult");
            foundDevice(result.getDevice().getAddress());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d(getClass().getSimpleName(), "onBatchScanResults");
            for (ScanResult result: results) {
                foundDevice(result.getDevice().getAddress());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d(getClass().getSimpleName(), "onScanFailed - " + errorCode);
        }
    };

    protected EddyManagerImplL(Context context) {
        super(context);
    }

    @Override
    public void startScanning() {
        mScanActive = true;
        mBTAdapter.getBluetoothLeScanner().startScan(mScanCallback);
    }

    @Override
    public void stopScanning() {
        mBTAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
        mScanActive = false;
    }
}
