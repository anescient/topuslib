package net.chaosworship.topuslib.geom3d;


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

    public void setArbitraryAboutW(Vec3 w) {
        this.w.set(w).normalize();
        u.setArbitraryPerpendicular(this.w).normalize();
        v.setCross(this.w, u);
    }

    public void realignAboutW(Vec3 w) {
        this.w.set(w).normalize();
        u.setCross(v, this.w).normalize();
        v.setCross(this.w, u);
    }

    public void setRightHanded(Vec3 unitW, Vec3 unitU) {
        w.set(unitW);
        u.set(unitU);
        v.setCross(u, w);
        if(!isRightHanded()) {
            v.negate();
        }
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
