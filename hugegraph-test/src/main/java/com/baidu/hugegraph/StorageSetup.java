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

package com.baidu.hugegraph;


import com.baidu.hugegraph.core.HugeGraphFactory;
import com.baidu.hugegraph.core.HugeGraph;
import com.baidu.hugegraph.diskstorage.configuration.BasicConfiguration;
import com.baidu.hugegraph.diskstorage.configuration.ModifiableConfiguration;
import com.baidu.hugegraph.diskstorage.configuration.ReadConfiguration;
import com.baidu.hugegraph.diskstorage.configuration.WriteConfiguration;

import static com.baidu.hugegraph.graphdb.configuration.GraphDatabaseConfiguration.*;
import com.baidu.hugegraph.util.system.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.time.Duration;

public class StorageSetup {

    //############ UTILITIES #############

    public static final String getHomeDir(String subdir) {
        String homedir = System.getProperty("hugegraph.testdir");
        if (null == homedir) {
            homedir = "target" + File.separator + "db";
        }
        if (subdir!=null && !StringUtils.isEmpty(subdir)) homedir += File.separator + subdir;
        File homefile = new File(homedir);
        if (!homefile.exists()) homefile.mkdirs();
        return homedir;
    }

    public static final File getHomeDirFile() {
        return getHomeDirFile(null);
    }

    public static final File getHomeDirFile(String subdir) {
        return new File(getHomeDir(subdir));
    }

    public static final void deleteHomeDir() {
        deleteHomeDir(null);
    }

    public static final void deleteHomeDir(String subdir) {
        File homeDirFile = getHomeDirFile(subdir);
        // Make directory if it doesn't exist
        if (!homeDirFile.exists())
            homeDirFile.mkdirs();
        boolean success = IOUtils.deleteFromDirectory(homeDirFile);
        if (!success) throw new IllegalStateException("Could not remove " + homeDirFile);
    }

    public static ModifiableConfiguration getInMemoryConfiguration() {
        return buildGraphConfiguration().set(STORAGE_BACKEND, "inmemory").set(IDAUTHORITY_WAIT, Duration.ZERO);
    }

    public static HugeGraph getInMemoryGraph() {
        return HugeGraphFactory.open(getInMemoryConfiguration());
    }

    public static WriteConfiguration addPermanentCache(ModifiableConfiguration conf) {
        conf.set(DB_CACHE, true);
        conf.set(DB_CACHE_TIME,0l);
        return conf.getConfiguration();
    }

    public static ModifiableConfiguration getConfig(WriteConfiguration config) {
        return new ModifiableConfiguration(ROOT_NS,config, BasicConfiguration.Restriction.NONE);
    }

    public static BasicConfiguration getConfig(ReadConfiguration config) {
        return new BasicConfiguration(ROOT_NS,config, BasicConfiguration.Restriction.NONE);
    }

}
