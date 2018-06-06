package net.chaosworship.topuslib.geom3d;


@SuppressWarnings("UnusedReturnValue")
public class OrthonormalBasis {

    // these are expected to be unit length
    public final Vec3 u;
    public final Vec3 v;
    public final Vec3 w;

    @SuppressWarnings("WeakerAccess")
    public OrthonormalBasis() {
        u = new Vec3(1, 0, 0);
        v = new Vec3(0, 1, 0);
        w = new Vec3(0, 0, 1);
    }

    public OrthonormalBasis set(OrthonormalBasis src) {
        this.u.set(src.u);
        this.v.set(src.v);
        this.w.set(src.w);
        return this;
    }

    public OrthonormalBasis renormalize() {
        u.normalize();
        v.normalize();
        w.normalize();
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

    public OrthonormalBasis realignAboutW(Vec3 w) {
        if(this.w.dot(w) == 0) {
            // hacky but effective
            realignAboutW(new Vec3().setMix(this.w, w, 0.5f));
        }
        this.w.set(w).normalize();
        u.setCross(v, this.w).normalize();
        v.setCross(this.w, u);
        return this;
    }

    public OrthonormalBasis setRightHanded(Vec3 unitW, Vec3 unitU) {
        w.set(unitW);
        u.set(unitU);
        v.setCross(u, w);
        if(!isRightHanded()) {
            v.negate();
        }
        return this;
    }

    // ab in [0,1]
    public OrthonormalBasis setMix(OrthonormalBasis a, OrthonormalBasis b, float ab) {
        this.u.setZero().addScaled(a.u, 1 - ab).addScaled(b.u, ab);
        this.v.setZero().addScaled(a.v, 1 - ab).addScaled(b.v, ab);
        this.w.setZero().addScaled(a.w, 1 - ab).addScaled(b.w, ab);
        return this;
    }

    public void transformFromXYZ(Vec3 p) {
        float x = p.x;
        float y = p.y;
        float z = p.z;
        p.setZero().addScaled(u, x).addScaled(v, y).addScaled(w, z);
    }

    public Vec3 transformedFromXYZ(Vec3 p) {
        return new Vec3().addScaled(u, p.x).addScaled(v, p.y).addScaled(w, p.z);
    }

    public boolean isRightHanded() {
        return new Vec3().setCross(u, v).dot(w) > 0;
    }
}
