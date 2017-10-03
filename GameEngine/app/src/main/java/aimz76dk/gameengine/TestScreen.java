package aimz76dk.gameengine;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.List;

public class TestScreen extends Screen
{
    Bitmap bob = null;
    float bobX = -100;
    float bobY = 50;
    TouchEvent event = null;
    Sound sound = null;
    Music music = null;
    boolean isPlaying = false;

    public TestScreen(GameEngine gameEngine)

    {
        super(gameEngine);
        bob = gameEngine.loadBitmap("bob.png");
        sound = gameEngine.loadSound("bounce.wav");
        music = gameEngine.loadMusic("music.ogg");
        music.setLooping(true);
        music.play();
        isPlaying = true;

    }

    @Override
    public void update(float deltaTime)
    {
        gameEngine.clearFrameBuffer(Color.BLUE);

        bobX = bobX + (float) (10 * deltaTime);
        if (bobX > gameEngine.getFrameBufferWidth()) bobX = 0 - bob.getWidth();
        gameEngine.drawBitmap(bob, (int)bobX, (int)bobY);



    }

    @Override
    public void pause()
    {
        Log.d("TestScreen", "the test screen is paused!!");
        music.pause();
    }

    @Override
    public void resume()
    {
        Log.d("TestScreen", "the test screen is resumed");

        if (!music.isPlaying())
        {
            music.play();
            isPlaying = true;
        }
    }

    @Override
    public void dispose()
    {
        Log.d("TestScreen", "the test screen is disposed");
        music.dispose();

    }
}
