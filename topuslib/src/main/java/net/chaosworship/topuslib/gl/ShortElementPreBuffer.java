package net.chaosworship.topuslib.gl;

import net.chaosworship.topuslib.tuple.IntTriple;

import java.nio.ShortBuffer;
import java.util.Arrays;

import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glBufferSubData;


public class ShortElementPreBuffer {

    private final boolean mDynamic;
    private short[] mPreBuffer;
    private ShortBuffer mShortBuffer;
    private int mShortsBuffered;
    private boolean mGLBufferInitialized;

    public ShortElementPreBuffer(int shortCount, boolean dynamic) {
        mDynamic = dynamic;
        mPreBuffer = new short[shortCount];
        mShortBuffer = Brush.makeShortBuffer(mPreBuffer.length);
        mShortsBuffered = 0;
        mGLBufferInitialized = false;
    }

    public void ensureCapacity(int shortCount) {
        if(mPreBuffer.length >= shortCount) {
            return;
        }
        mPreBuffer = Arrays.copyOf(mPreBuffer, shortCount);
        mShortBuffer = Brush.makeShortBuffer(mPreBuffer.length);
        mGLBufferInitialized = false;
    }

    public void reset() {
        mShortsBuffered = 0;
    }

    public void put(short value) {
        mPreBuffer[mShortsBuffered++] = value;
    }

    public void put(int value) {
        mPreBuffer[mShortsBuffered++] = (short)value;
    }

    public void put(IntTriple abc) {
        mPreBuffer[mShortsBuffered++] = (short)abc.a;
        mPreBuffer[mShortsBuffered++] = (short)abc.b;
        mPreBuffer[mShortsBuffered++] = (short)abc.c;
    }

    public void skip(int count) {
        mShortsBuffered += count;
    }

    public void glBufferDataElementArray() {
        mShortBuffer.position(0);
        mShortBuffer.put(mPreBuffer, 0, mShortsBuffered);
        mShortBuffer.position(0);
        if(!mGLBufferInitialized) {
            int usage = mDynamic ? GL_DYNAMIC_DRAW : GL_STATIC_DRAW;
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, mShortBuffer.capacity() * Brush.SHORTSIZE, mShortBuffer, usage);
            mGLBufferInitialized = true;
        } else {
            glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, mShortsBuffered * Brush.SHORTSIZE, mShortBuffer);
        }
    }
}
