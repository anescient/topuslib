package net.chaosworship.topuslib.gl;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glVertexAttribPointer;


public class FrameBrush extends Brush {

    private static final String SHADER_V =
            "attribute vec2 aPos;\n" +
            "varying vec2 vPos;\n" +
            "void main() {\n" +
            "    gl_Position = vec4(aPos * 2.0 - 1.0, 0.0, 1.0);\n" +
            "    vPos = aPos;\n" +
            "}";
    private static final String SHADER_F =
            "precision mediump float;\n" +
            "uniform sampler2D uFrameTexture;\n" +
            "varying vec2 vPos;\n" +
            "void main() {\n" +
            "    gl_FragColor = texture2D(uFrameTexture, vPos);\n" +
            "}\n";
    private static final Loader.LiteralProgram mProgram = new Loader.LiteralProgram(SHADER_V, SHADER_F);

    private final Loader mLoader;
    private final int mVertexBuffer;

    private final int mFrameTextureHandle;
    private final int mPosHandle;

    FrameBrush(Loader loader) {
        mLoader = loader;
        mVertexBuffer = generateBuffer();

        FloatBuffer vertexBuffer = makeFloatBuffer(8 * FLOATSIZE);
        vertexBuffer.position(0);

        vertexBuffer.put(0.0f); vertexBuffer.put(0.0f);
        vertexBuffer.put(1.0f); vertexBuffer.put(0.0f);
        vertexBuffer.put(1.0f); vertexBuffer.put(1.0f);
        vertexBuffer.put(0.0f); vertexBuffer.put(1.0f);

        vertexBuffer.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer.capacity() * FLOATSIZE, vertexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        int program = mLoader.useProgram(mProgram);
        mFrameTextureHandle = glGetUniformLocation(program, "uFrameTexture");
        mPosHandle = glGetAttribLocation(program, "aPos");
    }

    public void putTexture(int texture, boolean filter) {
        mLoader.useProgram(mProgram);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter ? GL_LINEAR : GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter ? GL_LINEAR : GL_NEAREST);
        glUniform1i(mFrameTextureHandle, 0);

        glDisable(GL_DEPTH_TEST);
        glBindBuffer(GL_ARRAY_BUFFER, mVertexBuffer);
        glVertexAttribPointer(mPosHandle, 2, GL_FLOAT, false, 2 * FLOATSIZE, 0);
        glEnableVertexAttribArray(mPosHandle);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisableVertexAttribArray(mPosHandle);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
