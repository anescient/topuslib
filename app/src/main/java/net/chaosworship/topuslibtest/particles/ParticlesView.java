package net.chaosworship.topuslibtest.particles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.gl.FlatViewTransform;
import net.chaosworship.topuslib.gl.RectViewTransform;
import net.chaosworship.topuslib.gl.ShapesBrush;
import net.chaosworship.topuslib.input.MotionEventConverter;
import net.chaosworship.topuslib.random.SuperRandom;
import net.chaosworship.topuslibtest.gl.TestLoader;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;


class ParticlesView
        extends GLSurfaceView
        implements GLSurfaceView.Renderer {

    private static class Particle {
        Vec2 pos;
        Vec2 vel;
        Vec2 acc;

        private Particle() {
            pos = new Vec2();
            vel = new Vec2();
            acc = new Vec2();
        }
    }


    private static final SuperRandom sRandom = new SuperRandom();
    private final TestLoader mLoader;
    private final FlatViewTransform mViewTransform;
    private final MotionEventConverter mInputConverter;

    private final ArrayList<Particle> mParticles;
    private Rectangle mBound;

    public ParticlesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLoader = new TestLoader(context);

        mViewTransform = new FlatViewTransform();
        mViewTransform.setViewCenter(new Vec2(0, 0));
        mViewTransform.setViewZoom(400.0f);

        mInputConverter = new MotionEventConverter();

        mParticles = new ArrayList<>();

        mBound = new Rectangle(-1.2f, -1.5f, 1.2f, 1.5f);

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(false);
        setRenderer(this);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mInputConverter.pushEvent(e);
        return true;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mLoader.invalidateAll();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mLoader.invalidateAll();
        mViewTransform.setViewport(width, height);
    }

    public void setZoom(float zoom) {
        mViewTransform.setViewZoom(400 * zoom);
    }

    public void reset() {
        synchronized(mParticles) {
            mParticles.clear();
        }
    }

    private void createParticles() {
        mParticles.clear();
        for(int i = 0; i < 1200; i++) {
            Particle p = new Particle();
            p.pos = sRandom.uniformUnit().scale(0.7f + 0.2f * sRandom.nextFloat());
            p.vel = sRandom.uniformUnit().scale(0.01f);
            mParticles.add(p);
        }
    }

    private void step() {
        synchronized(mParticles) {

            if(mParticles.isEmpty()) {
                createParticles();
            }

            for(Particle p : mParticles) {
                if(!mBound.contains(p.pos)) {
                    if((p.pos.x < mBound.minx && p.vel.x < 0) || (p.pos.x > mBound.maxx && p.vel.x > 0)) {
                        p.vel.x *= -0.9f;
                        p.vel.y *= 0.5f;
                    }
                    if((p.pos.y < mBound.miny && p.vel.y < 0) || (p.pos.y > mBound.maxy && p.vel.y > 0)) {
                        p.vel.y *= -0.9f;
                        p.vel.x *= 0.5f;
                    }
                }
            }

            float d = 0.5f;
            for(int i = 0; i < mParticles.size(); i++) {
                Particle p1 = mParticles.get(i);
                for(int j = i + 1; j < mParticles.size(); j++) {
                    Particle p2 = mParticles.get(j);
                    Vec2 diff = p1.pos.difference(p2.pos);
                    float distance = diff.magnitude();
                    diff.scaleInverse(distance);
                    if(false){//distance < d) {
                        Vec2 vdiff = p1.vel.difference(p2.vel);
                        float f = 0.00001f * (d - distance) / d;
                        p1.acc.addScaled(vdiff, -f);
                        p2.acc.addScaled(vdiff, f);
                        p1.acc.addScaled(diff, -0.00001f);
                        p2.acc.addScaled(diff, 0.00001f);
                    }
                    float g = 0.000001f / (1 + distance * distance);
                    p1.acc.addScaled(diff, -g);
                    p2.acc.addScaled(diff, g);
                }
            }

            for(Particle p : mParticles) {
                p.vel.add(p.acc);
                //p.vel.scale(0.99f);
                p.pos.add(p.vel);
                p.acc.setZero();
            }

            Vec2 centroid = new Vec2();
            for(Particle p : mParticles) {
                centroid.add(p.pos);
            }
            centroid.scaleInverse(mParticles.size());
            for(Particle p : mParticles) {
                p.pos.subtract(centroid);
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        step();

        mViewTransform.callGlViewport();
        glClearColor(0, 0, 0.2f, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        final ShapesBrush brush = mLoader.getShapesBrush();
        brush.begin(mViewTransform.getViewMatrix());

        brush.setColor(Color.WHITE);

        brush.setAlpha(0.2f);
        brush.drawRectangle(mBound, 0.01f);

        brush.setAlpha(1f);
        synchronized(mParticles) {
            for(Particle p : mParticles) {
                brush.drawSpot(p.pos, 0.005f);
            }
        }

        brush.end();
    }
}
