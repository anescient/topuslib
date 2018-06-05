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

        mPath = new ArrayList<>();
        mPath.add(new Vec3(0, 0, 0));
        mPath.add(new Vec3(0.1f, 0.02f, 0.04f));
        mPath.add(new Vec3(0.2f, 0.03f, 0.14f));
        mPath.add(new Vec3(0.5f, 0.08f, 0.3f));
        mPath.add(new Vec3(0.7f, 0.16f, 0.6f));
        mPath.add(new Vec3(1.0f, 0.5f, 0.7f));
        mPath.add(new Vec3(0.9f, 0.8f, 0.3f));
        mPath.add(new Vec3(0.8f, 0.6f, 0.2f));

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(false);
        setRenderer(this);
        //setRenderMode(RENDERMODE_WHEN_DIRTY);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
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

        float modelSpin = (SystemClock.uptimeMillis() / (float)100) % (float)(360);

        mViewTransform.setRotation(mSpin);
        mViewTransform.setFOV(90);
        mViewTransform.setEyeDistance(3);
        mViewTransform.setEyeHeight(mEyeHeight);

        mViewTransform.callGlViewport();
        glClearColor(0, 0.2f, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        GLLinesBrush linesBrush = mLoader.getGLLinesBrush();
        linesBrush.begin(mViewTransform.getViewMatrix(), 3);

        linesBrush.addCube(new Vec3(0,0,0), new OrthonormalBasis(), 1.0f, Color.MAGENTA);

        linesBrush.setColor(Color.WHITE);
        for(int i = 0; i < mPath.size() - 1; i++) {
            linesBrush.addLine(mPath.get(i), mPath.get(i + 1));
        }
        linesBrush.end();
    }
}
