package org.jlab.srm.persistence.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

class TreeNodeTest {

  @Test
  void newNodeIsRootAndLeaf() {
    TreeNode<String> node = new TreeNode<>("a");

    assertTrue(node.isRoot());
    assertTrue(node.isLeaf());
    assertNull(node.getParent());
    assertEquals("a", node.getData());
  }

  @Test
  void appendChildLinksParentAndKeepsOrder() {
    TreeNode<String> root = new TreeNode<>("root");
    TreeNode<String> b = new TreeNode<>("b");
    TreeNode<String> a = new TreeNode<>("a");

    root.appendChild(b);
    root.appendChild(a);

    assertFalse(root.isLeaf());
    assertFalse(b.isRoot());
    assertSame(root, b.getParent());
    assertEquals(Arrays.asList(b, a), root.getChildren());

    List<TreeNode<String>> iterated = new ArrayList<>();
    root.forEach(iterated::add);
    assertEquals(Arrays.asList(b, a), iterated);
  }

  @Test
  void childWithParentCannotBeAppendedAgain() {
    TreeNode<String> first = new TreeNode<>("first");
    TreeNode<String> second = new TreeNode<>("second");
    TreeNode<String> child = new TreeNode<>("child");
    first.appendChild(child);

    assertThrows(IllegalStateException.class, () -> second.appendChild(child));
    assertSame(first, child.getParent());
  }

  @Test
  void removeChildUnlinksParent() {
    TreeNode<String> root = new TreeNode<>("root");
    TreeNode<String> child = new TreeNode<>("child");
    root.appendChild(child);

    root.removeChild(child);

    assertTrue(root.isLeaf());
    assertTrue(child.isRoot());
  }

  @Test
  void removingSomeoneElsesChildLeavesItAlone() {
    TreeNode<String> parent = new TreeNode<>("parent");
    TreeNode<String> other = new TreeNode<>("other");
    TreeNode<String> child = new TreeNode<>("child");
    parent.appendChild(child);

    other.removeChild(child);

    assertSame(parent, child.getParent());
  }

  @Test
  void childrenCannotBeModifiedFromOutside() {
    TreeNode<String> root = new TreeNode<>("root");
    root.appendChild(new TreeNode<>("child"));

    assertThrows(
        UnsupportedOperationException.class, () -> root.getChildren().add(new TreeNode<>("x")));

    Iterator<TreeNode<String>> iterator = root.iterator();
    iterator.next();
    assertThrows(UnsupportedOperationException.class, iterator::remove);
  }
}
