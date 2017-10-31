package aimz76dk.gameengine.Breakout;

public interface CollisionListener
{
    public void collisionWall();
    public void collisionPaddle();
    public void collisionBlock();
}