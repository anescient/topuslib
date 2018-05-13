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
        Vec2 pos;
        Vec2 vel;
        Vec2 acc;
        float age;

        private Particle() {
            id = nextId++;
            pos = new Vec2();
            vel = new Vec2();
            acc = new Vec2();
            age = 0;
        }
    }


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

        mBound = new Rectangle(-2.4f, -4.2f, 2.4f, 4.2f);

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
        for(int i = 0; i < 4000; i++) {
            Particle p = new Particle();
            p.pos = sRandom.uniformInRect(mBound);//sRandom.uniformUnit().scale(0.7f + 0.2f * sRandom.nextFloat());
            p.vel = sRandom.uniformUnit().scale(0.1f * sRandom.nextFloat());
            mParticles.add(p);
            mPointMasses.add(new PointMass(p.pos, 1));
            ppvps.add(new PointValuePair<>(p.pos, p));
        }
        mNeighborSearch.load(ppvps);
    }

    private void step() {
        synchronized(mParticles) {

            if(mParticles.isEmpty()) {
                setParticles();
            }


            Vec2 avgVel = null;
            for(Particle p : mParticles) {
                if(!mBound.contains(p.pos)) {
                    if(avgVel == null) {
                        avgVel = new Vec2();
                        for(Particle cp : mParticles) {
                            avgVel.add(cp.vel);
                        }
                        avgVel.scaleInverse(mParticles.size());
                    }
                    //p.pos.setUnit(sRandom.nextDouble() * Math.PI * 2).scale(sRandom.nextFloat());
                    p.pos.set(sRandom.uniformInRect(mBound));
                    p.vel.set(avgVel).negate().scale(3f);
                    p.age = 0;
                }
            }


            for(Particle p : mParticles) {
                //p.acc.addScaled(p.pos.normalized(), -0.02f);
                p.acc.addScaled(p.pos.normalized().rotate90(), 0.001f);
            }


            mBarnesHut.clear();
            mBarnesHut.load(mPointMasses);
            Vec2 force = new Vec2();
            for(int i = 0; i < mParticles.size(); i++) {
                Particle pi = mParticles.get(i);
                force.setZero();
                mBarnesHut.getForce(pi.pos, force);
                force.clampMagnitude(0, 100.0f);
                pi.acc.addScaled(force, -0.0004f);
            }

            mNeighborSearch.reload();
            Rectangle searchRect = new Rectangle();

            float d = 0.05f;
            for(Particle p : mParticles) {
                searchRect.setWithCenter(p.pos, 2 * d, 2 * d);
                for(Particle q : mNeighborSearch.search(searchRect)) {
                    if(q.id <= p.id)
                        continue;
                    Vec2 diff = p.pos.difference(q.pos);
                    float distance = diff.magnitude();
                    diff.scaleInverse(distance);
                    if(distance < d) {
                        Vec2 vdiff = p.vel.difference(q.vel);
                        float vdot = vdiff.dot(diff);
                        float f = (d - distance) / d;
                        float f1 = 1f * f - 0.05f * vdot;
                        p.acc.addScaled(diff, f1);
                        q.acc.addScaled(diff, -f1);
                        //float f2 = 0.05f * f * vdot;
                        //p.acc.addScaled(vdiff, f2);
                        //q.acc.addScaled(vdiff, -f2);
                    }
                }
            }

            for(Particle p : mParticles) {
                p.vel.add(p.acc);
                //p.vel.scale(0.999f);
                p.pos.addScaled(p.vel, 0.01f);
                p.acc.setZero();
                p.age += 0.01f;
                //if(p.age > 1)
                //    p.age = 1;
            }

            /*
            Vec2 centroid = new Vec2();
            int centroidCount = 0;
            for(Particle p : mParticles) {
                if(mBound.contains(p.pos)) {
                    centroid.add(p.pos);
                    centroidCount++;
                }
            }
            if(centroidCount > 0) {
                centroid.scaleInverse(centroidCount);
                for(Particle p : mParticles) {
                    if(mBound.contains(p.pos)) {
                        p.pos.addScaled(centroid, -0.05f);
                    }
                }
            }
            */
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
                float a = Math.min(p.age, (float)(Math.sin(p.age) * 0.5 + 0.5) * 0.5f + 0.2f);
                dotsBrush.add(p.pos, 0.07f, a);
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
