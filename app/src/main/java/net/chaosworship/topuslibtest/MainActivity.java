package net.chaosworship.topuslibtest;

import android.annotation.SuppressLint;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.chaosworship.topuslib.geom2d.rangesearch.KDTree;
import net.chaosworship.topuslib.geom2d.rangesearch.RectangularSearch;

import java.util.Random;


@SuppressLint({"SetTextI18n", "DefaultLocale"})
public class MainActivity extends AppCompatActivity {

    private static final Random sRandom = new Random();

    private TextView mTextOutput;
    private Button mButtonGo;

    ////////////////////////////////////////////////////

    private class KDTreeBenchTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            RectangularSearch<String> tree = new KDTree<>();
            long ms = new KDTreeBench().timedTest(tree);
            return String.format("%dms", ms);
        }

        @Override
        protected void onPostExecute(String out) {
            mTextOutput.setText(mTextOutput.getText() + "\n" + out);
            mButtonGo.setEnabled(true);
        }
    }

    ////////////////////////////////////////////////////

    private class MatrixBenchTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            float[] m_in = new float[16];
            float[] m_out = new float[16];
            for(int i = 0; i < 16; i++) {
                m_in[i] = sRandom.nextFloat();
            }
            long start = SystemClock.uptimeMillis();
            for(int i = 0; i < 1000000; i++) {
                Matrix.setIdentityM(m_out, 0);
                Matrix.invertM(m_out, 0, m_in, 0);
            }
            return String.format("%dms", SystemClock.uptimeMillis() - start);
        }

        @Override
        protected void onPostExecute(String out) {
            mTextOutput.setText(mTextOutput.getText() + "\n" + out);
            mButtonGo.setEnabled(true);
        }
    }

    ////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextOutput = findViewById(R.id.text_output);
        mButtonGo = findViewById(R.id.button_go);

        mButtonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonGo.setEnabled(false);
                //new KDTreeBenchTask().execute();
                new MatrixBenchTask().execute();
            }
        });

        mTextOutput.setText("ready");
    }
}
