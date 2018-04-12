package net.chaosworship.topuslibtest;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import net.chaosworship.topuslib.imagegen.PerlinNoise;
import net.chaosworship.topuslib.imagegen.WhiteNoise;
import net.chaosworship.topuslib.random.SuperRandom;


public class BitmapGenActivity extends AppCompatActivity {

    private static final SuperRandom sRandom = new SuperRandom();

    private ImageView mOutputImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap_gen);
        mOutputImageView = findViewById(R.id.image_generated);
    }

    public void generateBitmap(View view) {
        //Bitmap bmp = WhiteNoise.generateRGB(512, 512, sRandom.nextLong());
        Bitmap bmp = PerlinNoise.generateGrayscale(512, 512, sRandom.nextDouble(), 5);
        mOutputImageView.setImageBitmap(bmp);
    }
}
