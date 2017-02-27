/*
 * This is the sample incremental loading script given in the manual.
 * 
 * See the documentation on hugegraph-Hadoop incremental loading and the
 * config option "loader-script-file" for more information.
 */

def hugegraphVertex getOrCreateVertex(faunusVertex, graph, context, log) {
    String uniqueKey = "name";
    Object uniqueValue = faunusVertex.getProperty(uniqueKey);
    Vertex hugegraphVertex;
    if (null == uniqueValue)
      throw new RuntimeException(faunusVertex + " has no value for key " + uniqueKey);

    Iterator<Vertex> itty = graph.query().has(uniqueKey, uniqueValue).vertices().iterator();
    if (itty.hasNext()) {
      hugegraphVertex = itty.next();
      if (itty.hasNext())
        log.info("The key {} has duplicated value {}", uniqueKey, uniqueValue);
    } else {
      hugegraphVertex = graph.addVertex(faunusVertex.getId());
    }
    return hugegraphVertex;
}

def hugegraphEdge getOrCreateEdge(faunusEdge, inVertex, outVertex, graph, context, log) {
    final String label = faunusEdge.getLabel();

    log.debug("outVertex:{} label:{} inVertex:{}", outVertex, label, inVertex);

    final Edge hugegraphEdge = !outVertex.out(label).has("id", inVertex.getId()).hasNext() ?
        graph.addEdge(null, outVertex, inVertex, label) :
        outVertex.outE(label).as("here").inV().has("id", inVertex.getId()).back("here").next();

    return hugegraphEdge;
}

def Object getOrCreateVertexProperty(faunusProperty, vertex, graph, context, log) {

    final com.baidu.hugegraph.core.PropertyKey pkey = faunusProperty.getPropertyKey();
    if (pkey.cardinality().equals(com.baidu.hugegraph.core.Cardinality.SINGLE)) {
        vertex.setProperty(pkey.name(), faunusProperty.getValue());
    } else {
        vertex.addProperty(pkey.name(), faunusProperty.getValue());
    }

    log.debug("Set property {}={} on {}", pkey.name(), faunusProperty.getValue(), vertex);

    return faunusProperty.getValue();
}
