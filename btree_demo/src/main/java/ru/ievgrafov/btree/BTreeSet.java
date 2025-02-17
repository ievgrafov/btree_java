package ru.ievgrafov.btree;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BTreeSet<E> implements Set<E> {
  // Simple struct to represent one node
  private static class Node {
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

    public boolean hasChildren() {
      return childrenCount > 0;
    }
  }

  // Iterator over BTree
  private static class BTreeIterator<E> implements Iterator<E> {
    private static class IteratorPosition {
      public Node node;
      public int keysInNodeToGoThrough;

      public IteratorPosition(Node node, int keysInNodeToGoThrough) {
        this.node = node;
        this.keysInNodeToGoThrough = keysInNodeToGoThrough;
      }

      public IteratorPosition clone() {
        return new IteratorPosition(node, keysInNodeToGoThrough);
      }
    }

    private BTreeSet<E> btree;
    private Stack<IteratorPosition> nodesStack;
    private IteratorPosition previousResultPosition;
    private boolean started;


    public BTreeIterator(BTreeSet<E> btree) {
      this.btree = btree;
      this.nodesStack = new Stack<>();
      this.started = false;
    }

    @Override
    public boolean hasNext() {
      if (!started) start();

      return !nodesStack.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public E next() {
      if (!started) start();

      if (nodesStack.empty()) return null;

      IteratorPosition currentPosition = nodesStack.peek();
      int currentIndex = currentPosition.node.valuesCount - currentPosition.keysInNodeToGoThrough;
      this.previousResultPosition = currentPosition.clone();
      E result = (E)currentPosition.node.values[currentIndex];

      goToNextValue();

      return result;
    }

    @Override
    public void remove() {
      if (previousResultPosition != null) {
        int index = previousResultPosition.node.valuesCount - previousResultPosition.keysInNodeToGoThrough;
        btree.removeValueFromNodeByIndex(previousResultPosition.node, index);
      }
    }

    private void start() {
      this.started = true;

      this.nodesStack.push(new IteratorPosition(btree.root, btree.root.valuesCount + 1));
      goToNextValue();
    }

    private void goToNextValue() {
      IteratorPosition currentPosition = nodesStack.peek();
      currentPosition.keysInNodeToGoThrough--;

      // Go deeper in the tree while possible
      while (currentPosition.node.hasChildren() && currentPosition.keysInNodeToGoThrough >= 0) {
        int currentIndex = currentPosition.node.valuesCount - currentPosition.keysInNodeToGoThrough;
        Node nextNode = currentPosition.node.children[currentIndex];
        this.nodesStack.push(new IteratorPosition(nextNode, nextNode.valuesCount));
        currentPosition = nodesStack.peek();
      }

      // Return to first not empty node if current is empty
      while (!this.nodesStack.empty() && nodesStack.peek().keysInNodeToGoThrough <= 0) {
        this.nodesStack.pop();
      }
    }
  }

  final private int valuesMaxSize;
  final private int childrenMaxSize;
  final private Comparator<E> comparator;
  private int size;
  private Node root;
  private int findValuePosition;
  private boolean findValueExactMatch;
  private boolean splitNodeExactMatch;

  public BTreeSet(Comparator<E> comparator) {
    this(2, comparator);
  }

  public BTreeSet(int factor, Comparator<E> comparator) {
    this.size = 0;
    this.comparator = comparator;
    this.valuesMaxSize = factor << 1;
    this.childrenMaxSize = this.valuesMaxSize + 1;
    this.root = new Node(
      new Object[valuesMaxSize],
      null,
      0,
      0
    );
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public boolean add(E value) {
    return add(value, root, null);
  }

  @Override
  public boolean contains(Object value) {
    return contains(value, root);
  }

  @Override
  public boolean remove(Object value) {
    return remove(value, root);
  }

  @Override
  public void clear() {
    this.size = 0;
    this.root = new Node(
      new Object[valuesMaxSize],
      null,
      0,
      0
    );
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    var iterator = c.iterator();

    while (iterator.hasNext()) {
      if (!contains(iterator.next())) return false;
    }

    return true;
  }

  @Override
  public Iterator<E> iterator() {
    return new BTreeIterator<>(this);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    var collectionIterator = c.iterator();
    boolean result = false;

    while (collectionIterator.hasNext()) {
      result = result || remove(collectionIterator.next());
    }

    return result;
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    var collectionIterator = c.iterator();
    boolean result = false;

    while (collectionIterator.hasNext()) {
      result = result || add(collectionIterator.next());
    }

    return result;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    Iterator<E> it = iterator();
    boolean result = false;

    while (it.hasNext()) {
      if (!c.contains(it.next())) {
        it.remove();
        result = true;
      }
    }

    return result;
  }

  @Override
  public Object[] toArray() {
    return toArray(new Object[0]);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] toArray(T[] arg0) {
    T[] result = arg0.length >= size ? arg0 : (T[])new Object[size];
    Iterator<E> iterator = iterator();
    int index = 0;

    while (iterator.hasNext()) {
      result[index] = (T)iterator.next();
      index++;
    }

    if (result.length > size) result[size] = null;

    return result;
  }

  public void reset() {
    BTreeSet<E> newSet = new BTreeSet<E>(valuesMaxSize >> 1, comparator);
    Iterator<E> iterator = iterator();
    iterator.forEachRemaining((k) -> newSet.add(k));

    this.root = newSet.root;
    this.size = newSet.size;
  }

  // Implementation

  private boolean add(E value, Node node, Node parent) {
    for (;;) {
      if (node.valuesCount >= valuesMaxSize) {
        node = splitNode(node, parent, value);
        if (this.splitNodeExactMatch) {
          // Value already present, return false on attempt to duplicate it
          return false;
        }
      }

      findPositionForValueInNode(value, node);

      if (this.findValueExactMatch) {
        // Value already present, return false on attempt to duplicate it
        return false;
      } else if (!node.hasChildren()) {
        // A leaf node, we should put value here
        insertValueInArray((Object[])node.values, node.valuesCount, value, this.findValuePosition);
        this.size++;
        node.valuesCount++;
  
        return true;
      }

      // Not a leaf node, we should go further down the tree
      parent = node;
      node = node.children[this.findValuePosition];
    }
  }

  @SuppressWarnings("unchecked")
  private Node splitNode(Node sourceNode, Node parent, E value) {
    int middleIndex = valuesMaxSize >> 1;
    E middleValue = (E)sourceNode.values[middleIndex];
    Node leftNode = new Node(
      copyOfRangeWithSize(sourceNode.values, 0, middleIndex - 1, valuesMaxSize),
      sourceNode.hasChildren() ? copyOfRangeWithSize(sourceNode.children, 0, middleIndex, childrenMaxSize) : null,
      middleIndex,
      sourceNode.hasChildren() ? middleIndex + 1 : 0
    );
    Node rightNode = new Node(
      copyOfRangeWithSize(sourceNode.values, middleIndex + 1, valuesMaxSize - 1, valuesMaxSize),
      sourceNode.hasChildren() ? copyOfRangeWithSize(sourceNode.children, middleIndex + 1, childrenMaxSize - 1, childrenMaxSize) : null,
      valuesMaxSize - middleIndex - 1,
      sourceNode.hasChildren() ? childrenMaxSize - middleIndex - 1 : 0
    );
    int compareResult = comparator.compare(value, middleValue);

    if (parent == null) {
      // it means that given node is root and it is full, we should split it and have new root in place
      sourceNode.values = (E[]) new Object[valuesMaxSize];
      sourceNode.children = new Node[childrenMaxSize];
      sourceNode.values[0] = middleValue;
      sourceNode.valuesCount = 1;
      sourceNode.children[0] = leftNode;
      sourceNode.children[1] = rightNode;
      sourceNode.childrenCount = 2;
    } else {
      pushValueWithChildrenToNode(parent, middleValue, leftNode, rightNode);
    }

    if (compareResult == 0) {
      this.splitNodeExactMatch = true;
      return null;
    } else if (compareResult < 0) {
      this.splitNodeExactMatch = false;
      return leftNode;
    } else {
      this.splitNodeExactMatch = false;
      return rightNode;
    }
  }

  private void pushValueWithChildrenToNode(Node parent, E value, Node leftNode, Node rightNode) {
    // We can be sure that there's at least one empty spot in the array at this moment
    // otherwise we would already have splitten the node on the way down to it
    findPositionForValueInNode(value, parent);

    insertValueInArray((Object[])parent.values, parent.valuesCount, value, this.findValuePosition);
    insertValueInArray((Object[])parent.children, parent.childrenCount, leftNode, this.findValuePosition);
    parent.children[this.findValuePosition + 1] = rightNode;
    parent.valuesCount++;
    parent.childrenCount++;
  }

  // Find position for a new value in a node
  private void findPositionForValueInNode(E value, Node node) {
    if (node.valuesCount == 0) {
      // Empty node, should put first element at 0
      this.findValuePosition = 0;
      this.findValueExactMatch = false;
    }
    findPositionBinarySearch(value, node);
  }

  @SuppressWarnings("unchecked")
  private void findPositionBinarySearch(E value, Node node) {
    int upperBound = node.valuesCount - 1;
    int lowerBound = 0;
    int currentPosition;

    while (upperBound >= lowerBound) {
      if (upperBound - lowerBound < 6) {
        findPositionLinearSearch(value, node, upperBound, lowerBound);
        return;
      }
      currentPosition = (upperBound + lowerBound) >> 1;
      int compareResult = comparator.compare((E)node.values[currentPosition], value);
  
      if (compareResult == 0) {
        // Found it!
        this.findValuePosition = currentPosition;
        this.findValueExactMatch = true;
        return;
      } else if (compareResult > 0) {
        upperBound = currentPosition - 1;
      } else {
        lowerBound = currentPosition + 1;
      }
    }

    this.findValueExactMatch = false;
    this.findValuePosition = lowerBound;
  }

  @SuppressWarnings("unchecked")
  private void findPositionLinearSearch(E value, Node node, int start, int end) {
    for (int i = end; i >= start; i--) {
      int compareResult = comparator.compare((E)node.values[i], value);

      if (compareResult == 0) {
        // Found it!
        this.findValuePosition = i;
        this.findValueExactMatch = true;
        return;
      } else if (compareResult < 0) {
        this.findValuePosition = i + 1;
        this.findValueExactMatch = false;
        return;
      }
    }

    this.findValuePosition = start;
    this.findValueExactMatch = false;
  }

  private void insertValueInArray(Object[] arr, int currentLength, Object value, int position) {
    // We can be sure that there's at least one empty spot in the array at this moment
    // otherwise we would already have splitten the node on the way down to it
    System.arraycopy(arr, position, arr, position + 1, currentLength - position);
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

  @SuppressWarnings("unchecked")
  private boolean contains(Object value, Node node) {
    for(;;) {
      findPositionForValueInNode((E)value, node);

      // Found it!
      if (this.findValueExactMatch) return true;

      // We are at the leaf and didn't find element yet. It means it's not in the tree
      if (!node.hasChildren()) return false;

      node = node.children[this.findValuePosition];
    }
  }

  private boolean remove(Object value, Node node) {
    return remove(value, node, null, 0);
  }

  @SuppressWarnings("unchecked")
  private boolean remove(Object value, Node node, Node parent, int positionInParent) {
    findPositionForValueInNode((E)value, node);
    int position = this.findValuePosition;

    if (this.findValueExactMatch) {
      // found element, should remove it now
      removeValueFromNodeByIndex(node, position);

      return true;
    }

    // We are at the leaf and didn't find element yet. It means it's not in the tree
    if (!node.hasChildren()) return false;

    boolean result = remove(value, node.children[position], node, position);

    if (result) {
      // Try to merge with right or left neighbour after removing a key
      tryMergeNeighbours(node.children[position], node, position);

      // The tree may end up in a situation when its root has no keys and only one child
      // in such case we should compact it (place the only child in root's place)
      if (parent == null) compactNode(node);
    }

    return result;
  }

  // The tree may end up in a situation when its root has no keys and only one child
  // in such case we should compact it (place the only child in root's place)
  private void compactNode(Node node) {
    while (node.valuesCount == 0 && node.hasChildren()) {
      Node child = node.children[0];
      node.values = child.values;
      node.valuesCount = child.valuesCount;
      node.children = child.children;
      node.childrenCount = child.childrenCount;
    }
  }

  // Try to merge with right or left neighbour
  // returns result of the merge or node itself
  private Node tryMergeNeighbours(Node node, Node parent, int indexInParent) {
    if (indexInParent > 0) {
      Node leftNeighbour = parent.children[indexInParent - 1];
      int newNodeValuesCount = leftNeighbour.valuesCount + node.valuesCount + 1;

      if (newNodeValuesCount < valuesMaxSize) {
        mergeNodes(leftNeighbour, node, parent, indexInParent - 1);
        return leftNeighbour;
      }
    }

    if (indexInParent + 1 < parent.childrenCount) {
      Node rightNeighbour = parent.children[indexInParent + 1];
      int newNodeValuesCount = rightNeighbour.valuesCount + node.valuesCount + 1;

      if (newNodeValuesCount < valuesMaxSize) {
        mergeNodes(node, rightNeighbour, parent, indexInParent);
        return node;
      }
    }

    return node;
  }

  private void mergeNodes(Node left, Node right, Node parent, int leftPosition) {
    int rightPosition = leftPosition + 1;
    int nextAfterRightPosition = rightPosition + 1;

    // Put middle element back (opposite to split)
    left.values[left.valuesCount] = parent.values[leftPosition];
    left.valuesCount++;
  
    // Copy values from right node
    System.arraycopy(right.values, 0, left.values, left.valuesCount, right.valuesCount);
    left.valuesCount += right.valuesCount;

    if (left.hasChildren()) {
      System.arraycopy(right.children, 0, left.children, left.childrenCount, right.childrenCount);
      left.childrenCount += right.childrenCount;
    }

    // We perform merge not at the end of parent's children list
    // and therefore should copy over the elements after merged ones
    if (leftPosition < parent.valuesCount - 1) {
      System.arraycopy(parent.values, rightPosition, parent.values, leftPosition, parent.valuesCount - rightPosition);
      System.arraycopy(parent.children, nextAfterRightPosition, parent.children, rightPosition, parent.childrenCount - nextAfterRightPosition);
    }
    parent.valuesCount--;
    parent.childrenCount--;
  }

  private void removeValueFromNodeByIndex(Node node, int index) {
    this.size--;

    if (!node.hasChildren()) {
      // If no children, simply drop the value by copying array without it
      System.arraycopy(node.values, index + 1, node.values, index, node.valuesCount - index - 1);
      node.valuesCount--;

      return;
    }

    // Need to find replacement for value in child subtree
    Object replacement = extractMaxKeyFromSubtree(node.children[index]);

    if (replacement == null) {
      // Corresponding child is already empty so there's no replacement -> we should simply drop current value and child together
      System.arraycopy(node.values, index + 1, node.values, index, node.valuesCount - index - 1);
      System.arraycopy(node.children, index + 1, node.children, index, node.childrenCount - index - 1);
      node.valuesCount--;
      node.childrenCount--;
    } else {
      // There was a replacement in the leaf - should put it into place of removed key
      node.values[index] = replacement;
    }
  }

  private Object extractMaxKeyFromSubtree(Node subtree) {
    Object result = null;

    if (subtree.hasChildren()) {
      // Try to get max from right subtree first
      result = extractMaxKeyFromSubtree(subtree.children[subtree.childrenCount - 1]);
    }

    if (result == null && subtree.valuesCount > 0) {
      // If right subtree is already empty, max key is the rightmost one in current node
      subtree.valuesCount--;
      result = subtree.values[subtree.valuesCount];

      if (subtree.hasChildren()) {
        // Right subtree is empty and we're extracting max key from current node, can drop the child too
        subtree.childrenCount--;
      }
    }

    return result;
  }

  // Support functions not related to main functionality
  // only for dbugging purposes
  public Map<Integer,Integer> nodesCountByLevel() {
    Map<Integer, Integer> result = new HashMap<>();
    int currentLevel = 0;
    List<Node> currentLevelNodes = List.of(root);

    while (!currentLevelNodes.isEmpty()) {
      result.put(currentLevel, currentLevelNodes.size());
      currentLevelNodes =
        currentLevelNodes
          .stream()
          .filter((Node node) -> node.hasChildren())
          .flatMap((Node node) -> Stream.of(node.children).limit(node.childrenCount).filter((Node child) -> child != null))
          .collect(Collectors.toList());
      currentLevel++;
    }

    return result;
  }

  public Map<Integer,Integer> valuesCountByLevel() {
    Map<Integer, Integer> result = new HashMap<>();
    int currentLevel = 0;
    List<Node> currentLevelNodes = List.of(root);

    while (!currentLevelNodes.isEmpty()) {
      result.put(currentLevel, currentLevelNodes.stream().map((Node node) -> node.valuesCount).reduce((acc, value) -> acc + value).get());
      currentLevelNodes =
        currentLevelNodes
          .stream()
          .filter((Node node) -> node.hasChildren())
          .flatMap((Node node) -> Stream.of(node.children).limit(node.childrenCount).filter((Node child) -> child != null))
          .collect(Collectors.toList());
      currentLevel++;
    }

    return result;
  }
}
