package aimz76dk.gameengine.Breakout;

import android.hardware.SensorEvent;

import aimz76dk.gameengine.GameEngine;
import aimz76dk.gameengine.Screen;

public class Breakout extends GameEngine
{

    @Override
    public Screen createStartScreen()
    {
        return new MainMenuScreen(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {

    }
}
