package net.chaosworship.topuslibtest.gl;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.gl.Brush;

import java.nio.FloatBuffer;
import java.util.List;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_STREAM_DRAW;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;


@SuppressWarnings({"WeakerAccess", "SameParameterValue", "unused"})
public class ShapesBrush extends Brush {

    // floats per vertex
    // x, y
    private static final int VERTEXSIZE = 2;

    private static final int SPOTSEGMENTS = 13;

    private final TestLoader mLoader;
    private boolean mBegun;
    private final float[] mColor;
    private float mLineWidth;

    private final int mMVPHandle;
    private final int mColorHandle;
    private final int mPosHandle;

    private final FloatBuffer mVertexBuffer;
    private final int mVertexBufferHandle;


    ShapesBrush(TestLoader loader) {
        mLoader = loader;
        mBegun = false;
        mColor = new float[] { 1, 1, 1, 1 };
        mLineWidth = 1;

        int program = mLoader.useProgram("simple");
        mMVPHandle = glGetUniformLocation(program, "uMVPMatrix");
        mColorHandle = glGetUniformLocation(program, "uColor");
        mPosHandle = glGetAttribLocation(program, "aPos");

        mVertexBuffer = makeFloatBuffer(VERTEXSIZE * SPOTSEGMENTS);
        mVertexBufferHandle = generateBuffer();
    }

    void begin(float[] matPV) {
        if(mBegun) {
            throw new IllegalStateException();
        }
        mBegun = true;

        mLoader.useProgram("simple");

        glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandle);
        final int stride = VERTEXSIZE * FLOATSIZE;
        glVertexAttribPointer(mPosHandle, 2, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(mPosHandle);

        glUniformMatrix4fv(mMVPHandle, 1, false, matPV, 0);
        glUniform4fv(mColorHandle, 1, mColor, 0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
    }

    void setLineWidth(float lineWidth) {
        mLineWidth = lineWidth;
    }

    void setColor(@ColorInt int color) {
        mColor[0] = (float)Color.red(color) / 255;
        mColor[1] = (float)Color.green(color) / 255;
        mColor[2] = (float)Color.blue(color) / 255;
        mColor[3] = (float)Color.alpha(color) / 255;
        if(mBegun) {
            glUniform4fv(mColorHandle, 1, mColor, 0);
        }
    }

    void drawSpot(Vec2 position, float radius) {
        mVertexBuffer.position(0);
        for(int i = 0; i < SPOTSEGMENTS; i++) {
            Vec2 v = Vec2.unit((double)i / SPOTSEGMENTS * 2 * Math.PI).scale(radius).add(position);
            mVertexBuffer.put(v.x);
            mVertexBuffer.put(v.y);
        }
        mVertexBuffer.position(0);
        glBufferData(GL_ARRAY_BUFFER, SPOTSEGMENTS * VERTEXSIZE * FLOATSIZE, mVertexBuffer, GL_STREAM_DRAW);
        glDrawArrays(GL_TRIANGLE_FAN, 0, SPOTSEGMENTS);
    }

    void drawSegment(Vec2 a, Vec2 b) {
        mVertexBuffer.position(0);
        Vec2 unit = Vec2.difference(a, b).normalize().scale(mLineWidth * 0.5f).rotate90();
        Vec2 p = a.sum(unit);
        mVertexBuffer.put(p.x);
        mVertexBuffer.put(p.y);
        p = a.difference(unit);
        mVertexBuffer.put(p.x);
        mVertexBuffer.put(p.y);
        p = b.difference(unit);
        mVertexBuffer.put(p.x);
        mVertexBuffer.put(p.y);
        p = b.sum(unit);
        mVertexBuffer.put(p.x);
        mVertexBuffer.put(p.y);

        mVertexBuffer.position(0);
        glBufferData(GL_ARRAY_BUFFER, 4 * VERTEXSIZE * FLOATSIZE, mVertexBuffer, GL_STREAM_DRAW);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
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
        if(!mBegun) {
            throw new IllegalStateException();
        }
        mBegun = false;
        glDisableVertexAttribArray(mPosHandle);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
