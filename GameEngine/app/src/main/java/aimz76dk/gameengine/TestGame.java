package aimz76dk.gameengine;

import android.hardware.SensorEvent;

public class TestGame extends GameEngine
{

    @Override
    public Screen createStartScreen()
    {
        return new TestScreen(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }
}
