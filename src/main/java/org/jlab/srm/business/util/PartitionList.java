package org.jlab.srm.business.util;

import java.util.AbstractList;
import java.util.List;

/**
 * @author ryans
 */
public class PartitionList<T> extends AbstractList<List<T>> {
  final List<T> list;
  final int partitionSize;

  public PartitionList(List<T> list, int partitionSize) {
    if (list == null) {
      throw new NullPointerException();
    }

    if (partitionSize < 1) {
      throw new IllegalArgumentException("partition size must be a positive number");
    }

    this.list = list;
    this.partitionSize = partitionSize;
  }

  @Override
  public List<T> get(int index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException("index must not be negative");
    }
    if (index >= size()) {
      throw new IndexOutOfBoundsException("index must be less than size");
    }
    int start = index * partitionSize;
    int end = Math.min(start + partitionSize, list.size());
    return list.subList(start, end);
  }

  @Override
  public int size() {
    return (list.size() + partitionSize - 1) / partitionSize;
  }

  @Override
  public boolean isEmpty() {
    return list.isEmpty();
  }
}
