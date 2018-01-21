package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Timer;
import java.util.TimerTask;

import combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.views.StimuliView;

public class MainActivity extends AppCompatActivity {

    private StimuliView mStimuliView;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStimuliView = (StimuliView) findViewById(R.id.stimuliView);

        mTimer = new Timer();

        double doubleDelay = (1.0/120.0)*1000.0;

        int delay = (int)doubleDelay;
        int period = (int)doubleDelay;
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mStimuliView.stimuliFlashing();
            }
        }, delay, period);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // ignore orientation/keyboard change
        super.onConfigurationChanged(newConfig);
    }
}

// tasks:
// generate 3 rectangles (freq1, freq2, freq3) on start
// create timer object w/ period of (1/60) seconds
// increment counter from 0 to 30 (then reset) based on timer
// on each timer reset, mod2, mod3, mod5 the counter, and toggle colour if any remainders are 0
// Add back a song, play/pause, forward a song buttons above blinking rectangles
