package ru.ievgrafov.btree;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class BTreeSet<E> implements Set<E> {
  // Simple struct to contain one node
  public static class Node {
    public Object[] values;
    public Node[] children;
    public int valuesCount;
    public int childrenCount;
  
    public Node(Object[] values, Node[] children, int valuesCount, int childrenCount) {
      this.values = values;
      this.children = children;
      this.valuesCount = valuesCount;
      this.childrenCount = childrenCount;
    }
  }

  private int size;
  private int valuesMaxSize;
  private int childrenMaxSize;
  private Node root;
  private Comparator<E> comparator;

  public BTreeSet(Comparator<E> comparator) {
    this(2, comparator);
  }

  public BTreeSet(int factor, Comparator<E> comparator) {
    this.size = 0;
    this.comparator = comparator;
    this.valuesMaxSize = factor * 2;
    this.childrenMaxSize = factor * 2 + 1;
    this.root = new Node(
      new Object[valuesMaxSize],
      new Node[childrenMaxSize],
      0,
      0
    );
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public boolean add(E value) {
    return add(value, root, null);
  }

  public boolean contains(Object value) {
    return contains(value, root);
  }

  public boolean remove(Object value) {
    return remove(value, root);
  }

  // Implementation

  @SuppressWarnings("unchecked")
  private boolean add(E value, Node node, Node parent) {
    if (node.valuesCount >= valuesMaxSize) {
      node = splitNode(node, parent, value);
    }

    int newValuePosition = findPositionForValueInNode(value, node);

    if (newValuePosition < node.valuesCount && compare(value, (E)node.values[newValuePosition]) == 0) {
      // Value already present, return false on attempt to duplicate it
      return false;
    } else if (node.childrenCount == 0) {
      // A leaf node, we should put value here
      insertValueInArray((Object[])node.values, node.valuesCount, value, newValuePosition);
      this.size++;
      node.valuesCount++;

      return true;
    } else {
      // Not a leaf node, we should go further down the tree
      return add(value, node.children[newValuePosition], node);
    }
  }

  @SuppressWarnings("unchecked")
  private Node splitNode(Node sourceNode, Node parent, E value) {
    int middleIndex = valuesMaxSize / 2;
    E middleValue = (E)sourceNode.values[middleIndex];
    Node leftNode = new Node(
      copyOfRangeWithSize(sourceNode.values, 0, middleIndex - 1, valuesMaxSize),
      copyOfRangeWithSize(sourceNode.children, 0, middleIndex, childrenMaxSize),
      middleIndex,
      sourceNode.childrenCount > 0 ? middleIndex + 1 : 0
    );
    Node rightNode = new Node(
      copyOfRangeWithSize(sourceNode.values, middleIndex + 1, valuesMaxSize - 1, valuesMaxSize),
      copyOfRangeWithSize(sourceNode.children, middleIndex + 1, childrenMaxSize - 1, childrenMaxSize),
      valuesMaxSize - middleIndex - 1,
      sourceNode.childrenCount > 0 ? childrenMaxSize - middleIndex - 1 : 0
    );

    if (parent == null) {
      // it means that given node is root and it is full, we should split it and have new root in place
      sourceNode.values = (E[]) new Object[valuesMaxSize];
      sourceNode.children = new Node[childrenMaxSize];
      sourceNode.values[0] = middleValue;
      sourceNode.valuesCount = 1;
      sourceNode.children[0] = leftNode;
      sourceNode.children[1] = rightNode;
      sourceNode.childrenCount = 2;

      return sourceNode;
    } else {
      pushValueWithChildrenToNode(parent, middleValue, leftNode, rightNode);

      return parent;
    }
  }

  private void pushValueWithChildrenToNode(Node parent, E value, Node leftNode, Node rightNode) {
    // We can be sure that there's at least one empty spot in the array at this moment
    // otherwise we would already have splitten the node on the way down to it
    int newValuePosition = findPositionForValueInNode(value, parent);

    insertValueInArray((Object[])parent.values, parent.valuesCount, value, newValuePosition);
    parent.valuesCount++;
    insertValueInArray((Object[])parent.children, parent.childrenCount, leftNode, newValuePosition);
    parent.children[newValuePosition + 1] = rightNode;
    parent.childrenCount++;
  }

  // Find position for a new value in a node
  @SuppressWarnings("unchecked")
  private int findPositionForValueInNode(E value, Node node) {
      int upperBound = node.valuesCount - 1;
      int lowerBound = 0;
      int currentPosition;
    
      while (upperBound >= lowerBound) {
        currentPosition = (upperBound + lowerBound) / 2;
        int compareResult = compare((E)node.values[currentPosition], value);
    
        if (compareResult == 0) {
          // Found it!
          return currentPosition;
        } else if (compareResult > 0) {
          upperBound = currentPosition - 1;
        } else {
          lowerBound = currentPosition + 1;
        }
      }
    
      return lowerBound;
  }

  private void insertValueInArray(Object[] arr, int currentLength, Object value, int position) {
    // We can be sure that there's at least one empty spot in the array at this moment
    // otherwise we would already have splitten the node on the way down to it
    int i = currentLength;

    // TODO: check if it could be faster with System.arraycopy
    while (i > position) {
      arr[i] = arr[i - 1];

      i--;
    }

    arr[position] = value;
  }

  // Instanciates a new array of given size and copies there a range from given source
  private Object[] copyOfRangeWithSize(Object[] originalArray, int from, int to, int newSize) {
    Object[] result = new Object[newSize];

    System.arraycopy(originalArray, from, result, 0, to - from + 1);

    return result;
  }

  // Instanciates a new array of given size and copies there a range from given source
  private Node[] copyOfRangeWithSize(Node[] originalArray, int from, int to, int newSize) {
    Node[] result = new Node[newSize];

    System.arraycopy(originalArray, from, result, 0, to - from + 1);

    return result;
  }

  private int compare(E left, E right) {
    return comparator.compare(left, right);
  }

  @SuppressWarnings("unchecked")
  private boolean contains(Object value, Node node) {
    int position = findPositionForValueInNode((E)value, node);

    // Found you!
    if (position < node.valuesCount && compare((E)value, (E)node.values[position]) == 0) return true;

    // We are at the leaf and didn't find element yet. It means it's not in the tree
    if (node.childrenCount == 0) return false;

    return contains(value, node.children[position]);
  }

  @SuppressWarnings("unchecked")
  private boolean remove(Object value, Node node) {
    int position = findPositionForValueInNode((E)value, node);

    if (position < node.valuesCount && compare((E)value, (E)node.values[position]) == 0) {
      // found element, should remove it now
      removeValueFromNodeByIndex(node, position);
      this.size--;

      return true;
    }

    // We are at the leaf and didn't find element yet. It means it's not in the tree
    if (node.childrenCount == 0) {
      return false;
    }

    return remove(value, node.children[position]);
  }

  private void removeValueFromNodeByIndex(Node node, int index) {
    if (node.childrenCount == 0) {
      // If no children, simply drop the value by copying array without it
      System.arraycopy(node.values, index + 1, node.values, index, node.valuesCount - index - 1);
      node.valuesCount--;

      return;
    }

    // Need to find replacement for value in children
    Node replacementNode = node.children[index];

    // Corresponding child is already empty -> we can simply drop current value and child altogether
    if (replacementNode.valuesCount == 0) {
      System.arraycopy(node.values, index + 1, node.values, index, node.valuesCount - index - 1);
      node.valuesCount--;
      System.arraycopy(node.children, index + 1, node.children, index, node.childrenCount - index - 1);
      node.childrenCount--;

      return;
    }

    while (replacementNode.childrenCount > 0) {
      Node nextReplacementNode = replacementNode.children[replacementNode.childrenCount - 1];

      if (nextReplacementNode.valuesCount == 0) break;

      replacementNode = nextReplacementNode;
    }

    node.values[index] = replacementNode.values[replacementNode.valuesCount - 1];
    replacementNode.valuesCount--;

    if (replacementNode.childrenCount != 0) {
      // it's not a leaf but rightmost child is empty which means we should drop it (last value has already been taken as replacement)
      replacementNode.childrenCount--;
    }
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'addAll'");
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'clear'");
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'containsAll'");
  }

  @Override
  public Iterator<E> iterator() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'iterator'");
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'removeAll'");
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'retainAll'");
  }

  @Override
  public Object[] toArray() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'toArray'");
  }

  @Override
  public <T> T[] toArray(T[] arg0) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'toArray'");
  }
}
