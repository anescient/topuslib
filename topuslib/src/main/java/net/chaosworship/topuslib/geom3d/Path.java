package net.chaosworship.topuslib.geom3d;

import net.chaosworship.topuslib.geom2d.Arc;
import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Vec2;

import java.util.ArrayList;


public class Path {

    private Path() {}

    // curve about b, pass through a and c
    public static ArrayList<Vec3> generateCurve(Vec3 a, Vec3 b, Vec3 c, float maxRadius) {

        OrthonormalBasis basis = new OrthonormalBasis().setRightHandedW(a.difference(b), c.difference(b));

        Vec3 aTrans = basis.transformedToStandardBasis(a);
        Vec3 bTrans = basis.transformedToStandardBasis(b);
        Vec3 cTrans = basis.transformedToStandardBasis(c);

        aTrans.subtract(bTrans);
        cTrans.subtract(bTrans);
        bTrans.setZero();

        Vec3 abTrans = bTrans.difference(aTrans);
        Vec3 baTrans = aTrans.difference(bTrans);
        Vec3 bcTrans = cTrans.difference(bTrans);
        Vec3 startTangent = abTrans.normalized();
        Vec3 endTangent = bcTrans.normalized();
        Vec3 binarySplitter = startTangent.negated().sum(endTangent).normalize();

        LazyInternalAngle angle = new LazyInternalAngle(startTangent.negated(), endTangent);

        float cornerTrim = 2 * maxRadius * (float)Math.sqrt((1 + angle.cosine()) / 2);
        float radius = (float)Math.sqrt(4 * maxRadius * maxRadius - cornerTrim * cornerTrim);

        Vec3 inPoint = baTrans.normalize().scale(cornerTrim);
        Vec3 outPoint = bcTrans.normalize().scale(cornerTrim);
        Circle circle = new Circle(new Vec2(0, 0), radius);
        double startAngle = Math.PI + inPoint.getXY().rotated90().negated().atan2();
        double endAngle = Math.PI + outPoint.getXY().rotated90().atan2();

        basis.transformFromStandardBasis(inPoint);
        basis.transformFromStandardBasis(outPoint);
        inPoint.add(b);
        outPoint.add(b);

        Arc arc = new Arc(circle, startAngle, endAngle);
        int n = Math.max((int)(Math.abs(endAngle - startAngle) / 0.1), 3);

        ArrayList<Vec3> path = new ArrayList<>();
        path.add(a);
        path.add(inPoint);
        for(Vec2 p2 : arc.getPointsAlong(n)) {
            Vec3 p = basis.transformedFromStandardBasis(new Vec3(p2.x, p2.y, 0));
            path.add(p.sum(basis.transformedFromStandardBasis(binarySplitter).scaled(2 * maxRadius).add(b)));
        }
        path.add(outPoint);
        path.add(c);
        return path;
    }
}
