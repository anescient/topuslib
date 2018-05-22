package net.chaosworship.topuslib.gl.view;

import static android.opengl.GLES20.glViewport;


abstract class ViewportTransform implements ViewTransform {

    private int mViewportWidth;
    private int mViewportHeight;

    ViewportTransform() {
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

    abstract void viewportChanged();

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
}
