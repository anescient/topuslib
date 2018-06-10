package net.chaosworship.topuslib.gl.view;

import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.transform.RectangularMapping;
import net.chaosworship.topuslib.geom2d.transform.Vec2Transformer;

import static android.opengl.GLES20.glViewport;


public abstract class ViewportTransform implements ViewTransform {

    private int mViewportWidth;
    private int mViewportHeight;

    @SuppressWarnings("WeakerAccess")
    public ViewportTransform() {
        mViewportWidth = 0;
        mViewportHeight = 0;
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
        viewportChanged();
        return true;
    }

    protected abstract void viewportChanged();

    @Override
    public boolean isDegenerate() {
        return mViewportWidth <= 0 || mViewportHeight <= 0;
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

    @Override
    public Vec2Transformer getViewToNormalTransformer() {
        Rectangle from = new Rectangle(0, 0, mViewportWidth, mViewportHeight);
        Rectangle to = new Rectangle(-1, -1, 1, 1);
        return new RectangularMapping(from, to);
    }
}
