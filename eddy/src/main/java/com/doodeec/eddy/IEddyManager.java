package com.doodeec.eddy;

/**
 * @author Dusan Bartos
 */
//@SuppressWarnings("unused")
public interface IEddyManager {

    void startScanning();

    void stopScanning();

    void startWatchDog(IEddyListener listener);

    void stopWatchDog(IEddyListener listener);
}
