package net.chaosworship.topuslibtest.gl;

import android.content.Context;

import net.chaosworship.topuslib.gl.Loader;


public class TestLoader extends Loader {

    private DotsBrush mDotsBrush;

    public TestLoader(Context context) {
        super(context);
    }

    @Override
    public void invalidateAll() {
        super.invalidateAll();
        mDotsBrush = null;
    }

    public DotsBrush getDotsBrush() {
        if(mDotsBrush == null) {
            mDotsBrush = new DotsBrush(this);
        }
        return mDotsBrush;
    }
}
