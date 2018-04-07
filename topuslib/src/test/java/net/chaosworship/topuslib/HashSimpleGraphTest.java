package net.chaosworship.topuslib;

import net.chaosworship.topuslib.graph.HashSimpleGraph;
import net.chaosworship.topuslib.graph.SimpleGraph;


public class HashSimpleGraphTest extends SimpleGraphTest {

    @Override
    SimpleGraph getGraph() {
        return new HashSimpleGraph();
    }
}
