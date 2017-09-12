package aimz76dk.gameengine;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class TestScreen extends Screen
{
    Bitmap bob = null;

    public TestScreen(GameEngine gameEngine)

    {
        super(gameEngine);
        bob = gameEngine.loadBitmap("bob.png");
    }

    @Override
    public void update(float deltaTime)
    {
        gameEngine.clearFrameBuffer(Color.BLUE);
        gameEngine.drawBitmap(bob, 10, 10);
        gameEngine.drawBitmap(bob, 100, 200, 64, 64, 128, 128);
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
