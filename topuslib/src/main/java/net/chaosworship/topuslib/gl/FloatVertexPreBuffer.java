package net.chaosworship.topuslib.gl;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glBufferSubData;


public class FloatVertexPreBuffer {

    private final boolean mDynamic;
    private final float[] mPreBuffer;
    private final FloatBuffer mFloatBuffer;
    private int mFloatsBuffered;
    private boolean mGLBufferInitialized;

    public FloatVertexPreBuffer(int floatCount, boolean dynamic) {
        mDynamic = dynamic;
        mPreBuffer = new float[floatCount];
        mFloatBuffer = Brush.makeFloatBuffer(floatCount);
        mFloatsBuffered = 0;
        mGLBufferInitialized = false;
    }

    public void reset() {
        mFloatsBuffered = 0;
    }

    public void put(float value) {
        mPreBuffer[mFloatsBuffered++] = value;
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
