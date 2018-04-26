package net.chaosworship.topuslibtest.particles;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import net.chaosworship.topuslibtest.R;

public class ParticlesActivity extends AppCompatActivity {

    private ParticlesView mParticlesView;
    private boolean mZoomed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particles);
        mParticlesView = findViewById(R.id.particlesview);
    }

    public void buttonReset(View view) {
        mParticlesView.reset();
    }

    public void buttonZoom(View view) {
        mZoomed = !mZoomed;
        mParticlesView.setZoom(mZoomed ? 3 : 1);
    }
}
