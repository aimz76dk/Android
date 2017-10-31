package aimz76dk.gameengine;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class GameEngine extends Activity implements Runnable, SensorEventListener
{
    private Screen screen;
    private Canvas canvas;
    private Bitmap virtualScreen;
    Rect src = new Rect();
    Rect dst = new Rect();

    Paint paint = new Paint();
    public Music music = null;

    private SoundPool soundPool;

    private TouchHandler touchHandler;
    private TouchEventPool touchEventPool = new TouchEventPool();
    private List<TouchEvent> touchEventBuffer = new ArrayList<>();
    private List<TouchEvent> touchEventsCopied = new ArrayList<>();

    private float[] accelerometer = new float[3];

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


        if (surfaceView.getWidth() > surfaceView.getHeight())
        {
            setVirtualScreen(480, 320);
        }
        else
        {
            setVirtualScreen(320, 480);
        }
        touchHandler = new MultiTouchHandler(surfaceView, touchEventBuffer, touchEventPool);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0)
        {
            Sensor accelerometer = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        SoundPool.Builder sBuilder = new SoundPool.Builder();
        sBuilder.setMaxStreams(20);
        AudioAttributes.Builder audioAttrBuilder = new AudioAttributes.Builder();
        audioAttrBuilder.setUsage(AudioAttributes.USAGE_GAME);
        AudioAttributes audioAttr = audioAttrBuilder.build();
        sBuilder.setAudioAttributes(audioAttr);
        this.soundPool = sBuilder.build();

        screen = createStartScreen();


        //this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);

    } // End of onCreate() method

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    public void onSenserChanged(SensorEvent event)
    {
        System.arraycopy(event.values, 0, accelerometer, 0, 3);
        accelerometer[0] = -1.0f * accelerometer[1];
    }

    public void setVirtualScreen(int width, int height)
    {
        if (virtualScreen != null) virtualScreen.recycle();
        virtualScreen = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        canvas = new Canvas(virtualScreen);
    }

    private void fillEvents()
    {
        synchronized (touchEventBuffer)
        {
            int count = touchEventBuffer.size();
            for (int i = 0; i < count; i++)
            {
                touchEventsCopied.add(touchEventBuffer.get(i)); // copy all object from one list to the other
            }
            touchEventBuffer.clear(); // empty the buffer
        }
    }

    private void freeEvents()
    {
        synchronized (touchEventsCopied)
        {
            int count = touchEventsCopied.size();
            for (int i = 0; i < count; i++)
            {
                touchEventPool.free(touchEventsCopied.get(i)); // return all used objects to the free pool
            }
            touchEventsCopied.clear();
        }
    }

    public List<TouchEvent> getTouchEvents()
    {
        return touchEventsCopied;
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

    public Music loadMusic(String filename)
    {
        try
        {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(filename);
            return new Music(assetFileDescriptor);
        } catch (IOException e)
        {
            throw new RuntimeException("Could not load music file: " + filename);
        }

    }

    public Sound loadSound(String filename)
    {
        try
        {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(filename);
            int soundId = soundPool.load(assetFileDescriptor, 0);
            Sound sound = new Sound(soundPool, soundId);
            return sound;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not load sound from file " + filename);
        }

    }

    public Typeface loadFont(String filename)
    {
        Typeface font = Typeface.createFromAsset(getAssets(), filename);
            if (font == null)
        {
            throw new RuntimeException("Could not load font: " + filename);
        }
        return font;
    }

    public void drawText(Typeface font, String text, int x, int y, int color, int size)
    {
        paint.setTypeface(font);
        paint.setTextSize(size);
        paint.setColor(color);
        canvas.drawText(text, x, y, paint);
    }

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
        return touchHandler.isTouchDown(pointer);
    }
    public int getTouchX(int pointer)
    {
        int virtualX = 0;
        virtualX = (int) ((float)touchHandler.getTouchX(pointer)/(float)surfaceView.getWidth() * virtualScreen.getWidth());
        return virtualX;
    }
    public int getTouchY(int pointer)
    {
        int virtualY = 0;
        virtualY = (int) ((float)touchHandler.getTouchY(pointer)/(float)surfaceView.getHeight() * virtualScreen.getHeight());
        return virtualY;
    }

    public float[] getAccelerometer()
    {
        return accelerometer;
    }

    public void onPause()
    {
        super.onPause();
        synchronized(stateChanges)
        {
            if(isFinishing())
            {
                soundPool.release();
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
        if (isFinishing())
        {
            ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).unregisterListener(this);
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
        long startTime = System.nanoTime();
        long currentTime = startTime;
        float deltaTime;

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
                fillEvents();
                //now we can do all the drawing stuff
                currentTime = System.nanoTime();
                deltaTime = (currentTime - startTime)/1000000000.0f;
                if(screen != null )
                {
                    screen.update(deltaTime); //this is were the game does all the logic for the screen
                }
                startTime = currentTime;
                freeEvents();
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
            } // end of state running

        } // end of while loop
    } // end of run()

}