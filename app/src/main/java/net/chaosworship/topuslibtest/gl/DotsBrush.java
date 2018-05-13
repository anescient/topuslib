package net.chaosworship.topuslibtest.gl;

import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.gl.Brush;
import net.chaosworship.topuslib.gl.Loader;
import net.chaosworship.topuslibtest.R;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glBufferSubData;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;


public class DotsBrush extends Brush {

    private static final int BATCHSIZE = 1000;
    private static final int VERTEXSIZE = 5;
    private static final int VERTICESPER = 4;
    private static final int ELEMENTSPER = 6;
    private static final String PROGRAMNAME = "dots";

    // a vertex is:
    // position x
    // position y
    // tex coord x
    // tex coord y
    // alpha

    private final Loader mLoader;

    private final float[] mVertexPreBuffer;
    private int mQuadsBuffered;
    private final FloatBuffer mVertexBuffer;
    private final int mVertexBufferHandle;
    private final int mElementBufferHandle;

    private final int mMVPHandle;
    private final int mTextureHandle;

    private final int mPosHandle;
    private final int mTexCoordHandle;
    private final int mAlphaHandle;

    @SuppressWarnings("PointlessArithmeticExpression")
    DotsBrush(Loader loader) {
        mLoader = loader;

        mVertexPreBuffer = new float[BATCHSIZE * VERTEXSIZE * VERTICESPER];
        for(int quadi = 0; quadi < BATCHSIZE; quadi++) {
            int basei = quadi * VERTEXSIZE * VERTICESPER;

            // texture coordinates
            mVertexPreBuffer[basei + 2] = 0;
            mVertexPreBuffer[basei + 3] = 1;
            mVertexPreBuffer[basei + VERTEXSIZE + 2] = 1;
            mVertexPreBuffer[basei + VERTEXSIZE + 3] = 1;
            mVertexPreBuffer[basei + 2 * VERTEXSIZE + 2] = 1;
            mVertexPreBuffer[basei + 2 * VERTEXSIZE + 3] = 0;
            mVertexPreBuffer[basei + 3 * VERTEXSIZE + 2] = 0;
            mVertexPreBuffer[basei + 3 * VERTEXSIZE + 3] = 0;
        }

        mQuadsBuffered = 0;

        mVertexBuffer = makeFloatBuffer(mVertexPreBuffer.length);
        mVertexBufferHandle = generateBuffer();

        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexPreBuffer, 0, BATCHSIZE * VERTEXSIZE * VERTICESPER);
        mVertexBuffer.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexBuffer.capacity() * FLOATSIZE, mVertexBuffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        mElementBufferHandle = generateBuffer();
        ShortBuffer elements = makeShortBuffer(BATCHSIZE * ELEMENTSPER);
        elements.position(0);
        for(short quadi = 0; quadi < BATCHSIZE; quadi++) {
            short base = (short)(quadi * 4);

            elements.put((short)(base + 3));
            elements.put((short)(base + 0));
            elements.put((short)(base + 1));

            elements.put((short)(base + 1));
            elements.put((short)(base + 2));
            elements.put((short)(base + 3));
        }
        elements.position(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mElementBufferHandle);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements.capacity() * SHORTSIZE, elements, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        int program = mLoader.useProgram(PROGRAMNAME);

        mMVPHandle = glGetUniformLocation(program, "uMVPMatrix");
        mTextureHandle = glGetUniformLocation(program, "uTexture");

        mPosHandle = glGetAttribLocation(program, "aPos");
        mTexCoordHandle = glGetAttribLocation(program, "aTexCoord");
        mAlphaHandle = glGetAttribLocation(program, "aAlpha");
    }

    public void begin(float[] matPV) {
        mLoader.useProgram(PROGRAMNAME);

        glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandle);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mElementBufferHandle);

        glUniformMatrix4fv(mMVPHandle, 1, false, matPV, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mLoader.getTexture(R.drawable.blurwhitespot));
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glUniform1i(mTextureHandle, 0);

        glVertexAttribPointer(mPosHandle, 2, GL_FLOAT, false, VERTEXSIZE * FLOATSIZE, 0);
        glEnableVertexAttribArray(mPosHandle);

        glVertexAttribPointer(mTexCoordHandle, 2, GL_FLOAT, false, VERTEXSIZE * FLOATSIZE, 2 * FLOATSIZE);
        glEnableVertexAttribArray(mTexCoordHandle);

        glVertexAttribPointer(mAlphaHandle, 1, GL_FLOAT, false, VERTEXSIZE * FLOATSIZE, 4 * FLOATSIZE);
        glEnableVertexAttribArray(mAlphaHandle);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
    }

    public void add(Vec2 position, float radius, float alpha) {
        if(mQuadsBuffered >= BATCHSIZE) {
            flush();
        }

        int floatIndex = mQuadsBuffered * VERTEXSIZE * VERTICESPER;

        mVertexPreBuffer[floatIndex++] = position.x - radius;
        mVertexPreBuffer[floatIndex++] = position.y - radius;
        floatIndex += 2; // skip texture coords
        mVertexPreBuffer[floatIndex++] = alpha;

        mVertexPreBuffer[floatIndex++] = position.x - radius;
        mVertexPreBuffer[floatIndex++] = position.y + radius;
        floatIndex += 2; // skip texture coords
        mVertexPreBuffer[floatIndex++] = alpha;

        mVertexPreBuffer[floatIndex++] = position.x + radius;
        mVertexPreBuffer[floatIndex++] = position.y + radius;
        floatIndex += 2; // skip texture coords
        mVertexPreBuffer[floatIndex++] = alpha;

        mVertexPreBuffer[floatIndex++] = position.x + radius;
        mVertexPreBuffer[floatIndex++] = position.y - radius;
        floatIndex += 2; // skip texture coords
        mVertexPreBuffer[floatIndex] = alpha;

        mQuadsBuffered++;
    }

    private void flush() {
        if(mQuadsBuffered <= 0) {
            return;
        }
        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexPreBuffer, 0, mQuadsBuffered * VERTEXSIZE * VERTICESPER);
        mVertexBuffer.position(0);
        glBufferSubData(GL_ARRAY_BUFFER, 0, mQuadsBuffered * VERTEXSIZE * VERTICESPER * FLOATSIZE, mVertexBuffer);
        glDrawElements(GL_TRIANGLES, mQuadsBuffered * ELEMENTSPER, GL_UNSIGNED_SHORT, 0);
        mQuadsBuffered = 0;
    }

    public void end() {
        flush();
        glDisableVertexAttribArray(mPosHandle);
        glDisableVertexAttribArray(mTexCoordHandle);
        glDisableVertexAttribArray(mAlphaHandle);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
