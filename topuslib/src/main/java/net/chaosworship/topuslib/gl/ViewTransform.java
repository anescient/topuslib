package net.chaosworship.topuslib.gl;

import net.chaosworship.topuslib.geom2d.transform.Vec2Transformer;


@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface ViewTransform {

    // return true if view actually changes
    boolean setViewport(int width, int height);

    boolean isDegenerate();

    void callGlViewport();

    int getViewportWidth();

    int getViewportHeight();

    float[] getViewMatrix();
}
