package aimz76dk.gameengine.Breakout;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;

import java.util.List;

import aimz76dk.gameengine.GameEngine;
import aimz76dk.gameengine.Screen;
import aimz76dk.gameengine.Sound;
import aimz76dk.gameengine.TouchEvent;


public class GameScreen extends Screen
{
    enum State
    {
        Paused,
        Running,
        GameOver
    }

    World world = null;
    WorldRenderer worldRenderer = null;
    State  state      = State.Running;

    Bitmap background = null;
    Bitmap resume     = null;
    Bitmap gameOver   = null;

    Typeface font     = null;

    Sound bounceSound   = null;
    Sound blockSound    = null;
    Sound gameOverSound = null;

    public GameScreen(GameEngine gameEngine)
    {
        super(gameEngine);
        world = new World(gameEngine, new CollisionListener()
        {
            @Override
            public void collisionWall() {
                bounceSound.play(1);
            }

            @Override
            public void collisionPaddle() {
                bounceSound.play(1);
            }

            @Override
            public void collisionBlock() {
                blockSound.play(1);
            }
        } );
        worldRenderer = new WorldRenderer(gameEngine, world);

        //Bitmaps
        background = gameEngine.loadBitmap("breakoutassets/background.png");
        resume = gameEngine.loadBitmap("breakoutassets/resume.png");
        gameOver = gameEngine.loadBitmap("breakoutassets/gameover.png");

        //Init font
        font = gameEngine.loadFont("breakoutassets/font.ttf");

        //Sounds
        bounceSound   = gameEngine.loadSound("breakoutassets/bounce.wav");
        blockSound    = gameEngine.loadSound("breakoutassets/blocksplosion.wav");
        gameOverSound = gameEngine.loadSound("breakoutassets/gameover.wav");
    }

    @Override
    public void update(float deltaTime)
    {
        if(world.gameOver)
        {
            state = State.GameOver;
        }

        if(state == State.Paused && gameEngine.getTouchEvents().size() > 0)
        {
            state = State.Running;
            resume();
        }

        if(state == State.GameOver)
        {
            List<TouchEvent> events = gameEngine.getTouchEvents();
            for(int i = 0; i < events.size(); i++)
            {
                if (events.get(i).type == TouchEvent.TouchEventType.Up)
                {
                    gameEngine.setScreen(new MainMenuScreen(gameEngine));
                    return;
                }
            }
        }

        if(state == State.Running && gameEngine.getTouchY(0) < 38 && gameEngine.getTouchX(0) > 280)
        {
            state = State.Paused;
            pause();
            return;
        }

        gameEngine.drawBitmap(background, 0, 0);

        if(state == State.Running)
        {
            world.update(deltaTime, gameEngine.getAccelerometer()[0]);
        }
        worldRenderer.render();

        gameEngine.drawText(font, "POINTS " + Integer.toString(world.points), 24, 24, Color.GREEN, 15);

        if(state == State.Paused)
        {
            gameEngine.drawBitmap(resume, 160 - (resume.getWidth() / 2), 240 - (resume.getHeight() / 2));
        }

        if(state == State.GameOver)
        {
            gameEngine.drawBitmap(gameOver, 160 - (gameOver.getWidth() / 2), 240 - (gameOver.getHeight() / 2));
        }
    }

    @Override
    public void pause()
    {
        if(state == State.Running) state = State.Paused;
        gameEngine.music.pause();
    }

    @Override
    public void resume()
    {
        gameEngine.music.play();
    }

    @Override
    public void dispose()
    {

    }
}