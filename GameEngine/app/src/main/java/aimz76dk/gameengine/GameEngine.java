package aimz76dk.gameengine;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public abstract class GameEngine extends Activity implements Runnable
{
    private Screen screen;
    private Canvas canvas;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private Thread mainLoopThread;
    private State state = State.Paused;
    private List<State> stateChanges = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        surfaceView = new SurfaceView(this);
        setContentView(surfaceView);
        surfaceHolder = surfaceView.getHolder();

        screen = createStartScreen();
    }

    public abstract Screen createStartScreen();

    public void setScreen(Screen screen)
    {
        if(this.screen != null)
        {
            this.screen.dispose();
        }
        this.screen = screen;
    }

    public Bitmap loadBitmap(String filename)
    {
        return null;
    }
    //public Music loadMusic(String filename) { return null; }
    //public Sound loadSound(String filename) { return null; }

    public void clearFrameBuffer(int color)
    {
        canvas.drawColor(color);
    }
    public int getFrameBufferWidth() { return 0; }
    public int getFrameBufferHeight()
    {
        return 0;
    }
    public void drawBitmap(Bitmap bitmap, int x, int y) {}
    public void drawBitmap(Bitmap bitmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight) {}

    public boolean isTouchDown(int pointer)
    {
        return false;
    }
    public int getTouchX(int pointer)
    {
        return 0;
    }
    public int getTouchY(int pointer)
    {
        return 0;
    }

    //public List<TouchEvent> getTouchEvents() { return null; }
    public float[] getAccelerometer(){ return null; }

    public void onPause()
    {
        super.onPause();
        synchronized(stateChanges)
        {
            if(isFinishing())
            {
                stateChanges.add(State.Disposed);
            }
            else
            {
                stateChanges.add(State.Paused);
            }
        }
        try
        {
            mainLoopThread.join();
        }
        catch(Exception e)
        {
            Log.d("GameEngine", "something went wrong");
        }
    }

    public void onResume()
    {
        super.onResume();
        synchronized(stateChanges)
        {
            stateChanges.add(State.Resume);
        }
        mainLoopThread = new Thread(this);
        mainLoopThread.start();
    }

    @Override
    public void run()
    {
        while(true)
        {
            synchronized(stateChanges)
            {
                int stopValue = stateChanges.size();
                for(int i = 0; i < stopValue; i++)
                {
                    state = stateChanges.get(i);
                    switch(state)
                    {
                        case Disposed:
                            if(screen != null)
                            {
                                screen.dispose();
                            }
                            Log.d("GameEngine" , "Main Loop thread is disposed");
                            stateChanges.clear();
                            return;
                        case Paused:
                            if(screen != null)
                            {
                                screen.pause();
                            }
                            Log.d("GameEngine", "Main Loop thread is paused");
                            stateChanges.clear();
                            return;
                        case Resume:
                            if(screen != null)
                            {
                                screen.resume();
                            }
                            Log.d("GameEngine", "Main Loop thread is resumed");
                            state = State.Running;
                            break;
                        default: break;
                    }
                }
                stateChanges.clear();
            }
            //After the synchronized state check we can do the actual work of the thread
            if(state == State.Running)
            {
                if(!surfaceHolder.getSurface().isValid())
                {
                    continue;
                }
                canvas = surfaceHolder.lockCanvas();
                //now we can do all the drawing stuff
                //canvas.drawColor(Color.RED);
                if(screen != null )
                {
                    screen.update(0);
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
