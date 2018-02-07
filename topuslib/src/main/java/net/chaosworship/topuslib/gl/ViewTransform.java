package net.chaosworship.topuslib.gl;

import net.chaosworship.topuslib.math.Vec2Transformer;


public interface ViewTransform {
    void callGlViewport();
    int getViewportWidth();
    int getViewportHeight();
    float[] getViewMatrix();
    Vec2Transformer getViewToWorldTransformer();
}
