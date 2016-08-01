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
package org.apache.apex.malhar.lib.state.spillable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.validation.constraints.NotNull;

import org.apache.apex.malhar.lib.utils.serde.Serde;
import org.apache.apex.malhar.lib.utils.serde.SerdeIntSlice;
import org.apache.apex.malhar.lib.utils.serde.SerdeListSlice;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.datatorrent.api.Context;
import com.datatorrent.netlet.util.Slice;

@DefaultSerializer(FieldSerializer.class)
public class SpillableArrayListImpl<T> implements Spillable.SpillableArrayList<T>, Spillable.SpillableComponent
{
  public static final int DEFAULT_BATCH_SIZE = 1000;

  private int batchSize = DEFAULT_BATCH_SIZE;
  private long bucketId;
  private byte[] prefix;

  @NotNull
  private SpillableStateStore store;
  @NotNull
  private Serde<T, Slice> serde;
  @NotNull
  private SpillableByteMapImpl<Integer, List<T>> map;

  private boolean sizeCached = false;
  private int size;
  private int numBatches;

  private transient boolean isRunning = false;
  private transient boolean isInWindow = false;

  private SpillableArrayListImpl()
  {
    //for kryo
  }

  public SpillableStateStore getStore()
  {
    return store;
  }

  public SpillableArrayListImpl(long bucketId, @NotNull byte[] prefix,
      @NotNull SpillableStateStore store,
      @NotNull Serde<T, Slice> serde)
  {
    this.bucketId = bucketId;
    this.prefix = Preconditions.checkNotNull(prefix);
    this.store = Preconditions.checkNotNull(store);
    this.serde = Preconditions.checkNotNull(serde);

    map = new SpillableByteMapImpl<>(store, prefix, bucketId, new SerdeIntSlice(), new SerdeListSlice(serde));
  }

  public SpillableArrayListImpl(long bucketId, @NotNull byte[] prefix,
      @NotNull SpillableStateStore store,
      @NotNull Serde<T, Slice> serde,
      int batchSize)
  {
    this(bucketId, prefix, store, serde);

    Preconditions.checkArgument(this.batchSize > 0);
    this.batchSize = batchSize;
  }

  public void setSize(int size)
  {
    Preconditions.checkArgument(size >= 0);
    this.size = size;
  }

  @Override
  public int size()
  {
    return size;
  }

  @Override
  public boolean isEmpty()
  {
    return size == 0;
  }

  @Override
  public boolean contains(Object o)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<T> iterator()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object[] toArray()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T1> T1[] toArray(T1[] t1s)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(T t)
  {
    Preconditions.checkArgument((size() + 1) > 0);

    int batchIndex = (size / batchSize);

    List<T> batch = null;

    if (batchIndex == numBatches) {
      batch = Lists.newArrayListWithCapacity(batchSize);
      numBatches++;
    } else {
      batch = map.get(batchIndex);
    }

    batch.add(t);

    size++;
    map.put(batchIndex, batch);
    return true;
  }

  @Override
  public boolean remove(Object o)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(Collection<?> collection)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection<? extends T> collection)
  {
    for (T element: collection) {
      add(element);
    }

    return true;
  }

  @Override
  public boolean addAll(int i, Collection<? extends T> collection)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> collection)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> collection)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public T get(int i)
  {
    if (!(i < size)) {
      throw new IndexOutOfBoundsException();
    }

    int batchIndex = i / batchSize;
    int batchOffset = i % batchSize;

    List<T> batch = map.get(batchIndex);
    return batch.get(batchOffset);
  }

  @Override
  public T set(int i, T t)
  {
    if (!(i < size)) {
      throw new IndexOutOfBoundsException();
    }

    int batchIndex = i / batchSize;
    int batchOffset = i % batchSize;

    List<T> batch = map.get(batchIndex);
    T old = batch.get(batchOffset);
    batch.set(batchOffset, t);
    map.put(batchIndex, batch);
    return old;
  }

  @Override
  public void add(int i, T t)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public T remove(int i)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int indexOf(Object o)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int lastIndexOf(Object o)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public ListIterator<T> listIterator()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public ListIterator<T> listIterator(int i)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<T> subList(int i, int i1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setup(Context.OperatorContext context)
  {
    map.setup(context);
    isRunning = true;
  }

  @Override
  public void beginWindow(long windowId)
  {
    map.beginWindow(windowId);
    isInWindow = true;
  }

  @Override
  public void endWindow()
  {
    isInWindow = false;
    map.endWindow();
  }

  @Override
  public void teardown()
  {
    isRunning = false;
    map.teardown();
  }

  @Override
  public boolean isRunning()
  {
    return isRunning;
  }

  @Override
  public boolean isInWindow()
  {
    return isInWindow;
  }
}