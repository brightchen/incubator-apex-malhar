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
package org.apache.apex.malhar.lib.state.managed;

import javax.validation.constraints.NotNull;

/**
 * Simple Time bucket assigner to assiger time bucket for any given time <br>
 * The algorithm is simple to just round the time to time bucket span. <br>
 * Ex. given time bucket span is 1000 milliseconds <br>
 * All times 1001, 1002 ... 1999 will be assigned to time bucket 1000 <br>
 *
 *
 */
public class UnBoundedTimeBucketAssigner extends TimeBucketAssigner
{
  @Override
  public long getTimeBucket(long time)
  {
    if (time < 0) {
      throw new IllegalArgumentException("Time: " + time + " is illegal");
    }
    return time - time % getBucketSpan().getMillis();
  }

  @Override
  public long getNumBuckets()
  {
    return Long.MAX_VALUE;
  }

  @Override
  public void setup(@NotNull ManagedStateContext managedStateContext)
  {
    super.setup(managedStateContext);
    setInitialized(true);
  }

  @Override
  public void teardown()
  {

  }

  @Override
  public void beginWindow(long windowId)
  {

  }

  @Override
  public void endWindow()
  {

  }
}
