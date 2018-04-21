package net.chaosworship.topuslibtest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import net.chaosworship.topuslibtest.benchmark.BenchmarkActivity;
import net.chaosworship.topuslibtest.bitmapgen.BitmapGenActivity;
import net.chaosworship.topuslibtest.drawingboard.DrawingBoardActivity;
import net.chaosworship.topuslibtest.inputtest.InputTestActivity;
import net.chaosworship.topuslibtest.inputtest.WidgetActivity;


@SuppressLint({"SetTextI18n", "DefaultLocale"})
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startDrawingBoard(null);
    }

    public void startDrawingBoard(View view) {
        startActivity(new Intent(this, DrawingBoardActivity.class));
    }

    public void startBenchmark(View view) {
        startActivity(new Intent(this, BenchmarkActivity.class));
    }

    public void startInputTest(View view) {
        startActivity(new Intent(this, InputTestActivity.class));
    }

    public void startBitmapGenTest(View view) {
        startActivity(new Intent(this, BitmapGenActivity.class));
    }

    public void startWidgetTest(View view) {
        startActivity(new Intent(this, WidgetActivity.class));
    }
}
