package aimz76dk.gameengine.Breakout;

import java.util.ArrayList;
import java.util.List;


public class World
{
    public static final float ACCEL_SENSITIVITY = 0.3f;
    public static final float MIN_X = 0;
    public static final float MAX_X = 320-1;
    public static final float MIN_Y = 36;
    public static final float MAX_Y = 480-1;
    Ball ball = new Ball();
    Paddle paddle = new Paddle();
    List<Block> blocks = new ArrayList<>();

    public World()
    {
        generateBlocks();
    }

    private void generateBlocks()
    {
        blocks.clear();
        for (int y = 50, type = 0; y < 50 + 8*Block.HEIGHT; y = y+3+(int)Block.HEIGHT, type++) // for each row
        {
            for (int x = 20; x < MAX_X - Block.WIDTH/2; x = x+2 + (int)Block.WIDTH) // for each column
            {
                blocks.add(new Block(x, y, type));
            }
        }
    }

    public void update(float deltaTime, float accelX)
    {
        ball.x += (int)(ball.vx * deltaTime);
        ball.y += (int)(ball.vy * deltaTime);

        if (ball.x + ball.WIDTH >= MAX_X)
        {
            ball.vx *= -1;
            ball.x -= 1;
        }
        if (ball.y + ball.HEIGHT >= MAX_Y)
        {
            ball.vy *= -1;
            ball.y -= 1;
        }
        if (ball.x <= MIN_X)
        {
            ball.vx *= -1;
            ball.x += 1;
        }
        if (ball.y <= MIN_Y)
        {
            ball.vy *= -1;
            ball.y += 1;
        }

        if (Math.abs(accelX) > ACCEL_SENSITIVITY)
        {
            paddle.x += accelX * deltaTime * Paddle.SPEED;

            if (paddle.x < MIN_X)
            {
                paddle.x = MIN_X;
            }
            if (paddle.x > MAX_X - paddle.WIDTH)
            {
                paddle.x = MAX_X - paddle.WIDTH;
            }
        }

        collideBallPaddle();
        collideBallBlocks(deltaTime);

    }

    private void collideBallBlocks(float deltatime)
    {
        Block block = null;
        for (int i = 0; i<blocks.size(); i++)
        {
            block = blocks.get(i);
            if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y, Block.WIDTH, Block.HEIGHT))
            {
                blocks.remove(i);
                i--;
                float oldvx = ball.vx;
                float oldvy = ball.vy;
                reflectBall(ball, block);
                ball.x = (int)(ball.x - oldvx * deltatime * 1.01f);
                ball.y = (int)(ball.y - oldvy * deltatime * 1.01f);
            }
        }
    }

    private void reflectBall(Ball ball, Block block)
    {
        // check top left corner of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y, 1, 1))
        {
            if (ball.vx > 0) ball.vx = - ball.vx;
            if (ball.vy > 0) ball.vy = - ball.vy;
            return;
        }

        // check top right corner of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x+Block.WIDTH, block.y+Block.HEIGHT, 1, 1))
        {
            if (ball.vx < 0) ball.vx = - ball.vx;
            if (ball.vy < 0) ball.vy = - ball.vy;
            return;
        }

        // check bottom left corner of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y+Block.HEIGHT, 1, 1))
        {
            if (ball.vx > 0) ball.vx = - ball.vx;
            if (ball.vy < 0) ball.vy = - ball.vy;
            return;
        }

        // check bottom right corner of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x+Block.WIDTH, block.y+Block.HEIGHT, 1, 1))
        {
            if (ball.vx < 0) ball.vx = - ball.vx;
            if (ball.vy < 0) ball.vy = - ball.vy;
            return;
        }

        // check the top edge of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y, Block.WIDTH, 1))
        {
            ball.vy = - ball.vy;
            return;
        }

        // check the bottom edge of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y+Block.HEIGHT, Block.WIDTH, 1))
        {
            ball.vy = - ball.vy;
            return;
        }

        // check the left edge of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y, 1, Block.HEIGHT))
        {
            ball.vx = - ball.vx;
            return;
        }

        // check the right edge of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x+Block.WIDTH, block.y, 1, Block.HEIGHT))
        {
            ball.vx = - ball.vx;
            return;
        }


    }

    private boolean collideRects(float x1, float y1, float width1, float height1,
                                float x2, float y2, float width2, float height2)
    {
        return x1 < x2 + width2
                && x1 + width1 > x2
                && y1 + height1 > y2
                && y1 < y2 + height2;
    }

    private void collideBallPaddle()
    {
        if (ball.y + Ball.HEIGHT >= paddle.y
                && ball.x < paddle.x + Paddle.WIDTH
                && ball.x + Ball.WIDTH > paddle.x)
        {
            ball.y = (int)(paddle.y - Ball.HEIGHT - 2);
            ball.vy = -ball.vy;
        }
    }


}
