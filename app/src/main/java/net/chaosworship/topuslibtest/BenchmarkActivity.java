package net.chaosworship.topuslibtest;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.chaosworship.topuslib.geom2d.rangesearch.KDTree;
import net.chaosworship.topuslib.geom2d.rangesearch.RectangularSearch;

import java.util.Random;

public class BenchmarkActivity extends AppCompatActivity {

    private static final Random sRandom = new Random();

    private TextView mTextOutput;
    private Button mButtonGo;

    ////////////////////////////////////////////////////

    private class KDTreeBenchTask extends AsyncTask<Void, Void, String> {

        @SuppressLint("DefaultLocale")
        @Override
        protected String doInBackground(Void... voids) {
            RectangularSearch<String> tree = new KDTree<>();
            long ms = new KDTreeBench().timedTest(tree);
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
                new BenchmarkActivity.KDTreeBenchTask().execute();
            }
        });

        mTextOutput.setText("ready");
    }
}
