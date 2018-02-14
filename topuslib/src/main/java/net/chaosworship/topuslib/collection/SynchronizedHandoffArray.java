package net.chaosworship.topuslib.collection;

import java.util.ArrayList;


// insert one object at a time, retrieve all accumulated objects and clear
public final class SynchronizedHandoffArray<E> {

    private final Object mHandoffLock = new Object();

    // these references are swapped on get
    private ArrayList<E> arrayIn = new ArrayList<>();
    private ArrayList<E> arrayOut = new ArrayList<>();

    public void add(E object) {
        synchronized (mHandoffLock) {
            arrayIn.add(object);
        }
    }

    // get array of accumulated objects and clear intake array
    // returned array need not be cleared by caller, but is safe to modify
    // NOT reentrant, do NOT hold reference to returned array
    public ArrayList<E> takeHandoff() {
        synchronized (mHandoffLock) {
            ArrayList<E> temp = arrayIn;
            arrayIn = arrayOut;
            arrayOut = temp;
            arrayIn.clear();
        }

        return arrayOut;
    }
}
