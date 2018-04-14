package net.chaosworship.topuslib.imagegen;

import android.graphics.Bitmap;
import android.graphics.Color;


public class HueMap {

    private HueMap() {}

    public static Bitmap generateHueCycle(int width, float saturation) {
        float[] hsv = new float[] {0, saturation, 1.0f};
        Bitmap bmp = Bitmap.createBitmap(width, 1, Bitmap.Config.ARGB_8888);
        for(int x = 0; x < width; x++) {
            hsv[0] = (float)x * 360 / width;
            bmp.setPixel(x, 0, Color.HSVToColor(hsv));
        }
        return bmp;
    }

    public static Bitmap generateHueSaturation(int width, int height, float value) {
        float[] hsv = new float[] {0, 0, value};
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for(int y = 0; y < height; y++) {
            hsv[1] = (float)y / height;
            for(int x = 0; x < width; x++) {
                hsv[0] = (float) x * 360 / width;
                bmp.setPixel(x, y, Color.HSVToColor(hsv));
            }
        }
        return bmp;
    }

    public static Bitmap generateHueSaturationBlack(int width, int height) {
        float[] hsv = new float[] {0, 0, 0};
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for(int y = 0; y < height; y++) {
            hsv[2] = hsv[1] = (float)y / height;
            for(int x = 0; x < width; x++) {
                hsv[0] = (float) x * 360 / width;
                bmp.setPixel(x, y, Color.HSVToColor(hsv));
            }
        }
        return bmp;
    }

    public static Bitmap generateHueBlackToWhite(int width, int height) {
        float[] hsv = new float[] {0, 0, 0};
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for(int y = 0; y < height / 2; y++) {
            hsv[2] = hsv[1] = (float)y * 2 / height;
            for(int x = 0; x < width; x++) {
                hsv[0] = (float) x * 360 / width;
                bmp.setPixel(x, y, Color.HSVToColor(hsv));
            }
        }
        hsv[2] = 1;
        for(int y = height / 2; y < height; y++) {
            hsv[1] = 1 - 2 * (((float)y / height) - 0.5f);
            for(int x = 0; x < width; x++) {
                hsv[0] = (float) x * 360 / width;
                bmp.setPixel(x, y, Color.HSVToColor(hsv));
            }
        }
        return bmp;
    }
}
