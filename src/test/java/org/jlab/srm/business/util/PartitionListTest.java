package org.jlab.srm.business.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class PartitionListTest {

  @Test
  void splitsIntoFullPartitionsAndRemainder() {
    PartitionList<Integer> partitions = new PartitionList<>(Arrays.asList(1, 2, 3, 4, 5), 2);

    assertEquals(3, partitions.size());
    assertEquals(Arrays.asList(1, 2), partitions.get(0));
    assertEquals(Arrays.asList(3, 4), partitions.get(1));
    assertEquals(List.of(5), partitions.get(2));
  }

  @Test
  void exactMultipleHasNoPartialPartition() {
    PartitionList<Integer> partitions = new PartitionList<>(Arrays.asList(1, 2, 3, 4), 2);

    assertEquals(2, partitions.size());
    assertEquals(Arrays.asList(3, 4), partitions.get(1));
  }

  @Test
  void partitionLargerThanListGivesOnePartition() {
    PartitionList<Integer> partitions = new PartitionList<>(Arrays.asList(1, 2, 3), 1000);

    assertEquals(1, partitions.size());
    assertEquals(Arrays.asList(1, 2, 3), partitions.get(0));
  }

  @Test
  void emptyListHasNoPartitions() {
    PartitionList<Integer> partitions = new PartitionList<>(Collections.emptyList(), 3);

    assertTrue(partitions.isEmpty());
    assertEquals(0, partitions.size());
    assertThrows(IndexOutOfBoundsException.class, () -> partitions.get(0));
  }

  @Test
  void rejectsIndexOutOfRange() {
    PartitionList<Integer> partitions = new PartitionList<>(Arrays.asList(1, 2, 3), 2);

    assertThrows(IndexOutOfBoundsException.class, () -> partitions.get(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> partitions.get(2));
  }

  @Test
  void rejectsInvalidArguments() {
    assertThrows(NullPointerException.class, () -> new PartitionList<Integer>(null, 2));
    assertThrows(IllegalArgumentException.class, () -> new PartitionList<>(List.of(1), 0));
    assertThrows(IllegalArgumentException.class, () -> new PartitionList<>(List.of(1), -1));
  }
}
