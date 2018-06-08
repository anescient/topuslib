package net.chaosworship.topuslibtest.gl;

import android.opengl.Matrix;

import net.chaosworship.topuslib.collection.TriangleConsumer;
import net.chaosworship.topuslib.geom3d.Vec3;
import net.chaosworship.topuslib.gl.Brush;
import net.chaosworship.topuslib.gl.FloatVertexPreBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;


public class ShadedTrianglesBrush extends Brush implements TriangleConsumer {

    private static final int BATCHSIZE = 500;
    private static final int VERTEXSIZE = 6;
    private static final int VERTICESPER = 3;
    private static final String PROGRAMNAME = "shadedtriangles";

    // a vertex is:
    // position x
    // position y
    // position z
    // normal dx
    // normal dy
    // normal dz

    private final TestLoader mLoader;

    private final FloatVertexPreBuffer mVertexPreBuffer;
    private int mTrianglesBuffered;
    private final int mVertexBufferHandle;

    private final int mMMatrixHandle;
    private final int mVPMatrixHandle;
    private final int mColorHandle;
    private final int mLightHandle;

    private final int mPosHandle;
    private final int mNormalHandle;

    ShadedTrianglesBrush(TestLoader loader) {
        mLoader = loader;

        mVertexPreBuffer = new FloatVertexPreBuffer(BATCHSIZE * VERTEXSIZE * VERTICESPER, true);
        mTrianglesBuffered = 0;
        mVertexBufferHandle = generateBuffer();

        int program = mLoader.useProgram(PROGRAMNAME);

        mMMatrixHandle = glGetUniformLocation(program, "uMMatrix");
        mVPMatrixHandle = glGetUniformLocation(program, "uVPMatrix");
        mColorHandle = glGetUniformLocation(program, "uColor");
        mLightHandle = glGetUniformLocation(program, "uLight");

        mPosHandle = glGetAttribLocation(program, "aPos");
        mNormalHandle = glGetAttribLocation(program, "aNormal");
    }

    public void begin(float[] matPV, float rotation) {
        mLoader.useProgram(PROGRAMNAME);

        glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandle);

        float[] matM = new float[16];
        Matrix.setIdentityM(matM, 0);
        Matrix.rotateM(matM, 0, rotation, 0, 0, 1);
        glUniformMatrix4fv(mMMatrixHandle, 1, false, matM, 0);

        glUniformMatrix4fv(mVPMatrixHandle, 1, false, matPV, 0);
        glUniform3fv(mColorHandle, 1, new float[] {1.0f, 1.0f, 1.0f}, 0);
        glUniform3fv(mLightHandle, 1, new float[] {0.0f, 1.0f, 0.0f}, 0);

        glVertexAttribPointer(mPosHandle, 3, GL_FLOAT, false, VERTEXSIZE * FLOATSIZE, 0);
        glEnableVertexAttribArray(mPosHandle);

        glVertexAttribPointer(mNormalHandle, 3, GL_FLOAT, false, VERTEXSIZE * FLOATSIZE, 3 * FLOATSIZE);
        glEnableVertexAttribArray(mNormalHandle);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
    }

    public void add(Vec3 a, Vec3 b, Vec3 c, Vec3 normal) {
        if(mTrianglesBuffered >= BATCHSIZE) {
            flush();
        }

        mVertexPreBuffer.put(a);
        mVertexPreBuffer.put(normal);

        mVertexPreBuffer.put(b);
        mVertexPreBuffer.put(normal);

        mVertexPreBuffer.put(c);
        mVertexPreBuffer.put(normal);

        mTrianglesBuffered++;
    }

    public void addTriangle(Vec3 a, Vec3 b, Vec3 c) {
        Vec3 ab = b.difference(a);
        Vec3 bc = c.difference(b);
        Vec3 normal = new Vec3().setCross(ab, bc).normalize();
        add(a, b, c, normal);
    }

    private void flush() {
        if(mTrianglesBuffered <= 0) {
            return;
        }
        mVertexPreBuffer.glBufferDataArray();
        mVertexPreBuffer.reset();
        glDrawArrays(GL_TRIANGLES, 0, mTrianglesBuffered * VERTICESPER);
        mTrianglesBuffered = 0;
    }

    public void end() {
        flush();
        glDisableVertexAttribArray(mPosHandle);
        glDisableVertexAttribArray(mNormalHandle);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
