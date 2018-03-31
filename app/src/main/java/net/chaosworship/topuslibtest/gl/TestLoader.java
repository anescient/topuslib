package net.chaosworship.topuslibtest.gl;

import android.content.Context;

import net.chaosworship.topuslib.gl.Loader;


public class TestLoader extends Loader {

    private ShapesBrush mShapesBrush;

    public TestLoader(Context context) {
        super(context);
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        super.invalidateAll();
        mShapesBrush = null;
    }

    ShapesBrush getShapesBrush() {
        if(mShapesBrush == null) {
            mShapesBrush = new ShapesBrush(this);
        }
        return mShapesBrush;
    }
}
