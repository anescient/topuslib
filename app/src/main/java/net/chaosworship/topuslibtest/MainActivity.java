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

        startDrawingBoard(null);
    }

    public void startDrawingBoard(View view) {
        startActivity(new Intent(this, DrawingBoardActivity.class));
    }

    public void startBenchmark(View view) {
        startActivity(new Intent(this, BenchmarkActivity.class));
    }
}
