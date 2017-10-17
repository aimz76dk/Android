package aimz76dk.gameengine.Breakout;

import aimz76dk.gameengine.Breakout.World;

/**
 * Created by jakob on 10/17/17.
 */

public class Paddle
{
    public static final int WIDTH = 56;
    public static final int HEIGHT = 11;
    public static final int SPEED = 100;
    public float x = 160 - WIDTH/2;
    public float y = World.MAX_Y - 40 - HEIGHT/2;
}
