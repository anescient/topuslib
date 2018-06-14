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
import net.chaosworship.topuslib.geom3d.Vec3;
import net.chaosworship.topuslib.gl.GLLinesBrush;
import net.chaosworship.topuslib.gl.view.TurnTableViewTransform;
import net.chaosworship.topuslib.input.MotionEventConverter;
import net.chaosworship.topuslib.random.SuperRandom;
import net.chaosworship.topuslibtest.gl.TestLoader;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.glBindFramebuffer;
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

    public DrawingBoard(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLoader = new TestLoader(context);
        mViewTransform = new TurnTableViewTransform();
        mInputConverter = new MotionEventConverter();

        mSpin = 0.1f;
        mEyeHeight = 3;

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

        float phase = (SystemClock.uptimeMillis() / (float)70000) % 1.0f;
        float modelSpin = (float)(2 * Math.PI * phase);
        modelSpin = 0;

        mViewTransform.setRotation(mSpin + modelSpin);
        mViewTransform.setFOV(60);
        mViewTransform.setEyeDistance(4);
        mViewTransform.setEyeHeight(mEyeHeight);

        Vec3 startPos = new Vec3(1, 0, 0);
        Vec3 startTangent = new Vec3(-1, 0, 0);
        Vec3 endPos = new Vec3(0, 1, 0);
        Vec3 endTangent = new Vec3(0, 1, 0);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        mViewTransform.callGlViewport();

        glClearColor(0, 0.2f, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        GLLinesBrush linesBrush = mLoader.getGLLinesBrush();

        linesBrush.begin(mViewTransform.getViewMatrix(), 3);
        linesBrush.setColor(Color.WHITE);
        linesBrush.setAlpha(0.3f);
        linesBrush.addCuboid(new Cuboid(-1, 1, -1, 1, -1, 1));

        linesBrush.end();

        linesBrush.begin(mViewTransform.getViewMatrix(), 5);
        linesBrush.setAlpha(1.0f);
//        linesBrush.addAxes(new Vec3(), new OrthonormalBasis(), 1.0f);

        linesBrush.setColor(Color.WHITE);
        linesBrush.addPointer(startPos, startPos.sum(startTangent), 0.2f);
        linesBrush.addPointer(endPos, endPos.sum(endTangent), 0.2f);

        linesBrush.end();
    }
}
