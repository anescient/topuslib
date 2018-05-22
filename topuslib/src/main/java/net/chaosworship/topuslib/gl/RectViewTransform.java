package net.chaosworship.topuslib.gl;

import android.opengl.Matrix;

import net.chaosworship.topuslib.BuildConfig;
import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.transform.Vec2Transformer;

import static android.opengl.GLES20.glViewport;


// handles transformations for a simple rectangular view of a 2D world
// rotation and pan are not supported. (0,0) is always centered.
// projection is such that:
//   largest dimension (viewport width or height) fits to world coordinates spanning [-1,1]
//   smaller dimension spans a range that makes world coordinates square
@SuppressWarnings("unused")
public class RectViewTransform implements ViewTransform {

    private int mViewportWidth;
    private int mViewportHeight;

    // visible rectangle is abused as a "dirty" flag
    // if this reference is null everything needs to be calculated, this rect, view matrix, etc.
    private Rectangle mVisibleRect;

    private final float[] mViewMatrix;

    private final FlatViewWorldTransformer mViewWorldTransformer;

    public RectViewTransform() {
        mViewportWidth = 0;
        mViewportHeight = 0;
        mVisibleRect = null;
        mViewMatrix = new float[16];
        mViewWorldTransformer = new FlatViewWorldTransformer();
    }

    @Override
    public boolean isDegenerate() {
        return mViewportWidth <= 0 || mViewportHeight <= 0;
    }

    @Override
    public boolean setViewport(int width, int height) {
        if(width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }

        if(mViewportWidth == width && mViewportHeight == height) {
            return false;
        }

        mViewportWidth = width;
        mViewportHeight = height;
        mVisibleRect = null;
        return true;
    }

    @Override
    public void callGlViewport() {
        glViewport(0, 0, mViewportWidth, mViewportHeight);
    }

    @Override
    public int getViewportWidth() {
        return mViewportWidth;
    }

    @Override
    public int getViewportHeight() {
        return mViewportHeight;
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

        float halfVisibleWidth;
        float halfVisibleHeight;
        if(mViewportWidth > mViewportHeight) {
            halfVisibleWidth = 1.0f;
            halfVisibleHeight = halfVisibleWidth * mViewportHeight / (float)mViewportWidth;
        } else {
            halfVisibleHeight = 1.0f;
            halfVisibleWidth = halfVisibleHeight * mViewportWidth / (float)mViewportHeight;
        }

        mVisibleRect = new Rectangle(-halfVisibleWidth, -halfVisibleHeight, halfVisibleWidth, halfVisibleHeight);

        synchronized(mViewMatrix) {
            Matrix.orthoM(mViewMatrix, 0, -halfVisibleWidth, halfVisibleWidth, -halfVisibleHeight, halfVisibleHeight, -1, 1);
        }

        mViewWorldTransformer.setInverse(mViewMatrix, mViewportWidth, mViewportHeight);
    }
}
