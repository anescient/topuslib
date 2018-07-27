package net.chaosworship.topuslib.gl;

import net.chaosworship.topuslib.BuildConfig;

import java.util.ArrayList;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glVertexAttribPointer;


@SuppressWarnings("unused")
public class FloatAttributeList {

    private static final int FLOATSIZE = 4;
    private static final int INVALIDHANDLE = 0;

    /////////////////////////////////////////////

    public static class AttributeException extends Exception {
        AttributeException(String message) {
            super(message);
        }
    }

    /////////////////////////////////////////////

    private static class Attribute {
        final String glslName;
        final int floatCount;
        int handle;

        Attribute(String glslName, int floatCount) {
            this.glslName = glslName;
            this.floatCount = floatCount;
            handle = INVALIDHANDLE;
        }
    }

    /////////////////////////////////////////////

    private final ArrayList<Attribute> mAttributes;
    private boolean mEnabled;
    private boolean mInitialized;
    private int mFloatCount;

    public FloatAttributeList() {
        mAttributes = new ArrayList<>();
        mEnabled = false;
        mInitialized = false;
        mFloatCount = 0;
    }

    public int floatCount() {
        return mFloatCount;
    }

    public void addFloat(String glslName) throws AttributeException {
        tryAddAttribute(new Attribute(glslName, 1));
    }

    public void addVec2(String glslName) throws AttributeException {
        tryAddAttribute(new Attribute(glslName, 2));
    }

    public void addVec3(String glslName) throws AttributeException {
        tryAddAttribute(new Attribute(glslName, 3));
    }

    public void addFloatArray(String glslName, int size) throws AttributeException {
        tryAddAttribute(new Attribute(glslName, size));
    }

    private void tryAddAttribute(Attribute attribute) throws AttributeException {
        if(mInitialized) {
            throw new AttributeException("can't add attribute after initialization");
        }
        if(BuildConfig.DEBUG) {
            if(mEnabled) {
                throw new AssertionError();
            }
        }
        mAttributes.add(attribute);
        mFloatCount += attribute.floatCount;
    }

    public void enable(int program) throws AttributeException {
        if(mEnabled) {
            throw new IllegalStateException();
        }

        if(!mInitialized) {
            for(Attribute attr : mAttributes) {
                attr.handle = glGetAttribLocation(program, attr.glslName);
                if(attr.handle < 0) {
                    throw new AttributeException(String.format("couldn't find attribute %s", attr.glslName));
                }
            }
            mInitialized = true;
        }

        int stride = 0;
        for(Attribute attr : mAttributes) {
            stride += attr.floatCount * FLOATSIZE;
        }

        int offset = 0;
        for(Attribute attr : mAttributes) {
            glVertexAttribPointer(attr.handle, attr.floatCount, GL_FLOAT, false, stride, offset);
            offset += attr.floatCount * FLOATSIZE;
            glEnableVertexAttribArray(attr.handle);
        }

        mEnabled = true;
    }

    public void disable() {
        if(!mEnabled) {
            throw new IllegalStateException();
        }

        for(Attribute attr : mAttributes) {
            glDisableVertexAttribArray(attr.handle);
        }

        mEnabled = false;
    }
}
