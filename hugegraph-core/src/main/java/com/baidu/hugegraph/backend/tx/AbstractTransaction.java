package com.baidu.hugegraph.backend.tx;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.hugegraph.HugeGraph;
import com.baidu.hugegraph.backend.BackendException;
import com.baidu.hugegraph.backend.Transaction;
import com.baidu.hugegraph.backend.id.Id;
import com.baidu.hugegraph.backend.id.IdGenerator;
import com.baidu.hugegraph.backend.id.IdGeneratorFactory;
import com.baidu.hugegraph.backend.query.IdQuery;
import com.baidu.hugegraph.backend.query.Query;
import com.baidu.hugegraph.backend.serializer.AbstractSerializer;
import com.baidu.hugegraph.backend.store.BackendEntry;
import com.baidu.hugegraph.backend.store.BackendMutation;
import com.baidu.hugegraph.backend.store.BackendStore;
import com.baidu.hugegraph.type.HugeTypes;
import com.google.common.base.Preconditions;

public abstract class AbstractTransaction implements Transaction {

    // parent graph
    protected final HugeGraph graph;
    protected AbstractSerializer serializer;
    protected IdGenerator idGenerator;

    protected BackendStore store;

    protected Map<Id, BackendEntry> additions;
    protected Map<Id, BackendEntry> deletions;

    private static final Logger logger = LoggerFactory.getLogger(AbstractTransaction.class);

    public AbstractTransaction(HugeGraph graph, BackendStore store) {
        this.graph = graph;
        this.serializer = this.graph.serializer();
        this.idGenerator = IdGeneratorFactory.generator();

        this.store = store;
        this.additions = new ConcurrentHashMap<Id, BackendEntry>();
        this.deletions = new ConcurrentHashMap<Id, BackendEntry>();
    }

    public BackendStore store() {
        return this.store;
    }

    public Iterable<BackendEntry> query(Query query) {
        logger.debug("Transaction query: {}", query);
        query = this.serializer.writeQuery(query);
        return this.store.query(query);
    }

    public BackendEntry query(HugeTypes type, Id id) {
        IdQuery q = new IdQuery(type, id);
        Iterator<BackendEntry> results = this.query(q).iterator();
        if (results.hasNext()) {
            BackendEntry entry = results.next();
            assert !results.hasNext();
            return entry;
        }
        return null;
    }

    public BackendEntry get(HugeTypes type, Id id) {
        BackendEntry entry = query(type, id);
        if (entry == null) {
            throw new BackendException(String.format(
                    "Not found id '%s' with type %s", id, type));
        }
        return entry;
    }

    protected void prepareCommit() {
        // for sub-class preparing data, nothing to do here
        logger.debug("Transaction prepare commit...");
    }

    @Override
    public void commit() throws BackendException {
        logger.debug("Transaction commit...");
        this.prepareCommit();

        BackendMutation m = new BackendMutation(
                this.additions.values(),
                this.deletions.values());

        // if an exception occurred, catch in the upper layer and roll back
        this.store.beginTx();
        this.store.mutate(m);
        this.store.commitTx();

        this.additions.clear();
        this.deletions.clear();
    }

    @Override
    public void rollback() throws BackendException {
        logger.debug("Transaction rollback...");
        this.store.rollbackTx();
    }

    public void addEntry(BackendEntry entry) {
        logger.debug("Transaction add entry {}", entry);
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(entry.id());

        Id id = entry.id();
        if (this.additions.containsKey(id)) {
            this.additions.remove(id);
        }
        this.additions.put(id, entry);
    }

    public void removeEntry(BackendEntry entry) {
        logger.debug("Transaction remove entry {}", entry);
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(entry.id());

        Id id = entry.id();
        if (this.deletions.containsKey(id)) {
            this.deletions.remove(id);
        }
        this.deletions.put(id, entry);
    }

    public void removeEntry(HugeTypes type, Id id) {
        this.removeEntry(this.serializer.writeId(type, id));
    }

}
