package aimz76dk.gameengine.Breakout;


import android.graphics.Bitmap;

import aimz76dk.gameengine.GameEngine;
import aimz76dk.gameengine.Screen;

public class MainMenuScreen extends Screen {

    Bitmap mainMenu = null;
    Bitmap insertCoin = null;
    float passedTime = 0;
    long startTime = System.nanoTime();

    public MainMenuScreen(GameEngine gameEngine)
    {
        super(gameEngine);
        insertCoin = gameEngine.loadBitmap("breakoutassets/insertcoin.png");
        mainMenu = gameEngine.loadBitmap("breakoutassets/mainmenu.png");
    }

    @Override
    public void update(float deltaTime)
    {
        if (gameEngine.isTouchDown(0))
        {
            gameEngine.setScreen(new GameScreen(gameEngine));
            return;
        }
        gameEngine.drawBitmap(mainMenu, 0, 0);
        passedTime = passedTime + deltaTime;
        if ( (passedTime - (int) passedTime) > 0.5f )
        {
            gameEngine.drawBitmap(insertCoin, 160 - (insertCoin.getWidth()/2), 350);
        }

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
