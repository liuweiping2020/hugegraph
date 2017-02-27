// Copyright 2017 HugeGraph Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.baidu.hugegraph.blueprints;

import com.baidu.hugegraph.diskstorage.configuration.ModifiableConfiguration;
import com.baidu.hugegraph.graphdb.configuration.GraphDatabaseConfiguration;
import com.baidu.hugegraph.graphdb.database.idassigner.placement.SimpleBulkPlacementStrategy;
import com.baidu.hugegraph.graphdb.olap.computer.FulgoraGraphComputer;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategy;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.engine.ComputerTraversalEngine;
import org.apache.tinkerpop.gremlin.structure.Graph;

import java.util.stream.Stream;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public abstract class AbstractHugeGraphComputerProvider extends AbstractHugeGraphProvider {

    @Override
    public GraphTraversalSource traversal(final Graph graph) {
        return GraphTraversalSource.build().engine(ComputerTraversalEngine.build().computer(FulgoraGraphComputer.class)).create(graph);
    }

    @Override
    public GraphTraversalSource traversal(final Graph graph, final TraversalStrategy... strategies) {
        final GraphTraversalSource.Builder builder = GraphTraversalSource.build().engine(ComputerTraversalEngine.build().computer(FulgoraGraphComputer.class));
        Stream.of(strategies).forEach(builder::with);
        return builder.create(graph);
    }

    @Override
    public ModifiableConfiguration getHugeGraphConfiguration(String graphName, Class<?> test, String testMethodName) {
        return GraphDatabaseConfiguration.buildGraphConfiguration()
                .set(GraphDatabaseConfiguration.IDS_BLOCK_SIZE,1)
                .set(SimpleBulkPlacementStrategy.CONCURRENT_PARTITIONS,1)
                .set(GraphDatabaseConfiguration.CLUSTER_MAX_PARTITIONS, 2)
                .set(GraphDatabaseConfiguration.IDAUTHORITY_CAV_BITS,0);
    }

}
