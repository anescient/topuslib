package net.chaosworship.topuslib.math.rangesearch;

import net.chaosworship.topuslib.math.Rectangle;

import java.util.Collection;
import java.util.List;


public interface RectangularSearch<T> {

    // clear and load
    void load(Collection<PointValuePair<T>> pointValues);

    // if the collection of points/values objects hasn't changed but the points themselves have been altered
    void reload();

    List<T> search(Rectangle area);
}
