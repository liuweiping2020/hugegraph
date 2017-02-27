package com.baidu.hugegraph.hadoop.formats.graphson

def hugegraphVertex getOrCreateVertex(faunusVertex, graph, context, log) {
    String uniqueKey = "name";
    Object uniqueValue = faunusVertex.value(uniqueKey);
    Vertex hugegraphVertex;
    if (null == uniqueValue)
      throw new RuntimeException("The provided Faunus vertex does not have a property for the unique key: " + faunusVertex);
  
    Iterator<Vertex> itty = graph.query().has(uniqueKey, uniqueValue).vertices().iterator();
    if (itty.hasNext()) {
      hugegraphVertex = itty.next();
      if (itty.hasNext())
        log.info("The unique key is not unique as more than one vertex with the value {}", uniqueValue);
    } else {
      hugegraphVertex = graph.addVertex(faunusVertex.longId(),faunusVertex.label());
    }
    return hugegraphVertex;
}
