package com.doodeec.eddy;

/**
 * @author Dusan Bartos
 */
//@SuppressWarnings("unused")
public interface IEddyManager {

    String WAKELOCK_TAG = IEddyManager.class.getSimpleName();

    void startScanning();

    void stopScanning();

    void startWatchDog(String macUrl, IEddyListener listener);

    void stopWatchDog(String macUrl, IEddyListener listener);
}
