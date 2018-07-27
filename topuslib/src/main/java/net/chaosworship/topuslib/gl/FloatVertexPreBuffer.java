package net.chaosworship.topuslib.gl;

import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom3d.Vec3;

import java.nio.FloatBuffer;
import java.util.Arrays;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glBufferSubData;


@SuppressWarnings("unused")
public class FloatVertexPreBuffer {

    private final boolean mDynamic;
    private float[] mPreBuffer;
    private FloatBuffer mFloatBuffer;
    private int mFloatsBuffered;
    private boolean mGLBufferInitialized;

    public FloatVertexPreBuffer(int floatCount, boolean dynamic) {
        mDynamic = dynamic;
        mPreBuffer = new float[floatCount];
        mFloatBuffer = Brush.makeFloatBuffer(mPreBuffer.length);
        mFloatsBuffered = 0;
        mGLBufferInitialized = false;
    }

    public void ensureCapacity(int floatCount) {
        if(mPreBuffer.length >= floatCount) {
            return;
        }
        mPreBuffer = Arrays.copyOf(mPreBuffer, floatCount);
        mFloatBuffer = Brush.makeFloatBuffer(mPreBuffer.length);
        mGLBufferInitialized = false;
    }

    public void reset() {
        mFloatsBuffered = 0;
    }

    public void put(float value) {
        mPreBuffer[mFloatsBuffered++] = value;
    }

    public void put(Vec2 v) {
        mPreBuffer[mFloatsBuffered++] = v.x;
        mPreBuffer[mFloatsBuffered++] = v.y;
    }

    public void putRGBA(float[] rgba) {
        mPreBuffer[mFloatsBuffered++] = rgba[0];
        mPreBuffer[mFloatsBuffered++] = rgba[1];
        mPreBuffer[mFloatsBuffered++] = rgba[2];
        mPreBuffer[mFloatsBuffered++] = rgba[3];
    }

    public void putSum(Vec2 a, Vec2 b) {
        mPreBuffer[mFloatsBuffered++] = a.x + b.x;
        mPreBuffer[mFloatsBuffered++] = a.y + b.y;
    }

    public void put(Vec3 v) {
        mPreBuffer[mFloatsBuffered++] = v.x;
        mPreBuffer[mFloatsBuffered++] = v.y;
        mPreBuffer[mFloatsBuffered++] = v.z;
    }

    public void putSum(Vec3 a, Vec3 b) {
        mPreBuffer[mFloatsBuffered++] = a.x + b.x;
        mPreBuffer[mFloatsBuffered++] = a.y + b.y;
        mPreBuffer[mFloatsBuffered++] = a.z + b.z;
    }

    public void put(float[] values) {
        for(float value : values) {
            mPreBuffer[mFloatsBuffered++] = value;
        }
    }

    public void skip(int count) {
        mFloatsBuffered += count;
    }

    public void glBufferDataArray() {
        mFloatBuffer.position(0);
        mFloatBuffer.put(mPreBuffer, 0, mFloatsBuffered);
        mFloatBuffer.position(0);
        if(!mGLBufferInitialized) {
            int usage = mDynamic ? GL_DYNAMIC_DRAW : GL_STATIC_DRAW;
            glBufferData(GL_ARRAY_BUFFER, mFloatBuffer.capacity() * Brush.FLOATSIZE, mFloatBuffer, usage);
            mGLBufferInitialized = true;
        } else {
            glBufferSubData(GL_ARRAY_BUFFER, 0, mFloatsBuffered * Brush.FLOATSIZE, mFloatBuffer);
        }
    }
}
