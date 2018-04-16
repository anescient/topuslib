package net.chaosworship.topuslibtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import net.chaosworship.topuslib.ui.FloatSeekBar;


public class WidgetActivity extends AppCompatActivity {

    private TextView mTextOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);

        mTextOut = findViewById(R.id.inputtestoutput);

        FloatSeekBar testSeekBar = findViewById(R.id.testseekbar);
        testSeekBar.setOnSeekBarChangeListener(new FloatSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(float value) {

            }

            @Override
            public void onProgressFinishedChanging(float value) {
                mTextOut.setText(Float.toString(value));
            }
        });
    }
}
