package net.chaosworship.topuslib.geom2d;


public class RectangularMapping implements Vec2Transformer {

    private final Rectangle mFromRect;
    private final Rectangle mToRect;

    public RectangularMapping(Rectangle from, Rectangle to) {
        mFromRect = from;
        mToRect = to;
    }

    @Override
    public Vec2 transform(Vec2 v) {
        Vec2 result = new Vec2(v);
        result.subtract(mFromRect.minx, mFromRect.miny);
        if(!mFromRect.isDegenerate()) {
            result.scale(mToRect.width() / mFromRect.width(), mToRect.height() / mFromRect.height());
        }
        result.add(mToRect.minx, mToRect.miny);
        return result;
    }
}
