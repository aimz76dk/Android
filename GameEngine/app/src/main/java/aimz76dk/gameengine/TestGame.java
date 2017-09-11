package aimz76dk.gameengine;


public class TestGame extends GameEngine
{
    @Override
    public Screen createStartScreen()
    {
        return new TestScreen(this);
    }
}
