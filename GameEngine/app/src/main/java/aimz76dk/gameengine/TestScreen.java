package aimz76dk.gameengine;


import android.graphics.Color;
import android.util.Log;

public class TestScreen extends Screen
{
    public TestScreen(GameEngine gameEngine)
    {
        super(gameEngine);
    }

    @Override
    public void update(float deltaTime)
    {
        gameEngine.clearFrameBuffer(Color.GREEN);
    }

    @Override
    public void pause()
    {
        Log.d("TestScreen", "the test screen is paused!!");
    }

    @Override
    public void resume()
    {
        Log.d("TestScreen", "the test screen is resumed");
    }

    @Override
    public void dispose()
    {
        Log.d("TestScreen", "the test screen is disposed");

    }
}
