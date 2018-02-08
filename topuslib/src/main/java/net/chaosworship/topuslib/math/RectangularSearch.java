package net.chaosworship.topuslib.math;

import java.util.Collection;


public interface RectangularSearch<T> {

    void load(Collection<PointValuePair<T>> pointValues);

    Iterable<T> search(Rectangle area);
}
