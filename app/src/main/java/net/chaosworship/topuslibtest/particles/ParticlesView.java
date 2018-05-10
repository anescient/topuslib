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

        private Particle() {
            id = nextId++;
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
    private final KDTree<Particle> mNeighborSearch;
    private Rectangle mBound;

    public ParticlesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLoader = new TestLoader(context);

        mViewTransform = new FlatViewTransform();
        mViewTransform.setViewCenter(new Vec2(0, 0));
        mViewTransform.setViewZoom(400.0f);

        mInputConverter = new MotionEventConverter();

        mParticles = new ArrayList<>();
        mNeighborSearch = new KDTree<>();

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

    private void setParticles() {
        mParticles.clear();
        ArrayList<PointValuePair<Particle>> ppvps = new ArrayList<>();
        for(int i = 0; i < 1500; i++) {
            Particle p = new Particle();
            p.pos = sRandom.uniformUnit().scale(0.7f + 0.2f * sRandom.nextFloat());
            p.vel = sRandom.uniformUnit().scale(1.0f * sRandom.nextFloat());
            mParticles.add(p);
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
                    if((p.pos.x < mBound.minx && p.vel.x < 0) || (p.pos.x > mBound.maxx && p.vel.x > 0)) {
                        p.vel.x *= -0.9f;
                        p.vel.y *= 0.5f;
                        p.vel.setZero();
                    }
                    if((p.pos.y < mBound.miny && p.vel.y < 0) || (p.pos.y > mBound.maxy && p.vel.y > 0)) {
                        p.vel.y *= -0.9f;
                        p.vel.x *= 0.5f;
                        p.vel.setZero();
                    }
                }
            }

            /*
            for(Particle p : mParticles) {
                p.acc.addScaled(p.pos.normalized(), -0.004f);
            }*/

            BarnesHutTree bht = new BarnesHutTree(mBound);
            ArrayList<PointMass> pointMasses = new ArrayList<>();
            for(Particle p : mParticles) {
                pointMasses.add(new PointMass(p.pos, 1));
            }
            bht.load(pointMasses);

            for(int i = 0; i < mParticles.size(); i++) {
                Particle pi = mParticles.get(i);
                Vec2 force = bht.getForce(pi.pos);
                force.clampMagnitude(0, 10);
                pi.acc.addScaled(force, -0.0001f);
            }

            /*
            for(int i = 0; i < mParticles.size(); i++) {
                Particle pi = mParticles.get(i);
                for(int j = i + 1; j < mParticles.size(); j++) {
                    Particle pj = mParticles.get(j);
                    Vec2 pdiff = Vec2.difference(pj.pos, pi.pos);
                    float dist = pdiff.magnitude();
                    if(dist <= 0) {
                        continue;
                    }
                    pdiff.scaleInverse(dist);
                    float f = 0.000001f / (dist * dist);
                    f = Math.min(f, 0.01f);
                    pi.acc.addScaled(pdiff, f);
                    pj.acc.addScaled(pdiff, -f);
                }
            }
            */

            mNeighborSearch.reload();
            Rectangle searchRect = new Rectangle();

            float d = 0.02f;
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
                        if(vdot < 0) {
                            float f = (d - distance) / d;
                            float f1 = 0.01f + 1f * f * f;
                            if(f > 0.75f)
                                f1 = 1.5f;
                            p.acc.addScaled(diff, f1);
                            q.acc.addScaled(diff, -f1);
                            //float f2 = 0.05f * f * vdot;
                            //p.acc.addScaled(vdiff, f2);
                            //q.acc.addScaled(vdiff, -f2);
                        }
                    }
                }
            }

            for(Particle p : mParticles) {
                p.vel.add(p.acc);
                //p.vel.scale(0.99f);
                p.pos.addScaled(p.vel, 0.01f);
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
                brush.drawSpot(p.pos, 0.015f);
            }
        }

        brush.end();
    }
}
