package net.chaosworship.topuslib.gl;

import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;


public abstract class QuadElementsBrush extends Brush {

    static protected final int ELEMENTSPER = 6;
    static protected final int VERTICESPER = 4;

    protected QuadElementsBrush() {
    }

    protected int makeQuadElementBuffer(int quadsCount) {
        int elementBufferHandle = generateBuffer();
        ShortBuffer elements = makeShortBuffer(quadsCount * ELEMENTSPER);
        elements.position(0);
        for(short quadi = 0; quadi < quadsCount; quadi++) {
            short base = (short)(quadi * 4);

            elements.put((short)(base + 3));
            elements.put((short)(base));
            elements.put((short)(base + 1));

            elements.put((short)(base + 1));
            elements.put((short)(base + 2));
            elements.put((short)(base + 3));
        }
        elements.position(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBufferHandle);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements.capacity() * SHORTSIZE, elements, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        return elementBufferHandle;
    }
}
