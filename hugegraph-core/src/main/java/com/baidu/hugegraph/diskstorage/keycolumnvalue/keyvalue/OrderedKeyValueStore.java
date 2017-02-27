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

package com.baidu.hugegraph.diskstorage.keycolumnvalue.keyvalue;

import com.baidu.hugegraph.diskstorage.BackendException;
import com.baidu.hugegraph.diskstorage.StaticBuffer;
import com.baidu.hugegraph.diskstorage.keycolumnvalue.StoreTransaction;
import com.baidu.hugegraph.diskstorage.util.RecordIterator;

import java.util.List;
import java.util.Map;

/**
 * A {@link KeyValueStore} where the keys are ordered such that keys can be retrieved in order.
 *
 * @author Matthias Br&ouml;cheler (me@matthiasb.com);
 */
public interface OrderedKeyValueStore extends KeyValueStore {

    /**
     * Inserts the given key-value pair into the store. If the key already exists, its value is overwritten by the given one.
     *
     * @param key
     * @param value
     * @param txh
     * @throws com.baidu.hugegraph.diskstorage.BackendException
     */
    public void insert(StaticBuffer key, StaticBuffer value, StoreTransaction txh) throws BackendException;

    /**
     * Returns a list of all Key-value pairs ({@link KeyValueEntry} where the keys satisfy the given {@link KVQuery}.
     * That means, the key lies between the query's start and end buffers, satisfied the filter condition (if any) and the position
     * of the result in the result list iterator is less than the given limit.
     *
     * The operation is executed inside the context of the given transaction.
     *
     * @param query
     * @param txh
     * @return
     * @throws com.baidu.hugegraph.diskstorage.BackendException
     */
    public RecordIterator<KeyValueEntry> getSlice(KVQuery query, StoreTransaction txh) throws BackendException;


    /**
     * Like {@link #getSlice(KVQuery, com.baidu.hugegraph.diskstorage.keycolumnvalue.StoreTransaction)} but executes
     * all of the given queries at once and returns a map of all the result sets of each query.
     *
     * Only supported when the given store implementation supports multi-query, i.e.
     * {@link com.baidu.hugegraph.diskstorage.keycolumnvalue.StoreFeatures#hasMultiQuery()} return true. Otherwise
     * this method may throw a {@link UnsupportedOperationException}.
     *
     * @param queries
     * @param txh
     * @return
     * @throws BackendException
     */
    public Map<KVQuery,RecordIterator<KeyValueEntry>> getSlices(List<KVQuery> queries, StoreTransaction txh) throws BackendException;

}
