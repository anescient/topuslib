package net.chaosworship.topuslibtest.gl;

import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.gl.Brush;

import java.nio.FloatBuffer;

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


public class ShapesBrush extends Brush {

    // floats per vertex
    // x, y
    private static final int VERTEXSIZE = 2;

    private static final int SPOTSEGMENTS = 13;

    private final TestLoader mLoader;
    private boolean mBegun;

    private final int mMVPHandle;
    private final int mColorHandle;
    private final int mPosHandle;

    private final FloatBuffer mVertexBuffer;
    private final int mVertexBufferHandle;


    ShapesBrush(TestLoader loader) {
        mLoader = loader;
        mBegun = false;

        int program = mLoader.useProgram("simple");
        mMVPHandle = glGetUniformLocation(program, "uMVPMatrix");
        mColorHandle = glGetUniformLocation(program, "uColor");
        mPosHandle = glGetAttribLocation(program, "aPos");

        mVertexBuffer = makeFloatBuffer(VERTEXSIZE * SPOTSEGMENTS);
        mVertexBufferHandle = generateBuffer();
    }

    void begin(float[] matPV, float[] color) {
        if(mBegun) {
            throw new IllegalStateException();
        }
        mBegun = true;

        if(color == null) {
            color = new float[]{ 1, 1, 1, 1 };
        }

        mLoader.useProgram("simple");

        glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandle);
        final int stride = VERTEXSIZE * FLOATSIZE;
        glVertexAttribPointer(mPosHandle, 2, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(mPosHandle);

        glUniformMatrix4fv(mMVPHandle, 1, false, matPV, 0);
        glUniform4fv(mColorHandle, 1, color, 0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
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

    void drawSegment(float width, Vec2 a, Vec2 b) {
        mVertexBuffer.position(0);
        Vec2 unit = Vec2.difference(a, b).normalize().scale(width).rotate90();
        Vec2 p = a.sum(unit);
        mVertexBuffer.put(p.x);
        mVertexBuffer.put(p.y);
        p = a.difference(unit);
        mVertexBuffer.put(p.x);
        mVertexBuffer.put(p.y);
        p = b.sum(unit);
        mVertexBuffer.put(p.x);
        mVertexBuffer.put(p.y);
        p = b.difference(unit);
        mVertexBuffer.put(p.x);
        mVertexBuffer.put(p.y);

        mVertexBuffer.position(0);
        glBufferData(GL_ARRAY_BUFFER, 4 * VERTEXSIZE * FLOATSIZE, mVertexBuffer, GL_STREAM_DRAW);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
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
