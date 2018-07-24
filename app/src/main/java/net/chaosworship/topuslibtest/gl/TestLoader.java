package net.chaosworship.topuslibtest.gl;

import android.content.Context;

import net.chaosworship.topuslib.gl.Loader;


public class TestLoader extends Loader {

    private DotsBrush mDotsBrush;
    private ShadedTrianglesBrush mShadedTrianglesBrush;
    private TexturedSphereBrush mTexturedSphereBrush;

    public TestLoader(Context context) {
        super(context);
    }

    @Override
    public void invalidateAll() {
        super.invalidateAll();
        mDotsBrush = null;
        mShadedTrianglesBrush = null;
        mTexturedSphereBrush = null;
    }

    public DotsBrush getDotsBrush() {
        if(mDotsBrush == null) {
            mDotsBrush = new DotsBrush(this);
        }
        return mDotsBrush;
    }

    public ShadedTrianglesBrush getShadedTrianglesBrush() {
        if(mShadedTrianglesBrush == null) {
            mShadedTrianglesBrush = new ShadedTrianglesBrush(this);
        }
        return mShadedTrianglesBrush;
    }

    public TexturedSphereBrush getTexturedSphereBrush() {
        if(mTexturedSphereBrush == null) {
            mTexturedSphereBrush = new TexturedSphereBrush(this);
        }
        return mTexturedSphereBrush;
    }
}
