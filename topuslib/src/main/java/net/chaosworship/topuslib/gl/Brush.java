package net.chaosworship.topuslib.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.glGenBuffers;


@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Brush {

    protected static final int SHORTSIZE = 2;
    protected static final int FLOATSIZE = 4;

    protected Brush() {}

    protected static int generateBuffer() {
        final int buffers[] = new int[1];
        glGenBuffers(1, buffers, 0);
        return buffers[0];
    }

    protected static FloatBuffer makeFloatBuffer(int n) {
        return ByteBuffer.allocateDirect(FLOATSIZE * n).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    protected static ShortBuffer makeShortBuffer(int n) {
        return ByteBuffer.allocateDirect(SHORTSIZE * n).order(ByteOrder.nativeOrder()).asShortBuffer();
    }
}
