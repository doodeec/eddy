package com.doodeec.eddy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

/**
 * @author Dusan Bartos
 */
//@SuppressWarnings("unused")
public class Eddy {

    Context mContext;
    BluetoothManager mBTManager;
    BluetoothAdapter mBTAdapter;
    IEddyManager mEddyManager;

    public Eddy(Context context) {
        mContext = context;
        mBTManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBTAdapter = mBTManager.getAdapter();

        initManager();
    }

    void initManager() {
        mEddyManager = EddyManagerFactory.getManager(mBTAdapter, VERSION.SDK_INT);
    }

    public IEddyManager getManager() {
        return mEddyManager;
    }

    private static class EddyManagerFactory {
        static IEddyManager getManager(BluetoothAdapter BTAdapter, int versionCode) {
            switch (versionCode) {

                case VERSION_CODES.JELLY_BEAN_MR2:
                case VERSION_CODES.KITKAT:
                case VERSION_CODES.KITKAT_WATCH:
                    return new EddyManagerImpl20(BTAdapter);

                case VERSION_CODES.LOLLIPOP:
                case VERSION_CODES.LOLLIPOP_MR1:
                case VERSION_CODES.M:
                    return new EddyManagerImplL(BTAdapter);

                default:
                    return null;
            }
        }
    }
}
