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
package org.apache.apex.malhar.lib.window.accumulation;

public class MultiAccumulationLong extends MultiAccumulation<Long, Long>
{
  public static class AccumulationValuesLong extends AbstractAccumulationValues<Long, Long>
  {
    @Override
    protected void accumulateValue(AccumulationType type, Long value)
    {
      Long oldValue = accumulationTypeToValue.get(type);
      switch(type) {
        case MAX:
          if (oldValue < value) {
            accumulationTypeToValue.put(type, value);
          }
          break;
        case MIN:
          if (oldValue > value) {
            accumulationTypeToValue.put(type, value);
          }
          break;
        case SUM:
          accumulationTypeToValue.put(type, oldValue + value);
          break;
      }
    }

    @Override
    protected void mergeValue(AccumulationType type, Long otherValue)
    {
      accumulateValue(type, otherValue);
    }

    @Override
    protected double doubleValue(Long value)
    {
      return value;
    }
  }

  public MultiAccumulationLong(boolean includeCount, boolean includeAverage, AccumulationType ... accumulationTypes)
  {
    defaultAccumulationValues = new AccumulationValuesLong();
    setAccumulateTypes(includeCount, includeAverage, accumulationTypes);
  }

  public MultiAccumulationLong()
  {
    this(true, true, AccumulationType.values());
  }
}
