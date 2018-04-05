package net.chaosworship.topuslibtest;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.chaosworship.topuslibtest.benchmark.DelaunayBench;
import net.chaosworship.topuslibtest.benchmark.KDTreeBench;
import net.chaosworship.topuslibtest.benchmark.TimedRunner;


public class BenchmarkActivity extends AppCompatActivity {

    private TextView mTextOutput;
    private Button mButtonGo;

    ////////////////////////////////////////////////////

    private class BenchmarkTask extends AsyncTask<Void, Void, String> {

        @SuppressLint("DefaultLocale")
        @Override
        protected String doInBackground(Void... voids) {

            //TimedRunner testRunner = new KDTreeBench();
            TimedRunner testRunner = new DelaunayBench();

            long ms = testRunner.timedRun();
            return String.format("%dms", ms);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String out) {
            mTextOutput.setText(mTextOutput.getText() + "\n" + out);
            mButtonGo.setEnabled(true);
        }
    }

    ////////////////////////////////////////////////////

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benchmark);
        mTextOutput = findViewById(R.id.text_output);
        mButtonGo = findViewById(R.id.button_go);

        mButtonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonGo.setEnabled(false);
                new BenchmarkTask().execute();
            }
        });

        mTextOutput.setText("ready");
    }
}
