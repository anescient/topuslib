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
import net.chaosworship.topuslib.geom3d.Cuboid;
import net.chaosworship.topuslib.geom3d.OrthonormalBasis;
import net.chaosworship.topuslib.geom3d.TriangleMesh;
import net.chaosworship.topuslib.geom3d.TriangulatedSphere;
import net.chaosworship.topuslib.geom3d.Vec3;
import net.chaosworship.topuslib.gl.GLLinesBrush;
import net.chaosworship.topuslib.gl.view.TurnTableViewTransform;
import net.chaosworship.topuslib.input.MotionEventConverter;
import net.chaosworship.topuslib.random.SuperRandom;
import net.chaosworship.topuslibtest.gl.ShadedTrianglesBrush;
import net.chaosworship.topuslibtest.gl.TestLoader;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;


@SuppressLint("UseSparseArrays")
public class DrawingBoard
        extends GLSurfaceView
        implements GLSurfaceView.Renderer {

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

        mPath = generateTestPath();

        Vec3 avg = new Vec3();
        for(Vec3 p : mPath) {
            avg.add(p);
        }
        avg.scaleInverse(mPath.size());
        for(Vec3 p : mPath) {
            p.subtract(avg);
        }

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(false);
        setRenderer(this);
        //setRenderMode(RENDERMODE_WHEN_DIRTY);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    private static ArrayList<Vec3> generateTestPath() {
        ArrayList<Vec3> path = new ArrayList<>();
        SuperRandom random = new SuperRandom();
        random.setSeed(787);
        path.add(new Vec3(random.nextFloat(), random.nextFloat(), random.nextFloat()));
        for(int i = 0; i < 35; i++) {
            path.add(path.get(path.size() - 1).sum(random.uniformOnUnitSphere().scale(0.4f)));
        }
        return path;
    }

    public void go() {
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

        float phase = (SystemClock.uptimeMillis() / (float)10000) % 1.0f;
        float modelSpin = (float)(2 * Math.PI * phase);

        mViewTransform.setRotation(mSpin + modelSpin);
        mViewTransform.setFOV(60);
        mViewTransform.setEyeDistance(6);
        mViewTransform.setEyeHeight(mEyeHeight);

        mViewTransform.callGlViewport();
        glClearColor(0, 0.2f, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        GLLinesBrush linesBrush = mLoader.getGLLinesBrush();
        linesBrush.begin(mViewTransform.getViewMatrix(), 3);

        linesBrush.setColor(Color.YELLOW);
        linesBrush.setAlpha(0.3f);
        for(int i = 0; i < mPath.size() - 1; i++) {
            linesBrush.addLine(mPath.get(i), mPath.get(i + 1));
        }

        Cuboid boundBox = Cuboid.bound(mPath);
        linesBrush.setColor(Color.WHITE);
        linesBrush.setAlpha(0.5f);
        linesBrush.addCuboid(boundBox);

        linesBrush.end();
        linesBrush.begin(mViewTransform.getViewMatrix(), 5);

        linesBrush.setColor(Color.WHITE);
        linesBrush.setAlpha(1.0f);
        for(int skip = 0; skip < mPath.size() - 3; skip++) {
            Vec3 lastPos = null;
            for(float along = 0; along <= 1.0f; along += 0.1f) {
                Vec3 pos = new Vec3().setCubicBSpline(
                        mPath.get(skip),
                        mPath.get(skip + 1),
                        mPath.get(skip + 2),
                        mPath.get(skip + 3),
                        along);
                if(lastPos != null) {
                    linesBrush.addLine(lastPos, pos);
                    lastPos = pos;
                }
                if(lastPos == null) {
                    lastPos = new Vec3(pos);
                }
            }
        }

        linesBrush.end();
    }
}
