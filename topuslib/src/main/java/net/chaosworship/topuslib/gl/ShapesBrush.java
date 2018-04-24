package net.chaosworship.topuslib.gl;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import net.chaosworship.topuslib.geom2d.Arc;
import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;

import java.util.List;


@SuppressWarnings({"WeakerAccess", "SameParameterValue", "unused"})
public class ShapesBrush extends Brush {

    private static final int SPOTSEGMENTS = 13;

    private final float[] mColor;
    private final TrianglesBrush mTrianglesBrush;

    private final Vec2[] mSpotVerts;

    ShapesBrush(Loader loader) {
        mColor = new float[] { 1, 1, 1, 1 };
        mTrianglesBrush = loader.getTrianglesBrush();

        mSpotVerts = new Vec2[SPOTSEGMENTS];
        for(int i = 0; i < SPOTSEGMENTS; i++) {
            mSpotVerts[i] = Vec2.unit((double)i / SPOTSEGMENTS * 2 * Math.PI);
        }
    }

    public void setColor(@ColorInt int color) {
        mColor[0] = (float)Color.red(color) / 255;
        mColor[1] = (float)Color.green(color) / 255;
        mColor[2] = (float)Color.blue(color) / 255;
        //mColor[3] = (float)Color.alpha(color) / 255;
    }

    public void setAlpha(float alpha) {
        mColor[3] = alpha;
    }

    public void begin(float[] matPV) {
        mTrianglesBrush.begin(matPV);
    }

    public void drawSpot(Vec2 position, float radius) {
        for(int i = 0; i < SPOTSEGMENTS; i++) {
            Vec2 a = mSpotVerts[i].scaled(radius).add(position);
            Vec2 b = mSpotVerts[(i + 1) % SPOTSEGMENTS].scaled(radius).add(position);
            mTrianglesBrush.addTriangle(position, a, b, mColor);
        }
    }

    public void drawSegment(Vec2 a, Vec2 b, float width) {
        Vec2 unit = Vec2.difference(a, b).normalize().scale(width * 0.5f).rotate90();
        Vec2 a1 = a.sum(unit);
        Vec2 a2 = a.difference(unit);
        Vec2 b1 = b.sum(unit);
        Vec2 b2 = b.difference(unit);
        mTrianglesBrush.addTriangle(a1, a2, b1, mColor);
        mTrianglesBrush.addTriangle(a2, b1, b2, mColor);
    }

    public void drawTriangle(Triangle triangle, float lineWidth) {
        drawSegment(triangle.pointA, triangle.pointB, lineWidth);
        drawSegment(triangle.pointB, triangle.pointC, lineWidth);
        drawSegment(triangle.pointC, triangle.pointA, lineWidth);
    }

    public void fillTriangle(Triangle triangle) {
        mTrianglesBrush.addTriangle(triangle, mColor);
    }

    public void drawRectangle(Rectangle rect, float lineWidth) {
        Vec2 a = new Vec2(rect.minx, rect.miny);
        Vec2 b = new Vec2(rect.minx, rect.maxy);
        Vec2 c = new Vec2(rect.maxx, rect.maxy);
        Vec2 d = new Vec2(rect.maxx, rect.miny);
        drawSegment(a, b, lineWidth);
        drawSegment(b, c, lineWidth);
        drawSegment(c, d, lineWidth);
        drawSegment(d, a, lineWidth);
    }

    public void drawCircle(Circle circle, float lineWidth) {
        List<Vec2> points = circle.getBoundPoints(33);
        for(int i = 0; i < points.size(); i++) {
            drawSegment(points.get(i), points.get((i + 1) % points.size()), lineWidth);
        }
    }

    public void drawArc(Arc arc, float lineWidth) {
        List<Vec2> points = arc.getPointsAlong(33);
        drawSegment(points.get(0), points.get(1), lineWidth);
        for(int i = 1; i < points.size(); i++) {
            drawSegment(points.get(i - 1), points.get(i), lineWidth);
        }
    }

    public void end() {
        mTrianglesBrush.end();
    }
}
