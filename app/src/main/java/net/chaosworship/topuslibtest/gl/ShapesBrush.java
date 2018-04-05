package net.chaosworship.topuslibtest.gl;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.gl.Brush;

import java.util.List;


@SuppressWarnings({"WeakerAccess", "SameParameterValue", "unused"})
public class ShapesBrush extends Brush {

    private static final int SPOTSEGMENTS = 13;

    private final float[] mColor;
    private float mLineWidth;
    private final TrianglesBrush mTrianglesBrush;

    private final Vec2[] mSpotVerts;

    ShapesBrush(TestLoader loader) {
        mColor = new float[] { 1, 1, 1, 1 };
        mLineWidth = 1;
        mTrianglesBrush = loader.getTrianglesBrush();

        mSpotVerts = new Vec2[SPOTSEGMENTS];
        for(int i = 0; i < SPOTSEGMENTS; i++) {
            mSpotVerts[i] = Vec2.unit((double)i / SPOTSEGMENTS * 2 * Math.PI);
        }
    }

    void setLineWidth(float lineWidth) {
        mLineWidth = lineWidth;
    }

    void setColor(@ColorInt int color) {
        mColor[0] = (float)Color.red(color) / 255;
        mColor[1] = (float)Color.green(color) / 255;
        mColor[2] = (float)Color.blue(color) / 255;
        mColor[3] = (float)Color.alpha(color) / 255;
    }

    void setAlpha(float alpha) {
        mColor[3] = alpha;
    }

    void begin(float[] matPV) {
        mTrianglesBrush.begin(matPV);
    }

    void drawSpot(Vec2 position, float radius) {
        for(int i = 0; i < SPOTSEGMENTS; i++) {
            Vec2 a = mSpotVerts[i].scaled(radius).add(position);
            Vec2 b = mSpotVerts[(i + 1) % SPOTSEGMENTS].scaled(radius).add(position);
            mTrianglesBrush.addTriangle(position, a, b, mColor);
        }
    }

    void drawSegment(Vec2 a, Vec2 b) {
        Vec2 unit = Vec2.difference(a, b).normalize().scale(mLineWidth * 0.5f).rotate90();
        Vec2 a1 = a.sum(unit);
        Vec2 a2 = a.difference(unit);
        Vec2 b1 = b.sum(unit);
        Vec2 b2 = b.difference(unit);
        mTrianglesBrush.addTriangle(a1, a2, b1, mColor);
        mTrianglesBrush.addTriangle(a2, b1, b2, mColor);
    }

    void drawTriangle(Triangle triangle) {
        drawSegment(triangle.pointA, triangle.pointB);
        drawSegment(triangle.pointB, triangle.pointC);
        drawSegment(triangle.pointC, triangle.pointA);
    }

    void drawRectangle(Rectangle rect) {
        Vec2 a = new Vec2(rect.minx, rect.miny);
        Vec2 b = new Vec2(rect.minx, rect.maxy);
        Vec2 c = new Vec2(rect.maxx, rect.maxy);
        Vec2 d = new Vec2(rect.maxx, rect.miny);
        drawSegment(a, b);
        drawSegment(b, c);
        drawSegment(c, d);
        drawSegment(d, a);
    }

    void drawCircle(Circle circle) {
        List<Vec2> points = circle.getBoundPoints(33);
        for(int i = 0; i < points.size(); i++) {
            drawSegment(points.get(i), points.get((i + 1) % points.size()));
        }
    }

    void end() {
        mTrianglesBrush.end();
    }
}
