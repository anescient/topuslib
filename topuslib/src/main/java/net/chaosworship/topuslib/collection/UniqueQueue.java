package net.chaosworship.topuslib.collection;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;


// similar to LinkedHashSet, but focused on being a FIFO
// objects can be added, with duplicates rejected, then (only) removed in the same order
public final class UniqueQueue<E>
        implements Iterable<E> {

    private final LinkedList<E> mList;
    private final HashSet<E> mSet;

    public UniqueQueue() {
        mList = new LinkedList<>();
        mSet = new HashSet<>();
    }

    // return true if object was added
    // return false if object was already in the collection
    //   in this case the existing object is not moved in the queue
    public boolean add(E object) {
        if(object == null)
            throw new IllegalArgumentException("cannot add null");
        if(mSet.contains(object))
            return false;
        mSet.add(object);
        mList.add(object);
        return true;
    }

    public E remove() {
        if(mList.isEmpty())
            return null;
        E object = mList.remove();
        mSet.remove(object);
        return object;
    }

    // discard objects in order as needed to reduce the size of the collection
    public void trim(int size) {
        int oversize = mSet.size() - size;
        while(oversize-- > 0) {
            remove();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return mList.iterator();
    }
}
