package net.chaosworship.topuslib.math;


@SuppressWarnings("WeakerAccess")
public abstract class Spline {

    private static final float OVER2 = 1.0f / 2.0f;
    private static final float OVER6 = 1.0f / 6.0f;
    private static final float OVER24 = 1.0f / 24.0f;
    private static final float OVER35 = 1.0f / 35.0f;

    // u in [0,1] for approximation from b to c
    public static float CubicBSpline(float a, float b, float c, float d, float u) {
        final float u2 = u * u;
        final float u3 = u2 * u;
        final float b0 = u3 * OVER6;
        final float b1 = (1 + 3 * u + 3 * u2 - 3 * u3) * OVER6;
        final float b2 = (4 - 6 * u2 + 3 * u3) * OVER6;
        final float b3 = (1 - 3 * u + 3 * u2 - u3) * OVER6;
        return b0 * d + b1 * c + b2 * b + b3 * a;
    }

    // first derivative
    public static float CubicBSplineFirst(float a, float b, float c, float d, float u) {
        final float u2 = u * u;
        final float b0 = u2 * OVER2;
        final float b1 = (1 + 2 * u - 3 * u2) * OVER2;
        final float b2 = (-4 * u + 3 * u2) * OVER2;
        final float b3 = (-1 + 2 * u - u2) * OVER2;
        return b0 * d + b1 * c + b2 * b + b3 * a;
    }

    // second derivative
    public static float CubicBSplineSecond(float a, float b, float c, float d, float u) {
        @SuppressWarnings("UnnecessaryLocalVariable")
        final float b0 = u;
        final float b1 = 1 - 3 * u;
        final float b2 = 3 * u - 2;
        final float b3 = 1 - u;
        return b0 * d + b1 * c + b2 * b + b3 * a;
    }

    // u in [0,1] for approximation from (b+c)/2 to (c+d)/2
    public static float QuarticBSpline(float a, float b, float c, float d, float e, float u) {
        final float u2 = u * u;
        final float u3 = u2 * u;
        final float u4 = u2 * u2;
        final float b0 = u4 * OVER24;
        final float b1 = (1 + 4 * u + 6 * u2 + 4 * u3 - 4 * u4) * OVER24;
        final float b2 = (11 + 12 * u - 6 * u2 - 12 * u3 + 6 * u4) * OVER24;
        final float b3 = (11 - 12 * u - 6 * u2 + 12 * u3 - 4 * u4) * OVER24;
        final float b4 = (1 - 4 * u + 6 * u2 - 4 * u3 + u4) * OVER24;
        return b0 * e + b1 * d + b2 * c + b3 * b + b4 * a;
    }

    // first derivative
    public static float QuarticBSplineFirst(float a, float b, float c, float d, float e, float u) {
        final float u2 = u * u;
        final float u3 = u2 * u;
        final float b0 = u3 * OVER6;
        final float b1 = (-4 * u3 + 3 * u2 + 3 * u + 1) * OVER6;
        final float b2 = (2 * u3 - 3 * u2 - u + 1) * OVER2;
        final float b3 = (-4 * u3 + 9 * u2 - 3 * u - 3) * OVER6;
        final float b4 = (u3 - 3 * u2 + 3 * u - 1) * OVER6;
        return b0 * e + b1 * d + b2 * c + b3 * b + b4 * a;
    }

    // second derivative
    public static float QuarticBSplineSecond(float a, float b, float c, float d, float e, float u) {
        final float u2 = u * u;
        final float b0 = u2 * OVER2;
        final float b1 = (-4 * u2 + 2 * u + 1) * OVER2;
        final float b2 = (6 * u2 - 6 * u - 1) * OVER2;
        final float b3 = (-4 * u2 + 6 * u - 1) * OVER2;
        final float b4 = (u2 - 2 * u + 1) * OVER2;
        return b0 * e + b1 * d + b2 * c + b3 * b + b4 * a;
    }

    // https://en.wikipedia.org/wiki/Savitzky%E2%80%93Golay_filter
    // smoothed value at c
    public static float SavitzkyGolay(float a, float b, float c, float d, float e) {
        return (-3 * a + 12 * b + 17 * c + 12 * d - 3 * e) * OVER35;
    }
}
