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
import net.chaosworship.topuslib.gl.FlatViewTransform;
import net.chaosworship.topuslib.gl.ShapesBrush;
import net.chaosworship.topuslib.input.MotionEventConverter;
import net.chaosworship.topuslib.random.SuperRandom;
import net.chaosworship.topuslib.tuple.PointMass;
import net.chaosworship.topuslib.tuple.PointValuePair;
import net.chaosworship.topuslibtest.gl.DotsBrush;
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
        static int nextId = 0;
        final int id;
        final Vec2 pos;
        final Vec2 vel;
        final Vec2 acc;
        float radius;
        float mass;

        float phase;

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

        mBound = new Rectangle(-2.7f, -2.7f, 2.7f, 2.7f);

        mBarnesHut = new BarnesHutTree(mBound);

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
        for(int i = 0; i < 400; i++) {
            Particle p = new Particle();
            p.pos.set(sRandom.uniformInRect(mBound));
            p.vel.setZero();
            float r = sRandom.nextFloat();
            p.radius = 0.03f + (float)Math.pow(r, 3) * 0.07f;
            p.mass = 1.0f * p.radius * p.radius;
            p.phase = sRandom.nextFloat();
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
                if(!mBound.contains(p.pos)) {
                    if(p.vel.dot(p.pos) > 0) {
                        p.vel.setZero();
                    }
                    p.acc.addScaled(p.pos.normalized(), -0.05f);
                }

                p.phase += TIMERATE * 0.1f;
                if(p.phase > 1)
                    p.phase -= 1;
                p.radius = 0.02f + 0.03f * ((float)Math.sin(Math.PI * 2 * p.phase) + 1);
                p.mass = p.radius * p.radius;
            }


            for(Particle p : mParticles) {
                //p.acc.addScaled(p.pos.normalized(), -0.005f);
                //p.acc.addScaled(p.pos.normalized().rotate90(), 0.001f);
            }


            mBarnesHut.clear();
            mBarnesHut.load(mPointMasses);
            Vec2 force = new Vec2();
            for(Particle p : mParticles) {
                force.setZero();
                mBarnesHut.getForce(p.pos, force, p.radius);
                p.acc.addScaled(force, -0.01f);
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
                        float distance = (float)Math.sqrt(distSq);
                        pdiff.scaleInverse(distance);
                        vdiff.setDifference(p.vel, q.vel);
                        float moveApart = (d - distance) / d;
                        p.pos.addScaled(pdiff, 0.8f * moveApart * q.radius);
                        q.pos.addScaled(pdiff, 0.8f * -moveApart * p.radius);
                        p.acc.addScaled(pdiff, 0.2f * moveApart * q.radius / TIMERATE);
                        q.acc.addScaled(pdiff, 0.2f * -moveApart * p.radius / TIMERATE);
                        if(vdiff.dot(pdiff) < 0) {
                            float f = 0.5f * 2 * vdiff.dot(pdiff) / (p.mass + q.mass);
                            p.acc.addScaled(pdiff, q.mass * -f);
                            q.acc.addScaled(pdiff, p.mass * f);
                        }
                    }
                }
            }

            for(Particle p : mParticles) {
                p.vel.add(p.acc);
                //p.vel.scale(0.99f);
                p.pos.addScaled(p.vel, TIMERATE);
                p.acc.setZero();
            }

            Vec2 centroid = new Vec2();
            Vec2 meanVelocity = new Vec2();
            float angularVelocity = 0;
            int inBoundCount = 0;
            for(Particle p : mParticles) {
                if(mBound.contains(p.pos)) {
                    centroid.add(p.pos);
                    meanVelocity.add(p.vel);
                    inBoundCount++;
                }

                angularVelocity += p.vel.dot(p.pos.normalized().rotate90());
            }

            if(inBoundCount > 0) {
                centroid.scaleInverse(inBoundCount);
                meanVelocity.scaleInverse(inBoundCount);


                for(Particle p : mParticles) {
                    if(mBound.contains(p.pos)) {
                        p.pos.addScaled(centroid, -0.01f);
                        p.vel.subtract(meanVelocity);
                        p.vel.addScaled(p.pos.normalized().rotate90(), angularVelocity * -0.3f / inBoundCount);
                    }
                }

                /*
                float meanSpeed = 0;
                for(Particle p : mParticles) {
                    if(mBound.contains(p.pos)) {
                        meanSpeed += p.vel.magnitude();
                    }
                }
                meanSpeed /= inBoundCount;
                float speedAdjust = 0.4f / meanSpeed;
                for(Particle p : mParticles) {
                    if(mBound.contains(p.pos)) {
                        p.vel.scale(speedAdjust);
                    }
                }
                */
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
                dotsBrush.add(p.pos, p.radius, 0.7f);
            }
        }
        dotsBrush.end();

        final ShapesBrush brush = mLoader.getShapesBrush();
        brush.begin(mViewTransform.getViewMatrix());
        brush.setColor(Color.WHITE);
        brush.setAlpha(0.2f);
        brush.drawRectangle(mBound, 0.01f);
        brush.end();
    }
}
