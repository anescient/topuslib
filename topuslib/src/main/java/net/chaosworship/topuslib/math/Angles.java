package net.chaosworship.topuslib.math;

import net.chaosworship.topuslib.BuildConfig;


public class Angles {

    private Angles() {}

    // return equivalent angle in range [0,2PI)
    public static double unloopRadians(double a) {
        double cosine = 0;
        if(BuildConfig.DEBUG) {
            cosine = Math.cos(a);
        }

        a -= 2 * Math.PI * Math.floor(a / (2 * Math.PI));

        if(BuildConfig.DEBUG) {
            if(a < 0 || a >= 2 * Math.PI)
                throw new AssertionError();
            if(Math.abs(Math.cos(a) - cosine) > 0.000001)
                throw new AssertionError();
        }

        return a;
    }
}
