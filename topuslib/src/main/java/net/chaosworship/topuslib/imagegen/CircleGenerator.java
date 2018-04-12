package net.chaosworship.topuslib.imagegen;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.annotation.ColorInt;


public class CircleGenerator {

    private CircleGenerator() {}

    public static Bitmap generateSpot(int size, @ColorInt int color) {
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawCircle(size * 0.5f, size * 0.5f, size * 0.45f, paint);
        return bmp;
    }

    public static Bitmap generateSoftSpot(int size, @ColorInt int color) {
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        int transparent = Color.argb(0, Color.red(color), Color.green(color), Color.blue(color));
        paint.setShader(
                new RadialGradient(size * 0.5f, size * 0.5f, size * 0.45f,
                color, transparent, Shader.TileMode.CLAMP));
        canvas.drawCircle(size * 0.5f, size * 0.5f, size * 0.45f, paint);
        return bmp;
    }
}
