package net.chaosworship.topuslibtest.gl;

import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.gl.Brush;
import net.chaosworship.topuslib.gl.FloatVertexPreBuffer;
import net.chaosworship.topuslibtest.R;

import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
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

    private final TestLoader mLoader;

    private final FloatVertexPreBuffer mVertexPreBuffer;
    private int mQuadsBuffered;
    private final int mVertexBufferHandle;
    private final int mElementBufferHandle;

    private final int mMVPHandle;
    private final int mTextureHandle;

    private final int mPosHandle;
    private final int mTexCoordHandle;
    private final int mAlphaHandle;

    @SuppressWarnings("PointlessArithmeticExpression")
    DotsBrush(TestLoader loader) {
        mLoader = loader;

        mVertexPreBuffer = new FloatVertexPreBuffer(BATCHSIZE * VERTEXSIZE * VERTICESPER, true);
        for(int quadi = 0; quadi < BATCHSIZE; quadi++) {
            mVertexPreBuffer.skip(2);
            mVertexPreBuffer.put(0); // texture coordinates
            mVertexPreBuffer.put(1);
            mVertexPreBuffer.skip(3);
            mVertexPreBuffer.put(1);
            mVertexPreBuffer.put(1);
            mVertexPreBuffer.skip(3);
            mVertexPreBuffer.put(1);
            mVertexPreBuffer.put(0);
            mVertexPreBuffer.skip(3);
            mVertexPreBuffer.put(0);
            mVertexPreBuffer.put(0);
            mVertexPreBuffer.skip(1);
        }
        mVertexPreBuffer.reset();

        mQuadsBuffered = 0;

        mVertexBufferHandle = generateBuffer();

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
        glBindTexture(GL_TEXTURE_2D, mLoader.getTexture(R.drawable.whitespot));
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

        mVertexPreBuffer.put(position.x - radius);
        mVertexPreBuffer.put(position.y - radius);
        mVertexPreBuffer.skip(2); // skip texture coords
        mVertexPreBuffer.put(alpha);

        mVertexPreBuffer.put(position.x - radius);
        mVertexPreBuffer.put(position.y + radius);
        mVertexPreBuffer.skip(2); // skip texture coords
        mVertexPreBuffer.put(alpha);

        mVertexPreBuffer.put(position.x + radius);
        mVertexPreBuffer.put(position.y + radius);
        mVertexPreBuffer.skip(2); // skip texture coords
        mVertexPreBuffer.put(alpha);

        mVertexPreBuffer.put(position.x + radius);
        mVertexPreBuffer.put(position.y - radius);
        mVertexPreBuffer.skip(2); // skip texture coords
        mVertexPreBuffer.put(alpha);

        mQuadsBuffered++;
    }

    private void flush() {
        if(mQuadsBuffered <= 0) {
            return;
        }
        mVertexPreBuffer.glBufferDataArray();
        mVertexPreBuffer.reset();
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
