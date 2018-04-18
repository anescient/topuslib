package net.chaosworship.topuslib.graph;


public class HashSimpleGraphTest extends SimpleGraphTest {

    @Override
    SimpleGraph getGraph() {
        return new HashSimpleGraph();
    }
}
