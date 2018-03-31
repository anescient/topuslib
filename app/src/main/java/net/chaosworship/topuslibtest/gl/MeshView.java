package net.chaosworship.topuslibtest.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.gl.FlatViewTransform;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;


public class MeshView
        extends GLSurfaceView
        implements GLSurfaceView.Renderer {

    private final TestLoader mLoader;
    private final FlatViewTransform mViewTransform;

    public MeshView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLoader = new TestLoader(context);
        mViewTransform = new FlatViewTransform();

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(false);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        mLoader.invalidateAll();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mLoader.invalidateAll();
        mViewTransform.setViewport(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClearColor(0, 0.2f, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        mViewTransform.setVisibleRectangle(new Rectangle(-1, -3, 1, 3));
        mViewTransform.callGlViewport();
        ShapesBrush brush = mLoader.getShapesBrush();
        brush.begin(mViewTransform.getViewMatrix(), null);
        brush.drawSpot(new Vec2(-1, -3), 0.2f);
        brush.drawSpot(new Vec2(-1, 3), 0.2f);
        brush.drawSpot(new Vec2(1, -3), 0.2f);
        brush.drawSpot(new Vec2(1, 3), 0.2f);
        brush.end();
    }
}
