package net.chaosworship.topuslibtest.gl;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.gl.FlatViewTransform;
import net.chaosworship.topuslib.graph.IntegerEdge;
import net.chaosworship.topuslib.graph.SimpleGraph;
import net.chaosworship.topuslib.random.SuperRandom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
        mViewTransform.callGlViewport();
        glClearColor(0, 0.2f, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        SuperRandom random = new SuperRandom();
        HashMap<Integer, Vec2> points = new HashMap<>();
        SimpleGraph graph = new SimpleGraph();
        for(int i = 0; i < 20; i++) {
            points.put(graph.addVertex(), new Vec2(
                    99 * (random.nextFloat() - 0.5f),
                    33 * (random.nextFloat() - 0.5f)));
        }
        ArrayList<Integer> verts = new ArrayList<>(graph.getVertices());
        for(int i = 0; i < verts.size() * 2; i++) {
            int a = random.choice(verts);
            int b = random.choice(verts);
            if(a != b && !graph.hasEdge(a, b)) {
                graph.addEdge(a, b);
            }
        }

        mViewTransform.setVisibleRectangle(Rectangle.bound(points.values()).scale(1.07f));
        ShapesBrush brush = mLoader.getShapesBrush();
        brush.begin(mViewTransform.getViewMatrix());

        brush.setColor(Color.RED);
        for(IntegerEdge e : graph.getEdges()) {
            brush.drawSegment(0.05f, points.get(e.a), points.get(e.b));
        }

        brush.setColor(Color.WHITE);
        for(int v : graph.getVertices()) {
            Vec2 p = points.get(v);
            brush.drawSpot(p, 0.5f);
        }

        brush.end();
    }
}
