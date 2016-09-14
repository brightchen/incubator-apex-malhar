/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.datatorrent.benchmark.state;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;

import com.datatorrent.api.DAG;
import com.datatorrent.api.LocalMode;
import com.datatorrent.api.StreamingApplication;
import com.datatorrent.benchmark.state.StoreOperator.ExeMode;

/**
 * This is not a really unit test, but in fact a benchmark runner.
 * Provides this class to give developers the convenience to run in local IDE environment.
 *
 */
public class ManagedStateBenchmarkAppTester extends ManagedStateBenchmarkApp
{
  public static final String basePath = "target/temp";
  
  @Before
  public void before()
  {
    FileUtil.fullyDelete(new File(basePath));
  }
  
  @Test
  public void testUpdateSync() throws Exception
  {
    test(ExeMode.UpdateSync);
  }
  
  @Test
  public void testUpdateAsync() throws Exception
  {
    test(ExeMode.UpdateAsync);
  }
  
  @Test
  public void testInsert() throws Exception
  {
    test(ExeMode.Insert);
  }
  
  public void test(ExeMode exeMode) throws Exception
  {
    Configuration conf = new Configuration(false);

    LocalMode lma = LocalMode.newInstance();
    DAG dag = lma.getDAG();

    super.populateDAG(dag, conf);
    store.exeMode = exeMode;
    
    StreamingApplication app = new StreamingApplication()
    {
      @Override
      public void populateDAG(DAG dag, Configuration conf)
      {
      }
    };

    lma.prepareDAG(app, conf);

    // Create local cluster
    final LocalMode.Controller lc = lma.getController();
    lc.run(300000);

    lc.shutdown();
  }


  
  @Override
  public String getStoreBasePath(Configuration conf)
  {
    return basePath;
  }
}
