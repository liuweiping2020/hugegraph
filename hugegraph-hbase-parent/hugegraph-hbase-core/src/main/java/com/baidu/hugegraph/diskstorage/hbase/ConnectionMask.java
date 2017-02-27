// Copyright 2017 hugegraph Authors
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

/**
 * Copyright DataStax, Inc.
 * <p>
 * Please see the included license file for details.
 */
package com.baidu.hugegraph.diskstorage.hbase;

import java.io.Closeable;
import java.io.IOException;

/**
 * This interface hides ABI/API breaking changes that HBase has made to its (H)Connection class over the course
 * of development from 0.94 to 1.0 and beyond.
 */
public interface ConnectionMask extends Closeable
{

    TableMask getTable(String name) throws IOException;

    AdminMask getAdmin() throws IOException;
}
