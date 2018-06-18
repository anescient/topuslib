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
import net.chaosworship.topuslib.geom3d.Path;
import net.chaosworship.topuslib.geom3d.Vec3;
import net.chaosworship.topuslib.geom3d.transform.AxisAngleRotator;
import net.chaosworship.topuslib.gl.GLLinesBrush;
import net.chaosworship.topuslib.gl.view.TurnTableViewTransform;
import net.chaosworship.topuslib.input.MotionEventConverter;
import net.chaosworship.topuslib.random.SuperRandom;
import net.chaosworship.topuslibtest.gl.TestLoader;

import java.util.List;

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

        float phase = (SystemClock.uptimeMillis() / (float)10000) % 1.0f;
        float modelSpin = (float)(2 * Math.PI * phase);
        //modelSpin = 0;

        mViewTransform.setRotation(mSpin);
        mViewTransform.setFOV(60);
        mViewTransform.setEyeDistance(4);
        mViewTransform.setEyeHeight(mEyeHeight);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        mViewTransform.callGlViewport();

        glClearColor(0, 0.2f, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Vec3 a = new Vec3(-2, -5, 1).normalize().scale(2);
        Vec3 b = new Vec3();//0, 3, -2);//new Vec3((float)Math.sin(-modelSpin), (float)Math.cos(modelSpin), (float)Math.cos(-modelSpin));
        Vec3 c = new Vec3((float)Math.sin(modelSpin), (float)Math.cos(modelSpin), 0).normalize().scale(2);

        /*
        Vec3 move = new Vec3(1, 1, 1);
        a.add(move);
        b.add(move);
        c.add(move);
        */

        AxisAngleRotator rotator;
        rotator = new AxisAngleRotator(new Vec3(1, 1.3f, 0.5f).normalize(), modelSpin);
        //rotator.rotate(a);
        //rotator.rotate(c);
        rotator = new AxisAngleRotator(new Vec3(0.1f, 0.3f, -0.7f).normalize(), 2 * modelSpin);
        //rotator.rotate(a);
        //rotator.rotate(c);

        GLLinesBrush linesBrush = mLoader.getGLLinesBrush();
        linesBrush.begin(mViewTransform.getViewMatrix(), 1);
        linesBrush.setColor(Color.RED);
        linesBrush.setAlpha(1);

        List<Vec3> path = Path.generateCurve(a, b, c, 0.7f);

        linesBrush.end();


        linesBrush.begin(mViewTransform.getViewMatrix(), 3);
        linesBrush.setColor(Color.WHITE);
        linesBrush.setAlpha(0.15f);
        linesBrush.addCuboid(new Cuboid(-1, 1, -1, 1, -1, 1));
        linesBrush.end();

        linesBrush.begin(mViewTransform.getViewMatrix(), 5);
        linesBrush.setAlpha(1f);
        linesBrush.setColor(Color.WHITE);
        linesBrush.addPath(path);
        linesBrush.end();

        /*
        linesBrush.begin(mViewTransform.getViewMatrix(), 2);
        linesBrush.setAlpha(0.5f);
        linesBrush.setColor(Color.YELLOW);
        linesBrush.addLine(a, b);
        linesBrush.addLine(b, c);
        linesBrush.end();
        */
    }
}
