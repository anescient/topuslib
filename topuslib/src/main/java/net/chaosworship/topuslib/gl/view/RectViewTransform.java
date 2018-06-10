package net.chaosworship.topuslib.gl.view;

import android.opengl.Matrix;

import net.chaosworship.topuslib.BuildConfig;
import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.transform.Vec2Transformer;


// handles transformations for a simple rectangular view of a 2D world
// rotation and pan are not supported. (0,0) is always centered.
// projection is such that:
//   largest dimension (viewport width or height) fits to world coordinates spanning [-1,1]
//   smaller dimension spans a range that makes world coordinates square
@SuppressWarnings("unused")
public class RectViewTransform extends ViewportTransform implements ViewTransform {

    // visible rectangle is abused as a "dirty" flag
    // if this reference is null everything needs to be calculated, this rect, view matrix, etc.
    private Rectangle mVisibleRect;

    private final float[] mViewMatrix;

    private final FlatViewWorldTransformer mViewWorldTransformer;

    public RectViewTransform() {
        mVisibleRect = null;
        mViewMatrix = new float[16];
        mViewWorldTransformer = new FlatViewWorldTransformer();
    }

    @Override
    protected void viewportChanged() {
        mVisibleRect = null;
    }

    // please do not modify returned object
    public Rectangle getVisibleRect() {
        updateTransform();
        return mVisibleRect;
    }

    @Override
    public float[] getViewMatrix() {
        updateTransform();
        return mViewMatrix;
    }

    public Vec2Transformer getViewToWorldTransformer() {
        updateTransform();
        return mViewWorldTransformer;
    }

    private void updateTransform() {
        if(BuildConfig.DEBUG && isDegenerate()) {
            throw new AssertionError();
        }

        if(mVisibleRect != null) {
            return;
        }

        float width = getViewportWidth();
        float height = getViewportHeight();
        float halfVisibleWidth;
        float halfVisibleHeight;
        if(width > height) {
            halfVisibleWidth = 1.0f;
            halfVisibleHeight = halfVisibleWidth * height / width;
        } else {
            halfVisibleHeight = 1.0f;
            halfVisibleWidth = halfVisibleHeight * width / height;
        }

        mVisibleRect = new Rectangle(-halfVisibleWidth, -halfVisibleHeight, halfVisibleWidth, halfVisibleHeight);

        synchronized(mViewMatrix) {
            Matrix.orthoM(mViewMatrix, 0, -halfVisibleWidth, halfVisibleWidth, -halfVisibleHeight, halfVisibleHeight, -1, 1);
        }

        mViewWorldTransformer.setInverse(mViewMatrix, getViewportWidth(), getViewportHeight());
    }
}
