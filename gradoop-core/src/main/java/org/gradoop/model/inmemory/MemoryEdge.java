package org.gradoop.model.inmemory;

import org.gradoop.GConstants;
import org.gradoop.model.Edge;

import java.util.Map;

/**
 * Transient representation of an edge.
 */
public class MemoryEdge extends SingleLabeledPropertyContainer implements Edge {
  private final Long otherID;

  private final Long index;

  public MemoryEdge(final Long otherID, final Long index) {
    this(otherID, GConstants.DEFAULT_EDGE_LABEL, index, null);
  }

  public MemoryEdge(final Long otherID, final String label, final Long index) {
    this(otherID, label, index, null);
  }

  public MemoryEdge(final Long otherID, final String label, final Long index,
                    final Map<String, Object> properties) {
    super(label, properties);
    checkID(otherID);
    checkIndex(index);
    this.otherID = otherID;
    this.index = index;
  }

  private void checkID(Long otherID) {
    if (otherID == null) {
      throw new IllegalArgumentException("otherID must not be null");
    }
  }

  private void checkIndex(Long index) {
    if (index == null) {
      throw new IllegalArgumentException("index must not be null");
    }
  }

  @Override
  public Long getOtherID() {
    return this.otherID;
  }

  @Override
  public Long getIndex() {
    return this.index;
  }

  /**
   * Equality of edges is only valid in the context of a single vertex. Two
   * edges are equal if they have the same otherID, label and index.
   *
   * @param o edge to check equality to
   * @return true if the edge is equal to the given object, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MemoryEdge that = (MemoryEdge) o;

    if (!index.equals(that.index)) {
      return false;
    }
    if (!otherID.equals(that.otherID)) {
      return false;
    }
    if (!getLabel().equals((that.getLabel()))) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = otherID.hashCode();
    result = 31 * result + index.hashCode();
    return result;
  }

  /**
   * Edges are ordered by otherID, label and index.
   *
   * @param o the edge to compare that edge to
   * @return -1, 0, 1 if that object is smaller, equal or greater than {@code o}
   */
  @Override
  public int compareTo(Edge o) {
    int result;
    int otherIDCompare = otherID.compareTo(o.getOtherID());
    if (otherIDCompare == 0) {
      int labelCompare = getLabel().compareTo(o.getLabel());
      if (labelCompare == 0) {
        result = index.compareTo(o.getIndex());
      } else {
        result = labelCompare;
      }
    } else {
      result = otherIDCompare;
    }
    return result;
  }
}
