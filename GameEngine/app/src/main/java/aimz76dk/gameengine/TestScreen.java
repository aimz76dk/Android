package aimz76dk.gameengine;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.List;

public class TestScreen extends Screen
{
    Bitmap bob = null;
    TouchEvent event = null;
    Sound sound = null;

    public TestScreen(GameEngine gameEngine)

    {
        super(gameEngine);
        bob = gameEngine.loadBitmap("bob.png");
        sound = gameEngine.loadSound("bounce.wav");
    }

    @Override
    public void update(float deltaTime)
    {
        gameEngine.clearFrameBuffer(Color.BLUE);
        //   gameEngine.drawBitmap(bob, 10, 10);
        //   gameEngine.drawBitmap(bob, 100, 200, 64, 64, 128, 128);

        /*
        for (int pointer = 0; pointer < 5; pointer++)
        {
            if (gameEngine.isTouchDown(pointer))
            {
                gameEngine.drawBitmap(bob, gameEngine.getTouchX(pointer), gameEngine.getTouchY(pointer));
            }
        }
        */

        List<TouchEvent> touchEvents = gameEngine.getTouchEvents();
        int count = touchEvents.size();
        if (count == 0 && event != null)
        {
            gameEngine.drawBitmap(bob, gameEngine.getTouchX(event.pointer), gameEngine.getTouchY(event.pointer));
        }
        for (int i = 0; i < count; i++)
        {
            event = touchEvents.get(i);
            Log.d("TestScreen", "Touch event type: " + event.type + ", x: " + event.x + ", y: " + event.y);
            gameEngine.drawBitmap(bob, gameEngine.getTouchX(event.pointer), gameEngine.getTouchY(event.pointer));
            if (event.type == TouchEvent.TouchEventType.Down)
            {
                sound.play(1);
            }
        }

        /*
        float accX = gameEngine.getAccelerometer()[0];
        float accY = gameEngine.getAccelerometer()[1];
        float x = gameEngine.getFrameBufferWidth() / 2 + (accX / 20) * gameEngine.getFrameBufferWidth();
        float y = gameEngine.getFrameBufferHeight() / 2 + (accY / 20) * gameEngine.getFrameBufferHeight();

        gameEngine.drawBitmap(bob, (int) (x - (bob.getWidth()/2)), (int) (y - (bob.getHeight()/2)));
        */

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
