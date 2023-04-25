package org.jlab.hco.persistence.model;

import java.util.*;

/**
 * @author ryans
 */
public class TreeNode<T> implements Iterable<TreeNode<T>> {
    private T data;
    private final LinkedHashSet<TreeNode<T>> children = new LinkedHashSet<TreeNode<T>>();
    private TreeNode<T> parent;

    public TreeNode(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public void appendChild(TreeNode<T> child) {
        if (child.parent != null) {
            throw new IllegalStateException("node already has a parent");
        }
        children.add(child);
        child.parent = this;
    }

    public void removeChild(TreeNode<T> child) {
        if (children.remove(child)) {
            child.parent = null;
        }
    }

    public List<TreeNode<T>> getChildren() {
        return Collections.unmodifiableList(new ArrayList<TreeNode<T>>(children));
    }

    @Override
    public Iterator<TreeNode<T>> iterator() {
        return Collections.unmodifiableSet(children).iterator();
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean isRoot() {
        return parent == null;
    }
}
