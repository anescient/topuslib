package net.chaosworship.topuslibtest.drawingboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;

import net.chaosworship.topuslib.collection.RectangularMap;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.transform.Vec2Transformer;
import net.chaosworship.topuslib.geom3d.Vec3;
import net.chaosworship.topuslib.gl.GLLinesBrush;
import net.chaosworship.topuslib.gl.view.TurnTableViewTransform;
import net.chaosworship.topuslib.graph.HashSimpleGraph;
import net.chaosworship.topuslib.graph.Walk;
import net.chaosworship.topuslib.input.MotionEventConverter;
import net.chaosworship.topuslib.random.SuperRandom;
import net.chaosworship.topuslibtest.gl.TestLoader;

import java.util.ArrayList;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;


@SuppressLint("UseSparseArrays")
public class DrawingBoard
        extends GLSurfaceView
        implements GLSurfaceView.Renderer {

    private static final float CELLSIZE = 0.5f;
    private static final int GRIDSIZE = 8;

    private static final SuperRandom sRandom = new SuperRandom();

    private final TestLoader mLoader;
    private final TurnTableViewTransform mViewTransform;
    private final MotionEventConverter mInputConverter;

    private float mSpin;
    private float mEyeHeight;

    private final ArrayList<Vec3> mPath;

    public DrawingBoard(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLoader = new TestLoader(context);
        mViewTransform = new TurnTableViewTransform();
        mInputConverter = new MotionEventConverter();

        mSpin = 0.1f;
        mEyeHeight = 3;

        mPath = new ArrayList<>();

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(false);
        setRenderer(this);
        //setRenderMode(RENDERMODE_WHEN_DIRTY);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    public void go() {
        HashSimpleGraph graph = new HashSimpleGraph();
        HashMap<Integer, Vec3> points = new HashMap<>();
        RectangularMap<Integer> grid = new RectangularMap<>(GRIDSIZE + 1, GRIDSIZE + 1);

        for(int xi = 0; xi < GRIDSIZE + 1; xi++) {
            float x = (xi - GRIDSIZE / 2) * CELLSIZE;
            for(int yi = 0; yi < GRIDSIZE + 1; yi++) {
                float y = (yi - GRIDSIZE / 2) * CELLSIZE;
                Integer vertex = graph.addVertex();
                grid.set(xi, yi, vertex);
                points.put(vertex, new Vec3(x, y, 0));
            }
        }

        for(int i = 0; i < GRIDSIZE + 1; i++) {
            for(int j = 0; j < GRIDSIZE; j++) {
                Integer a;
                Integer b;

                a = grid.get(i, j);
                b = grid.get(i, j + 1);
                graph.addEdge(a, b);

                a = grid.get(j, i);
                b = grid.get(j + 1, i);
                graph.addEdge(a, b);
            }
        }

        mPath.clear();
        for(Integer v : Walk.randomWalk(graph)) {
            mPath.add(points.get(v));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mInputConverter.pushEvent(e);
        return true;
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

        Vec2 touch = null;
        for(Vec2 dp : mInputConverter.dumpDowns()) {
            touch = dp;
        }
        if(touch != null) {
            Vec2Transformer touchTransform = mViewTransform.getViewToNormalTransformer();
            float leftRight = touchTransform.transform(touch).x;
            float topBottom = touchTransform.transform(touch).y;
            mSpin = -leftRight;
            mEyeHeight = 10 * topBottom;
        }

        if(mSpin != 0) {
            mViewTransform.setRotation(SystemClock.uptimeMillis() / ((1000f / mSpin)));
        }
        mViewTransform.setEyeDistance(10);
        mViewTransform.setEyeHeight(mEyeHeight);

        mViewTransform.callGlViewport();
        glClearColor(0, 0.2f, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        GLLinesBrush linesBrush = mLoader.getGLLinesBrush();
        linesBrush.begin(mViewTransform.getViewMatrix(), 3);
        linesBrush.setColor(Color.WHITE);
        linesBrush.setAlpha(0.1f);
        linesBrush.addXYGrid(GRIDSIZE, CELLSIZE, 0);
        if(!mPath.isEmpty()) {
            linesBrush.setAlpha(1.0f);
            linesBrush.addPath(mPath);
        }
        linesBrush.end();
    }
}
