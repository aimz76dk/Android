package aimz76dk.gameengine.Breakout;

import android.graphics.Bitmap;

import aimz76dk.gameengine.GameEngine;
import aimz76dk.gameengine.Music;
import aimz76dk.gameengine.Screen;
import aimz76dk.gameengine.Sound;

public class GameScreen extends Screen
{

    enum State
    {
        Paused,
        Running,
        GameOver
    }

    World world;
    WorldRenderer worldRenderer;

    Bitmap background = null;
    Bitmap resume = null;
    Bitmap gameOver = null;

    Music music = null;
    Sound explosion = null;

    State state = State.Running;

    public GameScreen(GameEngine gameEngine)
    {
        super(gameEngine);
        world = new World();
        worldRenderer = new WorldRenderer(gameEngine, world);

        background = gameEngine.loadBitmap("breakoutassets/background.png");
        resume = gameEngine.loadBitmap("breakoutassets/resume.png");
        gameOver = gameEngine.loadBitmap("breakoutassets/gameover.png");

        music = gameEngine.loadMusic("breakoutassets/music.ogg");
        explosion = gameEngine.loadSound("breakoutassets/explosion.ogg");
        music.setLooping(true);
        music.play();
    }

    @Override
    public void update(float deltaTime)
    {
        if (state == State.Paused && gameEngine.getTouchEvents().size() > 0)
        {
            state = State.Running;
        }
        if (state == State.GameOver && gameEngine.getTouchEvents().size() > 0)
        {
            gameEngine.setScreen(new MainMenuScreen(gameEngine));
            return;
        }
        if (state == State.Running && gameEngine.getTouchY(0) < 38 && gameEngine.getTouchX(0) > 320-38)
        {
            state = State.Paused;
            return;
        }

        gameEngine.drawBitmap(background, 0, 0);

        if (state == State.Running)
        {
            world.update(deltaTime, gameEngine.getAccelerometer()[0]);
        }

        worldRenderer.render();


        if (state == State.Paused)
        {
            gameEngine.drawBitmap(resume, 160 - resume.getWidth()/2, 240 - resume.getHeight()/2);
        }

        if (state == State.GameOver)
        {
            gameEngine.drawBitmap(gameOver, 160 - gameOver.getWidth()/2, 240 - gameOver.getHeight()/2);
        }

        if (gameEngine.isTouchDown(0))
        {
            explosion.play(1);
        }
    }

    @Override
    public void pause()
    {
        if (state == State.Running)
        {
            state = State.Paused;
        }
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
