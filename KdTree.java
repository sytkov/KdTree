/**
 * k-dimensional tree to store locations so we can display only
 * the geographic points in the inspection area
 */
public class KdTree {
  /**
   * Top node of the tree
   */
  private Node root = null;
  /**
   * Number of nodes in the tree
   */
  private int size = 0;

  /**
   * Get node with point closest to the given point by traversing tree
   *
   * @param p    given point
   * @param node top node of the search tree
   * @return Node with point closest to the given point
   */
  private Node getClosestNodeOrNull(Point2D p, Node node) {
    if (node != null) {
      if (node.point.equals(p))
        return node;

      if (node.checkX) {
        if (p.x() >= node.point.x()) {
          if (node.rt != null)
            return getClosestNodeOrNull(p, node.rt);
        }
        else
          if (node.lb != null)
            return getClosestNodeOrNull(p, node.lb);
      }
      else {
        if (p.y() >= node.point.y()) {
          if (node.rt != null)
            return getClosestNodeOrNull(p, node.rt);
        }
        else
          if (node.lb != null)
            return getClosestNodeOrNull(p, node.lb);
      }
    }
    return node;
  }

  /**
   * Add the point to the set
   *
   * @param p point
   */
  public void insert(Point2D p) {

    if (isEmpty()) {
      root = new Node(p, new RectHV(0, 0, 1, 1), true);
      size++;
      return;
    }
    Node node = getClosestNodeOrNull(p, root);

    if (p.equals(node.point)) return;

    if (node.checkX) {
      if (p.x() >= node.point.x()) {
        node.rt = new Node(p,
                           new RectHV(node.point.x(),
                                      node.rect.ymin(),
                                      node.rect.xmax(),
                                      node.rect.ymax()),
                           !node.checkX);
      }
      else {
        node.lb = new Node(p,
                           new RectHV(node.rect.xmin(),
                                      node.rect.ymin(),
                                      node.point.x(),
                                      node.rect.ymax()),
                           !node.checkX);
      }
    }
    else {
      if (p.y() >= node.point.y()) {
        node.rt = new Node(p,
                           new RectHV(node.rect.xmin(),
                                      node.point.y(),
                                      node.rect.xmax(),
                                      node.rect.ymax()),
                           !node.checkX);
      }
      else {
        node.lb = new Node(p,
                           new RectHV(node.rect.xmin(),
                                      node.rect.ymin(),
                                      node.rect.xmax(),
                                      node.point.y()),
                           !node.checkX);
      }
    }
    size++;
  }

  /**
   * Does set contain the given point?
   *
   * @param point 2D-point
   * @return true if it contains
   */
  public boolean contains(Point2D point) {
    Node closestNode = getClosestNodeOrNull(point, root);
    return null != closestNode && closestNode.point.equals(point);
  }

  /**
   * Get point nearest to the given point
   *
   * @param point 2D-point
   * @return Nearest point
   */
  public Point2D nearest(Point2D point) {
    if (isEmpty()) return null;
    Node closest = getClosestNodeOrNull(point, root);
    Node current;
    // search tree to find guaranteed closest point
    Queue<Node> toSearch = new Queue<Node>();
    toSearch.enqueue(root);
    double leastDistance = point.distanceSquaredTo(closest.point);
    while (!toSearch.isEmpty()) {
      current = toSearch.dequeue();
      double currentDistance = point.distanceSquaredTo(current.point);
      if (currentDistance < leastDistance) {
        closest = current;
        leastDistance = currentDistance;
      }
      if (current.rt != null
          && current.rt.rect.distanceSquaredTo(point) < leastDistance)
        toSearch.enqueue(current.rt);
      if (current.lb != null
          && current.lb.rect.distanceSquaredTo(point) < leastDistance)
        toSearch.enqueue(current.lb);
    }
    return closest.point;
  }

  /**
   * Get sequence of points located in the given rectangle
   *
   * @param rect 2D-rectangle
   * @return sequence of points within given rectangle
   */
  public Iterable<Point2D> range(RectHV rect) {
    Queue<Point2D> inRange = new Queue<Point2D>();
    if (size() == 0) return inRange;
    Queue<Node> toSearch = new Queue<Node>();
    toSearch.enqueue(root);
    while (!toSearch.isEmpty()) {
      Node current = toSearch.dequeue();
      if (rect.contains(current.point))
        inRange.enqueue(current.point);
      if (current.rt != null && rect.intersects(current.rt.rect))
        toSearch.enqueue(current.rt);
      if (current.lb != null && rect.intersects(current.lb.rect))
        toSearch.enqueue(current.lb);
    }
    return inRange;
  }

  /**
   * Is this set empty?
   *
   * @return true if empty
   */
  public boolean isEmpty() {
    return size() == 0;
  }

  /**
   * Get size of the tree
   *
   * @return number of points in the set
   */
  public int size() {
    return size;
  }

  /**
   * Draw all of the points to standard draw
   */
  public void draw() {
    for (Point2D point : range(new RectHV(0, 0, 1, 1)))
      point.draw();
  }

  private static class Node {
    /**
     * Point
     */
    private Point2D point;
    /**
     * Axis-aligned rectangle corresponding to node
     */
    private RectHV rect;
    /**
     * Left/bottom subtree
     */
    private Node lb;
    /**
     * Right/top subtree
     */
    private Node rt;
    /**
     * Coordinate to comparison. X or Y?
     */
    private boolean checkX;

    public Node(Point2D point, RectHV rect, boolean checkX) {
      this.point = point;
      this.rect = rect;
      this.lb = null;
      this.rt = null;
      this.checkX = checkX;
    }
  }
}
