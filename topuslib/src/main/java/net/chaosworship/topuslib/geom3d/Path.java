package net.chaosworship.topuslib.geom3d;

import net.chaosworship.topuslib.geom2d.Arc;
import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Vec2;

import java.util.ArrayList;


public class Path {

    private Path() {}

    private static ArrayList<Vec3> directPath(Vec3 a, Vec3 b, Vec3 c) {
        ArrayList<Vec3> path = new ArrayList<>();
        path.add(a);
        path.add(b);
        path.add(c);
        return path;
    }

    // curve about b, pass through a and c
    public static ArrayList<Vec3> generateCurve(Vec3 a, Vec3 b, Vec3 c, float maxRadius, float radiansPerSegment) {

        // translate everything to b = (0,0)
        Vec3 aTrans = a.difference(b);
        Vec3 cTrans = c.difference(b);

        LazyInternalAngle angle = new LazyInternalAngle(aTrans, cTrans);

        float minArm = (float)Math.sqrt(Math.min(aTrans.magnitudeSq(), cTrans.magnitudeSq()));
        maxRadius = Math.min(maxRadius, minArm * angle.sine());
        if(maxRadius <= 0 || angle.cosine() > 0.99999f) {
            return directPath(a, b, c);
        }

        float cosHalfAngle = (float)Math.sqrt((1 + angle.cosine()) / 2);
        float tanHalfAngle = (1 - angle.cosine()) / angle.sine();

        if(cosHalfAngle <= 0 || Float.isNaN(cosHalfAngle) || Float.isNaN(tanHalfAngle)) {
            return directPath(a, b, c);
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

        OrthonormalBasis basis = new OrthonormalBasis().setRightHandedW(cTrans, aTrans);

        Vec3 inTangent = new Vec3(0, 1, 0);
        Vec3 outTangent = basis.transformedToStandardBasis(cTrans).normalize();
        Vec3 inPoint = inTangent.scaled(trim);
        Vec3 outPoint = outTangent.scaled(trim);
        basis.transformFromStandardBasis(inPoint);
        basis.transformFromStandardBasis(outPoint);
        inPoint.add(b);
        outPoint.add(b);

        double outAngle = Math.atan2(outTangent.x, -outTangent.y);
        Arc arc = new Arc(new Circle(), 0, outAngle);
        int n = Math.max((int)(Math.abs(outAngle) / radiansPerSegment), 1);

        Vec3 circleCenter = inTangent.sum(outTangent).normalize().scale(trim / cosHalfAngle);

        ArrayList<Vec3> path = new ArrayList<>();
        path.add(a);
        path.add(inPoint);
        for(Vec2 p2 : arc.getPointsAlongOpen(n)) {
            Vec3 p = new Vec3(p2.x, p2.y, 0).scale(-radius).add(circleCenter);
            basis.transformFromStandardBasis(p);
            path.add(p.add(b));
        }
        path.add(outPoint);
        path.add(c);

        return path;
    }
}
