/* k-Dimensional Tree to store locations so we can display only the geographic points in the inspection area */

public class KdTree 
{

    private Node root;   //node at top of tree
    private int size;    //number of nodes in tree

    public KdTree()                               // construct an empty set of points
    {
        root = null;
        size = 0;
    }
    
    public boolean isEmpty()                        // is the set empty?
    {
        return root == null;
    }
    
    public int size()                               // number of points in the set
    {
        return size;
    }
    
    public void insert(Point p)                     // add the point p to the set
    {
        if (root == null)
        {
            root = new Node(p, new RectHV(0, 0, 1, 1), true);
            return;
        }
        Node prev = null;
        Node cur = root;
        while (cur != null)
        {
            prev = cur;
            if (cur.checkX)
            {
                if (cur.p.x() <= p.x())
                    cur = cur.rt;
                else
                    cur = cur.lb;
            }
            else
            {
                if (cur.p.y() <= p.y())
                    cur = cur.rt;
                else
                    cur = cur.lb;
            }

        }

        if (p.equals(prev.p))
            return;

        if (prev.checkX)
        {
            if (p.x() >= prev.p.x())
            {
                cur = new Node(p, new RectHV(prev.p.x(), prev.rect.ymin(), 
                                             prev.rect.xmax(), prev.rect.ymax()), 
                                             !prev.checkX);
                prev.rt = cur;
            }
            else
            {
                cur = new Node(p, new RectHV(prev.rect.xmin(), prev.rect.ymin(), 
                                             prev.p.x(), prev.rect.ymax()),
                                             !prev.checkX);
                prev.lb = cur;
            }
        }
        else
        {
            if (p.y() >= prev.p.y())
            {
                cur = new Node(p, new RectHV(prev.rect.xmin(), prev.p.y(), 
                                             prev.rect.xmax(), prev.rect.ymax()),
                                             !prev.checkX);
                prev.rt = cur;
            }
            else
            {
                cur = new Node(p, new RectHV(prev.rect.xmin(), prev.rect.ymin(), 
                                             prev.rect.xmax(), prev.p.y()),
                                             !prev.checkX);
                prev.lb = cur;
            }
        }
    }
    
    public boolean contains(Point p)                // does the set contain p?
    {
        Node cur = root;

        while (cur != null)
        {
            if (p.equals(cur.p))
                return true;
            if (cur.rt.rect.contains(p))
                cur = cur.rt;
            else
                cur = cur.lb;
        }
        return false;
    }
            
    public Point nearest(Point p)    // nearest neighbor in the set to p 
                                     //(null if set is empty)
    {
        if (root == null)
            return null;
    
        Node prev = null; 
        Node closest;
        Node cur = root;

        // find a close point
        while (cur != null)
        {
            prev = cur;
            if (cur.checkX)
            {
                if (cur.p.x() <= p.x())
                    cur = cur.rt;
                else
                    cur = cur.lb;
            }
            else
            {
                if (cur.p.y() <= p.y())
                    cur = cur.rt;
                else
                    cur = cur.lb;
            }
        }
        closest = prev;
        closest = root;

        // search tree to find guarunteed closest point
        double leastDist = p.distanceTo(closest.p);
        Queue<Node> toSearch = new Queue<Node>();
        toSearch.enqueue(root);

        while (!toSearch.isEmpty())
        {
            cur = toSearch.dequeue();
            if (p.distanceTo(cur.p) < leastDist)
            {
                closest = cur;
                leastDist = p.distanceTo(closest.p);
            }

            if (cur.rt != null && cur.rt.rect.distanceTo(p) < leastDist)
                toSearch.enqueue(cur.rt);
            if (cur.lb != null && cur.lb.rect.distanceTo(p) < leastDist)
                toSearch.enqueue(cur.lb);
            }
        return closest.p;
    }
        
    public Iterable<Point> range(RectHV rect)   // points in the set that are 
                                                // in the rectangle
    {
        Node cur;
        Queue<Node> toSearch = new Queue<Node>();
        Queue<Point> inRange = new Queue<Point>();
        
        toSearch.enqueue(root);
        while (!toSearch.isEmpty())
        {
            cur = toSearch.dequeue();
            if (rect.contains(cur.p))
                inRange.enqueue(cur.p);

            if (cur.rt != null && rect.intersects(cur.rt.rect))
                toSearch.enqueue(cur.rt);

            if (cur.lb != null && rect.intersects(cur.lb.rect))
                toSearch.enqueue(cur.lb);
        }
        return inRange;
    }            

    private class Node 
    {
        private Point p;        //The point
        private RectHV rect;    //axis-aligned rectangle corresponding to node
        private Node lb;        //Node pointer to left/bottom subtree
        private Node rt;        //Node pointer to right/top subtree
        private boolean checkX;

        public Node(Point p, RectHV rect, boolean checkX)
        {
            this.p = p;
            this.rect = rect;
            this.lb = null;
            this.rt = null;
            this.checkX = checkX;
        }

        public Node(Node n)
        {
            this.p = n.p;
            this.rect = n.rect;
            this.lb = n.lb;
            this.rt = n.rt;
        }
    }
}

