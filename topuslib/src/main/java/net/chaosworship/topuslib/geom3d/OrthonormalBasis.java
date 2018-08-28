package net.chaosworship.topuslib.geom3d;


import net.chaosworship.topuslib.geom3d.transform.AxisAngleRotator;

@SuppressWarnings("UnusedReturnValue")
public class OrthonormalBasis {

    // these are expected to be unit length
    public final Vec3 u;
    public final Vec3 v;
    public final Vec3 w;

    private AxisAngleRotator mRotator;
    private Vec3 mCrossTemp;

    @SuppressWarnings("WeakerAccess")
    public OrthonormalBasis() {
        u = new Vec3(1, 0, 0);
        v = new Vec3(0, 1, 0);
        w = new Vec3(0, 0, 1);
        mRotator = null;
        mCrossTemp = new Vec3();
    }

    public OrthonormalBasis set(OrthonormalBasis src) {
        this.u.set(src.u);
        this.v.set(src.v);
        this.w.set(src.w);
        mRotator = null;
        return this;
    }

    public OrthonormalBasis setArbitraryAboutW(Vec3 w) {
        this.w.set(w).normalize();
        u.setArbitraryPerpendicular(this.w).normalize();
        v.setCross(this.w, u);
        if(!isRightHanded()) {
            v.negate();
        }
        return this;
    }

    public OrthonormalBasis rotateToW(Vec3 w) {
        if(mRotator == null) {
            mRotator = new AxisAngleRotator();
        }
        mRotator.capture(this.w, w);
        mRotator.rotate(this.u);
        mRotator.rotate(this.v);
        mRotator.rotate(this.w);
        return this;
    }

    public OrthonormalBasis setRightHandedU(Vec3 w, Vec3 v) {
        this.w.set(w).normalize();
        this.v.set(v);
        this.u.setCross(this.v, this.w).normalize();
        if(!isRightHanded()) {
            this.u.negate();
        }
        this.v.setCross(this.w, this.u);
        return this;
    }

    public OrthonormalBasis setRightHandedV(Vec3 w, Vec3 u) {
        this.w.set(w).normalize();
        this.u.set(u);
        this.v.setCross(this.u, this.w).normalize();
        if(!isRightHanded()) {
            this.v.negate();
        }
        this.u.setCross(this.v, this.w);
        return this;
    }

    public OrthonormalBasis setRightHandedW(Vec3 u, Vec3 v) {
        this.u.set(u);
        this.v.set(v).normalize();
        this.w.setCross(this.v, this.u).normalize();
        if(!isRightHanded()) {
            this.w.negate();
        }
        this.u.setCross(this.v, this.w);
        return this;
    }

    // ab in [0,1]
    public OrthonormalBasis setMix(OrthonormalBasis a, OrthonormalBasis b, float ab) {
        this.u.setZero().addScaled(a.u, 1 - ab).addScaled(b.u, ab);
        this.v.setZero().addScaled(a.v, 1 - ab).addScaled(b.v, ab);
        this.w.setZero().addScaled(a.w, 1 - ab).addScaled(b.w, ab);
        return this;
    }

    public OrthonormalBasis negate() {
        u.negate();
        v.negate();
        w.negate();
        return this;
    }

    public void transformFromStandardBasis(Vec3 p) {
        float x = p.x;
        float y = p.y;
        float z = p.z;
        p.setZero().addScaled(u, x).addScaled(v, y).addScaled(w, z);
    }

    public Vec3 transformedFromStandardBasis(Vec3 p) {
        return new Vec3().addScaled(u, p.x).addScaled(v, p.y).addScaled(w, p.z);
    }

    public void transformToStandardBasis(Vec3 p) {
        p.set(u.dot(p), v.dot(p), w.dot(p));
    }

    public Vec3 transformedToStandardBasis(Vec3 p) {
        Vec3 q = new Vec3(p);
        transformToStandardBasis(q);
        return q;
    }

    public boolean isRightHanded() {
        mCrossTemp.setCross(u, v);
        return mCrossTemp.dot(w) > 0;
    }
}
