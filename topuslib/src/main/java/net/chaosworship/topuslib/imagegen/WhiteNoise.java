package net.chaosworship.topuslib.imagegen;

import android.graphics.Bitmap;
import android.graphics.Color;

import net.chaosworship.topuslib.random.XORShiftRandom;


public class WhiteNoise {

    private WhiteNoise() {}

    public static Bitmap generateGrayscale(int width, int height, long seed) {
        XORShiftRandom random = new XORShiftRandom(seed);
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                int v = random.nextInt(256);
                bmp.setPixel(x, y, Color.argb(0xff, v, v, v));
            }
        }
        return bmp;
    }

    public static Bitmap generateRGB(int width, int height, long seed) {
        XORShiftRandom random = new XORShiftRandom(seed);
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                int r = random.nextInt(256);
                int g = random.nextInt(256);
                int b = random.nextInt(256);
                bmp.setPixel(x, y, Color.argb(0xff, r, g, b));
            }
        }
        return bmp;
    }
}
