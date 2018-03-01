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


@SuppressLint({"SetTextI18n", "DefaultLocale"})
public class MainActivity extends AppCompatActivity {

    private TextView mTextOutput;
    private Button mButtonGo;

    private class BenchmarkTask extends AsyncTask<Void, Void, String> {

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
                new BenchmarkTask().execute();
            }
        });

        mTextOutput.setText("ready");
    }
}
