package visualization.tree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import visualization.S_ExpressionHandler;

/**
 * TreeGraph class is used to display TreeModel.
 * 
 * @author Makoto Tanji
 */
public class TreeGraph extends JComponent implements MouseListener, MouseMotionListener
{
    /**
     * Default height between two stages.
     */
    public static final double DEFAULT_ROW_HEIGHT = 40;
    /**
     * Default node arc radius
     */
    public static final double DEFAULT_NODE_ARC_RADIUS = 8;

    /**
     * Default node size
     */
    public static final double DEFAULT_NODES_DISTANCE = 16;

    /**
     * Default font size
     */
    public static final float DEFAULT_FONT_SIZE = 5f;

    private TreeModel _treeModel;
    private double _scale;
    private double _nodeArcRadius;
    private double _rowHeight;
    private double _nodesDistance;
    private List<TreeNodeUI> _nodeUIArray; // in BFS order
    private List<Integer> _nodeCounts; // number of nodes on each stage 0 to N
    //private List<Object> _nodeModelArray;
    private TreeNodeUI _selected;
    protected double _viewXPosition;
    protected double _viewYPosition;
    protected double _previousX = -1;
    protected double _previousY = -1;
    
    protected AffineTransform _transform;
    
    /** constructs empty TreeGraph */
    public TreeGraph()
    {
        this(null);
    }

    /** constructs TreeGraph that represent specified TreeModel */
    public TreeGraph(TreeModel treeModel)
    {
        this(treeModel, 1);
    }

    /** constructs TreeGraph that represent specified TreeModel */
    public TreeGraph(TreeModel treeModel, double scale)
    {
        _scale = scale;
        _nodeArcRadius = DEFAULT_NODE_ARC_RADIUS;
        _rowHeight = DEFAULT_ROW_HEIGHT;
        _nodesDistance = DEFAULT_NODES_DISTANCE;

        setTreeModel(treeModel); // calculates node positions in this method

        // event
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        //g2.getFont().deriveFont(DEFAULT_FONT_SIZE);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.white);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        g2.setColor(Color.black);      
        
        g2.translate( (int)(_viewXPosition * _scale), (int)(_viewYPosition * _scale) );
        g2.translate(-(_scale-1) * (getVisibleRect().getCenterX() ), -(_scale-1) * (getVisibleRect().getCenterY() ));
        
        g2.scale(_scale, _scale);
        _transform = g2.getTransform();
        
        double maxX = 0;
        double maxY = 0;

        for (int i = 0; i < _nodeUIArray.size(); i++)
        {
        	TreeNodeUI nodeUI = _nodeUIArray.get(i);
            if (nodeUI == _selected)
                nodeUI.draw(g2, Color.RED, Color.BLUE);
            else
                nodeUI.draw(g2, Color.BLACK, Color.BLUE);
            if (nodeUI.getCenterX() + _nodeArcRadius > maxX)
            {
                maxX = nodeUI.getCenterX() + _nodeArcRadius;
            }
            if (nodeUI.getCenterY() + _nodeArcRadius > maxY)
            {
                maxY = nodeUI.getCenterY() + _nodeArcRadius;
            }
        }
        this.setPreferredSize(new Dimension((int) (maxX + 50), (int) (maxY + _nodeArcRadius)));
    }
    
    // calculates node position
    private synchronized void calculateNodePosition()
    {
        List<List<Object>> bfs = breadthFirstSearch();

        double minimumDistance = 4 * _nodeArcRadius;
        int nodeUIIndex = 0;
        int stage = _nodeCounts.size() - 1;

        for (int i = 0; i < _nodeCounts.size() - 1; i++)
        {
            nodeUIIndex += _nodeCounts.get(i);
        }
        
        // start from bottom stage
        for (int i = 0; i < _nodeCounts.get(stage); i++)
        {
            _nodeUIArray.get(nodeUIIndex + i).setPosition(_nodeArcRadius + i * minimumDistance, (bfs.size() - 1) * _rowHeight + _nodeArcRadius);
        }
        
        // climb up to stage 0
        for (int i = stage - 1; i >= 0; i--)
        {
            double lastPosition = Double.NEGATIVE_INFINITY;
            nodeUIIndex = nodeUIIndex - _nodeCounts.get(i);
            for (int j = 0; j < _nodeCounts.get(i); j++)
            {
                TreeNodeUI node = _nodeUIArray.get(nodeUIIndex + j);
                double positionX = _nodeArcRadius + j * minimumDistance;
                if (!_treeModel.isLeaf(node.getTreeNode()))
                {
                    positionX = 0;
                    for (int c = 0; c < node.getChildCount(); c++)
                    {
                        positionX += node.getChildNodeUI(c).getCenterX();
                    }
                    positionX /= node.getChildCount(); // get average of children's X
                    // position
                }
                node.setPosition(positionX, i * _rowHeight + _nodeArcRadius);

                // adjusts if collision occurs
                if (positionX - lastPosition < minimumDistance)
                {
                    moveToRight(node, minimumDistance - (positionX - lastPosition), bfs, i, j, false);
                }
                lastPosition = node.getCenterX();
            }
        }
        
        // adjustment
        int repetition = 0;
        while (true)
        {
            boolean endFlag = true;
            for (int i = 0; i < _nodeCounts.size(); i++)
            {
                nodeUIIndex += _nodeCounts.get(i);
            }
            for (int i = stage; i >= 0; i--)
            {
                double lastPosition = Double.NEGATIVE_INFINITY;
                nodeUIIndex = nodeUIIndex - _nodeCounts.get(i);
                
                for (int j = 0; j < _nodeCounts.get(i); j++)
                {
                    TreeNodeUI node = _nodeUIArray.get(nodeUIIndex + j);
                    double positionX = _nodeArcRadius + j * minimumDistance;
                    if (!_treeModel.isLeaf(node.getTreeNode()))
                    {
                        positionX = 0;
                        for (int c = 0; c < node.getChildCount(); c++)
                        {
                            positionX += node.getChildNodeUI(c).getCenterX();
                        }
                        positionX = positionX / node.getChildCount(); // get average of children's X
                        // position
                        if (node.getCenterX() != positionX)
                        {
                            endFlag = false;
                        }
                        node.setPosition(positionX, i * _rowHeight + _nodeArcRadius);
                    } else
                    {
                        positionX = node.getCenterX();
                    }
                    
                    // adjusts if collision occurs
                    if (positionX < (lastPosition + minimumDistance))
                    {
                        moveToRight(node, minimumDistance - (positionX - lastPosition), bfs, i, j, true);
                    }
                    lastPosition = node.getCenterX();
                }
            }
            if (endFlag || repetition > 10)
            {
                break;
            }
            repetition++;
        }
        
        double maxWidth = 0;
        double maxHeight = 0;
        // finally, creates all links
        for (int i = 0; i < _nodeUIArray.size(); i++)
        {
            TreeNodeUI node = _nodeUIArray.get(i);
            if( maxWidth < node.getShape().getBounds().getMaxX() )
            {
                maxWidth = node.getShape().getBounds().getMaxX();
            }
            if( maxHeight < node.getShape().getBounds().getMaxY() )
            {
                maxHeight = node.getShape().getBounds().getMaxY();
            }
            int childSize = node.getChildCount();
            for (int j = 0; j < childSize; j++)
            {
                node.getChildNodeUI(j).setLine(node.getCenterX(), node.getCenterY());
            }
        }
        this.setPreferredSize(new Dimension((int)( maxWidth * _scale), (int)( maxHeight * _scale)));
    }
    
    // -----------------------
    private synchronized void moveToRight(TreeNodeUI node, double d, List<List<Object>> bfs, int stage, int index, boolean isRightNodeGenerated)
    {
        node.setPosition(node.getCenterX() + d, node.getCenterY());
        if (isRightNodeGenerated && bfs.get(stage).size() > index + 1 && getNodeUI(bfs.get(stage).get(index + 1)).getCenterX() - node.getCenterX() < 4 * _nodeArcRadius)
        {
            TreeNodeUI rightNode = getNodeUI(bfs.get(stage).get(index + 1));
            // System.out.println("collision chain at " + stage + " " + index + " "
            // + (rightNode.getCenterX() - node.getCenterX()) );
            //moveToRight(rightNode, (4 * _nodeArcRadius) - (rightNode.getCenterX() - node.getCenterX()), bfs, stage, index + 1, true);
        }
        for (int i = node.getChildCount() - 1; i != -1; i--)
        {
            int p = 0;
            int sum = 0;
            while (p < index)
            {
                sum += _treeModel.getChildCount(bfs.get(stage).get(p++));
            }
            moveToRight(node.getChildNodeUI(i), d, bfs, stage + 1, sum + i, true);
        }
    }

    private TreeNodeUI getNodeUI(Object node)
    {
        for (int i = 0; i < _nodeUIArray.size(); i++)
        {
            if (_nodeUIArray.get(i).getTreeNode() == node)
                return _nodeUIArray.get(i);
        }
        return null;
    }

    /**
     * returns BFS result
     * 
     * @return array of result on breadth first search
     */
    protected List<List<Object>> breadthFirstSearch()
    {
        List<List<Object>> bfs = new ArrayList<List<Object>>();
        List<Object> nextStage = new ArrayList<Object>();
        List<Object> currentStage = new ArrayList<Object>();
        _nodeUIArray.clear();
        _nodeCounts.clear();
        
        // first stage
        Object parent = _treeModel.getRoot();
        currentStage.add(parent);
        _nodeUIArray.add(new TreeNodeUI(this, parent)); // nodeUI
        _nodeCounts.add(1); // the number of root node
        bfs.add(currentStage);
        
        while (true)
        {
            int childCountSum = 0;
            for (int i = 0; i < currentStage.size(); i++)
            {
                int childSize = _treeModel.getChildCount(currentStage.get(i));
                for (int j = 0; j < childSize; j++)
                {
                    // model
                    Object node = _treeModel.getChild(currentStage.get(i), j);
                    nextStage.add(node);
                    //_nodeModelArray.add(node);

                    // graphical UI
                    TreeNodeUI nodeUI = new TreeNodeUI(this, node);
                    nodeUI.setParentNodeUI(_nodeUIArray.get(_nodeUIArray.size() - childCountSum - currentStage.size() + i));
                    _nodeUIArray.add(nodeUI);
                    childCountSum++;
                }
            }
            if (nextStage.size() == 0)
                break;
            bfs.add(nextStage);
            _nodeCounts.add(nextStage.size());
            currentStage = nextStage;
            nextStage = new ArrayList<Object>();
        }
        return bfs;
    }

    /**
     * returns DFS result
     * 
     * @return array of result on depth first search
     */
    public List<Object> depthFirstSearch()
    {
        List<Object> dfsArray = new ArrayList<Object>();
        Stack<Object> searchStack = new Stack<Object>();

        // first stage
        Object parent = _treeModel.getRoot();
        searchStack.push(parent);

        dfsSearch(_treeModel, _treeModel.getRoot(), dfsArray);

        return dfsArray;
    }

    /**
     * returns DFS result
     * 
     * @return array of result on depth first search
     */
    public static List<Object> depthFirstSearch(TreeModel treeModel)
    {
        List<Object> dfsArray = new ArrayList<Object>();
        Stack<Object> searchStack = new Stack<Object>();

        // first stage
        Object parent = treeModel.getRoot();
        searchStack.push(parent);

        dfsSearch(treeModel, treeModel.getRoot(), dfsArray);

        return dfsArray;
    }

    /** DFS search method, this is recursive method */
    private static void dfsSearch(TreeModel treeModel, Object node, List<Object> resultArray)
    {
        resultArray.add(node);
        if (treeModel.isLeaf(node))
            return;

        for (int i = 0; i < treeModel.getChildCount(node); i++) // has childlen
        {
            dfsSearch(treeModel, treeModel.getChild(node, i), resultArray);
        }
    }

    /** returns radius of node arc */
    public double getNodeArcRadius()
    {
        return _nodeArcRadius;
    }

    /** sets radius of node arc */
    public void setNodeArcRadius(double nodeArcRadius)
    {
        _nodeArcRadius = nodeArcRadius;
    }

    /** returns height between two stages */
    public double getRowHeight()
    {
        return _rowHeight;
    }

    /** sets height between two stages */
    public void setRowHeight(double rowHeight)
    {
        _rowHeight = rowHeight;
    }

    /** returns horizontal distance of two nodes */
    public double getNodesDistance()
    {
        return _nodesDistance;
    }

    /** sets horizontal distance of two nodes */
    public void setNodesDistance(double nodesDistance)
    {
        _nodesDistance = nodesDistance;
    }

    /** returns TreeModel */
    public TreeModel getTreeModel()
    {
        return _treeModel;
    }

    /** sets TreeModel */
    public void setTreeModel(TreeModel treeModel)
    {
        _treeModel = treeModel;
        _nodeUIArray = new ArrayList<TreeNodeUI>();
        _nodeCounts = new ArrayList<Integer>();
        _selected = null;
        if (treeModel != null)
        {
            calculateNodePosition();
        }
    }

    /** sets scale value and this method calculates position of each node */
    public void setScale(double scale)
    {
        _scale = scale;
        //_nodeArcRadius = DEFAULT_NODE_ARC_RADIUS * _scale;
        //_rowHeight = DEFAULT_ROW_HEIGHT * _scale;
        //_nodesDistance = DEFAULT_NODES_DISTANCE * _scale;
    }

    /** returns scale value */
    public double getScale()
    {
        return _scale;
    }

    private void setCloseOrOpenFollowings(TreeNodeUI selected, boolean closed)
    {
        for (int i = 0; i < selected.getChildCount(); i++)
        {
            TreeNodeUI nodeUI = selected.getChildNodeUI(i);
            nodeUI.setClosed(closed);
            if (nodeUI.isClosedPoint())
            {
                // stop close or open recursive
            } else
                setCloseOrOpenFollowings(nodeUI, closed);
        }
    }

    public List<Object> getDFS()
    {
        return this.depthFirstSearch();
    }

    public TreeNodeUI getSelectedNode()
    {
        return _selected;
    }

    public void updateModel()
    {
        setTreeModel(_treeModel);
    }
    
    public int getGraphX(double x)
    {
        x -= getVisibleRect().getX();
        try{
            return (int)(_transform.inverseTransform(new Point2D.Double(x, 1), null).getX());
        }catch (Exception e) {
            return (int)x;
        }
    }

    public int getGraphY(double y)
    {
        y -= getVisibleRect().getY();
        try{
            return (int)(_transform.inverseTransform(new Point2D.Double(1, y), null).getY());
        }catch (Exception e) {
            return (int)y;
        }
    }
    
    // ----- listener methods -----
    public void mouseClicked(MouseEvent e)
    {
        int xPos = (int)((-_viewXPosition + e.getX()) / _scale);
        int yPos = (int)((-_viewYPosition + e.getY()) / _scale);
        xPos = getGraphX(e.getX());
        yPos = getGraphY(e.getY());
        
        for (TreeNodeUI nodeUI : _nodeUIArray)
        {
            if (nodeUI.getShape().intersects(xPos, yPos, 1, 1))
            {
                if (_selected == nodeUI) // opeartion close or open tree node
                {
                    if (_treeModel.isLeaf(nodeUI.getTreeNode()))
                        return; // is leaf

                    if (_selected.isClosedPoint())
                    {
                        _selected.setClosedPoint(false); // open
                        setCloseOrOpenFollowings(_selected, false);
                    } else
                    {
                        _selected.setClosedPoint(true); // close
                        setCloseOrOpenFollowings(_selected, true);
                    }
                } else
                    _selected = nodeUI;
            }
        }
        repaint();
        //_previousX = e.getX();
        //_previousY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if( (e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0 )
        {
            if( _previousX != -1 && _previousY != -1)
            {
                _scale = Math.max(0.01, _scale * (1 - 0.01 * (_previousY - e.getY())));
            }
            repaint();
        }
        else if( _previousX != -1 && _previousY != -1)
        {
            _viewXPosition += (e.getX() - _previousX ) / _scale;
            _viewYPosition += (e.getY() - _previousY ) / _scale;
            this.repaint();
        }
        _previousX = e.getX();
        _previousY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        _previousX = -1;
        _previousY = -1;
    }

    // -------------------- for test
    public static void main(String args[])
    {
        // creates sample tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        String s_expression = "( + ( + ( + ( + ( * ( * PD ( + ( - ( If ( - CD ST ) ( * LP LP ) ( * PV ( + MPos LP ) ) ( If ( - CD ST ) ( * LP LP ) ( * PV RT ) ( + DY LP ) ) ) ( * ( + PD ST ) DY ) ) ( + LP ( + ( If PP CP ( * PP PD ) ( If SL ST PP ST ) ) ( If PP ( + CP Pos ) ( / ST SL ) ST ) ) ) ) ) LP ) ( * ( * ( * ( * PV LP ) ( - R[4.653982127840841] ( If ( + CP ( / PP Pos ) ) DY PD LP ) ) ) ( - ( + ( If PP ( + CP Pos ) ( / ST SL ) ST ) ( + LP TD ) ) ( + ( - AC LP ) MPos ) ) ) LP ) ) ( + ( - ( - ( If ( / ST SL ) ( - ( If ( If ( If R[-18.02585479772201] LP PV DY ) SL ( - PD CD ) PP ) ( + CP ( * PD R[-16.84292196491981] ) ) TD DY ) ( + ( If Pos R[2.839500202149246] ( / DY PD ) ( / DY R[10.85033691938495] ) ) ( If Pos ( If PP CP PV PD ) DY ( If PP CP PV MPos ) ) ) ) TD ( + ( If MPos SL TD ( + ( / DY R[10.85033691938495] ) ( / AC MPos ) ) ) ( * ( + ( / DY R[10.85033691938495] ) ( - AC LP ) ) LP ) ) ) ( + ( + ( - CD ( + ( If PP CP PV MPos ) ( * RT LP ) ) ) CD ) ( / ST SL ) ) ) ( + CD ( - CD ST ) ) ) ( * ( * PD ( + ( - ( If PP ( + CP ( * PD R[-16.84292196491981] ) ) PV DY ) ( + ( If Pos R[2.839500202149246] SL ( / PP Pos ) ) ( If Pos R[2.839500202149246] Pos ( - R[4.653982127840841] TD ) ) ) ) ( + CD ( / ( If ( If PP CP PV LP ) ( / PP Pos ) ( * PP PD ) Pos ) SL ) ) ) ) LP ) ) ) ( + ( + ( - ( If ( * PD DY ) ( + ( * SL ( + AC DY ) ) ( - ( - AC CD ) CD ) ) ( If R[12.816319457226896] ( + ( / ( + PD TD ) ( * CD LP ) ) ( * DY ( * ( / CP DY ) Pos ) ) ) ( + LP ( + ( - ( - AC CD ) CD ) ( + LP ( + ST LP ) ) ) ) ( + ( / ( + LP ( + ST LP ) ) ( + LP ( + ST LP ) ) ) ( * DY ( * ( / CP DY ) Pos ) ) ) ) ( If ( + ( + ( * TD ( - SL AC ) ) LP ) ( If ( / DY CR ) ( - AC CD ) ( If SL PV ( - RT SL ) CD ) ( - AC CD ) ) ) SL PV ( / DY R[-23.34602332138852] ) ) ) ( / ( + ( + ( * ( / PV CR ) ( + ST LP ) ) ( If ( - SL AC ) ( * DY AC ) ( If SL ( + LP CD ) ( / DY R[-23.34602332138852] ) CD ) ( - AC CD ) ) ) ( If ( * ( + PD TD ) DY ) ( - RT SL ) ( If SL ( / DY R[-23.34602332138852] ) RT CD ) ( If ( - SL AC ) ( - AC SL ) ( + ( + LP CD ) MPos ) ( - AC CD ) ) ) ) ( - RT SL ) ) ) ( * PD DY ) ) ( + ( + ( + ( * ( / ( * ( / CP DY ) Pos ) CR ) ( + ST LP ) ) ( If ( - ( / ( + PD TD ) ( * CD LP ) ) ( / ( + LP ( + ST LP ) ) ( - RT SL ) ) ) ( - AC CD ) ( * ( / DY CR ) ( + ST LP ) ) ( - ( - AC CD ) CD ) ) ) ( If ( - ( If PV ( / ( + PD TD ) ( * CD LP ) ) CP SL ) ( / ( - ( - AC CD ) CD ) ( - RT SL ) ) ) ( If SL PV ( / DY R[-23.34602332138852] ) CD ) ( * ( / DY CR ) ( + ( + ( - LP CD ) MPos ) ( If ( - RT SL ) ( * ( / CP DY ) AC ) ( - SL AC ) ( / ( - DY SL ) ( - PP CR ) ) ) ) ) ( - ( - AC CD ) CD ) ) ) ( If ( - ( If ( * ( * PV ( * PD DY ) ) CP ) ( / ( * DY AC ) ( * CD LP ) ) CP SL ) ( / ( + ( * ( / DY CR ) ( + ST LP ) ) ( If ( - ( / PP PD ) AC ) ( + LP ( + ST LP ) ) ( If Pos CP RT ST ) ( - AC Pos ) ) ) ( - AC SL ) ) ) ( + LP ( - RT SL ) ) ( - ( If ( - ( / ( + PD TD ) ( * CD LP ) ) ( / ( + LP ( + ST LP ) ) ( + LP ( + ST LP ) ) ) ) ( * DY AC ) ( + LP ( + ( * PD DY ) ( * PD DY ) ) ) ( - ( - AC CD ) CD ) ) CD ) ( - AC CD ) ) ) ) ) ( If LP R[0.234] R[-1.232] R[1.34] ) ) ";
        TreeModel treeModel = S_ExpressionHandler.getTreeModelByS_Expression(s_expression);

        // DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("");
        // DefaultMutableTreeNode child2 = new DefaultMutableTreeNode("");
        // root.add(child1);
        // root.add(child2);
        // DefaultMutableTreeNode child11 = new DefaultMutableTreeNode("");
        // DefaultMutableTreeNode child12 = new DefaultMutableTreeNode("");
        // DefaultMutableTreeNode child13 = new DefaultMutableTreeNode("");
        // child1.add(child11);
        // child1.add(child12);
        // child1.add(new DefaultMutableTreeNode(""));
        // child1.add(new DefaultMutableTreeNode(""));
        // child1.add(new DefaultMutableTreeNode(""));
        // child1.add(new DefaultMutableTreeNode(""));
        // child1.add(new DefaultMutableTreeNode(""));
        // child1.add(new DefaultMutableTreeNode(""));
        // child1.add(child13);
        // DefaultMutableTreeNode child21 = new DefaultMutableTreeNode("");
        // DefaultMutableTreeNode child22 = new DefaultMutableTreeNode("");
        // child2.add(child21);
        // child2.add(child22);
        // DefaultMutableTreeNode child121 = new DefaultMutableTreeNode("");
        // child12.add(child121);
        // DefaultMutableTreeNode child131 = new DefaultMutableTreeNode("");
        // child13.add(child131);
        // DefaultTreeModel treeModel = new DefaultTreeModel(root);
        // child131.add(new DefaultMutableTreeNode(""));
        // child131.add(new DefaultMutableTreeNode(""));
        // child131.add(new DefaultMutableTreeNode(""));
        // child131.add(new DefaultMutableTreeNode(""));
        // child131.add(new DefaultMutableTreeNode(""));
        // child131.add(new DefaultMutableTreeNode(""));
        // ((DefaultMutableTreeNode) child131.getChildAt(3))
        // .add(new DefaultMutableTreeNode(""));
        // ((DefaultMutableTreeNode) child131.getChildAt(3))
        // .add(new DefaultMutableTreeNode(""));
        // ((DefaultMutableTreeNode) child131.getChildAt(3))
        // .add(new DefaultMutableTreeNode(""));
        // child121.add(new DefaultMutableTreeNode(""));
        // child121.add(new DefaultMutableTreeNode(""));
        // child121.add(new DefaultMutableTreeNode(""));
        // ((DefaultMutableTreeNode) child121.getChildAt(0))
        // .add(new DefaultMutableTreeNode(""));
        // ((DefaultMutableTreeNode) child121.getChildAt(0))
        // .add(new DefaultMutableTreeNode(""));
        // ((DefaultMutableTreeNode) child121.getChildAt(0))
        // .add(new DefaultMutableTreeNode(""));
        // ((DefaultMutableTreeNode) child121.getChildAt(0))
        // .add(new DefaultMutableTreeNode(""));
        // child21.add(new DefaultMutableTreeNode(""));
        // child21.add(new DefaultMutableTreeNode(""));
        // child21.add(new DefaultMutableTreeNode(""));
        // child21.add(new DefaultMutableTreeNode(""));
        // ((DefaultMutableTreeNode) child21.getChildAt(2))
        // .add(new DefaultMutableTreeNode(""));
        // ((DefaultMutableTreeNode) child21.getChildAt(2))
        // .add(new DefaultMutableTreeNode(""));
        // ((DefaultMutableTreeNode) child21.getChildAt(2))
        // .add(new DefaultMutableTreeNode(""));
        // child1.add(new DefaultMutableTreeNode(""));
        // child1.add(new DefaultMutableTreeNode(""));
        // child1.add(new DefaultMutableTreeNode(""));
        // child1.add(new DefaultMutableTreeNode(""));
        // child1.add(new DefaultMutableTreeNode(""));

        // tree graph
        TreeGraph treeGraph = new TreeGraph(treeModel, 1);
        //treeGraph.setScale(0.4);
        treeGraph.updateModel();
        // List<Object> dfs = treeGraph.depthFirstSearch();
        // for (Object node : dfs)
        // {
        // System.out.println(node);
        // }

        // draw tree
        JFrame frame = new JFrame("Tree view test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame
        frame.add(new JScrollPane(treeGraph));
        frame.setSize(600, 400);
        frame.repaint();
        frame.setVisible(true);
    }
}
