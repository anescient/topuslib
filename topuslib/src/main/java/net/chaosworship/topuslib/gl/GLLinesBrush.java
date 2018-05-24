package net.chaosworship.topuslib.gl;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom3d.Cuboid;
import net.chaosworship.topuslib.geom3d.Vec3;
import net.chaosworship.topuslib.graph.SimpleGraph;
import net.chaosworship.topuslib.tuple.IntPair;

import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.Map;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
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
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;


@SuppressWarnings("unused")
public class GLLinesBrush extends Brush {

    // floats per vertex
    // x, y, z, r, g, b, a
    private static final int VERTEXSIZE = 7;
    private static final int VERTICESPER = 2;

    private static final int BATCHSIZE = 500;

    private static final String SHADER_V =
            "uniform mat4 uMVPMatrix;\n" +
            "attribute vec3 aPos;\n" +
            "attribute vec4 aColor;\n" +
            "varying vec4 vColor;\n" +
            "void main() {\n" +
            "    vColor = aColor;\n" +
            "    gl_Position = uMVPMatrix * vec4(aPos, 1.0);\n" +
            "}";
    private static final String SHADER_F =
            "precision mediump float;\n" +
            "varying vec4 vColor;\n" +
            "void main() {\n" +
            "    gl_FragColor = vColor;\n" +
            "}\n";
    private static final Loader.LiteralProgram mProgram = new Loader.LiteralProgram(SHADER_V, SHADER_F);

    private final Loader mLoader;

    private final int mMVPHandle;
    private final int mPosHandle;
    private final int mColorHandle;

    private final FloatBuffer mVertexBuffer;
    private final int mVertexBufferHandle;
    private final float[] mVertexPreBuffer;
    private int mLinesBuffered;

    private final float[] mColor;

    GLLinesBrush(Loader loader) {
        mLoader = loader;

        int program = mLoader.useProgram(mProgram);
        mMVPHandle = glGetUniformLocation(program, "uMVPMatrix");
        mPosHandle = glGetAttribLocation(program, "aPos");
        mColorHandle = glGetAttribLocation(program, "aColor");

        mVertexBuffer = makeFloatBuffer(BATCHSIZE * VERTEXSIZE * VERTICESPER);
        mVertexBufferHandle = generateBuffer();

        mVertexPreBuffer = new float[BATCHSIZE * VERTEXSIZE * VERTICESPER];
        mLinesBuffered = 0;

        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexPreBuffer, 0, BATCHSIZE * VERTEXSIZE * VERTICESPER);
        mVertexBuffer.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexBuffer.capacity() * FLOATSIZE, mVertexBuffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        mColor = new float[]{1, 1, 1, 1};
    }

    public void begin(float[] matPV, float lineWidth) {
        mLoader.useProgram(mProgram);

        glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandle);
        final int stride = VERTEXSIZE * FLOATSIZE;
        glVertexAttribPointer(mPosHandle, 3, GL_FLOAT, false, stride, 0);
        glVertexAttribPointer(mColorHandle, 4, GL_FLOAT, false, stride, 3 * FLOATSIZE);
        glEnableVertexAttribArray(mPosHandle);
        glEnableVertexAttribArray(mColorHandle);

        glUniformMatrix4fv(mMVPHandle, 1, false, matPV, 0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);

        glLineWidth(lineWidth);
    }

    public void setColor(@ColorInt int color) {
        mColor[0] = (float) Color.red(color) / 255;
        mColor[1] = (float)Color.green(color) / 255;
        mColor[2] = (float)Color.blue(color) / 255;
    }

    public void setAlpha(float alpha) {
        mColor[3] = alpha;
    }

    public void addLine(Vec2 a, Vec2 b) {
        addLine(new Vec3(a.x, a.y, 0), new Vec3(b.x, b.y, 0));
    }

    @SuppressWarnings({"UnusedAssignment", "WeakerAccess"})
    public void addLine(Vec3 a, Vec3 b) {
        if(mLinesBuffered >= BATCHSIZE) {
            flush();
        }
        int i = mLinesBuffered * VERTICESPER * VERTEXSIZE;

        mVertexPreBuffer[i++] = a.x;
        mVertexPreBuffer[i++] = a.y;
        mVertexPreBuffer[i++] = a.z;
        mVertexPreBuffer[i++] = mColor[0];
        mVertexPreBuffer[i++] = mColor[1];
        mVertexPreBuffer[i++] = mColor[2];
        mVertexPreBuffer[i++] = mColor[3];

        mVertexPreBuffer[i++] = b.x;
        mVertexPreBuffer[i++] = b.y;
        mVertexPreBuffer[i++] = b.z;
        mVertexPreBuffer[i++] = mColor[0];
        mVertexPreBuffer[i++] = mColor[1];
        mVertexPreBuffer[i++] = mColor[2];
        mVertexPreBuffer[i++] = mColor[3];

        mLinesBuffered++;
    }

    public void addPath(Collection<Vec3> points) {
        Vec3 previous = null;
        for(Vec3 point : points) {
            if(previous != null) {
                addLine(previous, point);
            }
            previous = point;
        }
    }

    // todo addGrid(Vec3 center, Vec3 normal, float rotation)
    public void addXYGrid(int divisions, float cellSize, float z) {
        Vec3 a = new Vec3(0, 0, z);
        Vec3 b = new Vec3(0, 0, z);
        int n = Math.max(divisions / 2, 1);
        for(int i = -n; i <= n; i++) {
            a.x = i * cellSize;
            b.x = a.x;
            a.y = -n * cellSize;
            b.y = n * cellSize;
            addLine(a, b);

            a.y = i * cellSize;
            b.y = a.y;
            a.x = -n * cellSize;
            b.x = n * cellSize;
            addLine(a, b);
        }
    }

    public void addCuboid(Cuboid cube) {
        Vec3 a = new Vec3(cube.minx, cube.miny, cube.minz);
        Vec3 b = new Vec3(cube.minx, cube.miny, cube.maxz);
        Vec3 c = new Vec3(cube.maxx, cube.miny, cube.maxz);
        Vec3 d = new Vec3(cube.maxx, cube.miny, cube.minz);
        Vec3 e = new Vec3(cube.minx, cube.maxy, cube.minz);
        Vec3 f = new Vec3(cube.minx, cube.maxy, cube.maxz);
        Vec3 g = new Vec3(cube.maxx, cube.maxy, cube.maxz);
        Vec3 h = new Vec3(cube.maxx, cube.maxy, cube.minz);
        addLine(a, b);
        addLine(b, c);
        addLine(c, d);
        addLine(d, a);
        addLine(e, f);
        addLine(f, g);
        addLine(g, h);
        addLine(h, e);
        addLine(a, e);
        addLine(b, f);
        addLine(c, g);
        addLine(d, h);
    }

    public void addGraph(SimpleGraph graph, Map<Integer, Vec3> vertexPositions) {
        for(IntPair p : graph.getEdges()) {
            addLine(vertexPositions.get(p.a), vertexPositions.get(p.b));
        }
    }

    private void flush() {
        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexPreBuffer, 0, mLinesBuffered * VERTEXSIZE * VERTICESPER);
        mVertexBuffer.position(0);
        glBufferSubData(GL_ARRAY_BUFFER, 0, mLinesBuffered * VERTEXSIZE * VERTICESPER * FLOATSIZE, mVertexBuffer);
        glDrawArrays(GL_LINES, 0, mLinesBuffered * VERTICESPER);
        mLinesBuffered = 0;
    }

    public void end() {
        flush();
        glDisableVertexAttribArray(mPosHandle);
        glDisableVertexAttribArray(mColorHandle);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
