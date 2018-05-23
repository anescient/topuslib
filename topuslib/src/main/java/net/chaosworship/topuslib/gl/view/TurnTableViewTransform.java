package net.chaosworship.topuslib.gl.view;

import android.opengl.Matrix;


public class TurnTableViewTransform extends ViewportTransform implements ViewTransform {

    private float mRotation;
    private float mEyeDistance;
    private float mEyeHeight;

    private final float[] mViewMatrix;
    private boolean mViewMatrixDirty;

    public TurnTableViewTransform() {
        mRotation = 0;
        mEyeDistance = 1;
        mEyeHeight = 0;
        mViewMatrix = new float[16];
        mViewMatrixDirty = true;
        Matrix.setIdentityM(mViewMatrix, 0);
    }

    public void setRotation(float rotation) {
        if(mRotation != rotation) {
            mRotation = rotation;
            mViewMatrixDirty = true;
        }
    }

    public void setEyeDistance(float distance) {
        if(mEyeDistance != distance) {
            mEyeDistance = distance;
            mViewMatrixDirty = true;
        }
    }

    public void setEyeHeight(float height) {
        if(mEyeHeight != height) {
            mEyeHeight = height;
            mViewMatrixDirty = true;
        }
    }

    @Override
    void viewportChanged() {
        mViewMatrixDirty = true;
    }

    @Override
    public float[] getViewMatrix() {
        if(mViewMatrixDirty) {
            setViewMatrix();
            mViewMatrixDirty = false;
        }
        return mViewMatrix;
    }

    private void setViewMatrix() {
        if(isDegenerate()) {
            Matrix.setIdentityM(mViewMatrix, 0);
            return;
        }

        float[] v = new float[16];
        float[] p = new float[16];
        Matrix.setLookAtM(v, 0,
                mEyeDistance * (float)Math.cos(mRotation), mEyeDistance * (float)Math.sin(mRotation), mEyeHeight,
                0, 0, 0,
                0, 0, 1);
        float aspect = (float)getViewportWidth() / getViewportHeight();
        Matrix.perspectiveM(p, 0, 45, aspect, -1, 1);
        Matrix.multiplyMM(mViewMatrix, 0, p, 0, v, 0);
    }
}
