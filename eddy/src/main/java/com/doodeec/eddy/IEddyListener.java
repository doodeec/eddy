package com.doodeec.eddy;

/**
 * @author Dusan Bartos
 */
public interface IEddyListener {

    void onEnteredArea(String macAddress);

    void onExitedArea(String macAddress);
}
