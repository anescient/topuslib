package net.chaosworship.topuslibtest.particles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.barneshut.BarnesHutTree;
import net.chaosworship.topuslib.geom2d.rangesearch.KDTree;
import net.chaosworship.topuslib.geom2d.transform.Vec2Transformer;
import net.chaosworship.topuslib.gl.view.FlatViewTransform;
import net.chaosworship.topuslib.gl.FlatShapesBrush;
import net.chaosworship.topuslib.input.MotionEventConverter;
import net.chaosworship.topuslib.random.SuperRandom;
import net.chaosworship.topuslib.tuple.PointMass;
import net.chaosworship.topuslib.tuple.PointValuePair;
import net.chaosworship.topuslibtest.gl.DotsBrush;
import net.chaosworship.topuslibtest.gl.TestLoader;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;


class ParticlesView
        extends GLSurfaceView
        implements GLSurfaceView.Renderer {

    private static class Particle {
        static int nextId = 0;
        final int id;
        final Vec2 pos;
        final Vec2 vel;
        final Vec2 acc;
        float radius;
        float mass;
        float phase;
        float involvement;
        float lightness;

        private Particle() {
            id = nextId++;
            pos = new Vec2();
            vel = new Vec2();
            acc = new Vec2();
            radius = 0.1f;
            mass = 1;
        }
    }

    private static final float TIMERATE = 0.01f;

    private static final SuperRandom sRandom = new SuperRandom();
    private final TestLoader mLoader;
    private final FlatViewTransform mViewTransform;
    private final MotionEventConverter mInputConverter;

    private final ArrayList<Particle> mParticles;
    private final ArrayList<PointMass> mPointMasses;
    private final KDTree<Particle> mNeighborSearch;
    private Rectangle mBound;
    private final BarnesHutTree mBarnesHut;

    private Timer mTicker;

    public ParticlesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLoader = new TestLoader(context);

        mViewTransform = new FlatViewTransform();
        mViewTransform.setViewCenter(new Vec2(0, 0));
        mViewTransform.setViewZoom(200.0f);

        mInputConverter = new MotionEventConverter();

        mParticles = new ArrayList<>();
        mPointMasses = new ArrayList<>();
        mNeighborSearch = new KDTree<>();

        mBound = new Rectangle(-2.6f, -3.9f, 2.6f, 3.9f);

        mBarnesHut = new BarnesHutTree(mBound, 0.2f);

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(false);
        setRenderer(this);

        //setRenderMode(RENDERMODE_CONTINUOUSLY);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    private void start() {
        stop();
        mTicker = new Timer();
        mTicker.schedule(new TimerTask() {
            @Override
            public void run() {
                requestRender();
            }
        }, 30, 30);

    }

    private void stop() {
        if(mTicker != null) {
            mTicker.cancel();
            mTicker = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        start();
    }

    @Override
    public void onPause() {
        super.onPause();
        stop();
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
        mViewTransform.setViewZoom(200 * zoom);
    }

    public void reset() {
        synchronized(mParticles) {
            mParticles.clear();
        }
    }

    private void setParticles() {
        mParticles.clear();
        mPointMasses.clear();
        ArrayList<PointValuePair<Particle>> ppvps = new ArrayList<>();
        for(int i = 0; i < 500; i++) {
            Particle p = new Particle();
            p.pos.set(sRandom.uniformInRect(mBound));
            p.vel.setZero();
            float r = sRandom.nextFloat();
            p.radius = 0.03f + (float)Math.pow(r, 3) * 0.03f;
            p.mass = 1.0f * p.radius * p.radius;
            p.phase = sRandom.nextFloat();
            p.involvement = 0;
            p.lightness = 0.5f;
            mParticles.add(p);
            mPointMasses.add(new PointMass(p.pos, p.mass));
            ppvps.add(new PointValuePair<>(p.pos, p));
        }
        mNeighborSearch.load(ppvps);
    }

    private void step() {
        synchronized(mParticles) {

            if(mParticles.isEmpty()) {
                setParticles();
            }

            for(Particle p : mParticles) {
                if(p.pos.x < mBound.minx && p.vel.x < 0 || p.pos.x > mBound.maxx && p.vel.x > 0) {
                    p.vel.x *= -1;
                }

                p.phase = (p.phase + TIMERATE * 0.1f) % 1f;
                p.radius = 0.04f + 0.1f * p.involvement * p.involvement;
                p.mass = p.radius * p.radius;
            }


            for(Particle p : mParticles) {
                p.acc.addScaled(new Vec2(0, -1), 0.005f);
            }


            float meanMass = 0;
            for(Particle p : mParticles) {
                meanMass += p.mass;
            }
            meanMass /= mParticles.size();

            Vec2Transformer inputTransform = mViewTransform.getViewToWorldTransformer();
            for(MotionEventConverter.Pointer ptr : mInputConverter.getActivePointers()) {
                if(ptr.isActive()) {
                    Vec2 touch = inputTransform.transform(ptr.getLastPosition());
                    for(Particle p : mParticles) {
                        float distanceSq = Vec2.distanceSq(touch, p.pos);
                        Vec2 pullNormal = Vec2.difference(touch, p.pos).normalize();
                        p.acc.addScaled(pullNormal, 0.3f / (1 + distanceSq));
                    }
                }
            }

            mBarnesHut.clear();
            mBarnesHut.load(mPointMasses, meanMass);
            Vec2 force = new Vec2();
            for(Particle p : mParticles) {
                force.setZero();
                mBarnesHut.getForce(p.pos, force, p.radius);
                p.acc.addScaled(force, -0.08f);
            }

            mNeighborSearch.reload();
            Rectangle searchRect = new Rectangle();

            float maxradius = 0;
            for(Particle p : mParticles) {
                maxradius = Math.max(maxradius, p.radius);
            }
            Vec2 pdiff = new Vec2();
            Vec2 vdiff = new Vec2();
            for(Particle p : mParticles) {
                float searchRadius = maxradius + p.radius;
                searchRect.setWithCenter(p.pos, 2 * searchRadius, 2 * searchRadius);
                for(Particle q : mNeighborSearch.search(searchRect)) {
                    if(q.id <= p.id)
                        continue;
                    float d = q.radius + p.radius;
                    pdiff.setDifference(p.pos, q.pos);
                    float distSq = pdiff.magnitudeSq();
                    if(distSq < d * d) {
                        p.involvement += 0.01;
                        q.involvement += 0.01;
                        float distance = (float)Math.sqrt(distSq);
                        pdiff.scaleInverse(distance);
                        vdiff.setDifference(p.vel, q.vel);
                        float moveApart = (d - distance) / d;
                        p.pos.addScaled(pdiff, 0.8f * moveApart * q.radius);
                        q.pos.addScaled(pdiff, 0.8f * -moveApart * p.radius);
                        p.acc.addScaled(pdiff, 0.2f * moveApart * q.radius / TIMERATE);
                        q.acc.addScaled(pdiff, 0.2f * -moveApart * p.radius / TIMERATE);
                        if(vdiff.dot(pdiff) < 0) {
                            float f = 0.2f * 2 * vdiff.dot(pdiff) / (p.mass + q.mass);
                            p.acc.addScaled(pdiff, q.mass * -f);
                            q.acc.addScaled(pdiff, p.mass * f);
                        }
                    }
                }
            }

            for(Particle p : mParticles) {
                p.vel.add(p.acc);
                if(p.vel.magnitudeSq() > 10) {
                    p.vel.scale(0.9f);
                }
                //p.vel.scale(0.99f);
                p.pos.addScaled(p.vel, TIMERATE);
                float lightness = p.acc.magnitude();
                if(lightness < p.lightness)
                    p.lightness = 0.9f * p.lightness + 0.1f * lightness;
                else
                    p.lightness = 0.5f * p.lightness + 0.5f * lightness;
                p.acc.setZero();
                p.involvement *= 0.95f;
                if(p.involvement > 1) {
                    p.involvement = 1;
                }
            }

            Vec2 meanVelocity = new Vec2();
            for(Particle p : mParticles) {
                if(p.pos.y < -3 && p.vel.y < 0) {
                    if(sRandom.nextInt(100) < 95) {
                        p.vel.y *= -1.5;
                    } else {
                        p.pos.set(sRandom.uniformInRect(mBound));
                        p.pos.y = 3;
                        p.vel.y *= 0.5f;
                    }
                }
                if(p.pos.x > mBound.maxx && p.vel.x > 0) {
                    p.vel.x *= -0.75;
                }
                if(p.pos.x < mBound.minx && p.vel.x < 0) {
                    p.vel.x *= -0.75;
                }
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        step();

        mViewTransform.callGlViewport();
        glClearColor(0, 0, 0.2f, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        final DotsBrush dotsBrush = mLoader.getDotsBrush();
        dotsBrush.begin(mViewTransform.getViewMatrix());
        synchronized(mParticles) {
            for(Particle p : mParticles) {
                dotsBrush.add(p.pos, p.radius, 0.3f + 0.7f * p.lightness);
            }
        }
        dotsBrush.end();

        final FlatShapesBrush brush = mLoader.getFlatShapesBrush();
        brush.begin(mViewTransform.getViewMatrix());
        brush.setColor(Color.WHITE);
        brush.setAlpha(0.2f);
        brush.drawRectangle(mBound, 0.01f);
        brush.end();
    }
}
