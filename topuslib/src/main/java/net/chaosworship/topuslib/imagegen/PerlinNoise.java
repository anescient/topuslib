package net.chaosworship.topuslib.imagegen;

import android.graphics.Bitmap;
import android.graphics.Color;

public class PerlinNoise {

    private PerlinNoise() {}

    public static Bitmap generateGrayscale(int width, int height, double z, float density) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for(int y = 0; y < height; y++) {
            double dy = (double)y / height;
            for(int x = 0; x < width; x++) {
                double dx = (double)x / width;
                double dv = Perlin.noise(dx * density, dy * density, z);
                int v = (int)Math.floor((0.5f + 0.5f * dv) * 255);
                bmp.setPixel(x, y, Color.argb(0xff, v, v, v));
            }
        }
        return bmp;
    }
}
