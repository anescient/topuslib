package net.chaosworship.topuslib.gl;

import android.support.annotation.DrawableRes;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_REPEAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glVertexAttribPointer;


public class FlatFillBrush extends Brush {

    private static final int VERTEXSIZE = 4;
    private static final int VERTICESPER = 4;

    // a vertex is
    // x
    // y
    // s
    // t

    private static final String SHADER_V =
            "attribute vec2 aPos;\n" +
            "attribute vec2 aTexCoord;\n" +
            "varying vec2 vTexCoord;\n" +
            "void main() {\n" +
            "    vTexCoord = aTexCoord;\n" +
            "    gl_Position = vec4(aPos, 0.0, 1.0);\n" +
            "}";
    private static final String SHADER_F =
            "precision mediump float;\n" +
            "uniform sampler2D uTexture;\n" +
            "varying vec2 vTexCoord;\n" +
            "void main() {\n" +
            "    gl_FragColor = texture2D(uTexture, vTexCoord);;\n" +
            "}\n";
    private static final Loader.LiteralProgram mProgram = new Loader.LiteralProgram(SHADER_V, SHADER_F);

    private final Loader mLoader;

    private final int mTextureHandle;

    private final int mPosHandle;
    private final int mTexCoordHandle;

    private final FloatVertexPreBuffer mVertexPreBuffer;
    private final int mVertexBufferHandle;

    FlatFillBrush(Loader loader) {
        mLoader = loader;

        int program = mLoader.useProgram(mProgram);

        mTextureHandle = glGetUniformLocation(program, "uTexture");

        mPosHandle = glGetAttribLocation(program, "aPos");
        mTexCoordHandle = glGetAttribLocation(program, "aTexCoord");

        mVertexPreBuffer = new FloatVertexPreBuffer(VERTEXSIZE * VERTICESPER, false);

        mVertexPreBuffer.put(-1, -1);
        mVertexPreBuffer.skip(2);

        mVertexPreBuffer.put(1, -1);
        mVertexPreBuffer.skip(2);

        mVertexPreBuffer.put(1, 1);
        mVertexPreBuffer.skip(2);

        mVertexPreBuffer.put(-1, 1);
        mVertexPreBuffer.skip(2);

        mVertexBufferHandle = generateBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandle);
        mVertexPreBuffer.glBufferDataArray();
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void fill(@DrawableRes int texture, boolean filter, float xScale, float yScale) {
        mLoader.useProgram(mProgram);

        glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandle);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mLoader.getTexture(texture));
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter ? GL_LINEAR : GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter ? GL_LINEAR : GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glUniform1i(mTextureHandle, 0);

        glVertexAttribPointer(mPosHandle, 2, GL_FLOAT, false, VERTEXSIZE * FLOATSIZE, 0);
        glEnableVertexAttribArray(mPosHandle);

        glVertexAttribPointer(mTexCoordHandle, 2, GL_FLOAT, false, VERTEXSIZE * FLOATSIZE, 2 * FLOATSIZE);
        glEnableVertexAttribArray(mTexCoordHandle);

        glDisable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        mVertexPreBuffer.reset();

        mVertexPreBuffer.skip(2);
        mVertexPreBuffer.put(0, 0);

        mVertexPreBuffer.skip(2);
        mVertexPreBuffer.put(1 / xScale, 0);

        mVertexPreBuffer.skip(2);
        mVertexPreBuffer.put(1 / xScale, 1 / yScale);

        mVertexPreBuffer.skip(2);
        mVertexPreBuffer.put(0, 1 / yScale);

        mVertexPreBuffer.glBufferDataArray();

        glDrawArrays(GL_TRIANGLE_FAN, 0, VERTICESPER);

        glDisableVertexAttribArray(mPosHandle);
        glDisableVertexAttribArray(mTexCoordHandle);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
