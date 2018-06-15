package net.chaosworship.topuslibtest.drawingboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.MotionEvent;

import net.chaosworship.topuslib.geom2d.Arc;
import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.transform.Vec2Transformer;
import net.chaosworship.topuslib.geom3d.Cuboid;
import net.chaosworship.topuslib.geom3d.LazyInternalAngle;
import net.chaosworship.topuslib.geom3d.OrthonormalBasis;
import net.chaosworship.topuslib.geom3d.Vec3;
import net.chaosworship.topuslib.geom3d.transform.AxisAngleRotator;
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

        float phase = (SystemClock.uptimeMillis() / (float)30000) % 1.0f;
        float modelSpin = (float)(2 * Math.PI * phase);
        modelSpin = 0;

        mViewTransform.setRotation(mSpin);
        mViewTransform.setFOV(60);
        mViewTransform.setEyeDistance(4);
        mViewTransform.setEyeHeight(mEyeHeight);

        Vec3 a = new Vec3(-2f, 0, 0);
        Vec3 b = new Vec3(0, 0, 0);
        Vec3 c = new Vec3(-1.5f, 2f, 0);

        AxisAngleRotator rotator = new AxisAngleRotator(new Vec3(0, 0, 1), modelSpin);
        rotator.rotate(a);
        rotator.rotate(c);

        Vec3 startTangent = b.difference(a).normalized();
        Vec3 endTangent = c.difference(b).normalized();

        Vec3 binarySpliter = startTangent.negated().sum(endTangent).normalize();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        mViewTransform.callGlViewport();

        glClearColor(0, 0.2f, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        GLLinesBrush linesBrush = mLoader.getGLLinesBrush();

        linesBrush.begin(mViewTransform.getViewMatrix(), 3);
        linesBrush.setColor(Color.WHITE);
        linesBrush.setAlpha(0.15f);
        linesBrush.addCuboid(new Cuboid(-1, 1, -1, 1, -1, 1));
        linesBrush.end();

        linesBrush.begin(mViewTransform.getViewMatrix(), 5);
        linesBrush.setAlpha(1.0f);
//        linesBrush.addAxes(new Vec3(), new OrthonormalBasis(), 1.0f);


        linesBrush.setColor(Color.BLUE);

        //linesBrush.addLine(new Vec3(), binarySpliter);

        LazyInternalAngle angle = new LazyInternalAngle(startTangent.negated(), endTangent);

        float r = 0.5f;
        double h = Math.sqrt(2) * r / Math.sqrt(1 - angle.cosine());
        Circle circle = new Circle(binarySpliter.scaled((float)h).getXY(), r);
        linesBrush.addCircle(circle);

        //linesBrush.addLine(a, b);
        //linesBrush.addLine(b, c);

        float midclip = (float)h * (float)Math.sqrt((1 + angle.cosine()) / 2);
        linesBrush.setColor(Color.RED);
        //linesBrush.addLine(b, a.difference(b).normalized().scale(midclip));
        //linesBrush.addLine(b, c.difference(b).normalized().scale(midclip));

        linesBrush.setColor(Color.WHITE);
        Vec3 inPoint = a.difference(b).normalize().scale(midclip);
        linesBrush.addLine(a, inPoint);
        Vec3 outPoint = c.difference(b).normalize().scale(midclip);
        linesBrush.addLine(c, outPoint);

        double startAngle = inPoint.getXY().difference(circle.center).atan2();
        double endAngle = outPoint.getXY().difference(circle.center).atan2();
        while(endAngle < startAngle) {
            endAngle += 2 * Math.PI;
        }
        int n = Math.max((int)((endAngle - startAngle) / 0.2), 3);
        ArrayList<Vec2> path = new Arc(circle, startAngle, endAngle).getPointsAlong(n);

        ArrayList<Vec3> path3 = new ArrayList<>();
        for(Vec2 p : path) {
            path3.add(new Vec3(p.x, p.y, 0));
        }
        linesBrush.addPath(path3);

        linesBrush.setColor(Color.CYAN);
        linesBrush.addPointer(a, a.sum(startTangent), 0.2f);
        linesBrush.setColor(Color.YELLOW);
        linesBrush.addPointer(c, c.sum(endTangent), 0.2f);


        linesBrush.end();
    }
}
