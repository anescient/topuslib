package net.chaosworship.topuslibtest.gl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.triangulation.DelaunayTriangulation;
import net.chaosworship.topuslib.geom2d.triangulation.Triangulation;
import net.chaosworship.topuslib.gl.FlatViewTransform;
import net.chaosworship.topuslib.random.SuperRandom;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;


@SuppressLint("UseSparseArrays")
public class DrawingBoard
        extends GLSurfaceView
        implements GLSurfaceView.Renderer {

    private final TestLoader mLoader;
    private final FlatViewTransform mViewTransform;

    public DrawingBoard(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLoader = new TestLoader(context);
        mViewTransform = new FlatViewTransform();

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(false);
        setRenderer(this);
        //setRenderMode(RENDERMODE_WHEN_DIRTY);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
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
        mViewTransform.callGlViewport();
        glClearColor(0, 0.2f, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        SuperRandom random = new SuperRandom();
        ArrayList<Vec2> points = new ArrayList<>();

        for(int i = 0; i < 20; i++) {
            points.add(random.uniformUnit().scale(44 * random.nextFloat()));
        }

        /*
        for(int i = 0; i < 7; i++) {
            for(int j = 0; j < 7; j++) {
                points.add(new Vec2(
                        i * 10 + 0.00f * random.nextFloat(),
                        j * 10 + 0.00f * random.nextFloat() + (i % 2) * 5));
                //points.add(new Vec2(i * 10 + 0.01f * random.nextFloat(), j * 10 + 0.01f * random.nextFloat()));
            }
        }
*/
        mViewTransform.setVisibleRectangle(Rectangle.bound(points).scale(2));
        ShapesBrush brush = mLoader.getShapesBrush();
        brush.begin(mViewTransform.getViewMatrix());

        Circle c = Circle.minimumBound(points);
        brush.setColor(Color.RED);
        brush.setLineWidth(0.3f);
        brush.drawCircle(c);

        brush.setColor(Color.LTGRAY);
        DelaunayTriangulation dt = new DelaunayTriangulation(points);
        for(Triangle t : dt.getTriangles()) {
            brush.drawTriangle(t);
        }

        brush.setColor(Color.WHITE);
        for(Vec2 p : points) {
            brush.drawSpot(p, 1.0f);
        }

        brush.end();
    }
}
