package net.chaosworship.topuslibtest.bitmapgen;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import net.chaosworship.topuslib.imagegen.HueMap;
import net.chaosworship.topuslibtest.R;


public class BitmapGenActivity extends AppCompatActivity {

    private ImageView mOutputImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap_gen);
        mOutputImageView = findViewById(R.id.image_generated);
    }

    public void generateBitmap(View view) {
        //Bitmap bmp = WhiteNoise.generateRGB(512, 512, sRandom.nextLong());
        //Bitmap bmp = PerlinNoise.generateGrayscale(512, 512, sRandom.nextDouble(), 5);
        //Bitmap bmp = HueMap.generateHueCycle(256, 1.0f);
        Bitmap bmp = HueMap.generateHueBlackToWhite(256, 64);
        mOutputImageView.setImageBitmap(bmp);
    }
}
