package net.chaosworship.topuslib.gl.view;

import android.opengl.Matrix;

import net.chaosworship.topuslib.BuildConfig;
import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.transform.Vec2Transformer;


// handles the dirty work of viewing a 2D area in a GLSurfaceView
// converts screen coords into world coords for doing input, etc.
@SuppressWarnings({"SameParameterValue", "unused", "WeakerAccess"})
public class FlatViewTransform extends ViewportTransform implements ViewTransform {

    private final Vec2 mViewCenter; // world coordinates centered in view
    private float mZoom;
    private float mViewRotation;
    private float mVisibleWidth; // world units
    private float mVisibleHeight;

    private final float[] mViewMatrix;
    private boolean mViewMatrixDirty;

    private final FlatViewWorldTransformer mViewWorldTransformer;
    private boolean mViewToWorldDirty;

    public FlatViewTransform() {
        // arbitrary sane initial values
        mViewCenter = new Vec2(0, 0);
        mZoom = 1.0f;
        mViewRotation = 0;
        mVisibleWidth = 1.0f;
        mVisibleHeight = 1.0f;

        mViewMatrix = new float[16];
        mViewWorldTransformer = new FlatViewWorldTransformer();
        setDirty();
    }

    @Override
    void viewportChanged() {
        mVisibleHeight = getViewportHeight() / mZoom;
        mVisibleWidth = getViewportWidth() / mZoom;
        setDirty();
    }

    public float getVisibleWidth() {
        return mVisibleWidth;
    }

    public float getVisibleHeight() {
        return mVisibleHeight;
    }

    private void setDirty() {
        mViewMatrixDirty = true;
        mViewToWorldDirty = true;
    }

    // center and zoom such that a given rectangular area is visible
    // rectangle won't necessarily fill the viewport
    public void setVisibleRectangle(Rectangle rect) {
        if(rect.width() == 0 || rect.height() == 0) {
            float size = Math.max(rect.width(), rect.height());
            rect = new Rectangle(rect);
            rect.setWithCenter(rect.center(), size, size);
        }
        if(rect.area() == 0) {
            throw new IllegalArgumentException();
        }
        setViewRotationRadians(0);
        setViewCenter(rect.center());
        float viewAspect = (float)getViewportWidth() / getViewportHeight();
        float rectAspect = rect.width() / rect.height();
        if(rectAspect > viewAspect) {
            setVisibleWidth(rect.width());
        } else {
            setVisibleHeight(rect.height());
        }
    }

    public void setViewZoom(float zoom) {
        if(mZoom != zoom) {
            mZoom = zoom;
            mVisibleHeight = getViewportHeight() / mZoom;
            mVisibleWidth = getViewportWidth() / mZoom;
            setDirty();
        }
    }

    public void setVisibleWidth(float width) {
        if(width <=0) {
            throw new IllegalArgumentException();
        }
        setViewZoom(getViewportWidth() / width);
    }

    public void setVisibleHeight(float height) {
        if(height <= 0) {
            throw new IllegalArgumentException();
        }
        setViewZoom(getViewportHeight() / height);
    }

    public void setViewCenter(Vec2 worldCoord) {
        if(!mViewCenter.equals(worldCoord)) {
            mViewCenter.set(worldCoord);
            setDirty();
        }
    }

    public void setViewRotationDegrees(float degrees) {
        if(mViewRotation != degrees) {
            mViewRotation = degrees;
            setDirty();
        }
    }

    public void setViewRotationRadians(float radians) {
        setViewRotationDegrees(radians * (float)(180 / Math.PI));
    }

    @Override
    public float[] getViewMatrix() {
        synchronized(mViewMatrix) {
            if(mViewMatrixDirty) {
                setViewMatrix();
            }
            return mViewMatrix;
        }
    }

    public Vec2Transformer getViewToWorldTransformer() {
        if(mViewToWorldDirty) {
            setViewWorldTransformer();
        }
        return mViewWorldTransformer;
    }

    private void setViewMatrix() {
        if(BuildConfig.DEBUG) {
            if(!mViewMatrixDirty)
                throw new AssertionError();
            if(mVisibleHeight == 0 || mVisibleWidth == 0)
                throw new AssertionError();
        }

        float halfHeight = mVisibleHeight / 2;
        float halfWidth = mVisibleWidth / 2;
        synchronized(mViewMatrix) {
            Matrix.orthoM(mViewMatrix, 0, -halfWidth, halfWidth, -halfHeight, halfHeight, -1, 1);
            Matrix.rotateM(mViewMatrix, 0, mViewRotation, 0, 0, 1);
            Matrix.translateM(mViewMatrix, 0, -mViewCenter.x, -mViewCenter.y, 0);
            mViewMatrixDirty = false;
        }
    }

    private void setViewWorldTransformer() {
        if(BuildConfig.DEBUG && !mViewToWorldDirty) {
            throw new AssertionError();
        }

        mViewWorldTransformer.setInverse(getViewMatrix(), getViewportWidth(), getViewportHeight());
        mViewToWorldDirty = false;
    }
}
