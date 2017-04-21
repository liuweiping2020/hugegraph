package com.baidu.hugegraph.backend.query;

import java.util.LinkedHashSet;
import java.util.Set;

import com.baidu.hugegraph.backend.id.Id;
import com.baidu.hugegraph.type.HugeTypes;

public class IdQuery extends Query {

    // ids will be concated with `or`
    private Set<Id> ids;

    public IdQuery(HugeTypes resultType) {
        super(resultType);
        this.ids = new LinkedHashSet<>();
    }

    public IdQuery(HugeTypes resultType, Id id) {
        this(resultType);
        this.query(id);
    }

    @Override
    public Set<Id> ids() {
        return this.ids;
    }

    public void resetIds() {
        this.ids = new LinkedHashSet<>();
    }

    public IdQuery query(Id id) {
        this.ids.add(id);
        return this;
    }

    @Override
    public String toString() {
        return String.format("%s where id in %s",
                super.toString(),
                this.ids.toString());
    }
}
