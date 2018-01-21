package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // ignore orientation/keyboard change
        super.onConfigurationChanged(newConfig);
    }
}
