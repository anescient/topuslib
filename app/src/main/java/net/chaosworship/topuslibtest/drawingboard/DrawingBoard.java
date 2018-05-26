package net.chaosworship.topuslibtest.drawingboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;

import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.transform.Vec2Transformer;
import net.chaosworship.topuslib.geom3d.Vec3;
import net.chaosworship.topuslib.gl.GLLinesBrush;
import net.chaosworship.topuslib.gl.view.TurnTableViewTransform;
import net.chaosworship.topuslib.graph.GenerateGraph;
import net.chaosworship.topuslib.graph.HashSimpleGraph;
import net.chaosworship.topuslib.graph.SimpleGraph;
import net.chaosworship.topuslib.graph.Walk;
import net.chaosworship.topuslib.input.MotionEventConverter;
import net.chaosworship.topuslib.random.SuperRandom;
import net.chaosworship.topuslib.tuple.IntTriple;
import net.chaosworship.topuslibtest.gl.TestLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;


@SuppressLint("UseSparseArrays")
public class DrawingBoard
        extends GLSurfaceView
        implements GLSurfaceView.Renderer {

    private static final float CELLSIZE = 0.7f;
    private static final int GRIDSIZE = 9;

    private static final SuperRandom sRandom = new SuperRandom();

    private final TestLoader mLoader;
    private final TurnTableViewTransform mViewTransform;
    private final MotionEventConverter mInputConverter;

    private float mSpin;
    private float mEyeHeight;

    private final ArrayList<Vec3> mPath;
    private float mPathStepsShown;

    private SimpleGraph mGridGraph;
    private Map<Integer, Vec3> mGridPoints;

    public DrawingBoard(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLoader = new TestLoader(context);
        mViewTransform = new TurnTableViewTransform();
        mInputConverter = new MotionEventConverter();

        mSpin = 0.1f;
        mEyeHeight = 3;

        mPath = new ArrayList<>();
        mPathStepsShown = 0;

        mGridGraph = new HashSimpleGraph();
        Map<Integer, IntTriple> gridCoords = GenerateGraph.cuboidGrid(mGridGraph, GRIDSIZE, GRIDSIZE, GRIDSIZE);
        mGridPoints = new HashMap<>();
        Vec3 offset = new Vec3(-0.5f, -0.5f, -0.5f).scale(CELLSIZE * (GRIDSIZE - 1));
        for(Integer vertex : gridCoords.keySet()) {
            IntTriple coord = gridCoords.get(vertex);
            mGridPoints.put(vertex, new Vec3(coord).scale(CELLSIZE).add(offset));
        }

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(false);
        setRenderer(this);
        //setRenderMode(RENDERMODE_WHEN_DIRTY);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    public void go() {
        mPath.clear();
        for(Integer v : Walk.randomWalk(mGridGraph)) {
            mPath.add(mGridPoints.get(v));
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

        Vec2 touch = mInputConverter.getActivePointerMean();
        if(touch != null) {
            Vec2Transformer touchTransform = mViewTransform.getViewToNormalTransformer();
            float leftRight = touchTransform.transform(touch).x;
            float topBottom = touchTransform.transform(touch).y;
            mSpin = -4 * leftRight;
            mEyeHeight = 10 * topBottom;
        }

        float extraSpin = 0;//(SystemClock.uptimeMillis() / (float)10000) % (float)(2 * Math.PI);
        mViewTransform.setRotation(mSpin + extraSpin);
        mViewTransform.setFOV(120);
        mViewTransform.setEyeDistance(5);
        mViewTransform.setEyeHeight(mEyeHeight);

        mViewTransform.callGlViewport();
        glClearColor(0, 0.2f, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        GLLinesBrush linesBrush = mLoader.getGLLinesBrush();
        linesBrush.begin(mViewTransform.getViewMatrix(), 2);
        linesBrush.setColor(Color.WHITE);
        linesBrush.setAlpha(0.05f);
        linesBrush.addGraph(mGridGraph, mGridPoints);
        linesBrush.end();

        linesBrush.begin(mViewTransform.getViewMatrix(), 5);
        linesBrush.setColor(Color.WHITE);
        linesBrush.setAlpha(1);

        mPathStepsShown += 0.3f;
        int showCount = (int)(mPathStepsShown);
        if(showCount > mPath.size()) {
            go();
            mPathStepsShown = 0;
            showCount = 0;
        }
        ArrayList<Vec3> subPath = new ArrayList<>();
        for(int i = 0; i < showCount; i++) {
            subPath.add(mPath.get(i));
        }
        linesBrush.setAlpha(1.0f);
        linesBrush.addPath(subPath);

        linesBrush.end();

    }
}
