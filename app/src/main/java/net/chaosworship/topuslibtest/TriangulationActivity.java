package net.chaosworship.topuslibtest;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import net.chaosworship.topuslibtest.gl.MeshView;

public class TriangulationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triangulation);
    }

    public void go(View view) {
        MeshView meshView = findViewById(R.id.triangulation_meshview);
        meshView.requestRender();
    }
}
