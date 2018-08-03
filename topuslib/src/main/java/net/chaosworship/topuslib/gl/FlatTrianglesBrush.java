package net.chaosworship.topuslib.gl;

import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;


public class FlatTrianglesBrush extends Brush {

    private static final int VERTICESPER = 3;

    private static final int BATCHSIZE = 500;

    private static final String SHADER_V =
            "uniform mat4 uMVPMatrix;\n" +
            "attribute vec2 aPos;\n" +
            "attribute vec4 aColor;\n" +
            "varying vec4 vColor;\n" +
            "void main() {\n" +
            "    vColor = aColor;\n" +
            "    gl_Position = uMVPMatrix * vec4(aPos, 0.0, 1.0);\n" +
            "}";
    private static final String SHADER_F =
            "precision mediump float;\n" +
            "varying vec4 vColor;\n" +
            "void main() {\n" +
            "    gl_FragColor = vColor;\n" +
            "}\n";
    private static final Loader.LiteralProgram mProgram = new Loader.LiteralProgram(SHADER_V, SHADER_F);

    private final Loader mLoader;

    private final FloatAttributeList mVertexAttributes;

    private final int mMVPHandle;

    private final FloatVertexPreBuffer mVertexPreBuffer;
    private final int mVertexBufferHandle;
    private int mTrianglesBuffered;

    FlatTrianglesBrush(Loader loader) {
        mLoader = loader;

        mVertexAttributes = new FloatAttributeList();
        try {
            mVertexAttributes.addVec2("aPos"); // x, y
            mVertexAttributes.addFloatArray("aColor", 4); // r, g, b, a
        } catch (FloatAttributeList.AttributeException e) {
            e.printStackTrace();
        }

        int program = mLoader.useProgram(mProgram);
        mMVPHandle = glGetUniformLocation(program, "uMVPMatrix");

        mVertexBufferHandle = generateBuffer();
        mVertexPreBuffer = new FloatVertexPreBuffer(mVertexAttributes.floatCount() * VERTICESPER * BATCHSIZE, true);
        mTrianglesBuffered = 0;
    }

    public void begin(float[] matPV) {
        int program = mLoader.useProgram(mProgram);

        glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandle);
        try {
            mVertexAttributes.enable(program);
        } catch (FloatAttributeList.AttributeException e) {
            e.printStackTrace();
        }

        glUniformMatrix4fv(mMVPHandle, 1, false, matPV, 0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
    }

    public void addTriangle(Vec2 a, Vec2 b, Vec2 c, float[] color) {
        if(mTrianglesBuffered >= BATCHSIZE) {
            flush();
        }

        mVertexPreBuffer.put(a);
        mVertexPreBuffer.putRGBA(color);

        mVertexPreBuffer.put(b);
        mVertexPreBuffer.putRGBA(color);

        mVertexPreBuffer.put(c);
        mVertexPreBuffer.putRGBA(color);

        mTrianglesBuffered++;
    }

    @SuppressWarnings("unused")
    public void addTriangle(Triangle triangle, float[] color) {
        addTriangle(triangle.pointA, triangle.pointB, triangle.pointC, color);
    }

    private void flush() {
        mVertexPreBuffer.glBufferDataArray();
        mVertexPreBuffer.reset();
        glDrawArrays(GL_TRIANGLES, 0, mTrianglesBuffered * VERTICESPER);
        mTrianglesBuffered = 0;
    }

    public void end() {
        flush();
        mVertexAttributes.disable();
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
