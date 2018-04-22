package net.chaosworship.topuslibtest.drawingboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import net.chaosworship.topuslibtest.R;


public class DrawingBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawingboard);
    }

    public void go(View view) {
        DrawingBoard drawingBoard = findViewById(R.id.triangulation_meshview);
        drawingBoard.requestRender();
    }
}
