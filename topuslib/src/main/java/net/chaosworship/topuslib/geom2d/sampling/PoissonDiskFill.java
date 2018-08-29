package net.chaosworship.topuslib.geom2d.sampling;

import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.rangesearch.FixedKDTree;
import net.chaosworship.topuslib.math.RadianMultiInterval;
import net.chaosworship.topuslib.random.RandomQueue;
import net.chaosworship.topuslib.random.SuperRandom;

import java.util.ArrayList;


public class PoissonDiskFill {

    private static final SuperRandom sRandom = new SuperRandom();

    private static final double PIOVER3 = Math.PI / 3;

    private PoissonDiskFill() {}

    public static ArrayList<Vec2> fill(Rectangle area, float spacing) {
        FixedKDTree<Vec2> locator = new FixedKDTree<>(area);
        Rectangle searchRect = new Rectangle();
        ArrayList<Vec2> points = new ArrayList<>();
        RandomQueue<Vec2> active = new RandomQueue<>(sRandom);
        points.add(new Vec2().setRandomInRect(area, sRandom));
        active.add(points.get(0));
        locator.insert(points.get(0), points.get(0));
        while(!active.isEmpty()) {
            Vec2 ap = active.popRandom();
            RadianMultiInterval rmi = new RadianMultiInterval();
            searchRect.setWithCenter(ap, 4 * spacing, 4 * spacing);
            for(Vec2 p : locator.search(searchRect)) {
                if(p == ap) continue;
                Vec2 to_p = Vec2.difference(p, ap);
                float d = to_p.magnitude();
                if(d < 2 * spacing) {
                    double a = to_p.atan2();
                    double w = Math.acos(d / (2 * spacing));
                    rmi.exclude(a - w, a + w);
                }
            }
            while(!rmi.isEmpty()) {
                double a = rmi.uniformSample(sRandom);
                Vec2 p = ap.sum(Vec2.unit(a).scale(spacing));
                rmi.exclude(a - PIOVER3, a + PIOVER3);
                if(area.contains(p)) {
                    active.add(p);
                    points.add(p);
                    locator.insert(p, p);
                }
            }
        }
        return points;
    }
}
