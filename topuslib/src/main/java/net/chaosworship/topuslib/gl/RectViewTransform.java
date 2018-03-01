package net.chaosworship.topuslib.gl;

import android.graphics.RectF;
import android.opengl.Matrix;

import static android.opengl.GLES20.glViewport;

import net.chaosworship.topuslib.BuildConfig;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.Vec2Transformer;


// handles transformations for a simple rectangular view of a 2D world
// rotation and pan are not supported. (0,0) is always centered.
// projection is such that:
//   largest dimension (viewport width or height) fits to world coordinates spanning [-1,1]
//   smaller dimension spans a range that makes world coordinates square
public class RectViewTransform implements ViewTransform {

    ///////////////////////////////////////////////////////////////

    private class ViewWorldTransformer implements Vec2Transformer {

        private final float[] mMatrix;
        private float[] mInputVector;
        private float[] mOutputVector;

        private ViewWorldTransformer() {
            mMatrix = new float[16];
            mInputVector = new float[4];
            mInputVector[2] = 0;
            mInputVector[3] = 1;
            mOutputVector = new float[4];
        }

        private void setInverse(float[] inverseMatrix) {
            Matrix.invertM(mMatrix, 0, inverseMatrix, 0);
        }

        @Override
        public Vec2 transform(Vec2 coord) {
            if(mViewportWidth == 0 || mViewportHeight == 0) {
                throw new IllegalStateException();
            }
            mInputVector[0] = 2 * coord.x / mViewportWidth - 1;
            mInputVector[1] = -(2 * coord.y / mViewportHeight - 1);
            Matrix.multiplyMV(mOutputVector, 0, mMatrix, 0, mInputVector, 0);
            return new Vec2(mOutputVector[0], mOutputVector[1]);
        }
    }

    ///////////////////////////////////////////////////////////////

    private int mViewportWidth;
    private int mViewportHeight;

    // visible rectangle is abused as a "dirty" flag
    // if this reference is null everything needs to be calculated, this rect, view matrix, etc.
    private RectF mVisibleRect;

    private final float[] mViewMatrix;

    private final ViewWorldTransformer mViewWorldTransformer;

    public RectViewTransform() {
        mViewportWidth = 0;
        mViewportHeight = 0;
        mVisibleRect = null;
        mViewMatrix = new float[16];
        mViewWorldTransformer = new ViewWorldTransformer();
    }

    public boolean isDegenerate() {
        return mViewportWidth <= 0 || mViewportHeight <= 0;
    }

    // return true if view actually changes
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

    public void callGlViewport() {
        glViewport(0, 0, mViewportWidth, mViewportHeight);
    }

    public int getViewportWidth() {
        return mViewportWidth;
    }

    public int getViewportHeight() {
        return mViewportHeight;
    };

    // please do not modify returned object
    public RectF getVisibleRect() {
        updateTransform();
        return mVisibleRect;
    }

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

        mVisibleRect = new RectF(-halfVisibleWidth, -halfVisibleHeight, halfVisibleWidth, halfVisibleHeight);

        synchronized(mViewMatrix) {
            Matrix.orthoM(mViewMatrix, 0, -halfVisibleWidth, halfVisibleWidth, -halfVisibleHeight, halfVisibleHeight, -1, 1);
        }

        mViewWorldTransformer.setInverse(mViewMatrix);
    }
}
