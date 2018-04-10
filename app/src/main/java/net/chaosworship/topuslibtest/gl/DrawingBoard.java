package net.chaosworship.topuslibtest.gl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import net.chaosworship.topuslib.collection.SegmentConsumer;
import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.mesh.DelaunayTriangulator;
import net.chaosworship.topuslib.geom2d.mesh.Triangulation;
import net.chaosworship.topuslib.gl.FlatViewTransform;
import net.chaosworship.topuslib.gl.ShapesBrush;
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
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        //setRenderMode(RENDERMODE_CONTINUOUSLY);
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
            float r = random.nextFloat();
            points.add(random.uniformUnit().scale(44 * (1 - r * r)));
        }

        /*
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                points.add(new Vec2(
                        i * 10 + 0.00f * random.nextFloat(),
                        j * 10 + 0.00f * random.nextFloat()));
                //points.add(new Vec2(i * 10 + 0.01f * random.nextFloat(), j * 10 + 0.01f * random.nextFloat()));
            }
        }
        */

        mViewTransform.setVisibleRectangle(Rectangle.bound(points).scale(2));
        final ShapesBrush brush = mLoader.getShapesBrush();
        brush.begin(mViewTransform.getViewMatrix());

        Circle c = Circle.minimumBound(points);
        brush.setColor(Color.RED);
        brush.drawCircle(c, 0.3f);

        DelaunayTriangulator dt = new DelaunayTriangulator();
        Triangulation triangulation = dt.triangulate(points);

        brush.setColor(Color.BLUE);
        brush.setAlpha(0.2f);
        for(Triangle t : triangulation.getTriangles()) {
            brush.fillTriangle(t);
        }

        brush.setColor(Color.WHITE);
        brush.setAlpha(0.2f);
        SegmentConsumer segmentDrawer = new SegmentConsumer() {
            @Override
            public void addSegment(Vec2 a, Vec2 b) {
                brush.drawSegment(a, b, 0.3f);
            }
        };
        triangulation.outputSegments(segmentDrawer);

        brush.setColor(Color.WHITE);
        brush.setAlpha(1);
        for(Vec2 p : points) {
            brush.drawSpot(p, 0.7f);
        }

        brush.end();
    }
}
