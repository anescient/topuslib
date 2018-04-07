package net.chaosworship.topuslib;

import net.chaosworship.topuslib.graph.MatrixSimpleGraph;
import net.chaosworship.topuslib.graph.SimpleGraph;


public class MatrixSimpleGraphTest extends SimpleGraphTest {
    @Override
    SimpleGraph getGraph() {
        return new MatrixSimpleGraph();
    }
}
