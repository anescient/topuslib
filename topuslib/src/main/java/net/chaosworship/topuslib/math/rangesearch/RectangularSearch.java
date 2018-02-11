package net.chaosworship.topuslib.math.rangesearch;

import net.chaosworship.topuslib.math.Rectangle;

import java.util.Collection;
import java.util.List;


public interface RectangularSearch<T> {

    void load(Collection<PointValuePair<T>> pointValues);

    List<T> search(Rectangle area);
}
