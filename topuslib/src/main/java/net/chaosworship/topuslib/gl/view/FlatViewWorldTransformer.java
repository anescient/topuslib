package net.chaosworship.topuslib.gl.view;

import android.opengl.Matrix;

import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.transform.Vec2Transformer;


// for "flat" 2d views, map screen/input coordinates to world coordinates
class FlatViewWorldTransformer implements Vec2Transformer {

    private final float[] mMatrix;
    private float[] mInputVector;
    private float[] mOutputVector;
    private int mViewportWidth;
    private int mViewportHeight;

    FlatViewWorldTransformer() {
        mMatrix = new float[16];
        mInputVector = new float[4];
        mInputVector[2] = 0;
        mInputVector[3] = 1;
        mOutputVector = new float[4];
        mViewportWidth = 1;
        mViewportHeight = 1;
    }

    void setInverse(float[] inverseMatrix, int viewportWidth, int viewportHeight) {
        mViewportWidth = viewportWidth;
        mViewportHeight = viewportHeight;
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
