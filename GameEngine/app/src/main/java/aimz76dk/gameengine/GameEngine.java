package aimz76dk.gameengine;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class GameEngine extends Activity implements Runnable
{
    private Screen screen;
    private Canvas canvas;
    private Bitmap virtualScreen;
    Rect src = new Rect();
    Rect dst = new Rect();

    private TouchHandler touchHandler;

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

        if (surfaceView.getWidth() > surfaceView.getHeight())
        {
            setVirtualScreen(480, 320);
        }
        else
        {
            setVirtualScreen(320, 480);
        }
       // touchHandler =
    } // End of onCreate() method

    public void setVirtualScreen(int width, int height)
    {
        if (virtualScreen != null) virtualScreen.recycle();
        virtualScreen = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        canvas = new Canvas(virtualScreen);
    }

    public int getFrameBufferWidth()
    {
        return virtualScreen.getWidth();
    }

    public int getFrameBufferHeight()
    {
        return virtualScreen.getHeight();
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
        InputStream in = null;
        Bitmap bitmap = null;

        try
        {
            in = getAssets().open(filename);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null)
            {
                throw new RuntimeException(" *** There was no graphics in this file: " + filename);
            }
            return bitmap;
        }
        catch (IOException e)
        {
            throw new RuntimeException(" *** Could not load the stupid graphic file: " + filename);
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e) {}
            }
        }
    } // End of loadBitmap() method

    //public Music loadMusic(String filename) { return null; }
    //public Sound loadSound(String filename) { return null; }

    public void clearFrameBuffer(int color)
    {
        canvas.drawColor(color);
    }


    public void drawBitmap(Bitmap bitmap, int x, int y)
    {
        if (canvas != null) canvas.drawBitmap(bitmap, x, y, null);
    }

    public void drawBitmap(Bitmap bitmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight)
    {
        Rect src = new Rect();
        Rect dst = new Rect();
        if (canvas == null) return;
        src.left = srcX;
        src.top = srcY;
        src.right = srcX + srcWidth;
        src.bottom = srcY + srcHeight;
        dst.left = x;
        dst.top = y;
        dst.right = x + srcWidth;
        dst.bottom = y + srcHeight;
        canvas.drawBitmap(bitmap, src, dst, null);

    }

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
            //after the synchronized state check we can do the actual work of the thread
            if(state == State.Running)
            {
                if(!surfaceHolder.getSurface().isValid())
                {
                    continue;
                }
                Canvas canvas = surfaceHolder.lockCanvas();
                //now we can do all the drawing stuff
                if(screen != null )
                {
                    screen.update(0); //this is were the game does all the logic for the screen
                }
                //after the screen has made all game object to the virtualScreen
                //we need to copy and resize the virtualScreen to the actual physical surfaceView
                src.left = 0;
                src.top = 0;
                src.right = virtualScreen.getWidth() - 1; // -1 because pixels are 0 indexed
                src.bottom = virtualScreen.getHeight() - 1;
                dst.left = 0;
                dst.top = 0;
                dst.right = surfaceView.getWidth();
                dst.bottom = surfaceView.getHeight();
                canvas.drawBitmap(virtualScreen, src, dst, null);

                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
