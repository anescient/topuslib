package net.chaosworship.topuslibtest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


@SuppressLint({"SetTextI18n", "DefaultLocale"})
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Thread.sleep(500);
        } catch(InterruptedException ignored) {}
        startTriangulation(null);
    }

    public void startTriangulation(View view) {
        Intent intent = new Intent(this, TriangulationActivity.class);
        startActivity(intent);
    }

    public void startBenchmark(View view) {
        Intent intent = new Intent(this, BenchmarkActivity.class);
        startActivity(intent);
    }
}
