package net.chaosworship.topuslibtest.inputtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.transform.Vec2Transformer;
import net.chaosworship.topuslib.gl.view.FlatViewTransform;
import net.chaosworship.topuslib.gl.FlatShapesBrush;
import net.chaosworship.topuslib.input.MotionEventConverter;
import net.chaosworship.topuslibtest.gl.TestLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;


public class InputTestView
        extends GLSurfaceView
        implements GLSurfaceView.Renderer {

    private static boolean GOFAST = true;

    private final TestLoader mLoader;
    private final FlatViewTransform mViewTransform;
    private final MotionEventConverter mInputConverter;

    private final Vec2 mPosition;
    private MotionEventConverter.Pointer mPointer;

    private Timer mTicker;

    private long mLastTimestamp;

    public InputTestView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLoader = new TestLoader(context);
        mViewTransform = new FlatViewTransform();
        mInputConverter = new MotionEventConverter();

        mPosition = new Vec2();
        mPointer = null;

        mTicker = null;

        mLastTimestamp = 0;

        mViewTransform.setViewZoom(300);

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(false);
        setRenderer(this);
        setRenderMode(GOFAST ? RENDERMODE_CONTINUOUSLY : RENDERMODE_WHEN_DIRTY);
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

        if(!GOFAST) {
            if(mTicker != null) {
                mTicker.cancel();
                mTicker = null;
            }
            mTicker = new Timer();
            mTicker.schedule(new TimerTask() {
                @Override
                public void run() {
                    requestRender();
                }
            }, 50, 50);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        long t = SystemClock.uptimeMillis();

        if(mPointer == null || !mPointer.isActive()) {
            List<MotionEventConverter.Pointer> pointers = mInputConverter.getActivePointers();
            if(!pointers.isEmpty()) {
                mPointer = pointers.get(0);
            }
        }

        if(mPointer != null) {
            Vec2Transformer viewWorldTransform = mViewTransform.getViewToWorldTransformer();
            mPosition.set(viewWorldTransform.transform(mPointer.getLastPosition()));
            //mPosition.set(viewWorldTransform.transform(mPointer.getFuturePosition(t + 20)));
            //mPosition.add(0, 1);
        }

        mViewTransform.callGlViewport();
        glClearColor(0, 0.2f, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        FlatShapesBrush brush = mLoader.getFlatShapesBrush();
        brush.begin(mViewTransform.getViewMatrix());
        brush.setColor(Color.WHITE);
        brush.setAlpha(1);
        brush.drawSpot(mPosition, 0.1f);
        brush.drawCircle(new Circle(mPosition, 0.3f), 0.02f);
        brush.end();
    }
}
