package aimz76dk.gameengine.Breakout;

import android.graphics.Bitmap;

import aimz76dk.gameengine.GameEngine;
import aimz76dk.gameengine.Screen;


public class GameScreen extends Screen
{
    Bitmap background = null;

    public GameScreen(GameEngine gameEngine)
    {
        super(gameEngine);
        background = gameEngine.loadBitmap("breakoutassets/background.png");
    }

    @Override
    public void update(float deltaTime)
    {
        gameEngine.drawBitmap(background, 0, 0);
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void dispose()
    {

    }
}
