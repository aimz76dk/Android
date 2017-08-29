package aimz76dk.gameengine;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class GameEngine extends Activity implements Runnable
{
    private Thread mainLoopThread;
    private State state = State.Paused;
    private List<State> stateChanges = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



        // setContentView(R.layout.activity_main);
    }

    public void onPause()
    {
        super.onPause();
        synchronized (stateChanges)
        {
            if (isFinishing())
            {

            }
        }

    }

    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void run()
    {
        while(true)
        {
            synchronized (stateChanges)
            {
                int stopValue = stateChanges.size();
                for (int i = 0; i < stopValue; i++)
                {
                    state = stateChanges.get(i);
                    if (state == State.Disposed)
                    {
                        Log.d("GameEngine", "Main Loop thread is disposed");
                        return;
                    }
                    if (state == State.Paused)
                    {
                        Log.d("GameEngine", "Main Loop thread is paused");
                        return;
                    }
                    if (state == State.Resume)
                    {
                        Log.d("GameEngine", "Main Loop thread is resumed");
                        state = State.Running;
                    }
                }
                stateChanges.clear();
            }
        }

    }
}
