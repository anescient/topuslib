package net.chaosworship.topuslib.gl;

import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT16;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_RENDERBUFFER;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindRenderbuffer;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glFramebufferRenderbuffer;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glRenderbufferStorage;
import static android.opengl.GLES20.glViewport;


public class FrameBuffer {

    private final int mWidth;
    private final int mHeight;
    private final int mFBO;
    private final int mColorTexture;

    FrameBuffer(int width, int height) throws Loader.LoaderException {
        mWidth = width;
        mHeight = height;

        mFBO = Loader.genFramebuffer();
        mColorTexture = Loader.createTexture(mWidth, mHeight);

        int depthRBO = Loader.genRenderBuffer();
        glBindRenderbuffer(GL_RENDERBUFFER, depthRBO);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, mWidth, mHeight);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);

        glBindFramebuffer(GL_FRAMEBUFFER, mFBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, mColorTexture, 0);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRBO);
        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        if(status != GL_FRAMEBUFFER_COMPLETE) {
            throw new Loader.LoaderException("framebuffer not complete");
        }
    }

    boolean isSize(int width, int height) {
        return mWidth == width && mHeight == height;
    }

    public void bindSetViewport() {
        glBindFramebuffer(GL_FRAMEBUFFER, mFBO);
        glViewport(0, 0, mWidth, mHeight);
    }

    public int getTexture() {
        return mColorTexture;
    }
}
