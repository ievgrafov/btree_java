package ru.ievgrafov.btree;

public class Node {
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
