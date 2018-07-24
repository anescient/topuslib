package net.chaosworship.topuslibtest.gl;

import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom3d.TriangleMesh;
import net.chaosworship.topuslib.geom3d.TriangulatedSphere;
import net.chaosworship.topuslib.geom3d.Vec3;
import net.chaosworship.topuslib.gl.Brush;
import net.chaosworship.topuslib.gl.FloatVertexPreBuffer;
import net.chaosworship.topuslib.gl.ShortElementPreBuffer;
import net.chaosworship.topuslib.tuple.IntTriple;
import net.chaosworship.topuslibtest.R;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_REPEAT;
import static android.opengl.GLES20.GL_SRC_ALPHA;
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


public class TexturedSphereBrush extends Brush {

    private static final int VERTEXSIZE = 5;
    private static final int ELEMENTSPER = 3;
    private static final String PROGRAMNAME = "texturedtriangles";

    // a vertex is:
    // position x
    // position y
    // position z
    // texture s
    // texture t

    private final TestLoader mLoader;

    private final int mFaceCount;

    private final int mVertexBufferHandle;
    private final int mElementBufferHandle;

    private final int mVPMatrixHandle;
    private final int mTextureHandle;

    private final int mPosHandle;
    private final int mTexCoordHandle;

    TexturedSphereBrush(TestLoader loader) {
        mLoader = loader;

        TriangleMesh sphereMesh = TriangulatedSphere.generateIcosphere(4);
        mFaceCount = sphereMesh.getFaces().size();

        FloatVertexPreBuffer vertexPreBuffer = new FloatVertexPreBuffer(sphereMesh.getVertices().size() * VERTEXSIZE, false);
        for(Vec3 pos : sphereMesh.getVertices()) {
            vertexPreBuffer.put(pos);
            float s = (float)(new Vec2(pos.x, pos.y).atan2() / (2 * Math.PI)) + 0.5f;
            vertexPreBuffer.put(s);
            float r = new Vec2(pos.x, pos.y).magnitude();
            vertexPreBuffer.put(2 * (float)(new Vec2(pos.z, r).atan2() / (2 * Math.PI)));
        }

        ShortElementPreBuffer elementPreBuffer= new ShortElementPreBuffer(mFaceCount * ELEMENTSPER, false);
        for(IntTriple face : sphereMesh.getFaces()) {
            elementPreBuffer.put(face);
        }

        mVertexBufferHandle = generateBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandle);
        vertexPreBuffer.glBufferDataArray();
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        mElementBufferHandle = generateBuffer();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mElementBufferHandle);
        elementPreBuffer.glBufferDataElementArray();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        int program = mLoader.useProgram(PROGRAMNAME);

        mVPMatrixHandle = glGetUniformLocation(program, "uVPMatrix");
        mTextureHandle = glGetUniformLocation(program, "uTexture");

        mPosHandle = glGetAttribLocation(program, "aPos");
        mTexCoordHandle = glGetAttribLocation(program, "aTexCoord");
    }

    public void begin(float[] matPV) {
        mLoader.useProgram(PROGRAMNAME);

        glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandle);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mElementBufferHandle);

        glUniformMatrix4fv(mVPMatrixHandle, 1, false, matPV, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mLoader.getTexture(R.drawable.earthgrid));
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glUniform1i(mTextureHandle, 0);

        glVertexAttribPointer(mPosHandle, 3, GL_FLOAT, false, VERTEXSIZE * FLOATSIZE, 0);
        glEnableVertexAttribArray(mPosHandle);

        glVertexAttribPointer(mTexCoordHandle, 2, GL_FLOAT, false, VERTEXSIZE * FLOATSIZE, 3 * FLOATSIZE);
        glEnableVertexAttribArray(mTexCoordHandle);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
    }

    public void drawSphere() {
        glDrawElements(GL_TRIANGLES, mFaceCount * ELEMENTSPER, GL_UNSIGNED_SHORT, 0);
    }

    public void end() {
        glDisableVertexAttribArray(mPosHandle);
        glDisableVertexAttribArray(mTexCoordHandle);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
