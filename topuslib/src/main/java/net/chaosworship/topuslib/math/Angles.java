package net.chaosworship.topuslib.math;

import net.chaosworship.topuslib.BuildConfig;


public class Angles {

    private Angles() {}

    // return equivalent angle in range [0,2PI)
    public static double unloopRadians(double a) {
        return a - 2 * Math.PI * Math.floor(a / (2 * Math.PI));
    }
}
