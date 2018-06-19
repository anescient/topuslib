package net.chaosworship.topuslib.geom3d;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;


public class PathTest {

    public void straight() {
        Vec3 a = new Vec3(1, 1, 0);
        Vec3 b = new Vec3(0, 1, 0);
        Vec3 c = new Vec3(-1, 1, 0);
        List<Vec3> path = Path.generateCurve(a, b, c, 99, 0.01f);
        assertTrue(path.size() == 3);
    }

    @Test
    public void rightAngle() {
        Vec3 a = new Vec3(1, 0, 0);
        Vec3 b = new Vec3(1, 1, 0);
        Vec3 c = new Vec3(0, 1, 0);
        List<Vec3> path = Path.generateCurve(a, b, c, 99, 0.1f);
        assertTrue(path.size() > 3);
    }
}
