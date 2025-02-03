package ru.ievgrafov.btree;

import java.util.Comparator;

public class BTreeSet<E extends Object> {
  // Simple struct to contain one node

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
    return addValueToNode(value, root, null);
  }

  public boolean contains(E value) {
    return recursiveContains(value, root);
  }

  // public boolean remove(E value);
  // public E[] toArray();

  // Implementation

  @SuppressWarnings("unchecked")
  private boolean addValueToNode(E value, Node node, Node parent) {
    if (isNodeFull(node)) {
      node = splitNode(node, parent, value);
    }

    int newValuePosition = findPositionForNewValueInNode(value, node);

    if (newValuePosition < node.valuesCount && compare(value, (E)node.values[newValuePosition]) == 0) {
    // value already present, return false on attempt to duplicate it
      return false;
    } else if (node.childrenCount == 0) {
      // a leaf node, we should put value here
      insertValueInArray((Object[])node.values, node.valuesCount, value, newValuePosition);
      this.size += 1;
      node.valuesCount += 1;

      return true;
    } else {
      // not a leaf node, we should go further down the tree
      return addValueToNode(value, node.children[newValuePosition], node);
    }
  }

  private boolean isNodeFull(Node node) {
    return node.valuesCount >= valuesMaxSize;
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
    int newValuePosition = findPositionForNewValueInNode(value, parent);

    insertValueInArray((Object[])parent.values, parent.valuesCount, value, newValuePosition);
    parent.valuesCount += 1;
    insertValueInArray((Object[])parent.children, parent.childrenCount, leftNode, newValuePosition);
    parent.children[newValuePosition + 1] = rightNode;
    parent.childrenCount += 1;
  }

  // Find position for a new value in a node
  @SuppressWarnings("unchecked")
  private int findPositionForNewValueInNode(E value, Node node) {
    int newValuePosition = node.valuesCount;

    if (newValuePosition == 0) return 0;

    while (newValuePosition > 0) {
      int compareResult =  compare((E)node.values[newValuePosition - 1], value);

      // We found element itself, should return its position
      if (compareResult == 0) return newValuePosition - 1;

      // Left element is smaller than current one - should insert current one on current place
      if (compareResult < 0) return newValuePosition;

      newValuePosition -= 1;
    }

    return newValuePosition;
  }

  private void insertValueInArray(Object[] arr, int currentLength, Object value, int position) {
    // We can be sure that there's at least one empty spot in the array at this moment
    // otherwise we would already have splitten the node on the way down to it
    int i = currentLength;

    // TODO: check if it could be faster with System.arraycopy
    while (i > position) {
      arr[i] = arr[i - 1];

      i -= 1;
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
  private boolean recursiveContains(E value, Node node) {
    int position = findPositionForNewValueInNode(value, node);

    if (position < node.valuesCount && compare(value, (E)node.values[position]) == 0) return true;

    if (node.childrenCount == 0) return false;

    return recursiveContains(value, node.children[position]);
  }
}
