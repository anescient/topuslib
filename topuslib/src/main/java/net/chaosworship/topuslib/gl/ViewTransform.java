package net.chaosworship.topuslib.gl;

import net.chaosworship.topuslib.math.Vec2Transformer;


public interface ViewTransform {

    // return true if view actually changes
    boolean setViewport(int width, int height);

    boolean isDegenerate();

    void callGlViewport();

    int getViewportWidth();

    int getViewportHeight();

    float[] getViewMatrix();

    Vec2Transformer getViewToWorldTransformer();
}
