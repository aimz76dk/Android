package aimz76dk.gameengine.Breakout;

import android.hardware.SensorEvent;

import aimz76dk.gameengine.GameEngine;
import aimz76dk.gameengine.Screen;

public class Breakout extends GameEngine
{
    @Override
    public Screen createStartScreen()
    {
        music = this.loadMusic("breakoutassets/music.ogg");
        return new MainMenuScreen(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        music.pause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        music.play();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}