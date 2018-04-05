package net.chaosworship.topuslibtest.gl;

import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.gl.Brush;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glBufferSubData;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;


public class TrianglesBrush extends Brush {

    // floats per vertex
    // x, y, r, g, b, a
    private static final int VERTEXSIZE = 6;
    private static final int VERTICESPER = 3;

    private static final int BATCHSIZE = 500;

    private final TestLoader mLoader;

    private final int mMVPHandle;
    private final int mPosHandle;
    private final int mColorHandle;

    private final FloatBuffer mVertexBuffer;
    private final int mVertexBufferHandle;
    private final float[] mVertexPreBuffer;
    private int mTrianglesBuffered;

    TrianglesBrush(TestLoader loader) {
        mLoader = loader;

        int program = mLoader.useProgram("triangles");
        mMVPHandle = glGetUniformLocation(program, "uMVPMatrix");
        mPosHandle = glGetAttribLocation(program, "aPos");
        mColorHandle = glGetAttribLocation(program, "aColor");

        mVertexBuffer = makeFloatBuffer(BATCHSIZE * VERTEXSIZE * VERTICESPER);
        mVertexBufferHandle = generateBuffer();

        mVertexPreBuffer = new float[BATCHSIZE * VERTEXSIZE * VERTICESPER];
        mTrianglesBuffered = 0;

        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexPreBuffer, 0, BATCHSIZE * VERTEXSIZE * VERTICESPER);
        mVertexBuffer.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexBuffer.capacity() * FLOATSIZE, mVertexBuffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    void begin(float[] matPV) {
        mLoader.useProgram("triangles");

        glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandle);
        final int stride = VERTEXSIZE * FLOATSIZE;
        glVertexAttribPointer(mPosHandle, 2, GL_FLOAT, false, stride, 0);
        glVertexAttribPointer(mColorHandle, 4, GL_FLOAT, false, stride, 2 * FLOATSIZE);
        glEnableVertexAttribArray(mPosHandle);
        glEnableVertexAttribArray(mColorHandle);

        glUniformMatrix4fv(mMVPHandle, 1, false, matPV, 0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
    }

    void addTriangle(Vec2 a, Vec2 b, Vec2 c, float[] color) {
        if(mTrianglesBuffered >= BATCHSIZE) {
            flush();
        }
        int i = mTrianglesBuffered * VERTICESPER * VERTEXSIZE;

        mVertexPreBuffer[i++] = a.x;
        mVertexPreBuffer[i++] = a.y;
        mVertexPreBuffer[i++] = color[0];
        mVertexPreBuffer[i++] = color[1];
        mVertexPreBuffer[i++] = color[2];
        mVertexPreBuffer[i++] = color[3];

        mVertexPreBuffer[i++] = b.x;
        mVertexPreBuffer[i++] = b.y;
        mVertexPreBuffer[i++] = color[0];
        mVertexPreBuffer[i++] = color[1];
        mVertexPreBuffer[i++] = color[2];
        mVertexPreBuffer[i++] = color[3];

        mVertexPreBuffer[i++] = c.x;
        mVertexPreBuffer[i++] = c.y;
        mVertexPreBuffer[i++] = color[0];
        mVertexPreBuffer[i++] = color[1];
        mVertexPreBuffer[i++] = color[2];
        mVertexPreBuffer[i] = color[3];

        mTrianglesBuffered++;
    }

    @SuppressWarnings("unused")
    void addTriangle(Triangle triangle, float[] color) {
        addTriangle(triangle.pointA, triangle.pointB, triangle.pointC, color);
    }

    private void flush() {
        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexPreBuffer, 0, mTrianglesBuffered * VERTEXSIZE * VERTICESPER);
        mVertexBuffer.position(0);
        glBufferSubData(GL_ARRAY_BUFFER, 0, mTrianglesBuffered * VERTEXSIZE * VERTICESPER * FLOATSIZE, mVertexBuffer);
        glDrawArrays(GL_TRIANGLES, 0, mTrianglesBuffered * VERTICESPER);
        mTrianglesBuffered = 0;
    }

    void end() {
        flush();
        glDisableVertexAttribArray(mPosHandle);
        glDisableVertexAttribArray(mColorHandle);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
