package net.chaosworship.topuslib.geom3d;

import java.util.Collection;


public class Path {

    private static final Vec3 sATrans = new Vec3();
    private static final Vec3 sCTrans = new Vec3();
    private static final OrthonormalBasis sBasis = new OrthonormalBasis();
    private static final Vec3 sInTangent = new Vec3();
    private static final Vec3 sOutTangent = new Vec3();
    private static final Vec3 sCircleCenter = new Vec3();

    private Path() {}

    // curve about b, pass through a and c
    public static void generateCurve(Collection<Vec3> appendPath, Vec3 a, Vec3 b, Vec3 c, float maxRadius, float radiansPerSegment) {

        // translate everything to b = (0,0)
        sATrans.setDifference(a, b);
        sCTrans.setDifference(c, b);

        LazyInternalAngle angle = new LazyInternalAngle(sATrans, sCTrans);

        float minArm = (float)Math.sqrt(Math.min(sATrans.magnitudeSq(), sCTrans.magnitudeSq()));
        maxRadius = Math.min(maxRadius, minArm * angle.sine());
        if(maxRadius <= 0 || angle.cosine() > 0.99999f) {
            appendPath.add(a);
            appendPath.add(b);
            appendPath.add(c);
            return;
        }

        float cosHalfAngle = (float)Math.sqrt((1 + angle.cosine()) / 2);
        float tanHalfAngle = (1 - angle.cosine()) / angle.sine();

        if(cosHalfAngle <= 0 || Float.isNaN(cosHalfAngle) || Float.isNaN(tanHalfAngle)) {
            appendPath.add(a);
            appendPath.add(b);
            appendPath.add(c);
            return;
        }

        float radius;
        float trim;
        if(angle.cosine() > 0) {
            // sharper than 90 deg., reduce radius
            trim = maxRadius;
            radius = trim * tanHalfAngle;
        } else {
            // 90 deg. or wider
            radius = maxRadius;
            trim = radius / tanHalfAngle;
        }

        sBasis.setRightHandedW(sCTrans, sATrans);

        sInTangent.set(0, 1, 0);
        sOutTangent.set(sCTrans);
        sBasis.transformToStandardBasis(sOutTangent);
        sOutTangent.normalize();
        Vec3 inPoint = sInTangent.scaled(trim);
        Vec3 outPoint = sOutTangent.scaled(trim);
        sBasis.transformFromStandardBasis(inPoint);
        sBasis.transformFromStandardBasis(outPoint);
        inPoint.add(b);
        outPoint.add(b);

        double outAngle = Math.atan2(sOutTangent.x, -sOutTangent.y);
        int n = Math.max((int)(Math.abs(outAngle) / radiansPerSegment), 1);

        sCircleCenter.setSum(sInTangent, sOutTangent).normalize().scale(trim / cosHalfAngle);

        appendPath.add(new Vec3(a));
        appendPath.add(inPoint);
        for(int i = 0; i < n; i++) {
            double theta = (double)(i + 1) * (outAngle) / (n + 1);
            Vec3 p = new Vec3((float)Math.cos(theta), (float)Math.sin(theta), 0).scale(-radius).add(sCircleCenter);
            sBasis.transformFromStandardBasis(p);
            appendPath.add(p.add(b));
        }
        appendPath.add(outPoint);
        appendPath.add(new Vec3(c));
    }
}
