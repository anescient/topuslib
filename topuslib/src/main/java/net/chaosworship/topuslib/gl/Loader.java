package net.chaosworship.topuslib.gl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Scanner;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRUE;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenRenderbuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glUseProgram;


@SuppressWarnings({"unused", "SameParameterValue", "WeakerAccess"})
public class Loader {

    protected static final int INVALIDPROGRAM = 0;
    protected static final int INVALIDTEXTURE = 0;

    /////////////////////////////////////////////

    public static class LoaderException extends Exception {
        LoaderException(String message) {
            super(message);
        }
    }

    /////////////////////////////////////////////

    public static class LiteralProgram {

        private final String mShaderV;
        private final String mShaderF;
        private final int mHashcode;

        public LiteralProgram(String shader_v, String shader_f) {
            mShaderV = shader_v;
            mShaderF = shader_f;
            mHashcode = mShaderV.hashCode() ^ mShaderF.hashCode();
        }

        @Override
        public boolean equals(Object rhs) {
            if(rhs == null || !this.getClass().equals(rhs.getClass())) {
                return false;
            }

            LiteralProgram rhsProgram = (LiteralProgram)rhs;
            return mHashcode == rhsProgram.mHashcode
                    && mShaderV.equals(rhsProgram.mShaderV)
                    && mShaderF.equals(rhsProgram.mShaderF);
        }

        @Override
        public int hashCode() {
            return mHashcode;
        }
    }

    /////////////////////////////////////////////

    protected final Context mContext;
    private final HashMap<String, Integer> mPrograms;
    private final HashMap<LiteralProgram, Integer> mLiteralPrograms;
    private final HashMap<Integer, Integer> mTextures;
    private final HashMap<String, FrameBuffer> mFrameBuffers;

    private ShapesBrush mShapesBrush;
    private TrianglesBrush mTrianglesBrush;

    @SuppressLint("UseSparseArrays")
    public Loader(Context context) {
        mContext = context;
        mPrograms = new HashMap<>();
        mLiteralPrograms = new HashMap<>();
        mTextures = new HashMap<>();
        mFrameBuffers = new HashMap<>();
        invalidateAll();
    }

    public void invalidateAll() {
        mPrograms.clear();
        mLiteralPrograms.clear();
        mTextures.clear();
        mFrameBuffers.clear();
        mShapesBrush = null;
        mTrianglesBrush = null;
    }

    public int useProgram(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if(!mPrograms.containsKey(name)) {
            try {
                mPrograms.put(name, loadProgram(name));
            } catch (LoaderException e) {
                e.printStackTrace();
                mPrograms.put(name, INVALIDPROGRAM); // don't try more than once
            }
        }

        int program = mPrograms.get(name);
        if(program != INVALIDPROGRAM) {
            glUseProgram(program);
        }
        return program;
    }

    public int useProgram(LiteralProgram literalProgram) {
        if(!mLiteralPrograms.containsKey(literalProgram)) {
            try {
                mLiteralPrograms.put(literalProgram, loadProgram(literalProgram));
            } catch(LoaderException e) {
                e.printStackTrace();
                mLiteralPrograms.put(literalProgram, INVALIDPROGRAM); // don't try more than once
            }
        }

        int program = mLiteralPrograms.get(literalProgram);
        if(program != INVALIDPROGRAM) {
            glUseProgram(program);
        }
        return program;
    }

    public FrameBuffer getFrameBuffer(String name, int width, int height) throws LoaderException {
        if(!mFrameBuffers.containsKey(name)) {
            FrameBuffer newFB;
            try {
                newFB = new FrameBuffer(width, height);
            } catch (LoaderException e) {
                e.printStackTrace();
                newFB = null;
            }

            mFrameBuffers.put(name, newFB);
        }

        FrameBuffer frameBuffer = mFrameBuffers.get(name);
        if(frameBuffer != null && !frameBuffer.isSize(width, height)) {
            throw new LoaderException("possible frame buffer name collision");
        }
        return frameBuffer;
    }

    public int getTexture(int resourceId) {
        if(!mTextures.containsKey(resourceId)) {
            try {
                mTextures.put(resourceId, loadTexture(resourceId));
            } catch (LoaderException e) {
                e.printStackTrace();
                mTextures.put(resourceId, INVALIDTEXTURE); // don't try more than once
            }
        }
        return mTextures.get(resourceId);
    }

    public ShapesBrush getShapesBrush() {
        if(mShapesBrush == null) {
            mShapesBrush = new ShapesBrush(this);
        }
        return mShapesBrush;
    }

    public TrianglesBrush getTrianglesBrush() {
        if(mTrianglesBrush == null) {
            mTrianglesBrush = new TrianglesBrush(this);
        }
        return mTrianglesBrush;
    }

    private int loadProgram(String shader_v, String shader_f) throws LoaderException {

        int vertShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertShader, shader_v);
        glCompileShader(vertShader);
        checkShaderCompile(vertShader);

        int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragShader, shader_f);
        glCompileShader(fragShader);
        checkShaderCompile(fragShader);

        int program = glCreateProgram();
        glAttachShader(program, vertShader);
        glAttachShader(program, fragShader);
        glLinkProgram(program);
        checkProgramLink(program);

        return program;
    }

    private int loadProgram(LiteralProgram literalProgram) throws LoaderException {
        return loadProgram(literalProgram.mShaderV, literalProgram.mShaderF);
    }

    private int loadProgram(String shaderBaseName) throws LoaderException {
        return loadProgram(
                readShaderAsset(shaderBaseName + "_v"),
                readShaderAsset(shaderBaseName + "_f"));
    }

    private static void checkShaderCompile(int shader) throws LoaderException {
        int[] result = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, result, 0);
        if(result[0] != GL_TRUE) {
            throw new LoaderException(glGetShaderInfoLog(shader));
        }
    }

    private static void checkProgramLink(int program) throws LoaderException {
        int[] result = new int[1];
        glGetProgramiv(program, GL_LINK_STATUS, result, 0);
        if(result[0] != GL_TRUE) {
            throw new LoaderException(glGetProgramInfoLog(program));
        }
    }

    private String readShaderAsset(String name) throws LoaderException {
        try {
            InputStream is = mContext.getAssets().open("shaders/" + name + ".glsl");
            Scanner s = new Scanner(is).useDelimiter("\\A");
            return s.next();
        } catch (IOException e) {
            throw new LoaderException(e.getMessage());
        }
    }

    private String readShaderRawRes(String name) throws LoaderException {
        int identifier = mContext.getResources().getIdentifier(name, "raw", mContext.getPackageName());
        if(identifier == 0) {
            throw new LoaderException("shader not found:" + name);
        }
        InputStream is = mContext.getResources().openRawResource(identifier);
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.next();
    }

    private int loadTexture(int resourceId) throws LoaderException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), resourceId, options);
        return loadTextureAndRecycle(bmp);
    }

    public static int loadTextureAndRecycle(Bitmap bitmap) throws LoaderException {
        int handle = genTexture();
        glBindTexture(GL_TEXTURE_2D, handle);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
        bitmap.recycle();
        return handle;
    }

    static int createTexture(int width, int height) throws LoaderException {
        int handle = genTexture();
        glBindTexture(GL_TEXTURE_2D, handle);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
        glBindTexture(GL_TEXTURE_2D, 0);
        return handle;
    }

    static int genTexture() throws LoaderException {
        int[] textureHandle = new int[1];
        glGenTextures(1, textureHandle, 0);
        if(textureHandle[0] == 0) {
            throw new LoaderException("failed to generate texture");
        }
        return textureHandle[0];
    }

    static int genFramebuffer() throws LoaderException {
        int[] fboHandle = new int[1];
        glGenFramebuffers(1, fboHandle, 0);
        if(fboHandle[0] == 0) {
            throw new LoaderException("failed to generate fbo");
        }
        return fboHandle[0];
    }

    static int genRenderBuffer() throws LoaderException {
        int[] rboHandle = new int[1];
        glGenRenderbuffers(1, rboHandle, 0);
        if(rboHandle[0] == 0) {
            throw new LoaderException("failed to generate rbo");
        }
        return rboHandle[0];
    }

    protected static void debugSaveImage(Bitmap bmp, String filename) {
        String path = Environment.getExternalStorageDirectory().toString();
        File file = new File(path, "debugimage-" + filename + ".png");
        try {
            OutputStream fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
