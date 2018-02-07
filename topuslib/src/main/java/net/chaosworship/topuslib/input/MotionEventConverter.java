package net.chaosworship.topuslib.input;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashMap;

import net.chaosworship.topuslib.BuildConfig;
import net.chaosworship.topuslib.math.Vec2;


// consumes MotionEvents and makes better sense of them
@SuppressWarnings("unused")
public class MotionEventConverter {

    /////////////////////////////////////////////////////////////

    public static class Pointer {

        /////////////////////////////////////////////////////////

        private static class Event {

            private final Vec2 position;
            private final long timestamp;

            private Event(MotionEvent event, int pointerIndex) {
                position = new Vec2(
                        event.getX(pointerIndex),
                        event.getY(pointerIndex));
                timestamp = event.getEventTime();
            }

            private Event(MotionEvent event, int pointerIndex, int historicalPos) {
                position = new Vec2(
                        event.getHistoricalX(pointerIndex, historicalPos),
                        event.getHistoricalY(pointerIndex, historicalPos));
                timestamp = event.getHistoricalEventTime(historicalPos);
            }
        }

        /////////////////////////////////////////////////////////

        private static int sNextId = 1;

        public final int id;
        private boolean mDown;
        private final Event mFirstEvent; // first event may be removed from list of events
        private final ArrayList<Event> mEvents;

        // firstevent is expected to be a "down" action
        Pointer(MotionEvent firstEvent, int pointerIndex) {
            if(sNextId <= 0) { // overflow is theoretically possible
                sNextId = 1; // a collision is even more theoretically possible, but whatever
            }
            id = sNextId++;
            mDown = true;
            mFirstEvent = new Event(firstEvent, pointerIndex);
            mEvents = new ArrayList<>();
            mEvents.add(mFirstEvent);
            if(BuildConfig.DEBUG && firstEvent.getHistorySize() > 0) {
                throw new AssertionError("expected no history");
            }
        }

        public boolean isActive() {
            return mDown;
        }

        public float downSeconds() {
            long ms = latestEvent().timestamp - mFirstEvent.timestamp;
            return ms / 1000.0f;
        }

        // get a summary of movements since last call and then forget those events
        public PointerMotionSegment extractUpdate() {
            Event start;
            Event end;
            synchronized(mEvents) {
                start = mEvents.get(0);
                end = mEvents.get(mEvents.size() - 1);
                mEvents.clear();
                mEvents.add(end);
            }
            return new PointerMotionSegment(start.position, end.position, end.timestamp - start.timestamp);
        }

        public Vec2 getFirstPosition() {
            return mFirstEvent.position;
        }

        public Vec2 getLastPosition() {
            return latestEvent().position;
        }

        public ArrayList<Vec2> getPath() {
            ArrayList<Vec2> path = new ArrayList<>();
            synchronized(mEvents) {
                for(Event e : mEvents) {
                    path.add(e.position);
                }
            }
            return path;
        }

        private Event latestEvent() {
            synchronized(mEvents) {
                return mEvents.get(mEvents.size() - 1);
            }
        }

        private void pushHistorical(MotionEvent event, int pointerIndex) {
            synchronized(mEvents) {
                for(int history = 0; history < event.getHistorySize(); history++) {
                    mEvents.add(new Event(event, pointerIndex, history));
                }
            }
        }

        private void pushUpdate(MotionEvent event, int pointerIndex) {
            if(!mDown) {
                if(BuildConfig.DEBUG) {
                    throw new AssertionError("not active");
                } else {
                    return;
                }
            }
            synchronized(mEvents) {
                pushHistorical(event, pointerIndex);
                mEvents.add(new Event(event, pointerIndex));
            }
        }

        private void pushUpEvent(MotionEvent event, int pointerIndex) {
            if(BuildConfig.DEBUG && event.getHistorySize() > 0) {
                throw new AssertionError("expected no history");
            }
            synchronized(mEvents) {
                mEvents.add(new Event(event, pointerIndex));
            }
            end();
        }

        private void end() {
            if(BuildConfig.DEBUG && !mDown) {
                throw new AssertionError("not active");
            }
            mDown = false;
        }
    }

    /////////////////////////////////////////////////////////////

    // active (down, still moving) pointers by MotionEvent pointer id
    private final HashMap<Integer, Pointer> mActivePointers;

    // pointers that have gone up and ended
    private final ArrayList<Pointer> mFinishedPointers;

    @SuppressLint("UseSparseArrays")
    public MotionEventConverter() {
        mActivePointers = new HashMap<>();
        mFinishedPointers = new ArrayList<>();
    }

    // get all active and finished pointers, removing finished pointers
    public ArrayList<Pointer> dumpPointers() {
        ArrayList<Pointer> pointers = new ArrayList<>();
        // if finished pointers are taken first there shouldn't be any duplicates
        // at worst a pointer will sneak from active to finished and get picked up later
        synchronized(mFinishedPointers) {
            pointers.addAll(mFinishedPointers);
            mFinishedPointers.clear();
        }
        synchronized(mActivePointers) {
            pointers.addAll(mActivePointers.values());
        }
        return pointers;
    }

    public ArrayList<Pointer> getActivePointers() {
        synchronized(mActivePointers) {
            return new ArrayList<>(mActivePointers.values());
        }
    }

    // return null if none, otherwise return and discard
    public Pointer pollFinishedPointers() {
        synchronized(mFinishedPointers) {
            return mFinishedPointers.isEmpty() ? null : mFinishedPointers.remove(0);
        }
    }

    // what just happened, tell the thing
    public void pushEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                pushDownEvent(event);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                pushUpEvent(event);
                break;

            case MotionEvent.ACTION_CANCEL:
                // cancel occurs e.g. when screen is turned off with pointers still down
                abortAll();
                break;

            case MotionEvent.ACTION_MOVE:
                synchronized(mActivePointers) {
                    for(int pi = 0; pi < event.getPointerCount(); pi++) {
                        updatePointer(event, pi);
                    }
                }
                break;

            default:
                if(BuildConfig.DEBUG) {
                    Log.v("MotionEventConverter", "UNHANDLED EVENT: " + MotionEvent.actionToString(action));
                }
                break;
        }
    }

    private void pushDownEvent(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        int eventPointerId = event.getPointerId(pointerIndex);
        if(mActivePointers.containsKey(eventPointerId)) {
            throw new AssertionError("already down?");
        }

        synchronized(mActivePointers) {
            mActivePointers.put(eventPointerId, new Pointer(event, pointerIndex));
            for(int i = 0; i < event.getPointerCount(); i++) {
                if(i == pointerIndex) {
                    continue;
                }
                updatePointer(event, i);
            }
        }
    }

    private void pushUpEvent(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        int eventPointerId = event.getPointerId(pointerIndex);
        if(!mActivePointers.containsKey(eventPointerId)) {
            if(BuildConfig.DEBUG) {
                throw new AssertionError("no such active pointer");
            } else {
                return;
            }
        }

        Pointer endingPointer;
        synchronized(mActivePointers) {
            endingPointer = mActivePointers.remove(eventPointerId);
        }
        endingPointer.pushUpEvent(event, pointerIndex);
        synchronized(mFinishedPointers) {
            mFinishedPointers.add(endingPointer);
        }
    }

    private void abortAll() {
        ArrayList<Pointer> abortPointers;
        synchronized(mActivePointers) {
            abortPointers = new ArrayList<>(mActivePointers.values());
            mActivePointers.clear();
        }
        synchronized(mFinishedPointers) {
            for(Pointer p : abortPointers) {
                p.end();
                mFinishedPointers.add(p);
            }
        }
    }

    // assumes caller has synced active pointer collection
    private void updatePointer(MotionEvent event, int pointerIndex) {
        int eventPointerId = event.getPointerId(pointerIndex);
        if(!mActivePointers.containsKey(eventPointerId)) {
            if(BuildConfig.DEBUG) {
                throw new AssertionError();
            } else {
                return;
            }
        }
        Pointer pointer = mActivePointers.get(eventPointerId);
        pointer.pushUpdate(event, pointerIndex);
    }
}
