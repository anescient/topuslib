package net.chaosworship.topuslib.geom3d;

import net.chaosworship.topuslib.geom2d.Arc;
import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.gl.FloatVertexPreBuffer;

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
    public static ArrayList<Vec3> generateCurve(Vec3 a, Vec3 b, Vec3 c, float maxRadius) {

        // translate everything to b = (0,0)
        a = a.difference(b);
        c = c.difference(b);

        maxRadius = Math.min(maxRadius, Vec3.distance(a, c) / 2);
        if(maxRadius <= 0) {
            return directPath(a.sum(b), b, c.sum(b));
        }

        OrthonormalBasis basis = new OrthonormalBasis().setRightHandedW(a, c);

        Vec3 aTrans = basis.transformedToStandardBasis(a);
        Vec3 cTrans = basis.transformedToStandardBasis(c);

        Vec3 startTangent = aTrans.normalized().negate();
        Vec3 endTangent = cTrans.normalized();

        LazyInternalAngle angle = new LazyInternalAngle(startTangent.negated(), endTangent);

        float cosHalfAngle = (float)Math.sqrt((1 + angle.cosine()) / 2);
        float tanHalfAngle = (1 - angle.cosine()) / angle.sine();

        if(cosHalfAngle <= 0 || Float.isNaN(cosHalfAngle) || Float.isNaN(tanHalfAngle)) {
            return directPath(a.sum(b), b, c.sum(b));
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

        float h = trim / cosHalfAngle;

        Vec3 inPoint = aTrans.normalized().scale(trim);
        Vec3 outPoint = cTrans.normalized().scale(trim);
        double startAngle = Math.PI + inPoint.getXY().rotated90().negated().atan2();
        double endAngle = Math.PI + outPoint.getXY().rotated90().atan2();

        basis.transformFromStandardBasis(inPoint);
        basis.transformFromStandardBasis(outPoint);

        Arc arc = new Arc(new Circle(), startAngle, endAngle);
        int n = Math.max((int)(Math.abs(endAngle - startAngle) / 0.1), 3);

        Vec3 circleCenter = startTangent.negated().sum(endTangent).normalize().scale(h);

        ArrayList<Vec3> path = new ArrayList<>();
        path.add(a.sum(b));
        path.add(inPoint.sum(b));
        for(Vec2 p2 : arc.getPointsAlongOpen(n)) {
            Vec3 p = basis.transformedFromStandardBasis(new Vec3(p2.x, p2.y, 0).scaled(radius).sum(circleCenter));
            path.add(p.add(b));
        }
        path.add(outPoint.sum(b));
        path.add(c.sum(b));

        return path;
    }
}
